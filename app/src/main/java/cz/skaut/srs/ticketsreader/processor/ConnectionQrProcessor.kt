package cz.skaut.srs.ticketsreader.processor

import android.app.Activity
import android.content.Context
import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.api.ApiClient
import org.json.JSONObject
import org.json.JSONTokener

class ConnectionQrProcessor(
    private val context: Context
) : QrProcessor(context) {
    val apiClient = ApiClient()

    override fun process(value: String) {
        val jsonObject = JSONTokener(value).nextValue() as JSONObject

        val apiUrl = jsonObject.getString("apiUrl")
        val apiToken = jsonObject.getString("apiToken")
        Preferences.setConnectionInfo(apiUrl, apiToken)

        val connectionInfo = apiClient.getConnectionInfo()
        Preferences.setConnectionName(connectionInfo.name)

        if (context is Activity) context.finish()
    }
}