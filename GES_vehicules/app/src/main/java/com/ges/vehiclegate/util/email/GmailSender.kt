package com.ges.vehiclegate.util.email

import android.util.Log
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GmailSender {

    companion object {
        private const val TAG = "GmailSender"
        private const val SMTP_HOST = "smtp.gmail.com"
        private const val SMTP_PORT = "587"
    }

    suspend fun sendEmail(
        toEmail: String,
        subject: String,
        body: String,
        attachment: File? = null,
        fromEmail: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val properties = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", SMTP_HOST)
                put("mail.smtp.port", SMTP_PORT)
                put("mail.smtp.ssl.protocols", "TLSv1.2")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(fromEmail, password)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                setSubject(subject, "UTF-8")
                sentDate = Date()

                if (attachment != null && attachment.exists()) {
                    val multipart = MimeMultipart()

                    val textPart = MimeBodyPart().apply {
                        setText(body, "UTF-8")
                    }
                    multipart.addBodyPart(textPart)

                    val attachmentPart = MimeBodyPart().apply {
                        val source = FileDataSource(attachment)
                        dataHandler = DataHandler(source)
                        fileName = attachment.name
                    }
                    multipart.addBodyPart(attachmentPart)

                    setContent(multipart)
                } else {
                    setText(body, "UTF-8")
                }
            }

            Transport.send(message)
            Log.d(TAG, "Email sent successfully to $toEmail")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send email", e)
            Result.failure(e)
        }
    }
}
