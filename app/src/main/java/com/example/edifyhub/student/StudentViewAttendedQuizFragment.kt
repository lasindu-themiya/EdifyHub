package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

data class AttendedQuiz(
    val quizId: String = "",
    val teacherId: String = "",
    val quizName: String = "",
    val teacherName: String = "",
    val subject: String = "",
    val score: Int = 0,
    val total: Int = 0,
    val timestamp: com.google.firebase.Timestamp? = null
)

class StudentViewAttendedQuizFragment : Fragment() {
    private var userId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = AttendedQuizAdapter()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("USER_ID")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_student_view_attended_quiz, container, false)
        recyclerView = root.findViewById(R.id.recyclerViewAttendedQuizzes)
        progressBar = root.findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        fetchAttendedQuizzes()
        return root
    }

    private fun fetchAttendedQuizzes() {
        val uid = userId ?: return
        db.collection("users").document(uid)
            .collection("attemptedQuizzes")
            .get()
            .addOnSuccessListener { snapshot ->
                val attendedList = snapshot.map { doc ->
                    AttendedQuiz(
                        quizId = doc.getString("quizId") ?: "",
                        teacherId = doc.getString("teacherId") ?: "",
                        score = (doc.getLong("score") ?: 0L).toInt(),
                        total = (doc.getLong("total") ?: 0L).toInt(),
                        timestamp = doc.getTimestamp("timestamp")
                    )
                }
                if (attendedList.isEmpty()) {
                    adapter.submitList(emptyList())
                    progressBar.visibility = View.GONE
                    return@addOnSuccessListener
                }
                val resultList = mutableListOf<AttendedQuiz>()
                var fetchedCount = 0
                for (item in attendedList) {
                    if (item.quizId.isEmpty() || item.teacherId.isEmpty()) {
                        fetchedCount++
                        if (fetchedCount == attendedList.size) {
                            adapter.submitList(resultList)
                            progressBar.visibility = View.GONE
                        }
                        continue
                    }
                    db.collection("users").document(item.teacherId)
                        .collection("quizzes").document(item.quizId)
                        .get()
                        .addOnSuccessListener { quizDoc ->
                            val quizName = quizDoc.getString("name") ?: "Unknown Quiz"
                            val subject = quizDoc.getString("subject") ?: "Unknown Subject"
                            db.collection("users").document(item.teacherId)
                                .get()
                                .addOnSuccessListener { teacherDoc ->
                                    val teacherName = teacherDoc.getString("username") ?: "Unknown Teacher"
                                    resultList.add(
                                        item.copy(
                                            quizName = quizName,
                                            teacherName = teacherName,
                                            subject = subject
                                        )
                                    )
                                    fetchedCount++
                                    if (fetchedCount == attendedList.size) {
                                        adapter.submitList(resultList)
                                        progressBar.visibility = View.GONE
                                    }
                                }
                                .addOnFailureListener {
                                    resultList.add(
                                        item.copy(
                                            quizName = quizName,
                                            teacherName = "Unknown Teacher",
                                            subject = subject
                                        )
                                    )
                                    fetchedCount++
                                    if (fetchedCount == attendedList.size) {
                                        adapter.submitList(resultList)
                                        progressBar.visibility = View.GONE
                                    }
                                }
                        }
                        .addOnFailureListener {
                            resultList.add(item)
                            fetchedCount++
                            if (fetchedCount == attendedList.size) {
                                adapter.submitList(resultList)
                                progressBar.visibility = View.GONE
                            }
                        }
                }
            }
            .addOnFailureListener {
                adapter.submitList(emptyList())
                progressBar.visibility = View.GONE
            }
    }
}