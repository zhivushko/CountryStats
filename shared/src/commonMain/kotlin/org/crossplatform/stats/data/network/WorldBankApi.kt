package org.crossplatform.stats.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.crossplatform.stats.data.network.dto.CountryDto
import org.crossplatform.stats.data.network.dto.IndicatorRowDto

class WorldBankApi(private val client: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun getCountriesPage(
        page: Int = 1,
        perPage: Int = 300
    ): PaginatedResult<CountryDto> = safeCall {
        val text = client.get("country") {
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("format", "json")
        }.bodyAsText()
        parsePaginated(text) { json.decodeFromJsonElement(CountryDto.serializer(), it) }
    }

    suspend fun getIndicatorPage(
        indicatorCode: String,
        page: Int = 1,
        perPage: Int = 500,
        date: String? = null
    ): PaginatedResult<IndicatorRowDto> = safeCall {
        val text = client.get("country/all/indicator/$indicatorCode") {
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("format", "json")
            if (date != null) parameter("date", date)
        }.bodyAsText()
        parsePaginated(text) { json.decodeFromJsonElement(IndicatorRowDto.serializer(), it) }
    }

    suspend fun getCountry(code: String): CountryDto? = safeCall {
        val text = client.get("country/$code") {
            parameter("format", "json")
        }.bodyAsText()
        parsePaginated(text) { json.decodeFromJsonElement(CountryDto.serializer(), it) }
            .items.firstOrNull()
    }

    suspend fun getCountries(): List<CountryDto> = paginate { page ->
        getCountriesPage(page = page)
    }

    suspend fun getIndicatorValues(
        indicatorCode: String,
        date: String? = null
    ): Map<String, Double> {
        val map = mutableMapOf<String, Double>()
        paginate { page ->
            getIndicatorPage(indicatorCode, page = page, date = date)
        }.forEach { row ->
            val code = row.countryIso3Code?.trim()?.uppercase() ?: return@forEach
            val value = row.value ?: return@forEach
            map[code] = value
        }
        return map
    }

    private suspend fun <T> paginate(
        fetch: suspend (Int) -> PaginatedResult<T>
    ): List<T> {
        val all = mutableListOf<T>()
        var page = 1
        var pages = 1
        do {
            val result = fetch(page)
            all.addAll(result.items)
            pages = result.pages
            page++
        } while (page <= pages && page <= MAX_PAGES)
        return all
    }

    private fun <T> parsePaginated(
        text: String,
        decodeItem: (JsonElement) -> T
    ): PaginatedResult<T> {
        val root: JsonElement = try {
            json.parseToJsonElement(text)
        } catch (e: Exception) {
            throw IllegalStateException("bad response", e)
        }

        if (root !is JsonArray || root.size < 2) {
            throw IllegalStateException("bad response")
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

    private suspend fun <T> safeCall(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: HttpRequestTimeoutException) {
            throw IllegalStateException("timeout", e)
        } catch (e: IOException) {
            throw IllegalStateException("network", e)
        } catch (e: ResponseException) {
            throw IllegalStateException("server", e)
        } catch (e: SerializationException) {
            throw IllegalStateException("server", e)
        }
    }

    companion object {
        private const val MAX_PAGES = 50
    }
}

data class PaginatedResult<T>(
    val items: List<T>,
    val page: Int,
    val pages: Int
)