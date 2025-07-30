package com.example.edifyhub.student

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.example.edifyhub.payment.PayHerePaymentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.*
import kotlin.text.get

class StudentQuizListFragment : Fragment() {
    private lateinit var quizAdapter: QuizAdapter
    private val allQuizzes = mutableListOf<QuizItem>()
    private var userId: String? = null
    private val attemptedQuizIds = mutableSetOf<String>()
    private val paidQuizIds = mutableSetOf<String>()
    private var paidQuizzesListener: ListenerRegistration? = null
    private var studentStream: String? = null
    private val streamSubjects = mutableSetOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("USER_ID")
    }

    private val paymentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // No need to manually refresh, listener will auto-update
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_student_quiz_list, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.quizRecyclerView)
        val progressBar = root.findViewById<ProgressBar>(R.id.progressBar)
        val subjectSearch = root.findViewById<EditText>(R.id.subjectSearch)
        val teacherSearch = root.findViewById<EditText>(R.id.teacherSearch)

        progressBar.visibility = View.VISIBLE

        // Fetch student stream first
        fetchStudentStream { stream ->
            studentStream = stream
            if (stream != null) {
                fetchStreamSubjects(stream) { subjects ->
                    streamSubjects.clear()
                    streamSubjects.addAll(subjects)
                    fetchAttemptedQuizIds { ids ->
                        attemptedQuizIds.clear()
                        attemptedQuizIds.addAll(ids)
                        fetchPaidQuizIds { paidIds ->
                            paidQuizIds.clear()
                            paidQuizIds.addAll(paidIds)
                            quizAdapter = QuizAdapter(
                                onAttendClick = { quizItem -> showAttendQuizFragment(quizItem) },
                                onViewQuizClick = { quizItem -> showViewQuizFragment(quizItem) },
                                onPayClick = { quizItem ->
                                    val intent = Intent(requireContext(), PayHerePaymentActivity::class.java).apply {
                                        putExtra("quizId", quizItem.id)
                                        putExtra("quizName", quizItem.name)
                                        putExtra("quizAmount", quizItem.amount)
                                        putExtra("teacherId", quizItem.teacherId)
                                        putExtra("teacherName", quizItem.teacherName)
                                    }
                                    paymentLauncher.launch(intent)
                                },
                                attemptedQuizIds = attemptedQuizIds,
                                paidQuizIds = paidQuizIds
                            )
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            recyclerView.adapter = quizAdapter

                            fetchQuizzes { quizzes ->
                                // Filter quizzes by stream subjects
                                val filteredQuizzes = quizzes.filter { streamSubjects.contains(it.subject) }
                                allQuizzes.clear()
                                allQuizzes.addAll(filteredQuizzes)
                                quizAdapter.submitList(filteredQuizzes)
                                progressBar.visibility = View.GONE
                            }

                            listenForPaidQuizzesUpdates()
                        }
                    }
                }
            } else {
                // If stream is not found, show no quizzes
                allQuizzes.clear()
                quizAdapter = QuizAdapter(
                    onAttendClick = { quizItem -> showAttendQuizFragment(quizItem) },
                    onViewQuizClick = { quizItem -> showViewQuizFragment(quizItem) },
                    onPayClick = { quizItem -> /* ... */ },
                    attemptedQuizIds = attemptedQuizIds,
                    paidQuizIds = paidQuizIds
                )
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = quizAdapter
                quizAdapter.submitList(emptyList())
                progressBar.visibility = View.GONE
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
        parentFragmentManager.popBackStack(null, 0)
    }

    private fun listenForPaidQuizzesUpdates() {
        val db = FirebaseFirestore.getInstance()
        if (userId == null) return
        paidQuizzesListener?.remove()
        paidQuizzesListener = db.collection("users").document(userId!!)
            .collection("paidQuizzes")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    paidQuizIds.clear()
                    paidQuizIds.addAll(snapshot.documents.map { it.id })
                    if (::quizAdapter.isInitialized) {
                        quizAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        paidQuizzesListener?.remove()
        paidQuizzesListener = null
    }

    private fun fetchStudentStream(onResult: (String?) -> Unit) {
        if (userId == null) {
            onResult(null)
            return
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId!!)
            .get()
            .addOnSuccessListener { doc ->
                val stream = doc.getString("stream")
                onResult(stream)
            }
            .addOnFailureListener { onResult(null) }
    }
    private fun fetchStreamSubjects(stream: String, onResult: (Set<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("streams").document(stream)
            .get()
            .addOnSuccessListener { doc ->
                val subjects = doc.get("subjects") as? List<String> ?: emptyList()
                onResult(subjects.toSet())
            }
            .addOnFailureListener { onResult(emptySet()) }
    }
}