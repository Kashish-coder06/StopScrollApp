package com.gcuh.stopscroll

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private var isShowing = false
    private var layoutParams: WindowManager.LayoutParams? = null

    // Palette of vibrant colors that cycle dynamically with each reel scroll
    private val cycleColors = listOf(
        "#4A90E2", // Soft Sky Blue (matches mascot)
        "#50E3C2", // Mint Teal
        "#7ED321", // Fresh Green
        "#F5A623", // Warm Orange
        "#FF6B8B", // Rose Pink
        "#BD10E0", // Vibrant Violet
        "#E91E63", // Deep Magenta
        "#D0021B"  // Alert Red (when limit is close)
    )

    fun showOrUpdateOverlay(currentCount: Int, maxLimit: Int) {
        if (!android.provider.Settings.canDrawOverlays(context)) return

        if (overlayView == null) {
            val inflater = LayoutInflater.from(context)
            overlayView = inflater.inflate(R.layout.overlay_floating_counter, null)

            val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = 80 // Positioned prominently near the top
            }

            setupDragListener(overlayView!!, layoutParams!!)

            try {
                windowManager.addView(overlayView, layoutParams)
                isShowing = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 1. Calculate color based on scroll count
        val colorHex = if (currentCount >= maxLimit * 0.85) {
            "#D0021B" // Red alert when limit is 85%+ reached
        } else {
            cycleColors[currentCount % (cycleColors.size - 1)]
        }
        val dynamicColor = Color.parseColor(colorHex)

        // 2. Update pill container border & background
        val container = overlayView?.findViewById<View>(R.id.llOverlayContainer)
        val bgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 48f
            setColor(Color.WHITE)
            setStroke(5, dynamicColor) // Border dynamically changes color on each scroll!
        }
        container?.background = bgDrawable

        // 3. Update counter text & color
        val tvCounter = overlayView?.findViewById<TextView>(R.id.tvCounterText)
        tvCounter?.text = "$currentCount / $maxLimit"
        tvCounter?.setTextColor(dynamicColor)

        // 4. Update status indicator
        val tvStatus = overlayView?.findViewById<TextView>(R.id.tvCounterStatus)
        if (currentCount >= maxLimit * 0.85) {
            tvStatus?.text = "⚠️ Limit Almost Hit!"
            tvStatus?.setTextColor(Color.parseColor("#D0021B"))
        } else {
            tvStatus?.text = "Reel #$currentCount"
            tvStatus?.setTextColor(Color.parseColor("#555555"))
        }

        // 5. Mascot subtle bounce animation on increment
        val ivMascot = overlayView?.findViewById<ImageView>(R.id.ivMascot)
        ivMascot?.animate()?.scaleX(1.25f)?.scaleY(1.25f)?.setDuration(150)?.withEndAction {
            ivMascot.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
        }?.start()
    }

    fun hideOverlay() {
        if (isShowing && overlayView != null) {
            try {
                windowManager.removeView(overlayView)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                overlayView = null
                isShowing = false
            }
        }
    }

    private fun setupDragListener(view: View, params: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        view.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = motionEvent.rawX
                    initialTouchY = motionEvent.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (motionEvent.rawX - initialTouchX).toInt()
                    params.y = initialY + (motionEvent.rawY - initialTouchY).toInt()
                    try {
                        windowManager.updateViewLayout(view, params)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    true
                }
                else -> false
            }
        }
    }
}
