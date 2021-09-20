package com.olayg.onlykats.repo.local.util

import androidx.room.TypeConverter
import com.olayg.onlykats.model.Breed
import com.olayg.onlykats.model.Category
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class KatConverters {

    private val moshi by lazy { Moshi.Builder().build() }
    private val breedListAdapter by lazy {
        val type = Types.newParameterizedType(List::class.java, Breed::class.java)
        return@lazy moshi.adapter<List<Breed>>(type)
    }
    private val categoryListAdapter by lazy {
        val type = Types.newParameterizedType(List::class.java, Category::class.java)
        return@lazy moshi.adapter<List<Category>>(type)
    }


    @TypeConverter
    fun stringToBreedList(data: String?): List<Breed> {
        return data?.let { breedListAdapter.fromJson(it) } ?: emptyList()
    }

    @TypeConverter
    fun breedListToString(someObjects: List<Breed>): String {
        return breedListAdapter.toJson(someObjects)
    }

    @TypeConverter
    fun stringToCategoryList(data: String?): List<Category>? {
        return data?.let { categoryListAdapter.fromJson(it) }
    }

    @TypeConverter
    fun categoryListToString(someObjects: List<Category>): String {
        return categoryListAdapter.toJson(someObjects)
    }

    // over engineered solution:
//    private inline fun <reified T> Moshi.getGenericAdapter(): JsonAdapter<List<T>>? {
//        val type = Types.collectionElementType(List::class.java, T::class.java)
//        return Moshi.Builder().build().adapter(type)
//    }
}