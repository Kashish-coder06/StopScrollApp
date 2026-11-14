# StopScroll Prototype Android App

This is the functional prototype for **StopScroll (Reel Scroll Limiter)** developed for the Final Year Project (GCUH).

## Features Implemented in this Prototype

1. **Dashboard & Limit Setter UI (`MainActivity.kt` & `activity_main.xml`)**:
   - Styled to match the pink-theme design mockup.
   - Increment/Decrement daily limit buttons (`+`, `-`) with instant local persistence.
   - One-click shortcuts to grant **Draw Over Other Apps** and **Accessibility Service** permissions.
   - Test reset button to reset reel counter to 0 during testing.

2. **Floating Reel Counter (`OverlayManager.kt` & `overlay_floating_counter.xml`)**:
   - Uses Android `WindowManager` with `TYPE_APPLICATION_OVERLAY`.
   - Floats above Instagram Reels, YouTube Shorts, and TikTok.
   - Shows live count: `12 / 30`.
   - **Draggable:** Supports dragging with touch to move it anywhere on the screen.

3. **Scroll Interceptor (`ReelAccessibilityService.kt`)**:
   - Configured with `accessibility_service_config.xml`.
   - Detects `TYPE_VIEW_SCROLLED` in target social packages (`com.instagram.android`, `com.google.android.youtube`, etc.).
   - Implements a **1200ms debounce filter** to prevent duplicate counts on single swipes.
   - Automatically checks limit: when `count >= limit`, hides the floating counter and launches the lock screen.

4. **Lock Screen Enforcer (`LockScreenActivity.kt` & `activity_lock_screen.xml`)**:
   - Triggers `performGlobalAction(GLOBAL_ACTION_HOME)` to immediately exit the reel app.
   - Full-screen blocking alert displaying *"Time's Up! You've reached your daily limit."*
   - Intercepts back-button actions to prevent easy bypassing.

## How to Open and Run

1. Open **Android Studio**.
2. Select **Open** and choose the directory:  
   `c:\Users\DELL\Downloads\New folder\StopScrollApp`
3. Connect an Android phone (or start an emulator with Android 8.0+ / API 26+).
4. Click **Run (`Shift + F10`)**.
5. Inside the app, tap **Setup Permissions**:
   - Enable **Display over other apps** (System Overlay).
   - Enable **StopScroll** inside Accessibility Settings.
6. Open Instagram Reels or YouTube Shorts and watch the counter appear and increment on each scroll!
