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
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var horizontalAppsLayout: LinearLayout
    private lateinit var spotlightTitle: TextView
    private lateinit var spotlightDesc: TextView

    // Lista dinâmica que vai receber os filmes reais da web
    private var realHighlights = mutableListOf<Pair<String, String>>()
    private var currentHighlightIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var highlightRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ecrã Inteiro Absoluto (Sem barra de título)
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

        // ==========================================
        // CABEÇALHO
        // ==========================================
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

        // ==========================================
        // CORPO CENTRAL (3 Colunas)
        // ==========================================
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

        // Centro: Painel de Destaque com Lançamentos Reais da Web
        val centerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#121212"))
                setStroke(2, Color.parseColor("#FF1E56"))
                cornerRadius = 12f
            }
            setPadding(18, 18, 18, 18)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2f).apply {
                setMargins(0, 0, 8, 0)
            }
        }

        val spotlightPoster = TextView(this).apply {
            text = "🎬 [ CINEMA & STREAMING AO VIVO ]"
            textSize = 13f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        spotlightTitle = TextView(this).apply {
            text = "A carregar lançamentos reais da web..."
            textSize = 14f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        spotlightDesc = TextView(this).apply {
            text = "Aguarde enquanto sincronizamos com os cinemas."
            textSize = 10f
            setTextColor(Color.parseColor("#AAAAAA"))
        }

        centerCard.addView(spotlightPoster)
        centerCard.addView(spotlightTitle)
        centerCard.addView(spotlightDesc)
        middleBody.addView(centerCard)

        // Coluna Direita (Botão Loja TV ligado à Play Store)
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

        // ==========================================
        // PARTE INFERIOR: "Mes apps"
        // ==========================================
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
        fetchRealMoviesFromWeb()
    }

    // Puxa os filmes reais em cartaz através de uma API pública de cinema em background
    private fun fetchRealMoviesFromWeb() {
        thread {
            try {
                // Endpoint público de filmes populares / em cartaz
                val url = "https://api.themoviedb.org/3/movie/now_playing?api_key=c22634e304b4f535352c8b74542d997d&language=pt-PT"
                val response = URL(url).readText()
                val json = JSONObject(response)
                val results = json.getJSONArray("results")

                val fetchedList = mutableListOf<Pair<String, String>>()
                for (i in 0 until minOf(results.length(), 6)) {
                    val movie = results.getJSONObject(i)
                    val title = movie.getString("title")
                    val overview = movie.getString("overview")
                    val cleanOverview = if (overview.length > 90) overview.substring(0, 90) + "..." else overview
                    fetchedList.add(Pair("🔥 ESTREIA CINEMA: $title", cleanOverview))
                }

                if (fetchedList.isNotEmpty()) {
                    realHighlights = fetchedList
                    handler.post {
                        startHighlightRotator()
                    }
                }
            } catch (e: Exception) {
                handler.post {
                    spotlightTitle.text = "🎬 CINEMA RED TV"
                    spotlightDesc.text = "Explore os seus aplicativos e conteúdos multimédia."
                }
            }
        }
    }

    private fun startHighlightRotator() {
        if (realHighlights.isEmpty()) return
        highlightRunnable = object : Runnable {
            override fun run() {
                currentHighlightIndex = (currentHighlightIndex + 1) % realHighlights.size
                spotlightTitle.text = realHighlights[currentHighlightIndex].first
                spotlightDesc.text = realHighlights[currentHighlightIndex].second
                handler.postDelayed(this, 5000) // Troca a cada 5 segundos
            }
        }
        handler.post(highlightRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::highlightRunnable.isInitialized) {
            handler.removeCallbacks(highlightRunnable)
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
