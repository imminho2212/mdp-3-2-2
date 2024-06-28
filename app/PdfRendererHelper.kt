package com.example.mdpproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileOutputStream

fun copyAssetAndReturnFile(context: Context, fileName: String): File {
    val file = File(context.cacheDir, fileName)
    context.assets.open(fileName).use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return file
}

@Composable
fun rememberPdfRenderer(context: Context, file: File): PdfRenderer {
    val currentFile by rememberUpdatedState(file)
    return remember {
        val fileDescriptor = ParcelFileDescriptor.open(currentFile, ParcelFileDescriptor.MODE_READ_ONLY)
        PdfRenderer(fileDescriptor)
    }
}

@Composable
fun PdfPageImage(pdfRenderer: PdfRenderer, pageIndex: Int): Bitmap {
    val page = pdfRenderer.openPage(pageIndex)
    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    page.close()
    return bitmap
}
