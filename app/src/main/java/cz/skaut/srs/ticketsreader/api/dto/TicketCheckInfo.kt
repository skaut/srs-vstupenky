package cz.skaut.srs.ticketsreader.api.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TicketCheckInfo(
    val displayName: String,
    val roles: Array<String>,
    val subevents: Array<SubeventInfo>,
    val subeventChecks: Array<LocalDateTime>,
)