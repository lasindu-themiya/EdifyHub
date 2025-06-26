package com.example.edifyhub.teacher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.edifyhub.R
import com.example.edifyhub.teacher.QuestionInputFragment

class CreateQuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, QuizSetupFragment())
            }
        }
    }

    fun moveToQuestionInputFragment(name: String, subject: String, numQuestions: Int, numAnswers: Int, paid: Boolean, amount: Double?) {
        val fragment = QuestionInputFragment.newInstance(name, subject, numQuestions, numAnswers, paid, amount)
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }
}