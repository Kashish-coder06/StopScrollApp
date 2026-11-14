package com.gcuh.stopscroll

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppPreferences {
    private const val PREF_NAME = "stopscroll_prefs"
    private const val KEY_DAILY_LIMIT = "key_daily_limit"
    private const val KEY_REELS_COUNT = "key_reels_count"
    private const val KEY_LAST_DATE = "key_last_date"
    private const val KEY_MONITOR_INSTAGRAM = "key_mon_ig"
    private const val KEY_MONITOR_YOUTUBE = "key_mon_yt"
    private const val KEY_MONITOR_TIKTOK = "key_mon_tt"
    private const val KEY_PIN = "key_security_pin"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getDailyLimit(context: Context): Int {
        return getPrefs(context).getInt(KEY_DAILY_LIMIT, 30)
    }

    fun setDailyLimit(context: Context, limit: Int) {
        getPrefs(context).edit().putInt(KEY_DAILY_LIMIT, limit).apply()
    }

    fun getReelsCount(context: Context): Int {
        checkAndResetDaily(context)
        return getPrefs(context).getInt(KEY_REELS_COUNT, 0)
    }

    fun incrementReelCount(context: Context): Int {
        checkAndResetDaily(context)
        val current = getReelsCount(context) + 1
        getPrefs(context).edit().putInt(KEY_REELS_COUNT, current).apply()
        return current
    }

    fun resetCount(context: Context) {
        getPrefs(context).edit().putInt(KEY_REELS_COUNT, 0).apply()
    }

    fun setPin(context: Context, pin: String) {
        getPrefs(context).edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(context: Context): String? {
        return getPrefs(context).getString(KEY_PIN, null)
    }

    private fun checkAndResetDaily(context: Context) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = getPrefs(context).getString(KEY_LAST_DATE, "")
        if (today != lastDate) {
            getPrefs(context).edit()
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_REELS_COUNT, 0)
                .apply()
        }
    }
}
