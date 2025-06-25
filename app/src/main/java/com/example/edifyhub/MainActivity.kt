package com.example.edifyhub

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.login.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Create an Intent to start LoginActivity.
        val intent = Intent(this, LoginActivity::class.java)

        startActivity( intent)

        // 3. Finish MainActivity so the user cannot navigate back to it.
        finish()


    }
}