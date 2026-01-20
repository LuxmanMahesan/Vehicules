package com.ges.vehiclegate.util.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.ges.vehiclegate.domain.model.AgentManager
import com.ges.vehiclegate.domain.model.ShiftInfo
import com.ges.vehiclegate.domain.model.VehicleEntry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PdfReportGenerator {

    private val dateFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val fileDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    private val pageWidth = 595
    private val pageHeight = 842
    private val marginLeft = 35f
    private val marginRight = 35f
    private val marginTop = 45f
    private val marginBottom = 45f
    private val contentWidth = pageWidth - marginLeft - marginRight
    private val columnWidth = (contentWidth - 20f) / 2f
    private val columnGap = 20f

    fun generer(
        context: Context,
        vehicules: List<VehicleEntry>,
        shiftInfo: ShiftInfo
    ): File {
        val sortedVehicles = vehicules.sortedBy { it.arrivalAt }

        val pdf = PdfDocument()
        var pageNumber = 1
        var currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = currentPage.canvas
        var y = marginTop

        val titrePaint = Paint().apply {
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }

        val headerCenterPaint = Paint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val labelPaint = Paint().apply {
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }

        val valuePaint = Paint().apply {
            textSize = 12f
            color = Color.BLACK
            isAntiAlias = true
        }

        val redPaint = Paint().apply {
            textSize = 12f
            color = Color.RED
            isAntiAlias = true
        }

        val redLabelPaint = Paint().apply {
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.RED
            isAntiAlias = true
        }

        val redCenterPaint = Paint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.RED
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val separatorPaint = Paint().apply {
            strokeWidth = 1f
            color = Color.LTGRAY
            isAntiAlias = true
        }

        // Titre du rapport
        val reportDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
            Date(shiftInfo.reportDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
        )
        val headerText = "Rapport du $reportDateStr - ${AgentManager.SITE_NAME} - ${shiftInfo.agentName} - ${shiftInfo.label}"
        val headerLines = wrapText(headerText, titrePaint, contentWidth)
        headerLines.forEach { line ->
            canvas.drawText(line, marginLeft, y, titrePaint)
            y += 20f
        }
        y += 10f

        // Ligne sous le titre
        canvas.drawLine(marginLeft, y, pageWidth - marginRight, y, separatorPaint)
        y += 20f

        sortedVehicles.forEachIndexed { index, v ->
            val vehicleNumber = index + 1
            val isOnSite = v.exitAt == null
            val currentLabelPaint = if (isOnSite) redLabelPaint else labelPaint
            val currentValuePaint = if (isOnSite) redPaint else valuePaint

            // Estimer la hauteur nécessaire pour ce véhicule
            val estimatedHeight = 160f + if (v.notes.isNullOrBlank()) 0f else 50f
            if (y > pageHeight - marginBottom - estimatedHeight) {
                pdf.finishPage(currentPage)
                pageNumber++
                currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = currentPage.canvas
                y = marginTop
            }

            // Trait au-dessus de chaque véhicule (y compris le premier)
            canvas.drawLine(marginLeft, y, pageWidth - marginRight, y, separatorPaint)
            y += 18f

            // Numérotation centrée
            val centerX = pageWidth / 2f
            val vehicleTitle = "Véhicule n°$vehicleNumber"
            if (isOnSite) {
                canvas.drawText(vehicleTitle, centerX, y, redCenterPaint)
                val titleWidth = headerCenterPaint.measureText(vehicleTitle)
                canvas.drawText(" (ENCORE SUR SITE)", centerX + titleWidth / 2 + 5f, y, redPaint)
            } else {
                canvas.drawText(vehicleTitle, centerX, y, headerCenterPaint)
            }
            y += 20f

            val col1X = marginLeft
            val col2X = marginLeft + columnWidth + columnGap
            val lineHeight = 16f
            var currentY = y

            // Colonne 1
            val plateLines = wrapText("PLAQUE : ${v.plate}", currentLabelPaint, columnWidth)
            plateLines.forEach { line ->
                canvas.drawText(line, col1X, currentY, currentLabelPaint)
                currentY += lineHeight
            }

            val companyLines = wrapText("SOCIÉTÉ : ${v.companyName}", currentLabelPaint, columnWidth)
            companyLines.forEach { line ->
                canvas.drawText(line, col1X, currentY, currentLabelPaint)
                currentY += lineHeight
            }

            val driverLines = wrapText("CHAUFFEUR : ${v.driverName}", currentLabelPaint, columnWidth)
            driverLines.forEach { line ->
                canvas.drawText(line, col1X, currentY, currentLabelPaint)
                currentY += lineHeight
            }

            if (!v.driverPhone.isNullOrBlank()) {
                val phoneLines = wrapText("TEL : ${v.driverPhone}", currentLabelPaint, columnWidth)
                phoneLines.forEach { line ->
                    canvas.drawText(line, col1X, currentY, currentLabelPaint)
                    currentY += lineHeight
                }
            }

            val destLines = wrapText("DESTINATION : ${v.destination.label}", currentLabelPaint, columnWidth)
            destLines.forEach { line ->
                canvas.drawText(line, col1X, currentY, currentLabelPaint)
                currentY += lineHeight
            }

            // Colonne 2
            var col2Y = y
            val contactLines = wrapText("CONTACT SITE : ${v.siteContact}", currentLabelPaint, columnWidth)
            contactLines.forEach { line ->
                canvas.drawText(line, col2X, col2Y, currentLabelPaint)
                col2Y += lineHeight
            }

            val entryLines = wrapText("ENTRÉE : ${v.entryGate.label}", currentLabelPaint, columnWidth)
            entryLines.forEach { line ->
                canvas.drawText(line, col2X, col2Y, currentLabelPaint)
                col2Y += lineHeight
            }

            val exitLines = wrapText("SORTIE : ${v.exitGate.label}", currentLabelPaint, columnWidth)
            exitLines.forEach { line ->
                canvas.drawText(line, col2X, col2Y, currentLabelPaint)
                col2Y += lineHeight
            }

            val arrivalLines = wrapText("ARRIVÉE : ${dateFormat.format(Date(v.arrivalAt))}", currentLabelPaint, columnWidth)
            arrivalLines.forEach { line ->
                canvas.drawText(line, col2X, col2Y, currentLabelPaint)
                col2Y += lineHeight
            }

            if (!isOnSite) {
                val exitTimeLines = wrapText("DÉPART : ${dateFormat.format(Date(v.exitAt!!))}", currentLabelPaint, columnWidth)
                exitTimeLines.forEach { line ->
                    canvas.drawText(line, col2X, col2Y, currentLabelPaint)
                    col2Y += lineHeight
                }
            }

            y = maxOf(currentY, col2Y) + 6f

            // Notes (sur toute la largeur)
            if (!v.notes.isNullOrBlank()) {
                val noteLines = wrapText("NOTES : ${v.notes}", currentLabelPaint, contentWidth)
                noteLines.forEach { line ->
                    canvas.drawText(line, marginLeft, y, currentLabelPaint)
                    y += lineHeight
                }
            }

            // Temps sur site
            if (!isOnSite) {
                val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(v.exitAt!! - v.arrivalAt)
                val hours = durationMinutes / 60
                val minutes = durationMinutes % 60
                val durationStr = if (hours > 0) "${hours}h ${minutes}min" else "${minutes}min"
                canvas.drawText("TEMPS SUR SITE : $durationStr", marginLeft, y, currentLabelPaint)
                y += lineHeight
            }

            y += 10f
        }

        // Trait final après le dernier véhicule
        if (sortedVehicles.isNotEmpty()) {
            canvas.drawLine(marginLeft, y, pageWidth - marginRight, y, separatorPaint)
        }

        // Section photos - TOUJOURS sur une nouvelle page
        val vehiclesWithPhotos = sortedVehicles.filter { !it.photoPath.isNullOrBlank() }
        if (vehiclesWithPhotos.isNotEmpty()) {
            // Toujours commencer une nouvelle page pour les photos
            pdf.finishPage(currentPage)
            pageNumber++
            currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = currentPage.canvas
            y = marginTop

            canvas.drawText("PHOTOS DES BONS DE LIVRAISON", marginLeft, y, titrePaint)
            y += 30f

            vehiclesWithPhotos.forEachIndexed { index, v ->
                val vehicleNumber = sortedVehicles.indexOf(v) + 1

                if (y > pageHeight - marginBottom - 250) {
                    pdf.finishPage(currentPage)
                    pageNumber++
                    currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                    canvas = currentPage.canvas
                    y = marginTop
                }

                canvas.drawText("Véhicule n°$vehicleNumber - ${v.plate}", marginLeft, y, headerPaint)
                y += 20f

                try {
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 2
                    }
                    val bitmap = BitmapFactory.decodeFile(v.photoPath, options)
                    if (bitmap != null) {
                        val maxWidth = contentWidth
                        val maxHeight = 200f
                        val scale = minOf(maxWidth / bitmap.width, maxHeight / bitmap.height)
                        val scaledWidth = bitmap.width * scale
                        val scaledHeight = bitmap.height * scale

                        val destRect = RectF(marginLeft, y, marginLeft + scaledWidth, y + scaledHeight)
                        canvas.drawBitmap(bitmap, null, destRect, null)
                        y += scaledHeight + 15f
                        bitmap.recycle()
                    }
                } catch (e: Exception) {
                    canvas.drawText("(Photo non disponible)", marginLeft, y, valuePaint)
                    y += 20f
                }

                y += 15f
            }
        }

        pdf.finishPage(currentPage)

        val dateStr = fileDateFormat.format(
            Date(shiftInfo.reportDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
        )
        val shiftLabel = if (shiftInfo.shiftNumber == 1) "Shift 1" else "Shift 2"
        val fileName = "Rapport du $dateStr - ${AgentManager.SITE_NAME} - ${shiftInfo.agentName} - $shiftLabel.pdf"
            .replace("/", "-")
            .replace(":", "-")

        val file = File(context.getExternalFilesDir(null), fileName)
        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        cleanOldPdfs(context)

        return file
    }

    private fun cleanOldPdfs(context: Context) {
        val dir = context.getExternalFilesDir(null) ?: return
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)

        dir.listFiles()
            ?.filter { it.extension == "pdf" && it.lastModified() < sevenDaysAgo }
            ?.forEach { it.delete() }
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                }
                // Si le mot seul est trop long, on le coupe
                if (paint.measureText(word) > maxWidth) {
                    var remaining = word
                    while (remaining.isNotEmpty()) {
                        var end = remaining.length
                        while (end > 1 && paint.measureText(remaining.substring(0, end)) > maxWidth) {
                            end--
                        }
                        lines.add(remaining.substring(0, end))
                        remaining = remaining.substring(end)
                    }
                    currentLine = ""
                } else {
                    currentLine = word
                }
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        return lines
    }
}
