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

    // Cores neon para o ciclo do texto e da borda animada
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
        val msgIcon = createTopMiniBtn("🧹") {
            try {
                startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Armazenamento", Toast.LENGTH_SHORT).show()
            }
        }
        val infoIcon = createTopMiniBtn("ℹ️") {
            try {
                startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS))
            }
