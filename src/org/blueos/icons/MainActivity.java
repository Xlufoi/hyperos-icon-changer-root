package org.blueos.icons;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "hyperos_icon_settings";
    private static final String KEY_SHAPE = "selected_shape";
    private static final String KEY_STYLE = "selected_style";
    private static final String KEY_THEME = "selected_theme";
    private static final String KEY_OPACITY = "selected_opacity";
    private static final String KEY_FILL = "selected_fill";
    private static final String KEY_RADIUS = "selected_radius";

    private Spinner spinnerShape;
    private Spinner spinnerStyle;
    private Spinner spinnerTheme;
    private SeekBar seekbarOpacity;
    private TextView tvOpacityLabel;
    private SeekBar seekbarRadius;
    private TextView tvRadiusLabel;
    private SeekBar seekbarFill;
    private TextView tvFillLabel;
    private ImageView[] previewIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerShape = findViewById(R.id.spinner_shape);
        spinnerStyle = findViewById(R.id.spinner_style);
        spinnerTheme = findViewById(R.id.spinner_theme);
        seekbarOpacity = findViewById(R.id.seekbar_opacity);
        tvOpacityLabel = findViewById(R.id.tv_opacity_label);
        seekbarRadius = findViewById(R.id.seekbar_radius);
        tvRadiusLabel = findViewById(R.id.tv_radius_label);
        seekbarFill = findViewById(R.id.seekbar_fill);
        tvFillLabel = findViewById(R.id.tv_fill_label);

        previewIcons = new ImageView[]{
                findViewById(R.id.preview_icon1),
                findViewById(R.id.preview_icon2),
                findViewById(R.id.preview_icon3),
                findViewById(R.id.preview_icon4),
                findViewById(R.id.preview_icon5)
        };

        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, IconThemeEngine.SHAPES);
        spinnerShape.setAdapter(shapeAdapter);

        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, IconThemeEngine.STYLES);
        spinnerStyle.setAdapter(styleAdapter);

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, IconThemeEngine.THEMES);
        spinnerTheme.setAdapter(themeAdapter);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        spinnerShape.setSelection(prefs.getInt(KEY_SHAPE, 0)); // Circle
        spinnerStyle.setSelection(prefs.getInt(KEY_STYLE, 0)); // Original Colors
        spinnerTheme.setSelection(prefs.getInt(KEY_THEME, 0)); // Monet
        int opacity = prefs.getInt(KEY_OPACITY, 70);
        int radius = prefs.getInt(KEY_RADIUS, 36);
        int fill = prefs.getInt(KEY_FILL, 65);

        seekbarOpacity.setProgress(opacity);
        tvOpacityLabel.setText("Прозрачность подложки: " + opacity + "%");

        seekbarRadius.setProgress(radius);
        tvRadiusLabel.setText("Скругление углов: " + radius + "%");

        seekbarFill.setProgress(fill);
        tvFillLabel.setText("Заполнение картинки: " + fill + "%");

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveSettings();
                updatePreviews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerShape.setOnItemSelectedListener(spinnerListener);
        spinnerStyle.setOnItemSelectedListener(spinnerListener);
        spinnerTheme.setOnItemSelectedListener(spinnerListener);

        seekbarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvOpacityLabel.setText("Прозрачность подложки: " + progress + "%");
                if (fromUser) {
                    saveSettings();
                    updatePreviews();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveSettings();
                updatePreviews();
            }
        });

        seekbarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 10) {
                    progress = 10;
                    seekBar.setProgress(10);
                }
                tvRadiusLabel.setText("Скругление углов: " + progress + "%");
                if (fromUser) {
                    saveSettings();
                    updatePreviews();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveSettings();
                updatePreviews();
            }
        });

        seekbarFill.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 30) {
                    progress = 30;
                    seekBar.setProgress(30);
                }
                tvFillLabel.setText("Заполнение картинки: " + progress + "%");
                if (fromUser) {
                    saveSettings();
                    updatePreviews();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveSettings();
                updatePreviews();
            }
        });

        Button btnApplyHyperOS = findViewById(R.id.btn_apply_hyperos);
        Button btnRestoreHyperOS = findViewById(R.id.btn_restore_hyperos);
        Button btnApplyLauncher = findViewById(R.id.btn_apply_launcher);

        btnApplyHyperOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyHyperOSIcons();
            }
        });

        btnRestoreHyperOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreHyperOSIcons();
            }
        });

        btnApplyLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLauncherSettings();
            }
        });

        updatePreviews();
    }

    private void saveSettings() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putInt(KEY_SHAPE, spinnerShape.getSelectedItemPosition())
                .putInt(KEY_STYLE, spinnerStyle.getSelectedItemPosition())
                .putInt(KEY_THEME, spinnerTheme.getSelectedItemPosition())
                .putInt(KEY_OPACITY, seekbarOpacity.getProgress())
                .putInt(KEY_RADIUS, seekbarRadius.getProgress())
                .putInt(KEY_FILL, seekbarFill.getProgress())
                .apply();
    }

    private void updatePreviews() {
        int shapeIdx = spinnerShape.getSelectedItemPosition();
        int styleIdx = spinnerStyle.getSelectedItemPosition();
        int themeIdx = spinnerTheme.getSelectedItemPosition();
        int opacity = seekbarOpacity.getProgress();
        int radius = seekbarRadius.getProgress();
        int fill = seekbarFill.getProgress();

        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);

        for (int i = 0; i < previewIcons.length; i++) {
            Drawable stock = null;
            String pkg = null;
            String cls = null;
            if (apps != null && i < apps.size()) {
                try {
                    ResolveInfo info = apps.get(i);
                    stock = info.loadIcon(pm);
                    if (info.activityInfo != null) {
                        pkg = info.activityInfo.packageName;
                        cls = info.activityInfo.name;
                    }
                } catch (Throwable ignored) {}
            }
            if (stock == null) {
                stock = getApplicationInfo().loadIcon(pm);
                pkg = getPackageName();
                cls = getClass().getName();
            }

            Bitmap previewBitmap = IconThemeEngine.renderAppIcon(this, stock, pkg, cls, shapeIdx, themeIdx,
                    styleIdx, opacity, fill, radius, 168);
            previewIcons[i].setBackground(null);
            previewIcons[i].setImageBitmap(previewBitmap);
        }
    }

    private void applyHyperOSIcons() {
        final int shapeIdx = spinnerShape.getSelectedItemPosition();
        final int styleIdx = spinnerStyle.getSelectedItemPosition();
        final int themeIdx = spinnerTheme.getSelectedItemPosition();
        final int opacity = seekbarOpacity.getProgress();
        final int radius = seekbarRadius.getProgress();
        final int fill = seekbarFill.getProgress();

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Преобразование всех приложений HyperOS...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File customizedZip = IconThemeEngine.buildHyperOSThemeZip(MainActivity.this,
                            shapeIdx, themeIdx, styleIdx, opacity, fill, radius);
                    String iconsPath = customizedZip.getAbsolutePath();

                    String cmd = "mkdir -p /data/system/theme && " +
                            "cp -f " + iconsPath + " /data/system/theme/icons && " +
                            "chmod 644 /data/system/theme/icons && " +
                            "chown 1000:1000 /data/system/theme/icons 2>/dev/null || chown system:system /data/system/theme/icons 2>/dev/null ; " +
                            "rm -rf /data/system/theme/.runtime/* 2>/dev/null ; " +
                            "rm -rf /data/system/theme/icons_cache* 2>/dev/null ; " +
                            "rm -rf /data/system/theme/customized_icons* 2>/dev/null ; " +
                            "rm -rf /data/system/customized_icons* 2>/dev/null ; " +
                            "rm -rf /data/system/theme/dynamicicons* 2>/dev/null ; " +
                            "rm -rf /data/system/theme/dynamic_icons* 2>/dev/null ; " +
                            "rm -rf /data/system/theme_magic/* 2>/dev/null ; " +
                            "rm -rf /data/system/theme_config/theme_compatibility.xml 2>/dev/null ; " +
                            "rm -rf /data/user_de/0/com.mi.android.globallauncher/cache/* 2>/dev/null ; " +
                            "rm -rf /data/user/0/com.mi.android.globallauncher/cache/* 2>/dev/null ; " +
                            "rm -rf /data/user_de/0/com.mi.android.globallauncher/files/*icon* 2>/dev/null ; " +
                            "rm -rf /data/user/0/com.mi.android.globallauncher/files/*icon* 2>/dev/null ; " +
                            "rm -rf /data/user_de/0/com.mi.android.globallauncher/databases/*icon* /data/user/0/com.mi.android.globallauncher/databases/*icon* 2>/dev/null ; " +
                            "rm -rf /data/user_de/0/com.mi.android.globallauncher/databases/*app_icons* /data/user/0/com.mi.android.globallauncher/databases/*app_icons* 2>/dev/null ; " +
                            "rm -rf /data/user_de/0/com.miui.home/cache/* 2>/dev/null ; " +
                            "rm -rf /data/user/0/com.miui.home/cache/* 2>/dev/null ; " +
                            "rm -rf /data/user_de/0/com.miui.home/files/*icon* 2>/dev/null ; " +
                            "rm -rf /data/user/0/com.miui.home/files/*icon* 2>/dev/null ; " +
                            "rm -rf /data/user_de/0/com.miui.home/databases/*icon* 2>/dev/null ; " +
                            "rm -rf /data/user/0/com.miui.home/databases/*icon* 2>/dev/null ; " +
                            "am broadcast -a android.intent.action.THEME_CHANGED 2>/dev/null ; " +
                            "am broadcast -a com.miui.home.action.THEME_CHANGED 2>/dev/null ; " +
                            "am broadcast -a miui.intent.action.APPLY_THEME 2>/dev/null ; " +
                            "pkill -9 -f com.mi.android.globallauncher ; am force-stop com.mi.android.globallauncher 2>/dev/null ; " +
                            "pkill -9 -f com.miui.home ; am force-stop com.miui.home 2>/dev/null";

                    final boolean ok = RootHelper.runRootCommand(cmd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if (ok) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Успешно!")
                                        .setMessage("Значки (" + IconThemeEngine.SHAPES[shapeIdx] + ", " +
                                                IconThemeEngine.STYLES[styleIdx] + ", Прозрачность " + opacity +
                                                "%, Масштаб " + fill + "%) применены на ВСЕ приложения!\nРабочий стол перезапущен.")
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.msg_root_error), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (final Throwable t) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void restoreHyperOSIcons() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cmd = "rm -f /data/system/theme/icons ; " +
                        "rm -rf /data/system/theme/.runtime/* 2>/dev/null ; " +
                        "rm -rf /data/system/theme/icons_cache* 2>/dev/null ; " +
                        "rm -rf /data/system/theme/customized_icons* 2>/dev/null ; " +
                        "rm -rf /data/system/customized_icons* 2>/dev/null ; " +
                        "rm -rf /data/system/theme/dynamicicons* 2>/dev/null ; " +
                        "rm -rf /data/system/theme/dynamic_icons* 2>/dev/null ; " +
                        "rm -rf /data/user_de/0/com.mi.android.globallauncher/cache/* 2>/dev/null ; " +
                        "rm -rf /data/user/0/com.mi.android.globallauncher/cache/* 2>/dev/null ; " +
                        "rm -rf /data/user_de/0/com.mi.android.globallauncher/files/*icon* 2>/dev/null ; " +
                        "rm -rf /data/user/0/com.mi.android.globallauncher/files/*icon* 2>/dev/null ; " +
                        "rm -rf /data/user_de/0/com.mi.android.globallauncher/databases/*icon* 2>/dev/null ; " +
                        "rm -rf /data/user/0/com.mi.android.globallauncher/databases/*icon* 2>/dev/null ; " +
                        "rm -rf /data/user_de/0/com.miui.home/cache/* 2>/dev/null ; " +
                        "rm -rf /data/user/0/com.miui.home/cache/* 2>/dev/null ; " +
                        "rm -rf /data/user_de/0/com.miui.home/files/*icon* 2>/dev/null ; " +
                        "rm -rf /data/user/0/com.miui.home/files/*icon* 2>/dev/null ; " +
                        "rm -rf /data/user_de/0/com.miui.home/databases/*icon* 2>/dev/null ; " +
                        "rm -rf /data/user_de/0/com.miui.home/databases/*icon* 2>/dev/null ; " +
                        "am broadcast -a android.intent.action.THEME_CHANGED 2>/dev/null ; " +
                        "am broadcast -a com.miui.home.action.THEME_CHANGED 2>/dev/null ; " +
                        "am broadcast -a miui.intent.action.APPLY_THEME 2>/dev/null ; " +
                        "pkill -9 -f com.mi.android.globallauncher ; am force-stop com.mi.android.globallauncher 2>/dev/null ; " +
                        "pkill -9 -f com.miui.home ; am force-stop com.miui.home 2>/dev/null";
                final boolean ok = RootHelper.runRootCommand(cmd);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ok) {
                            Toast.makeText(MainActivity.this, getString(R.string.msg_restored), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.msg_root_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void openLauncherSettings() {
        Intent intent = new Intent("com.novalauncher.THEME");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            startActivity(Intent.createChooser(intent, "Выберите лаунчер"));
        } catch (Throwable t) {
            Toast.makeText(this, "Откройте Настройки лаунчера -> Значки (Icon pack)", Toast.LENGTH_LONG).show();
        }
    }
}
