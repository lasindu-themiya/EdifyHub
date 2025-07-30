package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

class StudentViewOpenDiscussionFragment : Fragment() {

    private var userId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiscussionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_student_view_open_discussion, container, false)
        userId = arguments?.getString("USER_ID")
        recyclerView = view.findViewById(R.id.recyclerViewDiscussions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DiscussionAdapter(userId ?: "", emptyList()) { fetchOpenDiscussions() }
        recyclerView.adapter = adapter

        fetchOpenDiscussions()
        return view
    }

    private fun fetchOpenDiscussions() {
        val db = FirebaseFirestore.getInstance()
        userId?.let { uid ->
            db.collection("users").document(uid)
                .collection("discussions")
                .whereEqualTo("status", "open")
                .get()
                .addOnSuccessListener { result ->
                    val discussions = result.map { doc ->
                        Discussion(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            subject = doc.getString("subject") ?: "",
                            context = doc.getString("context") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            status = doc.getString("status") ?: ""
                        )
                    }
                    adapter.updateData(discussions)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load discussions.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}