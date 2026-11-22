package org.crossplatform.stats.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IndicatorCountryRef(
    @SerialName("id") val id: String? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class IndicatorRowDto(
    @SerialName("countryiso3code") val countryIso3Code: String? = null,
    @SerialName("country") val country: IndicatorCountryRef? = null,
    @SerialName("value") val value: Double? = null,
    @SerialName("date") val date: String? = null
)
