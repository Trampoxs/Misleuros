package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoinItemUiState
import com.example.data.viewmodel.CoinCollectionViewModel
import com.example.ui.components.CoinCard
import com.example.ui.components.CoinDetailBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    countryCode: String,
    viewModel: CoinCollectionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val country = remember(countryCode) { viewModel.repository.getCountryByCode(countryCode) }

    LaunchedEffect(countryCode) {
        viewModel.selectedCountryCode.value = countryCode
        if (viewModel.selectedYear.value == null) {
            viewModel.selectedYear.value = 2025
        }
        viewModel.searchQuery.value = ""
    }

    val coins by viewModel.filteredCoinItems.collectAsState()
    val stats by viewModel.collectionStats.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    var activeDetailItem by remember { mutableStateOf<CoinItemUiState?>(null) }
    var showSummaryModal by remember { mutableStateOf(false) }

    val countryStats = remember(stats, countryCode) {
        stats.countryProgressList.find { it.country.code == countryCode }
    }

    val ownedCount = countryStats?.ownedCoins ?: 0
    val totalCount = countryStats?.totalCoins ?: 0
    val pendingCount = totalCount - ownedCount

    val primaryHeaderBg = MaterialTheme.colorScheme.primary
    val yearBarBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side: Flag + Country Name
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(country?.flagEmoji ?: "", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = country?.name ?: "Monedas",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 22.sp
                            )
                        }

                        // Right side: Collection vs Pending status counts
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✔ ", color = Color(0xFF00FF66), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text("Colección: ", color = Color.White, fontSize = 13.sp)
                                Text("$ownedCount", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("❌ ", color = Color(0xFFFF4D4D), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text("Pendientes:", color = Color.White, fontSize = 13.sp)
                                Text("$pendingCount", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.selectedCountryCode.value = null
                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.exportExcel(context, country?.name) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FileDownload,
                            contentDescription = "Exportar Excel",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryHeaderBg
                )
            )
        },
        bottomBar = {
            // Bottom Action Bar: Marcar Todas | Resumen | Desmarcar Todas
            Surface(
                color = primaryHeaderBg,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { viewModel.markAllFilteredAsOwned() },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0033CC),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Marcar Todas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showSummaryModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF001A80),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Resumen", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.unmarkAllFiltered() },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0033CC),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Desmarcar Todas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field (Search by year or country or title)
            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    viewModel.searchQuery.value = newQuery
                    if (newQuery.isNotBlank() && selectedYear != null) {
                        viewModel.selectedYear.value = null
                    }
                },
                placeholder = { Text("Buscar año (ej: 2023), país o moneda...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Buscar",
                        tint = primaryHeaderBg
                    )
                },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Limpiar"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Year Bar (Año: 2025 2024 2023 2022 2021 ...)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(yearBarBg)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Año:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )

                // "Todos" option
                Box(
                    modifier = Modifier
                        .background(
                            color = if (selectedYear == null) Color(0xFF3385FF) else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { viewModel.selectedYear.value = null }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Todos",
                        color = Color.White,
                        fontWeight = if (selectedYear == null) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }

                val startYr = country?.euroStartYear ?: 1999
                for (yr in 2026 downTo startYr) {
                    val isSelected = selectedYear == yr
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFF3385FF) else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { viewModel.selectedYear.value = yr }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$yr",
                            color = Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 17.sp
                        )
                    }
                }
            }

            // Coins List
            if (coins.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay monedas en este año o filtro.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(coins, key = { it.catalogCoin.id }) { item ->
                        CoinCard(
                            item = item,
                            onStatusToggle = { viewModel.toggleCoinStatus(item.catalogCoin.id) },
                            onOpenDetails = { activeDetailItem = item }
                        )
                    }
                }
            }
        }
    }

    // Detail Modal Sheet
    activeDetailItem?.let { item ->
        CoinDetailBottomSheet(
            item = item,
            onDismiss = { activeDetailItem = null },
            onSaveDetails = { status, qty, grade, notes ->
                viewModel.updateCoinDetails(item.catalogCoin.id, status, qty, grade, notes)
            }
        )
    }

    // Summary Modal Sheet
    if (showSummaryModal) {
        ModalBottomSheet(
            onDismissRequest = { showSummaryModal = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Resumen de ${country?.name ?: "Monedas"}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                countryStats?.let { cp ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Total de monedas en catálogo: ${cp.totalCoins}", fontWeight = FontWeight.Medium)
                            Text("Monedas poseídas: ${cp.ownedCoins}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text("Monedas pendientes: ${cp.totalCoins - cp.ownedCoins}", fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                            Text("Porcentaje completado: ${String.format("%.1f", cp.percentage)}%", fontWeight = FontWeight.Bold)
                            Text("Valor facial total poseído: ${String.format("%.2f", cp.ownedFaceValue)} €", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = { showSummaryModal = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}
