package com.lucas.apkbuilder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var buildButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            gravity = android.view.Gravity.CENTER
        }

        statusText = TextView(this).apply {
            text = "APKBuilderPro Pronto para Uso"
            textSize = 18f
            setPadding(0, 0, 0, 30)
            gravity = android.view.Gravity.CENTER
        }

        buildButton = Button(this).apply {
            text = "Testar Construção de APK"
            setOnClickListener {
                if (checkPermissions()) {
                    startBuildProcess()
                } else {
                    requestPermissions()
                }
            }
        }

        layout.addView(statusText)
        layout.addView(buildButton)
        setContentView(layout)

        checkPermissions()
    }

    private fun checkPermissions(): Boolean {
        val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            100
        )
    }

    private fun startBuildProcess() {
        statusText.text = "A criar APK..."
        
        val sampleProject = File(filesDir, "sample_project").apply { mkdirs() }
        File(sampleProject, "config.txt").writeText("App Gerada via APKBuilderPro")

        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val unsignedApk = File(outputDir, "unsigned_app.apk")
        val signedApk = File(outputDir, "APKBuilderPro_Final.apk")

        val built = ApkBuilderEngine.buildUnsignedApk(sampleProject, unsignedApk)
        
        if (built) {
            val signed = ApkSigner.sign(unsignedApk, signedApk)
            if (signed) {
                statusText.text = "Sucesso! APK guardado em Downloads:\n${signedApk.absolutePath}"
                Toast.makeText(this, "APK Criado e Assinado com Sucesso!", Toast.LENGTH_LONG).show()
            } else {
                statusText.text = "Erro ao assinar o APK."
            }
        } else {
            statusText.text = "Erro ao empacotar o projeto."
        }
    }
}
