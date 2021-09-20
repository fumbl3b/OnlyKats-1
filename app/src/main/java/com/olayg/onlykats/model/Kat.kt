package com.olayg.onlykats.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class Kat(
    val breeds: List<Breed> = listOf(),
    val categories: List<Category>?,
    val height: Int,
    @PrimaryKey() val id: String,
    val url: String,
    val width: Int
)