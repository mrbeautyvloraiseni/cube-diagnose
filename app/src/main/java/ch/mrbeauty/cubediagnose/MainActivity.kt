package ch.mrbeauty.cubediagnose

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import kotlin.math.roundToInt

class MainActivity : Activity() {

    private lateinit var resultView: TextView
    private lateinit var webView: WebView
    private var webResult: String = "WebView: wird gemessen…"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
            setPadding(18, 18, 18, 18)
        }

        val title = TextView(this).apply {
            text = "MR Beauty · Cube Diagnose"
            setTextColor(Color.rgb(200, 207, 4))
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 12)
        }
        root.addView(title, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        resultView = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = android.graphics.Typeface.MONOSPACE
            setLineSpacing(0f, 1.08f)
        }
        content.addView(resultView)

        val hint = TextView(this).apply {
            text = "\nBitte fotografiere diesen ganzen Bildschirm und schicke mir das Bild."
            setTextColor(Color.LTGRAY)
            textSize = 13f
        }
        content.addView(hint)

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            setBackgroundColor(Color.BLACK)
            visibility = View.GONE
            addJavascriptInterface(JsBridge(), "Diag")
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.evaluateJavascript("""
                        (function(){
                          var r = {
                            innerWidth: window.innerWidth,
                            innerHeight: window.innerHeight,
                            outerWidth: window.outerWidth,
                            outerHeight: window.outerHeight,
                            devicePixelRatio: window.devicePixelRatio,
                            screenWidth: screen.width,
                            screenHeight: screen.height,
                            availWidth: screen.availWidth,
                            availHeight: screen.availHeight,
                            visualViewportWidth: window.visualViewport ? window.visualViewport.width : null,
                            visualViewportHeight: window.visualViewport ? window.visualViewport.height : null
                          };
                          Diag.report(JSON.stringify(r));
                        })();
                    """.trimIndent(), null)
                }
            }
        }
        content.addView(webView, LinearLayout.LayoutParams(1, 1))

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        val refresh = Button(this).apply {
            text = "Neu messen"
            textSize = 15f
            setOnClickListener { measureAll() }
        }
        root.addView(refresh, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        setContentView(root)
        root.post { measureAll() }
    }

    inner class JsBridge {
        @JavascriptInterface
        fun report(value: String) {
            runOnUiThread {
                webResult = value
                renderResults()
            }
        }
    }

    private fun measureAll() {
        webResult = "WebView: wird gemessen…"
        renderResults()
        webView.loadDataWithBaseURL(
            null,
            "<!doctype html><html><head><meta name='viewport' content='width=device-width,initial-scale=1.0'></head><body></body></html>",
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun renderResults() {
        val dm: DisplayMetrics = resources.displayMetrics
        val config = resources.configuration
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val realMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(realMetrics)

        val windowMetricsText = if (Build.VERSION.SDK_INT >= 30) {
            val b = windowManager.currentWindowMetrics.bounds
            "${b.width()} × ${b.height()} px"
        } else {
            "nicht verfügbar"
        }

        val densityBucket = when {
            dm.densityDpi >= 640 -> "xxxhdpi"
            dm.densityDpi >= 480 -> "xxhdpi"
            dm.densityDpi >= 320 -> "xhdpi"
            dm.densityDpi >= 240 -> "hdpi"
            else -> "mdpi"
        }

        val dpW = (dm.widthPixels / dm.density).roundToInt()
        val dpH = (dm.heightPixels / dm.density).roundToInt()

        val text = buildString {
            appendLine("ANDROID / DISPLAY")
            appendLine("----------------------------")
            appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            appendLine("Hersteller: ${Build.MANUFACTURER}")
            appendLine("Modell: ${Build.MODEL}")
            appendLine()
            appendLine("App-DisplayMetrics:")
            appendLine("widthPixels     = ${dm.widthPixels}")
            appendLine("heightPixels    = ${dm.heightPixels}")
            appendLine("density         = ${"%.3f".format(dm.density)}")
            appendLine("densityDpi      = ${dm.densityDpi}")
            appendLine("scaledDensity   = ${"%.3f".format(dm.scaledDensity)}")
            appendLine("xdpi            = ${"%.2f".format(dm.xdpi)}")
            appendLine("ydpi            = ${"%.2f".format(dm.ydpi)}")
            appendLine("Dichte-Klasse   = $densityBucket")
            appendLine("ca. dp-Grösse   = $dpW × $dpH dp")
            appendLine()
            appendLine("Echter physischer Screen:")
            appendLine("real width      = ${realMetrics.widthPixels} px")
            appendLine("real height     = ${realMetrics.heightPixels} px")
            appendLine("WindowMetrics   = $windowMetricsText")
            appendLine()
            appendLine("LAUNCHER / APP-ICON")
            appendLine("----------------------------")
            appendLine("launcherLargeIconSize    = ${am.launcherLargeIconSize} px")
            appendLine("launcherLargeIconDensity = ${am.launcherLargeIconDensity} dpi")
            appendLine()
            appendLine("WEBVIEW / KASSEN-HTML")
            appendLine("----------------------------")
            appendLine(webResult)
        }

        resultView.text = text
    }
}
