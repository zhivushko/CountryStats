package org.crossplatform.stats.data.network

import org.crossplatform.stats.data.network.dto.CountryDto
import org.crossplatform.stats.domain.model.Country

fun CountryDto.toModel(
    popularity: Int? = null,
    gdp: Long? = null,
    urbTot: Float? = null,
    rurTot: Float? = null
): Country = Country(
    id = id.trim().uppercase(),
    code = iso2Code.trim(),
    name = name.trim(),
    capital = capitalCity?.trim() ?: "",
    region = region?.value?.trim() ?: "",
    popularity = popularity ?: 0,
    urb_tot = urbTot ?: 0f,
    rur_tot = rurTot ?: 0f,
    GDP = gdp ?: 0L
)