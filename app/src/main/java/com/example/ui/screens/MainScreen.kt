package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.viewmodel.CoinCollectionViewModel

import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.outlined.NewReleases

import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings

sealed class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Countries : MainTab("Países", Icons.Filled.Public, Icons.Outlined.Public, "tab_countries")
    object Stats : MainTab("Estadísticas", Icons.Filled.PieChart, Icons.Outlined.PieChart, "tab_stats")
    object News : MainTab("Noticias", Icons.Filled.NewReleases, Icons.Outlined.NewReleases, "tab_news")
    object Settings : MainTab("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: CoinCollectionViewModel
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val selectedCountryCode by viewModel.selectedCountryCode.collectAsState()
    val stats by viewModel.collectionStats.collectAsState()

    val tabs = listOf(
        MainTab.Countries,
        MainTab.Stats,
        MainTab.News,
        MainTab.Settings
    )

    Scaffold(
        topBar = {
            if (selectedCountryCode == null) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "mis €uros",
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${stats.totalOwnedCount} / ${stats.totalCatalogCount} (${stats.completionPercentage.toInt()}%)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.exportExcel(context) },
                            modifier = Modifier.testTag("top_app_bar_export_button")
                        ) {
                            Icon(Icons.Filled.FileDownload, contentDescription = "Exportar a Excel")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index && selectedCountryCode == null,
                        onClick = {
                            viewModel.selectedCountryCode.value = null
                            selectedTabIndex = index
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedTabIndex == index) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedCountryCode != null) {
                CountryDetailScreen(
                    countryCode = selectedCountryCode!!,
                    viewModel = viewModel,
                    onBack = { viewModel.selectedCountryCode.value = null }
                )
            } else {
                when (selectedTabIndex) {
                    0 -> CountriesScreen(
                        viewModel = viewModel,
                        onSelectCountry = { code -> viewModel.selectedCountryCode.value = code }
                    )
                    1 -> StatsScreen(viewModel = viewModel)
                    2 -> NewsScreen(
                        onNavigateToAddCoin = { selectedTabIndex = 0 }
                    )
                    3 -> ExportScreen(viewModel = viewModel)
                }
            }
        }
    }
}
