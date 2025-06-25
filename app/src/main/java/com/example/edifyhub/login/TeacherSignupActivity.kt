package com.example.edifyhub.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.example.edifyhub.R
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class TeacherSignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_teacher_signup)

        //navigate to signin page
        val SignInButton = findViewById<TextView>(R.id.signInLink)
        SignInButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



        val SignupButton = findViewById<MaterialButton>(R.id.teachersignupbtn)
        SignupButton.setOnClickListener {}


    }
}