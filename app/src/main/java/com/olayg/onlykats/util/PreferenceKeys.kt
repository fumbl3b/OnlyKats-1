package com.olayg.onlykats.util

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val ENDPOINT = stringPreferencesKey("endpoint")
    val LIMIT = intPreferencesKey("limit")
    val CATEGORY_IDS = intPreferencesKey("category_ids")
    val BREED_ID = stringPreferencesKey("breed_id")
}