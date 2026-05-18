package org.terst.cancan.reference.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferenceData(
    val version: Int,
    val items: List<ReferenceItem>
)

@Serializable
data class ReferenceItem(
    val id: String,
    val name: String,
    val category: String,
    // "lookup" for processing-time tables; "guide" for prose instructional content
    val type: String = "lookup",
    // lookup fields
    val method: String = "",
    @SerialName("altitude_note") val altitudeNote: String = "",
    val entries: List<ProcessingEntry> = emptyList(),
    // guide fields
    val summary: String = "",
    val difficulty: String = "",
    @SerialName("time_estimate") val timeEstimate: String = "",
    val sections: List<GuideSection> = emptyList(),
    // shared
    @SerialName("safety_notes") val safetyNotes: String = "",
    val source: String,
    @SerialName("wikipedia_title") val wikipediaTitle: String = ""
)

@Serializable
data class ProcessingEntry(
    @SerialName("jar_size") val jarSize: String,
    val pack: String,
    @SerialName("processing_time_minutes") val processingTimeMinutes: Int,
    @SerialName("pressure_psi_weighted") val pressurePsiWeighted: Int? = null,
    @SerialName("pressure_psi_dial") val pressurePsiDial: Int? = null
)

@Serializable
data class GuideSection(
    val heading: String,
    val body: String,
    val tips: List<String> = emptyList()
)
