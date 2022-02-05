package cz.skaut.srs.ticketsreader

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import cz.skaut.srs.ticketsreader.api.dto.SubeventInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

const val CONNECTED = "connected"
const val API_URL = "apiUrl"
const val API_TOKEN = "apiToken"
const val SEMINAR_NAME = "seminarName"
const val SUBEVENTS = "subevents"
const val SELECTED_SUBEVENT_ID = "selectedSubeventId"
const val SELECTED_SUBEVENT_POSITION = "selectedSubeventPosition"

class Preferences private constructor() {
    companion object {
        private lateinit var prefs: SharedPreferences

        var connected: Boolean = false
        var apiUrl: String? = null
        var apiToken: String? = null
        var seminarName: String? = null
        var subevents: Array<SubeventInfo> = emptyArray()
        var selectedSubeventId: Int = -1
        var selectedSubeventPosition: Int = -1

        fun init(context: Context) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            load()
        }

        fun setConnectionInfo(apiUrl: String, apiToken: String) {
            connected = true
            this.apiUrl = apiUrl
            this.apiToken = apiToken
            save()
        }

        fun removeConnectionInfo() {
            connected = false
            apiUrl = null
            apiToken = null
            removeSeminarInfo()
        }

        fun setSeminarInfo(seminarInfo: SeminarInfo) {
            seminarName = seminarInfo.name
            subevents = seminarInfo.subevents
            setSelectedSubevent(0)
        }

        fun removeSeminarInfo() {
            seminarName = null
            subevents = emptyArray()
            removeSelectedSubevent()
        }

        fun setSelectedSubevent(subeventPosition: Int) {
            selectedSubeventId = subevents[subeventPosition].id
            selectedSubeventPosition = subeventPosition
            save()
        }

        fun removeSelectedSubevent() {
            selectedSubeventId = -1
            selectedSubeventPosition = 0
            save()
        }

        private fun load() {
            connected = prefs.getBoolean(CONNECTED, false)
            apiUrl = prefs.getString(API_URL, null)
            apiToken = prefs.getString(API_TOKEN, null)
            seminarName = prefs.getString(SEMINAR_NAME, null)

            val subeventsStr = prefs.getString(SUBEVENTS, null)
            subevents = if (subeventsStr != null) Json.decodeFromString(
                serializer(),
                subeventsStr
            ) else emptyArray()

            selectedSubeventId = prefs.getInt(SELECTED_SUBEVENT_ID, -1)
            selectedSubeventPosition = prefs.getInt(SELECTED_SUBEVENT_POSITION, 0)
        }

        private fun save() {
            val edit = prefs.edit()
            edit.putBoolean(CONNECTED, connected)
            edit.putString(API_URL, apiUrl)
            edit.putString(API_TOKEN, apiToken)
            edit.putString(SEMINAR_NAME, seminarName)
            edit.putString(SUBEVENTS, Json.encodeToString(serializer(), subevents))
            edit.putInt(SELECTED_SUBEVENT_ID, selectedSubeventId)
            edit.putInt(SELECTED_SUBEVENT_POSITION, selectedSubeventPosition)
            edit.apply()
        }
    }
}
