package org.blueos.icons;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void applyHyperOSIcons() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Extract icons from assets
                    File internalIcons = new File(getFilesDir(), "icons");
                    InputStream in = getAssets().open("icons");
                    FileOutputStream out = new FileOutputStream(internalIcons);
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    String iconsPath = internalIcons.getAbsolutePath();
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
                            if (ok) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Успешно!")
                                        .setMessage(getString(R.string.msg_applied))
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.msg_root_error), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (Throwable t) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
