package cz.skaut.srs.ticketsreader.processor

import android.app.Activity
import android.content.Context
import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.json.JSONTokener

class ConnectionQrProcessor(context: Context) : QrProcessor(context) {
    override fun process(value: String) {
        val jsonObject = JSONTokener(value).nextValue() as JSONObject

        val apiUrl = jsonObject.getString("apiUrl")
        val apiToken = jsonObject.getString("apiToken")
        Preferences.setConnectionInfo(apiUrl, apiToken)

        val apiClient = ApiClient()

        val seminarInfo: SeminarInfo
        runBlocking {
            seminarInfo = apiClient.getSeminarInfo()
        }

        Preferences.setSeminarInfo(seminarInfo)

        if (context is Activity) context.finish()
    }
}