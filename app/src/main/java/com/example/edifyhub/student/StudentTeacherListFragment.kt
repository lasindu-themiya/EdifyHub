package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

class StudentTeacherListFragment : Fragment() {
    private lateinit var adapter: TeacherListAdapter
    private val teachers = mutableListOf<TeacherItem>()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("USER_ID")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_student_teacher_list, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.teacherRecyclerView)
        val progressBar = root.findViewById<ProgressBar>(R.id.progressBar)

        adapter = TeacherListAdapter(teachers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        fetchTeachers {
            adapter.submitList(it)
            progressBar.visibility = View.GONE
        }

        return root
    }

    private fun fetchTeachers(onResult: (List<TeacherItem>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("userRole", "teacher")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.map { doc ->
                    TeacherItem(
                        id = doc.id,
                        name = doc.getString("username") ?: "Unknown",
                        institute = doc.getString("institute") ?: "",
                        about = doc.getString("about Qualifications") ?: "",
                        imageUrl = doc.getString("profileImageUrl"),
                        subject = doc.getString("subject") ?: "N/A"
                    )
                }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}