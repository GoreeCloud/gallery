package com.goreecloud.gallery

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

/**
 * Native Android material mapping for Gallery's Glaze presentation layer.
 *
 * Environmental tint remains deliberately low influence and non-semantic. Selection, destructive,
 * privacy, permission, and other protected states keep their own semantic authority. The material
 * uses a restrained optical gradient, edge highlight, and readable near-opaque fallback instead of
 * pretending that unsupported backdrop blur exists on every Android device.
 */
object GalleryGlazeSurfaces {
    enum class Role {
        CONTROL,
        CHROME,
        RAISED,
        OVERLAY,
    }

    fun drawable(
        context: Context,
        role: Role,
        radiusDp: Int,
    ): GradientDrawable {
        val isNight = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
        val base = context.getColor(
            if (role == Role.OVERLAY || role == Role.CHROME) R.color.gallery_surface_strong
            else R.color.gallery_surface,
        )
        val environment = context.getColor(R.color.gallery_environment_accent)
        val tintFraction = when (role) {
            Role.CONTROL -> 0.065f
            Role.CHROME -> 0.055f
            Role.RAISED -> 0.045f
            Role.OVERLAY -> 0.025f
        }.coerceAtMost(GalleryGlazeContract.OPTICAL_MEMORY_TINT_MAX_FRACTION)
        val surface = mix(base, environment, tintFraction)
        val highlightTarget = if (isNight) Color.WHITE else Color.WHITE
        val highlightAmount = when (role) {
            Role.CONTROL -> if (isNight) 0.035f else 0.060f
            Role.CHROME -> if (isNight) 0.045f else 0.075f
            Role.RAISED -> if (isNight) 0.025f else 0.045f
            Role.OVERLAY -> if (isNight) 0.018f else 0.025f
        }
        val highlight = mix(surface, highlightTarget, highlightAmount)
        val alpha = when (role) {
            Role.CONTROL -> if (isNight) 0.97f else 0.95f
            Role.CHROME -> if (isNight) 0.985f else 0.965f
            Role.RAISED -> 0.985f
            Role.OVERLAY -> 1f
        }
        val strokeAlpha = when (role) {
            Role.CONTROL -> if (isNight) 0.22f else 0.14f
            Role.CHROME -> if (isNight) 0.24f else 0.16f
            Role.RAISED -> if (isNight) 0.18f else 0.11f
            Role.OVERLAY -> if (isNight) 0.16f else 0.09f
        }
        val edge = mix(environment, if (isNight) Color.WHITE else Color.BLACK, if (isNight) 0.05f else 0.03f)

        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(withAlpha(highlight, alpha), withAlpha(surface, alpha)),
        ).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(context, radiusDp).toFloat()
            setStroke(dp(context, 1), withAlpha(edge, strokeAlpha))
        }
    }

    private fun mix(base: Int, tint: Int, fraction: Float): Int {
        val amount = fraction.coerceIn(0f, GalleryGlazeContract.OPTICAL_MEMORY_TINT_MAX_FRACTION)
        fun channel(baseChannel: Int, tintChannel: Int): Int =
            (baseChannel + ((tintChannel - baseChannel) * amount)).toInt().coerceIn(0, 255)

        return Color.rgb(
            channel(Color.red(base), Color.red(tint)),
            channel(Color.green(base), Color.green(tint)),
            channel(Color.blue(base), Color.blue(tint)),
        )
    }

    private fun withAlpha(color: Int, alpha: Float): Int = Color.argb(
        (255f * alpha.coerceIn(0f, 1f)).toInt(),
        Color.red(color),
        Color.green(color),
        Color.blue(color),
    )

    private fun dp(context: Context, value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt().coerceAtLeast(1)
}
