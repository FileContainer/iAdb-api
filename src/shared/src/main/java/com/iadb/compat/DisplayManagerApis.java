package com.iadb.compat;

import static com.iadb.compat.Services.displayManager;

import android.hardware.display.IDisplayManagerCallback;
import android.os.RemoteException;
import android.view.DisplayInfo;

public class DisplayManagerApis {

    public static DisplayInfo getDisplayInfo(int displayId) throws RemoteException {
        return Services.displayManager.get().getDisplayInfo(displayId);
    }

    public static void registerCallback(IDisplayManagerCallback callback) throws RemoteException {
        Services.displayManager.get().registerCallback(callback);
    }
}
