package com.example.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.asResource(): Flow<Resource<T>> =
    this.map<T, Resource<T>> { Resource.Success(it) }
        .catch { emit(Resource.Error(it.message ?: "Unknown error")) }
