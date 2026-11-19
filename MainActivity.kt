package com.lucas.apkbuilder

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
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
    private lateinit var spotlightCard: LinearLayout
    private lateinit var wallpaperArtText: TextView
    private lateinit var spotlightTitle: TextView
    private lateinit var spotlightDesc: TextView

    // Coleção de papéis de parede visuais dinâmicos (Arte abstrata, gradientes e temas OLED)
    private val wallpaperArtList = listOf(
        Triple("🌟 [ WALLPAPER: NEON RED & ABYSS ]", "RED TV CINEMATIC OLED", "Ecrã total com preto absoluto e moldura em vermelho vivo."),
        Triple("🌌 [ WALLPAPER: CÓSMICO PROFUNDO ]", "DEEP SPACE AMBIENT", "Fundo escuro estelar com gradientes de alta imersão visual."),
        Triple("⚡ [ WALLPAPER: CYBERPUNK RED ]", "FUTURISTIC RETRO", "Estética de luzes dinâmicas e design minimalista avançado."),
        Triple("🔥 [ WALLPAPER: AURORA BOREALIS ]", "RED GLOW DYNAMICS", "Transição de tons escuros profundos com brilho lateral em LED.")
    )

    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var rotatorRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#050505"))
            setPadding(24, 12, 24, 12)
        }

        // Cabeçalho
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 8)
        }

        val logoText = TextView(this).apply {
            text = "RED TV"
            textSize = 16f
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
        val infoIcon = createTopMiniBtn("ℹ️") { Toast.makeText(this, "Sistema", Toast.LENGTH_SHORT).show() }
        
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateText = TextView(this).apply {
            text = "Quarta-feira | ${timeFormat.format(Date())}"
            textSize = 11f
            setTextColor(Color.parseColor("#CCCCCC"))
            setPadding(10, 0, 0, 0)
        }

        iconsHeaderLayout.addView(searchIcon)
        iconsHeaderLayout.addView(msgIcon)
        iconsHeaderLayout.addView(infoIcon)
        iconsHeaderLayout.addView(dateText)

        headerLayout.addView(logoText)
        headerLayout.addView(iconsHeaderLayout)
        rootLayout.addView(headerLayout)

        // Corpo Central
        val middleBody = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply { setMargins(0, 0, 0, 8) }
        }

        // Coluna Esquerda
        val leftCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(160, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins(0, 0, 8, 0)
            }
        }
        leftCol.addView(createModelButton("📺 Guia TV") { Toast.makeText(this, "Guia TV", Toast.LENGTH_SHORT).show() })
        leftCol.addView(createModelButton("📁 Gravações") { Toast.makeText(this, "Gravações", Toast.LENGTH_SHORT).show() })
        leftCol.addView(createModelButton("🌐 Streaming") { Toast.makeText(this, "Streaming", Toast.LENGTH_SHORT).show() })
        middleBody.addView(leftCol)

        // Centro: Painel com Papel de Parede Dinâmico e Arte Visual Rotativa
        spotlightCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            background = createWallpaperBackground(0)
            setPadding(18, 18, 18, 18)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2f).apply {
                setMargins(0, 0, 8, 0)
            }
        }

        wallpaperArtText = TextView(this).apply {
            text = wallpaperArtList[0].first
            textSize = 13f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        spotlightTitle = TextView(this).apply {
            text = wallpaperArtList[0].second
            textSize = 14f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        spotlightDesc = TextView(this).apply {
            text = wallpaperArtList[0].third
            textSize = 10f
            setTextColor(Color.parseColor("#AAAAAA"))
        }

        spotlightCard.addView(wallpaperArtText)
        spotlightCard.addView(spotlightTitle)
        spotlightCard.addView(spotlightDesc)
        middleBody.addView(spotlightCard)

        // Coluna Direita (Loja TV -> Play Store)
        val rightCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(160, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        rightCol.addView(createModelButton("⚙️ Definições") { 
            try { startActivity(Intent(Settings.ACTION_SETTINGS)) } catch (e: Exception) {}
        })
        rightCol.addView(createModelButton("🛒 Loja TV") { 
            launchPackage("com.android.vending")
        })
        rightCol.addView(createModelButton("📱 Apps e Jogos") { 
            Toast.makeText(this, "Grelha em baixo", Toast.LENGTH_SHORT).show() 
        })
        middleBody.addView(rightCol)

        rootLayout.addView(middleBody)

        // Parte Inferior
        val sectionTitle = TextView(this).apply {
            text = "Mes apps"
            textSize = 11f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 3)
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
                75
            )
        }
        rootLayout.addView(horizontalScroll)

        setContentView(rootLayout)
        loadInstalledAppsAsBanners()
        startWallpaperVisualRotator()
    }

    // Cria fundos gráficos dinâmicos diferentes para cada papel de parede visual
    private fun createWallpaperBackground(index: Int): GradientDrawable {
        val colors = arrayOf(
            intArrayOf(Color.parseColor("#140A0D"), Color.parseColor("#050505")), // Tema 1: Vermelho escuro profundo
            intArrayOf(Color.parseColor("#091018"), Color.parseColor("#050505")), // Tema 2: Azul estelar escuro
            intArrayOf(Color.parseColor("#180A14"), Color.parseColor("#050505")), // Tema 3: Magenta escuro cyber
            intArrayOf(Color.parseColor("#0D140A"), Color.parseColor("#050505"))  // Tema 4: Verde escuro neon
        )
        return GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors[index % colors.size]).apply {
            setStroke(2, Color.parseColor("#FF1E56"))
            cornerRadius = 12f
        }
    }

    private fun startWallpaperVisualRotator() {
        rotatorRunnable = object : Runnable {
            override fun run() {
                currentIndex = (currentIndex + 1) % wallpaperArtList.size
                wallpaperArtText.text = wallpaperArtList[currentIndex].first
                spotlightTitle.text = wallpaperArtList[currentIndex].second
                spotlightDesc.text = wallpaperArtList[currentIndex].third

                // Altera completamente o fundo visual / papel de parede do painel central
                spotlightCard.background = createWallpaperBackground(currentIndex)

                handler.postDelayed(this, 5000) // Transição visual a cada 5 segundos
            }
        }
        handler.postDelayed(rotatorRunnable, 5000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::rotatorRunnable.isInitialized) {
            handler.removeCallbacks(rotatorRunnable)
        }
    }

    private fun createTopMiniBtn(iconStr: String, onClick: () -> Unit): TextView {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#121212"))
            setStroke(1, Color.parseColor("#333333"))
            cornerRadius = 6f
        }
        return TextView(this).apply {
            text = iconStr
            textSize = 11f
            background = bg
            setPadding(8, 4, 8, 4)
            setOnClickListener { onClick() }
            isFocusable = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(5, 0, 0, 0) }
        }
    }

    private fun createModelButton(textStr: String, onClick: () -> Unit): TextView {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#141414"))
            setStroke(1, Color.parseColor("#262626"))
            cornerRadius = 6f
        }
        return TextView(this).apply {
            text = textStr
            textSize = 11f
            setTextColor(Color.WHITE)
            background = bg
            setPadding(10, 8, 10, 8)
            setOnClickListener { onClick() }
            isFocusable = true
            gravity = Gravity.CENTER_VERTICAL
            
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    background = GradientDrawable().apply {
                        setColor(Color.parseColor("#1E1E1E"))
                        setStroke(2, Color.parseColor("#FF1E56"))
                        cornerRadius = 6f
                    }
                } else {
                    background = bg
                }
            }

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply { setMargins(0, 0, 0, 4) }
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
            Toast.makeText(this, "Erro ao abrir aplicação", Toast.LENGTH_SHORT).show()
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
                        cornerRadius = 6f
                    }

                    val appCard = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                        background = cardBg
                        setPadding(10, 4, 10, 4)
                        isFocusable = true
                        isClickable = true
                        
                        setOnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                background = GradientDrawable().apply {
                                    setColor(Color.parseColor("#1E1E1E"))
                                    setStroke(2, Color.parseColor("#FF1E56"))
                                    cornerRadius = 6f
                                }
                            } else {
                                background = cardBg
                            }
                        }

                        setOnClickListener {
                            try { startActivity(launchIntent) } catch (e: Exception) {}
                        }
                    }

                    val params = LinearLayout.LayoutParams(165, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        setMargins(0, 0, 8, 0)
                    }
                    appCard.layoutParams = params

                    val iconText = TextView(this).apply {
                        text = "📺"
                        textSize = 16f
                        setPadding(0, 0, 6, 0)
                    }

                    val nameText = TextView(this).apply {
                        text = appName
                        textSize = 10f
                        setTextColor(Color.WHITE)
                        typeface = Typeface.DEFAULT_BOLD
                        maxLines = 2
                    }

                    appCard.addView(iconText)
                    appCard.addView(nameText)
                    horizontalAppsLayout.addView(appCard)
                }
            }
        } catch (e: Exception) {
            // Tratamento silencioso
        }
    }
}
