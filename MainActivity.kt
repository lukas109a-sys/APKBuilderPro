package com.lucas.apkbuilder

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#12141C"))
            isFillViewport = true
        }

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 50, 40, 50)
        }

        val titleText = TextView(this).apply {
            text = "O que pretende criar?"
            textSize = 24f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitleText = TextView(this).apply {
            text = "Escolha o formato. Suporte a HTML, JS, Kotlin e JSON."
            textSize = 14f
            setTextColor(Color.parseColor("#9CA3AF"))
            setPadding(0, 10, 0, 30)
        }

        statusText = TextView(this).apply {
            text = "Sistema pronto para compilar..."
            textSize = 14f
            setTextColor(Color.parseColor("#34D399"))
            setPadding(0, 0, 0, 20)
        }

        mainLayout.addView(titleText)
        mainLayout.addView(subtitleText)
        mainLayout.addView(statusText)

        mainLayout.addView(createMenuCard("Construção Rápida (HTML / JS / Web)", "Transforme códigos web e páginas HTML em APK.") {
            startBuildWithLanguage("HTML", "index.html", "<h1>App Web Gerada</h1>")
        })

        mainLayout.addView(createMenuCard("Construção Avançada (Kotlin & Manifest)", "Controlo total de permissões (QUERY_ALL_PACKAGES).") {
            startBuildWithLanguage("Kotlin", "App.kt", "class App {}")
        })

        mainLayout.addView(createMenuCard("Compilar e Assinar Teste Final", "Empacota o projeto e aplica assinatura digital JAR/V1.") {
            runFullEngineTest()
        })

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    private fun createMenuCard(title: String, description: String, onClick: () -> Unit): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1F2430"))
            setPadding(35, 35, 35, 35)
            setOnClickListener { onClick() }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 25)
        }
        card.layoutParams = params

        card.addView(TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        })

        card.addView(TextView(this).apply {
            text = description
            textSize = 13f
            setTextColor(Color.parseColor("#9CA3AF"))
            setPadding(0, 8, 0, 0)
        })

        return card
    }

    private fun startBuildWithLanguage(lang: String, fileName: String, fileContent: String) {
        statusText.text = "A preparar projeto [$lang]..."
        try {
            val projectDir = File(filesDir, "project_$lang").apply { mkdirs() }
            File(projectDir, fileName).writeText(fileContent)
            
            val unsignedApk = File(filesDir, "unsigned_$lang.apk")
            val signedApk = File(filesDir, "APKBuilderPro_${lang}_Final.apk")

            val built = ApkBuilderEngine.buildUnsignedApk(projectDir, unsignedApk)
            if (built) {
                val signed = ApkSigner.sign(unsignedApk, signedApk)
                if (signed) {
                    statusText.text = "Sucesso [$lang]! APK em:\n${signedApk.absolutePath}"
                    Toast.makeText(this, "APK ($lang) Criado e Assinado!", Toast.LENGTH_LONG).show()
                } else {
                    statusText.text = "Erro ao assinar APK."
                }
            } else {
                statusText.text = "Erro ao empacotar assets."
            }
        } catch (e: Exception) {
            statusText.text = "Erro: ${e.message}"
        }
    }

    private fun runFullEngineTest() {
        statusText.text = "A executar motor completo..."
        try {
            val sampleProject = File(filesDir, "sample_project").apply { mkdirs() }
            File(sampleProject, "manifest.xml").writeText("<manifest><uses-permission android:name=\"android.permission.QUERY_ALL_PACKAGES\"/></manifest>")

            val unsignedApk = File(filesDir, "unsigned_app.apk")
            val signedApk = File(filesDir, "APKBuilderPro_Master_Final.apk")

            val built = ApkBuilderEngine.buildUnsignedApk(sampleProject, unsignedApk)
            if (built) {
                val signed = ApkSigner.sign(unsignedApk, signedApk)
                if (signed) {
                    statusText.text = "Sucesso Mestre! APK em:\n${signedApk.absolutePath}"
                    Toast.makeText(this, "APK Master Criado com Sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    statusText.text = "Erro na assinatura master."
                }
            } else {
                statusText.text = "Erro no empacotamento."
            }
        } catch (e: Exception) {
            statusText.text = "Exceção: ${e.message}"
        }
    }
}
