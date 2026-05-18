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
    val method: String,
    @SerialName("safety_notes") val safetyNotes: String = "",
    val source: String,
    @SerialName("altitude_note") val altitudeNote: String = "",
    val entries: List<ProcessingEntry>
)

@Serializable
data class ProcessingEntry(
    @SerialName("jar_size") val jarSize: String,
    val pack: String,
    @SerialName("processing_time_minutes") val processingTimeMinutes: Int,
    @SerialName("pressure_psi_weighted") val pressurePsiWeighted: Int? = null,
    @SerialName("pressure_psi_dial") val pressurePsiDial: Int? = null
)
