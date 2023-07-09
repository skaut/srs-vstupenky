package cz.skaut.srs.ticketsreader.processor.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionInfo(
    val srsUrl: String,
    val apiToken: String,
)
