# StopScroll: How to Build & Run Full Working APK on Android

All complete production-grade source code for **StopScroll** is placed in:
`C:\Users\DELL\Downloads\New folder\StopScrollApp`

---

## 3 Quick Steps to Install & Test on Your Mobile Phone

### Step 1: Open the Project in Android Studio
1. Open **Android Studio** on your PC.
2. Click **File -> Open...**
3. Select this folder:
   ```text
   C:\Users\DELL\Downloads\New folder\StopScrollApp
   ```
4. Let Gradle sync dependencies (takes about 1-2 minutes).

---

### Step 2: Connect Your Android Phone
1. On your Android phone, enable **Developer Options**:
   - Go to **Settings -> About Phone -> Tap "Build Number" 7 times**.
2. Go to **Settings -> System / Additional Settings -> Developer Options**.
3. Turn on **USB Debugging** (and "Install via USB" if on Xiaomi/Realme/Oppo).
4. Connect phone via USB cable to PC, and allow the prompt *"Allow USB debugging?"*.

---

### Step 3: Run / Build the APK
* **Direct Run:** In Android Studio, select your phone in the top bar and click the green **Play (Run)** button (`Shift + F10`).
* **Generate APK:** Go to **Build -> Build Bundle(s) / APK(s) -> Build APK(s)**. Transfer the `.apk` file to your phone and install.

---

## App Permissions to Activate on Phone
After installing the app:
1. **Draw Over Other Apps:** Tap "Setup Permissions" inside the app, find **StopScroll** and turn toggle **ON**.
2. **Accessibility Service:** Go to phone **Settings -> Accessibility -> Downloaded Apps / StopScroll -> Turn ON**.

Once both are on:
* Open **Instagram** and tap the **Reels** tab, or open **YouTube Shorts**.
* The floating pink counter `1/30` will appear automatically!
* Once your limit is hit, it automatically redirects you to the home screen and displays the **Time's Up** lock screen!
