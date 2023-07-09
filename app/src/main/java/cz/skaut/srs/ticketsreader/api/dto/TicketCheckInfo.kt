package cz.skaut.srs.ticketsreader.api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketCheckInfo(
    @SerialName("attendee_name") val attendeeName: String,
    @SerialName("attendee_age") val attendeeAge: Int,
    @SerialName("attendee_photo") val attendeePhoto: String,
    val roles: Array<String>,
    val subevents: Array<SubeventInfo>,
    @SerialName("has_subevent") val hasSubevent: Boolean,
    @SerialName("subevent_checks") val subeventChecks: Array<Instant>,
)
