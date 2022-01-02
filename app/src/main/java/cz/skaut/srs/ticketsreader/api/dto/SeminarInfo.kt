package cz.skaut.srs.ticketsreader.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeminarInfo(
    val name: String,
    val subevents: Array<SubeventInfo>,
)
