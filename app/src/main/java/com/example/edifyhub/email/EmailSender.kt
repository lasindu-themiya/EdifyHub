package com.example.edifyhub.email

import java.util.Properties
import java.util.concurrent.Executors
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {


    private const val SENDER_EMAIL = "lasinduthemiya96@gmail.com"
    private const val SENDER_PASSWORD = "gvkj mfib aqnk cjjg"

    fun sendEmail(recipient: String, subject: String, body: String) {
        Executors.newSingleThreadExecutor().execute {
            val props = Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.socketFactory.port", "465")
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                put("mail.smtp.auth", "true")
                put("mail.smtp.port", "465")
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
                }
            })

            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(SENDER_EMAIL))
                    addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
                    this.subject = subject
                    setText(body)
                }
                Transport.send(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}