package org.blueos.icons;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class ColorPickerDialog {
    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public static void show(Context context, String title, int initialColor, final OnColorSelectedListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null);
        final ColorWheelView wheelView = view.findViewById(R.id.color_wheel);
        final SeekBar seekBrightness = view.findViewById(R.id.seek_brightness);
        final View previewSwatch = view.findViewById(R.id.color_preview_swatch);
        final TextView tvHex = view.findViewById(R.id.tv_hex_code);

        wheelView.setColor(initialColor);

        float[] hsv = new float[3];
        Color.colorToHSV(initialColor, hsv);
        seekBrightness.setProgress((int) (hsv[2] * 100));

        final GradientDrawable swatchBg = new GradientDrawable();
        swatchBg.setShape(GradientDrawable.OVAL);
        swatchBg.setColor(initialColor);
        swatchBg.setStroke(3, 0xFF475569);
        previewSwatch.setBackground(swatchBg);
        tvHex.setText(String.format("#%06X", (0xFFFFFF & initialColor)));

        wheelView.setOnColorChangeListener(new ColorWheelView.OnColorChangeListener() {
            @Override
            public void onColorChanged(int color) {
                swatchBg.setColor(color);
                tvHex.setText(String.format("#%06X", (0xFFFFFF & color)));
            }
        });

        seekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                wheelView.setValue(progress / 100f);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("Select Color", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        if (listener != null) {
                            listener.onColorSelected(wheelView.getColor());
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }
}
