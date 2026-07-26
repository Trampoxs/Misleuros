package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoinItemUiState
import com.example.data.model.CollectionStatus
import com.example.ui.theme.CoinCopper
import com.example.ui.theme.CoinNordicGold
import com.example.ui.theme.CoinSilver

@Composable
fun CoinCard(
    item: CoinItemUiState,
    onStatusToggle: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coin = item.catalogCoin

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() }
            .testTag("coin_card_${coin.id}"),
        colors = CardDefaults.cardColors(
            containerColor = when (item.status) {
                CollectionStatus.OWNED -> MaterialTheme.colorScheme.surfaceVariant
                CollectionStatus.WISHLIST -> MaterialTheme.colorScheme.surface
                CollectionStatus.MISSING -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Coin Visual Graphic Icon
            CoinVisualGraphic(
                coin = coin,
                size = 60.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Title and subtitle details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                val titleText = when {
                    coin.isCommemorative -> "2 €uros Conm."
                    coin.denomination == com.example.data.model.CoinDenomination.EURO_1 -> "1 €uro"
                    coin.denomination == com.example.data.model.CoinDenomination.EURO_2 -> "2 €uros"
                    else -> coin.denomination.label
                }

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                val subtitleText = if (coin.isCommemorative) {
                    "${coin.title} (${coin.year})"
                } else {
                    "Moneda ${coin.denomination.label} ${coin.countryName}"
                }

                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )

                if (item.quantity > 0 || item.notes.isNotBlank() || item.grade.code != "CIRC") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        if (item.quantity > 0) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${item.quantity} ud.",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        if (item.grade.code != "CIRC") {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = item.grade.code,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        if (item.notes.isNotBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Text(
                                    text = "📝 ${item.notes}",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right column: Status toggle symbol (Red X or Green Check) + Country Flag
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Status Toggle Icon Button (Red X / Green Check)
                IconButton(
                    onClick = onStatusToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    when (item.status) {
                        CollectionStatus.OWNED -> {
                            Text(
                                text = "✔",
                                color = Color(0xFF2E7D32),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        CollectionStatus.WISHLIST -> {
                            Text(
                                text = "⭐",
                                fontSize = 22.sp
                            )
                        }
                        CollectionStatus.MISSING -> {
                            Text(
                                text = "❌",
                                color = Color(0xFFE53935),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Country Flag Emoji
                val flagEmoji = when (coin.countryCode.uppercase()) {
                    "ES" -> "🇪🇸"
                    "DE" -> "🇩🇪"
                    "FR" -> "🇫🇷"
                    "IT" -> "🇮🇹"
                    "AT" -> "🇦🇹"
                    "IE" -> "🇮🇪"
                    "NL" -> "🇳🇱"
                    "BE" -> "🇧🇪"
                    "PT" -> "🇵🇹"
                    "GR" -> "🇬🇷"
                    "VA" -> "🇻🇦"
                    "MC" -> "🇲🇨"
                    "SM" -> "🇸🇲"
                    "HR" -> "🇭🇷"
                    "LT" -> "🇱🇹"
                    "EE" -> "🇪🇪"
                    "FI" -> "🇫🇮"
                    "SK" -> "🇸🇰"
                    "MT" -> "🇲🇹"
                    "CY" -> "🇨🇾"
                    "LU" -> "🇱🇺"
                    "LV" -> "🇱🇻"
                    else -> "🇪🇺"
                }

                Text(
                    text = flagEmoji,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CoinBadge(
    denominationLabel: String,
    badgeColor: Color,
    isCommemorative: Boolean,
    modifier: Modifier = Modifier
) {
    val sizeDp = if (isCommemorative) 48.dp else 42.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape)
            .background(badgeColor)
            .border(
                width = if (isCommemorative) 2.dp else 1.dp,
                color = if (isCommemorative) Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f),
                shape = CircleShape
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = denominationLabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = if (badgeColor == CoinSilver) Color.Black else Color.White,
                fontSize = if (denominationLabel.length > 3) 10.sp else 12.sp
            )
            if (isCommemorative) {
                Text(
                    text = "COMM",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun StatusButton(
    status: CollectionStatus,
    onToggle: () -> Unit,
    onOpenDetails: () -> Unit
) {
    val containerColor = when (status) {
        CollectionStatus.OWNED -> Color(0xFF2E7D32)
        CollectionStatus.WISHLIST -> Color(0xFFED6C02)
        CollectionStatus.MISSING -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    val contentColor = when (status) {
        CollectionStatus.OWNED, CollectionStatus.WISHLIST -> Color.White
        CollectionStatus.MISSING -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        contentColor = contentColor,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = when (status) {
                    CollectionStatus.OWNED -> Icons.Filled.Check
                    CollectionStatus.WISHLIST -> Icons.Filled.Star
                    CollectionStatus.MISSING -> Icons.Outlined.Check
                },
                contentDescription = status.label,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.shortLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
