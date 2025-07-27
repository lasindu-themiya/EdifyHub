package com.example.edifyhub.teacher

import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.example.edifyhub.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class QuestionInputFragment : Fragment() {
    private var quizName = ""
    private var subject = ""
    private var numQuestions = 0
    private var numAnswers = 0
    private var paid = false
    private var amount: Double? = null
    private var userId: String? = null

    companion object {
        fun newInstance(
            name: String,
            subject: String,
            numQuestions: Int,
            numAnswers: Int,
            paid: Boolean,
            amount: Double?,
            userId: String?
        ) =
            QuestionInputFragment().apply {
                arguments = Bundle().apply {
                    putString("name", name)
                    putString("subject", subject)
                    putInt("questions", numQuestions)
                    putInt("answers", numAnswers)
                    putBoolean("paid", paid)
                    putDouble("amount", amount ?: 0.0)
                    putString("userId", userId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            quizName = it.getString("name") ?: ""
            subject = it.getString("subject") ?: ""
            numQuestions = it.getInt("questions")
            numAnswers = it.getInt("answers")
            paid = it.getBoolean("paid")
            amount = it.getDouble("amount", 0.0)
            userId = it.getString("userId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_question_input, container, false)
        val questionContainer = root.findViewById<LinearLayout>(R.id.question_container)

        // Section: Quiz meta fields
        val metaSection = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24.dpToPx())
            setBackgroundResource(R.drawable.card_bg)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 16.dpToPx()
            }
        }
        metaSection.addView(createSectionHeader("Quiz Details"))
        val quizNameEdit = createEditText("Enter quiz name").apply { setText(quizName) }
        val subjectEdit = createEditText("Enter subject").apply { setText(subject) }
        val questionsEdit = createEditText("Enter number of questions", android.text.InputType.TYPE_CLASS_NUMBER).apply {
            setText(numQuestions.toString())
        }
        val answersEdit = createEditText("Enter number of answers", android.text.InputType.TYPE_CLASS_NUMBER).apply {
            setText(numAnswers.toString())
        }
        val paidCheckbox = MaterialCheckBox(requireContext()).apply {
            text = "Paid Quiz?"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            textSize = 16f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            isChecked = paid
        }
        val amountLabel = createLabel("Amount").apply { visibility = if (paid) View.VISIBLE else View.GONE }
        val amountEdit = createEditText("Enter amount", android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_CLASS_NUMBER).apply {
            visibility = if (paid) View.VISIBLE else View.GONE
            setText(amount?.takeIf { it > 0 }?.toString() ?: "")
        }
        metaSection.addView(createLabel("Quiz Name"))
        metaSection.addView(quizNameEdit)
        metaSection.addView(createLabel("Subject"))
        metaSection.addView(subjectEdit)
        metaSection.addView(createLabel("Number of Questions"))
        metaSection.addView(questionsEdit)
        metaSection.addView(createLabel("Number of Answers per Question"))
        metaSection.addView(answersEdit)
        metaSection.addView(paidCheckbox)
        metaSection.addView(amountLabel)
        metaSection.addView(amountEdit)
        questionContainer.addView(metaSection)

        paidCheckbox.setOnCheckedChangeListener { _, isChecked ->
            amountLabel.visibility = if (isChecked) View.VISIBLE else View.GONE
            amountEdit.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Store question and answer views for later retrieval
        val questionEditList = mutableListOf<EditText>()
        val answerEditMatrix = mutableListOf<List<EditText>>()
        val correctAnswerSpinners = mutableListOf<Spinner>()

        // Section: Questions
        for (i in 1..numQuestions) {
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20.dpToPx())
                setBackgroundResource(R.drawable.card_bg)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    bottomMargin = 16.dpToPx()
                }
                elevation = 4f
            }
            card.addView(createSectionHeader("Question $i"))

            val questionEditText = createEditText("Enter question $i")
            questionEditList.add(questionEditText)
            card.addView(questionEditText)

            val answerLabel = createLabel("Answers")
            card.addView(answerLabel)

            val answerEdits = mutableListOf<EditText>()
            for (j in 1..numAnswers) {
                val answerEditText = createEditText("Answer $j").apply {
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        setMargins(0, 8.dpToPx(), 0, 8.dpToPx())
                    }
                }
                answerEdits.add(answerEditText)
                card.addView(answerEditText)
            }
            answerEditMatrix.add(answerEdits)

            // Spinner for correct answer selection
            val spinnerLabel = createLabel("Select Correct Answer")
            val correctAnswerSpinner = Spinner(requireContext()).apply {
                val items = (1..numAnswers).map { "Answer $it" }
                adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = 8.dpToPx()
                    bottomMargin = 8.dpToPx()
                }
                setBackgroundResource(R.drawable.spinner_bg)
            }
            correctAnswerSpinners.add(correctAnswerSpinner)
            card.addView(spinnerLabel)
            card.addView(correctAnswerSpinner)

            questionContainer.addView(card)
        }

        // Create Quiz button
        val createButton = MaterialButton(requireContext()).apply {
            text = "Create Quiz"
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
            setPadding(24.dpToPx(), 24.dpToPx(), 24.dpToPx(), 24.dpToPx())
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 24.dpToPx()
                bottomMargin = 24.dpToPx()
            }
            cornerRadius = 12.dpToPx()
            setOnClickListener {
                // Validate fields
                for (idx in questionEditList.indices) {
                    if (questionEditList[idx].text.isNullOrBlank()) {
                        Toast.makeText(requireContext(), "Please enter text for question ${idx + 1}", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    answerEditMatrix[idx].forEachIndexed { aIdx, edit ->
                        if (edit.text.isNullOrBlank()) {
                            Toast.makeText(requireContext(), "Please enter text for answer ${aIdx + 1} of question ${idx + 1}", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to create this quiz?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        saveQuizAndQuestions(
                            quizNameEdit.text.toString(),
                            subjectEdit.text.toString(),
                            questionsEdit.text.toString().toIntOrNull() ?: numQuestions,
                            answersEdit.text.toString().toIntOrNull() ?: numAnswers,
                            paidCheckbox.isChecked,
                            amountEdit.text.toString().toDoubleOrNull(),
                            questionEditList,
                            answerEditMatrix,
                            correctAnswerSpinners
                        )
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
        questionContainer.addView(createButton)

        return root
    }

    private fun saveQuizAndQuestions(
        quizName: String,
        subject: String,
        numQuestions: Int,
        numAnswers: Int,
        paid: Boolean,
        amount: Double?,
        questionEditList: List<EditText>,
        answerEditMatrix: List<List<EditText>>,
        correctAnswerSpinners: List<Spinner>
    ) {
        if (userId == null) {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }
        // Validate correct answer selection
        for (i in 0 until numQuestions) {
            if (correctAnswerSpinners[i].selectedItemPosition == AdapterView.INVALID_POSITION) {
                Toast.makeText(requireContext(), "Select the correct answer for question ${i + 1}", Toast.LENGTH_SHORT).show()
                return
            }
        }
        val db = FirebaseFirestore.getInstance()
        val quizData = hashMapOf(
            "name" to quizName,
            "subject" to subject,
            "numQuestions" to numQuestions,
            "numAnswers" to numAnswers,
            "paid" to paid,
            "amount" to (amount ?: 0.0),
            "createdAt" to Date()
        )
        db.collection("users").document(userId!!)
            .collection("quizzes")
            .add(quizData)
            .addOnSuccessListener { quizRef ->
                for (i in 0 until numQuestions) {
                    val questionText = questionEditList[i].text.toString()
                    val answers = answerEditMatrix[i].map { it.text.toString() }
                    val correctIndex = correctAnswerSpinners[i].selectedItemPosition
                    val questionData = hashMapOf(
                        "question" to questionText,
                        "answers" to answers,
                        "correctIndex" to correctIndex
                    )
                    quizRef.collection("questions").add(questionData)
                }
                Toast.makeText(requireContext(), "Quiz created successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), TeacherDashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to create quiz: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createLabel(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            textSize = 16f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }
    }

    private fun createSectionHeader(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            textSize = 18f
            typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
            setPadding(0, 0, 0, 12.dpToPx())
        }
    }

    private fun createEditText(hint: String, inputType: Int = android.text.InputType.TYPE_CLASS_TEXT): EditText {
        return EditText(requireContext()).apply {
            this.hint = hint
            this.inputType = inputType
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            setHintTextColor(ContextCompat.getColor(requireContext(), R.color.surface))
            textSize = 16f
            setBackgroundResource(R.drawable.edittext_background)
            setPadding(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 8.dpToPx()
                bottomMargin = 8.dpToPx()
            }
        }
    }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}