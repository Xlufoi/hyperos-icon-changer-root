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
            if (fillPercent >= 75) {
                canvas.clipPath(path);
            }
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

        // Check corners to detect if there is a solid background container
        int c1 = pixels[0];
        int c2 = pixels[w - 1];
        int c3 = pixels[(h - 1) * w];
        int c4 = pixels[w * h - 1];

        int a1 = (c1 >>> 24) & 0xff;
        int a2 = (c2 >>> 24) & 0xff;
        int a3 = (c3 >>> 24) & 0xff;
        int a4 = (c4 >>> 24) & 0xff;

        boolean hasSolidBg = (a1 > 128 && a2 > 128 && a3 > 128 && a4 > 128);

        if (!hasSolidBg) {
            // Already a transparent-backed glyph/logo
            for (int i = 0; i < pixels.length; i++) {
                int a = (pixels[i] >>> 24) & 0xff;
                if (a > 0) {
                    pixels[i] = (a << 24) | (tr << 16) | (tg << 8) | tb;
                }
            }
        } else {
            // Solid background container detected (Dialer, Gallery, Block Blast, etc.)
            // Background color is estimated from corners
            int bgR = (((c1 >> 16) & 0xff) + ((c2 >> 16) & 0xff) + ((c3 >> 16) & 0xff) + ((c4 >> 16) & 0xff)) / 4;
            int bgG = (((c1 >> 8) & 0xff) + ((c2 >> 8) & 0xff) + ((c3 >> 8) & 0xff) + ((c4 >> 8) & 0xff)) / 4;
            int bgB = ((c1 & 0xff) + (c2 & 0xff) + (c3 & 0xff) + (c4 & 0xff)) / 4;

            for (int i = 0; i < pixels.length; i++) {
                int p = pixels[i];
                int a = (p >>> 24) & 0xff;
                if (a == 0) continue;

                int pr = (p >> 16) & 0xff;
                int pg = (p >> 8) & 0xff;
                int pb = p & 0xff;

                int dr = Math.abs(pr - bgR);
                int dg = Math.abs(pg - bgG);
                int db = Math.abs(pb - bgB);
                int diff = dr + dg + db; // 0..765

                if (diff < 45) {
                    // Background pixel -> make transparent
                    pixels[i] = 0;
                } else {
                    // Foreground symbol (handset, flower lines, etc.)
                    int symbolAlpha = Math.min(255, (diff - 45) * 5);
                    int finalAlpha = (a * symbolAlpha) / 255;
                    pixels[i] = (finalAlpha << 24) | (tr << 16) | (tg << 8) | tb;
                }
            }
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

        // 2. Query ALL installed launcher applications on the device
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);

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

                // Save both package and activity names so HyperOS matches 100% of apps
                writeZipEntry(zos, pkg, iconBytes, added);
                writeZipEntry(zos, pkg + ".png", iconBytes, added);
                if (cls != null && !cls.isEmpty()) {
                    writeZipEntry(zos, cls + ".png", iconBytes, added);
                    writeZipEntry(zos, pkg + "#" + cls + ".png", iconBytes, added);
                }
            } catch (Throwable ignored) {}
        }

        zos.close();
        return outFile;
    }

    private static void writeZipEntry(ZipOutputStream zos, String name, byte[] data, java.util.Set<String> added) throws IOException {
        if (!added.add(name)) return;
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }
}
