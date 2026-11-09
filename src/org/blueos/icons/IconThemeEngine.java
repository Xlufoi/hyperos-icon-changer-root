package org.blueos.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class IconThemeEngine {

    public static final String[] SHAPES = {
            "Circle",
            "Squircle",
            "Rounded Square",
            "Teardrop",
            "Teardrop Right",
            "Hexagon",
            "Octagon",
            "Pebble"
    };

    public static final String[] THEMES = {
            "System Monet (Wallpaper)",
            "Sky Blue Monet",
            "Mint Green Monet",
            "Lavender Violet Monet",
            "Rose Pink Monet",
            "Peach Coral Monet",
            "Amber Sand Monet",
            "Teal Cyan Monet",
            "Ruby Crimson Monet",
            "Deep Indigo Monet",
            "Slate Gray Monet",
            "AOSP AMOLED Black",
            "AOSP Pure White"
    };

    public static final String[] STYLES = {
            "Themed Monochrome (RGB Palette)",
            "Original Stock Icons"
    };

    public static final String[] SHAPE_SVG_PATHS = {
            // Circle
            "M 50,0 A 50,50 0 1,1 50,100 A 50,50 0 1,1 50,0 Z",
            // Squircle
            "M 50,0 C 10,0 0,10 0,50 C 0,90 10,100 50,100 C 90,100 100,90 100,50 C 100,10 90,0 50,0 Z",
            // Rounded Square
            "M 22,0 L 78,0 C 90,0 100,10 100,22 L 100,78 C 100,90 90,100 78,100 L 22,100 C 10,100 0,90 0,78 L 0,22 C 0,10 10,0 22,0 Z",
            // Teardrop
            "M 50,0 C 77.6,0 100,22.4 100,50 C 100,77.6 77.6,100 50,100 C 22.4,100 0,77.6 0,50 L 0,0 Z",
            // Teardrop Right
            "M 50,0 C 22.4,0 0,22.4 0,50 C 0,77.6 22.4,100 50,100 C 77.6,100 100,77.6 100,50 L 100,0 Z",
            // Hexagon
            "M 50,0 L 93.3,25 L 93.3,75 L 50,100 L 6.7,75 L 6.7,25 Z",
            // Octagon
            "M 29.3,0 L 70.7,0 L 100,29.3 L 100,70.7 L 70.7,100 L 29.3,100 L 0,70.7 L 0,29.3 Z",
            // Pebble / Flower
            "M 50,0 C 65,0 75,10 82,18 C 90,25 100,35 100,50 C 100,65 90,75 82,82 C 75,90 65,100 50,100 C 35,100 25,90 18,82 C 10,75 0,65 0,50 C 0,35 10,25 18,18 C 25,10 35,0 50,0 Z"
    };

    public static Path createShapePath(int shapeIndex, float width, float height) {
        return createShapePath(shapeIndex, width, height, 36);
    }

    public static Path createShapePath(int shapeIndex, float width, float height, int cornerRadius) {
        Path path = new Path();
        float r = Math.min(width, height) / 2f;
        float cx = width / 2f;
        float cy = height / 2f;
        float crFraction = Math.max(0.10f, Math.min(0.50f, cornerRadius / 100f));
        float rx = width * crFraction;
        float ry = height * crFraction;

        switch (shapeIndex) {
            case 0: // Circle
                path.addCircle(cx, cy, r, Path.Direction.CW);
                break;
            case 1: // Squircle (Organic continuous curvature)
                float cp1 = width * (crFraction * 0.52f);
                float cp2 = width * (1f - crFraction * 0.52f);
                path.moveTo(cx, 0);
                path.cubicTo(cp1, 0, 0, cp1, 0, cy);
                path.cubicTo(0, cp2, cp1, height, cx, height);
                path.cubicTo(cp2, height, width, cp2, width, cy);
                path.cubicTo(width, cp1, cp2, 0, cx, 0);
                path.close();
                break;
            case 2: // Rounded Square (Modern smooth AOSP rounded square)
                path.addRoundRect(new RectF(0, 0, width, height), rx, ry, Path.Direction.CW);
                break;
            case 3: // Teardrop Left
                path.moveTo(cx, 0);
                path.cubicTo(width, 0, width, height, cx, height);
                path.cubicTo(0, height, 0, height * 0.5f, 0, height * 0.5f);
                path.lineTo(0, 0);
                path.close();
                break;
            case 4: // Teardrop Right
                path.moveTo(cx, 0);
                path.cubicTo(0, 0, 0, height, cx, height);
                path.cubicTo(width, height, width, height * 0.5f, width, height * 0.5f);
                path.lineTo(width, 0);
                path.close();
                break;
            case 5: // Hexagon
                for (int i = 0; i < 6; i++) {
                    double angle = Math.toRadians(60 * i - 30);
                    float x = cx + (float) (r * Math.cos(angle));
                    float y = cy + (float) (r * Math.sin(angle));
                    if (i == 0) path.moveTo(x, y);
                    else path.lineTo(x, y);
                }
                path.close();
                break;
            case 6: // Octagon
                for (int i = 0; i < 8; i++) {
                    double angle = Math.toRadians(45 * i - 22.5);
                    float x = cx + (float) (r * Math.cos(angle));
                    float y = cy + (float) (r * Math.sin(angle));
                    if (i == 0) path.moveTo(x, y);
                    else path.lineTo(x, y);
                }
                path.close();
                break;
            case 7: // Pebble / Flower
                path.moveTo(cx, 0);
                path.cubicTo(width * 0.75f, 0, width, height * 0.25f, width, cy);
                path.cubicTo(width, height * 0.75f, width * 0.75f, height, cx, height);
                path.cubicTo(width * 0.25f, height, 0, height * 0.75f, 0, cy);
                path.cubicTo(0, height * 0.25f, width * 0.25f, 0, cx, 0);
                path.close();
                break;
            default:
                path.addCircle(cx, cy, r, Path.Direction.CW);
                break;
        }
        return path;
    }

    public static String getShapeSvgPath(int shapeIndex, int cornerRadius) {
        float cr = Math.max(10f, Math.min(50f, cornerRadius));
        if (shapeIndex == 0) {
            return "M 50,0 A 50,50 0 1,1 50,100 A 50,50 0 1,1 50,0 Z";
        } else if (shapeIndex == 1) {
            float cp = cr * 0.52f;
            return String.format(java.util.Locale.US,
                "M 50,0 C %.2f,0 0,%.2f 0,50 C 0,%.2f %.2f,100 50,100 C %.2f,100 100,%.2f 100,50 C 100,%.2f %.2f,0 50,0 Z",
                cp, cp, 100f - cp, cp, 100f - cp, 100f - cp, cp, 100f - cp);
        } else if (shapeIndex == 2) {
            return String.format(java.util.Locale.US,
                "M %.2f,0 L %.2f,0 C %.2f,0 100,%.2f 100,%.2f L 100,%.2f C 100,%.2f %.2f,100 %.2f,100 L %.2f,100 C %.2f,100 0,%.2f 0,%.2f L 0,%.2f C 0,%.2f %.2f,0 %.2f,0 Z",
                cr, 100f - cr, 100f - cr * 0.45f, cr * 0.45f, cr,
                100f - cr, 100f - cr * 0.45f, 100f - cr * 0.45f, 100f - cr,
                cr, cr * 0.45f, 100f - cr * 0.45f, 100f - cr,
                cr, cr * 0.45f, cr * 0.45f, cr);
        }
        return SHAPE_SVG_PATHS[Math.min(shapeIndex, SHAPE_SVG_PATHS.length - 1)];
    }

    public static int getPlateColor(Context context, int themeIndex, int opacityPercent) {
        int alpha = (int) (255 * (opacityPercent / 100f));
        alpha = Math.max(0, Math.min(255, alpha));

        int r = 30, g = 41, b = 59; // Dark fallback

        switch (themeIndex) {
            case 0: // System Monet (from wallpaper)
                if (Build.VERSION.SDK_INT >= 31 && context != null) {
                    try {
                        int c = context.getColor(android.R.color.system_accent1_100);
                        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
                    } catch (Throwable ignored) {}
                }
                r = 219; g = 234; b = 254; // #DBEAFE
                break;
            case 1: // Sky Blue
                r = 224; g = 242; b = 254; // #E0F2FE
                break;
            case 2: // Mint Green
                r = 209; g = 250; b = 229; // #D1FAE5
                break;
            case 3: // Lavender Violet
                r = 243; g = 232; b = 255; // #F3E8FF
                break;
            case 4: // Rose Pink
                r = 252; g = 231; b = 243; // #FCE7F3
                break;
            case 5: // Peach Coral
                r = 254; g = 215; b = 170; // #FED7AA
                break;
            case 6: // Amber Sand
                r = 254; g = 240; b = 138; // #FEF08A
                break;
            case 7: // Teal Cyan
                r = 204; g = 251; b = 241; // #CCFBF1
                break;
            case 8: // Ruby Crimson
                r = 254; g = 205; b = 211; // #FECDD3
                break;
            case 9: // Deep Indigo
                r = 224; g = 231; b = 255; // #E0E7FF
                break;
            case 10: // Slate Gray
                r = 226; g = 232; b = 240; // #E2E8F0
                break;
            case 11: // AOSP Pure Black
                r = 15; g = 23; b = 42; // #0F172A
                break;
            case 12: // AOSP Pure White
                r = 248; g = 250; b = 252; // #F8FAFC
                break;
        }
        return Color.argb(alpha, r, g, b);
    }

    public static int getMonetGlyphColor(Context context) {
        return getMonetGlyphColor(context, 0);
    }

    public static int getMonetGlyphColor(Context context, int themeIndex) {
        switch (themeIndex) {
            case 0: // System Monet
                if (Build.VERSION.SDK_INT >= 31 && context != null) {
                    try {
                        return context.getColor(android.R.color.system_accent1_200);
                    } catch (Throwable ignored) {}
                }
                return 0xFF0284C7;
            case 1: // Sky Blue
                return 0xFF0284C7;
            case 2: // Mint Green
                return 0xFF059669;
            case 3: // Lavender Violet
                return 0xFF9333EA;
            case 4: // Rose Pink
                return 0xFFDB2777;
            case 5: // Peach Coral
                return 0xFFEA580C;
            case 6: // Amber Sand
                return 0xFFD97706;
            case 7: // Teal Cyan
                return 0xFF0D9488;
            case 8: // Ruby Crimson
                return 0xFFE11D48;
            case 9: // Deep Indigo
                return 0xFF4F46E5;
            case 10: // Slate Gray
                return 0xFF475569;
            case 11: // AOSP Black
                return 0xFFF8FAFC;
            case 12: // AOSP White
                return 0xFF0F172A;
            default:
                return 0xFF0284C7;
        }
    }

    public static Bitmap createPlateBitmap(Context context, int shapeIndex, int themeIndex, int opacityPercent, int size) {
        return createPlateBitmap(context, shapeIndex, themeIndex, opacityPercent, 36, size, 0, false);
    }

    public static Bitmap createPlateBitmap(Context context, int shapeIndex, int themeIndex, int opacityPercent, int cornerRadius, int size) {
        return createPlateBitmap(context, shapeIndex, themeIndex, opacityPercent, cornerRadius, size, 0, false);
    }

    public static Bitmap createPlateBitmap(Context context, int shapeIndex, int themeIndex, int opacityPercent, int cornerRadius, int size,
                                           int customPlateColor, boolean useCustomPlate) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path path = createShapePath(shapeIndex, size, size, cornerRadius);
        int plateColor;
        if (useCustomPlate) {
            int alpha = (int) (255 * (opacityPercent / 100f));
            alpha = Math.max(0, Math.min(255, alpha));
            plateColor = Color.argb(alpha, Color.red(customPlateColor), Color.green(customPlateColor), Color.blue(customPlateColor));
        } else {
            plateColor = getPlateColor(context, themeIndex, opacityPercent);
        }
        if (Color.alpha(plateColor) > 0) {
            Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fillPaint.setColor(plateColor);
            fillPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, fillPaint);
        }
        return bitmap;
    }

    public static Bitmap createMaskBitmap(int shapeIndex, int size) {
        return createMaskBitmap(shapeIndex, 36, size);
    }

    public static Bitmap createMaskBitmap(int shapeIndex, int cornerRadius, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path path = createShapePath(shapeIndex, size, size, cornerRadius);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        return bitmap;
    }

    // ==================== Unified Visual Sizing & Content Normalization ====================

    /**
     * Finds the tight bounding box of visible pixels (alpha > 25) in any bitmap,
     * scales it proportionally so its maximum dimension is targetSize * fillFraction,
     * and perfectly centers it in a targetSize x targetSize bitmap.
     * This guarantees 100% consistent optical fill across Adaptive, Legacy, Vector, and Stock icons!
     */
    public static Bitmap normalizeAndCenterBitmap(Bitmap src, int targetSize, float fillFraction) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[w * h];
        src.getPixels(pixels, 0, w, 0, 0, w, h);

        int minX = w, maxX = -1, minY = h, maxY = -1;
        for (int y = 0; y < h; y++) {
            int row = y * w;
            for (int x = 0; x < w; x++) {
                int a = (pixels[row + x] >>> 24) & 0xff;
                if (a > 25) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return src; // All transparent, return original
        }

        int contentW = maxX - minX + 1;
        int contentH = maxY - minY + 1;
        int maxDim = Math.max(contentW, contentH);

        float desiredSize = targetSize * fillFraction;
        float scale = desiredSize / (float) maxDim;

        Bitmap result = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        float drawW = contentW * scale;
        float drawH = contentH * scale;
        float left = (targetSize - drawW) / 2f;
        float top = (targetSize - drawH) / 2f;

        Rect srcRect = new Rect(minX, minY, maxX + 1, maxY + 1);
        RectF dstRect = new RectF(left, top, left + drawW, top + drawH);
        canvas.drawBitmap(src, srcRect, dstRect, paint);

        return result;
    }

    // ==================== Deterministic Vector Glyphs for HyperOS Apps ====================

    public static void drawPhoneGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        canvas.rotate(-45);
        float w = s * 0.26f;
        float h = s * 0.80f;
        canvas.drawRoundRect(new RectF(-w, -h/2, w, -h/2 + w * 1.15f), w * 0.45f, w * 0.45f, paint);
        canvas.drawRoundRect(new RectF(-w, h/2 - w * 1.15f, w, h/2), w * 0.45f, w * 0.45f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w * 0.75f);
        Path path = new Path();
        path.moveTo(-w * 0.25f, -h/2 + w * 0.8f);
        path.cubicTo(-w * 1.45f, -h * 0.2f, -w * 1.45f, h * 0.2f, -w * 0.25f, h/2 - w * 0.8f);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    public static void drawContactsGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        canvas.drawCircle(0, -s * 0.18f, s * 0.20f, paint);
        Path path = new Path();
        path.moveTo(-s * 0.38f, s * 0.38f);
        path.cubicTo(-s * 0.35f, s * 0.08f, s * 0.35f, s * 0.08f, s * 0.38f, s * 0.38f);
        path.close();
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    public static void drawCameraGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.80f;
        float h = s * 0.60f;
        canvas.drawRoundRect(new RectF(-w * 0.35f, -h/2 - h * 0.20f, -w * 0.05f, -h/2), 6f, 6f, paint);
        canvas.drawRoundRect(new RectF(-w/2, -h/2, w/2, h/2), h * 0.22f, h * 0.22f, paint);
        Paint cut = new Paint(Paint.ANTI_ALIAS_FLAG);
        cut.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(0, 0, h * 0.34f, cut);
        canvas.drawCircle(0, 0, h * 0.18f, paint);
        canvas.restore();
    }

    public static void drawMessagesGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.80f;
        float h = s * 0.62f;
        canvas.drawRoundRect(new RectF(-w/2, -h/2, w/2, h/2), h * 0.26f, h * 0.26f, paint);
        Path tail = new Path();
        tail.moveTo(-w * 0.30f, h * 0.35f);
        tail.lineTo(-w * 0.46f, h * 0.62f);
        tail.lineTo(-w * 0.10f, h * 0.44f);
        tail.close();
        canvas.drawPath(tail, paint);
        Paint cut = new Paint(Paint.ANTI_ALIAS_FLAG);
        cut.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(-w * 0.22f, 0, s * 0.06f, cut);
        canvas.drawCircle(0, 0, s * 0.06f, cut);
        canvas.drawCircle(w * 0.22f, 0, s * 0.06f, cut);
        canvas.restore();
    }

    public static void drawSettingsGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float rOut = s * 0.40f;
        float rIn = s * 0.26f;
        for (int i = 0; i < 8; i++) {
            canvas.save();
            canvas.rotate(i * 45f);
            canvas.drawRoundRect(new RectF(-s * 0.08f, -rOut, s * 0.08f, -rIn), 4f, 4f, paint);
            canvas.restore();
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.14f);
        canvas.drawCircle(0, 0, (rIn + rOut) / 2f, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    public static void drawGalleryGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.80f;
        float h = s * 0.64f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.08f);
        canvas.drawRoundRect(new RectF(-w/2, -h/2, w/2, h/2), h * 0.18f, h * 0.18f, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(-w * 0.22f, -h * 0.14f, s * 0.08f, paint);
        Path mtn = new Path();
        mtn.moveTo(-w * 0.36f, h * 0.34f);
        mtn.lineTo(-w * 0.10f, 0);
        mtn.lineTo(w * 0.10f, h * 0.20f);
        mtn.lineTo(w * 0.22f, h * 0.08f);
        mtn.lineTo(w * 0.36f, h * 0.34f);
        mtn.close();
        canvas.drawPath(mtn, paint);
        canvas.restore();
    }

    public static void drawClockGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float r = s * 0.38f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.09f);
        canvas.drawCircle(0, 0, r, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, s * 0.06f, paint);
        canvas.drawRoundRect(new RectF(0, -s * 0.045f, r * 0.65f, s * 0.045f), s * 0.02f, s * 0.02f, paint);
        canvas.drawRoundRect(new RectF(-s * 0.04f, -r * 0.85f, s * 0.04f, 0), s * 0.02f, s * 0.02f, paint);
        canvas.restore();
    }

    public static void drawCalendarGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.74f;
        float h = s * 0.74f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.08f);
        canvas.drawRoundRect(new RectF(-w/2, -h/2, w/2, h/2), s * 0.12f, s * 0.12f, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(-w/2, -h/2, w/2, -h * 0.15f), s * 0.08f, s * 0.08f, paint);
        canvas.drawRoundRect(new RectF(-w * 0.30f, -h/2 - s * 0.08f, -w * 0.16f, -h/2 + s * 0.06f), 4f, 4f, paint);
        canvas.drawRoundRect(new RectF(w * 0.16f, -h/2 - s * 0.08f, w * 0.30f, -h/2 + s * 0.06f), 4f, 4f, paint);
        float dotR = s * 0.04f;
        canvas.drawCircle(-w * 0.22f, h * 0.10f, dotR, paint);
        canvas.drawCircle(0, h * 0.10f, dotR, paint);
        canvas.drawCircle(w * 0.22f, h * 0.10f, dotR, paint);
        canvas.drawCircle(-w * 0.22f, h * 0.28f, dotR, paint);
        canvas.drawCircle(0, h * 0.28f, dotR, paint);
        canvas.drawCircle(w * 0.22f, h * 0.28f, dotR, paint);
        canvas.restore();
    }

    public static void drawCalculatorGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.72f;
        float h = s * 0.80f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.08f);
        canvas.drawRoundRect(new RectF(-w/2, -h/2, w/2, h/2), s * 0.14f, s * 0.14f, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(-w * 0.38f, -h * 0.40f, w * 0.38f, -h * 0.15f), s * 0.05f, s * 0.05f, paint);
        float bR = s * 0.05f;
        canvas.drawCircle(-w * 0.24f, 0, bR, paint);
        canvas.drawCircle(0, 0, bR, paint);
        canvas.drawCircle(w * 0.24f, 0, bR, paint);
        canvas.drawCircle(-w * 0.24f, h * 0.18f, bR, paint);
        canvas.drawCircle(0, h * 0.18f, bR, paint);
        canvas.drawCircle(w * 0.24f, h * 0.18f, bR, paint);
        canvas.drawCircle(-w * 0.24f, h * 0.34f, bR, paint);
        canvas.drawCircle(0, h * 0.34f, bR, paint);
        canvas.drawCircle(w * 0.24f, h * 0.34f, bR, paint);
        canvas.restore();
    }

    public static void drawNotesGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.68f;
        float h = s * 0.80f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.08f);
        canvas.drawRoundRect(new RectF(-w/2, -h/2, w/2, h/2), s * 0.10f, s * 0.10f, paint);
        paint.setStyle(Paint.Style.FILL);
        float lw = s * 0.05f;
        canvas.drawRoundRect(new RectF(-w * 0.35f, -h * 0.20f, w * 0.35f, -h * 0.20f + lw), lw/2, lw/2, paint);
        canvas.drawRoundRect(new RectF(-w * 0.35f, 0, w * 0.35f, lw), lw/2, lw/2, paint);
        canvas.drawRoundRect(new RectF(-w * 0.35f, h * 0.20f, w * 0.15f, h * 0.20f + lw), lw/2, lw/2, paint);
        canvas.restore();
    }

    public static void drawWeatherGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        canvas.drawCircle(s * 0.15f, -s * 0.15f, s * 0.18f, paint);
        Paint cloudPaint = new Paint(paint);
        cloudPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(-s * 0.12f, s * 0.05f, s * 0.18f, cloudPaint);
        canvas.drawCircle(s * 0.10f, s * 0.08f, s * 0.14f, cloudPaint);
        canvas.drawRoundRect(new RectF(-s * 0.32f, s * 0.08f, s * 0.26f, s * 0.26f), s * 0.09f, s * 0.09f, cloudPaint);
        canvas.restore();
    }

    public static void drawSecurityGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.74f;
        float h = s * 0.82f;
        Path shield = new Path();
        shield.moveTo(0, -h/2);
        shield.lineTo(w/2, -h * 0.35f);
        shield.cubicTo(w/2, h * 0.15f, 0, h * 0.45f, 0, h/2);
        shield.cubicTo(0, h * 0.45f, -w/2, h * 0.15f, -w/2, -h * 0.35f);
        shield.close();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.08f);
        canvas.drawPath(shield, paint);
        paint.setStrokeWidth(s * 0.09f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        Path check = new Path();
        check.moveTo(-w * 0.20f, 0);
        check.lineTo(-w * 0.05f, h * 0.14f);
        check.lineTo(w * 0.22f, -h * 0.14f);
        canvas.drawPath(check, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    public static void drawFolderGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.80f;
        float h = s * 0.60f;
        Path path = new Path();
        path.moveTo(-w/2, -h/2 + s * 0.06f);
        path.lineTo(-w * 0.15f, -h/2 + s * 0.06f);
        path.lineTo(-w * 0.05f, -h/2);
        path.lineTo(w/2 - s * 0.06f, -h/2);
        path.cubicTo(w/2, -h/2, w/2, -h/2 + s * 0.06f, w/2, -h/2 + s * 0.06f);
        path.lineTo(w/2, h/2 - s * 0.06f);
        path.cubicTo(w/2, h/2, w/2 - s * 0.06f, h/2, w/2 - s * 0.06f, h/2);
        path.lineTo(-w/2 + s * 0.06f, h/2);
        path.cubicTo(-w/2, h/2, -w/2, h/2 - s * 0.06f, -w/2, h/2 - s * 0.06f);
        path.close();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    public static void drawMusicGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        canvas.drawCircle(-s * 0.12f, s * 0.18f, s * 0.14f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.08f);
        Path stem = new Path();
        stem.moveTo(0, s * 0.18f);
        stem.lineTo(0, -s * 0.28f);
        stem.cubicTo(s * 0.20f, -s * 0.28f, s * 0.30f, -s * 0.10f, s * 0.30f, 0);
        canvas.drawPath(stem, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    public static void drawRecorderGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.24f;
        float h = s * 0.48f;
        canvas.drawRoundRect(new RectF(-w/2, -h/2 - s * 0.06f, w/2, h/2 - s * 0.06f), w/2, w/2, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.07f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF arc = new RectF(-w * 0.9f, -s * 0.14f, w * 0.9f, s * 0.22f);
        canvas.drawArc(arc, 0, 180, false, paint);
        canvas.drawLine(0, s * 0.22f, 0, s * 0.34f, paint);
        canvas.drawLine(-s * 0.15f, s * 0.34f, s * 0.15f, s * 0.34f, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    public static void drawCompassGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float r = s * 0.38f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.08f);
        canvas.drawCircle(0, 0, r, paint);
        paint.setStyle(Paint.Style.FILL);
        Path needle = new Path();
        needle.moveTo(0, -r * 0.75f);
        needle.lineTo(s * 0.10f, 0);
        needle.lineTo(0, r * 0.75f);
        needle.lineTo(-s * 0.10f, 0);
        needle.close();
        canvas.drawPath(needle, paint);
        canvas.restore();
    }

    public static void drawBrowserGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float r = s * 0.36f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.07f);
        canvas.drawCircle(0, 0, r, paint);
        canvas.drawLine(-r, 0, r, 0, paint);
        canvas.drawOval(new RectF(-r * 0.45f, -r, r * 0.45f, r), paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    public static void drawTikTokGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        canvas.drawCircle(-s * 0.08f, s * 0.16f, s * 0.15f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.10f);
        Path note = new Path();
        note.moveTo(s * 0.07f, s * 0.16f);
        note.lineTo(s * 0.07f, -s * 0.32f);
        note.cubicTo(s * 0.07f, -s * 0.16f, s * 0.22f, -s * 0.08f, s * 0.34f, -s * 0.08f);
        canvas.drawPath(note, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    public static void drawTelegramGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        Path plane = new Path();
        plane.moveTo(s * 0.34f, -s * 0.28f);
        plane.lineTo(-s * 0.32f, -s * 0.02f);
        plane.lineTo(-s * 0.06f, s * 0.14f);
        plane.lineTo(s * 0.24f, -s * 0.18f);
        plane.lineTo(-s * 0.02f, s * 0.18f);
        plane.lineTo(0, s * 0.32f);
        plane.lineTo(s * 0.10f, s * 0.20f);
        plane.lineTo(s * 0.34f, -s * 0.28f);
        plane.close();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(plane, paint);
        canvas.restore();
    }

    public static void drawYouTubeGlyph(Canvas canvas, Paint paint, float cx, float cy, float s) {
        canvas.save();
        canvas.translate(cx, cy);
        float w = s * 0.78f;
        float h = s * 0.54f;
        canvas.drawRoundRect(new RectF(-w/2, -h/2, w/2, h/2), h * 0.30f, h * 0.30f, paint);
        Paint cut = new Paint(Paint.ANTI_ALIAS_FLAG);
        cut.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Path tri = new Path();
        tri.moveTo(-w * 0.12f, -h * 0.24f);
        tri.lineTo(w * 0.18f, 0);
        tri.lineTo(-w * 0.12f, h * 0.24f);
        tri.close();
        canvas.drawPath(tri, cut);
        canvas.restore();
    }

    // ==================== Render App Icon (Complete Plate + Normalized Glyph) ====================

    public static Bitmap renderAppIcon(Context context, Drawable icon, int shapeIndex, int themeIndex,
                                      int styleIndex, int opacityPercent, int fillPercent, int size) {
        return renderAppIcon(context, icon, null, null, shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, 36, size, 0, false, 0, false);
    }

    public static Bitmap renderAppIcon(Context context, Drawable icon, String pkg, String cls,
                                      int shapeIndex, int themeIndex, int styleIndex,
                                      int opacityPercent, int fillPercent, int size) {
        return renderAppIcon(context, icon, pkg, cls, shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, 36, size, 0, false, 0, false);
    }

    public static Bitmap renderAppIcon(Context context, Drawable icon, String pkg, String cls,
                                      int shapeIndex, int themeIndex, int styleIndex,
                                      int opacityPercent, int fillPercent, int cornerRadius, int size) {
        return renderAppIcon(context, icon, pkg, cls, shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, cornerRadius, size, 0, false, 0, false);
    }

    public static Bitmap renderAppIcon(Context context, Drawable icon, String pkg, String cls,
                                      int shapeIndex, int themeIndex, int styleIndex,
                                      int opacityPercent, int fillPercent, int cornerRadius, int size,
                                      int customPlateColor, boolean useCustomPlate,
                                      int customGlyphColor, boolean useCustomGlyph) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 1. Draw Plate (no borders)
        Path path = createShapePath(shapeIndex, size, size, cornerRadius);
        int plateColor;
        if (useCustomPlate) {
            int alpha = (int) (255 * (opacityPercent / 100f));
            alpha = Math.max(0, Math.min(255, alpha));
            plateColor = Color.argb(alpha, Color.red(customPlateColor), Color.green(customPlateColor), Color.blue(customPlateColor));
        } else {
            plateColor = getPlateColor(context, themeIndex, opacityPercent);
        }
        if (Color.alpha(plateColor) > 0) {
            Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fillPaint.setColor(plateColor);
            fillPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, fillPaint);
        }

        // 2. Optical visual fill fraction (consistent across ALL apps)
        float visualFill = (fillPercent / 100f) * 0.72f;
        float gs = size * visualFill;
        float cx = size / 2f;
        float cy = size / 2f;

        if (styleIndex == 1) {
            // Style 1: "Original Stock Icons"
            if (icon != null) {
                Bitmap raw = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas rawCanvas = new Canvas(raw);
                icon.setBounds(0, 0, size, size);
                icon.draw(rawCanvas);

                Bitmap norm = normalizeAndCenterBitmap(raw, size, visualFill);
                canvas.save();
                canvas.clipPath(path);
                canvas.drawBitmap(norm != null ? norm : raw, 0, 0, null);
                canvas.restore();
            }
            return bitmap;
        }

        // Style 0: "Themed Monochrome (RGB Palette)"
        int glyphColor = useCustomGlyph ? ((0xFF << 24) | (customGlyphColor & 0x00FFFFFF)) : getMonetGlyphColor(context, themeIndex);

        String p = (pkg != null) ? pkg.toLowerCase() : "";
        String c = (cls != null) ? cls.toLowerCase() : "";

        Paint glyphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glyphPaint.setColor(glyphColor);
        glyphPaint.setStyle(Paint.Style.FILL);

        // Recognized System Apps & Top Apps -> 100% deterministic vector glyphs!
        if (c.contains("twelvekeydialer") || c.contains("dialtacts") || p.contains("dialer") ||
                (p.contains("contacts") && (c.contains("dial") || c.contains("twelve") || c.contains("call")))) {
            drawPhoneGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("contacts") && (c.contains("people") || c.contains("contact"))) {
            drawContactsGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("camera")) {
            drawCameraGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("mms") || p.contains("messaging")) {
            drawMessagesGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("settings")) {
            drawSettingsGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("gallery")) {
            drawGalleryGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("deskclock") || p.contains("clock")) {
            drawClockGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("calendar")) {
            drawCalendarGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("calculator")) {
            drawCalculatorGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("notes")) {
            drawNotesGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("weather")) {
            drawWeatherGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("security") || p.contains("cleaner") || p.contains("cleanmaster")) {
            drawSecurityGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("fileexplorer") || p.contains("files") || p.contains("documentsui")) {
            drawFolderGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("music") || p.contains("player")) {
            drawMusicGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("soundrecorder") || p.contains("recorder")) {
            drawRecorderGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("compass")) {
            drawCompassGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("browser") || p.contains("chrome")) {
            drawBrowserGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("musically") || p.contains("trill") || p.contains("tiktok") || p.contains("aweme")) {
            drawTikTokGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("telegram")) {
            drawTelegramGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        } else if (p.contains("youtube")) {
            drawYouTubeGlyph(canvas, glyphPaint, cx, cy, gs);
            return bitmap;
        }

        if (icon == null) return bitmap;

        // Check official monochrome
        Drawable mono = null;
        if (Build.VERSION.SDK_INT >= 33 && icon instanceof AdaptiveIconDrawable) {
            try {
                mono = ((AdaptiveIconDrawable) icon).getMonochrome();
            } catch (Throwable ignored) {}
        }

        if (mono != null) {
            try {
                Bitmap rawMono = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas mc = new Canvas(rawMono);
                Drawable m = mono.mutate();
                m.setTint(glyphColor);
                m.setBounds(0, 0, size, size);
                m.draw(mc);

                Bitmap normMono = normalizeAndCenterBitmap(rawMono, size, visualFill);
                canvas.drawBitmap(normMono != null ? normMono : rawMono, 0, 0, null);
                return bitmap;
            } catch (Throwable ignored) {}
        }

        // Try smart extraction on foreground / icon
        try {
            if (icon instanceof AdaptiveIconDrawable) {
                Drawable fg = ((AdaptiveIconDrawable) icon).getForeground();
                if (fg != null) {
                    Bitmap fgBmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    Canvas fgc = new Canvas(fgBmp);
                    Drawable clone = (fg.getConstantState() != null) ? fg.getConstantState().newDrawable().mutate() : fg;
                    clone.setColorFilter(new PorterDuffColorFilter(glyphColor, PorterDuff.Mode.SRC_IN));
                    clone.setBounds(0, 0, size, size);
                    clone.draw(fgc);

                    Bitmap themedMono = extractSmartMonochrome(fgBmp, glyphColor);
                    Bitmap normMono = normalizeAndCenterBitmap(themedMono != null ? themedMono : fgBmp, size, visualFill);
                    canvas.drawBitmap(normMono != null ? normMono : fgBmp, 0, 0, null);
                    return bitmap;
                }
            } else if (icon != null) {
                Bitmap temp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas tempCanvas = new Canvas(temp);
                icon.setBounds(0, 0, size, size);
                icon.draw(tempCanvas);

                Bitmap themedMono = extractSmartMonochrome(temp, glyphColor);
                if (themedMono != null) {
                    Bitmap normMono = normalizeAndCenterBitmap(themedMono, size, visualFill);
                    canvas.drawBitmap(normMono != null ? normMono : themedMono, 0, 0, null);
                    return bitmap;
                }
            }
        } catch (Throwable ignored) {}

        // Monogram letter fallback (AOSP Themed Icon style) - guarantees NO nested squircle or extra border!
        String letter = "?";
        if (pkg != null && !pkg.isEmpty()) {
            String[] parts = pkg.split("\\.");
            String last = parts[parts.length - 1];
            if (!last.isEmpty()) {
                letter = last.substring(0, 1).toUpperCase();
            }
        }
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(glyphColor);
        textPaint.setTextSize(gs * 0.75f);
        textPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float baseline = cy - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(letter, cx, baseline, textPaint);

        return bitmap;
    }

    /**
     * Renders just the foreground glyph on a transparent canvas (for HyperOS dynamic layered icons: 1.png).
     */
    public static Bitmap renderGlyphOnly(Context context, String pkg, String cls,
                                        int styleIndex, int fillPercent, int size) {
        return renderGlyphOnly(context, pkg, cls, styleIndex, fillPercent, size, 0, false);
    }

    public static Bitmap renderGlyphOnly(Context context, String pkg, String cls,
                                        int styleIndex, int fillPercent, int size,
                                        int customGlyphColor, boolean useCustomGlyph) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float visualFill = (fillPercent / 100f) * 0.72f;
        float gs = size * visualFill;
        float cx = size / 2f;
        float cy = size / 2f;

        int glyphColor;
        if (useCustomGlyph) {
            glyphColor = (0xFF << 24) | (customGlyphColor & 0x00FFFFFF);
        } else if (styleIndex == 1) {
            glyphColor = getMonetGlyphColor(context);
        } else {
            glyphColor = 0xFFF1F5F9;
        }
        Paint glyphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glyphPaint.setColor(glyphColor);
        glyphPaint.setStyle(Paint.Style.FILL);

        String p = (pkg != null) ? pkg.toLowerCase() : "";
        String c = (cls != null) ? cls.toLowerCase() : "";

        if (c.contains("twelvekeydialer") || c.contains("dialtacts") || p.contains("dialer") ||
                (p.contains("contacts") && (c.contains("dial") || c.contains("twelve") || c.contains("call")))) {
            drawPhoneGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("contacts") && (c.contains("people") || c.contains("contact"))) {
            drawContactsGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("camera")) {
            drawCameraGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("mms") || p.contains("messaging")) {
            drawMessagesGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("settings")) {
            drawSettingsGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("gallery")) {
            drawGalleryGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("deskclock") || p.contains("clock")) {
            drawClockGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("calendar")) {
            drawCalendarGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("calculator")) {
            drawCalculatorGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("notes")) {
            drawNotesGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("weather")) {
            drawWeatherGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("security") || p.contains("cleaner") || p.contains("cleanmaster")) {
            drawSecurityGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("fileexplorer") || p.contains("files") || p.contains("documentsui")) {
            drawFolderGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("music") || p.contains("player")) {
            drawMusicGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("soundrecorder") || p.contains("recorder")) {
            drawRecorderGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("compass")) {
            drawCompassGlyph(canvas, glyphPaint, cx, cy, gs);
        } else if (p.contains("browser") || p.contains("chrome")) {
            drawBrowserGlyph(canvas, glyphPaint, cx, cy, gs);
        }

        return bitmap;
    }

    private static Bitmap extractSmartMonochrome(Bitmap src, int tintColor) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[w * h];
        src.getPixels(pixels, 0, w, 0, 0, w, h);

        int tr = Color.red(tintColor);
        int tg = Color.green(tintColor);
        int tb = Color.blue(tintColor);

        // Find bounding box and count opaque pixels
        int minX = w, maxX = 0, minY = h, maxY = 0;
        int opaqueCount = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int a = (pixels[y * w + x] >>> 24) & 0xff;
                if (a > 30) {
                    opaqueCount++;
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (opaqueCount < 20 || maxX <= minX || maxY <= minY) {
            return null;
        }

        int bw = maxX - minX + 1;
        int bh = maxY - minY + 1;
        float bboxArea = (float) (bw * bh);
        float fillRatio = (float) opaqueCount / bboxArea;

        // Sample perimeter points inside the bounding box
        int insetX = Math.max(2, bw / 10);
        int insetY = Math.max(2, bh / 10);

        int[] sampleX = {
            minX + insetX, minX + bw / 2, maxX - insetX,
            minX + insetX,                maxX - insetX,
            minX + insetX, minX + bw / 2, maxX - insetX
        };
        int[] sampleY = {
            minY + insetY, minY + insetY, minY + insetY,
            minY + bh / 2,                minY + bh / 2,
            maxY - insetY, maxY - insetY, maxY - insetY
        };

        int borderOpaqueCount = 0;
        for (int i = 0; i < sampleX.length; i++) {
            int p = pixels[sampleY[i] * w + sampleX[i]];
            if (((p >>> 24) & 0xff) > 40) {
                borderOpaqueCount++;
            }
        }

        boolean isContainer = (fillRatio >= 0.65f) || (borderOpaqueCount >= 5);

        if (!isContainer) {
            // Cutout emblem on transparent background! Tint visible pixels
            Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            int visibleCount = 0;
            for (int i = 0; i < pixels.length; i++) {
                int a = (pixels[i] >>> 24) & 0xff;
                if (a > 20) {
                    visibleCount++;
                    pixels[i] = (a << 24) | (tr << 16) | (tg << 8) | tb;
                } else {
                    pixels[i] = 0;
                }
            }
            if (visibleCount > 15) {
                result.setPixels(pixels, 0, w, 0, 0, w, h);
                return result;
            }
            return null;
        }

        // Solid container with inner logo: Extract emblem via bilinear background subtraction
        int cTL = pixels[(minY + insetY) * w + (minX + insetX)];
        int cTR = pixels[(minY + insetY) * w + (maxX - insetX)];
        int cBL = pixels[(maxY - insetY) * w + (minX + insetX)];
        int cBR = pixels[(maxY - insetY) * w + (maxX - insetX)];

        int tlR = (cTL >> 16) & 0xff, tlG = (cTL >> 8) & 0xff, tlB = cTL & 0xff;
        int trR = (cTR >> 16) & 0xff, trG = (cTR >> 8) & 0xff, trB = cTR & 0xff;
        int blR = (cBL >> 16) & 0xff, blG = (cBL >> 8) & 0xff, blB = cBL & 0xff;
        int brR = (cBR >> 16) & 0xff, brG = (cBR >> 8) & 0xff, brB = cBR & 0xff;

        int[] candidatePixels = new int[w * h];
        int glyphPixelCount = 0;

        // Container boundary protection: Inner safe zone
        // Discards the outer perimeter of the container to eliminate squircle/circle antialiased border rings,
        // bevels, and drop-shadows completely (eliminates the double border / outline bug!)
        float cx = (minX + maxX) / 2f;
        float cy = (minY + maxY) / 2f;
        float maxAllowedRadiusX = (bw / 2f) * 0.82f;
        float maxAllowedRadiusY = (bh / 2f) * 0.82f;
        int padX = Math.max(4, (int) (bw * 0.09f));
        int padY = Math.max(4, (int) (bh * 0.09f));

        for (int y = minY + padY; y <= maxY - padY; y++) {
            float v = (bh > 1) ? (float) (y - minY) / (bh - 1) : 0f;
            float dy = y - cy;
            for (int x = minX + padX; x <= maxX - padX; x++) {
                float dx = x - cx;
                if ((dx * dx) / (maxAllowedRadiusX * maxAllowedRadiusX) + (dy * dy) / (maxAllowedRadiusY * maxAllowedRadiusY) > 1.0f) {
                    continue;
                }

                float u = (bw > 1) ? (float) (x - minX) / (bw - 1) : 0f;
                int idx = y * w + x;
                int p = pixels[idx];
                int a = (p >>> 24) & 0xff;
                if (a < 35) {
                    continue;
                }

                int pr = (p >> 16) & 0xff;
                int pg = (p >> 8) & 0xff;
                int pb = p & 0xff;

                // Bilinear interpolated background color at (x, y)
                float bgR = (1 - u) * (1 - v) * tlR + u * (1 - v) * trR + (1 - u) * v * blR + u * v * brR;
                float bgG = (1 - u) * (1 - v) * tlG + u * (1 - v) * trG + (1 - u) * v * blG + u * v * brG;
                float bgB = (1 - u) * (1 - v) * tlB + u * (1 - v) * trB + (1 - u) * v * blB + u * v * brB;

                float diff = Math.abs(pr - bgR) + Math.abs(pg - bgG) + Math.abs(pb - bgB);

                if (diff >= 42f) {
                    glyphPixelCount++;
                    int symbolAlpha = Math.min(255, (int) ((diff - 42f) * 6f));
                    int finalAlpha = (a * symbolAlpha) / 255;
                    candidatePixels[idx] = (finalAlpha << 24) | (tr << 16) | (tg << 8) | tb;
                }
            }
        }

        float glyphRatio = (float) glyphPixelCount / bboxArea;
        if (glyphRatio >= 0.008f && glyphRatio <= 0.60f) {
            Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            result.setPixels(candidatePixels, 0, w, 0, 0, w, h);
            return result;
        }

        // Fallback: Luminance contrast extraction around center (also protected from outer rim)
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        int centerSum = 0, centerCount = 0;
        int cyStart = Math.max(minY, (int) (cy - bh / 4));
        int cyEnd = Math.min(maxY, (int) (cy + bh / 4));
        int cxStart = Math.max(minX, (int) (cx - bw / 4));
        int cxEnd = Math.min(maxX, (int) (cx + bw / 4));

        for (int y = cyStart; y <= cyEnd; y++) {
            for (int x = cxStart; x <= cxEnd; x++) {
                int p = pixels[y * w + x];
                centerSum += (((p >> 16) & 0xff) + ((p >> 8) & 0xff) + (p & 0xff)) / 3;
                centerCount++;
            }
        }
        int avgLuma = centerCount > 0 ? centerSum / centerCount : 128;

        int[] lumaPixels = new int[w * h];
        int lumaCount = 0;
        for (int y = minY + padY; y <= maxY - padY; y++) {
            float dy = y - cy;
            for (int x = minX + padX; x <= maxX - padX; x++) {
                float dx = x - cx;
                if ((dx * dx) / (maxAllowedRadiusX * maxAllowedRadiusX) + (dy * dy) / (maxAllowedRadiusY * maxAllowedRadiusY) > 1.0f) {
                    continue;
                }
                int idx = y * w + x;
                int p = pixels[idx];
                int a = (p >>> 24) & 0xff;
                if (a < 35) continue;
                int luma = (((p >> 16) & 0xff) + ((p >> 8) & 0xff) + (p & 0xff)) / 3;
                int diff = Math.abs(luma - avgLuma);
                if (diff > 25) {
                    lumaCount++;
                    int alpha = Math.min(255, diff * 5);
                    lumaPixels[idx] = (alpha << 24) | (tr << 16) | (tg << 8) | tb;
                }
            }
        }

        float lumaRatio = (float) lumaCount / bboxArea;
        if (lumaRatio >= 0.008f && lumaRatio <= 0.60f) {
            result.setPixels(lumaPixels, 0, w, 0, 0, w, h);
            return result;
        }

        // Return null to allow renderAppIcon to safely render the crisp stock original icon!
        return null;
    }

    // ==================== Build Theme Zip for HyperOS ====================

    public static File buildHyperOSThemeZip(Context context, int shapeIndex, int themeIndex,
                                           int styleIndex, int opacityPercent, int fillPercent) throws Exception {
        return buildHyperOSThemeZip(context, shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, 36, 0, false, 0, false);
    }

    public static File buildHyperOSThemeZip(Context context, int shapeIndex, int themeIndex,
                                           int styleIndex, int opacityPercent, int fillPercent,
                                           int cornerRadius) throws Exception {
        return buildHyperOSThemeZip(context, shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, cornerRadius, 0, false, 0, false);
    }

    public static File buildHyperOSThemeZip(Context context, int shapeIndex, int themeIndex,
                                           int styleIndex, int opacityPercent, int fillPercent,
                                           int cornerRadius,
                                           int customPlateColor, boolean useCustomPlate,
                                           int customGlyphColor, boolean useCustomGlyph) throws Exception {
        File outFile = new File(context.getCacheDir(), "hyperos_icons.zip");
        if (outFile.exists()) outFile.delete();

        int size = 235; // Standard flat icon size

        Bitmap plateBitmap = createPlateBitmap(context, shapeIndex, themeIndex, opacityPercent, cornerRadius, size, customPlateColor, useCustomPlate);
        Bitmap maskBitmap = createMaskBitmap(shapeIndex, cornerRadius, size);
        Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        ByteArrayOutputStream plateBytes = new ByteArrayOutputStream();
        plateBitmap.compress(Bitmap.CompressFormat.PNG, 100, plateBytes);

        ByteArrayOutputStream maskBytes = new ByteArrayOutputStream();
        maskBitmap.compress(Bitmap.CompressFormat.PNG, 100, maskBytes);

        ByteArrayOutputStream emptyBytes = new ByteArrayOutputStream();
        emptyBitmap.compress(Bitmap.CompressFormat.PNG, 100, emptyBytes);

        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        Set<String> added = new HashSet<String>();

        // 1. Master compositing templates (clean, NO BORDER, NO BACKGROUND DUPLICATION)
        writeZipEntry(zos, "icon_background.png", emptyBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_mask.png", emptyBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_border.png", emptyBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_pattern.png", emptyBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_folder.png", plateBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_folder_light.png", plateBytes.toByteArray(), added);
        writeZipEntry(zos, "sym_def_app_icon.png", plateBytes.toByteArray(), added);

        // transform_config.xml:
        // SupportLayerIcon=false: Ensures HyperOS renders all icons as pure transparent flat icons
        // without forcing AdaptiveIconDrawable (which draws solid black underneath semi-transparent layers!)
        // UseDynamicIcon=false: Disables live animated MAML scripts so our Monet clock & calendar display cleanly!
        String xmlConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<IconTransform>\n" +
                "    <Config name=\"SupportLayerIcon\" value=\"false\" />\n" +
                "    <Config name=\"UseDynamicIcon\" value=\"false\" />\n" +
                "</IconTransform>";
        writeZipEntry(zos, "transform_config.xml", xmlConfig.getBytes("UTF-8"), added);

        PackageManager pm = context.getPackageManager();

        // 2. FLAT ICONS: Pre-generate dedicated Phone / Dialer and Contacts icons
        Drawable dialerStock = null;
        try {
            dialerStock = pm.getActivityIcon(new ComponentName("com.android.contacts", "com.android.contacts.activities.TwelveKeyDialer"));
        } catch (Throwable t) {
            try {
                dialerStock = pm.getApplicationIcon("com.android.contacts");
            } catch (Throwable ignored) {}
        }

        Bitmap phoneIcon = renderAppIcon(context, dialerStock, "com.android.contacts", "com.android.contacts.activities.TwelveKeyDialer",
                shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, cornerRadius, size,
                customPlateColor, useCustomPlate, customGlyphColor, useCustomGlyph);
        ByteArrayOutputStream phoneStream = new ByteArrayOutputStream();
        phoneIcon.compress(Bitmap.CompressFormat.PNG, 100, phoneStream);
        byte[] phoneBytes = phoneStream.toByteArray();

        Drawable contactsStock = null;
        try {
            contactsStock = pm.getActivityIcon(new ComponentName("com.android.contacts", "com.android.contacts.activities.PeopleActivity"));
        } catch (Throwable t) {
            try {
                contactsStock = pm.getApplicationIcon("com.android.contacts");
            } catch (Throwable ignored) {}
        }

        Bitmap contactsIcon = renderAppIcon(context, contactsStock, "com.android.contacts", "com.android.contacts.activities.PeopleActivity",
                shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, cornerRadius, size,
                customPlateColor, useCustomPlate, customGlyphColor, useCustomGlyph);
        ByteArrayOutputStream contactsStream = new ByteArrayOutputStream();
        contactsIcon.compress(Bitmap.CompressFormat.PNG, 100, contactsStream);
        byte[] contactsBytes = contactsStream.toByteArray();

        // Write Phone to all possible entries
        writeZipEntry(zos, "com.android.contacts.activities.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.activities.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.activities.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.activities.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.DialtactsContactsEntryActivity.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.DialtactsContactsEntryActivity", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.DialtactsContactsEntryActivity.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.DialtactsContactsEntryActivity", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.DialtactsContactsEntryActivity.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.DialtactsContactsEntryActivity", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.DialtactsContactsEntryActivity.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.DialtactsContactsEntryActivity", phoneBytes, added);
        writeZipEntry(zos, "TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "phone.png", phoneBytes, added);
        writeZipEntry(zos, "phone", phoneBytes, added);
        writeZipEntry(zos, "dialer.png", phoneBytes, added);
        writeZipEntry(zos, "dialer", phoneBytes, added);
        writeZipEntry(zos, "com.xiaomi.phone.png", phoneBytes, added);
        writeZipEntry(zos, "com.xiaomi.phone", phoneBytes, added);
        writeZipEntry(zos, "com.google.android.dialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.google.android.dialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone", phoneBytes, added);
        writeZipEntry(zos, "com.android.providers.contacts.CallLogProvider.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.providers.contacts.CallLogProvider", phoneBytes, added);

        // Write Contacts to all possible entries
        writeZipEntry(zos, "com.android.contacts.png", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts", contactsBytes, added);
        writeZipEntry(zos, "com.android.providers.contacts.png", contactsBytes, added);
        writeZipEntry(zos, "com.android.providers.contacts", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts.activities.PeopleActivity.png", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts.activities.PeopleActivity", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts/.activities.PeopleActivity.png", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts/.activities.PeopleActivity", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.PeopleActivity.png", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.PeopleActivity", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.PeopleActivity.png", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.PeopleActivity", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts.PeopleActivity.png", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts.PeopleActivity", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts/.PeopleActivity.png", contactsBytes, added);
        writeZipEntry(zos, "com.android.contacts/.PeopleActivity", contactsBytes, added);
        writeZipEntry(zos, "contacts.png", contactsBytes, added);
        writeZipEntry(zos, "contacts", contactsBytes, added);

        // Pre-generate Clock and Calendar flat entries
        Bitmap clockIcon = renderAppIcon(context, null, "com.android.deskclock", "com.android.deskclock.DeskClockTabActivity",
                shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, cornerRadius, size,
                customPlateColor, useCustomPlate, customGlyphColor, useCustomGlyph);
        ByteArrayOutputStream clockStream = new ByteArrayOutputStream();
        clockIcon.compress(Bitmap.CompressFormat.PNG, 100, clockStream);
        byte[] clockBytes = clockStream.toByteArray();
        writeZipEntry(zos, "com.android.deskclock.png", clockBytes, added);
        writeZipEntry(zos, "com.android.deskclock", clockBytes, added);
        writeZipEntry(zos, "deskclock.png", clockBytes, added);
        writeZipEntry(zos, "deskclock", clockBytes, added);
        writeZipEntry(zos, "clock.png", clockBytes, added);
        writeZipEntry(zos, "clock", clockBytes, added);
        writeZipEntry(zos, "com.android.deskclock/.DeskClockTabActivity.png", clockBytes, added);
        writeZipEntry(zos, "com.android.deskclock/.DeskClockTabActivity", clockBytes, added);

        Bitmap calIcon = renderAppIcon(context, null, "com.xiaomi.calendar", "com.android.calendar.homepage.AllInOneActivity",
                shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, cornerRadius, size,
                customPlateColor, useCustomPlate, customGlyphColor, useCustomGlyph);
        ByteArrayOutputStream calStream = new ByteArrayOutputStream();
        calIcon.compress(Bitmap.CompressFormat.PNG, 100, calStream);
        byte[] calBytes = calStream.toByteArray();
        writeZipEntry(zos, "com.xiaomi.calendar.png", calBytes, added);
        writeZipEntry(zos, "com.xiaomi.calendar", calBytes, added);
        writeZipEntry(zos, "com.android.calendar.png", calBytes, added);
        writeZipEntry(zos, "com.android.calendar", calBytes, added);
        writeZipEntry(zos, "calendar.png", calBytes, added);
        writeZipEntry(zos, "calendar", calBytes, added);

        // 3. Query ALL installed launcher applications on the device
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

                    Bitmap themedBitmap = renderAppIcon(context, stockIcon, pkg, cls, shapeIndex, themeIndex,
                            styleIndex, opacityPercent, fillPercent, cornerRadius, size,
                            customPlateColor, useCustomPlate, customGlyphColor, useCustomGlyph);
                    ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                    themedBitmap.compress(Bitmap.CompressFormat.PNG, 100, iconStream);
                    byte[] iconBytes = iconStream.toByteArray();

                    writeAppEntries(zos, iconBytes, pkg, cls, added);
                } catch (Throwable ignored) {}
            }
        }

        // 4. Explicitly ensure core HyperOS / MIUI system packages are included
        String[] criticalPkgs = {
                "com.android.contacts",
                "com.google.android.dialer",
                "com.android.phone",
                "com.xiaomi.phone",
                "com.miui.gallery",
                "com.android.gallery",
                "com.android.gallery3d",
                "com.android.camera",
                "com.android.mms",
                "com.google.android.apps.messaging",
                "com.android.settings",
                "com.xiaomi.misettings",
                "com.miui.securitycenter",
                "com.android.fileexplorer",
                "com.mi.android.globalFileexplorer",
                "com.android.deskclock",
                "com.android.calendar",
                "com.xiaomi.calendar",
                "com.miui.weather2",
                "com.miui.notes",
                "com.miui.calculator",
                "com.miui.compass",
                "com.miui.player",
                "com.android.soundrecorder",
                "com.android.browser",
                "com.mi.globalbrowser"
        };

        for (String cPkg : criticalPkgs) {
            try {
                if (!added.contains(cPkg) || !added.contains(cPkg + ".png")) {
                    Drawable cIcon = pm.getApplicationIcon(cPkg);
                    Bitmap themedBitmap = renderAppIcon(context, cIcon, cPkg, null, shapeIndex, themeIndex,
                            styleIndex, opacityPercent, fillPercent, cornerRadius, size,
                            customPlateColor, useCustomPlate, customGlyphColor, useCustomGlyph);
                    ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                    themedBitmap.compress(Bitmap.CompressFormat.PNG, 100, iconStream);
                    byte[] iconBytes = iconStream.toByteArray();

                    writeAppEntries(zos, iconBytes, cPkg, null, added);
                }
            } catch (Throwable ignored) {}
        }

        // 5. Query ALL installed packages on the device so NO app is ever missed!
        try {
            List<android.content.pm.PackageInfo> allInstalled = pm.getInstalledPackages(0);
            if (allInstalled != null) {
                for (android.content.pm.PackageInfo pInfo : allInstalled) {
                    if (pInfo == null || pInfo.packageName == null) continue;
                    String ipkg = pInfo.packageName;
                    if (!added.contains(ipkg) || !added.contains(ipkg + ".png")) {
                        try {
                            Drawable cIcon = pm.getApplicationIcon(ipkg);
                            Bitmap themedBitmap = renderAppIcon(context, cIcon, ipkg, null, shapeIndex, themeIndex,
                                    styleIndex, opacityPercent, fillPercent, cornerRadius, size,
                                    customPlateColor, useCustomPlate, customGlyphColor, useCustomGlyph);
                            ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                            themedBitmap.compress(Bitmap.CompressFormat.PNG, 100, iconStream);
                            byte[] iconBytes = iconStream.toByteArray();

                            writeAppEntries(zos, iconBytes, ipkg, null, added);
                        } catch (Throwable ignored) {}
                    }
                }
            }
        } catch (Throwable ignored) {}

        zos.close();
        return outFile;
    }

    private static void writeAppEntries(ZipOutputStream zos, byte[] iconBytes, String pkg, String cls,
                                        Set<String> added) throws IOException {
        if (iconBytes == null || iconBytes.length == 0) return;

        // 1. Direct package & class lookup
        if (pkg != null && !pkg.isEmpty()) {
            writeZipEntry(zos, pkg, iconBytes, added);
            writeZipEntry(zos, pkg + ".png", iconBytes, added);
        }
        if (cls != null && !cls.isEmpty()) {
            writeZipEntry(zos, cls + ".png", iconBytes, added);
            writeZipEntry(zos, cls, iconBytes, added);
            if (pkg != null) {
                writeZipEntry(zos, pkg + "/" + cls + ".png", iconBytes, added);
                writeZipEntry(zos, pkg + "/" + cls, iconBytes, added);
                writeZipEntry(zos, pkg + "#" + cls + ".png", iconBytes, added);
                writeZipEntry(zos, pkg + "#" + cls, iconBytes, added);

                if (cls.startsWith(pkg)) {
                    String shortCls = cls.substring(pkg.length());
                    writeZipEntry(zos, pkg + "/" + shortCls + ".png", iconBytes, added);
                    writeZipEntry(zos, pkg + "/" + shortCls, iconBytes, added);
                }
            }
        }

        if (pkg == null) return;
        String p = pkg.toLowerCase();
        String c = (cls != null) ? cls.toLowerCase() : "";

        // Phone / Dialer
        if (p.contains("contacts") || p.contains("dialer") || p.contains("phone")) {
            if (c.contains("dial") || c.contains("twelve") || (!c.contains("people") && !c.contains("contact"))) {
                writeZipEntry(zos, "phone.png", iconBytes, added);
                writeZipEntry(zos, "phone", iconBytes, added);
                writeZipEntry(zos, "dialer.png", iconBytes, added);
                writeZipEntry(zos, "dialer", iconBytes, added);
                writeZipEntry(zos, "TwelveKeyDialer.png", iconBytes, added);
                writeZipEntry(zos, "TwelveKeyDialer", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts.activities.TwelveKeyDialer.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts.activities.TwelveKeyDialer", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.activities.TwelveKeyDialer.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.activities.TwelveKeyDialer", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.TwelveKeyDialer.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.TwelveKeyDialer", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.TwelveKeyDialer.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.TwelveKeyDialer", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.TwelveKeyDialer.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.TwelveKeyDialer", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts.TwelveKeyDialer.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts.TwelveKeyDialer", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.DialtactsContactsEntryActivity.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.DialtactsContactsEntryActivity", iconBytes, added);
                writeZipEntry(zos, "com.android.phone.png", iconBytes, added);
                writeZipEntry(zos, "com.android.phone", iconBytes, added);
                writeZipEntry(zos, "com.android.providers.contacts.CallLogProvider.png", iconBytes, added);
                writeZipEntry(zos, "com.android.providers.contacts.CallLogProvider", iconBytes, added);
            }
            if (c.contains("people") || c.contains("contact")) {
                writeZipEntry(zos, "contacts.png", iconBytes, added);
                writeZipEntry(zos, "contacts", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts", iconBytes, added);
                writeZipEntry(zos, "com.android.providers.contacts.png", iconBytes, added);
                writeZipEntry(zos, "com.android.providers.contacts", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.activities.PeopleActivity.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.activities.PeopleActivity", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.PeopleActivity.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.PeopleActivity", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.PeopleActivity.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.PeopleActivity", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.PeopleActivity.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.PeopleActivity", iconBytes, added);
            }
        }

        // Camera
        if (p.contains("camera")) {
            writeZipEntry(zos, "camera.png", iconBytes, added);
            writeZipEntry(zos, "camera", iconBytes, added);
            writeZipEntry(zos, "com.android.camera.png", iconBytes, added);
            writeZipEntry(zos, "com.android.camera/.Camera.png", iconBytes, added);
            writeZipEntry(zos, "com.android.camera/.Camera", iconBytes, added);
        }

        // Gallery
        if (p.contains("gallery")) {
            writeZipEntry(zos, "gallery.png", iconBytes, added);
            writeZipEntry(zos, "gallery", iconBytes, added);
            writeZipEntry(zos, "gallery3d.png", iconBytes, added);
            writeZipEntry(zos, "com.miui.gallery.png", iconBytes, added);
            writeZipEntry(zos, "com.android.gallery.png", iconBytes, added);
            writeZipEntry(zos, "com.android.gallery3d.png", iconBytes, added);
        }

        // Messages
        if (p.contains("mms") || p.contains("messaging")) {
            writeZipEntry(zos, "mms.png", iconBytes, added);
            writeZipEntry(zos, "mms", iconBytes, added);
            writeZipEntry(zos, "sms.png", iconBytes, added);
            writeZipEntry(zos, "com.android.mms.png", iconBytes, added);
        }

        // Settings
        if (p.contains("settings")) {
            writeZipEntry(zos, "settings.png", iconBytes, added);
            writeZipEntry(zos, "settings", iconBytes, added);
            writeZipEntry(zos, "com.android.settings.png", iconBytes, added);
            writeZipEntry(zos, "com.xiaomi.misettings.png", iconBytes, added);
        }

        // Clock
        if (p.contains("clock")) {
            writeZipEntry(zos, "clock.png", iconBytes, added);
            writeZipEntry(zos, "clock", iconBytes, added);
            writeZipEntry(zos, "deskclock.png", iconBytes, added);
            writeZipEntry(zos, "deskclock", iconBytes, added);
            writeZipEntry(zos, "com.android.deskclock.png", iconBytes, added);
        }

        // Calendar
        if (p.contains("calendar")) {
            writeZipEntry(zos, "calendar.png", iconBytes, added);
            writeZipEntry(zos, "calendar", iconBytes, added);
            writeZipEntry(zos, "com.android.calendar.png", iconBytes, added);
            writeZipEntry(zos, "com.xiaomi.calendar.png", iconBytes, added);
        }

        // File Manager
        if (p.contains("fileexplorer") || p.contains("file_explorer") || p.contains("files")) {
            writeZipEntry(zos, "file_explorer.png", iconBytes, added);
            writeZipEntry(zos, "fileexplorer.png", iconBytes, added);
            writeZipEntry(zos, "com.android.fileexplorer.png", iconBytes, added);
            writeZipEntry(zos, "com.mi.android.globalFileexplorer.png", iconBytes, added);
        }

        // Weather
        if (p.contains("weather")) {
            writeZipEntry(zos, "weather.png", iconBytes, added);
            writeZipEntry(zos, "weather2.png", iconBytes, added);
            writeZipEntry(zos, "com.miui.weather2.png", iconBytes, added);
        }

        // Notes
        if (p.contains("notes")) {
            writeZipEntry(zos, "notes.png", iconBytes, added);
            writeZipEntry(zos, "com.miui.notes.png", iconBytes, added);
        }

        // Security
        if (p.contains("securitycenter") || p.contains("security")) {
            writeZipEntry(zos, "security.png", iconBytes, added);
            writeZipEntry(zos, "securitycenter.png", iconBytes, added);
            writeZipEntry(zos, "clean_master.png", iconBytes, added);
            writeZipEntry(zos, "com.miui.securitycenter.png", iconBytes, added);
        }

        // Calculator
        if (p.contains("calculator")) {
            writeZipEntry(zos, "calculator.png", iconBytes, added);
            writeZipEntry(zos, "calculator2.png", iconBytes, added);
            writeZipEntry(zos, "com.miui.calculator.png", iconBytes, added);
        }

        // Sound Recorder
        if (p.contains("soundrecorder") || p.contains("recorder")) {
            writeZipEntry(zos, "sound_recorder.png", iconBytes, added);
            writeZipEntry(zos, "soundrecorder.png", iconBytes, added);
            writeZipEntry(zos, "com.android.soundrecorder.png", iconBytes, added);
        }

        // Compass
        if (p.contains("compass")) {
            writeZipEntry(zos, "compass.png", iconBytes, added);
            writeZipEntry(zos, "com.miui.compass.png", iconBytes, added);
        }

        // Music
        if (p.contains("music") || p.contains("player")) {
            writeZipEntry(zos, "music.png", iconBytes, added);
            writeZipEntry(zos, "player.png", iconBytes, added);
            writeZipEntry(zos, "com.miui.player.png", iconBytes, added);
            writeZipEntry(zos, "com.android.music.png", iconBytes, added);
        }
    }

    private static void writeZipEntry(ZipOutputStream zos, String entryName, byte[] data,
                                      Set<String> added) throws IOException {
        if (added.contains(entryName)) return;
        added.add(entryName);
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }
}
