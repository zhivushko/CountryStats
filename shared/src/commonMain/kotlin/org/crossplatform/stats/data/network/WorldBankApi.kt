package org.crossplatform.stats.data.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.crossplatform.stats.core.suspendRunCatching
import org.crossplatform.stats.data.network.dto.CountryDto
import org.crossplatform.stats.data.network.dto.IndicatorRowDto

class WorldBankApi(private val client: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun fetchCountriesPage(
        page: Int = 1,
        perPage: Int = 15
    ): Result<PaginatedResult<CountryDto>> = suspendRunCatching {
        val text = client.get("country") {
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("format", "json")
        }.bodyAsText()
        parsePaginated(text) { json.decodeFromJsonElement(CountryDto.serializer(), it) }
    }

    suspend fun fetchCountry(code: String): Result<CountryDto?> = suspendRunCatching {
        val text = client.get("country/$code") {
            parameter("format", "json")
        }.bodyAsText()
        parsePaginated(text) { json.decodeFromJsonElement(CountryDto.serializer(), it) }
            .items.firstOrNull()
    }

    suspend fun fetchCountryIndicator(
        countryCode: String,
        indicatorCode: String,
        date: String? = null
    ): Result<Double?> = suspendRunCatching {
        val text = client.get("country/$countryCode/indicator/$indicatorCode") {
            parameter("format", "json")
            if (date != null) parameter("date", date) else parameter("mrv", 1)
        }.bodyAsText()
        val parsed = parsePaginated(text) { json.decodeFromJsonElement(IndicatorRowDto.serializer(), it) }
        val value = parsed.items.firstOrNull()?.value
        if (value == null && date != null) {
            val fallbackText = client.get("country/$countryCode/indicator/$indicatorCode") {
                parameter("format", "json")
                parameter("mrv", 1)
            }.bodyAsText()
            parsePaginated(fallbackText) { json.decodeFromJsonElement(IndicatorRowDto.serializer(), it) }
                .items.firstOrNull()?.value
        } else {
            value
        }
    }

    suspend fun fetchIndicatorPage(
        indicatorCode: String,
        page: Int = 1,
        perPage: Int = 300,
        date: String? = null
    ): Result<PaginatedResult<IndicatorRowDto>> = suspendRunCatching {
        val text = client.get("country/all/indicator/$indicatorCode") {
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("format", "json")
            if (date != null) parameter("date", date) else parameter("mrv", 1)
        }.bodyAsText()
        parsePaginated(text) { json.decodeFromJsonElement(IndicatorRowDto.serializer(), it) }
    }

    private fun <T> parsePaginated(
        text: String,
        decodeItem: (JsonElement) -> T
    ): PaginatedResult<T> {
        val root: JsonElement = json.parseToJsonElement(text)

        if (root !is JsonArray || root.size < 2) {
            throw IllegalStateException("Unexpected response format: $text")
        }

        val meta = root[0] as? JsonObject ?: JsonObject(emptyMap())
        val rows = root[1] as? JsonArray ?: JsonArray(emptyList())

        val page = meta["page"]?.jsonPrimitive?.int ?: 1
        val pages = (meta["pages"]?.jsonPrimitive?.int ?: 1).coerceAtLeast(1)

        val items = rows.mapNotNull { el ->
            try { decodeItem(el) } catch (e: Exception) { null }
        }
        return PaginatedResult(items, page, pages)
    }
}

data class PaginatedResult<T>(
    val items: List<T>,
    val page: Int,
    val pages: Int
)
