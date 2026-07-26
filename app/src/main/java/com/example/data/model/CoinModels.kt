package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class CoinDenomination(
    val code: String,
    val label: String,
    val faceValue: Double,
    val isCommemorative: Boolean = false
) {
    CENT_1("1c", "1 Céntimo", 0.01),
    CENT_2("2c", "2 Céntimos", 0.02),
    CENT_5("5c", "5 Céntimos", 0.05),
    CENT_10("10c", "10 Céntimos", 0.10),
    CENT_20("20c", "20 Céntimos", 0.20),
    CENT_50("50c", "50 Céntimos", 0.50),
    EURO_1("1€", "1 Euro", 1.00),
    EURO_2("2€", "2 Euros", 2.00),
    EURO_2_COMMEMORATIVE("2€_COMM", "2€ Conmemorativa", 2.00, isCommemorative = true);

    val badgeColor: Color
        get() = when (this) {
            CENT_1, CENT_2, CENT_5 -> Color(0xFFB87333) // Copper/Bronze
            CENT_10, CENT_20, CENT_50 -> Color(0xFFDAA520) // Nordic Gold
            EURO_1 -> Color(0xFFC0C0C0) // Bi-metallic Silver/Gold
            EURO_2 -> Color(0xFFE5C158) // Bi-metallic Gold/Silver
            EURO_2_COMMEMORATIVE -> Color(0xFFFFD700) // Rich Gold
        }
}

enum class CollectionStatus(val label: String, val shortLabel: String, val color: Color) {
    MISSING("Me falta", "Falta", Color(0xFF78909C)),
    OWNED("En mi colección", "Tengo", Color(0xFF2E7D32)),
    WISHLIST("Lista de deseos", "Busco", Color(0xFFED6C02))
}

enum class CoinGrade(val code: String, val label: String) {
    UNC("SC", "Sin Circular (SC)"),
    EBC("EBC", "Excelente Conservación (EBC)"),
    MBC("MBC", "Muy Buena Conservación (MBC)"),
    BC("BC", "Buena Conservación (BC)"),
    CIRC("CIRC", "Circular / Usada")
}

data class EuroCountry(
    val code: String,
    val name: String,
    val flagEmoji: String,
    val euroStartYear: Int,
    val primaryColorHex: String = "#1A365D"
)

data class CatalogCoin(
    val id: String, // e.g. "ES_2005_2E_COMM_1" or "DE_2002_1C"
    val countryCode: String,
    val countryName: String,
    val year: Int,
    val denomination: CoinDenomination,
    val title: String,
    val description: String = "",
    val isCommemorative: Boolean = false,
    val mintageInfo: String = "",
    val isCustom: Boolean = false,
    val imageUrl: String? = null
)

data class CoinItemUiState(
    val catalogCoin: CatalogCoin,
    val status: CollectionStatus = CollectionStatus.MISSING,
    val quantity: Int = 0,
    val grade: CoinGrade = CoinGrade.CIRC,
    val notes: String = ""
)

data class CatalogCoinSummary(
    val id: String,
    val title: String,
    val countryCode: String,
    val countryName: String,
    val year: Int,
    val denomination: String,
    val reason: String? = null,
    val addedDate: String? = "24/07/2026"
)

data class CatalogUpdateResult(
    val addedCount: Int,
    val removedCount: Int,
    val addedCoins: List<CatalogCoinSummary>,
    val removedCoins: List<CatalogCoinSummary>,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)
