package org.blueos.icons;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.File;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "blueos_icon_settings";
    private static final String KEY_SHAPE = "selected_shape";
    private static final String KEY_THEME = "selected_theme";

    private Spinner spinnerShape;
    private Spinner spinnerTheme;
    private ImageView[] previewIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerShape = findViewById(R.id.spinner_shape);
        spinnerTheme = findViewById(R.id.spinner_theme);

        previewIcons = new ImageView[]{
                findViewById(R.id.preview_icon1),
                findViewById(R.id.preview_icon2),
                findViewById(R.id.preview_icon3),
                findViewById(R.id.preview_icon4),
                findViewById(R.id.preview_icon5)
        };

        // Adapters
        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, IconThemeEngine.SHAPES);
        spinnerShape.setAdapter(shapeAdapter);

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, IconThemeEngine.THEMES);
        spinnerTheme.setAdapter(themeAdapter);

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        spinnerShape.setSelection(prefs.getInt(KEY_SHAPE, 0)); // Default Circle
        spinnerTheme.setSelection(prefs.getInt(KEY_THEME, 0)); // Default Monet 70%

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveSettings();
                updatePreviews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerShape.setOnItemSelectedListener(listener);
        spinnerTheme.setOnItemSelectedListener(listener);

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
                .putInt(KEY_THEME, spinnerTheme.getSelectedItemPosition())
                .apply();
    }

    private void updatePreviews() {
        int shapeIdx = spinnerShape.getSelectedItemPosition();
        int themeIdx = spinnerTheme.getSelectedItemPosition();

        Bitmap plate = IconThemeEngine.generateShapePlate(this, shapeIdx, themeIdx, 168);
        for (ImageView iv : previewIcons) {
            iv.setBackground(new android.graphics.drawable.BitmapDrawable(getResources(), plate));
        }
    }

    private void applyHyperOSIcons() {
        final int shapeIdx = spinnerShape.getSelectedItemPosition();
        final int themeIdx = spinnerTheme.getSelectedItemPosition();

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Генерация и установка значков BlueOS...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File customizedZip = IconThemeEngine.buildCustomizedIconsZip(MainActivity.this, shapeIdx, themeIdx);
                    String iconsPath = customizedZip.getAbsolutePath();

                    String cmd = "mkdir -p /data/system/theme && " +
                            "cp -f " + iconsPath + " /data/system/theme/icons && " +
                            "chmod 644 /data/system/theme/icons && " +
                            "chown 1000:1000 /data/system/theme/icons 2>/dev/null || chown system:system /data/system/theme/icons 2>/dev/null ; " +
                            "rm -rf /data/system/theme/.runtime/icons* /data/system/theme/icons_cache* 2>/dev/null ; " +
                            "rm -rf /data/user_de/0/com.mi.android.globallauncher/cache/* /data/user/0/com.mi.android.globallauncher/cache/* 2>/dev/null ; " +
                            "pkill -f com.mi.android.globallauncher ; am force-stop com.mi.android.globallauncher";

                    final boolean ok = RootHelper.runRootCommand(cmd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if (ok) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Успешно!")
                                        .setMessage("Значки BlueOS (" + IconThemeEngine.SHAPES[shapeIdx] + ", " +
                                                IconThemeEngine.THEMES[themeIdx] + ") применены на все приложения!\nРабочий стол перезапущен.")
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
                        "rm -rf /data/system/theme/.runtime/icons* /data/system/theme/icons_cache* 2>/dev/null ; " +
                        "pkill -f com.mi.android.globallauncher ; am force-stop com.mi.android.globallauncher";
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
