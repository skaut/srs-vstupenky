package cz.skaut.srs.ticketsreader.api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TicketCheckInfo(
    val attendee_name: String,
    val roles: Array<String>,
    val subevents: Array<SubeventInfo>,
    val has_subevent: Boolean,
    val subevent_checks: Array<Instant>,
)