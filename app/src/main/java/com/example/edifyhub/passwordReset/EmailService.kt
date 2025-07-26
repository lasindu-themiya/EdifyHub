package com.example.edifyhub.passwordReset

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailService {
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"
    private const val USERNAME = "lasinduthemiya96@gmail.com"
    private const val PASSWORD = "uzmo xwyr vnkm foqi"

    fun sendEmail(toEmail: String, subject: String, message: String) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", SMTP_HOST)
            put("mail.smtp.port", SMTP_PORT)
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(USERNAME, PASSWORD)
            }
        })

        val mimeMessage = MimeMessage(session).apply {
            setFrom(InternetAddress(USERNAME))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
            setSubject(subject)
            setText(message)
        }

        Transport.send(mimeMessage)
    }
}