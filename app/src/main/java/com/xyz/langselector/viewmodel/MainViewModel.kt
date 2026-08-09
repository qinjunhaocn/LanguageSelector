package com.xyz.langselector.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xyz.langselector.data.PrefsManager
import com.xyz.langselector.data.model.AppInfo
import com.xyz.langselector.data.model.LanguageInfo
import com.xyz.langselector.service.IUserService
import com.xyz.langselector.service.ShizukuManager
import com.xyz.langselector.util.AppUtils
import com.xyz.langselector.util.LocaleUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ShizukuState {
    NOT_CHECKED, NOT_RUNNING, NO_PERMISSION, READY
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PrefsManager(application)
    private val shizukuManager = ShizukuManager(application)

    private val _shizukuState = MutableStateFlow(ShizukuState.NOT_CHECKED)
    val shizukuState: StateFlow<ShizukuState> = _shizukuState.asStateFlow()

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedApp = MutableStateFlow<AppInfo?>(null)
    val selectedApp: StateFlow<AppInfo?> = _selectedApp.asStateFlow()

    private val _currentLocale = MutableStateFlow("")
    val currentLocale: StateFlow<String> = _currentLocale.asStateFlow()

    private val _showLanguagePicker = MutableStateFlow(false)
    val showLanguagePicker: StateFlow<Boolean> = _showLanguagePicker.asStateFlow()

    private val _showSystemApps = MutableStateFlow(false)
    val showSystemApps: StateFlow<Boolean> = _showSystemApps.asStateFlow()

    private val _pinnedApps = MutableStateFlow<Set<String>>(emptySet())
    val pinnedApps: StateFlow<Set<String>> = _pinnedApps.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    init {
        _showSystemApps.value = prefsManager.getShowSystemApps()
        _pinnedApps.value = prefsManager.getPinnedApps()

        shizukuManager.onShizukuAvailable = { checkShizukuState() }
        shizukuManager.onShizukuUnavailable = { _shizukuState.value = ShizukuState.NOT_RUNNING }
        shizukuManager.onPermissionResult = { granted ->
            if (granted) {
                _shizukuState.value = ShizukuState.READY
                shizukuManager.bindUserService()
            } else {
                _shizukuState.value = ShizukuState.NO_PERMISSION
            }
        }
        shizukuManager.onServiceConnected = { loadApps() }
        shizukuManager.onServiceDisconnected = { }

        shizukuManager.addListeners()
        checkShizukuState()
    }

    fun setSelectedTab(index: Int) {
        _selectedTabIndex.value = index
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSystemApps() {
        val newValue = !_showSystemApps.value
        _showSystemApps.value = newValue
        prefsManager.setShowSystemApps(newValue)
        loadApps()
    }

    private fun checkShizukuState() {
        when {
            !shizukuManager.isApiSupported -> {
                _shizukuState.value = ShizukuState.NOT_RUNNING
            }
            !shizukuManager.isShizukuRunning -> {
                _shizukuState.value = ShizukuState.NOT_RUNNING
            }
            !shizukuManager.hasPermission -> {
                _shizukuState.value = ShizukuState.NO_PERMISSION
            }
            else -> {
                _shizukuState.value = ShizukuState.READY
                shizukuManager.bindUserService()
            }
        }
    }

    fun requestPermission() {
        shizukuManager.requestPermission()
    }

    fun retryConnection() {
        checkShizukuState()
    }

    private fun loadApps() {
        if (shizukuManager.getUserService() == null) return
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val appList = AppUtils.getInstalledApps(
                    getApplication(),
                    includeSystemApps = _showSystemApps.value
                )
                _apps.value = appList
            } catch (e: Exception) {
                _message.value = "Failed to load apps: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectApp(app: AppInfo) {
        _selectedApp.value = app
        _currentLocale.value = ""
        _showLanguagePicker.value = true
        loadCurrentLocale(app.packageName)
    }

    fun dismissLanguagePicker() {
        _showLanguagePicker.value = false
        _selectedApp.value = null
        _currentLocale.value = ""
    }

    private fun loadCurrentLocale(packageName: String) {
        val service = shizukuManager.getUserService() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val locale = service.getApplicationLocales(packageName)
                _currentLocale.value = locale
            } catch (e: Exception) {
                _message.value = "Failed to get locale: ${e.message}"
            }
        }
    }

    fun setLocale(language: LanguageInfo) {
        val app = _selectedApp.value ?: return
        val service = shizukuManager.getUserService() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                service.setApplicationLocales(app.packageName, language.tag)
                service.forceStopPackage(app.packageName)

                _apps.value = _apps.value.map {
                    if (it.packageName == app.packageName) {
                        it.copy(currentLocale = language.tag)
                    } else it
                }
                _currentLocale.value = language.tag
                _message.value = "Language set to ${language.displayName}"
                _showLanguagePicker.value = false
                _selectedApp.value = null
            } catch (e: Exception) {
                _message.value = "Failed to set locale: ${e.message}"
            }
        }
    }

    fun forceStopApp(app: AppInfo) {
        val service = shizukuManager.getUserService() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                service.forceStopPackage(app.packageName)
                _message.value = "${app.name} stopped"
            } catch (e: Exception) {
                _message.value = "Failed to stop: ${e.message}"
            }
        }
    }

    fun togglePin(packageName: String) {
        val added = prefsManager.togglePinnedApp(packageName)
        _pinnedApps.value = prefsManager.getPinnedApps()
        _message.value = if (added) "Pinned" else "Unpinned"
    }

    fun clearMessage() {
        _message.value = null
    }

    val filteredApps: List<AppInfo>
        get() {
            val query = _searchQuery.value
            val allApps = _apps.value
            val pinned = _pinnedApps.value

            val sorted = allApps.sortedWith(
                compareByDescending<AppInfo> { it.packageName in pinned }
                    .thenBy { it.name.lowercase() }
            )

            return if (query.isBlank()) {
                sorted
            } else {
                val lowerQuery = query.lowercase()
                sorted.filter {
                    it.name.lowercase().contains(lowerQuery) ||
                    it.packageName.lowercase().contains(lowerQuery)
                }
            }
        }

    override fun onCleared() {
        super.onCleared()
        shizukuManager.removeListeners()
        shizukuManager.unbindUserService()
    }
}
