package com.example.mdpproject

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SubActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PdfViewerApp()
        }
    }
}

@Composable
fun PdfViewerApp() {
    val context = LocalContext.current
    val viewModel: PdfViewModel = viewModel()

    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            pdfRenderer = parcelFileDescriptor?.let { PdfRenderer(it) }
            pdfUri = uri
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { pdfLauncher.launch(arrayOf("application/pdf")) },
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("PDF 목록")
            }

            if (pdfRenderer != null) {
                val pageCount = pdfRenderer!!.pageCount
                val currentPage by viewModel.currentPage.collectAsState()

                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    // PDF 페이지 표시
                    pdfRenderer?.openPage(currentPage)?.let { page ->
                        val bitmap = page.renderPage()
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        page.close()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Button(
                        onClick = { viewModel.previousPage() },
                        enabled = currentPage > 0,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("이전 페이지")
                    }
                    Text(
                        text = "${currentPage + 1} / $pageCount",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Button(
                        onClick = { viewModel.nextPage(pageCount) },
                        enabled = currentPage < pageCount - 1,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("다음 페이지")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        pdfRenderer?.close()
                        pdfRenderer = null
                        pdfUri = null
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("돌아가기")
                }
            }
        }
    }
}

fun PdfRenderer.Page.renderPage(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    return bitmap
}

class PdfViewModel : ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    fun nextPage(pageCount: Int) {
        _currentPage.update { current ->
            if (current < pageCount - 1) current + 1 else current
        }
    }

    fun previousPage() {
        _currentPage.update { current ->
            if (current > 0) current - 1 else current
        }
    }
}
