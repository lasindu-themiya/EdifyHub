package com.example.edifyhub.login

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import com.example.edifyhub.R
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.edifyhub.admin.AdminDashboardActivity
import com.example.edifyhub.student.StudentDashboardActivity
import com.example.edifyhub.teacher.TeacherDashboardActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signupButtonTeacher = findViewById<MaterialButton>(R.id.teacherSignUp)
        signupButtonTeacher.setOnClickListener {
            val intent = Intent(this, TeacherSignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        val signupButtonStudent = findViewById<MaterialButton>(R.id.studentSignUp)
        signupButtonStudent.setOnClickListener {
            val intent = Intent(this, StudentSignupActivity::class.java)
            startActivity(intent)
        }

        // Assume you have a login button and a username input field
        val loginButton = findViewById<MaterialButton>(R.id.signinbtn)
        val usernameInput = findViewById<EditText>(R.id.loginusername)

        loginButton.setOnClickListener {
            val username = usernameInput.text?.toString()?.trim()?.lowercase()
            when (username) {
                "admin" -> startActivity(Intent(this, AdminDashboardActivity::class.java))
                "student" -> startActivity(Intent(this, StudentDashboardActivity::class.java))
                "teacher" -> startActivity(Intent(this, TeacherDashboardActivity::class.java))
                else -> {
                    // Show error or do nothing
                }
            }
            finish()
        }
    }
}