package com.example.mdpproject

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class SubActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 부여된 경우
        } else {
            // 권한이 부여되지 않은 경우
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            // 권한이 이미 부여된 경우
        }

        setContent {
            PdfViewerApp()
        }

        fun setStatusBarColor(colorResId: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = ContextCompat.getColor(this, colorResId)
            }
        }

        fun setStatusBarIconColor(isDark: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decor = window.decorView
                if (isDark) {
                    decor.systemUiVisibility = decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                } else {
                    decor.systemUiVisibility = decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }

        setStatusBarColor(R.color.black)
        setStatusBarIconColor(isDark = true)
    }
}

@Composable
fun PdfViewerApp() {
    val context = LocalContext.current
    val viewModel: PdfViewModel = viewModel()

    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var showFileList by remember { mutableStateOf(true) }

    val customFontFamily = FontFamily(
        Font(R.font.regular, FontWeight.Normal),
        Font(R.font.bold, FontWeight.Bold)
    )

    val buttonTextStyle = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    )

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (showFileList) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select a PDF file",
                    style = buttonTextStyle,
                    modifier = Modifier.padding(16.dp)
                )
                FileListView(
                    onFileSelected = { file ->
                        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(Uri.fromFile(file), "r")
                        pdfRenderer = parcelFileDescriptor?.let { PdfRenderer(it) }
                        showFileList = false
                    },
                    onBackToMain = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        (context as ComponentActivity).finish()
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (pdfRenderer != null) {
                    val pageCount = pdfRenderer!!.pageCount
                    val currentPage by viewModel.currentPage.collectAsState()

                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text("Previous", style = buttonTextStyle)
                        }
                        Text(
                            text = "${currentPage + 1} / $pageCount",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Button(
                            onClick = { viewModel.nextPage(pageCount) },
                            enabled = currentPage < pageCount - 1,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text("Next", style = buttonTextStyle)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            pdfRenderer?.close()
                            pdfRenderer = null
                            showFileList = true
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Go to PDF List", style = buttonTextStyle)
                    }
                }
            }
        }
    }
}

@Composable
fun FileListView(onFileSelected: (File) -> Unit, onBackToMain: () -> Unit) {
    val files = getFileList("/storage/emulated/0/Documents/MDP/")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f) // This makes the list take up all available space
        ) {
            items(files) { file ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFileSelected(file) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = file.name,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
            }
        }
        Button(
            onClick = onBackToMain,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("GO BACK")
        }
    }
}

fun getFileList(directoryPath: String): List<File> {
    val directory = File(directoryPath)
    return directory.listFiles()?.toList() ?: emptyList()
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
