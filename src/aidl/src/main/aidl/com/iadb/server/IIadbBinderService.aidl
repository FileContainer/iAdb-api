package com.iadb.server;

import com.iadb.server.IRemoteProcess;
import com.iadb.server.IIadbBinderApplication;
import com.iadb.server.IIadbBinderServiceConnection;

interface IIadbBinderService {

    int getVersion() = 2;

    int getUid() = 3;

    int checkPermission(String permission) = 4;

    IRemoteProcess newProcess(in String[] cmd, in String[] env, in String dir) = 7;

    String getSELinuxContext() = 8;

    String getSystemProperty(in String name, in String defaultValue) = 9;

    void setSystemProperty(in String name, in String value) = 10;

    int addUserService(in IIadbBinderServiceConnection conn, in Bundle args) = 11;

    int removeUserService(in IIadbBinderServiceConnection conn, in Bundle args) = 12;

    void requestPermission(int requestCode) = 14;

    boolean checkSelfPermission() = 15;

    boolean shouldShowRequestPermissionRationale() = 16;

    void attachApplication(in IIadbBinderApplication application,in Bundle args) = 17;

    void exit() = 100;

    void attachUserService(in IBinder binder, in Bundle options) = 101;

    oneway void dispatchPackageChanged(in Intent intent) = 102;

    boolean isHidden(int uid) = 103;

    oneway void dispatchPermissionConfirmationResult(int requestUid, int requestPid, int requestCode, in Bundle data) = 104;

    int getFlagsForUid(int uid, int mask) = 105;

    void updateFlagsForUid(int uid, int mask, int value) = 106;
 }
