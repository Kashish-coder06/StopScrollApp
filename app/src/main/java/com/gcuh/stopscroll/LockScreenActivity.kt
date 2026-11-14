package com.gcuh.stopscroll

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LockScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Show on top of keyguard / lock screen if device is locked
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        setContentView(R.layout.activity_lock_screen)

        val maxLimit = AppPreferences.getDailyLimit(this)
        val tvSub = findViewById<TextView>(R.id.tvLockSubtitle)
        tvSub.text = "You've reached your daily limit of $maxLimit reels.\nTake a break and regain your focus!"

        val btnGoHome = findViewById<Button>(R.id.btnGoHome)
        btnGoHome.setOnClickListener {
            exitToHome()
        }
    }

    private fun exitToHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(homeIntent)
        finishAffinity()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent bypassing lock screen via back button
        exitToHome()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // If user tries to minimize or switch apps, keep them locked out of reels
        exitToHome()
    }
}
