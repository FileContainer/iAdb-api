package com.iadb.compat;

import static com.iadb.compat.Services.windowManager;

import android.os.Build;
import android.os.RemoteException;

import androidx.annotation.RequiresApi;

import com.android.internal.policy.IKeyguardLockedStateListener;

public class WindowManagerApis {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    public static void addKeyguardLockedStateListener(IKeyguardLockedStateListener listener) throws RemoteException {
        Services.windowManager.get().addKeyguardLockedStateListener(listener);
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    public static void removeKeyguardLockedStateListener(IKeyguardLockedStateListener listener) throws RemoteException {
        Services.windowManager.get().removeKeyguardLockedStateListener(listener);
    }
}
