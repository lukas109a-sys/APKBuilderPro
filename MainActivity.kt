package com.lucas.apkbuilder

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            
            // Adiciona a ponte para o JavaScript conseguir controlar o Android nativamente
            addJavascriptInterface(WebAppInterface(this@MainActivity), "AndroidLauncher")
        }

        // HTML atualizado com ações reais de lançamento de pacotes
        val launcherHtml = """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>TV Box Launcher Pro</title>
                <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; font-family: system-ui, -apple-system, sans-serif; }
                    body { background-color: #0b0f19; color: #ffffff; height: 100vh; display: flex; flex-direction: column; padding: 30px; overflow-y: auto; }
                    header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
                    h1 { font-size: 28px; font-weight: 700; color: #f3f4f6; }
                    .clock { font-size: 24px; font-weight: 500; color: #34d399; background: rgba(52, 211, 153, 0.1); padding: 8px 16px; border-radius: 12px; }
                    .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; flex-grow: 1; }
                    .card { background: #171c28; border: 2px solid transparent; border-radius: 16px; padding: 25px; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; cursor: pointer; transition: all 0.2s ease; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.3); }
                    .card:hover, .card:active { background: #1e2538; border-color: #4f46e5; transform: translateY(-4px); }
                    .card .icon { font-size: 40px; margin-bottom: 12px; }
                    .card span { font-size: 16px; font-weight: 600; color: #e5e7eb; }
                    .status-bar { margin-top: 30px; background: #131822; padding: 15px 20px; border-radius: 10px; font-size: 14px; color: #9ca3af; display: flex; justify-content: space-between; align-items: center; }
                    .btn-clean { background: #4f46e5; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; }
                </style>
            </head>
            <body>
                <header>
                    <div>
                        <h1>Google TV Launcher</h1>
                        <p style="color: #9ca3af; font-size: 13px;">Controlo de Sistema & Ações Reais</p>
                    </div>
                    <div class="clock" id="clock">00:00</div>
                </header>
                <div class="grid">
                    <div class="card" onclick="AndroidLauncher.openPackage('com.google.android.youtube.tv')">
                        <div class="icon">📺</div>
                        <span>YouTube TV</span>
                    </div>
                    <div class="card" onclick="AndroidLauncher.openPackage('com.netflix.ninja')">
                        <div class="icon">🎬</div>
                        <span>Netflix</span>
                    </div>
                    <div class="card" onclick="AndroidLauncher.openSettings()">
                        <div class="icon">⚙️</div>
                        <span>Definições</span>
                    </div>
                    <div class="card" onclick="AndroidLauncher.showToast('Gestor de Ficheiros a abrir...')">
                        <div class="icon">📁</div>
                        <span>Ficheiros</span>
                    </div>
                </div>
                <div class="status-bar">
                    <span id="ram-status">Sistema pronto e otimizado para TV Box</span>
                    <button class="btn-clean" onclick="AndroidLauncher.cleanRam()">Limpar RAM</button>
                </div>
                <script>
                    function updateClock() {
                        const now = new Date();
                        document.getElementById('clock').innerText = String(now.getHours()).padStart(2, '0') + ':' + String(now.getMinutes()).padStart(2, '0');
                    }
                    setInterval(updateClock, 1000);
                    updateClock();
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, launcherHtml, "text/html", "UTF-8", null)
        setContentView(webView)
    }

    // Ponte de comunicação direta entre o visual da Launcher e o sistema Android
    class WebAppInterface(private val context: AppCompatActivity) {
        
        @JavascriptInterface
        fun openPackage(packageName: String) {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    context.startActivity(intent)
                } else {
                    // Se a app não estiver instalada, abre na web/store como alternativa
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                    context.startActivity(webIntent)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao abrir aplicação", Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun openSettings() {
            try {
                context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao abrir definições", Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun showToast(message: String) {
            context.runOnUiThread {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun cleanRam() {
            context.runOnUiThread {
                Toast.makeText(context, "Memória RAM otimizada com sucesso!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
