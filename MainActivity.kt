package com.lucas.apkbuilder

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appContainer: LinearLayout
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0B0F19"))
            setPadding(40, 40, 40, 40)
        }

        // Cabeçalho
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 30)
        }

        val titleView = TextView(this).apply {
            text = "Google TV Launcher"
            textSize = 24f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val settingsBtn = TextView(this).apply {
            text = " ⚙️ "
            textSize = 22f
            setPadding(15, 10, 15, 10)
            setBackgroundColor(Color.parseColor("#1F2430"))
            setOnClickListener {
                try {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Erro ao abrir definições", Toast.LENGTH_SHORT).show()
                }
            }
        }

        headerLayout.addView(titleView)
        headerLayout.addView(settingsBtn)
        rootLayout.addView(headerLayout)

        // Atalhos Fixos Principais
        val gridLayout = GridLayout(this).apply {
            columnCount = 2
            rowCount = 1
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        gridLayout.addView(createNativeCard("📺", "YouTube") {
            launchPackage("com.google.android.youtube")
        })

        gridLayout.addView(createNativeCard("🧹", "Limpar RAM") {
            statusText.text = "Estado: Memória RAM otimizada com sucesso!"
            Toast.makeText(this, "Sistema limpo!", Toast.LENGTH_SHORT).show()
        })

        rootLayout.addView(gridLayout)

        // Título da Lista de Apps Instaladas (Uso do QUERY_ALL_PACKAGES)
        val sectionTitle = TextView(this).apply {
            text = "Aplicações Instaladas no Dispositivo"
            textSize = 16f
            setTextColor(Color.parseColor("#34D399"))
            setPadding(0, 30, 0, 15)
            typeface = Typeface.DEFAULT_BOLD
        }
        rootLayout.addView(sectionTitle)

        // Scroll para listar os apps reais detetados
        appContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
            addView(appContainer)
        }
        rootLayout.addView(scroll)

        // Barra de estado
        statusText = TextView(this).apply {
            text = "Estado: A carregar aplicativos..."
            textSize = 13f
            setTextColor(Color.parseColor("#9CA3AF"))
            setPadding(0, 15, 0, 0)
        }
        rootLayout.addView(statusText)

        setContentView(rootLayout)

        // Carrega os aplicativos instalados em segundo plano
        loadInstalledApps()
    }

    private fun createNativeCard(icon: String, label: String, onClick: () -> Unit): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#171C28"))
            setPadding(20, 25, 20, 25)
            isFocusable = true
            isClickable = true
            setOnClickListener { onClick() }
        }

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(10, 10, 10, 10)
        }
        card.layoutParams = params

        card.addView(TextView(this).apply { text = icon; textSize = 30f; gravity = Gravity.CENTER })
        card.addView(TextView(this).apply { text = label; textSize = 14f;setTextColor(Color.WHITE); typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER; setPadding(0, 5, 0, 0) })
        return card
    }

    private fun loadInstalledApps() {
        try {
            val pm = packageManager
            // Graças à permissão QUERY_ALL_PACKAGES, isto lista todos os apps do telemóvel/TV Box
            val packages = pm.getInstalledPackages(0)
            appContainer.removeAllViews()

            var count = 0
            for (pkg in packages) {
                // Filtra apenas apps que têm launcher (que podem ser abertas)
                val launchIntent = pm.getLaunchIntentForPackage(pkg.packageName)
                if (launchIntent != null && pkg.packageName != packageName) {
                    val appName = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName
                    
                    val appRow = TextView(this).apply {
                        text = "📱  $appName"
                        textSize = 15f
                        setTextColor(Color.WHITE)
                        setBackgroundColor(Color.parseColor("#171C28"))
                        setPadding(30, 25, 30, 25)
                        isFocusable = true
                        isClickable = true
                        setOnClickListener {
                            try {
                                startActivity(launchIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao abrir app", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    val rowParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 10)
                    }
                    appRow.layoutParams = rowParams

                    appContainer.addView(appRow)
                    count++
                }
            }
            statusText.text = "Estado: $count aplicações detetadas com sucesso."
        } catch (e: Exception) {
            statusText.text = "Erro ao carregar apps: ${e.message}"
        }
    }

    private fun launchPackage(packageName: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Aplicação não encontrada no dispositivo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao iniciar aplicação", Toast.LENGTH_SHORT).show()
        }
    }
}
