package com.example.edifyhub.student

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AttendQuizFragment : Fragment() {
    private lateinit var quizItem: QuizItem
    private val questions = mutableListOf<Question>()
    private val selectedAnswers = mutableMapOf<String, Int>() // questionId -> selectedIndex
    private var userId: String? = null
    private lateinit var questionDbHelper: QuizQuestionDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizItem = requireArguments().getParcelable<QuizItem>("QUIZ_ITEM")!!
        userId = activity?.intent?.getStringExtra("USER_ID")
        questionDbHelper = QuizQuestionDbHelper(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_attend_quiz, container, false)
        val questionsLayout = root.findViewById<LinearLayout>(R.id.questionsLayout)
        val btnSubmit = root.findViewById<Button>(R.id.btnSubmitQuiz)
        val progressBar = root.findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.VISIBLE

        if (isNetworkAvailable(requireContext())) {
            fetchQuestions { questionList ->
                progressBar.visibility = View.GONE
                questions.clear()
                questions.addAll(questionList)
                // Save questions locally for offline
                questionDbHelper.saveQuestions(quizItem.id, questions)
                populateQuestionsUI(inflater, questionsLayout)
            }
        } else {
            // Offline: load from SQLite
            val offlineQuestions = questionDbHelper.getQuestions(quizItem.id)
            progressBar.visibility = View.GONE
            questions.clear()
            questions.addAll(offlineQuestions)
            populateQuestionsUI(inflater, questionsLayout)
        }

        btnSubmit.setOnClickListener {
            val score = calculateScore()
            // Only save attempt if online
            if (isNetworkAvailable(requireContext())) {
                saveQuizAttempt(score) { success ->
                    showResultDialog(score, questions.size)
                }
            } else {
                showResultDialog(score, questions.size)
            }
        }

        return root
    }

    private fun populateQuestionsUI(inflater: LayoutInflater, questionsLayout: LinearLayout) {
        questionsLayout.removeAllViews()
        for (question in questions) {
            val qView = inflater.inflate(R.layout.item_quiz_question, questionsLayout, false)
            val tvQuestion = qView.findViewById<TextView>(R.id.tvQuestion)
            val radioGroup = qView.findViewById<RadioGroup>(R.id.radioGroupAnswers)
            val tvCorrectAnswer = qView.findViewById<TextView?>(R.id.tvCorrectAnswer) // optional: add this TextView in your layout if you want to show correct answer
            tvQuestion.text = question.text
            radioGroup.removeAllViews()
            for ((i, answer) in question.answers.withIndex()) {
                val rb = RadioButton(requireContext())
                rb.text = answer
                rb.id = i
                rb.setTextColor(resources.getColor(R.color.text_primary, null))
                radioGroup.addView(rb)
            }
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                selectedAnswers[question.id] = checkedId
            }
            // Optionally show correct answer if submitting in offline mode
            if (!isNetworkAvailable(requireContext())) {
                tvCorrectAnswer?.visibility = View.VISIBLE
                tvCorrectAnswer?.text = "Correct: ${question.answers.getOrNull(question.correctIndex) ?: ""}"
            }
            questionsLayout.addView(qView)
        }
    }

    private fun fetchQuestions(onResult: (List<Question>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(quizItem.teacherId)
            .collection("quizzes").document(quizItem.id)
            .collection("questions")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.map { doc ->
                    Question(
                        id = doc.id,
                        text = doc.getString("question") ?: "",
                        answers = doc.get("answers") as? List<String> ?: emptyList(),
                        correctIndex = (doc.getLong("correctIndex") ?: 0L).toInt()
                    )
                }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    private fun calculateScore(): Int {
        var score = 0
        for (q in questions) {
            val selected = selectedAnswers[q.id]
            if (selected != null && selected == q.correctIndex) {
                score++
            }
        }
        return score
    }


    private fun saveQuizAttempt(score: Int, onComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val studentId = userId ?: UUID.randomUUID().toString()


        db.collection("users").document(studentId).get().addOnSuccessListener { studentDoc ->
            val studentUsername = studentDoc.getString("username") ?: "Unknown"
            db.collection("users").document(quizItem.teacherId).get().addOnSuccessListener { teacherDoc ->
                val teacherName = teacherDoc.getString("username") ?: "Unknown"

                val attemptData = hashMapOf(
                    "userId" to studentId,
                    "studentUsername" to studentUsername,
                    "quizId" to quizItem.id,
                    "teacherId" to quizItem.teacherId,
                    "teacherName" to teacherName,
                    "answers" to selectedAnswers,
                    "score" to score,
                    "total" to questions.size,
                    "timestamp" to Date()
                )

                db.collection("users").document(quizItem.teacherId)
                    .collection("quizzes").document(quizItem.id)
                    .collection("attempts").document(studentId)
                    .set(attemptData)

                db.collection("users").document(studentId)
                    .collection("attemptedQuizzes").document(quizItem.id)
                    .set(attemptData)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showResultDialog(score: Int, total: Int) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Quiz Result")
            .setMessage("You scored $score out of $total")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                parentFragmentManager.popBackStack()
            }
            .show()
    }

    companion object {
        fun newInstance(quizItem: QuizItem): AttendQuizFragment {
            val fragment = AttendQuizFragment()
            val args = Bundle()
            args.putParcelable("QUIZ_ITEM", quizItem)
            fragment.arguments = args
            return fragment
        }
    }
}

data class Question(
    val id: String,
    val text: String,
    val answers: List<String>,
    val correctIndex: Int
)