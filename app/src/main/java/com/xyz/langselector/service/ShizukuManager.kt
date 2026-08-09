package com.xyz.langselector.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import rikka.shizuku.Shizuku

/**
 * Manages the Shizuku connection lifecycle: checking availability, requesting
 * permission, and binding/unbinding the UserService.
 */
class ShizukuManager(private val context: Context) {

    private var userService: IUserService? = null
    private var serviceConnection: ServiceConnection? = null
    private var userServiceArgs: Shizuku.UserServiceArgs? = null

    var onShizukuAvailable: (() -> Unit)? = null
    var onShizukuUnavailable: (() -> Unit)? = null
    var onPermissionResult: ((Boolean) -> Unit)? = null
    var onServiceConnected: ((IUserService) -> Unit)? = null
    var onServiceDisconnected: (() -> Unit)? = null

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        onShizukuAvailable?.invoke()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        onShizukuUnavailable?.invoke()
    }

    private val permissionResultListener =
        Shizuku.OnRequestPermissionResultListener { _, grantResult ->
            val granted = grantResult == PackageManager.PERMISSION_GRANTED
            onPermissionResult?.invoke(granted)
        }

    val isShizukuRunning: Boolean
        get() = try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }

    val hasPermission: Boolean
        get() = try {
            if (isShizukuRunning) {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }

    val isApiSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun addListeners() {
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
    }

    fun removeListeners() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
    }

    fun requestPermission(requestCode: Int = REQUEST_CODE) {
        try {
            Shizuku.requestPermission(requestCode)
        } catch (e: Exception) {
            onPermissionResult?.invoke(false)
        }
    }

    fun bindUserService() {
        if (!isShizukuRunning || !hasPermission) {
            onServiceDisconnected?.invoke()
            return
        }

        val existingService = userService
        if (existingService != null) {
            onServiceConnected?.invoke(existingService)
            return
        }

        val args = Shizuku.UserServiceArgs(
            ComponentName(context.packageName, UserService::class.java.name)
        )
            .processNameSuffix("user_service")
            .debuggable(false)
            .version(SERVICE_VERSION)
        userServiceArgs = args

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val boundService = IUserService.Stub.asInterface(service)
                userService = boundService
                onServiceConnected?.invoke(boundService)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                userService = null
                onServiceDisconnected?.invoke()
            }
        }

        try {
            Shizuku.bindUserService(args, serviceConnection!!)
        } catch (e: Exception) {
            onServiceDisconnected?.invoke()
        }
    }

    fun unbindUserService() {
        val args = userServiceArgs
        val conn = serviceConnection
        if (args != null) {
            try {
                Shizuku.unbindUserService(args, conn, true)
            } catch (e: Exception) {
                // Ignore
            }
        }
        userServiceArgs = null
        serviceConnection = null
        userService = null
    }

    fun getUserService(): IUserService? = userService

    companion object {
        private const val REQUEST_CODE = 1001
        private const val SERVICE_VERSION = 1
    }
}
