package cz.skaut.srs.ticketsreader

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlin.properties.Delegates

const val SRS_CONNECTED = "srsConnected"
const val SRS_NAME = "srsName"
const val API_URL = "apiUrl"
const val API_TOKEN = "apiToken"

class Preferences() {
    companion object {
        private lateinit var preferences: SharedPreferences

        var srsConnected by Delegates.notNull<Boolean>()
        var srsName : String? = null
        var apiUrl : String? = null
        var apiToken : String? = null

        fun init(context: Context) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context)
            refresh()
        }

        fun setConnectionInfo(apiUrl: String, apiToken: String) {
            val edit = preferences.edit()
            edit.putBoolean(SRS_CONNECTED, true)
            edit.putString(API_URL, apiUrl)
            edit.putString(API_TOKEN, apiToken)
            edit.apply()
            refresh()
        }

        fun setConnectionName(name: String) {
            val edit = preferences.edit()
            edit.putString(SRS_NAME, name)
            edit.apply()
            refresh()
        }

        fun removeConnectionInfo() {
            val edit = preferences.edit()
            edit.putBoolean(SRS_CONNECTED, false)
            edit.putString(SRS_NAME, null)
            edit.putString(API_URL, null)
            edit.putString(API_TOKEN, null)
            edit.apply()
            refresh()
        }

        private fun refresh() {
            srsConnected = preferences.getBoolean(SRS_CONNECTED, false)
            srsName = preferences.getString(SRS_NAME, null)
            apiUrl = preferences.getString(API_URL, null)
            apiToken = preferences.getString(API_TOKEN, null)
        }
    }
}