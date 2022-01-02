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

class ApiClient() {
    val client = HttpClient() {
        install(JsonFeature)

        val token: String = Preferences.apiToken ?: throw Exception()

        defaultRequest {
            header("Api-Token", token)
        }
    }

    suspend fun getSeminarInfo(): SeminarInfo {
        checkConnectionPreferences()

        val response: SeminarInfo = client.get(Preferences.apiUrl + "tickets/seminar")

        return response
    }

    suspend fun checkTicket(userId: Int): TicketCheckInfo {
        checkConnectionPreferences()

        val response: HttpResponse =
            client.get(Preferences.apiUrl + "tickets/check-ticket/?userId=" + userId + "&subeventId=" + Preferences.selectedSubeventId)
        val responseObject: TicketCheckInfo = response.receive()

        return responseObject
    }

    private fun checkConnectionPreferences() {
        if (Preferences.apiUrl == null || Preferences.apiToken == null) {
            throw Exception() // todo
        }
    }
}
