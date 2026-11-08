#!/data/data/com.termux/files/usr/bin/bash
set -e
cd ~/HyperOSIconChanger
ANDROID_JAR=~/android-sdk/android.jar

echo "1. Generating R.java..."
aapt package -f -m \
    -J src \
    -M AndroidManifest.xml \
    -S res \
    -I $ANDROID_JAR

echo "2. Compiling Java sources..."
rm -rf bin/classes
mkdir -p bin/classes
javac -cp $ANDROID_JAR \
    -d bin/classes \
    $(find src -name "*.java")

echo "3. Converting to DEX..."
d8 --output bin/ $(find bin/classes -name "*.class")

echo "4. Packaging APK with assets..."
aapt package -f \
    -M AndroidManifest.xml \
    -S res \
    -A assets \
    -I $ANDROID_JAR \
    -F bin/unsigned.apk

cd bin
aapt add unsigned.apk classes.dex
cd ..

echo "5. Signing APK..."
mkdir -p /sdcard/Download/BlueOS_HyperOS
OUT_APK=/sdcard/Download/BlueOS_HyperOS/HyperOS-Icon-Changer.apk
apksigner sign --ks ~/debug.keystore --ks-pass pass:android --out "$OUT_APK" bin/unsigned.apk
cp -f "$OUT_APK" /sdcard/Download/BlueOS_HyperOS/BlueOS-Glass-Icons.apk

echo "Done! Output: $OUT_APK"
