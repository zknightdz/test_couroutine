package com.gvn.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_webview.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        var url: String? = null

        if (intent.hasExtra(MainActivity.KEY_URL))
            url = intent?.getStringExtra(MainActivity.KEY_URL)


        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                if (Uri.parse(url).host == "newsapi.org") {
//                    // This is my web site, so do not override; let my WebView load the page
//                    return false
//                }
//                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
//                    startActivity(this)
//                }
                return false
            }
        }
        webView.loadUrl(url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }
}

/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
}
