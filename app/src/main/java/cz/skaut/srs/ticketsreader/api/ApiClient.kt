package cz.skaut.srs.ticketsreader.api

import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import cz.skaut.srs.ticketsreader.api.dto.TicketCheckInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ApiClient {
    val client = HttpClient() {
        checkConnectionPreferences()

        expectSuccess = false

        install(JsonFeature)

        val token: String = Preferences.apiToken!!
        defaultRequest {
            header("Api-Token", token)
        }
    }

    suspend fun getSeminarInfo(): SeminarInfo {
        return getValidResponse("${Preferences.apiUrl}tickets/seminar")
    }

    suspend fun checkTicket(userId: Int): TicketCheckInfo {
        return getValidResponse(
            "${Preferences.apiUrl}tickets/check-ticket/?userId=$userId" +
                "&subeventId=${Preferences.selectedSubeventId}"
        )
    }

    private fun checkConnectionPreferences() {
        if (Preferences.apiUrl == null || Preferences.apiToken == null) {
            throw ApiConfigException()
        }
    }

    private suspend inline fun <reified T> getValidResponse(url: String): T {
        val response: HttpResponse
        try {
            response = client.get(url)
        } catch (e: Throwable) {
            throw ApiConnectionException(e)
        }

        if (response.status != HttpStatusCode.OK) {
            try {
                val message = Json.decodeFromString<String>(response.readText())
                throw ApiErrorResponseException(message)
            } catch (e: SerializationException) {
                throw ApiUnknownErrorException(e)
            }
        }

        try {
            return response.receive()
        } catch (e: SerializationException) {
            throw ApiSerializationException(e)
        }
    }
}
