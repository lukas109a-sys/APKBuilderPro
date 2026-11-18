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

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#050505"))
            setPadding(35, 25, 35, 25)
        }

        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 20)
        }

        val logoText = TextView(this).apply {
            text = "RED TV"
            textSize = 22f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val wifiButton = createHeaderButton("📶 Wi-Fi") {
            try { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) } catch (e: Exception) {}
        }
        val settingsButton = createHeaderButton("⚙️ Definições") {
            try { startActivity(Intent(Settings.ACTION_SETTINGS)) } catch (e: Exception) {}
        }

        headerLayout.addView(logoText)
        headerLayout.addView(wifiButton)
        headerLayout.addView(settingsButton)
        rootLayout.addView(headerLayout)

        val bannerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#121212"))
                setStroke(2, Color.parseColor("#FF1E56"))
                cornerRadius = 16f
            }
            setPadding(30, 30, 30, 30)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                180
            ).apply { setMargins(0, 0, 0, 25) }
        }

        val bannerTitle = TextView(this).apply {
            text = "BEM-VINDO À SUA TV BOX"
            textSize = 18f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        val bannerSub = TextView(this).apply {
            text = "Navegue pelas aplicações abaixo com suporte total QUERY_ALL_PACKAGES."
            textSize = 12f
            setTextColor(Color.parseColor("#AAAAAA"))
        }
        bannerCard.addView(bannerTitle)
        bannerCard.addView(bannerSub)
        rootLayout.addView(bannerCard)

        val sectionTitle = TextView(this).apply {
            text = "APLICAÇÕES INSTALADAS"
            textSize = 13f
            setTextColor(Color.parseColor("#FF1E56"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 10)
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
                0,
                1f
            )
        }
        rootLayout.addView(horizontalScroll)

        statusText = TextView(this).apply {
            text = "Sistema pronto para TV Box."
            textSize = 11f
            setTextColor(Color.parseColor("#7F8C8D"))
            setPadding(0, 10, 0, 0)
        }
        rootLayout.addView(statusText)

        setContentView(rootLayout)
        loadInstalledAppsAsBanners()
    }

    private fun createHeaderButton(textStr: String, onClick: () -> Unit): TextView {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#101010"))
            setStroke(1, Color.parseColor("#FF1E56"))
            cornerRadius = 8f
        }
        return TextView(this).apply {
            text = textStr
            textSize = 13f
            setTextColor(Color.WHITE)
            background = bg
            setPadding(15, 8, 15, 8)
            setOnClickListener { onClick() }
            isFocusable = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(10, 0, 0, 0) }
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
                        setStroke(2, Color.parseColor("#262626"))
                        cornerRadius = 12f
                    }

                    val appCard = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER
                        background = cardBg
                        setPadding(20, 15, 20, 15)
                        isFocusable = true
                        isClickable = true
                        
                        setOnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                background = GradientDrawable().apply {
                                    setColor(Color.parseColor("#1E1E1E"))
                                    setStroke(3, Color.parseColor("#FF1E56"))
                                    cornerRadius = 12f
                                }
                            } else {
                                background = cardBg
                            }
                        }

                        setOnClickListener {
                            try { startActivity(launchIntent) } catch (e: Exception) {}
                        }
                    }

                    val params = LinearLayout.LayoutParams(160, 110).apply {
                        setMargins(0, 0, 15, 0)
                    }
                    appCard.layoutParams = params

                    val iconText = TextView(this).apply {
                        text = "📺"
                        textSize = 28f
                        gravity = Gravity.CENTER
                    }

                    val nameText = TextView(this).apply {
                        text = appName
                        textSize = 12f
                        setTextColor(Color.WHITE)
                        typeface = Typeface.DEFAULT_BOLD
                        gravity = Gravity.CENTER
                        maxLines = 1
                        setPadding(0, 5, 0, 0)
                    }

                    appCard.addView(iconText)
                    appCard.addView(nameText)
                    horizontalAppsLayout.addView(appCard)
                }
            }
            statusText.text = "Apps carregadas em modo paisagem horizontal."
        } catch (e: Exception) {
            statusText.text = "Erro ao carregar grelha."
        }
    }
}
