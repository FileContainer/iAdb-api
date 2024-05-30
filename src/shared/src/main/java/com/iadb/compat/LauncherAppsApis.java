package com.iadb.compat;

import android.content.pm.IOnAppsChangedListener;
import android.os.RemoteException;

public class LauncherAppsApis {

    public static void addOnAppsChangedListener(String callingPackage, IOnAppsChangedListener listener) throws RemoteException {
        Services.launcherApps.get().addOnAppsChangedListener(callingPackage, listener);
    }

    public static void removeOnAppsChangedListener(IOnAppsChangedListener listener) throws RemoteException {
            Services.launcherApps.get().removeOnAppsChangedListener(listener);
    }
}
