package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class StudentQuizListFragment : Fragment() {
    private lateinit var quizAdapter: QuizAdapter
    private val allQuizzes = mutableListOf<QuizItem>()
    private var userId: String? = null
    private val attemptedQuizIds = mutableSetOf<String>()
    private val paidQuizIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("USER_ID")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_student_quiz_list, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.quizRecyclerView)
        val progressBar = root.findViewById<ProgressBar>(R.id.progressBar)
        val subjectSearch = root.findViewById<EditText>(R.id.subjectSearch)
        val teacherSearch = root.findViewById<EditText>(R.id.teacherSearch)

        progressBar.visibility = View.VISIBLE
        fetchAttemptedQuizIds { ids ->
            attemptedQuizIds.clear()
            attemptedQuizIds.addAll(ids)
            fetchPaidQuizIds { paidIds ->
                paidQuizIds.clear()
                paidQuizIds.addAll(paidIds)
                quizAdapter = QuizAdapter(
                    onAttendClick = { quizItem -> showAttendQuizFragment(quizItem) },
                    onViewQuizClick = { quizItem -> showViewQuizFragment(quizItem) },
                    onPayClick = { quizItem -> handlePayQuiz(quizItem) },
                    attemptedQuizIds = attemptedQuizIds,
                    paidQuizIds = paidQuizIds
                )
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = quizAdapter

                fetchQuizzes { quizzes ->
                    allQuizzes.clear()
                    allQuizzes.addAll(quizzes)
                    quizAdapter.submitList(quizzes)
                    progressBar.visibility = View.GONE
                }
            }
        }

        subjectSearch.addTextChangedListener { filterQuizzes(subjectSearch.text.toString(), teacherSearch.text.toString()) }
        teacherSearch.addTextChangedListener { filterQuizzes(subjectSearch.text.toString(), teacherSearch.text.toString()) }

        return root
    }

    private fun filterQuizzes(subject: String, teacher: String) {
        val filtered = allQuizzes.filter {
            (subject.isBlank() || it.subject.contains(subject, ignoreCase = true)) &&
                    (teacher.isBlank() || it.teacherName.contains(teacher, ignoreCase = true))
        }
        quizAdapter.submitList(filtered)
    }

    private fun fetchAttemptedQuizIds(onResult: (Set<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        if (userId == null) {
            onResult(emptySet())
            return
        }
        db.collection("users").document(userId!!)
            .collection("attemptedQuizzes")
            .get()
            .addOnSuccessListener { snapshot ->
                val ids = snapshot.map { it.id }.toSet()
                onResult(ids)
            }
            .addOnFailureListener { onResult(emptySet()) }
    }

    private fun fetchPaidQuizIds(onResult: (Set<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        if (userId == null) {
            onResult(emptySet())
            return
        }
        db.collection("users").document(userId!!)
            .collection("paidQuizzes")
            .get()
            .addOnSuccessListener { snapshot ->
                val ids = snapshot.map { it.id }.toSet()
                onResult(ids)
            }
            .addOnFailureListener { onResult(emptySet()) }
    }

    private fun fetchQuizzes(onResult: (List<QuizItem>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val quizzes = mutableListOf<QuizItem>()
        val now = Date()

        db.collection("users")
            .get()
            .addOnSuccessListener { usersSnapshot ->
                val teacherDocs = usersSnapshot.documents
                if (teacherDocs.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }
                var processed = 0
                for (teacherDoc in teacherDocs) {
                    val teacherId = teacherDoc.id
                    val teacherName = teacherDoc.getString("username") ?: "Unknown"
                    db.collection("users").document(teacherId)
                        .collection("quizzes")
                        .get()
                        .addOnSuccessListener { quizSnapshot ->
                            for (quizDoc in quizSnapshot) {
                                val scheduledAt = quizDoc.getDate("scheduledAt")
                                val meetingAt = quizDoc.getDate("meetingAt")
                                val amount = quizDoc.getDouble("amount") ?: 0.0
                                val isPaid = quizDoc.getBoolean("paid") ?: false
                                if (scheduledAt != null && meetingAt != null &&
                                    now.after(scheduledAt) && now.before(meetingAt)) {
                                    quizzes.add(
                                        QuizItem(
                                            id = quizDoc.id,
                                            name = quizDoc.getString("name") ?: "",
                                            subject = quizDoc.getString("subject") ?: "",
                                            teacherId = teacherId,
                                            teacherName = teacherName,
                                            scheduledAt = scheduledAt,
                                            meetingAt = meetingAt,
                                            amount = amount,
                                            isPaid = isPaid
                                        )
                                    )
                                }
                            }
                            processed++
                            if (processed == teacherDocs.size) {
                                onResult(quizzes)
                            }
                        }
                        .addOnFailureListener {
                            processed++
                            if (processed == teacherDocs.size) {
                                onResult(quizzes)
                            }
                        }
                }
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    private fun showAttendQuizFragment(quizItem: QuizItem) {
        val fragment = AttendQuizFragment.newInstance(quizItem)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showViewQuizFragment(quizItem: QuizItem) {
        val fragment = ViewQuizFragment.newInstance(quizItem)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handlePayQuiz(quizItem: QuizItem) {
        // Redirect to student dashboard
        parentFragmentManager.popBackStack(null, 0)
    }
}