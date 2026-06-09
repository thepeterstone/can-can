package org.terst.cancan.reference.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShoreFishData(
    val version: Int,
    val species: List<ShoreFish>
)

@Serializable
data class ShoreFish(
    val id: String,
    @SerialName("hawaiian_name") val hawaiianName: String,
    @SerialName("common_name") val commonName: String,
    @SerialName("scientific_name") val scientificName: String,
    @SerialName("wikipedia_title") val wikipediaTitle: String = "",
    @SerialName("fishing_category") val fishingCategory: String,
    val habitat: String = "",
    @SerialName("depth_range") val depthRange: String = "",
    @SerialName("best_season") val bestSeason: String = "",
    @SerialName("avg_size") val avgSize: String = "",
    @SerialName("edibility_rating") val edibilityRating: String = "",
    val regulations: String = "",
    @SerialName("identification_features") val identificationFeatures: List<String> = emptyList(),
    @SerialName("catch_methods") val catchMethods: List<String> = emptyList(),
    @SerialName("best_spots") val bestSpots: List<String> = emptyList(),
    val preparation: String = "",
    @SerialName("safety_note") val safetyNote: String = ""
)
