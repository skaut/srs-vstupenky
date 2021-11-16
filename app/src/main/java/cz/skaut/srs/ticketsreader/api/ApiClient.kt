package cz.skaut.srs.ticketsreader.api

import android.content.Context
import android.widget.Toast
import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.api.dto.ConnectionInfo
import cz.skaut.srs.ticketsreader.api.dto.TicketInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ApiClient(private val context: Context) {
    val client = HttpClient() {
        install(JsonFeature)

        val token: String = Preferences.apiToken ?: throw Exception()

        defaultRequest {
            header("Api-Token", token)
        }
    }

    suspend fun getConnectionInfo(): ConnectionInfo {
        checkConnectionPreferences()

        val response: ConnectionInfo = client.get(Preferences.apiUrl + "tickets/connect")

        return response
    }

    suspend fun getTicketInfo(id: String): TicketInfo {
        checkConnectionPreferences()

        val response: HttpResponse = client.get(Preferences.apiUrl + "tickets/check-ticket") {
            parameter("id", id)
        }
        val responseObject: TicketInfo = response.receive()

        return responseObject
    }

    private fun checkConnectionPreferences() {
        if (Preferences.apiUrl == null || Preferences.apiToken == null) {
            throw Exception() // todo
        }
    }

}