package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val regionId: String,       // State ID (e.g. "MH") or "District ID"
    val regionName: String,     // E.g., "Maharashtra" or "Pune"
    val regionType: String,     // "State" or "District"
    val notes: String = "",     // Custom perspective notes
    val customTilt: Float = 0f, // 3D Tilt angle preserved
    val customZoom: Float = 1f, // Current zoom multiplier
    val timestamp: Long = System.currentTimeMillis()
)
