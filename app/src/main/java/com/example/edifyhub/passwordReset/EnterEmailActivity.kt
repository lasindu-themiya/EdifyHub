package com.example.edifyhub.passwordReset

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.edifyhub.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class EnterEmailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_email)

        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

        val btnreset = findViewById<Button>(R.id.btnResetPass)
        val emailEditText = findViewById<EditText>(R.id.etEmail)

        btnreset.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to send reset email: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}