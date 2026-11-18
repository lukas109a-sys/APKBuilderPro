package com.lucas.apkbuilder

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                startBuildProcess()
            }
        }

        layout.addView(statusText)
        layout.addView(buildButton)
        setContentView(layout)
    }

    private fun startBuildProcess() {
        statusText.text = "A criar APK de teste..."
        
        try {
            val sampleProject = File(filesDir, "sample_project").apply { mkdirs() }
            File(sampleProject, "config.txt").writeText("App Gerada via APKBuilderPro")

            val unsignedApk = File(filesDir, "unsigned_app.apk")
            val signedApk = File(filesDir, "APKBuilderPro_Final.apk")

            if (unsignedApk.exists()) unsignedApk.delete()
            if (signedApk.exists()) signedApk.delete()

            val built = ApkBuilderEngine.buildUnsignedApk(sampleProject, unsignedApk)
            
            if (built) {
                val signed = ApkSigner.sign(unsignedApk, signedApk)
                if (signed) {
                    statusText.text = "Sucesso absoluto!\nAPK guardado em:\n${signedApk.absolutePath}"
                    Toast.makeText(this, "APK Criado e Assinado com Sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    statusText.text = "Erro: Falha ao assinar o APK."
                }
            } else {
                statusText.text = "Erro: Falha ao empacotar o projeto."
            }
        } catch (e: Exception) {
            statusText.text = "Exceção: ${e.message}"
            e.printStackTrace()
        }
    }
}
