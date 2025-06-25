package com.example.edifyhub.teachercreatequiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.edifyhub.R

class QuizSetupFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_quiz_setup, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.quiz_name)
        val subjectEditText = view.findViewById<EditText>(R.id.quiz_subject)
        val questionCount = view.findViewById<EditText>(R.id.number_questions)
        val answerCount = view.findViewById<EditText>(R.id.number_answers)
        val paidCheckBox = view.findViewById<CheckBox>(R.id.paid_checkbox)
        val amountEditText = view.findViewById<EditText>(R.id.amount)
        val nextButton = view.findViewById<Button>(R.id.next_button)

        paidCheckBox.setOnCheckedChangeListener { _, isChecked ->
            amountEditText.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        nextButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val subject = subjectEditText.text.toString()
            val numQuestions = questionCount.text.toString().toIntOrNull() ?: 0
            val numAnswers = answerCount.text.toString().toIntOrNull() ?: 0
            val isPaid = paidCheckBox.isChecked
            val amount = if (isPaid) amountEditText.text.toString().toDoubleOrNull() else null

            (activity as? CreateQuizActivity)?.moveToQuestionInputFragment(name, subject, numQuestions, numAnswers, isPaid, amount)
        }

        return view
    }
}