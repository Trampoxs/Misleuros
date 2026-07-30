package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.model.NumismaticNewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NumismaticNewsRepository {

    private val initialNewsList = mutableListOf(
        NumismaticNewsItem(
            id = "news_es_2026_generalife",
            title = "España 2026: 2€ Conmemorativa 'Jardín del Generalife de Granada'",
            summary = "Continuando la prestigiosa serie UNESCO, España emitirá en 2026 la moneda dedicada a los Palacios y Jardines del Generalife.",
            fullContent = "La Real Casa de la Moneda (FNMT) de España ha anunciado la temática oficial de la moneda de 2 Euros Conmemorativa para el año 2026. La pieza estará dedicada al conjunto monumental de la Alhambra, Generalife y Albaicín de Granada, inscrito como Patrimonio Mundial de la UNESCO.\n\nEl reverso del núcleo dorado representará una vista estilizada del Patio de la Acequia con sus icónicos surtidores de agua y los arcos nazaríes. La tirada estimada será de 1.500.000 piezas para circulación.",
            countryCode = "ES",
            countryName = "España",
            year = 2026,
            releaseDate = "Enero 2026",
            mintageVolume = "1.500.000 piezas",
            statusTag = "Confirmada oficial",
            category = "2€ Conmemorativa",
            imageUrl = "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/Spain.jpg",
            hasConfirmedImage = true,
            isHighlighted = true
        ),
        NumismaticNewsItem(
            id = "news_es_2026_articulo49",
            title = "España 2026: 2€ Conmemorativa 'Artículo 49 de la Constitución Española'",
            summary = "Segunda moneda española de 2€ confirmada para 2026 dedicada a la inclusión social y los derechos de las personas con discapacidad.",
            fullContent = "El Ministerio de Economía, Comercio y Empresa de España ha aprobado la acuñación de una segunda moneda de 2 euros conmemorativa en 2026. Esta pieza histórica celebra la reforma del Artículo 49 de la Constitución Española, promoviendo los derechos, la dignidad y la inclusión plena de las personas con discapacidad.",
            countryCode = "ES",
            countryName = "España",
            year = 2026,
            releaseDate = "Segundo semestre 2026",
            mintageVolume = "1.500.000 piezas",
            statusTag = "Confirmada oficial",
            category = "2€ Conmemorativa",
            imageUrl = "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_spain_felipe.jpg",
            hasConfirmedImage = true,
            isHighlighted = true
        ),
        NumismaticNewsItem(
            id = "news_fr_2025_notredame",
            title = "Francia 2025: 2€ Conmemorativa 'Reapertura de Notre-Dame de París'",
            summary = "Moneda especial para conmemorar la histórica reconstrucción y reapertura de la catedral gótica parisina.",
            fullContent = "La Monnaie de Paris lanzará una emisión histórica de 2 euros dedicada a la restauración y reapertura de la Catedral de Notre-Dame de París tras el incendio de 2019.\n\nEn la cara nacional se aprecia la majestuosa fachada oeste de la catedral flanqueada por la célebre roseta gótica y el año de emisión 2025.",
            countryCode = "FR",
            countryName = "Francia",
            year = 2025,
            releaseDate = "Febrero 2025",
            mintageVolume = "10.000.000 piezas",
            statusTag = "Emisión próxima",
            category = "2€ Conmemorativa",
            imageUrl = "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2024/2024_comm_France1.JPG",
            hasConfirmedImage = true,
            isHighlighted = true
        ),
        NumismaticNewsItem(
            id = "news_de_2026_bremen",
            title = "Alemania 2026: Serie Bundesländer II - Estado de Bremen",
            summary = "Cuarta entrega de la segunda serie de estados federados alemanes, dedicada a la ciudad libre hanseática de Bremen.",
            fullContent = "Alemania continúa su emblemática serie 'Bundesländer II' con la moneda dedicada a Bremen. El diseño ganador muestra la estatua de Roland de Bremen frente al Ayuntamiento gótico renacentista, ambos declarados Patrimonio de la Humanidad por la UNESCO.\n\nComo es habitual, la moneda será acuñada por las cinco cecas alemanas (A, D, F, G, J).",
            countryCode = "DE",
            countryName = "Alemania",
            year = 2026,
            releaseDate = "Enero 2026",
            mintageVolume = "30.000.000 piezas",
            statusTag = "Confirmada oficial",
            category = "Serie Bundesländer II",
            imageUrl = "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2023/2023_comm_Germany-Hamburg_540x520.jpg",
            hasConfirmedImage = true
        ),
        NumismaticNewsItem(
            id = "news_es_2025_paisajeluz",
            title = "España 2025: 2€ Conmemorativa 'Paisaje de la Luz, Madrid'",
            summary = "Moneda de la serie Patrimonio Mundial UNESCO dedicada al Paseo del Prado y Buen Retiro de Madrid.",
            fullContent = "La Fábrica Nacional de Moneda y Timbre (FNMT) emite en 2025 la moneda de 2 euros conmemorativa dedicada al 'Paisaje de la Luz' de Madrid (Paseo del Prado y Buen Retiro), sitio declarado Patrimonio Mundial de la UNESCO. El reverso destaca el entorno monumental del Paseo del Prado y la célebre Fuente de Cibeles.",
            countryCode = "ES",
            countryName = "España",
            year = 2025,
            releaseDate = "Primer trimestre 2025",
            mintageVolume = "1.500.000 piezas",
            statusTag = "En distribución",
            category = "2€ Conmemorativa",
            imageUrl = "",
            hasConfirmedImage = false
        ),
        NumismaticNewsItem(
            id = "news_it_2025_jubileo",
            title = "Italia 2025: 2€ Conmemorativa 'Año Jubilar 2025 de Roma'",
            summary = "Edición conmemorativa del Jubileo Santo 2025 emitida por el Instituto Poligráfico e Zecca dello Stato.",
            fullContent = "Italia dedicará una de sus monedas de 2 euros conmemorativas de 2025 al Jubileo Ordinario del año 2025 ('Peregrinos de la Esperanza'). El diseño representa la Puerta Santa de la Basílica de San Pedro junto con la paloma de la paz.",
            countryCode = "IT",
            countryName = "Italia",
            year = 2025,
            releaseDate = "Marzo 2025",
            mintageVolume = "3.000.000 piezas",
            statusTag = "Confirmada oficial",
            category = "2€ Conmemorativa",
            imageUrl = "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/IT-borsellino.jpg",
            hasConfirmedImage = true
        ),
        NumismaticNewsItem(
            id = "news_pt_2026_liberdade",
            title = "Portugal 2026: 2€ Conmemorativa '50 Años de la Constitución Portuguesa'",
            summary = "Celebración del 50º aniversario de la carta magna democrática de Portugal aprobada en 1976.",
            fullContent = "La Casa da Moeda de Portugal (INCM) ha preparado para 2026 una emotiva moneda de 2 euros celebrando el medio siglo de la Constitución de 1976. El diseño combina el texto estilizado de la Ley Fundamental con un clavel simbólico.",
            countryCode = "PT",
            countryName = "Portugal",
            year = 2026,
            releaseDate = "Abril 2026",
            mintageVolume = "500.000 piezas",
            statusTag = "Confirmada oficial",
            category = "2€ Conmemorativa",
            imageUrl = "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/Portugal.jpg",
            hasConfirmedImage = true
        )
    )

    private val availableLateBreakingNews = listOf(
        NumismaticNewsItem(
            id = "news_mc_2026_grandprix",
            title = "Mónaco 2026: 2€ Conmemorativa 'Centenario del Gran Premio de Mónaco'",
            summary = "Última hora: El Palacio Principesco confirma el diseño oficial de la nueva joya numismática monegasca.",
            fullContent = "El Principado de Mónaco emitirá una cotizada moneda de 2 euros conmemorativa dedicada a los 100 años de automovilismo en el circuito urbano de Montecarlo. Con una tirada estricta en calidad Proof, se prevé una altísima demanda en el mercado numismático.",
            countryCode = "MC",
            countryName = "Mónaco",
            year = 2026,
            releaseDate = "Mayo 2026",
            mintageVolume = "15.000 piezas (Proof)",
            statusTag = "Novedad de hoy",
            category = "Joyas Numismáticas",
            imageUrl = "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/2021_comm_Monaco1-mariageprincier_540x540.jpg",
            hasConfirmedImage = true,
            isHighlighted = true
        ),
        NumismaticNewsItem(
            id = "news_va_2026_guardia",
            title = "Vaticano 2026: 2€ Conmemorativa '500 Años de la Guardia Suiza Pontificia'",
            summary = "Publicado el boletín oficial del UFN del Vaticano con los primeros bocetos aprobados.",
            fullContent = "La Oficina Filatélica y Numismática de la Ciudad del Vaticano ha desvelado la imagen de la moneda de 2€ conmemorativa que celebrará medio milenio de servicio ininterrumpido de la Guardia Suiza Pontificia. Muestra a un alabardero con el uniforme tradicional diseñado según la tradición del Renacimiento.",
            countryCode = "VA",
            countryName = "Vaticano",
            year = 2026,
            releaseDate = "Junio 2026",
            mintageVolume = "80.000 piezas",
            statusTag = "Novedad de hoy",
            category = "2€ Conmemorativa",
            imageUrl = "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_vc_500raphael_sanzio.jpg",
            hasConfirmedImage = true
        ),
        NumismaticNewsItem(
            id = "news_eu_2026_conjunta",
            title = "Eurozona 2026: Propuesta de Emisión Conjunta Conmemorativa",
            summary = "El Consejo Europeo debate la aprobación de una nueva emisión conjunta de 2€ para todos los países del euro.",
            fullContent = "Representantes del Banco Central Europeo y los ministerios de finanzas de los países miembros han presentado la propuesta técnica para una nueva emisión de 2 euros común que circulará simultáneamente en los 20 países de la Eurozona en 2026.",
            countryCode = "EU",
            countryName = "Europa / UE",
            year = 2026,
            releaseDate = "Cuarto trimestre 2026",
            mintageVolume = "100.000.000 piezas (Eurozona)",
            statusTag = "En tramitación",
            category = "Emisión Conjunta",
            imageUrl = "",
            hasConfirmedImage = false
        )
    )

    private val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    private fun getTodayDateString(): String = "Hoy, ${dateFormat.format(Date())}"

    private val _newsFlow = MutableStateFlow<List<NumismaticNewsItem>>(initialNewsList)
    private val _lastSyncFlow = MutableStateFlow(getTodayDateString())
    private val _isRefreshingFlow = MutableStateFlow(false)

    fun getNews(): Flow<List<NumismaticNewsItem>> = _newsFlow.asStateFlow()
    fun getLastSyncDate(): Flow<String> = _lastSyncFlow.asStateFlow()
    fun getIsRefreshing(): Flow<Boolean> = _isRefreshingFlow.asStateFlow()

    private suspend fun isNetworkAvailable(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (connectivityManager != null) {
                val activeNetwork = connectivityManager.activeNetwork ?: return@withContext false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return@withContext false
                if (!capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    return@withContext false
                }
            }
            val url = java.net.URL("https://www.ecb.europa.eu")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            val code = conn.responseCode
            conn.disconnect()
            code in 200..399
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkAndRefreshDailyNews(context: Context? = null): Int {
        _isRefreshingFlow.value = true
        delay(800)

        if (context != null && !isNetworkAvailable(context)) {
            _isRefreshingFlow.value = false
            return -1
        }

        var newArticlesCount = 0
        val currentIds = _newsFlow.value.map { it.id }.toSet()

        for (item in availableLateBreakingNews) {
            if (!currentIds.contains(item.id)) {
                initialNewsList.add(0, item)
                newArticlesCount++
            }
        }

        _newsFlow.value = initialNewsList.toList()
        _lastSyncFlow.value = getTodayDateString()
        _isRefreshingFlow.value = false

        return newArticlesCount
    }
}
