package com.example.edifyhub.login

import android.os.Bundle
import com.example.edifyhub.R
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class TeacherSignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_teacher_signup)


        val SignupButton = findViewById<MaterialButton>(R.id.teachersignupbtn)
        SignupButton.setOnClickListener {}


    }
}