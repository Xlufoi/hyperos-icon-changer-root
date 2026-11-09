package org.blueos.icons;

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
            "Круг (Circle)",
            "Сквиркл (Squircle)",
            "Скругленный квадрат (Rounded Square)",
            "Капля слева (Teardrop)",
            "Капля справа (Teardrop Right)",
            "Шестиугольник (Hexagon)",
            "Восьмиугольник (Octagon)",
            "Цветок (Flower / Pebble)"
    };

    public static final String[] THEMES = {
            "Monet Светлая (70% полупрозрачная)",
            "Monet Темная (70% полупрозрачная)",
            "AOSP Черная (70% полупрозрачная)",
            "AOSP Белая (70% полупрозрачная)"
    };

    public static final String[] STYLES = {
            "Оригинальные цвета (Стоковые логотипы)",
            "Monet (Тематический стиль AOSP)",
            "Чисто черный / белый (AOSP Dark)"
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
        Path path = new Path();
        float r = Math.min(width, height) / 2f;
        float cx = width / 2f;
        float cy = height / 2f;

        switch (shapeIndex) {
            case 0: // Circle
                path.addCircle(cx, cy, r, Path.Direction.CW);
                break;
            case 1: // Squircle
                path.moveTo(cx, 0);
                path.cubicTo(width * 0.10f, 0, 0, height * 0.10f, 0, cy);
                path.cubicTo(0, height * 0.90f, width * 0.10f, height, cx, height);
                path.cubicTo(width * 0.90f, height, width, height * 0.90f, width, cy);
                path.cubicTo(width, height * 0.10f, width * 0.90f, 0, cx, 0);
                path.close();
                break;
            case 2: // Rounded Square
                path.addRoundRect(new RectF(0, 0, width, height), width * 0.24f, height * 0.24f, Path.Direction.CW);
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

    public static int getPlateColor(Context context, int themeIndex, int opacityPercent) {
        int alpha = (int) (255 * (opacityPercent / 100f));
        alpha = Math.max(0, Math.min(255, alpha));

        int baseR = 30, baseG = 41, baseB = 59; // Dark fallback

        switch (themeIndex) {
            case 0: // Monet Light
                if (Build.VERSION.SDK_INT >= 31 && context != null) {
                    try {
                        int c = context.getColor(android.R.color.system_accent1_100);
                        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
                    } catch (Throwable ignored) {}
                }
                baseR = 224; baseG = 236; baseB = 255;
                break;
            case 1: // Monet Dark
                if (Build.VERSION.SDK_INT >= 31 && context != null) {
                    try {
                        int c = context.getColor(android.R.color.system_accent1_800);
                        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
                    } catch (Throwable ignored) {}
                }
                baseR = 30; baseG = 41; baseB = 59;
                break;
            case 2: // AOSP Pure Black
                baseR = 15; baseG = 23; baseB = 42;
                break;
            case 3: // AOSP Pure White
                baseR = 248; baseG = 250; baseB = 252;
                break;
        }
        return Color.argb(alpha, baseR, baseG, baseB);
    }

    public static int getMonetGlyphColor(Context context) {
        if (Build.VERSION.SDK_INT >= 31 && context != null) {
            try {
                return context.getColor(android.R.color.system_accent1_200);
            } catch (Throwable ignored) {}
        }
        return 0xFF93C5FD;
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
        return renderAppIcon(context, icon, null, null, shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, size);
    }

    public static Bitmap renderAppIcon(Context context, Drawable icon, String pkg, String cls,
                                      int shapeIndex, int themeIndex, int styleIndex,
                                      int opacityPercent, int fillPercent, int size) {
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

        // 2. Optical visual fill fraction (consistent across ALL apps)
        float visualFill = (fillPercent / 100f) * 0.72f;
        float gs = size * visualFill;
        float cx = size / 2f;
        float cy = size / 2f;

        if (styleIndex == 0) {
            // Style 0: «Оригинальные цвета (Стоковые логотипы)»
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

        // Style 1 (Monet) or Style 2 (Pure Black/White)
        int glyphColor = (styleIndex == 1) ? getMonetGlyphColor(context) : 0xFFF1F5F9;

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
            Bitmap temp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(temp);

            Drawable fg = null;
            if (icon instanceof AdaptiveIconDrawable) {
                fg = ((AdaptiveIconDrawable) icon).getForeground();
            }
            if (fg == null) fg = icon;
            fg.setBounds(0, 0, size, size);
            fg.draw(tempCanvas);

            Bitmap themedMono = extractSmartMonochrome(temp, glyphColor);
            if (themedMono != null) {
                Bitmap normMono = normalizeAndCenterBitmap(themedMono, size, visualFill);
                canvas.drawBitmap(normMono != null ? normMono : themedMono, 0, 0, null);
                return bitmap;
            }
        } catch (Throwable ignored) {}

        // Ultimate safe fallback: NEVER leave a blank empty plate!
        // Render the crisp original stock icon, normalized to the exact same visual fill!
        Bitmap rawStock = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas sc = new Canvas(rawStock);
        icon.setBounds(0, 0, size, size);
        icon.draw(sc);
        Bitmap normStock = normalizeAndCenterBitmap(rawStock, size, visualFill);

        canvas.save();
        canvas.clipPath(path);
        canvas.drawBitmap(normStock != null ? normStock : rawStock, 0, 0, null);
        canvas.restore();

        return bitmap;
    }

    /**
     * Renders just the foreground glyph on a transparent canvas (for HyperOS dynamic layered icons: 1.png).
     */
    public static Bitmap renderGlyphOnly(Context context, String pkg, String cls,
                                        int styleIndex, int fillPercent, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float visualFill = (fillPercent / 100f) * 0.72f;
        float gs = size * visualFill;
        float cx = size / 2f;
        float cy = size / 2f;

        int glyphColor = (styleIndex == 1) ? getMonetGlyphColor(context) : 0xFFF1F5F9;
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

        // 8 border sample points
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
            // Transparent-backed logo
            Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
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

        // Solid background container detected
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

            if (diff < 45) {
                candidatePixels[i] = 0;
            } else {
                glyphPixelCount++;
                int symbolAlpha = Math.min(255, (diff - 45) * 5);
                int finalAlpha = (a * symbolAlpha) / 255;
                candidatePixels[i] = (finalAlpha << 24) | (tr << 16) | (tg << 8) | tb;
            }
        }

        float ratio = (float) glyphPixelCount / (float) (w * h);

        if (ratio >= 0.015f && ratio <= 0.60f) {
            Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            result.setPixels(candidatePixels, 0, w, 0, 0, w, h);
            return result;
        }

        // Return null to trigger the safe stock fallback
        return null;
    }

    // ==================== Build Theme Zip for HyperOS ====================

    public static File buildHyperOSThemeZip(Context context, int shapeIndex, int themeIndex,
                                           int styleIndex, int opacityPercent, int fillPercent) throws Exception {
        File outFile = new File(context.getCacheDir(), "hyperos_icons.zip");
        if (outFile.exists()) outFile.delete();

        int size = 235; // Standard flat icon size
        int layerSize = 351; // Standard HyperOS dynamic layer size

        Bitmap plateBitmap = createPlateBitmap(context, shapeIndex, themeIndex, opacityPercent, size);
        Bitmap maskBitmap = createMaskBitmap(shapeIndex, size);
        Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        ByteArrayOutputStream plateBytes = new ByteArrayOutputStream();
        plateBitmap.compress(Bitmap.CompressFormat.PNG, 100, plateBytes);

        ByteArrayOutputStream maskBytes = new ByteArrayOutputStream();
        maskBitmap.compress(Bitmap.CompressFormat.PNG, 100, maskBytes);

        ByteArrayOutputStream emptyBytes = new ByteArrayOutputStream();
        emptyBitmap.compress(Bitmap.CompressFormat.PNG, 100, emptyBytes);

        // 351x351 plate for dynamic layer 0.png
        Bitmap plate351 = createPlateBitmap(context, shapeIndex, themeIndex, opacityPercent, layerSize);
        ByteArrayOutputStream plate351Stream = new ByteArrayOutputStream();
        plate351.compress(Bitmap.CompressFormat.PNG, 100, plate351Stream);
        byte[] plate351Bytes = plate351Stream.toByteArray();

        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        Set<String> added = new HashSet<String>();

        // 1. Master compositing templates (clean, NO BORDER)
        writeZipEntry(zos, "icon_background.png", plateBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_mask.png", maskBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_border.png", emptyBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_pattern.png", emptyBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_folder.png", plateBytes.toByteArray(), added);
        writeZipEntry(zos, "icon_folder_light.png", plateBytes.toByteArray(), added);
        writeZipEntry(zos, "sym_def_app_icon.png", plateBytes.toByteArray(), added);

        // transform_config.xml:
        // SupportLayerIcon=true: HyperOS loads 0.png (plate) and 1.png (glyph) for dynamic apps
        // UseDynamicIcon=false: Disables live animated MAML scripts so our Monet clock & calendar display cleanly!
        String svgPath = SHAPE_SVG_PATHS[Math.min(shapeIndex, SHAPE_SVG_PATHS.length - 1)];
        String xmlConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<IconTransform>\n" +
                "    <Config name=\"SupportLayerIcon\" value=\"true\" />\n" +
                "    <Config name=\"UseDynamicIcon\" value=\"false\" />\n" +
                "    <Config name=\"ConfigIconMask\" value=\"" + svgPath + "\" />\n" +
                "</IconTransform>";
        writeZipEntry(zos, "transform_config.xml", xmlConfig.getBytes("UTF-8"), added);

        // 2. DYNAMIC / LAYERED ICONS for Xiaomi Apps (dynamic/<pkg>/0.png and 1.png)
        // This solves Dialer, Clock, Calendar, etc. not applying on HyperOS!
        String[][] dynamicApps = {
                // Phone / Dialer (ALL possible packages and aliases!)
                {"com.android.phone", "com.android.contacts.activities.TwelveKeyDialer"},
                {"com.android.contacts", "com.android.contacts.activities.TwelveKeyDialer"},
                {"com.android.contacts.activities.TwelveKeyDialer", "com.android.contacts.activities.TwelveKeyDialer"},
                {"com.android.contacts.TwelveKeyDialer", "com.android.contacts.activities.TwelveKeyDialer"},
                {"com.xiaomi.phone", "com.android.contacts.activities.TwelveKeyDialer"},
                {"com.google.android.dialer", "com.android.contacts.activities.TwelveKeyDialer"},
                {"phone", "com.android.contacts.activities.TwelveKeyDialer"},
                {"dialer", "com.android.contacts.activities.TwelveKeyDialer"},
                {"TwelveKeyDialer", "com.android.contacts.activities.TwelveKeyDialer"},

                // Clock & Calendar
                {"com.android.deskclock", "com.android.deskclock.DeskClockTabActivity"},
                {"com.xiaomi.calendar", "com.android.calendar.homepage.AllInOneActivity"},
                {"com.android.calendar", "com.android.calendar.homepage.AllInOneActivity"},

                // Core HyperOS Apps
                {"com.android.camera", "com.android.camera.Camera"},
                {"com.miui.gallery", "com.miui.gallery.activity.HomePageActivity"},
                {"com.android.settings", "com.android.settings.MainSettings"},
                {"com.miui.weather2", "com.miui.weather2.ActivityWeatherCycle"},
                {"com.miui.notes", "com.miui.notes.ui.NotesListActivity"},
                {"com.miui.calculator", "com.miui.calculator.cal.CalculatorActivity"},
                {"com.miui.securitycenter", "com.miui.securityscan.MainActivity"},
                {"com.android.fileexplorer", "com.android.fileexplorer.FileExplorerTabActivity"},
                {"com.mi.android.globalFileexplorer", "com.android.fileexplorer.FileExplorerTabActivity"},
                {"com.android.mms", "com.android.mms.ui.MmsTabActivity"},
                {"com.android.browser", "com.android.browser.BrowserActivity"},
                {"com.mi.globalbrowser", "com.android.browser.BrowserActivity"},
                {"com.miui.player", "com.miui.player.ui.MusicBrowserActivity"},
                {"com.android.soundrecorder", "com.android.soundrecorder.SoundRecorder"},
                {"com.miui.compass", "com.miui.compass.CompassActivity"},
                {"sym_def_app_icon", null}
        };

        for (String[] da : dynamicApps) {
            String pkg = da[0];
            String cls = da[1];
            Bitmap glyph = null;
            if (cls != null) {
                glyph = renderGlyphOnly(context, pkg, cls, styleIndex, fillPercent, layerSize);
            }
            writeDynamicLayer(zos, pkg, plate351Bytes, glyph, added);
        }

        // 3. FLAT ICONS: Pre-generate dedicated Phone / Dialer icon under ALL possible names
        Bitmap phoneIcon = renderAppIcon(context, null, "com.android.contacts", "com.android.contacts.activities.TwelveKeyDialer",
                shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, size);
        ByteArrayOutputStream phoneStream = new ByteArrayOutputStream();
        phoneIcon.compress(Bitmap.CompressFormat.PNG, 100, phoneStream);
        byte[] phoneBytes = phoneStream.toByteArray();

        // Write Phone to com.android.phone and com.android.contacts
        writeZipEntry(zos, "com.android.phone.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone/.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone/.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone/com.android.phone.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone/com.android.phone.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone#com.android.phone.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone#com.android.phone.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone/.DialtactsActivity.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.phone/.DialtactsActivity", phoneBytes, added);

        writeZipEntry(zos, "com.android.contacts.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.activities.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.activities.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.activities.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.activities.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.activities.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts/com.android.contacts.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.TwelveKeyDialer", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.TwelveKeyDialer.png", phoneBytes, added);
        writeZipEntry(zos, "com.android.contacts#com.android.contacts.TwelveKeyDialer", phoneBytes, added);
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
        writeZipEntry(zos, "com.google.android.dialer/.extensions.GoogleDialtactsActivity.png", phoneBytes, added);
        writeZipEntry(zos, "com.google.android.dialer/.extensions.GoogleDialtactsActivity", phoneBytes, added);
        writeZipEntry(zos, "com.google.android.dialer/com.google.android.dialer.extensions.GoogleDialtactsActivity.png", phoneBytes, added);
        writeZipEntry(zos, "com.google.android.dialer/com.google.android.dialer.extensions.GoogleDialtactsActivity", phoneBytes, added);

        // Pre-generate Clock and Calendar flat entries
        Bitmap clockIcon = renderAppIcon(context, null, "com.android.deskclock", "com.android.deskclock.DeskClockTabActivity",
                shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, size);
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
                shapeIndex, themeIndex, styleIndex, opacityPercent, fillPercent, size);
        ByteArrayOutputStream calStream = new ByteArrayOutputStream();
        calIcon.compress(Bitmap.CompressFormat.PNG, 100, calStream);
        byte[] calBytes = calStream.toByteArray();
        writeZipEntry(zos, "com.xiaomi.calendar.png", calBytes, added);
        writeZipEntry(zos, "com.xiaomi.calendar", calBytes, added);
        writeZipEntry(zos, "com.android.calendar.png", calBytes, added);
        writeZipEntry(zos, "com.android.calendar", calBytes, added);
        writeZipEntry(zos, "calendar.png", calBytes, added);
        writeZipEntry(zos, "calendar", calBytes, added);

        PackageManager pm = context.getPackageManager();

        // 4. Query ALL installed launcher applications on the device
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
                            styleIndex, opacityPercent, fillPercent, size);
                    ByteArrayOutputStream iconStream = new ByteArrayOutputStream();
                    themedBitmap.compress(Bitmap.CompressFormat.PNG, 100, iconStream);
                    byte[] iconBytes = iconStream.toByteArray();

                    writeAppEntries(zos, iconBytes, pkg, cls, added);
                } catch (Throwable ignored) {}
            }
        }

        // 5. Explicitly ensure core HyperOS / MIUI system packages are included
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

    private static void writeDynamicLayer(ZipOutputStream zos, String pkg, byte[] plateBytes,
                                          Bitmap glyphLayer, Set<String> added) throws IOException {
        writeZipEntry(zos, "dynamic/" + pkg + "/0.png", plateBytes, added);
        if (glyphLayer != null) {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            glyphLayer.compress(Bitmap.CompressFormat.PNG, 100, s);
            writeZipEntry(zos, "dynamic/" + pkg + "/1.png", s.toByteArray(), added);
        } else {
            // Empty transparent layer
            Bitmap empty = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            empty.compress(Bitmap.CompressFormat.PNG, 100, s);
            writeZipEntry(zos, "dynamic/" + pkg + "/1.png", s.toByteArray(), added);
        }
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
                writeZipEntry(zos, "com.android.contacts/.DialtactsContactsEntryActivity.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.DialtactsContactsEntryActivity", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.TwelveKeyDialer.png", iconBytes, added);
            }
            if (c.contains("people") || c.contains("contact")) {
                writeZipEntry(zos, "contacts.png", iconBytes, added);
                writeZipEntry(zos, "contacts", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.activities.PeopleActivity.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.activities.PeopleActivity", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.PeopleActivity.png", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts/.PeopleActivity", iconBytes, added);
                writeZipEntry(zos, "com.android.contacts#com.android.contacts.activities.PeopleActivity.png", iconBytes, added);
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
