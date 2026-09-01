package com.example.biomedix.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import java.io.OutputStream

object CaptureUtils {

    /**
     * Captures the full content of a ScrollView as a Bitmap.
     * Adapted from Grok's recommendation for legacy View support.
     */
    fun captureFullScrollContent(scrollView: ScrollView): Bitmap {
        val content = scrollView.getChildAt(0)
            ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        // Ensure we have a real content size
        if (content.width == 0 || content.height == 0) {
            content.measure(
                View.MeasureSpec.makeMeasureSpec(scrollView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            content.layout(0, 0, content.measuredWidth, content.measuredHeight)
        }

        val width = content.width.coerceAtLeast(content.measuredWidth)
        val height = content.height.coerceAtLeast(content.measuredHeight)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bg = content.background
        if (bg != null) bg.draw(canvas) else canvas.drawColor(Color.WHITE)

        content.draw(canvas)
        return bitmap
    }

    /**
     * Captures any View (including ComposeView) as it appears on screen.
     */
    fun captureView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    /**
     * Saves a Bitmap to the gallery.
     */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
        val filename = "BioMedix_Report_${System.currentTimeMillis()}.png"
        val outputStream: OutputStream?
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BioMedix")
            }
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = imageUri?.let { resolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val image = java.io.File(imagesDir, filename)
            outputStream = java.io.FileOutputStream(image)
        }

        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(context, "Report saved to Gallery!", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Failed to save report.", Toast.LENGTH_SHORT).show()
        }
    }
}
