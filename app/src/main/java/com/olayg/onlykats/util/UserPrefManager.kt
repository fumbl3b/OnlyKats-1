package com.olayg.onlykats.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.olayg.onlykats.model.request.Queries
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


// TODO: implement this
class UserPrefManager private constructor(private val dataStore: DataStore<Preferences>) {
//
//    val queries
//        get() = dataStore.data.map { preferences ->
//            preferences[PreferenceKeys.ENDPOINT]?.let {
//                Queries(
//                    endPoint = EndPoint.valueOf(it),
//                    limit = preferences[PreferenceKeys.LIMIT] ?: 10,
//                    page = 0
//                )
//            }
//        }

    companion object {
        private var INSTANCE: UserPrefManager? = null

        fun getInstance(context: Context): UserPrefManager {
            if (INSTANCE == null) INSTANCE = UserPrefManager(context.dataStore)
            return INSTANCE!!
        }
    }
}