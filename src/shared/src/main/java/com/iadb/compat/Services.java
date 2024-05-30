package com.iadb.compat;

import android.app.IActivityManager;
import android.content.pm.ILauncherApps;
import android.content.pm.IPackageManager;
import android.hardware.display.IDisplayManager;
import android.os.IBatteryPropertiesRegistrar;
import android.os.IDeviceIdleController;
import android.os.IUserManager;
import android.permission.IPermissionManager;
import android.view.IWindowManager;

import com.android.internal.app.IAppOpsService;
import com.iadb.compat.util.SystemServiceBinder;

class Services {

    protected static final SystemServiceBinder<IAppOpsService> appOps;
    protected static final SystemServiceBinder<IActivityManager> activityManager;
    protected static final SystemServiceBinder<IUserManager> userManager;
    protected static final SystemServiceBinder<IPackageManager> packageManager;
    protected static final SystemServiceBinder<IPermissionManager> permissionManager;
    protected static final SystemServiceBinder<IDeviceIdleController> deviceIdleController;
    protected static final SystemServiceBinder<IDisplayManager> displayManager;
    protected static final SystemServiceBinder<IBatteryPropertiesRegistrar> batteryPropertiesRegistrar;
    protected static final SystemServiceBinder<ILauncherApps> launcherApps;
    protected static final SystemServiceBinder<IWindowManager> windowManager;

    static {
        appOps = new SystemServiceBinder<>(
                "appops", IAppOpsService.Stub::asInterface);

        activityManager = new SystemServiceBinder<>("activity", binder -> IActivityManager.Stub.asInterface(binder));

        userManager = new SystemServiceBinder<>(
                "user", IUserManager.Stub::asInterface);

        packageManager = new SystemServiceBinder<>(
                "package", IPackageManager.Stub::asInterface);

        permissionManager = new SystemServiceBinder<>(
                "permissionmgr", IPermissionManager.Stub::asInterface);

        deviceIdleController = new SystemServiceBinder<>(
                "deviceidle", IDeviceIdleController.Stub::asInterface);

        displayManager = new SystemServiceBinder<>(
                "display", IDisplayManager.Stub::asInterface);

        batteryPropertiesRegistrar = new SystemServiceBinder<>(
                "batteryproperties", IBatteryPropertiesRegistrar.Stub::asInterface);

        launcherApps = new SystemServiceBinder<>(
                "launcherapps", ILauncherApps.Stub::asInterface);

        windowManager = new SystemServiceBinder<>(
                "window", IWindowManager.Stub::asInterface);
    }
}
