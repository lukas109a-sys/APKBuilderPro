package com.lucas.apkbuilder

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var inputName: EditText
    private lateinit var inputCode: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica permissões de armazenamento em runtime
        checkStoragePermissions()

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#12141C"))
            isFillViewport = true
        }

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val titleText = TextView(this).apply {
            text = "Gerador de APK com Permissões Totais"
            textSize = 20f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitleText = TextView(this).apply {
            text = "Cria um APK funcional com QUERY_ALL_PACKAGES, Webview e Acesso Total."
            textSize = 13f
            setTextColor(Color.parseColor("#9CA3AF"))
            setPadding(0, 5, 0, 20)
        }

        statusText = TextView(this).apply {
            text = "Pronto para gerar o teu APK..."
            textSize = 14f
            setTextColor(Color.parseColor("#34D399"))
            setPadding(0, 0, 0, 20)
        }

        // Campo para o nome da App
        inputName = EditText(this).apply {
            hint = "Nome da App (ex: MinhaLauncher)"
            setHintTextColor(Color.parseColor("#6B7280"))
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1F2430"))
            setPadding(30, 30, 30, 30)
        }

        // Campo para o Código HTML/JS ou Conteúdo
        inputCode = EditText(this).apply {
            hint = "Insere aqui o teu código HTML/JS ou descrição..."
            setHintTextColor(Color.parseColor("#6B7280"))
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1F2430"))
            setPadding(30, 30, 30, 30)
            minLines = 4
        }

        val generateButton = Button(this).apply {
            text = "GERAR APK COM PERMISSÕES TOTAIS"
            setBackgroundColor(Color.parseColor("#4F46E5"))
            setTextColor(Color.WHITE)
            setPadding(0, 30, 0, 30)
            setOnClickListener {
                generateRealApp()
            }
        }

        val paramsMargin = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 25)
        }

        mainLayout.addView(titleText)
        mainLayout.addView(subtitleText)
        mainLayout.addView(statusText)
        mainLayout.addView(inputName.apply { layoutParams = paramsMargin })
        mainLayout.addView(inputCode.apply { layoutParams = paramsMargin })
        mainLayout.addView(generateButton.apply { layoutParams = paramsMargin })

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    private fun checkStoragePermissions() {
        val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        }
    }

    private fun generateRealApp() {
        val appName = inputName.text.toString().ifEmpty { "AppGerada" }
        val userContent = inputCode.text.toString().ifEmpty { "<h1>Olá Mundo - App com Permissões Totais</h1>" }

        statusText.text = "A empacotar $appName com permissões de sistema..."

        try {
            // Diretório de projeto local no telemóvel
            val projectDir = File(filesDir, appName).apply { mkdirs() }
            
            // Cria o ficheiro de conteúdo principal
            File(projectDir, "index.html").writeText(userContent)

            // Cria o AndroidManifest.xml injetando explicitamente as permissões avançadas
            val manifestContent = """
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                    package="com.lucas.$appName">
                    <uses-permission android:name="android.permission.INTERNET"/>
                    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"/>
                    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
                    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
                    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
                    <application android:label="$appName">
                        <activity android:name=".MainActivity" android:exported="true">
                            <intent-filter>
                                <action android:name="android.intent.action.MAIN"/>
                                <category android:name="android.intent.category.LAUNCHER"/>
                            </intent-filter>
                        </activity>
                    </application>
                </manifest>
            """.trimIndent()
            File(projectDir, "AndroidManifest.xml").writeText(manifestContent)

            // Diretório de saída na pasta Downloads pública para faculdade de acesso
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadDir.exists()) downloadDir.mkdirs()

            val unsignedApk = File(filesDir, "${appName}_unsigned.apk")
            val signedApk = File(downloadDir, "${appName}_Final.apk")

            // Executa o Motor Nativo de Empacotamento
            val built = ApkBuilderEngine.buildUnsignedApk(projectDir, unsignedApk)
            
            if (built) {
                // Assina digitalmente para o Android aceitar a instalação
                val signed = ApkSigner.sign(unsignedApk, signedApk)
                if (signed) {
                    statusText.text = "Sucesso Total!\nAPK guardado em Downloads:\n${signedApk.absolutePath}"
                    Toast.makeText(this, "APK Criado com Permissões Totais!", Toast.LENGTH_LONG).show()
                } else {
                    statusText.text = "Erro: Falha ao assinar digitalmente o APK."
                }
            } else {
                statusText.text = "Erro: Falha ao empacotar os ficheiros da app."
            }
        } catch (e: Exception) {
            statusText.text = "Erro crítico: ${e.message}"
            e.printStackTrace()
        }
    }
}
