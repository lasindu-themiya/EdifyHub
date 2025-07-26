package com.example.edifyhub.passwordReset

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import com.example.edifyhub.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EnterOtpActivity : AppCompatActivity() {
    private lateinit var otpFields: Array<EditText>
    private lateinit var btnVerifyOtp: Button
    private var email: String = ""
    private val OTP_VALIDITY_MS = 5 * 60 * 1000L // 5 minutes
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "EnterOtpActivity"

    // ... (keep onCreate and setupOtpFields exactly the same) ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_otp)

        firebaseAuth = FirebaseAuth.getInstance()

        otpFields = arrayOf(
            findViewById(R.id.otp1),
            findViewById(R.id.otp2),
            findViewById(R.id.otp3),
            findViewById(R.id.otp4),
            findViewById(R.id.otp5),
            findViewById(R.id.otp6)
        )
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
        email = intent.getStringExtra("email") ?: ""

        if (email.isEmpty()) {
            Toast.makeText(this, "Error: Email not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupOtpFields()

        btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }
    }

    private fun setupOtpFields() {
        for (i in otpFields.indices) {
            otpFields[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < otpFields.size - 1) {
                        otpFields[i + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            otpFields[i].setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (i > 0 && otpFields[i].text.isEmpty()) {
                        otpFields[i - 1].requestFocus()
                    }
                }
                false
            })
        }
    }

    private fun verifyOtp() {
        val enteredOtp = otpFields.joinToString("") { it.text.toString() }
        if (enteredOtp.length < 6) {
            Toast.makeText(this, "Please enter the full OTP", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("otp_prefs", Context.MODE_PRIVATE)
        val storedOtp = prefs.getString("otp_${email}", null)
        val otpTime = prefs.getLong("otp_time_${email}", 0L)
        val now = System.currentTimeMillis()

        if (storedOtp == null || otpTime == 0L || now - otpTime > OTP_VALIDITY_MS) {
            Toast.makeText(
                this,
                "OTP is invalid or has expired. Please request a new one.",
                Toast.LENGTH_SHORT
            ).show()
            if (storedOtp != null) {
                prefs.edit().remove("otp_${email}").remove("otp_time_${email}").apply()
            }
            finish()
            return
        }

        if (enteredOtp == storedOtp) {
            prefs.edit().remove("otp_${email}").remove("otp_time_${email}").apply()
            Toast.makeText(this, "OTP Verified! Sending password reset link...", Toast.LENGTH_SHORT)
                .show()

            // Call the new function that uses the built-in Firebase method
            sendFirebasePasswordResetEmail()

        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
        }
    }


    private fun sendFirebasePasswordResetEmail() {
        Log.d(TAG, "Sending Firebase password reset email to: $email")
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send reset email: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}


