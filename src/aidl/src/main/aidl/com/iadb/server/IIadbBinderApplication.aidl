package com.iadb.server;

interface IIadbBinderApplication {

    oneway void bindApplication(in Bundle data) = 1;

    oneway void dispatchRequestPermissionResult(int requestCode, in Bundle data) = 2;

    // Sui only
//    void showPermissionConfirmation(int requestUid, int requestPid, in String requestPackageName, int requestCode) = 10000;
}