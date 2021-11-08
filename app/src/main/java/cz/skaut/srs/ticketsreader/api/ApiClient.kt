package cz.skaut.srs.ticketsreader.api

import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.api.dto.ConnectionInfo
import java.lang.Exception

class ApiClient() {
    fun getConnectionInfo(): ConnectionInfo {
        checkConnectionSettings()

        // todo: test SRS connection, get info (SRS name)
//        val result = Fuel.get(config.apiUrl!! + "/name").authentication().bearer(config.apiToken!!).responseObject(JSONObject).responseObject(ResponseDeserializable<ConnectionInfo>)
//        return result

        val connectionInfo = ConnectionInfo()
        connectionInfo.name = "Miquik 2021"

        return connectionInfo
    }

    private fun checkConnectionSettings(): Unit {
        if (Preferences.apiUrl == null || Preferences.apiToken == null) {
            throw Exception() // todo
        }
    }
}