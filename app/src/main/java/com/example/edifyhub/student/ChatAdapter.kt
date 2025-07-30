package com.example.edifyhub.student

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val currentUserId: String) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var messages: List<ChatMessage> = emptyList()
    private var currentUsername: String? = null

    init {
        fetchUsernameById(currentUserId) { username ->
            currentUsername = username
            notifyDataSetChanged()
        }
    }

    fun submitList(list: List<ChatMessage>) {
        messages = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val msg = messages[position]
        return if (currentUsername != null && msg.username == currentUsername) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 1) R.layout.item_chat_left else R.layout.item_chat_left
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position], currentUsername)
    }

    override fun getItemCount() = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(msg: ChatMessage, currentUsername: String?) {
            tvMessage.text = msg.message
            tvUsername.text = msg.username
            tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
            if (currentUsername != null && msg.username == currentUsername) {
                tvUsername.setTextColor(itemView.context.getColor(R.color.primary))
            } else {
                tvUsername.setTextColor(itemView.context.getColor(R.color.text_primary))
            }
        }
    }

    fun fetchUsernameById(userId: String, callback: (String?) -> Unit) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                callback(doc.getString("username"))
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}