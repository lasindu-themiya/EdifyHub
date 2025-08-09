package com.example.edifyhub.student

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
    private lateinit var ivDiscussionImage: ImageView
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
        ivDiscussionImage = view.findViewById(R.id.ivDiscussionImage)

        discussionId = arguments?.getString("DISCUSSION_ID")
        ownerUserId = arguments?.getString("USER_ID") // discussion owner
        loggedInUserId = arguments?.getString("LOGGED_IN_USER_ID") // current user

        if (discussionId == null || ownerUserId == null || loggedInUserId == null) {
            Toast.makeText(context, "Error: Missing discussion data.", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return view
        }

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        dbRef = FirebaseDatabase.getInstance().getReference("chats").child(discussionId!!)

        fetchDiscussionImage()
        fetchUsernameAndInit()
        return view
    }

    private fun fetchDiscussionImage() {
        FirebaseFirestore.getInstance()
            .collection("users").document(ownerUserId!!)
            .collection("discussions").document(discussionId!!)
            .get()
            .addOnSuccessListener { document ->
                val imageUrl = document.getString("imageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .centerCrop()
                        .into(ivDiscussionImage)
                    ivDiscussionImage.visibility = View.VISIBLE
                }
            }
    }

    private fun fetchUsernameAndInit() {
        FirebaseFirestore.getInstance().collection("users").document(loggedInUserId!!)
            .get()
            .addOnSuccessListener { doc ->
                username = doc.getString("username") ?: "Unknown"
                adapter = ChatAdapter(loggedInUserId!!)
                recyclerView.adapter = adapter
                btnSend.setOnClickListener { sendMessage() }
                listenForMessages()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load user data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendMessage() {
        val msg = etMessage.text.toString().trim()
        if (msg.isNotEmpty()) {
            val chatMsg = ChatMessage(
                userId = loggedInUserId!!,
                username = username!!,
                message = msg,
                timestamp = System.currentTimeMillis()
            )
            dbRef.push().setValue(chatMsg)
                .addOnSuccessListener {
                    etMessage.text.clear()
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to send message.", Toast.LENGTH_SHORT).show()
                }
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
                if (messages.isNotEmpty()) {
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load messages.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}