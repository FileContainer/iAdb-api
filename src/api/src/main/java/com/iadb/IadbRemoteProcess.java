package com.iadb;

import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;

import com.iadb.server.IRemoteProcess;
import com.iadb.shared.BuildConfig;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class IadbRemoteProcess extends Process implements Parcelable {

    private static final Set<IadbRemoteProcess> CACHE = Collections.synchronizedSet(new ArraySet<>());

    private static final String TAG = "IadbRemoteProcess";

    private IRemoteProcess remote;
    private OutputStream os;
    private InputStream is;

    IadbRemoteProcess(IRemoteProcess remote) {
        this.remote = remote;
        try {
            this.remote.asBinder().linkToDeath((IBinder.DeathRecipient) () -> {
                this.remote = null;
                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "remote process is dead");
                }

                CACHE.remove(IadbRemoteProcess.this);
            }, 0);
        } catch (RemoteException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "linkToDeath", e);
            }
        }

        // The reference to the binder object must be hold
        CACHE.add(this);
    }

    @Override
    public OutputStream getOutputStream() {
        if (os == null) {
            try {
                os = new ParcelFileDescriptor.AutoCloseOutputStream(remote.getOutputStream());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return os;
    }

    @Override
    public InputStream getInputStream() {
        if (is == null) {
            try {
                is = new ParcelFileDescriptor.AutoCloseInputStream(remote.getInputStream());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return is;
    }

    @Override
    public InputStream getErrorStream() {
        try {
            return new ParcelFileDescriptor.AutoCloseInputStream(remote.getErrorStream());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int waitFor() throws InterruptedException {
        try {
            return remote.waitFor();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int exitValue() {
        try {
            return remote.exitValue();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            remote.destroy();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean alive() {
        try {
            return remote.alive();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean waitForTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            return remote.waitForTimeout(timeout, unit.toString());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public IBinder asBinder() {
        return remote.asBinder();
    }

    private IadbRemoteProcess(Parcel in) {
        remote = IRemoteProcess.Stub.asInterface(in.readStrongBinder());
    }

    public static final Creator<IadbRemoteProcess> CREATOR = new Creator<IadbRemoteProcess>() {
        @Override
        public IadbRemoteProcess createFromParcel(Parcel in) {
            return new IadbRemoteProcess(in);
        }

        @Override
        public IadbRemoteProcess[] newArray(int size) {
            return new IadbRemoteProcess[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(remote.asBinder());
    }
}
