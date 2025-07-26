package com.example.edifyhub.passwordReset

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EnterEmailActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "EnterEmailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_email)

        etEmail = findViewById(R.id.etEmail)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        firebaseAuth = FirebaseAuth.getInstance()

        btnSendOtp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Use the new, robust method to check for the user's existence
            checkIfUserExists(email)
        }
    }


    private fun checkIfUserExists(email: String) {
        Log.d(TAG, "Checking for user existence: $email")
        firebaseAuth.signInWithEmailAndPassword(email, "testpass")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Log.d(TAG, "User exists.")
                    firebaseAuth.signOut()
                    sendOtpEmail(email)
                } else {

                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Log.d(TAG, "User exists but the password is wrong")
                            sendOtpEmail(email)
                        }
                        is FirebaseAuthInvalidUserException -> {
                            Log.w(TAG, "Firebase reports email is NOT registered: $email")
                            Toast.makeText(this, "Email not registered", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.e(TAG, "Error checking user existence", task.exception)
                            Toast.makeText(this, "An error occurred: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    private fun sendOtpEmail(email: String) {
        Log.d(TAG, "User confirmed. Sending custom OTP to $email")
        val generatedOtp = (100000..999999).random().toString()
        val timestamp = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                EmailService.sendEmail(
                    toEmail = email,
                    subject = "Your OTP Code for EdifyHub Password Reset",
                    message = "Your OTP for password reset is: $generatedOtp"
                )

                val prefs = getSharedPreferences("otp_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putString("otp_${email}", generatedOtp)
                    .putLong("otp_time_${email}", timestamp)
                    .apply()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EnterEmailActivity, "OTP sent to your email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EnterEmailActivity, EnterOtpActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Failed to send OTP email", e)
                    Toast.makeText(
                        this@EnterEmailActivity,
                        "Failed to send OTP: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}