package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

class ViewQuizFragment : Fragment() {
    private lateinit var quizItem: QuizItem
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizItem = requireArguments().getParcelable("quizItem")!!
        userId = activity?.intent?.getStringExtra("USER_ID")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_view_quiz, container, false)
        val title = root.findViewById<TextView>(R.id.tvQuizTitle)
        val info = root.findViewById<TextView>(R.id.tvQuizInfo)
        val answersLayout = root.findViewById<LinearLayout>(R.id.answersLayout)
        val progressBar = root.findViewById<ProgressBar>(R.id.progressBar)

        title.text = quizItem.name
        progressBar.visibility = View.VISIBLE

        // Fetch attempt
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId ?: "")
            .collection("attemptedQuizzes").document(quizItem.id)
            .get()
            .addOnSuccessListener { attemptDoc ->
                val score = attemptDoc.getLong("score")?.toInt() ?: 0
                val total = attemptDoc.getLong("total")?.toInt() ?: 0
                val studentUsername = attemptDoc.getString("studentUsername") ?: ""
                val teacherName = attemptDoc.getString("teacherName") ?: ""
                val answers = attemptDoc.get("answers") as? Map<String, Long> ?: emptyMap()

                info.text = "Student: $studentUsername\nTeacher: $teacherName\nScore: $score/$total"

                // Fetch questions to show text and correct answer
                db.collection("users").document(quizItem.teacherId)
                    .collection("quizzes").document(quizItem.id)
                    .collection("questions")
                    .get()
                    .addOnSuccessListener { qsnap ->
                        answersLayout.removeAllViews()
                        for (qdoc in qsnap) {
                            val qid = qdoc.id
                            val qtext = qdoc.getString("question") ?: ""
                            val qanswers = qdoc.get("answers") as? List<String> ?: emptyList()
                            val correctIndex = (qdoc.getLong("correctIndex") ?: 0L).toInt()
                            val userAnswerIndex = answers[qid]?.toInt() ?: -1

                            val qView = inflater.inflate(R.layout.item_view_quiz_answer, answersLayout, false)
                            qView.findViewById<TextView>(R.id.tvQuestion).text = qtext
                            qView.findViewById<TextView>(R.id.tvYourAnswer).text =
                                "Your answer: " + (if (userAnswerIndex in qanswers.indices) qanswers[userAnswerIndex] else "No answer")
                            qView.findViewById<TextView>(R.id.tvCorrectAnswer).text =
                                "Correct answer: " + (if (correctIndex in qanswers.indices) qanswers[correctIndex] else "N/A")
                            answersLayout.addView(qView)
                        }
                        progressBar.visibility = View.GONE
                    }
            }

        return root
    }

    companion object {
        fun newInstance(quizItem: QuizItem): ViewQuizFragment {
            val fragment = ViewQuizFragment()
            val args = Bundle()
            args.putParcelable("quizItem", quizItem)
            fragment.arguments = args
            return fragment
        }
    }
}