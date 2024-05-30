package com.iadb.compat;

import android.os.BatteryProperty;
import android.os.IBatteryPropertiesRegistrar;
import android.os.RemoteException;

public class BatteryPropertiesRegistrarApis {

    private static long queryProperty(int id) throws RemoteException {
        long ret;
        BatteryProperty prop = new BatteryProperty();
        IBatteryPropertiesRegistrar batteryPropertiesRegistrar = Services.batteryPropertiesRegistrar.get();
        if (batteryPropertiesRegistrar == null) {
            return Long.MIN_VALUE;
        }

        if (batteryPropertiesRegistrar.getProperty(id, prop) == 0) {
            ret = prop.getLong();
        } else {
            ret = Long.MIN_VALUE;
        }

        return ret;
    }

    public static int getIntProperty(int id) throws RemoteException {
        long value = queryProperty(id);
        if (value == Long.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        return (int) value;
    }

    public static long getLongProperty(int id) throws RemoteException {
        return queryProperty(id);
    }

    public static void scheduleUpdate() throws RemoteException {
        Services.batteryPropertiesRegistrar.get().scheduleUpdate();
    }
}
