package com.example.edifyhub.login

import android.content.Intent
import android.os.Bundle
import com.example.edifyhub.R
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        //navigate to teacher signup page
        val signupButtonTeacher = findViewById<MaterialButton>(R.id.teacherSignUp)
        signupButtonTeacher.setOnClickListener {
            val intent = Intent(this, TeacherSignupActivity::class.java)
            startActivity(intent)
            finish()
        }


        //navigate to student signup page
        val signupButtonStudent = findViewById<MaterialButton>(R.id.studentSignUp)
        signupButtonStudent.setOnClickListener {
            val intent = Intent(this, StudentSignupActivity::class.java)
            startActivity(intent)
        }




    }
}