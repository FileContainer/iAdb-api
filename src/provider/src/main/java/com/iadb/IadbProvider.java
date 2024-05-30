package com.iadb;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iadb.api.Container;
import com.iadb.shared.BuildConfig;

/**
 * <p>
 * This provider receives binder from Iadb server. When app process starts,
 * Iadb server (it runs under adb/root) will send the binder to client apps with this provider.
 * </p>
 * <p>
 * Add the provider to your manifest like this:
 * </p>
 * <pre class="prettyprint">&lt;manifest&gt;
 *    ...
 *    &lt;application&gt;
 *        ...
 *        &lt;provider
 *            android:name="com.iadb.IadbProvider"
 *            android:authorities="${applicationId}.iadb"
 *            android:exported="true"
 *            android:multiprocess="false"
 *            android:permission="android.permission.INTERACT_ACROSS_USERS_FULL"
 *        &lt;/provider&gt;
 *        ...
 *    &lt;/application&gt;
 * &lt;/manifest&gt;</pre>
 *
 * <p>
 * There are something needs you attention:
 * </p>
 * <ol>
 * <li><code>android:permission</code> shoule be a permission that granted to Shell (com.android.shell)
 * but not normal apps (e.g., android.permission.INTERACT_ACROSS_USERS_FULL), so that it can only
 * be used by the app itself and Iadb server.</li>
 * <li><code>android:exported</code> must be <code>true</code> so that the provider can be accessed
 * from Iadb server runs under adb.</li>
 * <li><code>android:multiprocess</code> must be <code>false</code>
 * since Iadb server only gets uid when app starts.</li>
 * </ol>
 * <p>
 * If your app runs in multiple processes, this provider also provides the functionality of sharing
 * the binder across processes. See {@link #enableMultiProcessSupport(boolean)}.
 * </p>
 */
public class IadbProvider extends ContentProvider {

    private static final String TAG = "IadbProvider";

    // For receive Binder from Iadb
    public static final String METHOD_SEND_BINDER = "sendBinder";

    // For share Binder between processes
    public static final String METHOD_GET_BINDER = "getBinder";

    public static final String ACTION_BINDER_RECEIVED = "com.iadb.api.action.BINDER_RECEIVED";

    private static final String EXTRA_BINDER = "com.iadb.helper.api.extra.BINDER";

    public static final String PERMISSION = "com.iadb.helper.permission.API";

    public static final String MANAGER_APPLICATION_ID = "com.iadb.helper";

    private static boolean enableMultiProcess = false;

    private static boolean isProviderProcess = false;

    private static boolean enableSuiInitialization = false;

    public static void setIsProviderProcess(boolean isProviderProcess) {
        IadbProvider.isProviderProcess = isProviderProcess;
    }

    /**
     * Enables built-in multi-process support.
     * <p>
     * This method MUST be called as early as possible (e.g., static block in Application).
     */
    public static void enableMultiProcessSupport(boolean isProviderProcess) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Enable built-in multi-process support (from " + (isProviderProcess ? "provider process" : "non-provider process") + ")");
        }

        IadbProvider.isProviderProcess = isProviderProcess;
        IadbProvider.enableMultiProcess = true;
    }

    /**
     * Disable automatic Sui initialization.
     */
    public static void disableAutomaticSuiInitialization() {
        IadbProvider.enableSuiInitialization = false;
    }

    /**
     * Require binder for non-provider process, should have {@link #enableMultiProcessSupport(boolean)} called first.
     *
     * @param context Context
     */
    public static void requestBinderForNonProviderProcess(@NonNull Context context) {
        if (isProviderProcess) {
            return;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "request binder in non-provider process");
        }

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Container container = intent.getParcelableExtra(EXTRA_BINDER);
                if (container != null && container.binder != null) {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "binder received from broadcast");
                    }
                    Iadb.onBinderReceived(container.binder, context.getPackageName());
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, new IntentFilter(ACTION_BINDER_RECEIVED), Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(receiver, new IntentFilter(ACTION_BINDER_RECEIVED));
        }

        Bundle reply;
        try {
            reply = context.getContentResolver().call(Uri.parse("content://" + context.getPackageName() + ".iadb"),
                    IadbProvider.METHOD_GET_BINDER, null, new Bundle());
        } catch (Throwable tr) {
            reply = null;
        }

        if (reply != null) {
            reply.setClassLoader(Container.class.getClassLoader());

            Container container = reply.getParcelable(EXTRA_BINDER);
            if (container != null && container.binder != null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Binder received from other process");
                }
                Iadb.onBinderReceived(container.binder, context.getPackageName());
            }
        }
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);

        if (info.multiprocess)
            throw new IllegalStateException("android:multiprocess must be false");

        if (!info.exported)
            throw new IllegalStateException("android:exported must be true");

        isProviderProcess = true;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {

        if (extras == null) {
            return null;
        }

        extras.setClassLoader(Container.class.getClassLoader());

        Bundle reply = new Bundle();
        switch (method) {
            case METHOD_SEND_BINDER: {
                handleSendBinder(extras);
                break;
            }
            case METHOD_GET_BINDER: {
                if (!handleGetBinder(reply)) {
                    return null;
                }
                break;
            }
        }
        return reply;
    }

    private void handleSendBinder(@NonNull Bundle extras) {
        if (Iadb.pingBinder()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "sendBinder is called when already a living binder");
            }
            return;
        }

        Container container = extras.getParcelable(EXTRA_BINDER);
        if (container != null && container.binder != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "binder received");
            }

            Iadb.onBinderReceived(container.binder, getContext().getPackageName());

            if (enableMultiProcess) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "broadcast binder");
                }

                Intent intent = new Intent(ACTION_BINDER_RECEIVED)
                        .putExtra(EXTRA_BINDER, container)
                        .setPackage(getContext().getPackageName());
                getContext().sendBroadcast(intent);
            }
        }
    }

    private boolean handleGetBinder(@NonNull Bundle reply) {
        // Other processes in the same app can read the provider without permission
        IBinder binder = Iadb.getBinder();
        if (binder == null || !binder.pingBinder())
            return false;

        reply.putParcelable(EXTRA_BINDER, new Container(binder));
        return true;
    }

    // no other provider methods
    @Nullable
    @Override
    public final Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public final String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public final Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public final int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public final int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
