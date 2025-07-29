// Kotlin: app/src/main/java/com/example/edifyhub/student/StudentTeacherListFragment.kt
package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.addAll
import kotlin.text.clear
import kotlin.text.replace

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
        val searchView = root.findViewById<SearchView>(R.id.searchView)

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

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = teachers.filter {
                    it.name.contains(newText.orEmpty(), ignoreCase = true)
                }
                adapter.submitList(filtered)
                return true
            }
        })

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