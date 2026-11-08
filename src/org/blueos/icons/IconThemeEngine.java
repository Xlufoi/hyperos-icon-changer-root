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
            "Круг (Circle — BlueOS)",
            "Сквиркл (Squircle)",
            "Скруглённый квадрат (Rounded Square)",
            "Капля (Teardrop)",
            "Шестиугольник (Hexagon)",
            "Восьмиугольник (Octagon)",
            "Цилиндр (Pebble)",
            "Квадрат (Square)"
    };

    public static final String[] THEMES = {
            "Monet Полупрозрачный (70%)",
            "Черный полупрозрачный (Pure Black 70%)",
            "Monet Сплошной (100%)",
            "Черный сплошной (Pure Black 100%)"
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

    public static int getPlateColor(Context context, int themeIndex) {
        if (themeIndex == 0) { // Monet 70%
            int baseRgb = getMonetBaseColor(context);
            return (baseRgb & 0x00FFFFFF) | (0xB2 << 24); // 70% opacity
        } else if (themeIndex == 1) { // Pure Black 70%
            return 0xB2000000; // 70% opacity pure black
        } else if (themeIndex == 2) { // Monet 100%
            return 0xFF000000 | getMonetBaseColor(context);
        } else { // Pure Black 100%
            return 0xFF000000;
        }
    }

    public static int getGlyphColor(Context context, int themeIndex) {
        if (themeIndex == 0 || themeIndex == 2) { // Monet
            try {
                if (Build.VERSION.SDK_INT >= 31) {
                    return context.getColor(android.R.color.system_accent1_100);
                }
            } catch (Throwable ignored) {}
            return 0xFFD8E4F8;
        } else { // Pure Black
            return 0xFFF1F5F9;
        }
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

    public static Bitmap createPlateBitmap(Context context, int shapeIndex, int themeIndex, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Path path = createShapePath(shapeIndex, size, size);
        Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(getPlateColor(context, themeIndex));
        fillPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, fillPaint);
        // NO BORDER / NO OUTLINE

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

    public static Bitmap renderThemedIcon(Context context, Drawable icon, int shapeIndex, int themeIndex, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 1. Plate: clean flat 70% semi-transparent plate (NO BORDER)
        Path path = createShapePath(shapeIndex, size, size);
        Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(getPlateColor(context, themeIndex));
        fillPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, fillPaint);

        // 2. Render AOSP Themed Glyph
        int glyphColor = getGlyphColor(context, themeIndex);
        int glyphSize = (int) (size * 0.54f);
        int offset = (size - glyphSize) / 2;

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

        // Convert stock icon/foreground to monochrome silhouette
        if (icon != null) {
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

                Bitmap monoBitmap = convertBitmapToThemedMonochrome(temp, glyphColor);
                canvas.drawBitmap(monoBitmap, offset, offset, null);
            } catch (Throwable ignored) {}
        }

        return bitmap;
    }

    private static Bitmap convertBitmapToThemedMonochrome(Bitmap src, int tintColor) {
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(result);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        c.drawBitmap(src, 0, 0, paint);

        return result;
    }

    public static File buildHyperOSThemeZip(Context context, int shapeIndex, int themeIndex) throws Exception {
        File outFile = new File(context.getCacheDir(), "hyperos_icons.zip");
        if (outFile.exists()) outFile.delete();

        int size = 235;
        Bitmap plateBitmap = createPlateBitmap(context, shapeIndex, themeIndex, 168);
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

                Bitmap themedBitmap = renderThemedIcon(context, stockIcon, shapeIndex, themeIndex, size);
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
