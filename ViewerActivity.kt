package com.my.epubreader

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class ViewerActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var settingsPanel: LinearLayout
    private var fontSize = 16
    private var lineHeight = 1.6f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = android.widget.RelativeLayout(this)
        
        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    val path = intent.getStringExtra("EPUB_PATH") ?: ""
                    evaluateJavascript("openEpub('$path');", null)
                }
            }
        }
        mainLayout.addView(webView, android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.MATCH_PARENT)

        settingsPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.parseColor("#EEEEEE"))
            setPadding(32, 32, 32, 32)
            visibility = View.GONE
        }
        
        val lp = android.widget.RelativeLayout.LayoutParams(
            android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
            android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply { addRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM) }
        mainLayout.addView(settingsPanel, lp)

        setupSettingsUI()
        setContentView(mainLayout)

        webView.setOnLongClickListener {
            settingsPanel.visibility = if (settingsPanel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            true
        }
    }

    private fun setupSettingsUI() {
        val sbFontSize = SeekBar(this).apply {
            max = 30
            progress = 4
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) {
                    fontSize = 12 + p
                    applyStyle()
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }
        settingsPanel.addView(sbFontSize)

        val sbLineHeight = SeekBar(this).apply {
            max = 20
            progress = 6
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) {
                    lineHeight = 1.0f + (p / 10f)
                    applyStyle()
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }
        settingsPanel.addView(sbLineHeight)
    }

    private fun applyStyle() {
        webView.evaluateJavascript("updateStyle($fontSize, $lineHeight);", null)
    }
}