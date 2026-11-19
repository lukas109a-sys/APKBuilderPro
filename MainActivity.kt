package com.lucas.apkbuilder

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.HorizontalScrollView
import android.widget.ImageView
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
    private lateinit var centerLabel: TextView

    private val neonColors = intArrayOf(
        Color.parseColor("#00FF66"), // Verde neon
        Color.parseColor("#00E5FF"), // Azul / Ciano neon
        Color.parseColor("#FF1E56"), // Vermelho neon
        Color.parseColor("#D500F9"), // Roxo / Magenta neon
        Color.parseColor("#FFEA00")  // Amarelo neon
    )

    private var currentIndex = 0
    private var isTiltLeft = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var animatorRunnable: Runnable
    private var borderAnimator: ValueAnimator? = null

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

        val searchIcon = createTopMiniBtn("🔍") {
            try {
                startActivity(Intent(Intent.ACTION_WEB_SEARCH))
            } catch (e: Exception) {
                openBrowser("https://www.google.com")
            }
        }
        val storageIcon = createTopMiniBtn("🧹") {
            try {
                startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Armazenamento", Toast.LENGTH_SHORT).show()
            }
        }
        val infoIcon = createTopMiniBtn("ℹ️") {
            try {
                startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "RED TV Launcher", Toast.LENGTH_SHORT).show()
            }
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateText = TextView(this).apply {
            text = "Quarta-feira | ${timeFormat.format(Date())}"
            textSize = 11f
            setTextColor(Color.parseColor("#CCCCCC"))
            setPadding(10, 0, 0, 0)
        }

        iconsHeaderLayout.addView(searchIcon)
        iconsHeaderLayout.addView(storageIcon)
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
            layoutParams = LinearLayout.LayoutParams(195, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins(0, 0, 8, 0)
            }
        }
        leftCol.addView(createModelButton("▶ YouTube") {
            openYouTube()
        })
        leftCol.addView(createModelButton("📶 Wi-Fi") {
            try {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } catch (e: Exception) {
                try {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                } catch (ex: Exception) {
                    Toast.makeText(this, "Não foi possível abrir o Wi-Fi", Toast.LENGTH_SHORT).show()
                }
            }
        })
        leftCol.addView(createModelButton("🌐 Navegador") {
            openBrowser("https://www.google.com")
        })
        middleBody.addView(leftCol)

        // Centro: Painel com Borda Neon Animada e "LAUNCHER TV" balançando
        spotlightCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = createAnimatedCardDrawable(neonColors[0])
            setPadding(18, 18, 18, 18)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2f).apply {
                setMargins(0, 0, 8, 0)
            }
        }

        centerLabel = TextView(this).apply {
            text = "LAUNCHER TV"
            textSize = 26f
            setTextColor(neonColors[0])
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        spotlightCard.addView(centerLabel)
        middleBody.addView(spotlightCard)

        // Coluna Direita
        val rightCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(195, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        rightCol.addView(createModelButton("⚙ Definições") {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao abrir definições", Toast.LENGTH_SHORT).show()
            }
        })
        rightCol.addView(createModelButton("🛒 Loja TV") {
            launchPackage("com.android.vending")
        })
        rightCol.addView(createModelButton("📱 Gerir Apps") {
            try {
                startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Grelha em baixo", Toast.LENGTH_SHORT).show()
            }
        })
        middleBody.addView(rightCol)

        rootLayout.addView(middleBody)

        // Parte Inferior: "Mes apps"
        val sectionTitle = TextView(this).apply {
            text = "Mes apps"
            textSize = 12f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 6)
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
                96
            )
        }
        rootLayout.addView(horizontalScroll)

        setContentView(rootLayout)
        loadInstalledAppsAsBanners()
        startNeonVibrationAndSwing()
        startRunningBorderEffect()
    }

    private fun createAnimatedCardDrawable(strokeColor: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(Color.parseColor("#11070A"), Color.parseColor("#050505"))
        ).apply {
            setStroke(4, strokeColor)
            cornerRadius = 14f
        }
    }

    private fun startRunningBorderEffect() {
        borderAnimator = ValueAnimator.ofFloat(0f, neonColors.size.toFloat()).apply {
            duration = 4500L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                val value = anim.animatedValue as Float
                val idxA = value.toInt() % neonColors.size
                val idxB = (idxA + 1) % neonColors.size
                val fraction = value - value.toInt()

                val blendedColor = blendColors(neonColors[idxA], neonColors[idxB], fraction)
                spotlightCard.background = createAnimatedCardDrawable(blendedColor)
                centerLabel.setTextColor(blendedColor)
            }
            start()
        }
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r = (Color.red(color1) * inverseRatio + Color.red(color2) * ratio).toInt()
        val g = (Color.green(color1) * inverseRatio + Color.green(color2) * ratio).toInt()
        val b = (Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio).toInt()
        return Color.rgb(r, g, b)
    }

    private fun startNeonVibrationAndSwing() {
        animatorRunnable = object : Runnable {
            override fun run() {
                currentIndex = (currentIndex + 1) % neonColors.size
                centerLabel.rotation = if (isTiltLeft) -2.5f else 2.5f
                isTiltLeft = !isTiltLeft
                handler.postDelayed(this, 2200)
            }
        }
        handler.postDelayed(animatorRunnable, 2200)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::animatorRunnable.isInitialized) {
            handler.removeCallbacks(animatorRunnable)
        }
        borderAnimator?.cancel()
    }

    private fun openYouTube() {
        val ytPackages = listOf(
            "com.google.android.youtube.tv",
            "com.google.android.youtube",
            "com.liskovsoft.smarttubetv.beta",
            "com.liskovsoft.smarttubetv"
        )
        for (pkg in ytPackages) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                startActivity(intent)
                return
            }
        }
        openBrowser("https://www.youtube.com")
    }

    private fun openBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Nenhum navegador encontrado", Toast.LENGTH_SHORT).show()
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
                    val appIcon = try {
                        pm.getApplicationIcon(pkg.packageName)
                    } catch (e: Exception) {
                        null
                    }

                    val cardBg = GradientDrawable().apply {
                        setColor(Color.parseColor("#161616"))
                        setStroke(1, Color.parseColor("#2E2E2E"))
                        cornerRadius = 10f
                    }

                    val appCard = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                        background = cardBg
                        setPadding(14, 8, 14, 8)
                        isFocusable = true
                        isClickable = true

                        setOnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                background = GradientDrawable().apply {
                                    setColor(Color.parseColor("#222222"))
                                    setStroke(2, Color.parseColor("#FF1E56"))
                                    cornerRadius = 10f
                                }
                            } else {
                                background = cardBg
                            }
                        }

                        setOnClickListener {
                            try {
                                startActivity(launchIntent)
                            } catch (e: Exception) {
                                Toast.makeText(this@MainActivity, "Erro ao abrir $appName", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    val params = LinearLayout.LayoutParams(210, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        setMargins(0, 0, 12, 0)
                    }
                    appCard.layoutParams = params

                    val iconView = ImageView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(52, 52).apply {
                            setMargins(0, 0, 10, 0)
                        }
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        if (appIcon != null) {
                            setImageDrawable(appIcon)
                        }
                    }

                    val nameText = TextView(this).apply {
                        text = appName
                        textSize = 12f
                        setTextColor(Color.WHITE)
                        typeface = Typeface.DEFAULT_BOLD
                        maxLines = 2
                    }

                    if (appIcon != null) {
                        appCard.addView(iconView)
                    }
                    appCard.addView(nameText)
                    horizontalAppsLayout.addView(appCard)
                }
            }
        } catch (e: Exception) {
            // Tratamento silencioso
        }
    }
}
