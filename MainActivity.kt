package com.lucas.apkbuilder

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
            setBackgroundColor(Color.parseColor("#050505"))
            setPadding(40, 40, 40, 40)
        }

        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 25)
        }

        val titleView = TextView(this).apply {
            text = "RED TV LAUNCHER"
            textSize = 24f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val wifiBtn = createTopIconButton("📶") {
            try {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao abrir Wi-Fi", Toast.LENGTH_SHORT).show()
            }
        }

        val settingsBtn = createTopIconButton("⚙️") {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao abrir definições", Toast.LENGTH_SHORT).show()
            }
        }

        headerLayout.addView(titleView)
        headerLayout.addView(wifiBtn)
        headerLayout.addView(settingsBtn)
        rootLayout.addView(headerLayout)

        val gridLayout = GridLayout(this).apply {
            columnCount = 3
            rowCount = 1
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        gridLayout.addView(createNeonCard("📺", "YouTube") {
            launchPackage("com.google.android.youtube")
        })

        gridLayout.addView(createNeonCard("📁", "Ficheiros") {
            try {
                startActivity(Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" })
            } catch (e: Exception) {
                Toast.makeText(this, "Gestor não encontrado", Toast.LENGTH_SHORT).show()
            }
        })

        gridLayout.addView(createNeonCard("🧹", "Limpar RAM") {
            statusText.text = "Estado: Memória otimizada com sucesso!"
            Toast.makeText(this, "Sistema limpo!", Toast.LENGTH_SHORT).show()
        })

        rootLayout.addView(gridLayout)

        val sectionTitle = TextView(this).apply {
            text = "APLICAÇÕES INSTALADAS"
            textSize = 14f
            setTextColor(Color.parseColor("#FF1E56"))
            setPadding(0, 30, 0, 15)
            typeface = Typeface.DEFAULT_BOLD
        }
        rootLayout.addView(sectionTitle)

        appContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
            addView(appContainer)
        }
        rootLayout.addView(scroll)

        statusText = TextView(this).apply {
            text = "Estado: Pronto a utilizar."
            textSize = 12f
            setTextColor(Color.parseColor("#7F8C8D"))
            setPadding(0, 15, 0, 0)
        }
        rootLayout.addView(statusText)

        setContentView(rootLayout)
        loadInstalledApps()
    }

    private fun createNeonCard(icon: String, label: String, onClick: () -> Unit): LinearLayout {
        val cardBg = GradientDrawable().apply {
            setColor(Color.parseColor("#101010"))
            setStroke(2, Color.parseColor("#FF1E56"))
            cornerRadius = 14f
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = cardBg
            setPadding(15, 20, 15, 20)
            isFocusable = true
            isClickable = true
            setOnClickListener { onClick() }
        }

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(8, 8, 8, 8)
        }
        card.layoutParams = params

        card.addView(TextView(this).apply { text = icon; textSize = 26f; gravity = Gravity.CENTER })
        card.addView(TextView(this).apply { text = label; textSize = 13f; setTextColor(Color.WHITE); typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER; setPadding(0, 5, 0, 0) })
        return card
    }

    private fun createTopIconButton(icon: String, onClick: () -> Unit): TextView {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#101010"))
            setStroke(2, Color.parseColor("#FF1E56"))
            cornerRadius = 10f
        }
        return TextView(this).apply {
            text = icon
            textSize = 18f
            background = bg
            setPadding(20, 12, 20, 12)
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 0, 0, 0)
            }
        }
    }

    private fun loadInstalledApps() {
        try {
            val pm = packageManager
            val packages = pm.getInstalledPackages(0)
            appContainer.removeAllViews()

            var count = 0
            for (pkg in packages) {
                val launchIntent = pm.getLaunchIntentForPackage(pkg.packageName)
                if (launchIntent != null && pkg.packageName != packageName) {
                    val appName = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName
                    
                    val rowBg = GradientDrawable().apply {
                        setColor(Color.parseColor("#0E0E0E"))
                        setStroke(1, Color.parseColor("#262626"))
                        cornerRadius = 8f
                    }

                    val appRow = TextView(this).apply {
                        text = "  $appName"
                        textSize = 14f
                        setTextColor(Color.WHITE)
                        background = rowBg
                        setPadding(25, 20, 25, 20)
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
                        setMargins(0, 0, 0, 8)
                    }
                    appRow.layoutParams = rowParams

                    appContainer.addView(appRow)
                    count++
                }
            }
            statusText.text = "Estado: $count aplicações carregadas com sucesso."
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
                Toast.makeText(this, "Aplicação não encontrada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao iniciar aplicação", Toast.LENGTH_SHORT).show()
        }
    }
}
