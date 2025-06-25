package com.example.edifyhub.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import com.google.android.material.button.MaterialButton

class StudentSignupActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_signup)

        //navigate to signin page
        val SignInButton = findViewById<TextView>(R.id.signInLink)
        SignInButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        val SignupButton = findViewById<MaterialButton>(R.id.signupbtn)
        SignupButton.setOnClickListener {
            // signup logic
        }

    }
}