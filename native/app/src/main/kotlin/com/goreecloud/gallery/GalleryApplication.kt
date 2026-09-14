package com.goreecloud.gallery

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import java.util.WeakHashMap

/**
 * App-local lifecycle guard for media-authority changes that can happen while Android system UI or
 * another app is in the foreground.
 *
 * GoreeCloud Gallery does not keep a private media authority cache. Recycle Bin presentation is
 * derived from Android MediaStore, so a permission-scope change must invalidate the current
 * Activity presentation before it can continue showing previously readable thumbnails or items.
 */
class GalleryApplication : Application(), Application.ActivityLifecycleCallbacks {
    private val recycleBinScopes = WeakHashMap<Activity, GalleryMediaAccessScope>()

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity !is RecycleBinActivity) return

        val current = AndroidGalleryMediaAccess.currentScope(activity)
        val previous = recycleBinScopes.put(activity, current)
        if (GalleryMediaAccessTransitionPolicy.requiresPresentationReset(previous, current)) {
            // ActivityLifecycleCallbacks receives this after Activity.onResume(). Recreate only after
            // recording the new scope so the replacement Activity cannot enter a recreation loop.
            // RecycleBinActivity owns no durable Trash state, so recreation safely discards its
            // viewer/selection/thumbnail presentation and re-queries Android MediaStore.
            activity.recreate()
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        recycleBinScopes.remove(activity)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) {
        // Install after Activity.onCreate() has completed so the main root hierarchy is available,
        // but before the resumed frame is presented to the user.
        GallerySystemBars.install(activity)
    }

    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}

internal object AndroidGalleryMediaAccess {
    fun currentScope(activity: Activity): GalleryMediaAccessScope =
        GalleryMediaAccessPolicy.resolve(
            GalleryMediaPermissionSnapshot(
                apiLevel = Build.VERSION.SDK_INT,
                readExternalStorage = activity.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE),
                readMediaImages = activity.hasPermission(Manifest.permission.READ_MEDIA_IMAGES),
                readMediaVideo = activity.hasPermission(Manifest.permission.READ_MEDIA_VIDEO),
                readMediaVisualUserSelected = Build.VERSION.SDK_INT >= 34 &&
                    activity.hasPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED),
            ),
        )

    private fun Activity.hasPermission(permission: String): Boolean =
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

object GalleryMediaAccessTransitionPolicy {
    /**
     * The first observed scope establishes a baseline. Any later Android permission-scope change —
     * including full to partial, partial to denied, or regained/broadened access — invalidates a
     * Recycle Bin presentation derived under the old authority.
     *
     * Android 14+ can also change the concrete user-selected media set while the coarse permission
     * classification remains SELECTED. Android does not expose a selected-set revision token to
     * this Activity, so a repeated resume under SELECTED authority conservatively invalidates the
     * presentation as well. The replacement Activity establishes a fresh baseline, preventing a
     * recreation loop while ensuring an open viewer cannot continue displaying a pre-change item.
     */
    fun requiresPresentationReset(
        previous: GalleryMediaAccessScope?,
        current: GalleryMediaAccessScope,
    ): Boolean =
        previous != null &&
            (previous != current || current == GalleryMediaAccessScope.SELECTED)
}
