package com.example.mdpproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settingButton()


        statusTextView = findViewById(R.id.statusTextView)
        val downloadButton: Button = findViewById(R.id.button)

        // 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        downloadButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result = downloadAndSaveFile("http://192.168.137.24:5000/download_folder", "file.pdf", "Documents")
                statusTextView.text = result
            }
        }
    }
    fun settingButton(){
        val button = findViewById<Button>(R.id.GoToPDF)
        button.setOnClickListener{
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었을 때의 동작
            }
        }
    }

    private suspend fun downloadAndSaveFile(fileUrl: String, fileName: String, directoryName: String): String {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(fileUrl).build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }

                    val body = response.body ?: throw IOException("Empty response body")

//                    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), directoryName)
                    val directory = File("/storage/emulated/0/Documents/Documents")
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }

                    val file = File(directory, fileName)
                    FileOutputStream(file).use { fos ->
                        body.byteStream().use { input ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                fos.write(buffer, 0, bytesRead)
                            }
                            fos.flush()
                        }
                    }

                    "Download completed. File saved to: ${file.absolutePath}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
        }
    }
}

