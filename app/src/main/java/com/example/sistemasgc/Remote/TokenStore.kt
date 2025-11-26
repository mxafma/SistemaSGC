package com.example.sistemasgc.Remote

import android.content.Context
import com.example.sistemasgc.Remote.model.UserDto
import com.google.gson.Gson

object TokenStore {
    private const val PREFS_NAME = "sistemasgc_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_ROLE = "jwt_role"
    private const val KEY_USER = "jwt_user_json"

    private var tokenInMemory: String? = null
    private var roleInMemory: String? = null
    private var userJsonInMemory: String? = null
    private var prefsInitialized = false
    private lateinit var prefs: android.content.SharedPreferences

    private val gson = Gson()

    fun init(context: Context) {
        if (prefsInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        tokenInMemory = prefs.getString(KEY_TOKEN, null)
        roleInMemory = prefs.getString(KEY_ROLE, null)
        userJsonInMemory = prefs.getString(KEY_USER, null)
        prefsInitialized = true
    }

    fun setAuth(token: String?, role: String?, user: UserDto?) {
        tokenInMemory = token
        roleInMemory = role
        userJsonInMemory = user?.let { gson.toJson(it) }
        if (prefsInitialized) {
            prefs.edit().putString(KEY_TOKEN, token).putString(KEY_ROLE, role).putString(KEY_USER, userJsonInMemory).apply()
        }
    }

    fun getToken(): String? {
        return tokenInMemory
    }

    fun getRole(): String? {
        return roleInMemory
    }

    fun getUserJson(): String? {
        return userJsonInMemory
    }

    fun getUser(): UserDto? {
        return try {
            userJsonInMemory?.let { gson.fromJson(it, UserDto::class.java) }
        } catch (e: Exception) {
            null
        }
    }

    fun clear() {
        tokenInMemory = null
        roleInMemory = null
        userJsonInMemory = null
        if (prefsInitialized) prefs.edit().remove(KEY_TOKEN).remove(KEY_ROLE).remove(KEY_USER).apply()
    }
}
