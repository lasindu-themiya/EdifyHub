package com.example.edifyhub.teacher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.commit
import java.util.Date

class CreateQuizActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: TeacherDrawerMenuHandler
    private lateinit var toolbar: Toolbar
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        toolbar = findViewById(R.id.teacherToolbar)
        setSupportActionBar(toolbar)
        userId = intent.getStringExtra("USER_ID")
        drawerLayout = findViewById(R.id.teacherDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = TeacherDrawerMenuHandler(this, drawerLayout, navigationView, toolbar, userId)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, QuizSetupFragment())
            }
        }
    }

    fun moveToQuestionInputFragment(
        name: String,
        subject: String,
        numQuestions: Int,
        numAnswers: Int,
        paid: Boolean,
        amount: Double?,
        scheduledDate: Date?
    ) {
        val fragment = QuestionInputFragment.newInstance(
            name, subject, numQuestions, numAnswers, paid, amount, userId, scheduledDate
        )
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }
}