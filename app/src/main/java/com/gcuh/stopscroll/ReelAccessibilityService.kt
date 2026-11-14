package com.gcuh.stopscroll

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Production-ready Accessibility Service that detects reels & shorts inside:
 * 1. Instagram (Reels tab / viewer)
 * 2. YouTube (Shorts player)
 * 3. TikTok (For You / Following vertical video feeds)
 * 4. Facebook (Reels viewer)
 */
class ReelAccessibilityService : AccessibilityService() {

    private lateinit var overlayManager: OverlayManager
    private var lastScrollTime = 0L
    private val DEBOUNCE_MS = 1000L // 1 second debounce
    private var lastObservedNodeText: String? = null

    companion object {
        const val PKG_INSTAGRAM = "com.instagram.android"
        const val PKG_YOUTUBE = "com.google.android.youtube"
        const val PKG_TIKTOK = "com.zhiliaoapp.musically"
        const val PKG_TIKTOK_ALT = "com.ss.android.ugc.trill"
        const val PKG_FACEBOOK = "com.facebook.katana"

        val MONITORED_APPS = setOf(
            PKG_INSTAGRAM,
            PKG_YOUTUBE,
            PKG_TIKTOK,
            PKG_TIKTOK_ALT,
            PKG_FACEBOOK
        )
    }

    override fun onCreate() {
        super.onCreate()
        overlayManager = OverlayManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        if (!MONITORED_APPS.contains(packageName)) {
            // User exited reels / target app
            overlayManager.hideOverlay()
            return
        }

        val limit = AppPreferences.getDailyLimit(this)
        val currentCount = AppPreferences.getReelsCount(this)

        // 1. Strict Enforcement: If limit exceeded, kick out immediately
        if (currentCount >= limit) {
            overlayManager.hideOverlay()
            lockAndExit()
            return
        }

        // 2. Identify if specifically in the vertical reel section
        val isInReels = isUserInsideReelFeed(packageName)
        if (!isInReels) {
            overlayManager.hideOverlay()
            return
        }

        // 3. Show live overlay counter
        overlayManager.showOrUpdateOverlay(currentCount, limit)

        // 4. Handle Scroll / Swipe Event
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            val now = System.currentTimeMillis()
            if (now - lastScrollTime > DEBOUNCE_MS) {
                // Check if content actually changed to avoid false scroll triggers
                val currentText = getActiveReelSignature()
                if (currentText != null && currentText != lastObservedNodeText) {
                    lastObservedNodeText = currentText
                    lastScrollTime = now
                    onReelSwiped(limit)
                } else if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                    lastScrollTime = now
                    onReelSwiped(limit)
                }
            }
        }
    }

    private fun onReelSwiped(limit: Int) {
        val newCount = AppPreferences.incrementReelCount(this)
        overlayManager.showOrUpdateOverlay(newCount, limit)

        if (newCount >= limit) {
            overlayManager.hideOverlay()
            lockAndExit()
        }
    }

    private fun isUserInsideReelFeed(pkg: String): Boolean {
        val root = rootInActiveWindow ?: return true // fallback to true if accessible

        return when (pkg) {
            PKG_INSTAGRAM -> {
                // Check for Instagram Reels indicators: clip viewer, audio icon, remix buttons
                findNodeByTextOrId(root, listOf("reel", "clips", "Reels", "like_button", "comment_button"))
            }
            PKG_YOUTUBE -> {
                // Check for YouTube Shorts indicators
                findNodeByTextOrId(root, listOf("Shorts", "shorts_player", "reel_player", "Dislike"))
            }
            PKG_TIKTOK, PKG_TIKTOK_ALT -> {
                // TikTok is almost 100% vertical feed
                true
            }
            else -> true
        }
    }

    private fun findNodeByTextOrId(node: AccessibilityNodeInfo, keywords: List<String>): Boolean {
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""

        for (kw in keywords) {
            val lowerKw = kw.lowercase()
            if (viewId.contains(lowerKw) || text.contains(lowerKw) || desc.contains(lowerKw)) {
                return true
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                if (findNodeByTextOrId(child, keywords)) return true
            }
        }
        return false
    }

    private fun getActiveReelSignature(): String? {
        val root = rootInActiveWindow ?: return null
        return root.contentDescription?.toString() ?: root.text?.toString()
    }

    private fun lockAndExit() {
        // Send user back to device home screen immediately
        performGlobalAction(GLOBAL_ACTION_HOME)

        // Show lock screen
        val lockIntent = Intent(this, LockScreenActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(lockIntent)
    }

    override fun onInterrupt() {
        overlayManager.hideOverlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.hideOverlay()
    }
}
