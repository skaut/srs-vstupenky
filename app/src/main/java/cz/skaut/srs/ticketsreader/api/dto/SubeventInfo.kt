package cz.skaut.srs.ticketsreader.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubeventInfo(
    val id: Int,
    val name: String,
) {
    override fun toString(): String {
        return name
    }
}
