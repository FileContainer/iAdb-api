package com.iadb.compat;

import android.annotation.SuppressLint;
import android.content.pm.UserInfo;
import android.os.IUserManager;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class UserManagerApis {

    @SuppressLint("NewApi")
    @NonNull
    public static List<UserInfo> getUsers(boolean excludePartial, boolean excludeDying, boolean excludePreCreated) throws RemoteException {
        IUserManager um = Services.userManager.get();
        List<UserInfo> list;
        list = um.getUsers(excludePartial, excludeDying, excludePreCreated);
        return list;
    }

    public static List<UserInfo> getUsersNoThrow(boolean excludePartial, boolean excludeDying, boolean excludePreCreated) {
        List<UserInfo> result = new ArrayList<>();
        try {
            result.addAll(getUsers(excludePartial, excludeDying, excludePreCreated));
        } catch (Throwable ignored) {
        }
        return result;
    }

    public static boolean isUserUnlocked(int userId) throws RemoteException {
        return Services.userManager.get().isUserUnlocked(userId);
    }

    public static boolean isUserStorageAvailable(int userId) throws RemoteException {
        return Services.userManager.get().isUserRunning(userId)
                && Services.userManager.get().isUserUnlocked(userId);
    }

    @NonNull
    public static UserInfo getUserInfo(int userId) {
        IUserManager um = Services.userManager.get();
        return um.getUserInfo(userId);
    }

    @NonNull
    public static Collection<Integer> getUserIdsNoThrow() {
        return getUserIdsNoThrow(true, true, true);
    }

    public static Collection<Integer> getUserIdsNoThrow(boolean excludePartial, boolean excludeDying, boolean excludePreCreated) {
        Set<Integer> result = new ArraySet<>();
        try {
            for (UserInfo it : getUsers(excludePartial, excludeDying, excludePreCreated)) {
                result.add(it.id);
            }
        } catch (Throwable ignored) {
            result.add(0);
        }
        return result;
    }
}
