package com.example.login

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.BufferedReader
import java.io.InputStreamReader

class PoliticaPrivacidadeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_politica_privacidade)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_politica_privacidade)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.menu_politica_privacidade)

        // Encontra o WebView no layout
        val webViewPolitica: WebView = findViewById(R.id.webViewPolitica)

        // Configurações para o WebView (melhora a visualização)
        webViewPolitica.settings.javaScriptEnabled = true // Habilita JavaScript se necessário
        webViewPolitica.settings.loadWithOverviewMode = true
        webViewPolitica.settings.useWideViewPort = true
        webViewPolitica.settings.builtInZoomControls = false // Desabilita zoom nativo

        // Carrega o HTML do arquivo 'politica_privacidade.html' na pasta assets
        loadHtmlFromAsset("politica_privacidade.html", webViewPolitica)
    }

    // Função para carregar HTML de um arquivo na pasta assets
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
            // "file:///android_asset/" é o caminho padrão para a pasta assets
            webView.loadDataWithBaseURL("file:///android_asset/", stringBuilder.toString(), "text/html", "utf-8", null)

        } catch (e: Exception) {
            e.printStackTrace()
            // Em caso de erro ao carregar o arquivo, mostra uma mensagem genérica no WebView
            webView.loadData("<html><body><h1>Erro ao carregar conteúdo da Política de Privacidade.</h1></body></html>", "text/html", "utf-8")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Quando o botão de voltar na Toolbar é pressionado, finaliza a Activity
        finish()
        return true
    }
}