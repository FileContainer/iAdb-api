# iAdb-api
## Summary
iAdb-api is a simplified version of [Shizuku-API](https://github.com/RikkaApps/Shizuku-API), only supporting Android 11 and above.

## Changes
- Add more comprehensive document
- Renaming of certain package names
- Remove the support for the `sui` function
- Delete unused code related to supporting old versions v11 and v13 of `Shizuku`.

## Usage
### Prebuild
Download the `aidl-release.aar`, `api-release.aar`, `provider-release.aar`, and `shared-release.aar` files in [release](./release/) dir, copy them to the `app/libs` directory, and configure implementation in `app/build.gradle`.

### Configuration
Configure in `AndroidManifest.xml`.
```xml
<provider
    android:name="com.iadb.IadbProvider"
    android:authorities="${applicationId}.iadb"
    android:directBootAware="true"
    android:enabled="true"
    android:exported="true"
    android:multiprocess="false"
    android:permission="android.permission.INTERACT_ACROSS_USERS_FULL" />
```
### Connect with iAdb app
```kotlin
private var code = 101
private var binder: IBinder? = null

private val receiverListener = Iadb.OnBinderReceivedListener {
        if (checkOrRequestPermission(code)) {
            if (Utils.isUiThread()) {
                post {
                    bindUserService()
                }
            } else {
                bindUserService()
            }
        }
    }


private val deadListener = Iadb.OnBinderDeadListener {
    //Do anything you want, for example
    //removeAllListener()
    //Iadb.unbindUserService()
}

private val permissionResultListener =
    Iadb.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            if (Utils.isUiThread()) {
                post {
                    bindUserService()
                }
            } else {
                bindUserService()
            }
        }
    }

//Suggest just only run once
fun connect() {
    Iadb.addBinderReceivedListener(receiverListener)
    Iadb.addBinderDeadListener(deadListener)
    Iadb.addRequestPermissionResultListener(permissionResultListener)
    if (checkOrRequestPermission(code)) {
        bindUserService()
    } else {
        binder = null
    }
}

private fun bindUserService() {
    if (binder != null) return
    userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder != null && binder.pingBinder()) {
                this.binder = binder
            } else {
                this.binder = null
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
                this.binder = null
            }
        }

    userServiceArgs = UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID, UserService::class.java.name))
        .daemon(false)
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)
    connectCallback?.onConnectStart()
    try {
        Iadb.bindUserService(userServiceArgs!!, userServiceConnection!!)
    } catch (e: Throwable) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
        binder = null
    }
}

private fun checkOrRequestPermission(code: Int): Boolean {
    try {
        return if (Iadb.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            true
        } else if (Iadb.shouldShowRequestPermissionRationale()) {
            false
        } else {
            Iadb.requestPermission(code)
            return runBlocking {
                permissionResult()
            }
        }
    } catch (e: Throwable) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }
    return false
}

private suspend fun permissionResult(): Boolean {
    return suspendCancellableCoroutine {
        val backgroundThread = Thread {
            Iadb.addRequestPermissionResultListener(object : Iadb.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(p0: Int, p1: Int) {
                    Iadb.removeRequestPermissionResultListener(this)
                    if (it.isActive) {
                        if (p1 == PERMISSION_GRANTED) {
                            it.resumeWith(Result.success(true))
                        } else {
                            it.resumeWith(Result.success(false))
                        }
                    }
                }
            }, Looper.myLooper()?.let { it1 -> Handler(it1) })
        }
        backgroundThread.start()
    }
}

//Check whether iAdb is installed and detect the Server in iAdb
fun installedAndRunning(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Iadb.pingBinder()
}

//Is your custom app currently connected to iAdb Server?
fun connected(): Boolean {
    return binder != null
}

//Before running your operation, check the connection with iAdb
private fun isAdbShellReady(): Boolean {
    if (!installedAndRunning()) return false
    if (!checkOrRequestPermission(code)) return false
    if (binder == null) {
        return false
    }
    return true
}

//example
//Your custom operation
fun getFD(
    path: String,
    mode: Int = ParcelFileDescriptor.MODE_READ_WRITE or ParcelFileDescriptor.MODE_CREATE
): ParcelFileDescriptor? {
    if (!isAdbShellReady()) return null
    val service = IUserService.Stub.asInterface(binder)
    return service.getFD(path, mode)
}
```
### Your Operation
```aidl
// IUserService.aidl
// don't change destory TRANSACTION code
interface IUserService {
    void destroy() = 57320; // Destroy method defined by iAdb server

    void exit() = 1; // Exit method defined by user

    ParcelFileDescriptor getFD(String path,int mode) = 3;
}

```

```kotlin
class UserService() : IUserService.Stub() {
    override fun destroy() {
        exitProcess(0)
    }

    @Keep
    constructor(context: Context) : this() {
    }

    override fun exit() {
        destroy()
    }

    // example
    // This function can get the fd that the system app can access
    override fun getFD(path: String?, mode: Int): ParcelFileDescriptor? {
        if (path == null) return null
        return try {
            ParcelFileDescriptor.open(
                File(path),
                mode
            )
        } catch (e: FileNotFoundException) {
            null
        }
    }
}
```