package com.iadb.server;

interface IIadbBinderServiceConnection {

    oneway void connected(IBinder service);

    oneway void died();
}
