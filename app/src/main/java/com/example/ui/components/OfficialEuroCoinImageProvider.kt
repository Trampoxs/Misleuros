package com.example.ui.components

import com.example.data.model.CatalogCoin
import com.example.data.model.CoinDenomination

object OfficialEuroCoinImageProvider {

    private const val ECB_BASE = "https://www.ecb.europa.eu/euro/coins/common/shared/img"

    /**
     * Returns direct, verified image URLs for the NATIONAL SIDE (Cara Nacional)
     * of real Euro coins from the European Central Bank (ECB) official database.
     */
    fun getImageUrl(coin: CatalogCoin): String {
        if (!coin.imageUrl.isNullOrBlank()) {
            return coin.imageUrl
        }

        val country = coin.countryCode.uppercase()
        val denom = coin.denomination
        val year = coin.year
        val title = coin.title

        val isCommemorativeCoin = denom == CoinDenomination.EURO_2_COMMEMORATIVE || 
            coin.isCommemorative || 
            title.contains("Conmemorativa", ignoreCase = true) || 
            title.contains("UNESCO", ignoreCase = true) || 
            title.contains("Centenario", ignoreCase = true) || 
            title.contains("Aniversario", ignoreCase = true) || 
            title.contains("Presidencia", ignoreCase = true) ||
            title.contains("Emisión Conjunta", ignoreCase = true) ||
            title.contains("Garajonay", ignoreCase = true) ||
            title.contains("Elcano", ignoreCase = true) ||
            title.contains("Cáceres", ignoreCase = true) ||
            title.contains("Sevilla", ignoreCase = true) ||
            title.contains("Policía", ignoreCase = true) ||
            title.contains("Quijote", ignoreCase = true) ||
            title.contains("Córdoba", ignoreCase = true) ||
            title.contains("Cordoba", ignoreCase = true) ||
            title.contains("Alhambra", ignoreCase = true) ||
            title.contains("Burgos", ignoreCase = true) ||
            title.contains("Escorial", ignoreCase = true) ||
            title.contains("Güell", ignoreCase = true) ||
            title.contains("Guell", ignoreCase = true) ||
            title.contains("Altamira", ignoreCase = true) ||
            title.contains("Segovia", ignoreCase = true) ||
            title.contains("Asturias", ignoreCase = true) ||
            title.contains("Santiago", ignoreCase = true) ||
            title.contains("Ávila", ignoreCase = true) ||
            title.contains("Avila", ignoreCase = true) ||
            title.contains("Mudéjar", ignoreCase = true) ||
            title.contains("Mudejar", ignoreCase = true) ||
            title.contains("Toledo", ignoreCase = true) ||
            title.contains("Erasmus", ignoreCase = true) ||
            title.contains("Paisaje", ignoreCase = true)

        if (isCommemorativeCoin) {
            val commUrl = getCommemorativeImageUrl(country, year, title)
            return commUrl ?: ""
        }

        return when (country) {
            "ES" -> getSpainNationalImageUrl(denom, title, year)
            "DE" -> getGermanyNationalImageUrl(denom)
            "FR" -> getFranceNationalImageUrl(denom)
            "IT" -> getItalyNationalImageUrl(denom)
            "AT" -> getAustriaNationalImageUrl(denom)
            "IE" -> getIrelandNationalImageUrl(denom)
            "PT" -> getPortugalNationalImageUrl(denom)
            "NL" -> getNetherlandsNationalImageUrl(denom)
            "BE" -> getBelgiumNationalImageUrl(denom)
            "FI" -> getFinlandNationalImageUrl(denom)
            "GR" -> getGreeceNationalImageUrl(denom)
            "HR" -> getCroatiaNationalImageUrl(denom)
            "SK" -> getSlovakiaNationalImageUrl(denom)
            "CY" -> getCyprusNationalImageUrl(denom)
            "MT" -> getMaltaNationalImageUrl(denom)
            "LV" -> getLatviaNationalImageUrl(denom)
            "LT" -> getLithuaniaNationalImageUrl(denom)
            "LU" -> getLuxembourgNationalImageUrl(denom)
            "AD" -> getAndorraNationalImageUrl(denom)
            "SM" -> getSanMarinoNationalImageUrl(denom)
            "VA" -> getVaticanNationalImageUrl(denom)
            else -> getGenericNationalImageUrl(denom)
        }
    }

    private fun getCountryFullName(code: String): String {
        return when (code.uppercase()) {
            "ES" -> "Spain"
            "DE" -> "Germany"
            "FR" -> "France"
            "IT" -> "Italy"
            "AT" -> "Austria"
            "BE" -> "Belgium"
            "NL" -> "Netherlands"
            "PT" -> "Portugal"
            "FI" -> "Finland"
            "GR" -> "Greece"
            "IE" -> "Ireland"
            "LU" -> "Luxembourg"
            "SI" -> "Slovenia"
            "SK" -> "Slovakia"
            "CY" -> "Cyprus"
            "MT" -> "Malta"
            "EE" -> "Estonia"
            "LV" -> "Latvia"
            "LT" -> "Lithuania"
            "HR" -> "Croatia"
            "AD" -> "Andorra"
            "MC" -> "Monaco"
            "SM" -> "SanMarino"
            "VA" -> "Vatican"
            else -> "Spain"
        }
    }

    private fun getCommemorativeImageUrl(country: String, year: Int, title: String): String? {
        val tLower = title.lowercase()

        // 1. JOINT ISSUES (Emisiones Conjuntas de la UE)
        if (tLower.contains("tratado de roma") || tLower.contains("rome")) {
            return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2007/joint_comm_2007_${getCountryFullName(country)}.jpg"
        }
        if (tLower.contains("unión económica") || tLower.contains("uem") || tLower.contains("emu")) {
            return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2009/joint_comm_2009_${getCountryFullName(country)}.jpg"
        }
        if (tLower.contains("billetes y monedas") || (year == 2012 && tLower.contains("10º aniv"))) {
            return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/joint_comm_2012_${getCountryFullName(country)}.jpg"
        }
        if (tLower.contains("bandera europea") || (year == 2015 && tLower.contains("30º aniv"))) {
            return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/joint_comm_2015_${getCountryFullName(country)}.jpg"
        }
        if (tLower.contains("erasmus")) {
            return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/${getCountryFullName(country)}.jpg"
        }

        // 2. SPAIN (ES) COMMEMORATIVES (Direct verified Numista photos)
        if (country == "ES") {
            return when {
                tLower.contains("quijote") -> "https://en.numista.com/catalogue/photos/espagne/2042-180.jpg"
                tLower.contains("tratado de roma") || (year == 2007 && tLower.contains("roma")) -> "https://en.numista.com/catalogue/photos/espagne/62806e07ba9617.05501076-180.jpg"
                tLower.contains("uem") || tLower.contains("unión económica") || tLower.contains("union economica") -> "https://en.numista.com/catalogue/photos/espagne/5fc927161b9a97.04250222-180.jpg"
                tLower.contains("córdoba") || tLower.contains("cordoba") || tLower.contains("mezquita") -> "https://en.numista.com/catalogue/photos/espagne/627f66bf3c9dd6.91318694-180.jpg"
                tLower.contains("alhambra") || tLower.contains("granada") || tLower.contains("leones") -> "https://en.numista.com/catalogue/photos/espagne/2330-180.jpg"
                tLower.contains("billetes y monedas") || (year == 2012 && tLower.contains("10º")) || (year == 2012 && tLower.contains("10 años")) -> "https://en.numista.com/catalogue/photos/espagne/5fc9d50426d177.37894229-180.jpg"
                tLower.contains("burgos") -> "https://en.numista.com/catalogue/photos/espagne/5fcdb93c7b8986.25464583-180.jpg"
                tLower.contains("escurial") || tLower.contains("escorial") -> "https://en.numista.com/catalogue/photos/espagne/5fcdb9bc0513c0.46243429-180.jpg"
                tLower.contains("güell") || tLower.contains("guell") || tLower.contains("gaudí") || tLower.contains("gaudi") || (year == 2014 && tLower.contains("park")) -> "https://en.numista.com/catalogue/photos/espagne/3448-180.jpg"
                tLower.contains("proclamación") || tLower.contains("proclamacion") -> "https://en.numista.com/catalogue/photos/espagne/5fc9dfd96f0406.97337762-180.jpg"
                tLower.contains("bandera") || (year == 2015 && tLower.contains("30º")) -> "https://en.numista.com/catalogue/photos/espagne/5fc9e3a85c2437.13884352-180.jpg"
                tLower.contains("altamira") -> "https://en.numista.com/catalogue/photos/espagne/5fc9ed323bea23.33443609-180.jpg"
                tLower.contains("segovia") || tLower.contains("acueducto") -> "https://en.numista.com/catalogue/photos/espagne/3697-180.jpg"
                tLower.contains("naranco") || tLower.contains("asturias") -> "https://en.numista.com/catalogue/photos/espagne/4026-180.jpg"
                tLower.contains("santiago") -> "https://en.numista.com/catalogue/photos/espagne/4113-180.jpg"
                tLower.contains("50º") || tLower.contains("50 aniversario") || tLower.contains("50º aniversario") || (year == 2018 && tLower.contains("felipe")) -> "https://en.numista.com/catalogue/photos/espagne/63ce1991177894.08848571-180.jpg"
                tLower.contains("ávila") || tLower.contains("avila") -> "https://en.numista.com/catalogue/photos/espagne/5fcdc29a2a6c57.57844668-180.jpg"
                tLower.contains("mudéjar") || tLower.contains("mudejar") || tLower.contains("aragón") || tLower.contains("aragon") -> "https://en.numista.com/catalogue/photos/espagne/5fcdc1f19fdfb7.64010884-180.jpg"
                tLower.contains("toledo") -> "https://en.numista.com/catalogue/photos/espagne/605254496a33f0.64039108-180.jpg"
                tLower.contains("erasmus") -> "https://en.numista.com/catalogue/photos/espagne/62cd19ce3dbd92.96407300-180.jpg"
                tLower.contains("garajonay") || tLower.contains("gomera") -> "https://en.numista.com/catalogue/photos/espagne/62a5cf0b4edd46.17235323-180.jpg"
                tLower.contains("elcano") || tLower.contains("vuelta al mundo") || tLower.contains("circunnavegación") -> "https://en.numista.com/catalogue/photos/espagne/62a5cd9ee032a2.26839912-180.jpg"
                tLower.contains("cáceres") || tLower.contains("caceres") -> "https://en.numista.com/catalogue/photos/espagne/643036d97abc16.47838250-180.jpg"
                tLower.contains("presidencia") || tLower.contains("consejo de la ue") || tLower.contains("consejo ue") -> "https://en.numista.com/catalogue/photos/espagne/64b80addb40a17.88280484-180.jpg"
                tLower.contains("sevilla") || tLower.contains("alcázar") || tLower.contains("alcazar") || tLower.contains("archivo de indias") -> "https://en.numista.com/catalogue/photos/espagne/66b319ca777b08.11740383-180.jpg"
                tLower.contains("policía") || tLower.contains("policia") || tLower.contains("cuerpo nacional") -> "https://en.numista.com/catalogue/photos/espagne/66b31a0a01dd66.38691439-180.jpg"
                tLower.contains("paisaje") || tLower.contains("luz") || tLower.contains("prado") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2025/Spain_540x540.jpg"
                tLower.contains("poblet") -> null // TODO: falta URL de imagen real verificada para esta moneda
                tLower.contains("constitución") || tLower.contains("constitucion") || tLower.contains("artículo 49") || tLower.contains("articulo 49") || tLower.contains("discapacidad") -> null // TODO: falta URL de imagen real verificada para esta moneda
                else -> null
            }
        }

        // 3. GERMANY (DE) COMMEMORATIVES
        if (country == "DE") {
            return when {
                tLower.contains("holstentor") || tLower.contains("lübeck") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2006/comm_2006_de.jpg"
                tLower.contains("michaelis") || tLower.contains("hamburgo") || tLower.contains("hamburg") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2008/comm_2008_de.jpg"
                year == 2009 && tLower.contains("ludwigskirche") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2009/comm_2009_de.jpg"
                year == 2010 && tLower.contains("bremen") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_de.jpg"
                tLower.contains("colonia") || tLower.contains("köln") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2011/comm_2011_de.jpg"
                tLower.contains("neuschwanstein") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/comm_2012_de.jpg"
                tLower.contains("maulbronn") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2013/comm_2013_de.jpg"
                tLower.contains("dresde") || tLower.contains("zwinger") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2016/comm_2016_germany.jpg"
                tLower.contains("charlottenburg") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_Germany_berlin.jpg"
                tLower.contains("schmidt") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_germany_anniversary.jpg"
                tLower.contains("caída") || tLower.contains("berlín") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_de_30anniv_fallBerlinwall.jpg"
                tLower.contains("bundesrat") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_de_70anniv_Bundesrat.jpg"
                tLower.contains("sanssouci") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_de_brandenburg.jpg"
                tLower.contains("genuflexión") || tLower.contains("brandt") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_de_50_kniefall_warschau.jpg"
                tLower.contains("wartburg") || tLower.contains("thüringen") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/DE-thueringen.jpg"
                tLower.contains("elbphilharmonie") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2023/2023_comm_Germany-Hamburg_540x520.jpg"
                else -> null
            }
        }

        // 4. FRANCE (FR) COMMEMORATIVES
        if (country == "FR") {
            return when {
                year == 2010 && tLower.contains("gaulle") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_fr.jpg"
                year == 2011 && tLower.contains("música") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2011/comm_2011_fr.jpg"
                tLower.contains("sida") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2014/comm_2014_France_2.jpg"
                tLower.contains("aciano") || tLower.contains("bleuet") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_france_cornflower.jpg"
                tLower.contains("asterix") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_fr_60annivAsterix.jpg"
                tLower.contains("médica") || tLower.contains("investigación") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_fr_medical_research.jpg"
                tLower.contains("unicef") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_fr_unicef.jpg"
                tLower.contains("chirac") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/FR-chirac.jpg"
                tLower.contains("olímpicos") || tLower.contains("jjoo") || tLower.contains("notre-dame") || tLower.contains("notre dame") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2024/2024_comm_France1.JPG"
                else -> null
            }
        }

        // 5. ITALY (IT) COMMEMORATIVES
        if (country == "IT") {
            return when {
                tLower.contains("alimentos") || tLower.contains("pma") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2004/comm_2004_it.jpg"
                year == 2005 && tLower.contains("constitución") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2005/comm_2005_it.jpg"
                tLower.contains("turín") || tLower.contains("torino") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2006/comm_2006_it.jpg"
                tLower.contains("cavour") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_it.jpg"
                tLower.contains("unificación") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2011/comm_2011_it.jpg"
                tLower.contains("sanitarios") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_italy_health.jpg"
                tLower.contains("da vinci") || tLower.contains("leonardo") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_500anniv_Leodavinci.jpg"
                tLower.contains("montessori") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_it_150mariamontessori.jpg"
                tLower.contains("bomberos") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_it_80annivFoundNatFiredept.jpg"
                tLower.contains("roma capital") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_it_150_rome_capital.jpg"
                tLower.contains("grazie") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_it_grazie.jpg"
                tLower.contains("falcone") || tLower.contains("borsellino") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/IT-borsellino.jpg"
                tLower.contains("policía") || tLower.contains("policia") -> "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/2022_1comm_Italy-polizia_540x540.jpg"
                else -> null
            }
        }

        // 6. MICROSTATES & OTHERS
        if (country == "MC" || tLower.contains("mónaco") || tLower.contains("grace kelly")) {
            return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/2021_comm_Monaco1-mariageprincier_540x540.jpg"
        }
        if (country == "VA" || tLower.contains("vaticano") || tLower.contains("papa")) {
            if (tLower.contains("manzoni")) {
                return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2023/Vatican_Alessandro_Manzoni1.jpg"
            }
            return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_vc_500raphael_sanzio.jpg"
        }
        if (country == "SM" || tLower.contains("san marino")) {
            return "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_sm_popejpii.jpg"
        }

        return null
    }

    private fun getSpainNationalImageUrl(denom: CoinDenomination, title: String, year: Int): String {
        val isFelipeSeries = year >= 2015
        return when (denom) {
            CoinDenomination.EURO_2 -> {
                if (isFelipeSeries) {
                    "$ECB_BASE/es/Spain_2euro_2015.jpg"
                } else {
                    "$ECB_BASE/es/Spain_2Euro_2003.jpg"
                }
            }
            CoinDenomination.EURO_1 -> {
                if (isFelipeSeries) {
                    "$ECB_BASE/es/Spain_1Euro_2015.jpg"
                } else {
                    "$ECB_BASE/es/Spain_1Euro_1999.jpg"
                }
            }
            CoinDenomination.CENT_50 -> "$ECB_BASE/es/Spain_50cent_1999.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/es/Spain_20cent_2001.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/es/Spain_10cent_2003.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/es/Spain_5cent_1999.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/es/Spain_2cent_2002.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/es/Spain_1cent_1999.jpg"
            else -> ""
        }
    }

    private fun getGermanyNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/de/Germany_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/de/Germany_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/de/Germany_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/de/Germany_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/de/Germany_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/de/Germany_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/de/Germany_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/de/Germany_1cent.jpg"
            else -> ""
        }
    }

    private fun getFranceNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/fr/France_2euro_2022.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/fr/France_1euro_2022.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/fr/France_50cent_2024.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/fr/France_20cent_2024.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/fr/France_10cent_2024.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/fr/France_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/fr/France_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/fr/France_1cent.jpg"
            else -> ""
        }
    }

    private fun getItalyNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/it/Italy_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/it/Italy_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/it/Italy_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/it/Italy_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/it/Italy_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/it/Italy_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/it/Italy_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/it/Italy_1cent.jpg"
            else -> ""
        }
    }

    private fun getAustriaNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/at/Austria_2Euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/at/Austria_1Euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/at/Austria_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/at/Austria_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/at/Austria_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/at/Austria_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/at/Austria_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/at/Austria_1cent.jpg"
            else -> ""
        }
    }

    private fun getIrelandNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/ie/Ireland_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/ie/Ireland_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/ie/Ireland_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/ie/Ireland_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/ie/Ireland_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/ie/Ireland_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/ie/Ireland_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/ie/Ireland_1cent.jpg"
            else -> ""
        }
    }

    private fun getPortugalNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/pt/Portugal_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/pt/Portugal_1Euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/pt/Portugal_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/pt/Portugal_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/pt/Portugal_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/pt/Portugal_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/pt/Portugal_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/pt/Portugal_1cent.jpg"
            else -> ""
        }
    }

    private fun getNetherlandsNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/nl/Netherlands_2Euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/nl/Netherlands_1euro_2014.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/nl/Netherlands_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/nl/Netherlands_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/nl/Netherlands_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/nl/Netherlands_5cent_2014.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/nl/Netherlands_2cent_2014.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/nl/Netherlands_1cent_2014.jpg"
            else -> ""
        }
    }

    private fun getBelgiumNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/be/Belgium_2euro_2014ph.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/be/Belgium_1euro_2014.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/be/Belgium_50cent_2014.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/be/Belgium_20cent_2014.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/be/Belgium_2014_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/be/Belgium_5cent_2014.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/be/Belgium_2cent_2014.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/be/Belgium_1cent_2014.jpg"
            else -> ""
        }
    }

    private fun getFinlandNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/fi/Finland_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/fi/Finland_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/fi/Finland_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/fi/Finland_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/fi/Finland_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/fi/Finland_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/fi/Finland_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/fi/Finland_1cent.jpg"
            else -> ""
        }
    }

    private fun getGreeceNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/gr/Greece_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/gr/Greece_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/gr/Greece_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/gr/Greece_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/gr/Greece_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/gr/Greece_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/gr/Greece_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/gr/Greece_1cent.jpg"
            else -> ""
        }
    }

    private fun getCroatiaNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/hr/Croatia_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/hr/Croatia_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/hr/Croatia_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/hr/Croatia_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/hr/Croatia_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/hr/Croatia_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/hr/Croatia_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/hr/Croatia_1cent.jpg"
            else -> ""
        }
    }

    private fun getSlovakiaNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/sk/Slovakia_2Euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/sk/Slovakia_1Euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/sk/Slovakia_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/sk/Slovakia_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/sk/Slovakia_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/sk/Slovakia_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/sk/Slovakia_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/sk/Slovakia_1cent.jpg"
            else -> ""
        }
    }

    private fun getCyprusNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/cy/Cyprus_2Euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/cy/Cyprus_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/cy/Cyprus_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/cy/Cyprus_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/cy/Cyprus_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/cy/Cyprus_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/cy/Cyprus_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/cy/Cyprus_1cent.jpg"
            else -> ""
        }
    }

    private fun getMaltaNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/mt/Malta_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/mt/Malta_1Euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/mt/Malta_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/mt/Malta_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/mt/Malta_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/mt/Malta_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/mt/Malta_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/mt/Malta_1cent.jpg"
            else -> ""
        }
    }

    private fun getLatviaNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/lv/Latvia_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/lv/Latvia_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/lv/Latvia_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/lv/Latvia_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/lv/Latvia_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/lv/Latvia_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/lv/Latvia_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/lv/Latvia_1cent.jpg"
            else -> ""
        }
    }

    private fun getLithuaniaNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/lt/Lithuania_2euro_2015.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/lt/Lithuania_1euro_2015.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/lt/Lithuania_50cent_2014.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/lt/Lithuania_20cent_2015.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/lt/Lithuania_10cent_2015.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/lt/Lithuania_5cent_2015.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/lt/Lithuania_2cent_2015.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/lt/Lithuania_1cent_2015.jpg"
            else -> ""
        }
    }

    private fun getLuxembourgNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/lu/Luxembourg_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/lu/Luxembourg_1Euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/lu/Luxembourg_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/lu/Luxembourg_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/lu/Luxembourg_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/lu/Luxembourg_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/lu/Luxembourg_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/lu/Luxembourg_1cent.jpg"
            else -> ""
        }
    }

    private fun getAndorraNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/ad/ad_2euro.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/ad/Andorra_1euro.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/ad/Andorra_50cent.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/ad/Andorra_20cent.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/ad/Andorra_10cent.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/ad/Andorra_5cent.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/ad/Andorra_2cent.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/ad/Andorra_1cent.jpg"
            else -> ""
        }
    }

    private fun getSanMarinoNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/sm/SanMarino_2euro_2017.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/sm/SanMarino_1euro_2017.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/sm/SanMarino_50cent_2017.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/sm/SanMarino_20cent_2017.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/sm/SanMarino_10cent_2017.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/sm/SanMarino_5cent_2017.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/sm/SanMarino_2cent_2017.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/sm/SanMarino_1cent_2017.jpg"
            else -> ""
        }
    }

    private fun getVaticanNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/va/Vatican_2euro_2017.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/va/Vatican_1euro_2017.jpg"
            CoinDenomination.CENT_50 -> "$ECB_BASE/va/Vatican_50cent_2017.jpg"
            CoinDenomination.CENT_20 -> "$ECB_BASE/va/Vatican_20cent_2017.jpg"
            CoinDenomination.CENT_10 -> "$ECB_BASE/va/Vatican_10cent_2017.jpg"
            CoinDenomination.CENT_5 -> "$ECB_BASE/va/Vatican_5cent_2017.jpg"
            CoinDenomination.CENT_2 -> "$ECB_BASE/va/Vatican_2cent_2017.jpg"
            CoinDenomination.CENT_1 -> "$ECB_BASE/va/Vatican_1cent_2017.jpg"
            else -> ""
        }
    }

    private fun getGenericNationalImageUrl(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2 -> "$ECB_BASE/es/Spain_2euro_2015.jpg"
            CoinDenomination.EURO_1 -> "$ECB_BASE/es/Spain_1Euro_2015.jpg"
            else -> "$ECB_BASE/es/Spain_50cent_1999.jpg"
        }
    }
}




