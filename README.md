# HyperOS Icon Changer (Root)

A simple, lightweight Android utility for Xiaomi, Redmi, and POCO devices running **HyperOS** (or MIUI). It lets you customize every single app icon on your device with clean AOSP shapes, custom RGB Monet colors, and transparency — directly via Root without needing the stock Xiaomi Theme Manager (`com.android.thememanager`).

---

## ✨ Features

- **AOSP Icon Shapes:** Circle, Squircle, Rounded Square, Teardrop, Hexagon, Octagon, Pebble.
- **Interactive RGB Color Wheels:** Drag to pick any custom color for:
  - **Background Plate Color** (with live brightness slider and HEX code).
  - **Icon Glyph Color** (for themed monochrome icons).
- **Icon Styles:**
  - **Themed Monochrome:** Clean Material You / Monet-style monochrome glyphs.
  - **Original Stock Icons:** Keep original colorful app logos neatly framed inside the chosen shape plate.
- **Customization Sliders:**
  - **Plate Opacity:** From 0% (fully transparent / floating glyph) to 100% solid.
  - **Corner Radius:** Fine-tune the roundness of squircle and square shapes.
  - **Content Scale:** Adjust the size of the inner icon inside the plate.
- **Live Real-Time Preview:** Instantly see changes as you tweak colors and sliders.
- **100% App Coverage:** Automatically queries all installed applications on your phone so no app is left un-themed.
- **Clean Single-Layer Rendering:** No duplicate backgrounds, no double borders, and no overlapping colors.
- **Deep Cache Cleaning:** Automatically clears icon caches for both HyperOS Launcher (`com.miui.home`) and POCO Launcher (`com.mi.android.globallauncher`), restarting the home screen seamlessly.

---

## 📱 Requirements

- Android 8.0+ (Optimized for Android 14 / HyperOS 1.0 & 2.0).
- Root access (Magisk, KernelSU, or APatch) to write the customized theme directly to `/data/system/theme/icons`.

---

## 🚀 How to Use

1. Download and install **[HyperOS-Icon-Changer.apk](https://github.com/Xlufoi/hyperos-icon-changer-root/releases/latest/download/HyperOS-Icon-Changer.apk)**.
2. Open the app and configure your look:
   - Select your preferred **Icon Shape**.
   - Choose your **Icon Style** (Themed Monochrome or Original Stock).
   - Pick your **Plate Color** and **Glyph Color** using the RGB color wheels.
   - Adjust **Opacity**, **Corner Radius**, and **Content Scale**.
3. Check the **Live Preview** at the top.
4. Tap **"Apply to HyperOS (Root)"** and grant Root permissions.
5. Your home screen launcher will restart with your new icons applied!

---

## 🛠️ How to Build from Source (PC / Laptop)

You do **not** need Termux to build this project. You can build it on any computer (Windows, macOS, or Linux) using standard Android development tools.

### Option 1: Android Studio (Recommended)

1. Clone this repository:
   ```bash
   git clone https://github.com/Xlufoi/hyperos-icon-changer-root.git
   ```
2. Open **Android Studio**.
3. Select **File > Open...** and select the cloned `hyperos-icon-changer-root` folder.
4. Let Gradle sync dependencies.
5. Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. Once compiled, click **locate** to get your signed/debug APK file.

### Option 2: Command Line (Gradle)

If you have Java (JDK 17+) and the Android SDK installed:

```bash
git clone https://github.com/Xlufoi/hyperos-icon-changer-root.git
cd hyperos-icon-changer-root
gradle assembleRelease
```
The output APK will be located at `build/outputs/apk/release/`.

---

## 📄 License

Open-source under the Apache 2.0 License. Free for personal customization and community development.
