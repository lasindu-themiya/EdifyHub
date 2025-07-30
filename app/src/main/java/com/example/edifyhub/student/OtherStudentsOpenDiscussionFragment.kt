package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore
import android.view.inputmethod.InputMethodManager
import android.content.Context

class OtherStudentsOpenDiscussionFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OtherDiscussionAdapter
    private var loggedInUserId: String? = null
    private var allDiscussions: List<OtherDiscussion> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_other_students_open_discussion, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewOtherDiscussions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OtherDiscussionAdapter(emptyList(), onChatClick = { userId, discussionId -> openChat(userId, discussionId) })
        recyclerView.adapter = adapter

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewSubject)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (newText.isNullOrBlank()) {
                    allDiscussions
                } else {
                    allDiscussions.filter { it.subject.contains(newText, ignoreCase = true) }
                }
                adapter.updateData(filtered)
                return true
            }
        })
        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchView.findViewById(androidx.appcompat.R.id.search_src_text), InputMethodManager.SHOW_IMPLICIT)
        }

        loggedInUserId = arguments?.getString("USER_ID")
        fetchOtherDiscussions()
        return view
    }

    private fun fetchOtherDiscussions() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").get()
            .addOnSuccessListener { usersSnapshot ->
                val discussions = mutableListOf<OtherDiscussion>()
                val userDocs = usersSnapshot.documents.filter { it.id != loggedInUserId }
                if (userDocs.isEmpty()) {
                    allDiscussions = emptyList()
                    adapter.updateData(emptyList())
                    return@addOnSuccessListener
                }
                var loadedCount = 0
                for (userDoc in userDocs) {
                    val userId = userDoc.id
                    db.collection("users").document(userId)
                        .collection("discussions")
                        .whereEqualTo("status", "open")
                        .get()
                        .addOnSuccessListener { discussionsSnapshot ->
                            for (doc in discussionsSnapshot) {
                                discussions.add(
                                    OtherDiscussion(
                                        id = doc.id,
                                        userId = userId,
                                        name = doc.getString("name") ?: "",
                                        subject = doc.getString("subject") ?: "",
                                        context = doc.getString("context") ?: "",
                                        imageUrl = doc.getString("imageUrl") ?: "",
                                        status = doc.getString("status") ?: ""
                                    )
                                )
                            }
                            loadedCount++
                            if (loadedCount == userDocs.size) {
                                allDiscussions = discussions
                                adapter.updateData(discussions)
                            }
                        }
                        .addOnFailureListener {
                            loadedCount++
                            if (loadedCount == userDocs.size) {
                                allDiscussions = discussions
                                adapter.updateData(discussions)
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load discussions.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openChat(ownerUserId: String, discussionId: String) {
        val fragment = StudentDiscussionChatFragment().apply {
            arguments = Bundle().apply {
                putString("USER_ID", ownerUserId)
                putString("DISCUSSION_ID", discussionId)
                putString("LOGGED_IN_USER_ID", loggedInUserId)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}