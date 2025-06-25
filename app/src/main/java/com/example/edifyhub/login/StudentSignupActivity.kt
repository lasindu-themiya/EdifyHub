package com.example.edifyhub.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import com.google.android.material.button.MaterialButton

class StudentSignupActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_signup)

        val SignupButton = findViewById<MaterialButton>(R.id.signupbtn)
        SignupButton.setOnClickListener {
            // signup logic
        }

    }
}