package com.uala.challengeandroid

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uala.challengeandroid.data.*
import com.uala.challengeandroid.model.*
import com.uala.challengeandroid.utils.toDomainList
import com.uala.challengeandroid.utils.toEntityList
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CityListScreenTest {

    /** Test-rule order: Hilt first, Compose second. */
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val compose  = createAndroidComposeRule<MainActivity>()

    /** Fake dependencies – instantiated eagerly. */
    private val repo = FakeCityRepository()

    @BindValue val fetchUC: FetchCityUseCase = FakeFetchCityUseCase(repo)
    @BindValue val favUC:   SetFavoriteUseCase = FakeSetFavoriteUseCase(repo)

    /** Inject the Hilt graph *before* the Activity is launched. */
    @Before fun injectHilt() = hiltRule.inject()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchFiltersByPrefix() {
        with(compose) {

            // Await the initial population of the list.
            waitUntilAtLeastOneExists(hasTestTag("CityItem"), timeoutMillis = 10_000)

            // 1️⃣  Open the SearchBar and enter “Ba”.
            onNode(hasTestTag("SearchBar")).performClick()
            waitUntilAtLeastOneExists(hasSetTextAction(), 3_000)
            onNode(hasSetTextAction()).performTextInput("Ba")

            // 2️⃣  After filtering only two cities should remain.
            onAllNodesWithTag("CityItem").assertCountEquals(2)

            // 3️⃣  Confirm that both cities beginning with “Ba” are visible.
            onNodeWithText("Bahía Blanca")
                .performScrollTo()        // Ensures the item is within the viewport.
                .assertIsDisplayed()

            onNodeWithText("Barcelona")
                .performScrollTo()
                .assertIsDisplayed()
        }
    }
}

/* ------------------------------  Fakes & Data  --------------------------- */

private val dummyCities = listOf(
    City("Argentina", "Buenos Aires", 1, Coord(-34.6, -58.38), false),
    City("Argentina", "Córdoba",      2, Coord(-31.42, -64.18), false),
    City("Argentina", "Bahía Blanca", 3, Coord(-38.70, -62.27), false),
    City("Spain",      "Barcelona",   4, Coord( 41.38,   2.17),false)
)

/** In-memory implementation of the local data source. */
private class InMemoryLocal : CityDataLocal {
    private val flow = MutableStateFlow(dummyCities.toEntityList())

    override val cities: Flow<List<City>> = flow.map { it.toDomainList() }

    override suspend fun saveCities(cities: List<City>) {
        flow.value = cities.toEntityList()
    }

    override suspend fun toggleFavorite(id: Long) {
        flow.value = flow.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    override fun getFavorite(): Flow<List<CityEntity>> =
        flow.map { it.filter { it.isFavorite } }
}

/** Remote data source fake. */
private class FakeRemote : CityDataRemote {
    override suspend fun getAllCities(): List<City> = dummyCities
}

/** Repository fake composed of the in-memory and remote fakes. */
private class FakeCityRepository :
    CityRepository(InMemoryLocal(), FakeRemote())

/** Use-case fakes delegating to the in-memory repository. */
private class FakeFetchCityUseCase(repo: CityRepository) : FetchCityUseCase(repo)
private class FakeSetFavoriteUseCase(repo: CityRepository) : SetFavoriteUseCase(repo)
