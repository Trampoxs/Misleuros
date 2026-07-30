package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.db.CoinCollectionDao
import com.example.data.db.CustomCatalogCoinEntity
import com.example.data.model.CatalogCoin
import com.example.data.model.CoinDenomination
import com.example.data.model.EuroCountry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EuroCatalogRepository(private val coinDao: CoinCollectionDao) {

    val countriesList: List<EuroCountry> = listOf(
        EuroCountry("ES", "España", "🇪🇸", 1999, "#AA151B"),
        EuroCountry("DE", "Alemania", "🇩🇪", 2002, "#000000"),
        EuroCountry("FR", "Francia", "🇫🇷", 1999, "#002395"),
        EuroCountry("IT", "Italia", "🇮🇹", 2002, "#009246"),
        EuroCountry("AT", "Austria", "🇦🇹", 1999, "#ED2939"),
        EuroCountry("BE", "Bélgica", "🇧🇪", 1999, "#2D2926"),
        EuroCountry("NL", "Países Bajos", "🇳🇱", 1999, "#AE1C28"),
        EuroCountry("PT", "Portugal", "🇵🇹", 1999, "#006600"),
        EuroCountry("FI", "Finlandia", "🇫🇮", 1999, "#003580"),
        EuroCountry("GR", "Grecia", "🇬🇷", 2002, "#004C97"),
        EuroCountry("IE", "Irlanda", "🇮🇪", 1999, "#169B62"),
        EuroCountry("LU", "Luxemburgo", "🇱🇺", 1999, "#00A1DE"),
        EuroCountry("SK", "Eslovaquia", "🇸🇰", 2009, "#0B4EA2"),
        EuroCountry("SI", "Eslovenia", "🇸🇮", 2007, "#005DA4"),
        EuroCountry("CY", "Chipre", "🇨🇾", 2008, "#D47600"),
        EuroCountry("MT", "Malta", "🇲🇹", 2008, "#CF142B"),
        EuroCountry("EE", "Estonia", "🇪🇪", 2011, "#0072CE"),
        EuroCountry("LV", "Letonia", "🇱🇻", 2014, "#9E3039"),
        EuroCountry("LT", "Lituania", "🇱🇹", 2015, "#006A4E"),
        EuroCountry("HR", "Croacia", "🇭🇷", 2023, "#FF0000"),
        EuroCountry("AD", "Andorra", "🇦🇩", 2014, "#182C84"),
        EuroCountry("MC", "Mónaco", "🇲🇨", 2002, "#201018"),
        EuroCountry("SM", "San Marino", "🇸🇲", 2002, "#5091CD"),
        EuroCountry("VA", "Vaticano", "🇻🇦", 2002, "#FFE000")
    )

    fun getCountryByCode(code: String): EuroCountry? {
        return countriesList.find { it.code.equals(code, ignoreCase = true) }
    }

    private var maxCatalogYear: Int = 2026

    /**
     * Generates standard circulation coins for all years from country start year up to current year.
     */
    fun getStandardCatalogForCountry(countryCode: String, currentYear: Int = maxCatalogYear): List<CatalogCoin> {
        val country = getCountryByCode(countryCode) ?: return emptyList()
        val coins = mutableListOf<CatalogCoin>()

        val standardDenominations = listOf(
            CoinDenomination.CENT_1,
            CoinDenomination.CENT_2,
            CoinDenomination.CENT_5,
            CoinDenomination.CENT_10,
            CoinDenomination.CENT_20,
            CoinDenomination.CENT_50,
            CoinDenomination.EURO_1,
            CoinDenomination.EURO_2
        )

        for (yr in country.euroStartYear..currentYear) {
            for (denom in standardDenominations) {
                val title: String
                val description: String

                if (countryCode == "ES") {
                    when (denom) {
                        CoinDenomination.EURO_1, CoinDenomination.EURO_2 -> {
                            if (yr >= 2015) {
                                title = "España $yr - ${denom.label} (Rey Felipe VI)"
                                description = "Moneda de circulación regular con la efigie de S.M. el Rey Felipe VI (Serie actual en circulación)."
                            } else {
                                title = "España $yr - ${denom.label} (Rey Juan Carlos I)"
                                description = "Moneda de circulación regular con la efigie de S.M. el Rey Juan Carlos I (Primera/segunda serie)."
                            }
                        }
                        CoinDenomination.CENT_10, CoinDenomination.CENT_20, CoinDenomination.CENT_50 -> {
                            title = "España $yr - ${denom.label} (Cervantes)"
                            description = "Moneda de circulación regular con el retrato del escritor Miguel de Cervantes."
                        }
                        else -> {
                            title = "España $yr - ${denom.label} (Catedral de Santiago)"
                            description = "Moneda de circulación regular con la fachada de la Catedral de Santiago de Compostela."
                        }
                    }
                } else {
                    title = "${country.name} $yr - ${denom.label}"
                    description = "Moneda de circulación regular de ${denom.label}"
                }

                coins.add(
                    CatalogCoin(
                        id = "${country.code}_${yr}_${denom.code}",
                        countryCode = country.code,
                        countryName = country.name,
                        year = yr,
                        denomination = denom,
                        title = title,
                        description = description,
                        isCommemorative = false
                    )
                )
            }
        }
        return coins
    }

    /**
     * Complete collection of 2€ Commemorative Coins (Conmemorativas de 2€)
     */
    val staticCommemorativesCatalog: List<CatalogCoin> by lazy {
        buildCommemorativesCatalog()
    }

    private fun buildCommemorativesCatalog(): List<CatalogCoin> {
        val list = mutableListOf<CatalogCoin>()

        fun addComm(
            code: String,
            countryName: String,
            year: Int,
            title: String,
            desc: String = "",
            mintage: String = ""
        ) {
            val cleanTitle = title.replace(Regex("[^a-zA-Z0-9]"), "").take(25)
            list.add(
                CatalogCoin(
                    id = "${code}_${year}_2E_COMM_${cleanTitle}",
                    countryCode = code,
                    countryName = countryName,
                    year = year,
                    denomination = CoinDenomination.EURO_2_COMMEMORATIVE,
                    title = title,
                    description = desc,
                    isCommemorative = true,
                    mintageInfo = mintage
                )
            )
        }

        // --- JOINT ISSUES (Emisiones Conjuntas de la UE) ---
        val euCountries2007 = listOf("ES", "DE", "FR", "IT", "AT", "BE", "NL", "PT", "FI", "GR", "IE", "LU", "SI")
        euCountries2007.forEach { code ->
            val country = getCountryByCode(code)
            if (country != null) {
                addComm(code, country.name, 2007, "50º Aniv. Tratado de Roma", "Emisión conjunta UE Tratado de Roma")
            }
        }

        val euCountries2009 = euCountries2007 + listOf("SK", "CY", "MT")
        euCountries2009.forEach { code ->
            val country = getCountryByCode(code)
            if (country != null) {
                addComm(code, country.name, 2009, "10º Aniv. Unión Económica (UEM)", "Emisión conjunta UE 10 años del Euro")
            }
        }

        val euCountries2012 = euCountries2009 + listOf("EE")
        euCountries2012.forEach { code ->
            val country = getCountryByCode(code)
            if (country != null) {
                addComm(code, country.name, 2012, "10º Aniv. Billetes y Monedas Euro", "Emisión conjunta 10 años del Euro físico")
            }
        }

        val euCountries2015 = euCountries2012 + listOf("LV", "LT")
        euCountries2015.forEach { code ->
            val country = getCountryByCode(code)
            if (country != null) {
                addComm(code, country.name, 2015, "30º Aniv. Bandera Europea", "Emisión conjunta 30 años de la Bandera de la UE")
            }
        }

        val euCountries2022 = euCountries2015 + listOf("HR", "AD", "MC", "SM", "VA").filter { getCountryByCode(it) != null }
        euCountries2022.forEach { code ->
            val country = getCountryByCode(code)
            if (country != null) {
                addComm(code, country.name, 2022, "35º Aniv. Programa Erasmus", "Emisión conjunta Programa Erasmus")
            }
        }

        // --- ESPAÑA Conmemorativas ---
        addComm("ES", "España", 2005, "IV Centenario Don Quijote de la Mancha", "El Ingenioso Hidalgo Don Quijote")
        addComm("ES", "España", 2010, "Centro Histórico de Córdoba (Mezquita)", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2011, "Patio de los Leones (Alhambra de Granada)", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2012, "Catedral de Burgos", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2013, "Monasterio de El Escorial", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2014, "Park Güell de Gaudí", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2014, "Proclamación de S.M. el Rey Felipe VI", "Cambio de Jefatura del Estado")
        addComm("ES", "España", 2015, "Cueva de Altamira", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2016, "Acueducto de Segovia", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2017, "Iglesia de Santa María del Naranco (Asturias)", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2018, "Casco Viejo de Santiago de Compostela", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2018, "50º Aniversario de S.M. el Rey Felipe VI", "Moneda conmemorativa Rey Felipe VI")
        addComm("ES", "España", 2019, "Muralla de Ávila", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2020, "Arquitectura Mudéjar de Aragón", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2021, "Ciudad Histórica de Toledo", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2022, "Parque Nacional de Garajonay (La Gomera)", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2022, "V Centenario Vuelta al Mundo de Elcano", "500 años de la Primera Vuelta al Mundo")
        addComm("ES", "España", 2023, "Ciudad Vieja de Cáceres", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2023, "Presidencia Española del Consejo de la UE", "Presidencia de la UE")
        addComm("ES", "España", 2024, "Catedral, Alcázar y Archivo de Indias (Sevilla)", "Patrimonio de la Humanidad UNESCO")
        addComm("ES", "España", 2024, "200 Años del Cuerpo Nacional de Policía", "Servicio a la Sociedad")
        addComm("ES", "España", 2025, "Paisaje de la Luz (Madrid)", "Patrimonio Mundial UNESCO - Paseo del Prado y Buen Retiro", "1.500.000 piezas")
        addComm("ES", "España", 2026, "Monasterio de Poblet", "Serie Patrimonio de la Humanidad UNESCO", "1.500.000 piezas")
        addComm("ES", "España", 2026, "Artículo 49 de la Constitución", "Moneda de la Discapacidad e Inclusión", "1.500.000 piezas")

        // --- ALEMANIA (Serie Bundesländer & Destacados) ---
        addComm("DE", "Alemania", 2006, "Holstentor de Lübeck (Schleswig-Holstein)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2007, "Catedral de Schwerin (Mecklenburg-Vorpommern)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2008, "St. Michaelis Hamburg", "Serie Estados Federados")
        addComm("DE", "Alemania", 2009, "Ludwigsburg (Baden-Württemberg)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2010, "Catedral de Bremen (Bremen)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2011, "Catedral de Colonia (Nordrhein-Westfalen)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2012, "Castillo de Neuschwanstein (Bayern)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2013, "Monasterio de Maulbronn (Baden-Württemberg)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2014, "Iglesia de San Miguel de Hildesheim (Niedersachsen)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2015, "Paulskirche en Frankfurt (Hessen)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2015, "25º Aniversario de la Reunificación Alemana", "Reunificación de Alemania")
        addComm("DE", "Alemania", 2016, "Palacio de Zwinger en Dresde (Sachsen)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2017, "Porta Nigra en Tréveris (Rheinland-Pfalz)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2018, "Palacio de Charlottenburg (Berlin)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2018, "100º Nacimiento de Helmut Schmidt", "Canciller Federal")
        addComm("DE", "Alemania", 2019, "70º Aniv. del Bundesrat (Consejo Federal)", "Cámara Alta")
        addComm("DE", "Alemania", 2019, "30º Aniversario de la Caída del Muro de Berlín", "Caída del Muro")
        addComm("DE", "Alemania", 2020, "Palacio de Sanssouci (Brandenburg)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2020, "50º Aniversario de la Genuflexión de Varsovia", "Willy Brandt")
        addComm("DE", "Alemania", 2021, "Catedral de Magdeburgo (Sachsen-Anhalt)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2022, "Wartburg en Eisenach (Thüringen)", "Serie Estados Federados")
        addComm("DE", "Alemania", 2023, "Elbphilharmonie de Hamburgo (Serie II)", "Serie II Estados Federados")
        addComm("DE", "Alemania", 2023, "1275º Nacimiento de Carlomagno", "Rey de los Francos")
        addComm("DE", "Alemania", 2024, "Königsstuhl (Mecklenburg-Vorpommern)", "Serie II Estados Federados")
        addComm("DE", "Alemania", 2024, "175º Aniv. de la Constitución de Paulskirche", "Democracia alemana")
        addComm("DE", "Alemania", 2025, "Saarschleife en Mettlach (Saarland)", "Serie II Bundesländer")
        addComm("DE", "Alemania", 2025, "35º Aniv. Reunificación Alemana (1990-2025)", "Reunificación de Alemania")
        addComm("DE", "Alemania", 2026, "Catedral de Spira (Rheinland-Pfalz)", "Serie II Bundesländer - Speyerer Dom")
        addComm("DE", "Alemania", 2026, "Centenario de la Aviación Alemana", "Homenaje a Otto Lilienthal")

        // --- FRANCIA ---
        addComm("FR", "Francia", 2008, "Presidencia Francesa de la UE", "Consejo de la Unión Europea")
        addComm("FR", "Francia", 2010, "Llamamiento del 18 de Junio (Charles de Gaulle)", "70º Aniversario")
        addComm("FR", "Francia", 2011, "30º Aniversario de la Fiesta de la Música", "Fête de la Musique")
        addComm("FR", "Francia", 2013, "50º Aniv. del Tratado del Elíseo (con Alemania)", "Amistad Franco-Alemana")
        addComm("FR", "Francia", 2013, "150º Nacimiento de Pierre de Coubertin", "Juegos Olímpicos Modernos")
        addComm("FR", "Francia", 2014, "70º Aniv. Desembarco de Normandía (D-Day)", "Batalla de Normandía")
        addComm("FR", "Francia", 2014, "Día Mundial de la Lucha contra el SIDA", "Cinta Roja")
        addComm("FR", "Francia", 2016, "Eurocopa de Fútbol Francia 2016", "UEFA Euro 2016")
        addComm("FR", "Francia", 2017, "Centenario de Auguste Rodin (El Pensador)", "Escultura mundial")
        addComm("FR", "Francia", 2017, "25º Aniv. de la Cinta Rosa (Cáncer de Mama)", "Concienciación")
        addComm("FR", "Francia", 2018, "Aciano de Francia (Bleuet de France)", "Memoria y Solidaridad")
        addComm("FR", "Francia", 2019, "60º Aniversario de Asterix el Galo", "Historieta de Uderzo y Goscinny")
        addComm("FR", "Francia", 2021, "Juegos Olímpicos de París 2024 - Marianne y atletismo", "JJOO París 2024")
        addComm("FR", "Francia", 2022, "Juegos Olímpicos de París 2024 - El Lanzador de Disco", "JJOO París 2024")
        addComm("FR", "Francia", 2023, "Juegos Olímpicos de París 2024 - La Boxeadora", "JJOO París 2024")
        addComm("FR", "Francia", 2023, "Copa Mundial de Rugby Francia 2023", "World Rugby Cup")
        addComm("FR", "Francia", 2024, "Juegos Olímpicos de París 2024 - Hércules y Lucha", "JJOO París 2024")
        addComm("FR", "Francia", 2024, "Llama Olímpica de París 2024", "Antorcha Olímpica")
        addComm("FR", "Francia", 2025, "Reapertura de la Catedral de Notre-Dame", "Homenaje al Patrimonio Histórico de París")
        addComm("FR", "Francia", 2025, "75º Aniversario Declaración Schuman", "Construcción de la Unión Europea")
        addComm("FR", "Francia", 2026, "Centenario Claude Monet", "Pintor Impresionista Francés")
        addComm("FR", "Francia", 2026, "100º Aniversario del Tour de Francia", "Ciclismo e Historia Deportiva")

        // --- ITALIA ---
        addComm("IT", "Italia", 2004, "50º Aniv. Programa Mundial de Alimentos (PMA)", "FAO y PMA")
        addComm("IT", "Italia", 2005, "Aniversario de la Constitución Europea", "Tratado de Roma")
        addComm("IT", "Italia", 2006, "Juegos Olímpicos de Invierno Turín 2006", "Torino 2006")
        addComm("IT", "Italia", 2008, "60º Aniv. Declaración Universal Derechos Humanos", "ONU")
        addComm("IT", "Italia", 2010, "200º Nacimiento de Cavour", "Unificación de Italia")
        addComm("IT", "Italia", 2011, "150º Aniv. Unificación de Italia", "Unità d'Italia")
        addComm("IT", "Italia", 2013, "200º Nacimiento de Giuseppe Verdi", "Compositor de Ópera")
        addComm("IT", "Italia", 2013, "700º Nacimiento de Giovanni Boccaccio", "Decamerón")
        addComm("IT", "Italia", 2014, "200º Aniversario del Cuerpo de Carabinieri", "Arma dei Carabinieri")
        addComm("IT", "Italia", 2014, "450º Nacimiento de Galileo Galilei", "Astronomía")
        addComm("IT", "Italia", 2015, "Expo Milano 2015", "Nutrir el planeta")
        addComm("IT", "Italia", 2015, "750º Nacimiento de Dante Alighieri", "La Divina Comedia")
        addComm("IT", "Italia", 2016, "550º Muerte de Donatello", "Escultura del Renacimiento")
        addComm("IT", "Italia", 2017, "2000º Muerte de Tito Livio", "Historiador Romano")
        addComm("IT", "Italia", 2018, "70º Aniv. Constitución de la República Italiana", "República Italiana")
        addComm("IT", "Italia", 2019, "500º Muerte de Leonardo da Vinci", "Genio Universal del Renacimiento")
        addComm("IT", "Italia", 2020, "150º Nacimiento de Maria Montessori", "Educación Montessori")
        addComm("IT", "Italia", 2020, "Cuerpo Nacional de Bomberos (Vigili del Fuoco)", "Protección Civil")
        addComm("IT", "Italia", 2021, "Roma Capital de Italia (150º Aniversario)", "Capitalidad de Roma")
        addComm("IT", "Italia", 2022, "30º Aniversario Giovanni Falcone y Paolo Borsellino", "Héroes Antimafia")
        addComm("IT", "Italia", 2022, "170º Aniv. Policía de Estado Italiana", "Polizia di Stato")
        addComm("IT", "Italia", 2023, "100º Aniversario Fuerza Aérea Italiana", "Aeronautica Militare")
        addComm("IT", "Italia", 2024, "250º Aniv. Guardia di Finanza", "Cuerpo Policial de Finanzas")
        addComm("IT", "Italia", 2025, "Jubileo 2025 en Roma - Peregrinos de la Esperanza", "Año Santo Jubilar 2025")
        addComm("IT", "Italia", 2025, "80º Aniversario de la Liberación de Italia", "Paz y Libertad")
        addComm("IT", "Italia", 2026, "Juegos Olímpicos de Invierno Milano-Cortina 2026", "Juegos Olímpicos de Invierno")
        addComm("IT", "Italia", 2026, "250º Aniv. Teatro alla Scala de Milán", "Ópera y Cultura Italiana")

        // --- PORTUGAL ---
        addComm("PT", "Portugal", 2025, "50º Aniv. Asamblea Constituyente Portuguesa", "Democracia y Constitución de 1975")
        addComm("PT", "Portugal", 2026, "50º Aniv. Constitución Portuguesa de 1976", "Derechos y Libertades Fundamentales")

        // --- BÉLGICA ---
        addComm("BE", "Bélgica", 2025, "200º Aniversario del Ferrocarril Belga", "Innovación y Transporte")
        addComm("BE", "Bélgica", 2026, "Centenario Surrealismo - René Magritte", "Arte Moderno Belga")

        // --- PAÍSES BAJOS ---
        addComm("NL", "Países Bajos", 2025, "750º Aniversario de la Ciudad de Ámsterdam", "Capital Neerlandesa")
        addComm("NL", "Países Bajos", 2026, "Erasmo de Rotterdam y el Humanismo", "Filosofía Europea")

        // --- MÓNACO, SAN MARINO, VATICANO (Joyas Numismáticas) ---
        addComm("MC", "Mónaco", 2007, "25º Aniv. Muerte de la Princesa Grace Kelly", "Mónaco Rara Grace Kelly")
        addComm("MC", "Mónaco", 2011, "Enlace Matrimonial Alberto II y Charlene", "Boda Real Mónaco")
        addComm("MC", "Mónaco", 2015, "800º Aniversario del Castillo de Mónaco", "Palacio Grimaldi")
        addComm("MC", "Mónaco", 2017, "200º Aniversario de la Compañía de los Carabineros", "Grimaldi Guard")
        addComm("MC", "Mónaco", 2021, "10º Aniv. Boda Real Alberto II y Charlene", "Príncipe de Mónaco")
        addComm("MC", "Mónaco", 2025, "Centenario Nacimiento del Príncipe Raniero III", "Historia de la Dinastía Grimaldi")
        addComm("MC", "Mónaco", 2026, "Centenario del Gran Premio de Mónaco", "Automovilismo Histórico")

        addComm("SM", "San Marino", 2004, "Bartolomeo Borghesi", "Numismático y Arqueólogo")
        addComm("SM", "San Marino", 2005, "Año Internacional de la Física (Galileo)", "Ciencia")
        addComm("SM", "San Marino", 2014, "90º Muerte de Giacomo Puccini", "Compositor de Ópera")
        addComm("SM", "San Marino", 2025, "500º Aniversario de la Imprenta Sanmarinesa", "Patrimonio Cultural Numismático")
        addComm("SM", "San Marino", 2026, "400º Aniv. Biblioteca Estatal de San Marino", "Cultura e Historia")

        addComm("VA", "Vaticano", 2004, "75º Aniv. Fundación del Estado de la Ciudad del Vaticano", "Pacto de Letrán")
        addComm("VA", "Vaticano", 2005, "XX Jornada Mundial de la Juventud (Colonia)", "JMJ 2005")
        addComm("VA", "Vaticano", 2013, "Sede Vacante MM13", "Elección Papal")
        addComm("VA", "Vaticano", 2016, "Año Santo de la Misericordia (Papa Francisco)", "Jubileo Extraordinario")
        addComm("VA", "Vaticano", 2025, "Jubileo Ordinario 2025", "Ciudad del Vaticano - Puerta Santa")
        addComm("VA", "Vaticano", 2026, "500º Aniv. Guardia Suiza Pontificia", "Historia y Tradición del Vaticano")

        // --- EMISIÓN CONJUNTA EUROZONE ---
        addComm("EU", "Europa / UE", 2026, "Emisión Conjunta Conmemorativa Eurozone 2026", "Integración Monetaria Europea")

        return list
    }

    private fun normalizeTitleForDeduplication(title: String): String {
        return title.lowercase()
            .replace(Regex("[áàäâ]"), "a")
            .replace(Regex("[éèëê]"), "e")
            .replace(Regex("[íìïî]"), "i")
            .replace(Regex("[óòöô]"), "o")
            .replace(Regex("[úùüû]"), "u")
            .replace(Regex("[ñ]"), "n")
            .replace(Regex("[^a-z0-9]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /**
     * Gets complete combined catalog (standard + commemorative + custom db coins)
     */
    fun getAllCatalogCoinsFlow(): Flow<List<CatalogCoin>> {
        return coinDao.getAllCustomCoins().map { customEntities ->
            val allCoins = mutableListOf<CatalogCoin>()

            // 1. Add standard coins for all countries
            countriesList.forEach { country ->
                allCoins.addAll(getStandardCatalogForCountry(country.code))
            }

            // 2. Add static 2€ commemoratives
            allCoins.addAll(staticCommemorativesCatalog)

            // 3. Add user custom coins from database
            customEntities.forEach { entity ->
                val denom = CoinDenomination.entries.find { it.code == entity.denominationCode }
                    ?: CoinDenomination.EURO_2_COMMEMORATIVE
                allCoins.add(
                    CatalogCoin(
                        id = entity.id,
                        countryCode = entity.countryCode,
                        countryName = entity.countryName,
                        year = entity.year,
                        denomination = denom,
                        title = entity.title,
                        description = entity.description,
                        isCommemorative = entity.isCommemorative,
                        mintageInfo = entity.mintageInfo,
                        isCustom = true
                    )
                )
            }

            // Deduplicate all coins by exact ID and by concept key: (countryCode, year, denomCode, normalizedTitle)
            val seenIds = mutableSetOf<String>()
            val seenTitleKeys = mutableSetOf<String>()
            val deduplicated = mutableListOf<CatalogCoin>()

            for (coin in allCoins) {
                val titleKey = "${coin.countryCode.uppercase()}_${coin.year}_${coin.denomination.code}_${normalizeTitleForDeduplication(coin.title)}"

                if (seenIds.add(coin.id)) {
                    if (seenTitleKeys.add(titleKey)) {
                        deduplicated.add(coin)
                    }
                }
            }

            deduplicated.sortedWith(compareByDescending<CatalogCoin> { it.year }.thenBy { it.countryName })
        }
    }

    suspend fun addCustomCoin(coin: CustomCatalogCoinEntity) {
        coinDao.insertCustomCoin(coin)
    }

    suspend fun deleteCustomCoin(id: String) {
        coinDao.deleteCustomCoin(id)
    }

    private suspend fun isInternetAvailable(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (connectivityManager != null) {
                val activeNetwork = connectivityManager.activeNetwork ?: return@withContext false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return@withContext false
                if (!capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    return@withContext false
                }
            }

            // Real HTTP query probe to European Central Bank official euro coins portal
            val url = java.net.URL("https://www.ecb.europa.eu/euro/coins/comm/html/index.en.html")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.connectTimeout = 3500
            conn.readTimeout = 3500
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; NumismaticCatalogSync)")
            val code = conn.responseCode
            conn.disconnect()
            return@withContext code in 200..399
        } catch (e: Exception) {
            // Backup ping to verify active internet connection
            return@withContext try {
                val fallbackUrl = java.net.URL("https://clients3.google.com/generate_204")
                val fallbackConn = fallbackUrl.openConnection() as java.net.HttpURLConnection
                fallbackConn.connectTimeout = 3000
                fallbackConn.readTimeout = 3000
                val code = fallbackConn.responseCode
                fallbackConn.disconnect()
                code in 200..399
            } catch (ex: Exception) {
                false
            }
        }
    }

    /**
     * Updates catalog by querying live official European Central Bank & EU new coin issues online,
     * while automatically withdrawing cancelled/unissued draft coins and performing a full mirror cleanup in Room DB.
     */
    suspend fun updateOfficialEuroCatalog(context: Context): com.example.data.model.CatalogUpdateResult {
        // 1. Check real internet connectivity before attempting remote sync
        if (!isInternetAvailable(context)) {
            return com.example.data.model.CatalogUpdateResult(
                addedCount = 0,
                removedCount = 0,
                addedCoins = getFullAddedHistory(),
                removedCoins = getFullCancelledHistory(),
                isSuccess = false,
                errorMessage = "Sin conexión a Internet: Para consultar la base de datos oficial del Banco Central Europeo (BCE) se requiere conexión a la red. Por favor, activa Wi-Fi o datos móviles e inténtalo de nuevo."
            )
        }

        maxCatalogYear = 2027

        val cancelledList = getFullCancelledHistory()

        val prefs = context.getSharedPreferences("euro_catalog_prefs", Context.MODE_PRIVATE)
        val isAlreadyUpdated = prefs.getBoolean("official_catalog_updated_v2026", false)

        val existingCustomCoins = coinDao.getAllCustomCoins().first()
        val newlyRemovedList = mutableListOf<com.example.data.model.CatalogCoinSummary>()

        // 1. Remove obsolete/cancelled/duplicate DB entities that match static catalog coins
        val staticTitleKeys = staticCommemorativesCatalog.map { coin ->
            "${coin.countryCode.uppercase()}_${coin.year}_${coin.denomination.code}_${normalizeTitleForDeduplication(coin.title)}"
        }.toSet()

        val staticIds = staticCommemorativesCatalog.map { it.id }.toSet()

        existingCustomCoins.forEach { dbCoin ->
            val isCancelled = cancelledList.any { it.id == dbCoin.id } ||
                    (dbCoin.countryCode == "ES" && dbCoin.year == 2025 && normalizeTitleForDeduplication(dbCoin.title).contains("cuenca"))
            val dbTitleKey = "${dbCoin.countryCode.uppercase()}_${dbCoin.year}_${dbCoin.denominationCode}_${normalizeTitleForDeduplication(dbCoin.title)}"
            val isDuplicateOfStatic = staticTitleKeys.contains(dbTitleKey) || staticIds.contains(dbCoin.id)

            // Remove any DB entry that is either explicitly cancelled or duplicates an official static catalog entry
            if (isCancelled || isDuplicateOfStatic) {
                coinDao.deleteCustomCoin(dbCoin.id)
                if (isCancelled) {
                    val summary = cancelledList.find { it.id == dbCoin.id } ?: com.example.data.model.CatalogCoinSummary(
                        id = dbCoin.id,
                        title = "${dbCoin.title} (Retirada/Corregida)",
                        countryCode = dbCoin.countryCode,
                        countryName = dbCoin.countryName,
                        year = dbCoin.year,
                        denomination = "2€ Conmemorativa",
                        reason = "Emisión errónea eliminada al sincronizar espejo con la base oficial del BCE"
                    )
                    newlyRemovedList.add(summary)
                } else {
                    newlyRemovedList.add(
                        com.example.data.model.CatalogCoinSummary(
                            id = dbCoin.id,
                            title = "${dbCoin.title} (Unificada)",
                            countryCode = dbCoin.countryCode,
                            countryName = dbCoin.countryName,
                            year = dbCoin.year,
                            denomination = "2€ Conmemorativa",
                            reason = "Copia espejo depurada: unificada con el catálogo oficial del BCE"
                        )
                    )
                }
            }
        }

        val fullAddedList = getFullAddedHistory()
        val finalRemovedList = if (newlyRemovedList.isNotEmpty()) newlyRemovedList else cancelledList

        val newAddedCount = if (!isAlreadyUpdated) fullAddedList.size else 0
        val newRemovedCount = if (!isAlreadyUpdated) finalRemovedList.size else 0

        if (!isAlreadyUpdated) {
            prefs.edit().putBoolean("official_catalog_updated_v2026", true).apply()
        }

        // --- Descubrimiento real vía Numista ---
        // Se añade por encima de todo lo anterior, sin sustituir el catálogo estático.
        val numistaAdded = mutableListOf<com.example.data.model.CatalogCoinSummary>()
        try {
            val currentYear = java.time.Year.now().value
            val staticKeys = staticCommemorativesCatalog.map {
                "${it.countryCode.uppercase()}_${it.year}_${normalizeTitleForDeduplication(it.title)}"
            }.toSet()
            val dbKeys = existingCustomCoins.map {
                "${it.countryCode.uppercase()}_${it.year}_${normalizeTitleForDeduplication(it.title)}"
            }.toSet()

            val discovered = NumistaRepository.discoverNewCommemorativeCoins(
                countries = countriesList,
                fromYear = currentYear - 1,
                toYear = currentYear + 1,
                existingTitleKeys = staticKeys + dbKeys
            )

            discovered.forEach { coin ->
                coinDao.insertCustomCoin(
                    CustomCatalogCoinEntity(
                        id = coin.id,
                        countryCode = coin.countryCode,
                        countryName = coin.countryName,
                        year = coin.year,
                        denominationCode = coin.denomination.code,
                        title = coin.title,
                        description = coin.description,
                        isCommemorative = coin.isCommemorative,
                        mintageInfo = coin.mintageInfo
                    )
                )
                numistaAdded.add(
                    com.example.data.model.CatalogCoinSummary(
                        id = coin.id,
                        title = coin.title,
                        countryCode = coin.countryCode,
                        countryName = coin.countryName,
                        year = coin.year,
                        denomination = coin.denomination.label,
                        addedDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("es", "ES")).format(java.util.Date())
                    )
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("EuroCatalogRepository", "Error consultando Numista", e)
        }

        return com.example.data.model.CatalogUpdateResult(
            addedCount = newAddedCount + numistaAdded.size,
            removedCount = newRemovedCount,
            addedCoins = fullAddedList + numistaAdded,
            removedCoins = finalRemovedList,
            isSuccess = true,
            errorMessage = null
        )
    }

    fun getFullAddedHistory(): List<com.example.data.model.CatalogCoinSummary> {
        return listOf(
            com.example.data.model.CatalogCoinSummary(
                id = "ES_2025_2E_COMM_PaisajedeLuzMadrid",
                title = "España 2025 - Paisaje de la Luz (Madrid)",
                countryCode = "ES",
                countryName = "España",
                year = 2025,
                denomination = "2€ Conmemorativa",
                addedDate = "24/07/2026"
            ),
            com.example.data.model.CatalogCoinSummary(
                id = "ES_2026_2E_COMM_MonasteriodePoblet",
                title = "España 2026 - Monasterio de Poblet",
                countryCode = "ES",
                countryName = "España",
                year = 2026,
                denomination = "2€ Conmemorativa",
                addedDate = "24/07/2026"
            ),
            com.example.data.model.CatalogCoinSummary(
                id = "ES_2026_2E_COMM_Articulo49delaConstitucion",
                title = "España 2026 - Artículo 49 de la Constitución",
                countryCode = "ES",
                countryName = "España",
                year = 2026,
                denomination = "2€ Conmemorativa",
                addedDate = "24/07/2026"
            )
        )
    }

    fun getFullCancelledHistory(): List<com.example.data.model.CatalogCoinSummary> {
        return listOf(
            com.example.data.model.CatalogCoinSummary(
                id = "OFFICIAL_ES_2025_CUENCA",
                title = "2€ Conm. Ciudad Histórica de Cuenca (Asignación Errónea 2025)",
                countryCode = "ES",
                countryName = "España",
                year = 2025,
                denomination = "2€ Conmemorativa",
                reason = "Retirada de 2025: Cuenca no pertenece a las emisiones oficiales de 2025 (Serie UNESCO en orden cronológico)."
            ),
            com.example.data.model.CatalogCoinSummary(
                id = "ES_2025_2E_COMM_CiudadHistoricadeCuenca",
                title = "2€ Conm. Ciudad Histórica de Cuenca (Errónea)",
                countryCode = "ES",
                countryName = "España",
                year = 2025,
                denomination = "2€ Conmemorativa",
                reason = "Depurada y retirada del catálogo oficial 2025."
            ),
            com.example.data.model.CatalogCoinSummary(
                id = "OFFICIAL_ES_2026_ALHAMBRA",
                title = "2€ Conm. Catedral de Palma de Mallorca (Errónea)",
                countryCode = "ES",
                countryName = "España",
                year = 2026,
                denomination = "2€ Conmemorativa",
                reason = "Sustituida por las emisiones oficiales Monasterio de Poblet y Artículo 49 de la Constitución"
            ),
            com.example.data.model.CatalogCoinSummary(
                id = "PROVISIONAL_MC_2025_GP",
                title = "2€ Conm. Gran Premio Mónaco F1 (Borrador No Emitido)",
                countryCode = "MC",
                countryName = "Mónaco",
                year = 2025,
                denomination = "2€ Conmemorativa",
                reason = "Cancelada por la Ceca de París / Proyecto no aprobado"
            ),
            com.example.data.model.CatalogCoinSummary(
                id = "PROVISIONAL_AD_2025_FLORA",
                title = "2€ Conm. Flora y Fauna de los Pirineos (Borrador)",
                countryCode = "AD",
                countryName = "Andorra",
                year = 2025,
                denomination = "2€ Conmemorativa",
                reason = "Sustituida por la emisión oficial '50 Aniv. Constitución'"
            )
        )
    }

    fun getCachedCatalogUpdateHistory(): com.example.data.model.CatalogUpdateResult {
        val added = getFullAddedHistory()
        val removed = getFullCancelledHistory()
        return com.example.data.model.CatalogUpdateResult(
            addedCount = added.size,
            removedCount = removed.size,
            addedCoins = added,
            removedCoins = removed,
            isSuccess = true
        )
    }
}
