package cz.skaut.srs.ticketsreader.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionInfo (
    val seminar_name: String
)
