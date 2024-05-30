package com.iadb.api;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RestrictTo;

@RestrictTo(LIBRARY_GROUP_PREFIX)
public class Container implements Parcelable {

    public IBinder binder;

    public Container(IBinder binder) {
        this.binder = binder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.binder);
    }

    protected Container(Parcel in) {
        this.binder = in.readStrongBinder();
    }

    public static final Creator<Container> CREATOR = new Creator<Container>() {
        @Override
        public Container createFromParcel(Parcel source) {
            return new Container(source);
        }

        @Override
        public Container[] newArray(int size) {
            return new Container[size];
        }
    };
}
