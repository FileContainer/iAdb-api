package com.iadb;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import androidx.annotation.RestrictTo;

public class IadbApiConstants {

    public static final int SERVER_VERSION = 1;
    public static final int SERVER_PATCH_VERSION = 1;

    // binder
    public static final String BINDER_DESCRIPTOR = "com.iadb.server.IIadbBinderService";
    public static final int BINDER_TRANSACTION_transact = 1;

    // user service
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public static final int USER_SERVICE_TRANSACTION_destroy = 57321;

    public static final String USER_SERVICE_ARG_TAG = "iadb:us-a-tag";
    public static final String USER_SERVICE_ARG_COMPONENT = "iadb:us-a-comp";
    public static final String USER_SERVICE_ARG_DEBUGGABLE = "iadb:us-a-debug";
    public static final String USER_SERVICE_ARG_VERSION_CODE = "iadb:us-a-vc";
    public static final String USER_SERVICE_ARG_PROCESS_NAME = "iadb:us-a-pname";
    public static final String USER_SERVICE_ARG_NO_CREATE = "iadb:us-a-nc";
    public static final String USER_SERVICE_ARG_DAEMON = "iadb:us-a-daemon";
    public static final String USER_SERVICE_ARG_USE_32_BIT_APP_PROCESS = "iadb:us-a-32b-app";
    public static final String USER_SERVICE_ARG_REMOVE = "iadb:us-r";

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public static final String USER_SERVICE_ARG_TOKEN = "iadb:us-a-token";

    // bind application
    public static final String BIND_APPLICATION_SERVER_VERSION = "iadb:ar-v";
    public static final String BIND_APPLICATION_SERVER_PATCH_VERSION = "iadb:a-r-p-v";
    public static final String BIND_APPLICATION_SERVER_UID = "iadb:a-r-uid";
    public static final String BIND_APPLICATION_SERVER_SECONTEXT = "iadb:a-r-se";
    public static final String BIND_APPLICATION_PERMISSION_GRANTED = "iadb:a-r-p-g";
    public static final String BIND_APPLICATION_SHOULD_SHOW_REQUEST_PERMISSION_RATIONALE = "iadb:a-r-p-r";

    // request permission
    public static final String REQUEST_PERMISSION_REPLY_ALLOWED = "iadb:r-p-r-a";
    public static final String REQUEST_PERMISSION_REPLY_IS_ONETIME = "iadb:r-p-r-o";

    // attach application
    public static final String ATTACH_APPLICATION_PACKAGE_NAME = "iadb:a-pname";
    public static final String ATTACH_APPLICATION_API_VERSION = "iadb:a-av";
}
