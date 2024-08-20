package com.example.mdpproject

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

@Composable
fun PdfViewer(
    pdfRenderer: PdfRenderer,
    currentPage: Int,
    onPageChange: (Int) -> Unit
) {
    val pageCount = pdfRenderer.pageCount

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val bitmap = PdfPageImage(pdfRenderer, currentPage)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentPage > 0) {
                Button(onClick = { onPageChange(currentPage - 1) }) {
                    Text("Previous")
                }
            }
            Text(text = "${currentPage + 1} / $pageCount", fontSize = 20.sp)
            if (currentPage < pageCount - 1) {
                Button(onClick = { onPageChange(currentPage + 1) }) {
                    Text("Next")
                }
            }
        }
    }
}
