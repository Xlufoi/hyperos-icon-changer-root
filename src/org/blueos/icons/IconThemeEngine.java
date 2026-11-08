package org.blueos.icons;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
            "Сосуд / Клевер (Vessel)",
            "Квадрат (Square)"
    };

    public static final String[] THEMES = {
            "Monet Полупрозрачный (70%)",
            "Monet Акцентный (70%)",
            "Pure Black (70%)",
            "AOSP Dark (70%)",
            "BlueOS Cyan (70%)"
    };

    public static Path createShapePath(int shapeIndex, float width, float height) {
        Path path = new Path();
        float xc = width / 2f;
        float yc = height / 2f;
        float r = Math.min(width, height) * 0.44f;
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
            case 3: // Teardrop (top-right sharp)
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
            case 7: // Vessel (Clover)
                for (int i = 0; i < 180; i++) {
                    double th = Math.toRadians(2 * i);
                    double rT = r * (0.86 + 0.14 * Math.cos(4 * th));
                    float px = (float) (xc + rT * Math.cos(th));
                    float py = (float) (yc + rT * Math.sin(th));
                    if (i == 0) path.moveTo(px, py);
                    else path.lineTo(px, py);
                }
                path.close();
                break;
            case 8: // Square
                path.addRoundRect(bounds, 16f, 16f, Path.Direction.CW);
                break;
            default:
                path.addOval(bounds, Path.Direction.CW);
                break;
        }
        return path;
    }

    public static int getThemeBaseColor(Context context, int themeIndex) {
        if (themeIndex == 0) { // Monet 70%
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
                                hsv[1] = Math.min(0.35f, hsv[1]); // soft tint
                                hsv[2] = 0.22f; // dark neutral surface
                                return Color.HSVToColor(hsv);
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}
            return 0xFF1C2A3A;
        } else if (themeIndex == 1) { // Monet Accent
            try {
                if (Build.VERSION.SDK_INT >= 27) {
                    WallpaperManager wm = WallpaperManager.getInstance(context);
                    if (wm != null) {
                        WallpaperColors wc = wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
                        if (wc != null) {
                            Color c = wc.getSecondaryColor();
                            if (c == null) c = wc.getPrimaryColor();
                            if (c != null) {
                                float[] hsv = new float[3];
                                Color.colorToHSV(c.toArgb(), hsv);
                                hsv[1] = Math.min(0.65f, Math.max(0.40f, hsv[1]));
                                hsv[2] = 0.35f;
                                return Color.HSVToColor(hsv);
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}
            return 0xFF1E3A5F;
        } else if (themeIndex == 2) { // Pure Black
            return 0xFF0C0C0E;
        } else if (themeIndex == 3) { // AOSP Dark
            return 0xFF22262C;
        } else { // Cyan
            return 0xFF0E304C;
        }
    }

    public static Bitmap generateShapePlate(Context context, int shapeIndex, int themeIndex, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Path path = createShapePath(shapeIndex, size, size);

        // Fill with 70% opacity (alpha = 178)
        int baseRgb = getThemeBaseColor(context, themeIndex);
        int plateColor = (baseRgb & 0x00FFFFFF) | (0xB2 << 24); // 0xB2 = 178 (70%)

        Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(plateColor);
        fillPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, fillPaint);

        // Specular highlight rim
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(0x88FFFFFF);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2.5f);
        canvas.drawPath(path, strokePaint);

        return bitmap;
    }

    public static Bitmap generateShapeMask(int shapeIndex, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path path = createShapePath(shapeIndex, size, size);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        return bitmap;
    }

    public static File buildCustomizedIconsZip(Context context, int shapeIndex, int themeIndex) throws Exception {
        File outFile = new File(context.getCacheDir(), "custom_icons.zip");
        if (outFile.exists()) outFile.delete();

        Bitmap plate168 = generateShapePlate(context, shapeIndex, themeIndex, 168);
        Bitmap mask168 = generateShapeMask(shapeIndex, 168);

        ByteArrayOutputStream plateStream = new ByteArrayOutputStream();
        plate168.compress(Bitmap.CompressFormat.PNG, 100, plateStream);
        byte[] plateBytes = plateStream.toByteArray();

        ByteArrayOutputStream maskStream = new ByteArrayOutputStream();
        mask168.compress(Bitmap.CompressFormat.PNG, 100, maskStream);
        byte[] maskBytes = maskStream.toByteArray();

        // Read base zip from assets and replace master templates
        InputStream in = context.getAssets().open("icons");
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in));
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));

        ZipEntry entry;
        byte[] buf = new byte[8192];
        while ((entry = zis.getNextEntry()) != null) {
            String name = entry.getName();
            zos.putNextEntry(new ZipEntry(name));
            if (name.equals("icon_background.png") || name.equals("icon_border.png") || name.equals("icon_pattern.png")) {
                zos.write(plateBytes);
            } else if (name.equals("icon_mask.png")) {
                zos.write(maskBytes);
            } else {
                int len;
                while ((len = zis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
            }
            zos.closeEntry();
        }
        zis.close();
        zos.close();
        return outFile;
    }
}
