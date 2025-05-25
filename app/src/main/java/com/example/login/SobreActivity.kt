package com.example.login

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.BufferedReader
import java.io.InputStreamReader

class SobreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_sobre)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.menu_sobre)

        val webViewSobre: WebView = findViewById(R.id.webViewSobre)

        // Configurações básicas para o WebView
        webViewSobre.settings.javaScriptEnabled = true
        webViewSobre.settings.loadWithOverviewMode = true
        webViewSobre.settings.useWideViewPort = true
        webViewSobre.settings.builtInZoomControls = false

        // Carrega o HTML do arquivo 'sobre.html' na pasta assets
        loadHtmlFromAsset("sobre.html", webViewSobre)
    }

    // Função auxiliar para carregar o conteúdo HTML de um arquivo na pasta assets
    private fun loadHtmlFromAsset(fileName: String, webView: WebView) {
        try {
            val inputStream = assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            reader.close()
            inputStream.close()

            // Carrega o conteúdo HTML no WebView usando a URL de assets
            webView.loadDataWithBaseURL("file:///android_asset/", stringBuilder.toString(), "text/html", "utf-8", null)

        } catch (e: Exception) {
            e.printStackTrace()
            // Em caso de erro, exibe uma mensagem no próprio WebView
            webView.loadData("<html><body><h1>Erro ao carregar conteúdo.</h1></body></html>", "text/html", "utf-8")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}