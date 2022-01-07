package cz.skaut.srs.ticketsreader.processor

import android.app.Activity
import android.content.Context
import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.R
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.ApiConfigException
import cz.skaut.srs.ticketsreader.api.ApiConnectionException
import cz.skaut.srs.ticketsreader.api.ApiErrorResponseException
import cz.skaut.srs.ticketsreader.api.ApiSerializationException
import cz.skaut.srs.ticketsreader.api.ApiUnknownErrorException
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import cz.skaut.srs.ticketsreader.processor.dto.ConnectionInfo
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class ConnectionQrProcessor(context: Context) : QrProcessor(context) {
    override fun process(value: String) {
        try {
            val connectionInfo = Json.decodeFromString(ConnectionInfo.serializer(), value)
            val apiUrl = connectionInfo.apiUrl
            val apiToken = connectionInfo.apiToken
            Preferences.setConnectionInfo(apiUrl, apiToken)

            val apiClient = ApiClient()
            val seminarInfo: SeminarInfo
            runBlocking {
                seminarInfo = apiClient.getSeminarInfo()
            }
            Preferences.setSeminarInfo(seminarInfo)

            if (context is Activity) context.finish()
        } catch (e: SerializationException) {
            showErrorDialog(R.string.dialog_error_message_invalid_connection_qr)
        } catch (e: ApiConfigException) {
            Preferences.removeConnectionInfo()
            showErrorDialog(R.string.dialog_error_message_api_config_error)
        } catch (e: ApiConnectionException) {
            Preferences.removeConnectionInfo()
            showErrorDialog(R.string.dialog_error_message_api_connection_error)
        } catch (e: ApiUnknownErrorException) {
            Preferences.removeConnectionInfo()
            showErrorDialog(R.string.dialog_error_message_api_unknown_error)
        } catch (e: ApiSerializationException) {
            Preferences.removeConnectionInfo()
            showErrorDialog(R.string.dialog_error_message_api_serialization_error)
        } catch (e: ApiErrorResponseException) {
            Preferences.removeConnectionInfo()
            showErrorDialog(e.message)
        }
    }
}
