package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.EuroCountry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomCoinDialog(
    countries: List<EuroCountry>,
    onDismiss: () -> Unit,
    onAddCoin: (countryCode: String, year: Int, title: String, description: String, mintage: String) -> Unit
) {
    var selectedCountry by remember { mutableStateOf(countries.firstOrNull() ?: EuroCountry("ES", "España", "🇪🇸", 1999)) }
    var yearText by remember { mutableStateOf("2025") }
    var titleText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }
    var mintageText by remember { mutableStateOf("") }

    var expandedCountryMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Añadir Moneda Conmemorativa de 2€",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Country Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCountryMenu,
                    onExpandedChange = { expandedCountryMenu = !expandedCountryMenu }
                ) {
                    OutlinedTextField(
                        value = "${selectedCountry.flagEmoji} ${selectedCountry.name}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("País") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCountryMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCountryMenu,
                        onDismissRequest = { expandedCountryMenu = false }
                    ) {
                        countries.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.flagEmoji} ${c.name}") },
                                onClick = {
                                    selectedCountry = c
                                    expandedCountryMenu = false
                                }
                            )
                        }
                    }
                }

                // Year Text
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { yearText = it.filter { ch -> ch.isDigit() }.take(4) },
                    label = { Text("Año de emisión") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Title Text
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Título de la moneda") },
                    placeholder = { Text("Ej: 200º Aniversario...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_coin_title_input")
                )

                // Description Text
                OutlinedTextField(
                    value = descText,
                    onValueChange = { descText = it },
                    label = { Text("Descripción o motivo (Opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mintage
                OutlinedTextField(
                    value = mintageText,
                    onValueChange = { mintageText = it },
                    label = { Text("Tirada estimada (Opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val yr = yearText.toIntOrNull() ?: 2025
                    if (titleText.isNotBlank()) {
                        onAddCoin(selectedCountry.code, yr, titleText, descText, mintageText)
                        onDismiss()
                    }
                },
                enabled = titleText.isNotBlank()
            ) {
                Text("Guardar Moneda")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
