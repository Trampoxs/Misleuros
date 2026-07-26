package com.example.data.model

data class NumismaticNewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val fullContent: String,
    val countryCode: String,
    val countryName: String,
    val year: Int,
    val releaseDate: String, // e.g. "Primer trimestre 2026"
    val mintageVolume: String, // e.g. "1.500.000 piezas"
    val statusTag: String, // e.g. "Confirmada", "En Diseño", "Emisión Anunciada"
    val category: String, // e.g. "2€ Conmemorativa", "Emisión Conjunta", "Novedad Real Ceca"
    val imageResId: Int? = null,
    val imageUrl: String? = null,
    val hasConfirmedImage: Boolean = true,
    val isHighlighted: Boolean = false
)
