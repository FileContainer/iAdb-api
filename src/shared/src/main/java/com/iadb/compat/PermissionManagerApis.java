package com.iadb.compat;

import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.RemoteException;
import android.permission.IPermissionManager;

import androidx.annotation.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public class PermissionManagerApis {

    public static int checkPermission(@Nullable String permName, int uid) throws RemoteException {
        if (Build.VERSION.SDK_INT != 30) {
            return Services.packageManager.get().checkUidPermission(permName, uid);
        } else {
            return Services.permissionManager.get().checkUidPermission(permName, uid);
        }
    }

    public static void grantRuntimePermission(@Nullable String packageName, @Nullable String permissionName, int deviceId, int userId) throws RemoteException {
        if (Build.VERSION.SDK_INT >= 34) {
            IPermissionManager perm = Services.permissionManager.get();
            Objects.requireNonNull(perm);

            try {
                perm.grantRuntimePermission(packageName, permissionName, deviceId, userId);
            }catch (NoSuchMethodError e) {
                perm.grantRuntimePermission(packageName, permissionName, userId);
            }
        } else {
            IPermissionManager perm = Services.permissionManager.get();
            Objects.requireNonNull(perm);
            perm.grantRuntimePermission(packageName, permissionName, userId);
        }
    }

    public static void revokeRuntimePermission(@Nullable String packageName, @Nullable String permissionName, int deviceId, int userId) throws RemoteException {
        if (Build.VERSION.SDK_INT >= 34) {
            IPermissionManager perm = Services.permissionManager.get();
            Objects.requireNonNull(perm);

            try {
                perm.revokeRuntimePermission(packageName, permissionName, deviceId, userId, (String) null);
            } catch (NoSuchMethodError e) {
                perm.revokeRuntimePermission(packageName, permissionName, userId, (String) null);
            }
        } else {
            IPermissionManager perm = Services.permissionManager.get();
            Objects.requireNonNull(perm);

            try {
                perm.revokeRuntimePermission(packageName, permissionName, userId, (String) null);
            } catch (NoSuchMethodError e) {
                perm.revokeRuntimePermission(packageName, permissionName, userId);
            }
        }
    }


    public static int checkPermission(String permName, String pkgName, int userId) throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return Services.packageManager.get().checkPermission(permName, pkgName, userId);
        } else {
            return Services.permissionManager.get().checkPermission(permName, pkgName, userId);
        }
    }

    public static int getPermissionFlags(String permissionName, String packageName, int userId) throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return Services.permissionManager.get().getPermissionFlags(packageName, permissionName, userId);
        } else {
            return Services.permissionManager.get().getPermissionFlags(permissionName, packageName, userId);
        }
    }

    public static void updatePermissionFlags(String permissionName, String packageName, int flagMask, int flagValues, boolean checkAdjustPolicyFlagPermission, int userId) throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Services.permissionManager.get().updatePermissionFlags(packageName, permissionName, flagMask, flagValues, checkAdjustPolicyFlagPermission, userId);
        } else {
            Services.permissionManager.get().updatePermissionFlags(permissionName, packageName, flagMask, flagValues, checkAdjustPolicyFlagPermission, userId);
        }
    }

    public static PermissionGroupInfo getPermissionGroupInfo(String groupName, int flags) throws RemoteException {
        return Services.permissionManager.get().getPermissionGroupInfo(groupName, flags);
    }

    public static PermissionInfo getPermissionInfo(String permissionName, String packageName, int flags) throws RemoteException {
        return Services.permissionManager.get().getPermissionInfo(permissionName, packageName, flags);
    }
}
