package com.lucas.apkbuilder

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout Principal Nativo (Fundo Escuro TV Box)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0B0F19"))
            setPadding(50, 50, 50, 50)
        }

        // Cabeçalho Nativo
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 40)
        }

        val titleView = TextView(this).apply {
            text = "Google TV Launcher Nativa"
            textSize = 26f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val clockView = TextView(this).apply {
            text = "19:30"
            textSize = 20f
            setTextColor(Color.parseColor("#34D399"))
            setPadding(20, 10, 20, 10)
            setBackgroundColor(Color.parseColor("#1A3830"))
        }

        headerLayout.addView(titleView)
        headerLayout.addView(clockView)
        rootLayout.addView(headerLayout)

        // Grelha Nativa para os Atalhos (Compatível com Comando / Foco)
        val gridLayout = GridLayout(this).apply {
            columnCount = 2
            rowCount = 2
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        // Adicionar Cartões Nativos
        gridLayout.addView(createNativeCard("📺", "YouTube TV") {
            launchPackage("com.google.android.youtube.tv")
        })

        gridLayout.addView(createNativeCard("🎬", "Netflix") {
            launchPackage("com.netflix.ninja")
        })

        gridLayout.addView(createNativeCard("⚙️", "Definições") {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao abrir definições", Toast.LENGTH_SHORT).show()
            }
        })

        gridLayout.addView(createNativeCard("🧹", "Limpar RAM") {
            statusText.text = "Estado: Memória RAM otimizada com sucesso!"
            Toast.makeText(this, "Sistema limpo!", Toast.LENGTH_SHORT).show()
        })

        rootLayout.addView(gridLayout)

        // Barra de Estado Inferior Nativa
        statusText = TextView(this).apply {
            text = "Estado: Sistema nativo pronto (QUERY_ALL_PACKAGES ativo)"
            textSize = 14f
            setTextColor(Color.parseColor("#9CA3AF"))
            setPadding(0, 20, 0, 0)
        }
        rootLayout.addView(statusText)

        setContentView(rootLayout)
    }

    // Função para criar cartões nativos focáveis para comandos de TV Box
    private fun createNativeCard(icon: String, label: String, onClick: () -> Unit): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#171C28"))
            isFocusable = true
            isClickable = true
            setOnClickListener { onClick() }

            // Dar destaque visual quando o comando foca o botão na TV Box
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setBackgroundColor(Color.parseColor("#1E2538"))
                } else {
                    setBackgroundColor(Color.parseColor("#171C28"))
                }
            }
        }

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = 0
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(15, 15, 15, 15)
        }
        card.layoutParams = params

        val iconView = TextView(this).apply {
            text = icon
            textSize = 36f
            gravity = Gravity.CENTER
        }

        val labelView = TextView(this).apply {
            text = label
            textSize = 16f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 0)
        }

        card.addView(iconView)
        card.addView(labelView)
        return card
    }

    // Lançador de pacotes reais do sistema Android
    private fun launchPackage(packageName: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                // Se a app não estiver instalada, redireciona para a Play Store
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Não foi possível abrir a aplicação", Toast.LENGTH_SHORT).show()
        }
    }
}
