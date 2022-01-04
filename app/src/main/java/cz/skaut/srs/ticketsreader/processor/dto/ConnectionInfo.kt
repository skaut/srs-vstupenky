package cz.skaut.srs.ticketsreader.processor.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionInfo(
    val apiUrl: String,
    val apiToken: String,
)
