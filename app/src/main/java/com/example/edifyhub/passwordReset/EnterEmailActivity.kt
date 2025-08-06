package com.example.edifyhub.passwordReset

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.edifyhub.email.EmailSender
import com.example.edifyhub.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class EnterEmailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_email)

        auth = FirebaseAuth.getInstance()

        val btnreset = findViewById<Button>(R.id.btnResetPass)
        val emailEditText = findViewById<EditText>(R.id.etEmail)

        btnreset.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isBlank()) {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            getPasswordResetLink(email) { link, error ->
                runOnUiThread {
                    if (link != null) {
                        Log.e("PasswordReset", "Generated reset link: $link")
                        EmailSender.sendEmail(email, "Password Reset Link", "Click the following link to reset your password: $link")
                        Toast.makeText(this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        if (error == "User did not sign up with email/password. Password reset is not available for this account.") {
                            showProviderErrorDialog()
                        } else {
                            Toast.makeText(this, error ?: "Failed to generate reset link.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun showProviderErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Password Reset Not Available")
            .setMessage("Your account was not created using email and password and cannot reset the password")
            .setCancelable(false)
            .setPositiveButton("Go to Login") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .show()
    }
}