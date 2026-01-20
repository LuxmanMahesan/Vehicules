package com.ges.vehiclegate.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.work.*
import com.ges.vehiclegate.data.mapper.toDomain
import com.ges.vehiclegate.di.AppModule
import com.ges.vehiclegate.domain.model.AgentManager
import com.ges.vehiclegate.domain.model.ShiftCalculator
import com.ges.vehiclegate.domain.model.ShiftInfo
import com.ges.vehiclegate.util.pdf.PdfReportGenerator
import com.ges.vehiclegate.util.email.EmailConfig
import com.ges.vehiclegate.util.email.GmailSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ShiftScheduler {

    private const val SHIFT_WORK_TAG = "shift_change_work"
    private const val EMAIL_ADDRESS = "evalux.casino@gmail.com"

    fun scheduleNextShiftChange(context: Context) {
        val now = LocalDateTime.now()
        val nextShiftTime = ShiftCalculator.getNextShiftChangeTime(now)
        val delayMillis = Duration.between(now, nextShiftTime).toMillis()

        val workRequest = OneTimeWorkRequestBuilder<ShiftChangeWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .addTag(SHIFT_WORK_TAG)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                SHIFT_WORK_TAG,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

        Log.d("ShiftScheduler", "Next shift change scheduled for: $nextShiftTime (in ${delayMillis / 1000 / 60} minutes)")
    }

    fun cancelScheduledWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(SHIFT_WORK_TAG)
    }
}

class ShiftChangeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                AgentManager.init(applicationContext)

                val previousShiftNumber = if (ShiftCalculator.getCurrentShiftNumber() == 1) 2 else 1
                val now = LocalDateTime.now()
                val reportDate = if (previousShiftNumber == 2) {
                    val time = now.toLocalTime()
                    if (time < LocalTime.of(7, 0)) {
                        now.toLocalDate().minusDays(1)
                    } else {
                        now.toLocalDate()
                    }
                } else {
                    now.toLocalDate()
                }

                val shiftInfo = ShiftInfo(
                    shiftNumber = previousShiftNumber,
                    startTime = ShiftCalculator.getShiftStartTime(previousShiftNumber, reportDate),
                    endTime = ShiftCalculator.getShiftEndTime(previousShiftNumber, reportDate),
                    agentName = AgentManager.getAgentDisplayName(),
                    reportDate = reportDate
                )

                val dao = AppModule.provideDatabase(applicationContext).vehicleEntryDao()

                // Récupérer TOUS les véhicules non archivés pour le rapport PDF:
                // - Véhicules encore sur site (onSite = 1, peu importe leur date d'arrivée)
                // - Véhicules sortis durant ce shift (onSite = 0)
                val vehiclesForReport = dao.getAllVehiclesForReport()
                    .map { it.toDomain() }
                    .sortedBy { it.arrivalAt }

                if (vehiclesForReport.isNotEmpty()) {
                    val pdfGenerator = PdfReportGenerator()
                    val pdfFile = pdfGenerator.generer(applicationContext, vehiclesForReport, shiftInfo)

                    sendEmailWithPdfAutomated(applicationContext, pdfFile, shiftInfo)

                    // Supprimer tous les véhicules sortis (onSite = 0) après envoi du PDF
                    dao.supprimerVehiculesSortis()
                }

                ShiftScheduler.scheduleNextShiftChange(applicationContext)

                Result.success()
            } catch (e: Exception) {
                Log.e("ShiftChangeWorker", "Error during shift change", e)
                ShiftScheduler.scheduleNextShiftChange(applicationContext)
                Result.failure()
            }
        }
    }

    private suspend fun sendEmailWithPdfAutomated(context: Context, pdfFile: File, shiftInfo: ShiftInfo) {
        try {
            if (EmailConfig.isConfigured(context)) {
                val fromEmail = EmailConfig.getFromEmail(context)!!
                val password = EmailConfig.getAppPassword(context)!!
                val toEmail = EmailConfig.getToEmail(context)

                val subject = "Rapport ${shiftInfo.label} - ${shiftInfo.reportDate} - ${AgentManager.SITE_NAME}"
                val body = """
                    Bonjour,

                    Veuillez trouver ci-joint le rapport de véhicules pour:

                    Site: ${AgentManager.SITE_NAME}
                    Date: ${shiftInfo.reportDate}
                    Shift: ${shiftInfo.label}
                    Agent: ${shiftInfo.agentName}

                    Cordialement,
                    Application GES Véhicules
                """.trimIndent()

                val gmailSender = GmailSender()
                val result = gmailSender.sendEmail(
                    toEmail = toEmail,
                    subject = subject,
                    body = body,
                    attachment = pdfFile,
                    fromEmail = fromEmail,
                    password = password
                )

                if (result.isSuccess) {
                    Log.d("ShiftChangeWorker", "Email sent successfully")
                } else {
                    Log.e("ShiftChangeWorker", "Failed to send email: ${result.exceptionOrNull()?.message}")
                    sendEmailWithPdfFallback(context, pdfFile, shiftInfo)
                }
            } else {
                Log.w("ShiftChangeWorker", "Email not configured, using fallback method")
                sendEmailWithPdfFallback(context, pdfFile, shiftInfo)
            }
        } catch (e: Exception) {
            Log.e("ShiftChangeWorker", "Error in automated email sending, using fallback", e)
            sendEmailWithPdfFallback(context, pdfFile, shiftInfo)
        }
    }

    private fun sendEmailWithPdfFallback(context: Context, pdfFile: File, shiftInfo: ShiftInfo) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                pdfFile
            )

            val subject = "Rapport ${shiftInfo.label} - ${shiftInfo.reportDate} - ${AgentManager.SITE_NAME}"
            val body = """
                Bonjour,

                Veuillez trouver ci-joint le rapport de véhicules pour:

                Site: ${AgentManager.SITE_NAME}
                Date: ${shiftInfo.reportDate}
                Shift: ${shiftInfo.label}
                Agent: ${shiftInfo.agentName}

                Cordialement,
                Application GES Véhicules
            """.trimIndent()

            val toEmail = EmailConfig.getToEmail(context)
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(emailIntent, "Envoyer le rapport par email").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e("ShiftChangeWorker", "Error sending email", e)
        }
    }
}
