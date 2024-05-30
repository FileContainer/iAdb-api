package com.iadb.compat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.ContentProviderHolder;
import android.app.IActivityManager;
import android.app.IApplicationThread;
import android.app.IProcessObserver;
import android.app.ITaskStackListener;
import android.app.IUidObserver;
import android.app.ProfilerInfo;
import android.content.BroadcastReceiver;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.iadb.compat.adapter.IntentReceiver;

import java.util.List;

@SuppressWarnings("unused")
public class ActivityManagerApis {

    public static int checkPermission(@Nullable String permission, int pid, int uid) throws RemoteException {
        return Services.activityManager.get().checkPermission(permission, pid, uid);
    }

    public static void registerProcessObserver(@Nullable IProcessObserver processObserver) throws RemoteException {
        Services.activityManager.get().registerProcessObserver(processObserver);
    }

    public static void unregisterProcessObserver(@Nullable IProcessObserver observer) throws RemoteException {
        Services.activityManager.get().unregisterProcessObserver(observer);
    }

    public static void registerUidObserver(@Nullable IUidObserver observer, int which, int cutpoint, @Nullable String callingPackage) throws RemoteException {
        Services.activityManager.get().registerUidObserver(observer, which, cutpoint, callingPackage);
    }

    public static void unregisterUidObserver(@Nullable IUidObserver observer) throws RemoteException {
        Services.activityManager.get().unregisterUidObserver(observer);
    }

    @Nullable
    public static IContentProvider getContentProviderExternal(@Nullable String name, int userId, @Nullable IBinder token, @Nullable String tag) throws RemoteException {
        IActivityManager am = Services.activityManager.get();
        ContentProviderHolder contentProviderHolder;
        IContentProvider provider;
        contentProviderHolder = am.getContentProviderExternal(name, userId, token, tag);
        provider = contentProviderHolder != null ? contentProviderHolder.provider : null;

        return provider;
    }

    public static void removeContentProviderExternal(@Nullable String name, @Nullable IBinder token) throws RemoteException {
        Services.activityManager.get().removeContentProviderExternal(name, token);
    }

    public static void forceStopPackageNoThrow(@Nullable String packageName, int userId) {
        try {
            Services.activityManager.get().forceStopPackage(packageName, userId);
        } catch (Exception ignored) {
        }
    }

    public static void startActivity(@Nullable Intent intent, @Nullable String mimeType, int userId) throws RemoteException {
        Services.activityManager.get().startActivityAsUser((IApplicationThread) null, android.system.Os.getuid() == 2000 ? "com.android.shell" : null, intent, mimeType, (IBinder) null, (String) null, 0, 0, (ProfilerInfo) null, (Bundle) null, userId);
    }

    public static void startActivityNoThrow(@NonNull Intent intent, @Nullable String mimeType, int userId) {
        try {
            startActivity(intent, mimeType, userId);
        } catch (Throwable ignored) {
        }
    }

    public static void broadcastIntent(
            Intent intent,
            String resolvedType, IIntentReceiver resultTo, int resultCode,
            String resultData, Bundle map, String[] requiredPermissions,
            int appOp, Bundle options, boolean serialized, boolean sticky, int userId) throws RemoteException {
        Services.activityManager.get().broadcastIntent(null, intent, null, resultTo, 0, null, null,
                null, -1, null, true, sticky, userId);
    }

    public static void broadcastIntent(Intent intent, int userId) throws RemoteException {
        boolean sticky = intent.getComponent() == null;
        broadcastIntent(intent, null, new IntentReceiver(), 0, null, null,
                null, -1, null, true, sticky, userId);
    }

    @SuppressLint("UnsafeOptInUsageError")
    public static int getPackageProcessState(String pkg, int userId, String callingPackage) throws RemoteException {
        IActivityManager am = Services.activityManager.get();
        return am.getUidProcessState(PackageManagerApis.getPackageUid(pkg, 0, userId), callingPackage);
    }

    public static void forceStopPackage(String packageName, int userId) throws RemoteException {
        Services.activityManager.get().forceStopPackage(packageName, userId);
    }

    /**
     * Method for the shell UID to start deletating its permission identity to an
     * active instrumenation. The shell can delegate permissions only to one active
     * instrumentation at a time. An active instrumentation is one running and
     * started from the shell.
     */
    public static void startDelegateShellPermissionIdentity(int uid, String[] permissions) throws RemoteException {
        Services.activityManager.get().startDelegateShellPermissionIdentity(uid, permissions);
    }

    /**
     * Method for the shell UID to stop deletating its permission identity to an
     * active instrumenation. An active instrumentation is one running and
     * started from the shell.
     */
    public static void stopDelegateShellPermissionIdentity() throws RemoteException {
        Services.activityManager.get().stopDelegateShellPermissionIdentity();
    }

    /**
     * Method for the shell UID to get currently adopted permissions for an active instrumentation.
     * An active instrumentation is one running and started from the shell.
     */
    public static List<String> getDelegatedShellPermissions() throws RemoteException {
        return Services.activityManager.get().getDelegatedShellPermissions();
    }

    @RequiresApi(Build.VERSION_CODES.S)
    public static ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
        return Services.activityManager.get().getFocusedRootTaskInfo();
    }

    public static void registerTaskStackListener(@NonNull ITaskStackListener listener) throws RemoteException {
        Services.activityManager.get().registerTaskStackListener(listener);
    }

    public static void unregisterTaskStackListener(@NonNull ITaskStackListener listener) throws RemoteException {
        Services.activityManager.get().unregisterTaskStackListener(listener);
    }

    public static List<ActivityManager.RunningTaskInfo> getTasks(int maxNum) throws RemoteException {
        return Services.activityManager.get().getTasks(maxNum);
    }

    public static Intent registerReceiver(
            String callerPackage,
            String callingFeatureId, String receiverId, BroadcastReceiver receiver,
            IntentFilter filter, String requiredPermission, int userId, int flags) throws RemoteException {

        IIntentReceiver intentReceiver;
        if (receiver != null) {
            intentReceiver = new IIntentReceiver.Stub() {
                @Override
                public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) throws RemoteException {
                    receiver.onReceive(null, intent);
                }
            };
        } else {
            intentReceiver = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return Services.activityManager.get().registerReceiverWithFeature(
                    null, callerPackage, callingFeatureId, receiverId, intentReceiver, filter,
                    requiredPermission, userId, flags);
        } else {
            return Services.activityManager.get().registerReceiverWithFeature(
                    null, callerPackage, callingFeatureId, intentReceiver, filter,
                    requiredPermission, userId, flags);
        }
    }

    public static void unregisterReceiver(IIntentReceiver receiver) throws RemoteException {
        Services.activityManager.get().unregisterReceiver(receiver);
    }
}
