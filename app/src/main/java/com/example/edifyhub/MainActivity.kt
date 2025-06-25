package com.example.edifyhub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Launch AdminApprovalActivity
        val intent = Intent(this, AdminApprovalActivity::class.java)
        startActivity(intent)

        // Optional: finish MainActivity if not needed
        finish()
    }
}
