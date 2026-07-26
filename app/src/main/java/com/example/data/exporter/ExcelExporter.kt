package com.example.data.exporter

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.CoinItemUiState
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExcelExporter {

    /**
     * Exports Room coin database items into a native OpenXML Microsoft Excel file (.xlsx).
     */
    fun exportCollectionToXlsx(
        context: Context,
        items: List<CoinItemUiState>,
        labelTitle: String? = null
    ): Uri? {
        try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val sanitizedLabel = if (!labelTitle.isNullOrBlank()) {
                labelTitle.lowercase().replace(" ", "_").replace("[^a-z0-9_]".toRegex(), "")
            } else {
                "coleccion_completa"
            }
            val fileName = "mis_euros_${sanitizedLabel}.xlsx"
            val file = File(exportDir, fileName)

            FileOutputStream(file).use { fos ->
                ZipOutputStream(fos).use { zip ->
                    // 1. [Content_Types].xml
                    zip.putNextEntry(ZipEntry("[Content_Types].xml"))
                    zip.write(
                        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>""".trimIndent().toByteArray(StandardCharsets.UTF_8)
                    )
                    zip.closeEntry()

                    // 2. _rels/.rels
                    zip.putNextEntry(ZipEntry("_rels/.rels"))
                    zip.write(
                        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>""".trimIndent().toByteArray(StandardCharsets.UTF_8)
                    )
                    zip.closeEntry()

                    // 3. xl/_rels/workbook.xml.rels
                    zip.putNextEntry(ZipEntry("xl/_rels/workbook.xml.rels"))
                    zip.write(
                        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>""".trimIndent().toByteArray(StandardCharsets.UTF_8)
                    )
                    zip.closeEntry()

                    // 4. xl/workbook.xml
                    zip.putNextEntry(ZipEntry("xl/workbook.xml"))
                    zip.write(
                        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Colección de Monedas" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>""".trimIndent().toByteArray(StandardCharsets.UTF_8)
                    )
                    zip.closeEntry()

                    // 5. xl/styles.xml
                    zip.putNextEntry(ZipEntry("xl/styles.xml"))
                    zip.write(
                        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="2">
    <font><sz val="11"/><color theme="1"/><name val="Calibri"/><family val="2"/></font>
    <font><b/><sz val="11"/><color rgb="FFFFFFFF"/><name val="Calibri"/><family val="2"/></font>
  </fonts>
  <fills count="3">
    <fill><patternFill fillType="none"/></fill>
    <fill><patternFill fillType="gray125"/></fill>
    <fill><patternFill fillType="solid"><fgColor rgb="FF1A365D"/><bgColor indexed="64"/></patternFill></fill>
  </fills>
  <borders count="1">
    <border><left/><right/><top/><bottom/></border>
  </borders>
  <cellStyleXfs count="1">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
  </cellStyleXfs>
  <cellXfs count="2">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="2" borderId="0" xfId="0" applyFont="1" applyFill="1"/>
  </cellXfs>
</styleSheet>""".trimIndent().toByteArray(StandardCharsets.UTF_8)
                    )
                    zip.closeEntry()

                    // 6. xl/worksheets/sheet1.xml
                    zip.putNextEntry(ZipEntry("xl/worksheets/sheet1.xml"))
                    val sheetBuilder = StringBuilder()
                    sheetBuilder.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <cols>
    <col min="1" max="1" width="18" customWidth="1"/>
    <col min="2" max="2" width="10" customWidth="1"/>
    <col min="3" max="3" width="10" customWidth="1"/>
    <col min="4" max="4" width="18" customWidth="1"/>
    <col min="5" max="5" width="20" customWidth="1"/>
    <col min="6" max="6" width="45" customWidth="1"/>
    <col min="7" max="7" width="18" customWidth="1"/>
    <col min="8" max="8" width="12" customWidth="1"/>
    <col min="9" max="9" width="25" customWidth="1"/>
    <col min="10" max="10" width="16" customWidth="1"/>
    <col min="11" max="11" width="30" customWidth="1"/>
  </cols>
  <sheetData>
""")

                    // Header Row (row 1, style 1)
                    val headers = listOf(
                        "País", "Código", "Año", "Denominación", "Tipo",
                        "Título / Motivo", "Estado en Colección", "Cantidad",
                        "Estado de Conservación", "Valor Facial (€)", "Notas"
                    )
                    sheetBuilder.append("    <row r=\"1\" ht=\"24\" customHeight=\"1\">\n")
                    headers.forEachIndexed { colIdx, header ->
                        val colLetter = getColLetter(colIdx)
                        sheetBuilder.append("      <c r=\"${colLetter}1\" t=\"inlineStr\" s=\"1\"><is><t>${xmlEscape(header)}</t></is></c>\n")
                    }
                    sheetBuilder.append("    </row>\n")

                    // Data Rows
                    items.forEachIndexed { index, item ->
                        val rowIndex = index + 2
                        val coin = item.catalogCoin
                        val typeStr = if (coin.isCommemorative) "2€ Conmemorativa" else "Moneda Regular"
                        val statusStr = item.status.label
                        val gradeStr = item.grade.label

                        sheetBuilder.append("    <row r=\"$rowIndex\">\n")
                        sheetBuilder.append("      <c r=\"A$rowIndex\" t=\"inlineStr\"><is><t>${xmlEscape(coin.countryName)}</t></is></c>\n")
                        sheetBuilder.append("      <c r=\"B$rowIndex\" t=\"inlineStr\"><is><t>${xmlEscape(coin.countryCode)}</t></is></c>\n")
                        sheetBuilder.append("      <c r=\"C$rowIndex\"><v>${coin.year}</v></c>\n")
                        sheetBuilder.append("      <c r=\"D$rowIndex\" t=\"inlineStr\"><is><t>${xmlEscape(coin.denomination.label)}</t></is></c>\n")
                        sheetBuilder.append("      <c r=\"E$rowIndex\" t=\"inlineStr\"><is><t>${xmlEscape(typeStr)}</t></is></c>\n")
                        sheetBuilder.append("      <c r=\"F$rowIndex\" t=\"inlineStr\"><is><t>${xmlEscape(coin.title)}</t></is></c>\n")
                        sheetBuilder.append("      <c r=\"G$rowIndex\" t=\"inlineStr\"><is><t>${xmlEscape(statusStr)}</t></is></c>\n")
                        sheetBuilder.append("      <c r=\"H$rowIndex\"><v>${item.quantity}</v></c>\n")
                        sheetBuilder.append("      <c r=\"I$rowIndex\" t=\"inlineStr\"><is><t>${xmlEscape(gradeStr)}</t></is></c>\n")
                        sheetBuilder.append("      <c r=\"J$rowIndex\"><v>${coin.denomination.faceValue}</v></c>\n")
                        sheetBuilder.append("      <c r=\"K$rowIndex\" t=\"inlineStr\"><is><t>${xmlEscape(item.notes)}</t></is></c>\n")
                        sheetBuilder.append("    </row>\n")
                    }

                    sheetBuilder.append("""  </sheetData>
</worksheet>""")

                    zip.write(sheetBuilder.toString().toByteArray(StandardCharsets.UTF_8))
                    zip.closeEntry()
                }
            }

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportCollectionToCsv(
        context: Context,
        items: List<CoinItemUiState>,
        countryFilterName: String? = null
    ): Uri? {
        try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val fileName = if (countryFilterName != null && countryFilterName.isNotBlank()) {
                "mis_euros_${countryFilterName.lowercase().replace(" ", "_")}.csv"
            } else {
                "mis_euros_coleccion.csv"
            }

            val file = File(exportDir, fileName)
            val outputStream = FileOutputStream(file)
            
            // Write UTF-8 BOM so Microsoft Excel auto-detects UTF-8 Spanish characters (€, ñ, á, é...)
            outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

            val writer = OutputStreamWriter(outputStream, StandardCharsets.UTF_8)

            // Header row (using semicolon as delimiter for European Excel compatibility)
            writer.append("País;Código;Año;Denominación;Tipo;Título;Estado;Cantidad;Conservación;Valor Facial (€);Notas\n")

            items.forEach { item ->
                val coin = item.catalogCoin
                val typeStr = if (coin.isCommemorative) "2€ Conmemorativa" else "Moneda Regular"
                val statusStr = item.status.label
                val qtyStr = item.quantity.toString()
                val gradeStr = item.grade.label
                val valueStr = String.format("%.2f", coin.denomination.faceValue).replace(".", ",")
                val cleanTitle = coin.title.replace(";", ",")
                val cleanNotes = item.notes.replace(";", ",").replace("\n", " ")

                writer.append(
                    "${coin.countryName};" +
                            "${coin.countryCode};" +
                            "${coin.year};" +
                            "${coin.denomination.label};" +
                            "$typeStr;" +
                            "\"$cleanTitle\";" +
                            "$statusStr;" +
                            "$qtyStr;" +
                            "$gradeStr;" +
                            "$valueStr;" +
                            "\"$cleanNotes\"\n"
                )
            }

            writer.flush()
            writer.close()

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun shareExportedFile(context: Context, fileUri: Uri, isXlsx: Boolean = true) {
        val mimeType = if (isXlsx) "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" else "text/csv"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_SUBJECT, "Mi colección de euros - mis €uros (Excel .xlsx)")
            putExtra(Intent.EXTRA_TEXT, "Adjunto la base de datos de mi colección personal de monedas de euro en formato Excel (.xlsx).")
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Descargar / Guardar archivo Excel (.xlsx)")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun xmlEscape(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun getColLetter(colIdx: Int): String {
        var temp = colIdx
        var colName = ""
        while (temp >= 0) {
            colName = ('A' + (temp % 26)).toString() + colName
            temp = temp / 26 - 1
        }
        return colName
    }
}

