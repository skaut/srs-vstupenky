package cz.skaut.srs.ticketsreader.api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketCheckInfo(
    /** Jméno účastníka. */
    @SerialName("attendee_name") val attendeeName: String,

    /** Věk účastníka. */
    @SerialName("attendee_age") val attendeeAge: Int,

    /** Odkaz na fotku účastníka. */
    @SerialName("attendee_photo") val attendeePhoto: String? = null,

    /** Má účastník propojený účet? */
    @SerialName("attendee_member") val attendeeMember: Boolean,

    /** Role účastníka. */
    val roles: Array<String>,

    /** Podakce účastníka. */
    val subevents: Array<SubeventInfo>,

    /** Má účastník podakci? */
    @SerialName("has_subevent") val hasSubevent: Boolean,

    /** Seznam časů kontroly vstupenky. */
    @SerialName("subevent_checks") val subeventChecks: Array<Instant>,
)
