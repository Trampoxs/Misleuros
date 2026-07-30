package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.data.model.CatalogCoin
import com.example.data.model.CoinDenomination
import com.example.data.repository.NumistaRepository
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CoinVisualGraphic(
    coin: CatalogCoin,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    showDetails: Boolean = true,
    allowFlip: Boolean = false
) {
    val context = LocalContext.current
    val hardcodedUrl = OfficialEuroCoinImageProvider.getImageUrl(coin)
    var imageUrl by remember(coin.id) { mutableStateOf(hardcodedUrl) }

    // Cada moneda que se muestra en pantalla se intenta verificar en segundo plano contra
    // Numista (con caché, para no repetir peticiones). Si encuentra una imagen real, la
    // sustituye; si no encuentra nada o falla, se queda con la que ya hubiera.
    LaunchedEffect(coin.id) {
        val verified = NumistaRepository.resolveVerifiedImageUrl(coin)
        if (!verified.isNullOrBlank()) {
            imageUrl = verified
        }
    }

    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl.ifBlank { null })
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36 EuroCoinCatalog/1.0"
            )
            .crossfade(true)
            .build()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .shadow(elevation = 4.dp, shape = CircleShape, clip = false)
            .clip(CircleShape)
            .border(
                width = if (size > 70.dp) 2.5.dp else 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFFDF73), Color(0xFF9E7711), Color(0xFFE2E8F0))
                ),
                shape = CircleShape
            )
            .background(Color(0xFFE2E8F0))
    ) {
        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = "${coin.title} (Arte de la moneda)",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        ) {
            val state = painter.state
            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    MetallicFallbackCoinGraphic(coin = coin, size = size, isNationalSide = true)
                    CircularProgressIndicator(
                        modifier = Modifier.size(size * 0.35f),
                        strokeWidth = 2.dp,
                        color = Color(0xFF0052CC)
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    // Fallback to rich, stylized realistic metallic coin graphic with National Art / 12 Stars
                    MetallicFallbackCoinGraphic(coin = coin, size = size, isNationalSide = true)
                }
                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
    }
}

private fun getCommonSideUrl(denom: CoinDenomination): String {
    return when (denom) {
        CoinDenomination.CENT_1 -> "https://upload.wikimedia.org/wikipedia/en/2/22/1_cent_euro_coin_common_side.png"
        CoinDenomination.CENT_2 -> "https://upload.wikimedia.org/wikipedia/en/8/8f/2_eurocent_common_1999.png"
        CoinDenomination.CENT_5 -> "https://upload.wikimedia.org/wikipedia/en/1/1e/5_eurocent_common_1999.png"
        CoinDenomination.CENT_10 -> "https://upload.wikimedia.org/wikipedia/en/d/d2/Common_face_of_10_cent_euro_coin_%28first_series%29.jpeg"
        CoinDenomination.CENT_20 -> "https://upload.wikimedia.org/wikipedia/en/d/d9/Common_face_of_20_cent_euro_coin_%28first_series%29.jpeg"
        CoinDenomination.CENT_50 -> "https://upload.wikimedia.org/wikipedia/en/d/dc/Common_face_of_50_eurocent_coin_%28first_series%29.jpeg"
        CoinDenomination.EURO_1 -> "https://upload.wikimedia.org/wikipedia/en/1/10/Common_face_of_one_euro_coin.png"
        CoinDenomination.EURO_2, CoinDenomination.EURO_2_COMMEMORATIVE -> "https://upload.wikimedia.org/wikipedia/en/6/65/Common_face_of_two_euro_coin.png"
    }
}

@Composable
private fun MetallicFallbackCoinGraphic(
    coin: CatalogCoin,
    size: Dp,
    isNationalSide: Boolean
) {
    val denomination = coin.denomination
    val goldOuter = Brush.radialGradient(
        colors = listOf(Color(0xFFFFF5B8), Color(0xFFD4AF37), Color(0xFF8F6F11)),
        center = Offset(0.35f, 0.35f)
    )
    val silverInner = Brush.radialGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0), Color(0xFF8092A7)),
        center = Offset(0.35f, 0.35f)
    )
    val copperGradient = Brush.radialGradient(
        colors = listOf(Color(0xFFFFB380), Color(0xFFB87333), Color(0xFF5A2A05)),
        center = Offset(0.35f, 0.35f)
    )
    val nordicGoldGradient = Brush.radialGradient(
        colors = listOf(Color(0xFFFFF1A8), Color(0xFFE5C158), Color(0xFF8A6A00)),
        center = Offset(0.35f, 0.35f)
    )

    val starColor = when (denomination) {
        CoinDenomination.EURO_1 -> Color(0xFF5C4500)
        CoinDenomination.EURO_2, CoinDenomination.EURO_2_COMMEMORATIVE -> Color(0xFF334155)
        CoinDenomination.CENT_1, CoinDenomination.CENT_2, CoinDenomination.CENT_5 -> Color(0xFFFFF3E0)
        else -> Color(0xFF4A3800)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Metallic Disc Base
        when (denomination) {
            CoinDenomination.CENT_1, CoinDenomination.CENT_2, CoinDenomination.CENT_5 -> {
                Box(modifier = Modifier.fillMaxSize().background(copperGradient))
            }
            CoinDenomination.CENT_10, CoinDenomination.CENT_20, CoinDenomination.CENT_50 -> {
                Box(modifier = Modifier.fillMaxSize().background(nordicGoldGradient))
            }
            CoinDenomination.EURO_1 -> {
                Box(modifier = Modifier.fillMaxSize().background(goldOuter), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.68f)
                            .clip(CircleShape)
                            .background(silverInner)
                            .border(1.dp, Color(0xFFB8911C), CircleShape)
                    )
                }
            }
            CoinDenomination.EURO_2, CoinDenomination.EURO_2_COMMEMORATIVE -> {
                Box(modifier = Modifier.fillMaxSize().background(silverInner), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.68f)
                            .clip(CircleShape)
                            .background(goldOuter)
                            .border(1.dp, Color(0xFF94A3B8), CircleShape)
                    )
                }
            }
        }

        // 12 European Union Stars Ring (Outer Edge)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.toPx() / 2, size.toPx() / 2)
            val radius = size.toPx() * 0.41f
            val starRadius = size.toPx() * 0.035f

            for (i in 0 until 12) {
                val angle = (i * 30.0 - 90.0) * (PI / 180.0)
                val starCenter = Offset(
                    x = (center.x + radius * cos(angle)).toFloat(),
                    y = (center.y + radius * sin(angle)).toFloat()
                )

                // Draw small 5-pointed star path
                val starPath = Path()
                for (j in 0 until 5) {
                    val outerAngle = (j * 72.0 - 90.0) * (PI / 180.0)
                    val innerAngle = ((j * 72.0 + 36.0) - 90.0) * (PI / 180.0)

                    val outerX = starCenter.x + starRadius * cos(outerAngle).toFloat()
                    val outerY = starCenter.y + starRadius * sin(outerAngle).toFloat()
                    val innerX = starCenter.x + (starRadius * 0.4f) * cos(innerAngle).toFloat()
                    val innerY = starCenter.y + (starRadius * 0.4f) * sin(innerAngle).toFloat()

                    if (j == 0) starPath.moveTo(outerX, outerY) else starPath.lineTo(outerX, outerY)
                    starPath.lineTo(innerX, innerY)
                }
                starPath.close()
                drawPath(path = starPath, color = starColor)
            }
        }

        // Center Artwork Content (Cara Nacional con Arte vs Cara Común con Valor)
        if (isNationalSide) {
            NationalArtOverlay(coin = coin, size = size)
        } else {
            CommonValueOverlay(coin = coin, size = size)
        }
    }
}

@Composable
private fun NationalArtOverlay(coin: CatalogCoin, size: Dp) {
    val country = coin.countryCode.uppercase()
    val isSpain = country == "ES"
    val isComm = coin.isCommemorative || 
            coin.denomination == CoinDenomination.EURO_2_COMMEMORATIVE ||
            coin.title.contains("Conmemorativa", ignoreCase = true) ||
            coin.title.contains("UNESCO", ignoreCase = true) ||
            coin.title.contains("Aniversario", ignoreCase = true) ||
            coin.title.contains("Centenario", ignoreCase = true) ||
            coin.title.contains("Presidencia", ignoreCase = true) ||
            coin.title.contains("Garajonay", ignoreCase = true) ||
            coin.title.contains("Elcano", ignoreCase = true) ||
            coin.title.contains("Cáceres", ignoreCase = true) ||
            coin.title.contains("Sevilla", ignoreCase = true) ||
            coin.title.contains("Policía", ignoreCase = true)

    val primaryText = when {
        isComm -> getCommemorativeShortArtLabel(coin.title)
        isSpain && (coin.denomination == CoinDenomination.EURO_1 || coin.denomination == CoinDenomination.EURO_2) -> {
            if (coin.year in 1999..2014) "JUAN CARLOS I" else "FELIPE VI"
        }
        isSpain && (coin.denomination == CoinDenomination.CENT_50 || coin.denomination == CoinDenomination.CENT_20 || coin.denomination == CoinDenomination.CENT_10) -> "CERVANTES"
        isSpain -> "SANTIAGO"
        country == "DE" -> "BUNDESADLER"
        country == "FR" -> "L'ARBRE"
        country == "IT" -> "DANTE"
        country == "AT" -> "MOZART"
        country == "IE" -> "HARP"
        else -> coin.countryName
    }

    val countryCodeText = when (country) {
        "ES" -> "ESPAÑA"
        "DE" -> "D"
        "FR" -> "RF"
        "IT" -> "RI"
        "AT" -> "ÖSTERREICH"
        "IE" -> "ÉIRE"
        else -> country
    }

    val artEmoji = when {
        isComm -> "🏛️"
        isSpain && (coin.denomination == CoinDenomination.EURO_1 || coin.denomination == CoinDenomination.EURO_2) -> "👑"
        isSpain && (coin.denomination == CoinDenomination.CENT_50 || coin.denomination == CoinDenomination.CENT_20 || coin.denomination == CoinDenomination.CENT_10) -> "✒️"
        isSpain -> "⛪"
        country == "DE" -> "🦅"
        country == "FR" -> "🌳"
        country == "IT" -> "🎨"
        country == "AT" -> "🎼"
        country == "IE" -> "🎻"
        else -> getFlagEmoji(country)
    }

    val textColor = when (coin.denomination) {
        CoinDenomination.CENT_1, CoinDenomination.CENT_2, CoinDenomination.CENT_5 -> Color.White
        CoinDenomination.EURO_1 -> Color(0xFF1E293B)
        CoinDenomination.EURO_2, CoinDenomination.EURO_2_COMMEMORATIVE -> Color(0xFF332000)
        else -> Color(0xFF3D2E00)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        if (size >= 50.dp) {
            Text(
                text = artEmoji,
                fontSize = if (size > 70.dp) 18.sp else 11.sp
            )
        }
        Text(
            text = primaryText,
            fontWeight = FontWeight.Black,
            fontSize = if (size > 70.dp) 11.sp else if (size >= 50.dp) 8.sp else 7.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        if (size >= 50.dp) {
            Text(
                text = "$countryCodeText '${coin.year.toString().takeLast(2)}",
                fontWeight = FontWeight.Bold,
                fontSize = if (size > 70.dp) 9.sp else 7.sp,
                color = textColor.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CommonValueOverlay(coin: CatalogCoin, size: Dp) {
    val label = when (coin.denomination) {
        CoinDenomination.EURO_1 -> "1 EURO"
        CoinDenomination.EURO_2, CoinDenomination.EURO_2_COMMEMORATIVE -> "2 EURO"
        else -> coin.denomination.code
    }

    val textColor = when (coin.denomination) {
        CoinDenomination.CENT_1, CoinDenomination.CENT_2, CoinDenomination.CENT_5 -> Color.White
        CoinDenomination.EURO_1 -> Color(0xFF1E293B)
        CoinDenomination.EURO_2, CoinDenomination.EURO_2_COMMEMORATIVE -> Color(0xFF332000)
        else -> Color(0xFF3D2E00)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Black,
            fontSize = if (size > 70.dp) 15.sp else if (size >= 50.dp) 10.sp else 8.sp,
            color = textColor,
            textAlign = TextAlign.Center
        )
        Text(
            text = "EURO",
            fontWeight = FontWeight.Bold,
            fontSize = if (size > 70.dp) 9.sp else 7.sp,
            color = textColor.copy(alpha = 0.8f)
        )
    }
}

private fun getCommemorativeShortArtLabel(title: String): String {
    val lower = title.lowercase()
    return when {
        "quijote" in lower -> "QUIJOTE"
        "alhambra" in lower -> "ALHAMBRA"
        "mezquita" in lower || "córdoba" in lower || "cordoba" in lower -> "MEZQUITA"
        "burgos" in lower -> "BURGOS"
        "escorial" in lower || "escurial" in lower -> "ESCORIAL"
        "güell" in lower || "guell" in lower || "gaudí" in lower -> "PARK GÜELL"
        "altamira" in lower -> "ALTAMIRA"
        "aqueduct" in lower || "acueducto" in lower || "segovia" in lower -> "ACUEDUCTO"
        "felipe" in lower -> "FELIPE VI"
        "santiago" in lower -> "SANTIAGO"
        "erasmus" in lower -> "ERASMUS"
        "garajonay" in lower -> "GARAJONAY"
        "elcano" in lower -> "ELCANO"
        "cáceres" in lower || "caceres" in lower -> "CÁCERES"
        "presidencia" in lower -> "PRESIDENCIA"
        "sevilla" in lower -> "SEVILLA"
        "policía" in lower || "policia" in lower || "police" in lower -> "POLICÍA"
        "poblet" in lower -> "POBLET"
        "artículo 49" in lower || "articulo 49" in lower || "discapacidad" in lower -> "CONST. ART 49"
        "paisaje" in lower || "luz" in lower -> "PAISAJE LUZ"
        "mudéjar" in lower || "mudejar" in lower || "aragón" in lower -> "MUDÉJAR"
        "toledo" in lower -> "TOLEDO"
        "ávila" in lower || "avila" in lower -> "ÁVILA"
        "naranco" in lower || "asturias" in lower -> "NARANCO"
        "roma" in lower -> "TRATADO ROMA"
        "uem" in lower -> "UEM 10 AÑOS"
        "bandera" in lower -> "BANDERA UE"
        "billetes" in lower -> "10 AÑOS EURO"
        else -> title.take(10).uppercase()
    }
}

private fun getFlagEmoji(countryCode: String): String {
    return when (countryCode.uppercase()) {
        "ES" -> "🇪🇸"
        "DE" -> "🇩🇪"
        "FR" -> "🇫🇷"
        "IT" -> "🇮🇹"
        "AT" -> "🇦🇹"
        "BE" -> "🇧🇪"
        "NL" -> "🇳🇱"
        "PT" -> "🇵🇹"
        "FI" -> "🇫🇮"
        "GR" -> "🇬🇷"
        "IE" -> "🇮🇪"
        "LU" -> "🇱🇺"
        "SK" -> "🇸🇰"
        "SI" -> "🇸🇮"
        "CY" -> "🇨🇾"
        "MT" -> "🇲🇹"
        "EE" -> "🇪🇪"
        "LV" -> "🇱🇻"
        "LT" -> "🇱🇹"
        "HR" -> "🇭🇷"
        "AD" -> "🇦🇩"
        "MC" -> "🇲🇨"
        "SM" -> "🇸🇲"
        "VA" -> "🇻🇦"
        else -> "🇪🇺"
    }
}


