package cz.skaut.srs.ticketsreader.api

import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import cz.skaut.srs.ticketsreader.api.dto.TicketCheckInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ApiClient() {
    val client = HttpClient() {
        checkConnectionPreferences()

        expectSuccess = false

        install(JsonFeature)

        val token: String = Preferences.apiToken ?: throw Exception()
        defaultRequest {
            header("Api-Token", token)
        }
    }

    suspend fun getSeminarInfo(): SeminarInfo {
        val response: HttpResponse = client.get(Preferences.apiUrl + "tickets/seminar")
        if (response.status != HttpStatusCode.OK) {
            throw ApiException(response.readText())
        }
        return response.receive()
    }

    suspend fun checkTicket(userId: Int): TicketCheckInfo {
        val response: HttpResponse = client.get(
            Preferences.apiUrl + "tickets/check-ticket/?userId=" + userId
                    + "&subeventId=" + Preferences.selectedSubeventId
        )
        if (response.status != HttpStatusCode.OK) {
            throw ApiException(response.readText())
        }
        return response.receive()
    }

    private fun checkConnectionPreferences() {
        if (Preferences.apiUrl == null || Preferences.apiToken == null) {
            throw ApiConfigException()
        }
    }
}
