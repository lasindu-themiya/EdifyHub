package com.example.edifyhub.student

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
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
        val teacherSearch = root.findViewById<EditText>(R.id.teacherSearch)

        adapter = TeacherListAdapter(teachers) { teacherId ->
            val fragment = InstituteDetailsFragment.newInstance(teacherId)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        fetchTeachers { list ->
            teachers.clear()
            teachers.addAll(list)
            adapter.submitList(list)
            progressBar.visibility = View.GONE
        }

        teacherSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtered = teachers.filter {
                    it.name.contains(s.toString(), ignoreCase = true)
                }
                adapter.submitList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return root
    }

    private fun fetchTeachers(onResult: (List<TeacherItem>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("userRole", "teacher")
            .whereEqualTo("status", "approved")
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