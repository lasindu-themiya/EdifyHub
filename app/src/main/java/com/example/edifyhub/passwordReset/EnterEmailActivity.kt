package com.example.edifyhub.passwordReset

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import android.widget.Button
import android.widget.EditText

class EnterEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_email)

        val btnSendOtp = findViewById<Button>(R.id.btnSendOtp)
        btnSendOtp.setOnClickListener {
            startActivity(Intent(this, EnterOtpActivity::class.java))
        }
    }
}