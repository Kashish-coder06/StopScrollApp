package com.gcuh.stopscroll

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvLimit: TextView
    private lateinit var tvStatus: TextView
    private var currentLimit = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentLimit = AppPreferences.getDailyLimit(this)

        tvLimit = findViewById(R.id.tvLimitValue)
        tvStatus = findViewById(R.id.tvServiceStatus)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val btnSave = findViewById<Button>(R.id.btnSaveLimit)
        val btnPermissions = findViewById<Button>(R.id.btnGrantPermissions)
        val btnResetTest = findViewById<Button>(R.id.btnResetCount)

        // Direct App Launchers
        val btnLaunchInstagram = findViewById<Button>(R.id.btnLaunchInstagram)
        val btnLaunchYouTube = findViewById<Button>(R.id.btnLaunchYouTube)
        val btnLaunchTikTok = findViewById<Button>(R.id.btnLaunchTikTok)

        updateLimitDisplay()

        btnMinus.setOnClickListener {
            if (currentLimit > 5) {
                currentLimit -= 5
                updateLimitDisplay()
            }
        }

        btnPlus.setOnClickListener {
            currentLimit += 5
            updateLimitDisplay()
        }

        btnSave.setOnClickListener {
            AppPreferences.setDailyLimit(this, currentLimit)
            Toast.makeText(this, "Daily limit saved: $currentLimit reels", Toast.LENGTH_SHORT).show()
        }

        btnPermissions.setOnClickListener {
            checkAndRequestPermissions()
        }

        btnResetTest.setOnClickListener {
            AppPreferences.resetCount(this)
            Toast.makeText(this, "Counter reset to 0 for testing", Toast.LENGTH_SHORT).show()
        }

        // Direct Launch to Instagram Reels
        btnLaunchInstagram.setOnClickListener {
            launchDirectReelsApp(
                "com.instagram.android",
                "https://www.instagram.com/reels/"
            )
        }

        // Direct Launch to YouTube Shorts
        btnLaunchYouTube.setOnClickListener {
            launchDirectReelsApp(
                "com.google.android.youtube",
                "https://www.youtube.com/shorts"
            )
        }

        // Direct Launch to TikTok
        btnLaunchTikTok.setOnClickListener {
            launchDirectReelsApp(
                "com.zhiliaoapp.musically",
                "https://www.tiktok.com"
            )
        }
    }

    private fun launchDirectReelsApp(packageName: String, webFallbackUrl: String) {
        val pm = packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)
        } else {
            // If app is not installed, open directly in browser
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webFallbackUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(webIntent)
            Toast.makeText(this, "Opening in browser (App not installed)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
    }

    private fun updateLimitDisplay() {
        tvLimit.text = "$currentLimit / day"
    }

    private fun updatePermissionStatus() {
        val hasOverlay = Settings.canDrawOverlays(this)
        val overlayStatus = if (hasOverlay) "Overlay: Granted ✅" else "Overlay: Missing ❌"
        tvStatus.text = "Status: $overlayStatus\nAccessibility Service must be active to track reels."
    }

    private fun checkAndRequestPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
            Toast.makeText(this, "Please enable 'Display over other apps'", Toast.LENGTH_LONG).show()
            return
        }

        val accessibilityIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(accessibilityIntent)
        Toast.makeText(this, "Find 'StopScroll' and switch it ON", Toast.LENGTH_LONG).show()
    }
}
