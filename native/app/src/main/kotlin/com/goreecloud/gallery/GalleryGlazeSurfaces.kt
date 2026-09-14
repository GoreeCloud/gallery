package com.goreecloud.gallery

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

/**
 * Native Android material mapping for Gallery's GLAZE UI V1.3 Adaptive Resonance pass.
 *
 * Environmental color is deliberately low influence and non-semantic. Selection, destructive,
 * privacy, permission, and other protected states continue to use their semantic authorities.
 * These surfaces remain near-opaque so loss of transparency/blur cannot remove hierarchy or text
 * readability; Gallery does not require a blur implementation for V1.3 identity.
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
            Role.CONTROL -> 0.055f
            Role.CHROME -> 0.035f
            Role.RAISED -> 0.045f
            Role.OVERLAY -> 0.025f
        }.coerceAtMost(GalleryGlazeContract.ENVIRONMENT_TINT_MAX_FRACTION)
        val surface = mix(base, environment, tintFraction)
        val alpha = when (role) {
            Role.CONTROL -> if (isNight) 0.96f else 0.94f
            Role.CHROME -> 0.98f
            Role.RAISED -> 0.97f
            Role.OVERLAY -> 1f
        }
        val strokeAlpha = when (role) {
            Role.CONTROL -> if (isNight) 0.18f else 0.13f
            Role.CHROME -> if (isNight) 0.20f else 0.14f
            Role.RAISED -> if (isNight) 0.17f else 0.12f
            Role.OVERLAY -> if (isNight) 0.14f else 0.09f
        }

        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(withAlpha(surface, alpha))
            cornerRadius = dp(context, radiusDp).toFloat()
            setStroke(dp(context, 1), withAlpha(environment, strokeAlpha))
        }
    }

    private fun mix(base: Int, tint: Int, fraction: Float): Int {
        val amount = fraction.coerceIn(0f, GalleryGlazeContract.ENVIRONMENT_TINT_MAX_FRACTION)
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
