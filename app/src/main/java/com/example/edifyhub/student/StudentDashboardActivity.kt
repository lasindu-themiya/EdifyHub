package com.example.edifyhub.student

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import com.example.edifyhub.login.StudentSignupActivity

class StudentDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        val userName = "Dasun"
        val completedQuizzes = 64
        val upComingQuizzes = 27
        val postedDiscussions = 30

        findViewById<TextView>(R.id.username).text = "Hello, $userName"
        findViewById<TextView>(R.id.completedQuizzes).text = completedQuizzes.toString()
        findViewById<TextView>(R.id.upComingQuizzes).text = upComingQuizzes.toString()
        findViewById<TextView>(R.id.postedDiscussions).text = postedDiscussions.toString()


        val searchQuizzes = findViewById<ImageButton>(R.id.searchQuizzes)
        searchQuizzes.setOnClickListener {
            val intent = Intent(this, StudentSignupActivity::class.java)
            startActivity(intent)
        }

    }



}