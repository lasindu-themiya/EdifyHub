package com.example.edifyhub.teacher

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.edifyhub.R
import java.text.SimpleDateFormat
import java.util.*

class QuizSetupFragment : Fragment() {

    private var scheduledDate: Date? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_quiz_setup, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.quiz_name)
        val subjectEditText = view.findViewById<EditText>(R.id.quiz_subject)
        val questionCount = view.findViewById<EditText>(R.id.number_questions)
        val answerCount = view.findViewById<EditText>(R.id.number_answers)
        val paidCheckBox = view.findViewById<CheckBox>(R.id.paid_checkbox)
        val amountEditText = view.findViewById<EditText>(R.id.amount)
        val nextButton = view.findViewById<Button>(R.id.next_button)

        val scheduledDateText = view.findViewById<TextView>(R.id.scheduled_date_text)
        val selectDateButton = view.findViewById<Button>(R.id.select_date_button)

        paidCheckBox.setOnCheckedChangeListener { _, isChecked ->
            amountEditText.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        selectDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth, 0, 0, 0)
                    scheduledDate = calendar.time
                    scheduledDateText.text = dateFormat.format(scheduledDate!!)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }

        nextButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val subject = subjectEditText.text.toString()
            val numQuestions = questionCount.text.toString().toIntOrNull() ?: 0
            val numAnswers = answerCount.text.toString().toIntOrNull() ?: 0
            val isPaid = paidCheckBox.isChecked
            val amount = if (isPaid) amountEditText.text.toString().toDoubleOrNull() else null

            if (scheduledDate == null) {
                Toast.makeText(requireContext(), "Please select a scheduled date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            (activity as? CreateQuizActivity)?.moveToQuestionInputFragment(
                name, subject, numQuestions, numAnswers, isPaid, amount, scheduledDate
            )
        }

        return view
    }
}