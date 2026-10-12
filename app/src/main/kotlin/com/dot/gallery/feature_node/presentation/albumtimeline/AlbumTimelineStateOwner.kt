package com.dot.gallery.feature_node.presentation.albumtimeline

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.dot.gallery.feature_node.presentation.util.Screen

internal fun albumViewerStateOwner(
    navController: NavHostController,
    viewerEntry: NavBackStackEntry,
    albumId: Long,
): NavBackStackEntry {
    val parent = try {
        navController.getBackStackEntry(Screen.AlbumViewScreen.albumAndName())
    } catch (_: IllegalArgumentException) {
        null
    }
    return parent?.takeIf {
        it.arguments?.containsKey("albumId") == true &&
            it.arguments?.getLong("albumId") == albumId
    } ?: viewerEntry
}
