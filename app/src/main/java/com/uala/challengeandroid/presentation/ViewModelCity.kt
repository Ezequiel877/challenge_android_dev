package com.uala.challengeandroid.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uala.challengeandroid.data.CityRepository
import com.uala.challengeandroid.model.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val repository: CityRepository
) : ViewModel() {

    private val _cities = MutableStateFlow<List<City>>(emptyList())

    var query = MutableStateFlow("")
    val filteredCities = query
        .combine(_cities) { q, list ->
            if (q.isBlank()) list
            else list.filter { it.name.startsWith(q, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun loadCities() {
        viewModelScope.launch {
            val result = repository.getCities()
            _cities.value = result.sortedWith(compareBy({ it.name }, { it.country }))
        }
    }

    fun setQuery(newQuery: String) {
        query.value = newQuery
    }
}
