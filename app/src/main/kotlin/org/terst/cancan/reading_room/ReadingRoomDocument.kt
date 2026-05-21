package org.terst.cancan.reading_room

data class ReadingRoomDocument(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val assetPath: String
)
