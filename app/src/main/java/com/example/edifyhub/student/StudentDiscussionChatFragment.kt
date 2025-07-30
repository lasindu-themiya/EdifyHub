package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

data class ChatMessage(
    val userId: String = "",
    val username: String = "",
    val message: String = "",
    val timestamp: Long = 0
)

class StudentDiscussionChatFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var dbRef: DatabaseReference

    private var discussionId: String? = null
    private var ownerUserId: String? = null
    private var loggedInUserId: String? = null
    private var username: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_student_discussion_chat, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewChat)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)

        discussionId = arguments?.getString("DISCUSSION_ID")
        ownerUserId = arguments?.getString("USER_ID") // discussion owner
        loggedInUserId = arguments?.getString("LOGGED_IN_USER_ID") // current user

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        dbRef = FirebaseDatabase.getInstance().getReference("chats").child(discussionId ?: "")

        fetchUsernameAndInit()
        return view
    }

    private fun fetchUsernameAndInit() {
        if (loggedInUserId == null) return
        FirebaseFirestore.getInstance().collection("users").document(loggedInUserId!!)
            .get()
            .addOnSuccessListener { doc ->
                username = doc.getString("username") ?: "Unknown"
                adapter = ChatAdapter(loggedInUserId!!) // always use logged-in user for sender
                recyclerView.adapter = adapter
                btnSend.setOnClickListener { sendMessage() }
                listenForMessages()
            }
    }

    private fun sendMessage() {
        val msg = etMessage.text.toString().trim()
        if (msg.isNotEmpty() && loggedInUserId != null && username != null) {
            val chatMsg = ChatMessage(
                userId = loggedInUserId!!,
                username = username!!,
                message = msg,
                timestamp = System.currentTimeMillis()
            )
            dbRef.push().setValue(chatMsg)
            etMessage.text.clear()
        }
    }

    private fun listenForMessages() {
        dbRef.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (child in snapshot.children) {
                    child.getValue(ChatMessage::class.java)?.let { messages.add(it) }
                }
                adapter.submitList(messages)
                recyclerView.scrollToPosition(messages.size - 1)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}