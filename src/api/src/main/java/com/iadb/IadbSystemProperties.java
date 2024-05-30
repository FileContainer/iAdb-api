package com.iadb;

import android.os.RemoteException;

/**
 * @since added from version 9
 */
public class IadbSystemProperties {

    public static String get(String key) throws RemoteException {
        return Iadb.requireService().getSystemProperty(key, null);
    }

    public static String get(String key, String def) throws RemoteException {
        return Iadb.requireService().getSystemProperty(key, def);
    }

    public static int getInt(String key, int def) throws RemoteException {
        return Integer.decode(Iadb.requireService().getSystemProperty(key, Integer.toString(def)));
    }

    public static long getLong(String key, long def) throws RemoteException {
        return Long.decode(Iadb.requireService().getSystemProperty(key, Long.toString(def)));
    }

    public static boolean getBoolean(String key, boolean def) throws RemoteException {
        return Boolean.parseBoolean(Iadb.requireService().getSystemProperty(key, Boolean.toString(def)));
    }

    public static void set(String key, String val) throws RemoteException {
        Iadb.requireService().setSystemProperty(key, val);
    }
}
