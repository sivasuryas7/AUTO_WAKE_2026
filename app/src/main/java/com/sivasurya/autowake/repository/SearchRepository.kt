package com.sivasurya.autowake.repository

import com.sivasurya.autowake.api.RetrofitClient
import com.sivasurya.autowake.model.Place

class SearchRepository {

    suspend fun search(query: String): List<Place> {
        return RetrofitClient.api.searchPlaces(query)
    }
}