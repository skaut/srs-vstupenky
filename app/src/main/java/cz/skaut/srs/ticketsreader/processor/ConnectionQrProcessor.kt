package cz.skaut.srs.ticketsreader.processor

import android.app.Activity
import android.content.Context
import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.ApiException
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.json.JSONTokener

class ConnectionQrProcessor(context: Context) : QrProcessor(context) {
    override fun process(value: String) {
        val qrJson = JSONTokener(value).nextValue() as JSONObject
        val apiUrl = qrJson.getString("apiUrl")
        val apiToken = qrJson.getString("apiToken")
        Preferences.setConnectionInfo(apiUrl, apiToken)

        val apiClient = ApiClient()
        try {
            val seminarInfo: SeminarInfo
            runBlocking {
                seminarInfo = apiClient.getSeminarInfo()
            }
            Preferences.setSeminarInfo(seminarInfo)
        } catch (e: ApiException) {
            Preferences.removeConnectionInfo()
            showAlertDialog(e.message)
        }

        if (context is Activity) context.finish()
    }
}