package com.example.data.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.CoinCollectionEntity
import com.example.data.db.CustomCatalogCoinEntity
import com.example.data.exporter.ExcelExporter
import com.example.data.model.CatalogCoin
import com.example.data.model.CoinGrade
import com.example.data.model.CoinItemUiState
import com.example.data.model.CollectionStatus
import com.example.data.model.EuroCountry
import com.example.data.repository.EuroCatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CountryProgress(
    val country: EuroCountry,
    val totalCoins: Int,
    val ownedCoins: Int,
    val ownedFaceValue: Double,
    val percentage: Float
)

data class CollectionStats(
    val totalCatalogCount: Int = 0,
    val totalOwnedCount: Int = 0,
    val totalWishlistCount: Int = 0,
    val totalMissingCount: Int = 0,
    val totalFaceValue: Double = 0.0,
    val completionPercentage: Float = 0f,
    val countryProgressList: List<CountryProgress> = emptyList()
)

class CoinCollectionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val coinDao = db.coinCollectionDao()
    val repository = EuroCatalogRepository(coinDao)

    val searchQuery = MutableStateFlow("")
    val selectedCountryCode = MutableStateFlow<String?>(null)
    val selectedYear = MutableStateFlow<Int?>(null)
    val selectedStatusFilter = MutableStateFlow<CollectionStatus?>(null) // null = all
    val selectedTypeFilter = MutableStateFlow<String>("ALL") // ALL, REGULAR, COMMEMORATIVE

    val isUpdatingCatalog = MutableStateFlow(false)
    val catalogUpdateMessage = MutableStateFlow<String?>(null)
    val catalogUpdateResult = MutableStateFlow<com.example.data.model.CatalogUpdateResult?>(repository.getCachedCatalogUpdateHistory())

    // Combine Catalog and User Database states
    private val allCoinItemsState: StateFlow<List<CoinItemUiState>> = combine(
        repository.getAllCatalogCoinsFlow(),
        coinDao.getAllUserCoins()
    ) { catalogCoins, userEntities ->
        val entityMap = userEntities.associateBy { it.coinCatalogId }

        catalogCoins.map { catalog ->
            val userEntity = entityMap[catalog.id]
            val status = userEntity?.let {
                try {
                    CollectionStatus.valueOf(it.status)
                } catch (e: Exception) {
                    CollectionStatus.MISSING
                }
            } ?: CollectionStatus.MISSING

            val grade = userEntity?.let {
                try {
                    CoinGrade.valueOf(it.grade)
                } catch (e: Exception) {
                    CoinGrade.CIRC
                }
            } ?: CoinGrade.CIRC

            CoinItemUiState(
                catalogCoin = catalog,
                status = status,
                quantity = userEntity?.quantity ?: if (status == CollectionStatus.OWNED) 1 else 0,
                grade = grade,
                notes = userEntity?.notes ?: ""
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Collection Statistics calculation
    val collectionStats: StateFlow<CollectionStats> = allCoinItemsState.combine(
        MutableStateFlow(repository.countriesList)
    ) { items, countries ->
        val totalCount = items.size
        val ownedItems = items.filter { it.status == CollectionStatus.OWNED }
        val ownedCount = ownedItems.size
        val wishlistCount = items.count { it.status == CollectionStatus.WISHLIST }
        val missingCount = totalCount - ownedCount
        val totalValue = ownedItems.sumOf { (if (it.quantity > 0) it.quantity else 1) * it.catalogCoin.denomination.faceValue }
        val pct = if (totalCount > 0) (ownedCount.toFloat() / totalCount.toFloat()) * 100f else 0f

        val countryProgressList = countries.map { country ->
            val countryItems = items.filter { it.catalogCoin.countryCode == country.code }
            val cTotal = countryItems.size
            val cOwnedItems = countryItems.filter { it.status == CollectionStatus.OWNED }
            val cOwned = cOwnedItems.size
            val cValue = cOwnedItems.sumOf { (if (it.quantity > 0) it.quantity else 1) * it.catalogCoin.denomination.faceValue }
            val cPct = if (cTotal > 0) (cOwned.toFloat() / cTotal.toFloat()) * 100f else 0f

            CountryProgress(
                country = country,
                totalCoins = cTotal,
                ownedCoins = cOwned,
                ownedFaceValue = cValue,
                percentage = cPct
            )
        }.sortedByDescending { it.percentage }

        CollectionStats(
            totalCatalogCount = totalCount,
            totalOwnedCount = ownedCount,
            totalWishlistCount = wishlistCount,
            totalMissingCount = missingCount,
            totalFaceValue = totalValue,
            completionPercentage = pct,
            countryProgressList = countryProgressList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CollectionStats()
    )

    // Filtered list of coins for active view
    val filteredCoinItems: StateFlow<List<CoinItemUiState>> = combine(
        allCoinItemsState,
        searchQuery,
        selectedCountryCode,
        selectedYear,
        combine(selectedStatusFilter, selectedTypeFilter) { status, type -> status to type }
    ) { items, query, countryCode, year, (statusFilter, typeFilter) ->
        items.filter { item ->
            val coin = item.catalogCoin

            val matchesQuery = query.isBlank() ||
                    coin.title.contains(query, ignoreCase = true) ||
                    coin.countryName.contains(query, ignoreCase = true) ||
                    coin.countryCode.contains(query, ignoreCase = true) ||
                    coin.year.toString().contains(query) ||
                    coin.denomination.label.contains(query, ignoreCase = true) ||
                    coin.description.contains(query, ignoreCase = true)

            val matchesCountry = countryCode == null || coin.countryCode.equals(countryCode, ignoreCase = true)
            val matchesYear = year == null || coin.year == year || (query.isNotBlank() && query.contains(coin.year.toString()))
            val matchesStatus = statusFilter == null || item.status == statusFilter

            val matchesType = when (typeFilter) {
                "COMMEMORATIVE" -> coin.isCommemorative
                "REGULAR" -> !coin.isCommemorative
                else -> true
            }

            matchesQuery && matchesCountry && matchesYear && matchesStatus && matchesType
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            filteredCoinItems.collect { items ->
                if (items.isNotEmpty()) {
                    com.example.data.repository.NumistaRepository.preFetchImagesForCoins(items.map { it.catalogCoin })
                }
            }
        }
    }

    fun toggleCoinStatus(coinCatalogId: String) {
        viewModelScope.launch {
            val currentItem = allCoinItemsState.value.find { it.catalogCoin.id == coinCatalogId }
            val nextStatus = when (currentItem?.status) {
                CollectionStatus.MISSING -> CollectionStatus.OWNED
                CollectionStatus.OWNED -> CollectionStatus.WISHLIST
                CollectionStatus.WISHLIST -> CollectionStatus.MISSING
                null -> CollectionStatus.OWNED
            }

            val qty = if (nextStatus == CollectionStatus.OWNED) {
                if (currentItem?.quantity ?: 0 > 0) currentItem?.quantity ?: 1 else 1
            } else 0

            coinDao.upsertCoinStatus(
                CoinCollectionEntity(
                    coinCatalogId = coinCatalogId,
                    status = nextStatus.name,
                    quantity = qty,
                    grade = currentItem?.grade?.name ?: CoinGrade.CIRC.name,
                    notes = currentItem?.notes ?: ""
                )
            )
        }
    }

    fun setCoinStatusDirect(coinCatalogId: String, status: CollectionStatus) {
        viewModelScope.launch {
            val currentItem = allCoinItemsState.value.find { it.catalogCoin.id == coinCatalogId }
            val qty = if (status == CollectionStatus.OWNED) {
                if ((currentItem?.quantity ?: 0) > 0) currentItem?.quantity ?: 1 else 1
            } else 0

            coinDao.upsertCoinStatus(
                CoinCollectionEntity(
                    coinCatalogId = coinCatalogId,
                    status = status.name,
                    quantity = qty,
                    grade = currentItem?.grade?.name ?: CoinGrade.CIRC.name,
                    notes = currentItem?.notes ?: ""
                )
            )
        }
    }

    fun markAllFilteredAsOwned() {
        viewModelScope.launch {
            val currentFiltered = filteredCoinItems.value
            currentFiltered.forEach { item ->
                coinDao.upsertCoinStatus(
                    CoinCollectionEntity(
                        coinCatalogId = item.catalogCoin.id,
                        status = CollectionStatus.OWNED.name,
                        quantity = if (item.quantity > 0) item.quantity else 1,
                        grade = item.grade.name,
                        notes = item.notes
                    )
                )
            }
        }
    }

    fun unmarkAllFiltered() {
        viewModelScope.launch {
            val currentFiltered = filteredCoinItems.value
            currentFiltered.forEach { item ->
                coinDao.upsertCoinStatus(
                    CoinCollectionEntity(
                        coinCatalogId = item.catalogCoin.id,
                        status = CollectionStatus.MISSING.name,
                        quantity = 0,
                        grade = item.grade.name,
                        notes = item.notes
                    )
                )
            }
        }
    }

    fun updateCoinDetails(
        coinCatalogId: String,
        status: CollectionStatus,
        quantity: Int,
        grade: CoinGrade,
        notes: String
    ) {
        viewModelScope.launch {
            coinDao.upsertCoinStatus(
                CoinCollectionEntity(
                    coinCatalogId = coinCatalogId,
                    status = status.name,
                    quantity = quantity,
                    grade = grade.name,
                    notes = notes
                )
            )
        }
    }

    fun addCustomCoin(
        countryCode: String,
        year: Int,
        title: String,
        description: String,
        mintage: String
    ) {
        viewModelScope.launch {
            val country = repository.getCountryByCode(countryCode) ?: repository.countriesList.first()
            val id = "${country.code}_${year}_2E_CUSTOM_${System.currentTimeMillis()}"

            repository.addCustomCoin(
                CustomCatalogCoinEntity(
                    id = id,
                    countryCode = country.code,
                    countryName = country.name,
                    year = year,
                    denominationCode = "2€_COMM",
                    title = title,
                    description = description,
                    isCommemorative = true,
                    mintageInfo = mintage
                )
            )
        }
    }

    fun updateOfficialCatalog(onComplete: (com.example.data.model.CatalogUpdateResult) -> Unit = {}) {
        viewModelScope.launch {
            isUpdatingCatalog.value = true
            catalogUpdateMessage.value = "Verificando conexión con el servidor del Banco Central Europeo (BCE)..."
            kotlinx.coroutines.delay(600)

            val context = getApplication<Application>().applicationContext
            val result = repository.updateOfficialEuroCatalog(context)

            if (!result.isSuccess) {
                catalogUpdateResult.value = result
                catalogUpdateMessage.value = result.errorMessage ?: "Sin conexión a Internet. Activa tu Wi-Fi o datos para actualizar."
                isUpdatingCatalog.value = false
                onComplete(result)
                return@launch
            }

            catalogUpdateMessage.value = "Sincronizando catálogo con la base oficial de la Eurozona y Numista..."
            kotlinx.coroutines.delay(600)

            val currentCoins = filteredCoinItems.value.map { it.catalogCoin }
            com.example.data.repository.NumistaRepository.preFetchImagesForCoins(currentCoins)

            catalogUpdateResult.value = result
            if (result.addedCount > 0 || result.removedCount > 0) {
                catalogUpdateMessage.value = "¡Catálogo e imágenes de Numista actualizadas! +${result.addedCount} confirmadas, -${result.removedCount} retiradas."
            } else {
                catalogUpdateMessage.value = "Imágenes de Numista comprobadas. El catálogo se encuentra al día."
            }

            isUpdatingCatalog.value = false
            onComplete(result)
        }
    }

    fun exportExcel(context: android.content.Context, countryNameFilter: String? = null): Uri? {
        val itemsToExport = if (countryNameFilter != null) {
            allCoinItemsState.value.filter { it.catalogCoin.countryName.equals(countryNameFilter, ignoreCase = true) }
        } else {
            allCoinItemsState.value
        }

        val uri = ExcelExporter.exportCollectionToXlsx(context, itemsToExport, countryNameFilter)
        if (uri != null) {
            ExcelExporter.shareExportedFile(context, uri, isXlsx = true)
        }
        return uri
    }

    fun exportExcelCustom(
        context: android.content.Context,
        statusFilter: CollectionStatus? = null,
        commemorativeOnly: Boolean = false,
        countryCodeFilter: String? = null,
        labelTitle: String? = null
    ): Uri? {
        var items = allCoinItemsState.value

        if (statusFilter != null) {
            items = items.filter { it.status == statusFilter }
        }
        if (commemorativeOnly) {
            items = items.filter { it.catalogCoin.isCommemorative }
        }
        if (countryCodeFilter != null) {
            items = items.filter { it.catalogCoin.countryCode.equals(countryCodeFilter, ignoreCase = true) }
        }

        val uri = ExcelExporter.exportCollectionToXlsx(context, items, labelTitle)
        if (uri != null) {
            ExcelExporter.shareExportedFile(context, uri, isXlsx = true)
        }
        return uri
    }
}
