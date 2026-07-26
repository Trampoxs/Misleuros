package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoinGrade
import com.example.data.model.CoinItemUiState
import com.example.data.model.CollectionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailBottomSheet(
    item: CoinItemUiState,
    onDismiss: () -> Unit,
    onSaveDetails: (CollectionStatus, Int, CoinGrade, String) -> Unit
) {
    val coin = item.catalogCoin

    var selectedStatus by remember { mutableStateOf(item.status) }
    var quantity by remember { mutableIntStateOf(item.quantity) }
    var selectedGrade by remember { mutableStateOf(item.grade) }
    var notesText by remember { mutableStateOf(item.notes) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info with large visual coin graphic
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CoinVisualGraphic(
                    coin = coin,
                    size = 88.dp,
                    allowFlip = false
                )

                Column {
                    Text(
                        text = "${coin.countryName} (${coin.year})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = coin.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (coin.mintageInfo.isNotBlank()) {
                        Text(
                            text = "Tirada: ${coin.mintageInfo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Collection Status Selector
            Text(
                text = "Estado de Colección",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CollectionStatus.entries.forEach { status ->
                    val isSelected = selectedStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedStatus = status
                            if (status == CollectionStatus.OWNED && quantity == 0) {
                                quantity = 1
                            }
                        },
                        label = { Text(status.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quantity Counter (If Owned)
            if (selectedStatus == CollectionStatus.OWNED) {
                Text(
                    text = "Cantidad de Ejemplares",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "Reducir")
                    }

                    Text(
                        text = "$quantity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { quantity++ }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Aumentar")
                    }
                }

                // Conservation Grade
                Text(
                    text = "Estado de Conservación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CoinGrade.entries.forEach { grade ->
                        val isSelected = selectedGrade == grade
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedGrade = grade },
                            label = { Text(grade.label) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Notes and Details Section
            Text(
                text = "Notas Personales y Estado de Conservación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Añade anotaciones sobre la pieza (ej: buen estado, con rayones, brillo original...):",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Quick suggestion chips
            val quickNotes = listOf("Buen estado", "Con rayones", "Sin circular", "Ligeros arañazos", "Brillo original", "Pátina natural")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickNotes.take(3).forEach { suggestion ->
                    OutlinedButton(
                        onClick = {
                            if (notesText.isBlank()) {
                                notesText = suggestion
                            } else if (!notesText.contains(suggestion)) {
                                notesText = "$notesText, $suggestion"
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = suggestion, fontSize = 11.sp)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickNotes.drop(3).forEach { suggestion ->
                    OutlinedButton(
                        onClick = {
                            if (notesText.isBlank()) {
                                notesText = suggestion
                            } else if (!notesText.contains(suggestion)) {
                                notesText = "$notesText, $suggestion"
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = suggestion, fontSize = 11.sp)
                    }
                }
            }

            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text("Notas de la moneda") },
                placeholder = { Text("Ejemplo: Buen estado, con rayones leves, comprada en 2023...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notes_input_field"),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )

            // Save Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        onSaveDetails(selectedStatus, quantity, selectedGrade, notesText)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
