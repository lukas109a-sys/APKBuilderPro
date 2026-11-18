package com.lucas.apkbuilder

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
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

        // Estrutura principal com Scroll para suportar múltiplos painéis
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#12141C"))
            isFillViewport = true
        }

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 50, 40, 50)
        }

        // Cabeçalho
        val titleText = TextView(this).apply {
            text = "O que pretende criar?"
            textSize = 24f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitleText = TextView(this).apply {
            text = "Escolha o formato. Suporte a HTML, JS, Kotlin, JSON e APKs nativos."
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

        // Cartão 1: Construção Rápida (HTML / Web / JS)
        mainLayout.addView(createMenuCard(
            "Construção Rápida (HTML / JS / Web)",
            "Transforme códigos web, scripts JS e páginas HTML em APK otimizado."
        ) {
            startBuildWithLanguage("HTML/JS", "index.html", "<html><body><h1>App Web Gerada</h1></body></html>")
        })

        // Cartão 2: Construção Avançada (Kotlin Nativo & Permissões)
        mainLayout.addView(createMenuCard(
            "Construção Avançada (Kotlin & Manifest)",
            "Controlo total de permissões (QUERY_ALL_PACKAGES) para Launchers e TV Box."
        ) {
            startBuildWithLanguage("Kotlin/Native", "MainActivity.kt", "package com.app\nclass CustomApp {}")
        })

        // Cartão 3: Gerador de Configurações (JSON / Utils)
        mainLayout.addView(createMenuCard(
            "Pacotes de Configuração & Utils",
            "Crie ferramentas de sistema, limpadores de RAM e utilitários via JSON."
        ) {
            startBuildWithLanguage("JSON/Config", "config.json", "{\"target\": \"tv_box\", \"permissions\": [\"QUERY_ALL_PACKAGES\"]}")
        })

        // Cartão 4: Executar Teste Geral do Motor
        mainLayout.addView(createMenuCard(
            "Compilar e Assinar Teste Final",
            "Empacota o projeto atual e aplica assinatura digital JAR/V1 automática."
        ) {
            runFullEngineTest()
        })

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    // Função para criar cartões visuais com o estilo da imagem de referência
    private fun createMenuCard(title: String, description: String, onClick: () -> Unit): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1F2430"))
            setPadding(35, 35, 35, 35)
            setOnClickListener { onClick() }
            
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setMargins(0, 0, 0, 25)
            layoutParams = params

            addView(TextView(context).apply {
                text = title
                textSize = 16f
                setTextColor(Color.WHITE)
                setTypeface(null, android.graphics.Typeface.BOLD)
            })

            addView(TextView(context).apply {
                text = description
                textSize = 13f
                setTextColor(Color.parseColor("#9CA3AF"))
                setPadding(0, 8, 0, 0)
            })
        }
    }

    private fun startBuildWithLanguage(lang: String, fileName: String, fileContent: String) {
        statusText.text = "A preparar projeto com suporte a [$lang]..."
        try {
            val projectDir = File(filesDir, "project_$lang").apply { mkdirs() }
            File(projectDir, fileName).writeText(fileContent)
            
            val unsignedApk = File(filesDir, "unsigned_$lang.apk")
            val signedApk = File(filesDir, "APKBuilderPro_${lang}_Final.apk")

            val built = ApkBuilderEngine.buildUnsignedApk(projectDir, unsignedApk)
            if (built) {
                val signed = ApkSigner.sign(unsignedApk, signedApk)
                if (signed) {
                    statusText.text = "Sucesso [$lang]! APK gerado:\n${signedApk.absolutePath}"
                    Toast.makeText(this, "APK ($lang) Criado e Assinado!", Toast.LENGTH_LONG).show()
                } else {
                    statusText.text = "Erro ao assinar APK para $lang."
                }
            } else {
                statusText.text = "Erro ao empacotar assets de $lang."
            }
        } catch (e: Exception) {
            statusText.text = "Erro: ${e.message}"
        }
    }

    private fun runFullEngineTest() {
        statusText.text = "A executar motor completo de compilação..."
        try {
            val sampleProject = File(filesDir, "sample_project").apply { mkdirs() }
            File(sampleProject, "manifest.xml").writeText("<manifest><uses-permission android:name=\"android.permission.QUERY_ALL_PACKAGES\"/></manifest>")

            val unsignedApk = File(filesDir, "unsigned_app.apk")
            val signedApk = File(filesDir, "APKBuilderPro_Master_Final.apk")

            val built = ApkBuilderEngine.buildUnsignedApk(sampleProject, unsignedApk)
            if (built) {
                val signed = ApkSigner.sign(unsignedApk, signedApk)
                if (signed) {
                    statusText.text = "Sucesso Mestre! APK guardado em:\n${signedApk.absolutePath}"
                    Toast.makeText(this, "APK Master Criado com Sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    statusText.text = "Erro na assinatura master."
                }
            } else {
                statusText.text = "Erro no empacotamento master."
            }
        } catch (e: Exception) {
            statusText.text = "Exceção: ${e.message}"
        }
    }
}
