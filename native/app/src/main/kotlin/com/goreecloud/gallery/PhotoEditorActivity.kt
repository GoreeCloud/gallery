package com.goreecloud.gallery

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoEditorActivity : Activity() {
    private val editorExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private lateinit var root: FrameLayout
    private lateinit var preview: ImageView
    private lateinit var cropOverlay: GalleryCropOverlayView
    private lateinit var status: TextView
    private lateinit var saveButton: TextView
    private val editControls = mutableListOf<TextView>()

    private var sourceUri: Uri? = null
    private var sourceDisplayName: String = "Photo"
    private var sourceMimeType: String = "image/jpeg"
    private var sourceBitmap: Bitmap? = null
    private var previewBitmap: Bitmap? = null
    private var editPlan = GalleryPhotoEditPlan()
    private var restoredEditPlan: GalleryPhotoEditPlan? = null
    private var renderGeneration = 0
    private var working = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uriValue = intent.getStringExtra(GalleryPhotoEditorContract.EXTRA_CONTENT_URI)
        val displayName = intent.getStringExtra(GalleryPhotoEditorContract.EXTRA_DISPLAY_NAME)
        val mimeType = intent.getStringExtra(GalleryPhotoEditorContract.EXTRA_MIME_TYPE)
        val uri = uriValue?.let(Uri::parse)

        if (
            uri == null ||
            uri.scheme != "content" ||
            uri.authority != "media" ||
            displayName.isNullOrBlank() ||
            mimeType.isNullOrBlank() ||
            !GalleryPhotoEditorContract.isSupportedMimeType(mimeType)
        ) {
            Toast.makeText(this, "Gallery refused an unsupported photo edit request.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        restoredEditPlan = savedInstanceState
            ?.takeIf { it.getBoolean(STATE_EDIT_PLAN_PRESENT, false) }
            ?.let {
                GalleryPhotoEditStatePolicy.restore(
                    GalleryPhotoEditStateSnapshot(
                        rotationQuarterTurns = it.getInt(STATE_ROTATION_QUARTER_TURNS),
                        flipHorizontal = it.getBoolean(STATE_FLIP_HORIZONTAL),
                        cropLeft = it.getFloat(STATE_CROP_LEFT),
                        cropTop = it.getFloat(STATE_CROP_TOP),
                        cropRight = it.getFloat(STATE_CROP_RIGHT),
                        cropBottom = it.getFloat(STATE_CROP_BOTTOM),
                    ),
                )
            }

        sourceUri = uri
        sourceDisplayName = displayName
        sourceMimeType = mimeType
        buildSurface()
        loadSourcePhoto()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val snapshot = GalleryPhotoEditStatePolicy.snapshot(restoredEditPlan ?: editPlan)
        outState.putBoolean(STATE_EDIT_PLAN_PRESENT, true)
        outState.putInt(STATE_ROTATION_QUARTER_TURNS, snapshot.rotationQuarterTurns)
        outState.putBoolean(STATE_FLIP_HORIZONTAL, snapshot.flipHorizontal)
        outState.putFloat(STATE_CROP_LEFT, snapshot.cropLeft)
        outState.putFloat(STATE_CROP_TOP, snapshot.cropTop)
        outState.putFloat(STATE_CROP_RIGHT, snapshot.cropRight)
        outState.putFloat(STATE_CROP_BOTTOM, snapshot.cropBottom)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        renderGeneration += 1
        editorExecutor.shutdownNow()
        super.onDestroy()
    }

    @Deprecated("The native editor uses explicit close controls and standard Activity back navigation.")
    override fun onBackPressed() {
        if (working) return
        finish()
    }

    private fun buildSurface() {
        root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }
        setContentView(root)
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        val stage = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        preview = ImageView(this).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(Color.BLACK)
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
        stage.addView(
            preview,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT),
        )
        cropOverlay = GalleryCropOverlayView(this).apply {
            visibility = View.INVISIBLE
            onCropChanged = { editPlan = editPlan.copy(crop = it) }
        }
        stage.addView(
            cropOverlay,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT),
        )
        root.addView(
            stage,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                topMargin = dp(76)
                bottomMargin = dp(158)
            },
        )

        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
            )
            background = roundedSurface(0xe61a1a1d.toInt(), GalleryGlazeContract.SHAPE_ROUNDED_DP)
        }
        val cancel = editorButton("Cancel", "Cancel editing and keep the original photo") {
            if (!working) finish()
        }
        topBar.addView(
            cancel,
            LinearLayout.LayoutParams(dp(76), dp(GalleryGlazeContract.GENERAL_TARGET_DP)),
        )

        val titles = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
                0,
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
                0,
            )
            addView(TextView(context).apply {
                text = "Edit photo"
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
                maxLines = 1
            })
            addView(TextView(context).apply {
                text = sourceDisplayName
                setTextColor(0xffb9bcc2.toInt())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            })
        }
        topBar.addView(titles, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        saveButton = editorButton("Save copy", "Save the edited photo as a new copy and keep the original") {
            saveEditedCopy()
        }
        topBar.addView(
            saveButton,
            LinearLayout.LayoutParams(dp(92), dp(GalleryGlazeContract.GENERAL_TARGET_DP)),
        )
        root.addView(
            topBar,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(64)).apply {
                gravity = Gravity.TOP
                marginStart = dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP)
                marginEnd = dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP)
                topMargin = dp(GalleryGlazeContract.SPACE_CONTROL_DP)
            },
        )

        status = TextView(this).apply {
            text = "Loading full-resolution photo…"
            setTextColor(0xffd7d9de.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f)
            gravity = Gravity.CENTER
            setPadding(
                dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP),
                dp(GalleryGlazeContract.SPACE_HAIRLINE_DP),
                dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP),
                dp(GalleryGlazeContract.SPACE_HAIRLINE_DP),
            )
            maxLines = 2
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
        root.addView(
            status,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(42)).apply {
                gravity = Gravity.BOTTOM
                bottomMargin = dp(104)
                marginStart = dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP)
                marginEnd = dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP)
            },
        )

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
                dp(GalleryGlazeContract.SPACE_CONTROL_DP),
            )
        }
        addControl(controls, "↺ 90°", "Rotate photo 90 degrees left") {
            applyTransform(GalleryPhotoEditPolicy.rotateLeft(editPlan))
        }
        addControl(controls, "↻ 90°", "Rotate photo 90 degrees right") {
            applyTransform(GalleryPhotoEditPolicy.rotateRight(editPlan))
        }
        addControl(controls, "Flip", "Flip photo horizontally") {
            applyTransform(GalleryPhotoEditPolicy.flipHorizontal(editPlan))
        }
        addControl(controls, "Original", "Reset crop to the full photo") {
            setCropPreset(GalleryNormalizedCrop.FULL)
        }
        addControl(controls, "1:1", "Crop photo to a centered square") { setAspectPreset(1f) }
        addControl(controls, "4:3", "Crop photo to a centered four by three rectangle") { setAspectPreset(4f / 3f) }
        addControl(controls, "16:9", "Crop photo to a centered sixteen by nine rectangle") { setAspectPreset(16f / 9f) }
        addControl(controls, "Reset", "Reset rotation, flip, and crop") {
            applyTransform(GalleryPhotoEditPolicy.reset())
        }

        val scroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isFillViewport = false
            background = roundedSurface(0xe61a1a1d.toInt(), GalleryGlazeContract.SHAPE_ROUNDED_DP)
            addView(
                controls,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT),
            )
        }
        root.addView(
            scroll,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(88)).apply {
                gravity = Gravity.BOTTOM
                marginStart = dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP)
                marginEnd = dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP)
                bottomMargin = dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP)
            },
        )

        setWorking(true, "Loading full-resolution photo…")
    }

    private fun addControl(
        row: LinearLayout,
        label: String,
        description: String,
        onClick: () -> Unit,
    ) {
        val control = editorButton(label, description) {
            if (!working) onClick()
        }.apply {
            minWidth = dp(72)
        }
        editControls += control
        row.addView(
            control,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(60)).apply {
                marginEnd = dp(GalleryGlazeContract.SPACE_HAIRLINE_DP)
            },
        )
    }

    private fun loadSourcePhoto() {
        val uri = sourceUri ?: return
        val generation = ++renderGeneration
        editorExecutor.execute {
            try {
                val decoded = GalleryBitmapEditor.decodeAuthorizedPhoto(contentResolver, uri)
                runOnUiThread {
                    if (generation != renderGeneration || isFinishing) {
                        decoded.recycle()
                        return@runOnUiThread
                    }
                    sourceBitmap = decoded
                    editPlan = restoredEditPlan ?: GalleryPhotoEditPolicy.reset()
                    restoredEditPlan = null
                    renderPlanPreview()
                }
            } catch (_: GalleryPhotoTooLargeException) {
                showLoadFailure(
                    generation,
                    "This photo is too large for the current first-party editor. Gallery left the original untouched.",
                )
            } catch (_: SecurityException) {
                showLoadFailure(generation, "Android no longer authorizes Gallery to read this photo.")
            } catch (_: IOException) {
                showLoadFailure(generation, "The photo could not be decoded.")
            } catch (_: RuntimeException) {
                showLoadFailure(generation, "The photo could not be decoded safely.")
            }
        }
    }

    private fun applyTransform(plan: GalleryPhotoEditPlan) {
        if (sourceBitmap == null || working) return
        editPlan = plan
        renderPlanPreview()
    }

    private fun renderPlanPreview() {
        val source = sourceBitmap ?: return
        val plan = editPlan
        val generation = ++renderGeneration
        setWorking(true, "Applying photo transform…")

        editorExecutor.execute {
            try {
                val transformed = GalleryBitmapEditor.transform(source, plan)
                runOnUiThread {
                    if (generation != renderGeneration || isFinishing) {
                        if (transformed !== source) transformed.recycle()
                        return@runOnUiThread
                    }
                    val oldPreview = previewBitmap
                    previewBitmap = transformed
                    preview.setImageBitmap(transformed)
                    cropOverlay.setSourceSize(transformed.width, transformed.height)
                    cropOverlay.setCrop(plan.crop, notify = false)
                    cropOverlay.visibility = View.VISIBLE
                    if (oldPreview != null && oldPreview !== source && oldPreview !== transformed) oldPreview.recycle()
                    setWorking(
                        false,
                        "Drag the crop handles or photo area to crop. Save copy keeps the original untouched.",
                    )
                }
            } catch (_: RuntimeException) {
                showLoadFailure(generation, "Gallery could not render this photo transform safely.")
            }
        }
    }

    private fun setAspectPreset(aspect: Float) {
        val bitmap = previewBitmap ?: return
        setCropPreset(GalleryPhotoEditPolicy.centerCropForAspect(bitmap.width, bitmap.height, aspect))
    }

    private fun setCropPreset(crop: GalleryNormalizedCrop) {
        if (working) return
        editPlan = editPlan.copy(crop = crop)
        cropOverlay.setCrop(crop, notify = false)
        status.text = "Crop updated. Drag the crop handles or photo area for a custom crop."
        cropOverlay.announceForAccessibility("Crop updated")
    }

    private fun saveEditedCopy() {
        val source = sourceUri ?: return
        val rendered = previewBitmap ?: return
        if (working) return

        val crop = editPlan.crop
        setWorking(true, "Saving edited copy…")
        editorExecutor.execute {
            var output: Bitmap? = null
            try {
                val editedBitmap = GalleryBitmapEditor.crop(rendered, crop)
                output = editedBitmap
                val saved = GalleryEditedMediaStore(contentResolver).saveCopy(
                    sourceUri = source,
                    sourceDisplayName = sourceDisplayName,
                    sourceMimeType = sourceMimeType,
                    bitmap = editedBitmap,
                )
                if (editedBitmap !== rendered) editedBitmap.recycle()
                runOnUiThread {
                    if (isFinishing) return@runOnUiThread
                    Toast.makeText(this, "Saved ${saved.displayName}", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            } catch (_: SecurityException) {
                if (output != null && output !== rendered && !output.isRecycled) output.recycle()
                showSaveFailure("Android denied permission to publish the edited copy.")
            } catch (_: IOException) {
                if (output != null && output !== rendered && !output.isRecycled) output.recycle()
                showSaveFailure("Gallery could not write the edited copy.")
            } catch (_: RuntimeException) {
                if (output != null && output !== rendered && !output.isRecycled) output.recycle()
                showSaveFailure("Gallery could not save the edited copy safely.")
            }
        }
    }

    private fun showLoadFailure(generation: Int, message: String) {
        runOnUiThread {
            if (generation != renderGeneration || isFinishing) return@runOnUiThread
            cropOverlay.visibility = View.INVISIBLE
            preview.setImageDrawable(null)
            setWorking(false, message)
            editControls.forEach {
                it.isEnabled = false
                it.isClickable = false
                it.isFocusable = false
                it.alpha = 0.35f
            }
            saveButton.isEnabled = false
            saveButton.isClickable = false
            saveButton.isFocusable = false
            saveButton.alpha = 0.35f
        }
    }

    private fun showSaveFailure(message: String) {
        runOnUiThread {
            if (isFinishing) return@runOnUiThread
            setWorking(false, message)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setWorking(value: Boolean, message: String) {
        working = value
        status.text = message
        cropOverlay.isEnabled = !value
        cropOverlay.alpha = if (value) 0.72f else 1f
        editControls.forEach {
            it.isEnabled = !value
            it.isClickable = !value
            it.isFocusable = !value
            it.alpha = if (value) 0.38f else 1f
        }
        saveButton.isEnabled = !value && previewBitmap != null
        saveButton.isClickable = saveButton.isEnabled
        saveButton.isFocusable = saveButton.isEnabled
        saveButton.alpha = if (saveButton.isEnabled) 1f else 0.38f
    }

    private fun editorButton(
        label: String,
        description: String,
        onClick: () -> Unit,
    ): TextView = TextView(this).apply {
        text = label
        gravity = Gravity.CENTER
        minHeight = dp(GalleryGlazeContract.GENERAL_TARGET_DP)
        minWidth = dp(GalleryGlazeContract.GENERAL_TARGET_DP)
        setPadding(
            dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP),
            0,
            dp(GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP),
            0,
        )
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setTypeface(typeface, Typeface.BOLD)
        background = roundedSurface(0x2effffff, GalleryGlazeContract.SHAPE_CONTROL_DP)
        isClickable = true
        isFocusable = true
        contentDescription = description
        setOnClickListener { onClick() }
    }

    private fun roundedSurface(color: Int, radiusDp: Int): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(color)
        cornerRadius = dp(radiusDp).toFloat()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val STATE_EDIT_PLAN_PRESENT = "editor.edit-plan-present"
        const val STATE_ROTATION_QUARTER_TURNS = "editor.rotation-quarter-turns"
        const val STATE_FLIP_HORIZONTAL = "editor.flip-horizontal"
        const val STATE_CROP_LEFT = "editor.crop-left"
        const val STATE_CROP_TOP = "editor.crop-top"
        const val STATE_CROP_RIGHT = "editor.crop-right"
        const val STATE_CROP_BOTTOM = "editor.crop-bottom"
    }
}
