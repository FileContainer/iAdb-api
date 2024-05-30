package com.iadb.compat;

import android.os.Build;

public class DeviceIdleControllerApis {

    public static void addPowerSaveTempWhitelistApp(String name, long duration, int userId, int reasonCode, String reason) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Services.deviceIdleController.get().addPowerSaveTempWhitelistApp(name, duration, userId, reasonCode, reason);
        } else {
            Services.deviceIdleController.get().addPowerSaveTempWhitelistApp(name, duration, userId, reason);
        }
    }
}
