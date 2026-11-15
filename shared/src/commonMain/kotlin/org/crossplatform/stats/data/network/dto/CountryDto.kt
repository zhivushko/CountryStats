package org.crossplatform.stats.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    @SerialName("id") val id: String,
    @SerialName("iso2Code") val iso2Code: String,
    @SerialName("name") val name: String,
    @SerialName("capitalCity") val capitalCity: String? = null,
    @SerialName("region") val region: RegionDto? = null
) {
    val isAggregate: Boolean get() = region?.value?.trim() == "Aggregates"
}

@Serializable
data class RegionDto(
    @SerialName("value") val value: String? = null
)