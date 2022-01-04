package cz.skaut.srs.ticketsreader.processor

import android.app.Activity
import android.content.Context
import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.ApiConfigException
import cz.skaut.srs.ticketsreader.api.ApiException
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

class ConnectionQrProcessor(context: Context) : QrProcessor(context) {
    override fun process(value: String) {
        try {
            val qrJson = JSONTokener(value).nextValue() as JSONObject // todo: pouzit dto
            val apiUrl = qrJson.getString("apiUrl")
            val apiToken = qrJson.getString("apiToken")
            Preferences.setConnectionInfo(apiUrl, apiToken)

            val apiClient = ApiClient()
            val seminarInfo: SeminarInfo
            runBlocking {
                seminarInfo = apiClient.getSeminarInfo()
            }
            Preferences.setSeminarInfo(seminarInfo)
        } catch (e: JSONException) {
            // todo: hlaska o nevalidnim QR kodu
        } catch (e: ApiConfigException) {
            // todo: hlaska o chybejicim nastaveni
        } catch (e: ApiException) {
            Preferences.removeConnectionInfo()
            showErrorDialog(e.message) // todo: extrakce z JSON
        }

        if (context is Activity) context.finish()
    }
}