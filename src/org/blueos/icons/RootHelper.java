package org.blueos.icons;

import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class RootHelper {
    private static final String TAG = "BlueOSIconsRoot";

    private static final String[] SU_PATHS = {
            "su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/adb/ap/bin/su",
            "/data/adb/ksu/bin/su",
            "/data/adb/magisk/su"
    };

    public static String findSuBinary() {
        for (String path : SU_PATHS) {
            try {
                Process p = new ProcessBuilder(path, "-v").start();
                boolean exited = p.waitFor(500, TimeUnit.MILLISECONDS);
                if (exited && p.exitValue() == 0) {
                    return path;
                }
            } catch (Throwable ignored) {}
        }
        return "su";
    }

    public static boolean runRootCommand(String cmd) {
        String su = findSuBinary();
        try {
            Process p = new ProcessBuilder(su).start();
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(cmd + "\nexit\n");
            os.flush();
            boolean finished = p.waitFor(15, TimeUnit.SECONDS);
            return finished && p.exitValue() == 0;
        } catch (Throwable t) {
            Log.e(TAG, "Root command failed: " + cmd, t);
            return false;
        }
    }
}
