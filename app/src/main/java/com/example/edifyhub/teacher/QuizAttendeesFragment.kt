package com.example.edifyhub.teacher

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

class QuizAttendeesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AttendeeAdapter
    private var quizId: String? = null
    private var teacherId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizId = arguments?.getString("QUIZ_ID")
        teacherId = arguments?.getString("TEACHER_ID")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_quiz_attendees, container, false)
        recyclerView = root.findViewById(R.id.attendeesRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AttendeeAdapter()
        recyclerView.adapter = adapter
        fetchAttendees()
        return root
    }

    private fun fetchAttendees() {
        if (quizId == null || teacherId == null) return
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(teacherId!!)
            .collection("quizzes").document(quizId!!)
            .collection("attempts")
            .get()
            .addOnSuccessListener { result ->
                val attempts = result.documents
                val attendees = mutableListOf<Attendee>()
                if (attempts.isEmpty()) {
                    adapter.submitList(attendees)
                    return@addOnSuccessListener
                }
                var loaded = 0
                for (doc in attempts) {
                    val userId = doc.getString("userId") ?: ""
                    val username = doc.getString("studentUsername") ?: ""
                    val score = doc.getLong("score")?.toInt() ?: 0
                    val total = doc.getLong("total")?.toInt() ?: 0
                    db.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            val email = userDoc.getString("email") ?: ""
                            val mobile = userDoc.getString("mobile") ?: ""
                            attendees.add(
                                Attendee(
                                    username = username,
                                    score = score,
                                    total = total,
                                    email = email,
                                    mobile = mobile
                                )
                            )
                            loaded++
                            if (loaded == attempts.size) {
                                adapter.submitList(attendees)
                            }
                        }
                        .addOnFailureListener {
                            loaded++
                            if (loaded == attempts.size) {
                                adapter.submitList(attendees)
                            }
                        }
                }
            }
    }

    companion object {
        fun newInstance(quizId: String, teacherId: String): QuizAttendeesFragment {
            val fragment = QuizAttendeesFragment()
            val args = Bundle()
            args.putString("QUIZ_ID", quizId)
            args.putString("TEACHER_ID", teacherId)
            fragment.arguments = args
            return fragment
        }
    }
}

data class Attendee(
    val username: String,
    val score: Int,
    val total: Int,
    val email: String,
    val mobile: String
)