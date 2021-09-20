package com.olayg.onlykats.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Settings (
    val categories: List<Category>?,
    val breeds: List<Breed>?,
)