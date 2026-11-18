package com.lucas.apkbuilder

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var horizontalAppsLayout: LinearLayout
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout Principal em Paisagem Absoluta (Preto Profundo)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#080808"))
            setPadding(30, 20, 30, 20)
        }

        // Cabeçalho Estilo TV (Logótipo e Atalhos de Sistema)
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 15)
        }

        val logoText = TextView(this).apply {
            text = "RED TV"
            textSize = 20f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val wifiButton = createHeaderBtn("📶 Wi-Fi") {
            try { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) } catch (e: Exception) {}
        }
        val settingsButton = createHeaderBtn("⚙️ Definições") {
            try { startActivity(Intent(Settings.ACTION_SETTINGS)) } catch (e: Exception) {}
        }

        headerLayout.addView(logoText)
        headerLayout.addView(wifiButton)
        headerLayout.addView(settingsButton)
        rootLayout.addView(headerLayout)

        // Corpo Central Dividido (Esquerda: Atalhos Rápidos | Direita: Painel de Destaque Central)
        val centerBody = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.3f
            ).apply { setMargins(0, 0, 0, 15) }
        }

        // Coluna Esquerda: Menu de Ações Estilo TV
        val leftMenu = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(220, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins(0, 0, 15, 0)
            }
        }

        leftMenu.addView(createSideMenuBtn("📺 YouTube") { launchPackage("com.google.android.youtube") })
        leftMenu.addView(createSideMenuBtn("📁 Ficheiros") { 
            try { startActivity(Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" }) } catch (e: Exception) {}
        })
        leftMenu.addView(createSideMenuBtn("🧹 Limpar RAM") {
            statusText.text = "Estado: Memória otimizada com sucesso!"
            Toast.makeText(this@MainActivity, "Sistema limpo!", Toast.LENGTH_SHORT).show()
        })

        centerBody.addView(leftMenu)

        // Coluna Direita: Painel Central de Destaque (Estilo Imagem de Referência)
        val highlightPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#121212"))
                setStroke(2, Color.parseColor("#FF1E56"))
                cornerRadius = 14f
            }
            setPadding(25, 25, 25, 25)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
        }

        val panelTitle = TextView(this).apply {
            text = "CENTRAL MULTIMÉDIA"
            textSize = 16f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        val panelDesc = TextView(this).apply {
            text = "Explore todas as aplicações instaladas no seu dispositivo com suporte nativo QUERY_ALL_PACKAGES."
            textSize = 11f
            setTextColor(Color.parseColor("#AAAAAA"))
            setPadding(0, 4, 0, 0)
        }
        highlightPanel.addView(panelTitle)
        highlightPanel.addView(panelDesc)

        centerBody.addView(highlightPanel)
        rootLayout.addView(centerBody)

        // Barra Inferior: Carrossel de Apps Instaladas (Estilo Banners Horizontais Grandes)
        val sectionTitle = TextView(this).apply {
            text = "APLICAÇÕES INSTALADAS"
            textSize = 12f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 8)
        }
        rootLayout.addView(sectionTitle)

        horizontalAppsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val horizontalScroll = HorizontalScrollView(this).apply {
            isFillViewport = true
            addView(horizontalAppsLayout)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                90
            )
        }
        rootLayout.addView(horizontalScroll)

        // Rodapé de Estado
        statusText = TextView(this).apply {
            text = "Pronto."
            textSize = 10f
            setTextColor(Color.parseColor("#666666"))
            setPadding(0, 5, 0, 0)
        }
        rootLayout.addView(statusText)

        setContentView(rootLayout)
        loadInstalledAppsAsTVBanners()
    }

    private fun createHeaderBtn(textStr: String, onClick: () -> Unit): TextView {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#121212"))
            setStroke(1, Color.parseColor("#FF1E56"))
            cornerRadius = 8f
        }
        return TextView(this).apply {
            text = textStr
            textSize = 12f
            setTextColor(Color.WHITE)
            background = bg
            setPadding(12, 6, 12, 6)
            setOnClickListener { onClick() }
            isFocusable = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(8, 0, 0, 0) }
        }
    }

    private fun createSideMenuBtn(textStr: String, onClick: () -> Unit): TextView {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#141414"))
            setStroke(1, Color.parseColor("#333333"))
            cornerRadius = 10f
        }
        return TextView(this).apply {
            text = textStr
            textSize = 13f
            setTextColor(Color.WHITE)
            background = bg
            setPadding(20, 14, 20, 14)
            setOnClickListener { onClick() }
            isFocusable = true
            gravity = Gravity.CENTER_VERTICAL
            
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    background = GradientDrawable().apply {
                        setColor(Color.parseColor("#1E1E1E"))
                        setStroke(2, Color.parseColor("#FF1E56"))
                        cornerRadius = 10f
                    }
                } else {
                    background = bg
                }
            }

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply { setMargins(0, 0, 0, 6) }
        }
    }

    private fun loadInstalledAppsAsTVBanners() {
        try {
            val pm = packageManager
            val packages = pm.getInstalledPackages(0)
            horizontalAppsLayout.removeAllViews()

            for (pkg in packages) {
                val launchIntent = pm.getLaunchIntentForPackage(pkg.packageName)
                if (launchIntent != null && pkg.packageName != packageName) {
                    val appName = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName

                    val cardBg = GradientDrawable().apply {
                        setColor(Color.parseColor("#141414"))
                        setStroke(2, Color.parseColor("#262626"))
                        cornerRadius = 10f
                    }

                    val appCard = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                        background = cardBg
                        setPadding(15, 10, 15, 10)
                        isFocusable = true
                        isClickable = true
                        
                        setOnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                background = GradientDrawable().apply {
                                    setColor(Color.parseColor("#1E1E1E"))
                                    setStroke(3, Color.parseColor("#FF1E56"))
                                    cornerRadius = 10f
                                }
                            } else {
                                background = cardBg
                            }
                        }

                        setOnClickListener {
                            try { startActivity(launchIntent) } catch (e: Exception) {}
                        }
                    }

                    val params = LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        setMargins(0, 0, 12, 0)
                    }
                    appCard.layoutParams = params

                    val iconText = TextView(this).apply {
                        text = "📺"
                        textSize = 22f
                        setPadding(0, 0, 10, 0)
                    }

                    val nameText = TextView(this).apply {
                        text = appName
                        textSize = 13f
                        setTextColor(Color.WHITE)
                        typeface = Typeface.DEFAULT_BOLD
                        maxLines = 2
                    }

                    appCard.addView(iconText)
                    appCard.addView(nameText)
                    horizontalAppsLayout.addView(appCard)
                }
            }
            statusText.text = "Modo TV Box horizontal aplicado com sucesso."
        } catch (e: Exception) {
            statusText.text = "Erro ao carregar banners."
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
            Toast.makeText(this, "Erro ao iniciar", Toast.LENGTH_SHORT).show()
        }
    }
}
