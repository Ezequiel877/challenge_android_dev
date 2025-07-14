package com.uala.challengeandroid.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uala.challengeandroid.data.FetchCityUseCase
import com.uala.challengeandroid.data.SetFavoriteUseCase
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.utils.Result
import com.uala.challengeandroid.utils.stateAsResultIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val repository: FetchCityUseCase, private val setFavoriteUseCase: SetFavoriteUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = _searchQuery
    private val uiReady = MutableStateFlow(false)
    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites
    fun setOnlyFavorites(value: Boolean) {
        _onlyFavorites.value = value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val list: StateFlow<Result<List<City>>> =
        uiReady.filter { it }
            .flatMapLatest { repository() }
            .stateAsResultIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val listFavorite: StateFlow<Result<List<City>>> =
        uiReady.filter { it }
            .flatMapLatest { setFavoriteUseCase.getAllFavorite() }
            .stateAsResultIn(viewModelScope)

    val state: StateFlow<Result<List<City>>> = combine(
        _searchQuery, _onlyFavorites, list, listFavorite
    ) { query, onlyFav, allResult, favResult ->
        val resultToUse = if (onlyFav) favResult else allResult
        when (resultToUse) {
            is Result.Success -> {
                val filtered = if (query.isBlank()) {
                    resultToUse.data
                } else {
                    resultToUse.data.filter {
                        it.name.startsWith(query, ignoreCase = true)
                    }
                }
                Result.Success(filtered)
            }

            is Result.Loading -> Result.Loading
            is Result.Error -> resultToUse
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Result.Loading)

    fun toggleFavorite(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            setFavoriteUseCase.setNewFavorite(city.id)
        }
    }

    fun onUiReady() {
        uiReady.value = true
    }

    fun setQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }
}
