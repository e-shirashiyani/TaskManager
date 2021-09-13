package org.maktab.taskmanager.utilities

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

class PictureUtils {
    private fun getScaledBitmap(filePath: String?, dstWidth: Int, dstHeight: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        val srcWidth = options.outWidth
        val srcHeight = options.outHeight
        val scaleFactor = Math.max(1, Math.min(srcWidth / dstWidth, srcHeight / dstHeight))
        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun getScaledBitmap(filePath: String?, activity: Activity): Bitmap? {
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return getScaledBitmap(filePath, size.x, size.y)
    }
}