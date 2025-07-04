package com.uala.challengeandroid.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uala.challengeandroid.data.CityRepository
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.utils.toDomainList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private val _allCities = MutableStateFlow<List<City>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val pageSize = 50
    private val generalPage = MutableStateFlow(0)
    private var isLoadingGeneral = false
    private val searchPage = MutableStateFlow(0)
    private var isLoadingSearch = false

    fun loadCities() {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getAllCities().isEmpty()) {
                repository.saveCities()
            }
            _allCities.value = repository.getAllCities().toDomainList()
        }
    }

    val pagedAllCities: StateFlow<List<City>> = combine(_allCities, generalPage) { list, page ->
        val to = minOf(list.size, (page + 1) * pageSize)
        list.take(to)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val query: StateFlow<String> = _searchQuery
    val pagedSearchCities: StateFlow<List<City>> = combine(_searchQuery, _allCities, searchPage) { query, list, page ->
        if (query.isBlank()) return@combine emptyList()
        val filtered = list.filter { it.name.startsWith(query, ignoreCase = true) }
        val to = minOf(filtered.size, (page + 1) * pageSize)
        filtered.take(to)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun loadNextGeneralPage() {
        if (isLoadingGeneral) return
        isLoadingGeneral = true

        val total = _allCities.value.size
        val maxPage = if (total == 0) 0 else (total - 1) / pageSize

        if (generalPage.value < maxPage) {
            generalPage.value += 1
        }

        viewModelScope.launch {
            delay(300)
            isLoadingGeneral = false
        }
    }

    fun loadNextSearchPage() {
        if (isLoadingSearch) return
        isLoadingSearch = true

        val filtered = _allCities.value.filter { it.name.startsWith(_searchQuery.value, ignoreCase = true) }
        val total = filtered.size
        val maxPage = if (total == 0) 0 else (total - 1) / pageSize

        if (searchPage.value < maxPage) {
            searchPage.value += 1
        }

        viewModelScope.launch {
            delay(300)
            isLoadingSearch = false
        }
    }

    fun setQuery(newQuery: String) {
        _searchQuery.value = newQuery
        searchPage.value = 0
    }

//    fun resetPagination() {
//        generalPage.value = 0
//        searchPage.value = 0
//    }
}
