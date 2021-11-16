package cz.skaut.srs.ticketsreader.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketInfo (
    val displayName: String,
    val roles: String,
    val subevents: String
)