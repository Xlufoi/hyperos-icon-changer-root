package org.blueos.icons;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorWheelView extends View {
    private Paint wheelPaint;
    private Paint thumbPaint;
    private Paint thumbStrokePaint;
    private Paint valOverlayPaint;

    private float hue = 0f;
    private float sat = 1f;
    private float val = 1f;

    private float cx, cy, radius;
    private int currentColor = Color.RED;

    public interface OnColorChangeListener {
        void onColorChanged(int color);
    }

    private OnColorChangeListener listener;

    public ColorWheelView(Context context) {
        super(context);
        init();
    }

    public ColorWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbStrokePaint.setStyle(Paint.Style.STROKE);
        thumbStrokePaint.setStrokeWidth(6f);
        thumbStrokePaint.setColor(Color.WHITE);

        valOverlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valOverlayPaint.setColor(Color.BLACK);
    }

    public void setOnColorChangeListener(OnColorChangeListener listener) {
        this.listener = listener;
    }

    public void setColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        this.hue = hsv[0];
        this.sat = hsv[1];
        this.val = hsv[2];
        this.currentColor = color;
        invalidate();
    }

    public void setValue(float val) {
        this.val = Math.max(0f, Math.min(1f, val));
        this.currentColor = Color.HSVToColor(new float[]{hue, sat, this.val});
        if (listener != null) listener.onColorChanged(currentColor);
        invalidate();
    }

    public int getColor() {
        return currentColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2f;
        cy = h / 2f;
        radius = Math.min(cx, cy) - 24f; // Margin for thumb indicator

        if (radius > 10f) {
            Shader sweepShader = new SweepGradient(cx, cy,
                    new int[]{0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000},
                    null);
            Shader radialShader = new RadialGradient(cx, cy, radius,
                    0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
            ComposeShader compose = new ComposeShader(sweepShader, radialShader, PorterDuff.Mode.SRC_OVER);
            wheelPaint.setShader(compose);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (radius <= 0) return;

        // 1. Draw color wheel
        canvas.drawCircle(cx, cy, radius, wheelPaint);

        // 2. Value/darkness overlay (if brightness < 1.0)
        if (val < 1f) {
            valOverlayPaint.setAlpha((int) ((1f - val) * 255));
            canvas.drawCircle(cx, cy, radius, valOverlayPaint);
        }

        // 3. Draw thumb selector indicator
        double rad = Math.toRadians(hue);
        float dist = sat * radius;
        float thumbX = cx + (float) (dist * Math.cos(rad));
        float thumbY = cy + (float) (dist * Math.sin(rad));

        thumbPaint.setColor(currentColor);
        canvas.drawCircle(thumbX, thumbY, 18f, thumbStrokePaint);
        canvas.drawCircle(thumbX, thumbY, 15f, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float dx = event.getX() - cx;
        float dy = event.getY() - cy;
        float dist = (float) Math.hypot(dx, dy);

        sat = Math.min(1.0f, dist / radius);
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) angle += 360.0;
        hue = (float) angle;

        currentColor = Color.HSVToColor(new float[]{hue, sat, val});
        if (listener != null) {
            listener.onColorChanged(currentColor);
        }
        invalidate();
        return true;
    }
}
