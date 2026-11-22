package org.crossplatform.stats.domain.model


data class Country(
    val id: String,
    val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val popularity: Int,
    val urb_tot: Float,
    val rur_tot: Float,
    val GDP: Long,
)