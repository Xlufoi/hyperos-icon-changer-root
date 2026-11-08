package org.blueos.icons;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class IconThemeEngine {

    public static final String[] SHAPES = {
            "Круг (Circle — HyperOS)",
            "Сквиркл (Squircle)",
            "Скруглённый квадрат (Rounded Square)",
            "Капля (Teardrop)",
            "Шестиугольник (Hexagon)",
            "Восьмиугольник (Octagon)",
            "Цилиндр (Pebble)",
            "Квадрат (Square)"
    };

    public static final String[] THEMES = {
            "Monet (Динамический под обои)",
            "Черный (Pure Black AMOLED)"
    };

    public static final String[] STYLES = {
            "Оригинальные цвета (Стоковые логотипы)",
            "Themed Monet (AOSP Монохром)",
            "Themed Черно-белый (Pure Black & White)"
    };

    public static final String[] SHAPE_SVG_PATHS = {
            "M 50 0 A 50 50 0 0 1 100 50 A 50 50 0 0 1 50 100 A 50 50 0 0 1 0 50 A 50 50 0 0 1 50 0 Z",
            "M 50,0 C 10,0 0,10 0,50 C 0,90 10,100 50,100 C 90,100 100,90 100,50 C 100,10 90,0 50,0 Z",
            "M 50,0 L 75,0 C 90,0 100,10 100,25 L 100,75 C 100,90 90,100 75,100 L 25,100 C 10,100 0,90 0,75 L 0,25 C 0,10 10,0 25,0 Z",
            "M 50,0 C 10,0 0,10 0,50 C 0,90 10,100 50,100 C 90,100 100,90 100,50 L 100,0 Z",
            "M 50 0 L 93.3 25 L 93.3 75 L 50 100 L 6.7 75 L 6.7 25 Z",
            "M 29.3,0 L 70.7,0 L 100,29.3 L 100,70.7 L 70.7,100 L 29.3,100 L 0,70.7 L 0,29.3 Z",
            "M 50,0 C 20,0 0,15 0,50 C 0,85 20,100 50,100 C 80,100 100,85 100,50 C 100,15 80,0 50,0 Z",
            "M 0 0 H 100 V 100 H 0 Z"
    };

    public static Path createShapePath(int shapeIndex, float width, float height) {
        Path path = new Path();
        float xc = width / 2f;
        float yc = height / 2f;
        float r = Math.min(width, height) * 0.45f;
        RectF bounds = new RectF(xc - r, yc - r, xc + r, yc + r);

        switch (shapeIndex) {
            case 0: // Circle
                path.addOval(bounds, Path.Direction.CW);
                break;
            case 1: // Squircle (n = 3.8)
                int numPts = 180;
                for (int i = 0; i < numPts; i++) {
                    double th = 2.0 * Math.PI * i / numPts;
                    double ct = Math.cos(th);
                    double st = Math.sin(th);
                    double rT = r / Math.pow(Math.pow(Math.abs(ct), 3.8) + Math.pow(Math.abs(st), 3.8), 1.0 / 3.8);
                    float px = (float) (xc + rT * ct);
                    float py = (float) (yc + rT * st);
                    if (i == 0) path.moveTo(px, py);
                    else path.lineTo(px, py);
                }
                path.close();
                break;
            case 2: // Rounded Square
                path.addRoundRect(bounds, r * 0.52f, r * 0.52f, Path.Direction.CW);
                break;
            case 3: // Teardrop
                path.moveTo(xc, yc - r);
                path.lineTo(xc + r, yc - r);
                path.lineTo(xc + r, yc);
                for (int i = 0; i <= 270; i += 5) {
                    double th = Math.toRadians(i);
                    path.lineTo((float) (xc + r * Math.cos(th)), (float) (yc + r * Math.sin(th)));
                }
                path.close();
                break;
            case 4: // Hexagon
                for (int i = 0; i < 6; i++) {
                    double th = Math.toRadians(60 * i + 30);
                    float px = (float) (xc + r * Math.cos(th));
                    float py = (float) (yc + r * Math.sin(th));
                    if (i == 0) path.moveTo(px, py);
                    else path.lineTo(px, py);
                }
                path.close();
                break;
            case 5: // Octagon
                for (int i = 0; i < 8; i++) {
                    double th = Math.toRadians(45 * i + 22.5);
                    float px = (float) (xc + r * Math.cos(th));
                    float py = (float) (yc + r * Math.sin(th));
                    if (i == 0) path.moveTo(px, py);
                    else path.lineTo(px, py);
                }
                path.close();
                break;
            case 6: // Pebble
                path.addRoundRect(new RectF(xc - r * 0.95f, yc - r, xc + r * 0.95f, yc + r), r * 0.85f, r * 0.85f, Path.Direction.CW);
                break;
            case 7: // Square
                path.addRoundRect(bounds, 12f, 12f, Path.Direction.CW);
                break;
            default:
                path.addOval(bounds, Path.Direction.CW);
                break;
        }
        return path;
    }

    public static int getPlateColor(Context context, int themeIndex, int opacityPercent) {
        int baseRgb;
        if (themeIndex == 0) { // Monet
            baseRgb = getMonetBaseColor(context);
        } else { // Pure Black
            baseRgb = 0x000000;
        }
        int alpha = Math.max(0, Math.min(255, (int) (opacityPercent * 255f / 100f)));
        return (baseRgb & 0x00FFFFFF) | (alpha << 24);
    }

    public static int getMonetGlyphColor(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                return context.getColor(android.R.color.system_accent1_100);
            }
        } catch (Throwable ignored) {}
        return 0xFFD8E4F8;
    }

    private static int getMonetBaseColor(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 27) {
                WallpaperManager wm = WallpaperManager.getInstance(context);
                if (wm != null) {
                    WallpaperColors wc = wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
                    if (wc != null) {
                        Color c = wc.getPrimaryColor();
                        if (c != null) {
                            float[] hsv = new float[3];
                            Color.colorToHSV(c.toArgb(), hsv);
                            hsv[1] = Math.min(0.35f, Math.max(0.15f, hsv[1]));
                            hsv[2] = 0.22f;
                            return Color.HSVToColor(hsv);
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}

        try {
            if (Build.VERSION.SDK_INT >= 31) {
                return context.getColor(android.R.color.system_neutral1_900);
            }
        } catch (Throwable ignored) {}

        return 0xFF1E293B;
    }

    public static Bitmap createPlateBitmap(Context context, int shapeIndex, int themeIndex, int opacityPercent, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Path path = createShapePath(shapeIndex, size, size);
        int plateColor = getPlateColor(context, themeIndex, opacityPercent);
        if (Color.alpha(plateColor) > 0) {
            Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fillPaint.setColor(plateColor);
            fillPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, fillPaint);
        }
        // No borders / no stroke
        return bitmap;
    }

    public static Bitmap createMaskBitmap(int shapeIndex, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path path = createShapePath(shapeIndex, size, size);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        return bitmap;
    }

    public static Bitmap renderAppIcon(Context context, Drawable icon, int shapeIndex, int themeIndex,
                                      int styleIndex, int opacityPercent, int fillPercent, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 1. Draw Plate (no borders)
        Path path = createShapePath(shapeIndex, size, size);
        int plateColor = getPlateColor(context, themeIndex, opacityPercent);
        if (Color.alpha(plateColor) > 0) {
            Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fillPaint.setColor(plateColor);
            fillPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, fillPaint);
        }

        if (icon == null) return bitmap;

        // 2. Icon size & offset
        int glyphSize = (int) (size * (fillPercent / 100f));
        glyphSize = Math.max(16, Math.min(size, glyphSize));
        int offset = (size - glyphSize) / 2;

        if (styleIndex == 0) {
            // «Оригинальные цвета (Стоковые логотипы)»
            canvas.save();
            canvas.clipPath(path);
            icon.setBounds(offset, offset, offset + glyphSize, offset + glyphSize);
            icon.draw(canvas);
            canvas.restore();
            return bitmap;
        }

        // 3. Themed Monochrome (Style 1: Monet, Style 2: Black/White)
        int glyphColor;
        if (styleIndex == 1) { // Monet
            glyphColor = getMonetGlyphColor(context);
        } else { // Pure Black/White
            glyphColor = 0xFFF1F5F9;
        }

        // Check official monochrome
        Drawable mono = null;
        if (Build.VERSION.SDK_INT >= 33 && icon instanceof AdaptiveIconDrawable) {
            try {
                mono = ((AdaptiveIconDrawable) icon).getMonochrome();
            } catch (Throwable ignored) {}
        }

        if (mono != null) {
            try {
                Drawable m = mono.mutate();
                m.setTint(glyphColor);
                m.setBounds(offset, offset, offset + glyphSize, offset + glyphSize);
                m.draw(canvas);
                return bitmap;
            } catch (Throwable ignored) {}
        }

        // Fallback: smart glyph extraction (prevents "битый окрас")
        try {
            Bitmap temp = Bitmap.createBitmap(glyphSize, glyphSize, Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(temp);

            Drawable fg = null;
            if (icon instanceof AdaptiveIconDrawable) {
                fg = ((AdaptiveIconDrawable) icon).getForeground();
            }
            if (fg == null) fg = icon;
            fg.setBounds(0, 0, glyphSize, glyphSize);
            fg.draw(tempCanvas);

            Bitmap themedMono = extractSmartMonochrome(temp, glyphColor);
            canvas.drawBitmap(themedMono, offset, offset, null);
        } catch (Throwable ignored) {}

        return bitmap;
    }

    private static Bitmap extractSmartMonochrome(Bitmap src, int tintColor) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[w * h];
        src.getPixels(pixels, 0, w, 0, 0, w, h);

        int tr = Color.red(tintColor);
        int tg = Color.green(tintColor);
        int tb = Color.blue(tintColor);

        // 8 border sample points (safely inside the shape, avoiding rounded corner transparency)
        int[] sampleX = { w / 2, w / 2, 8, w - 8, w / 4, 3 * w / 4, w / 4, 3 * w / 4 };
        int[] sampleY = { 8, h - 8, h / 2, h / 2, 12, 12, h - 12, h - 12 };

        int opaqueCount = 0;
        int sumR = 0, sumG = 0, sumB = 0;

        for (int i = 0; i < 8; i++) {
            int sx = Math.max(0, Math.min(w - 1, sampleX[i]));
            int sy = Math.max(0, Math.min(h - 1, sampleY[i]));
            int p = pixels[sy * w + sx];
            int a = (p >>> 24) & 0xff;
            if (a > 180) {
                opaqueCount++;
                sumR += (p >> 16) & 0xff;
                sumG += (p >> 8) & 0xff;
                sumB += p & 0xff;
            }
        }

        if (opaqueCount < 5) {
            // Transparent-backed logo/glyph (Chrome, Telegram, Drive, etc.)
            for (int i = 0; i < pixels.length; i++) {
                int a = (pixels[i] >>> 24) & 0xff;
                if (a > 20) {
                    pixels[i] = (a << 24) | (tr << 16) | (tg << 8) | tb;
                } else {
                    pixels[i] = 0;
                }
            }
            result.setPixels(pixels, 0, w, 0, 0, w, h);
            return result;
        }

        // Solid background container detected (TikTok, Dialer, imo, Gallery, etc.)
        int bgR = sumR / opaqueCount;
        int bgG = sumG / opaqueCount;
        int bgB = sumB / opaqueCount;

        int[] candidatePixels = new int[w * h];
        int glyphPixelCount = 0;

        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            int a = (p >>> 24) & 0xff;
            if (a < 50) {
                candidatePixels[i] = 0;
                continue;
            }

            int pr = (p >> 16) & 0xff;
            int pg = (p >> 8) & 0xff;
            int pb = p & 0xff;

            int diff = Math.abs(pr - bgR) + Math.abs(pg - bgG) + Math.abs(pb - bgB);

            if (diff < 42) {
                // Background pixel -> make transparent
                candidatePixels[i] = 0;
            } else {
                // Logo pixel (phone handset, TikTok musical note, imo text, flower, etc.)
                glyphPixelCount++;
                int symbolAlpha = Math.min(255, (diff - 42) * 5);
                int finalAlpha = (a * symbolAlpha) / 255;
                candidatePixels[i] = (finalAlpha << 24) | (tr << 16) | (tg << 8) | tb;
            }
        }

        float ratio = (float) glyphPixelCount / (float) (w * h);

        if (ratio >= 0.015f && ratio <= 0.65f) {
            // Successfully extracted clean inner glyph!
            result.setPixels(candidatePixels, 0, w, 0, 0, w, h);
            return result;
        }

        // Fallback for complex icons (games like Block Blast, complex artwork):
        // Convert to high-contrast luminance rather than a solid flat block!
        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            int a = (p >>> 24) & 0xff;
            if (a == 0) {
                pixels[i] = 0;
                continue;
            }
            int pr = (p >> 16) & 0xff;
            int pg = (p >> 8) & 0xff;
            int pb = p & 0xff;
            int lum = (pr * 77 + pg * 151 + pb * 28) >> 8;
            int finalAlpha = (a * (64 + (lum * 191 / 255))) / 255;
            pixels[i] = (finalAlpha << 24) | (tr << 16) | (tg << 8) | tb;
        }

        result.setPixels(pixels, 0, w, 0, 0, w, h);
        return result;
    }

    public static File buildHyperOSThemeZip(Context context, int shapeIndex, int themeIndex,
                                           int styleIndex, int opacityPercent, int fillPercent) throws Exception {
        File outFile = new File(context.getCacheDir(), "hyperos_icons.zip");
        if (outFile.exists()) outFile.delete();

        int size = 235;
        Bitmap plateBitmap = createPlateBitmap(context, shapeIndex, themeIndex, opacityPercent, 168);
        Bitmap maskBitmap = createMaskBitmap(shapeIndex, 168);
        Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        ByteArrayOutputStream plateBytes = new ByteArrayOutputStream();
        plateBitmap.compress(Bitmap.CompressFormat.PNG, 100, plateBytes);

        ByteArrayOutputStream maskBytes = new ByteArrayOutputStream();
        maskBitmap.compress(Bitmap.CompressFormat.PNG, 100, maskBytes);

        ByteArrayOutputStream emptyBytes = new ByteArrayOutputStream();
        emptyBitmap.compress(Bitmap.CompressFormat.PNG, 100, emptyBytes);

        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        java.util.HashSet<String> added = new java.util.HashSet<String>();

        // 1. Master compositing templates (clean, NO BORDER)
        writeZipEntry(zos, "icon_background.png", plateBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_mask.png", maskBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_border.png", emptyBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_pattern.png", emptyBytes.toByteArray(), added);

        String svgPath = SHAPE_SVG_PATHS[Math.min(shapeIndex, SHAPE_SVG_PATHS.length - 1)];
        String xmlConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<IconTransform>\n" +
                "    <Config name=\"SupportLayerIcon\" value=\"true\" />\n" +
                "    <Config name=\"UseDynamicIcon\" value=\"true\" />\n" +
                "    <Config name=\"ConfigIconMask\" value=\"" + svgPath + "\" />\n" +
                "</IconTransform>";
        writeZipEntry(zos, "transform_config.xml", xmlConfig.getBytes("UTF-8"), added);

        PackageManager pm = context.getPackageManager();

        // 2. Query ALL installed launcher applications on the device
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);

        if (apps != null) {
            for (ResolveInfo info : apps) {
                try {
                    if (info == null || info.activityInfo == null) continue;
                    String pkg = info.activityInfo.packageName;
                    String cls = info.activityInfo.name;
                    Drawable stockIcon = info.loadIcon(pm);

                    Bitmap themedBitmap = renderAppIcon(context, stockIcon, shapeIndex, themeIndex,
                            styleIndex, opacityPercent, fillPercent, size);
                    ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                    themedBitmap.compress(Bitmap.CompressFormat.PNG, 100, iconStream);
                    byte[] iconBytes = iconStream.toByteArray();

                    writeAppEntries(zos, iconBytes, pkg, cls, added);
                } catch (Throwable ignored) {}
            }
        }

        // 3. Fallback: explicitly ensure ALL core HyperOS / MIUI system packages are included
        String[] criticalPkgs = {
                "com.android.contacts",
                "com.google.android.dialer",
                "com.android.phone",
                "com.xiaomi.phone",
                "com.miui.gallery",
                "com.android.camera",
                "com.android.mms",
                "com.google.android.apps.messaging",
                "com.android.settings",
                "com.miui.securitycenter",
                "com.android.fileexplorer",
                "com.mi.android.globalfileexplorer",
                "com.android.deskclock",
                "com.android.calendar",
                "com.miui.weather2",
                "com.miui.notes",
                "com.miui.calculator",
                "com.miui.compass",
                "com.miui.player",
                "com.android.soundrecorder"
        };

        for (String cPkg : criticalPkgs) {
            try {
                if (!added.contains(cPkg) || !added.contains(cPkg + ".png")) {
                    Drawable cIcon = pm.getApplicationIcon(cPkg);
                    Bitmap themedBitmap = renderAppIcon(context, cIcon, shapeIndex, themeIndex,
                            styleIndex, opacityPercent, fillPercent, size);
                    ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                    themedBitmap.compress(Bitmap.CompressFormat.PNG, 100, iconStream);
                    byte[] iconBytes = iconStream.toByteArray();

                    writeAppEntries(zos, iconBytes, cPkg, null, added);
                }
            } catch (Throwable ignored) {}
        }

        zos.close();
        return outFile;
    }

    private static void writeAppEntries(ZipOutputStream zos, byte[] iconBytes, String pkg, String cls,
                                        java.util.Set<String> added) throws IOException {
        if (iconBytes == null || iconBytes.length == 0) return;

        // 1. Direct package & class lookup
        if (pkg != null && !pkg.isEmpty()) {
            writeZipEntry(zos, pkg, iconBytes, added);
            writeZipEntry(zos, pkg + ".png", iconBytes, added);
        }
        if (cls != null && !cls.isEmpty()) {
            writeZipEntry(zos, cls + ".png", iconBytes, added);
            if (pkg != null) {
                writeZipEntry(zos, pkg + "#" + cls + ".png", iconBytes, added);
                writeZipEntry(zos, pkg + "#" + cls, iconBytes, added);
            }
        }

        // 2. Comprehensive MIUI / HyperOS system aliases
        if (pkg == null) return;
        String p = pkg.toLowerCase();
        String c = (cls != null) ? cls.toLowerCase() : "";

        // Phone / Dialer (fixes standard HyperOS Phone not applying)
        if (p.contains("contacts") || p.contains("dialer") || p.contains("phone")) {
            writeZipEntry(zos, "phone.png", iconBytes, added);
            writeZipEntry(zos, "phone", iconBytes, added);
            writeZipEntry(zos, "contacts.png", iconBytes, added);
            writeZipEntry(zos, "contacts", iconBytes, added);
            writeZipEntry(zos, "dialer.png", iconBytes, added);
            writeZipEntry(zos, "dialer", iconBytes, added);
            writeZipEntry(zos, "com.android.contacts#com.android.contacts.TwelveKeyDialer.png", iconBytes, added);
            writeZipEntry(zos, "com.android.contacts#com.android.contacts.TwelveKeyDialer", iconBytes, added);
            writeZipEntry(zos, "TwelveKeyDialer.png", iconBytes, added);
        }

        // Camera
        if (p.contains("camera")) {
            writeZipEntry(zos, "camera.png", iconBytes, added);
            writeZipEntry(zos, "camera", iconBytes, added);
        }

        // Gallery / Photos
        if (p.contains("gallery") || p.contains("photos")) {
            writeZipEntry(zos, "gallery.png", iconBytes, added);
            writeZipEntry(zos, "gallery", iconBytes, added);
        }

        // Messaging / SMS
        if (p.contains("mms") || p.contains("messaging") || p.contains("message")) {
            writeZipEntry(zos, "mms.png", iconBytes, added);
            writeZipEntry(zos, "mms", iconBytes, added);
            writeZipEntry(zos, "sms.png", iconBytes, added);
            writeZipEntry(zos, "sms", iconBytes, added);
        }

        // Settings
        if (p.contains("settings")) {
            writeZipEntry(zos, "settings.png", iconBytes, added);
            writeZipEntry(zos, "settings", iconBytes, added);
        }

        // Browser
        if (p.contains("browser") || p.contains("chrome")) {
            writeZipEntry(zos, "browser.png", iconBytes, added);
            writeZipEntry(zos, "browser", iconBytes, added);
        }

        // Clock
        if (p.contains("clock")) {
            writeZipEntry(zos, "clock.png", iconBytes, added);
            writeZipEntry(zos, "clock", iconBytes, added);
            writeZipEntry(zos, "deskclock.png", iconBytes, added);
            writeZipEntry(zos, "deskclock", iconBytes, added);
        }

        // Calendar
        if (p.contains("calendar")) {
            writeZipEntry(zos, "calendar.png", iconBytes, added);
            writeZipEntry(zos, "calendar", iconBytes, added);
        }

        // File Manager
        if (p.contains("fileexplorer") || p.contains("file_explorer") || p.contains("files")) {
            writeZipEntry(zos, "file_explorer.png", iconBytes, added);
            writeZipEntry(zos, "fileexplorer.png", iconBytes, added);
            writeZipEntry(zos, "file_explorer", iconBytes, added);
        }

        // Weather
        if (p.contains("weather")) {
            writeZipEntry(zos, "weather.png", iconBytes, added);
            writeZipEntry(zos, "weather", iconBytes, added);
        }

        // Notes
        if (p.contains("notes") || p.contains("keep")) {
            writeZipEntry(zos, "notes.png", iconBytes, added);
            writeZipEntry(zos, "notes", iconBytes, added);
        }

        // Security / Cleaner
        if (p.contains("securitycenter") || p.contains("security")) {
            writeZipEntry(zos, "security.png", iconBytes, added);
            writeZipEntry(zos, "securitycenter.png", iconBytes, added);
            writeZipEntry(zos, "clean_master.png", iconBytes, added);
            writeZipEntry(zos, "cleanmaster.png", iconBytes, added);
        }

        // Calculator
        if (p.contains("calculator")) {
            writeZipEntry(zos, "calculator.png", iconBytes, added);
            writeZipEntry(zos, "calculator", iconBytes, added);
        }

        // Compass
        if (p.contains("compass")) {
            writeZipEntry(zos, "compass.png", iconBytes, added);
            writeZipEntry(zos, "compass", iconBytes, added);
        }

        // Music
        if (p.contains("music") || p.contains("player")) {
            writeZipEntry(zos, "music.png", iconBytes, added);
            writeZipEntry(zos, "music", iconBytes, added);
        }

        // Sound Recorder
        if (p.contains("soundrecorder") || p.contains("recorder")) {
            writeZipEntry(zos, "sound_recorder.png", iconBytes, added);
            writeZipEntry(zos, "sound_recorder", iconBytes, added);
        }

        // Themes
        if (p.contains("thememanager")) {
            writeZipEntry(zos, "theme_manager.png", iconBytes, added);
            writeZipEntry(zos, "theme_manager", iconBytes, added);
        }

        // Updater
        if (p.contains("updater")) {
            writeZipEntry(zos, "updater.png", iconBytes, added);
        }

        // Downloads
        if (p.contains("download")) {
            writeZipEntry(zos, "download.png", iconBytes, added);
            writeZipEntry(zos, "downloads.png", iconBytes, added);
        }

        // App Store
        if (p.contains("market") || p.contains("mipicks")) {
            writeZipEntry(zos, "market.png", iconBytes, added);
            writeZipEntry(zos, "appstore.png", iconBytes, added);
        }
    }

    private static void writeZipEntry(ZipOutputStream zos, String name, byte[] data, java.util.Set<String> added) throws IOException {
        if (!added.add(name)) return;
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }
}
