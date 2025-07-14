package com.uala.challengeandroid.data

import app.cash.turbine.test
import com.uala.challengeandroid.model.City
import com.uala.challengeandroid.model.CityEntity
import com.uala.challengeandroid.model.Coord
import com.uala.challengeandroid.presentation.CityViewModel
import com.uala.challengeandroid.utils.Result
import com.uala.challengeandroid.utils.toEntityList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    /* ---------- fakes ---------- */
    private val fakeRepo           = FakeCityRepository()
    private val fetchUC            = FakeFetchCityUseCase(fakeRepo)
    private val favoriteUC         = FakeFavoriteUseCase(fakeRepo)
    private lateinit var viewModel : CityViewModel

    @Before fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        viewModel = CityViewModel(fetchUC, favoriteUC)
        viewModel.onUiReady()
    }

    @After  fun tearDown() = Dispatchers.resetMain()

    /* ─────────────────────────────────────────────── */
    @Test fun `lista completa sin filtros`() = runTest {
        viewModel.state.test {
            assertTrue(awaitItem() is Result.Loading)
            val list = (awaitItem() as Result.Success).data
            assertEquals(6, list.size)
            cancel()
        }
    }

    @Test fun `query B filtra BA y BCN`() = runTest {
        viewModel.setQuery("B")

        viewModel.state.test {
            awaitItem()
            val names = (awaitItem() as Result.Success).data.map { it.name }
            assertEquals(listOf("Buenos Aires", "Barcelona"), names)
            cancel()
        }
    }

    @Test fun `sin coincidencias devuelve lista vacía`() = runTest {
        viewModel.setQuery("ZZZ")

        viewModel.state.test {
            awaitItem()
            val empty = (awaitItem() as Result.Success).data
            assertTrue(empty.isEmpty())
            cancel()
        }
    }

    @Test fun `solo favoritos devuelve BA y BCN`() = runTest {
        favoriteUC.setInitialFavorites(listOf(1L, 6L))
        viewModel.setOnlyFavorites(true)

        viewModel.state.test {
            awaitItem()
            val favs = (awaitItem() as Result.Success).data.map { it.name }
            assertEquals(listOf("Buenos Aires", "Barcelona"), favs)
            cancel()
        }
    }
}

/* ---------- datos dummy ---------- */
private val dummy = listOf(
    City("Argentina","Buenos Aires",1, Coord(-34.6,-58.38),false),
    City("Argentina","Córdoba",     2, Coord(-31.42,-64.18),false),
    City("Argentina","Rosario",     3, Coord(-32.94,-60.63),false),
    City("Brasil",   "Rio de Janeiro",4,Coord(-22.9,-43.17),false),
    City("Chile",    "Santiago",    5, Coord(-33.44,-70.66),false),
    City("España",   "Barcelona",   6, Coord(41.38,2.17),   false)
)

/* ---------- data-sources ---------- */
private class InMemoryLocal : CityDataLocal {
    private val citiesFlow = MutableStateFlow(dummy)
    private val favIds     = MutableStateFlow<Set<Long>>(emptySet())

    override val cities                     = citiesFlow
    override suspend fun saveCities(c: List<City>) { citiesFlow.value = c }
    override suspend fun toggleFavorite(id: Long) {
        favIds.value = if (id in favIds.value) favIds.value - id else favIds.value + id
    }
    override fun getFavorite(): Flow<List<CityEntity>> = citiesFlow.map { list -> list.filter { it.id in favIds.value }.toEntityList() }

    fun setFav(ids: Set<Long>) { favIds.value = ids }
}

private class InMemoryRemote : CityDataRemote {
    override suspend fun getAllCities(): List<City> = dummy
}
private class FakeCityRepository : CityRepository(InMemoryLocal(), InMemoryRemote()) {
    private val local get() = roomDataSource as InMemoryLocal
    fun setFav(ids: Set<Long>) = local.setFav(ids)
}

/* ---------- use-cases ---------- */
private class FakeFetchCityUseCase(repo: CityRepository) : FetchCityUseCase(repo)
private class FakeFavoriteUseCase(private val repo: FakeCityRepository)
    : SetFavoriteUseCase(repo) {
    fun setInitialFavorites(ids: List<Long>) = repo.setFav(ids.toSet())
}
