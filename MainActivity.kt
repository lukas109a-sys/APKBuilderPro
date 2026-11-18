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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var horizontalAppsLayout: LinearLayout
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ecrã Principal Paisagem (Fundo Preto Absoluto)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#050505"))
            setPadding(25, 15, 25, 15)
        }

        // ==========================================
        // CABEÇALHO (Topo Esquerdo: Logo | Topo Direito: Ícones e Relógio)
        // ==========================================
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 10)
        }

        val logoText = TextView(this).apply {
            text = "RED TV"
            textSize = 18f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val iconsHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val searchIcon = createTopMiniBtn("🔍") { Toast.makeText(this, "Pesquisa", Toast.LENGTH_SHORT).show() }
        val msgIcon = createTopMiniBtn("✉️") { Toast.makeText(this, "Mensagens", Toast.LENGTH_SHORT).show() }
        val infoIcon = createTopMiniBtn("ℹ️") { Toast.makeText(this, "Informação do Sistema", Toast.LENGTH_SHORT).show() }
        
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateText = TextView(this).apply {
            text = "Quarta-feira | ${timeFormat.format(Date())}"
            textSize = 12f
            setTextColor(Color.parseColor("#CCCCCC"))
            setPadding(15, 0, 0, 0)
        }

        iconsHeaderLayout.addView(searchIcon)
        iconsHeaderLayout.addView(msgIcon)
        iconsHeaderLayout.addView(infoIcon)
        iconsHeaderLayout.addView(dateText)

        headerLayout.addView(logoText)
        headerLayout.addView(iconsHeaderLayout)
        rootLayout.addView(headerLayout)

        // ==========================================
        // CORPO CENTRAL (Esquerda: 3 botões | Centro: Player/Destaque | Direita: 3 botões)
        // ==========================================
        val middleBody = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.4f
            ).apply { setMargins(0, 0, 0, 10) }
        }

        // Coluna Esquerda (3 Botões empilhados exatamente como no modelo)
        val leftCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins(0, 0, 10, 0)
            }
        }
        leftCol.addView(createModelButton("📺 Guia TV") { Toast.makeText(this, "Guia TV", Toast.LENGTH_SHORT).show() })
        leftCol.addView(createModelButton("📁 Gravações") { Toast.makeText(this, "Gravações", Toast.LENGTH_SHORT).show() })
        leftCol.addView(createModelButton("🌐 Streaming") { Toast.makeText(this, "Streaming", Toast.LENGTH_SHORT).show() })
        middleBody.addView(leftCol)

        // Centro: Painel de Destaque Multimédia (Com moldura vermelha neon)
        val centerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#101010"))
                setStroke(2, Color.parseColor("#FF1E56"))
                cornerRadius = 14f
            }
            setPadding(20, 20, 20, 20)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.2f).apply {
                setMargins(0, 0, 10, 0)
            }
        }

        val cardMainTitle = TextView(this).apply {
            text = "CENTRAL RED TV"
            textSize = 15f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        val cardMainSub = TextView(this).apply {
            text = "Aceda rapidamente aos seus conteúdos e desfrute de navegação fluida por D-Pad."
            textSize = 11f
            setTextColor(Color.parseColor("#AAAAAA"))
        }
        centerCard.addView(cardMainTitle)
        centerCard.addView(cardMainSub)
        middleBody.addView(centerCard)

        // Coluna Direita (3 Botões empilhados exatamente como no modelo: Definições, Loja, Apps)
        val rightCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        rightCol.addView(createModelButton("⚙️ Definições") { 
            try { startActivity(Intent(Settings.ACTION_SETTINGS)) } catch (e: Exception) {}
        })
        rightCol.addView(createModelButton("🛒 Loja TV") { 
            try { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) } catch (e: Exception) {}
        })
        rightCol.addView(createModelButton("📱 Apps e Jogos") { 
            Toast.makeText(this, "Grelha de Apps abaixo", Toast.LENGTH_SHORT).show() 
        })
        middleBody.addView(rightCol)

        rootLayout.addView(middleBody)

        // ==========================================
        // PARTE INFERIOR: "Mes apps" (Carrossel Horizontal de Aplicações)
        // ==========================================
        val sectionTitle = TextView(this).apply {
            text = "Mes apps"
            textSize = 12f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 5)
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
                85
            )
        }
        rootLayout.addView(horizontalScroll)

        // Rodapé informativo subtil
        statusText = TextView(this).apply {
            text = "Modo TV Box operando com QUERY_ALL_PACKAGES."
            textSize = 9f
            setTextColor(Color.parseColor("#555555"))
            setPadding(0, 4, 0, 0)
        }
        rootLayout.addView(statusText)

        setContentView(rootLayout)
        loadInstalledAppsAsBanners()
    }

    private fun createTopMiniBtn(iconStr: String, onClick: () -> Unit): TextView {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#121212"))
            setStroke(1, Color.parseColor("#333333"))
            cornerRadius = 6f
        }
        return TextView(this).apply {
            text = iconStr
            textSize = 12f
            background = bg
            setPadding(10, 6, 10, 6)
            setOnClickListener { onClick() }
            isFocusable = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(6, 0, 0, 0) }
        }
    }

    private fun createModelButton(textStr: String, onClick: () -> Unit): TextView {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#141414"))
            setStroke(1, Color.parseColor("#262626"))
            cornerRadius = 8f
        }
        return TextView(this).apply {
            text = textStr
            textSize = 12f
            setTextColor(Color.WHITE)
            background = bg
            setPadding(15, 12, 15, 12)
            setOnClickListener { onClick() }
            isFocusable = true
            gravity = Gravity.CENTER_VERTICAL
            
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    background = GradientDrawable().apply {
                        setColor(Color.parseColor("#1E1E1E"))
                        setStroke(2, Color.parseColor("#FF1E56"))
                        cornerRadius = 8f
                    }
                } else {
                    background = bg
                }
            }

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply { setMargins(0, 0, 0, 5) }
        }
    }

    private fun loadInstalledAppsAsBanners() {
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
                        setStroke(1, Color.parseColor("#262626"))
                        cornerRadius = 8f
                    }

                    val appCard = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                        background = cardBg
                        setPadding(12, 8, 12, 8)
                        isFocusable = true
                        isClickable = true
                        
                        setOnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                background = GradientDrawable().apply {
                                    setColor(Color.parseColor("#1E1E1E"))
                                    setStroke(2, Color.parseColor("#FF1E56"))
                                    cornerRadius = 8f
                                }
                            } else {
                                background = cardBg
                            }
                        }

                        setOnClickListener {
                            try { startActivity(launchIntent) } catch (e: Exception) {}
                        }
                    }

                    val params = LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        setMargins(0, 0, 10, 0)
                    }
                    appCard.layoutParams = params

                    val iconText = TextView(this).apply {
                        text = "📺"
                        textSize = 18f
                        setPadding(0, 0, 8, 0)
                    }

                    val nameText = TextView(this).apply {
                        text = appName
                        textSize = 11f
                        setTextColor(Color.WHITE)
                        typeface = Typeface.DEFAULT_BOLD
                        maxLines = 2
                    }

                    appCard.addView(iconText)
                    appCard.addView(nameText)
                    horizontalAppsLayout.addView(appCard)
                }
            }
            statusText.text = "Disposição espelhada do modelo aplicada com sucesso."
        } catch (e: Exception) {
            statusText.text = "Erro ao carregar aplicações."
        }
    }
}
