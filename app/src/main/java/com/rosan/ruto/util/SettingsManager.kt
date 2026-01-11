package com.rosan.ruto.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SettingsManager {
    private const val PREFS_NAME = "ruto_settings"
    private const val KEY_PERMISSION_PROVIDER = "permission_provider"
    private const val KEY_TERMINAL_SHELL = "terminal_shell"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun savePermissionProvider(
        context: Context,
        provider: PermissionProvider,
        shell: String? = null
    ) {
        getPrefs(context).edit {
            putString(KEY_PERMISSION_PROVIDER, provider.name)
            if (shell != null) {
                putString(KEY_TERMINAL_SHELL, shell)
            }
        }
    }

    fun getPermissionProvider(context: Context): PermissionProvider? {
        val providerName = getPrefs(context).getString(KEY_PERMISSION_PROVIDER, null)
        return providerName?.let { PermissionProvider.valueOf(it) }
    }

    fun getTerminalShell(context: Context): String {
        return getPrefs(context).getString(KEY_TERMINAL_SHELL, "su") ?: "su"
    }

    fun reset(context: Context) {
        getPrefs(context).edit { clear() }
    }
}
