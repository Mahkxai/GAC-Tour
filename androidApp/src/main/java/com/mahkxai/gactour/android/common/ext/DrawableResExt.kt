package com.mahkxai.gactour.android.common.ext

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

fun @receiver:DrawableRes Int.resize(
    context: Context,
    widthDp: Float
): Bitmap {
    // Convert dp size to pixels for the radius
    val scale = context.resources.displayMetrics.density
    val widthPx = (widthDp * scale).toInt()

    // Calculate width and height while maintaining the aspect ratio
    val drawable = ContextCompat.getDrawable(context, this)
    drawable?.let {
        val aspectRatio = it.intrinsicHeight.toFloat() / it.intrinsicWidth.toFloat()
        val heightPx = (widthPx * aspectRatio).toInt()
        return it.toBitmap(width = widthPx, height = heightPx)
    } ?: throw Resources.NotFoundException("Drawable resource not found.")
}