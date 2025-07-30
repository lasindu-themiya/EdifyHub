package com.example.edifyhub.student

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val currentUserId: String) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var messages: List<ChatMessage> = emptyList()

    fun submitList(list: List<ChatMessage>) {
        messages = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].userId == currentUserId) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 1) R.layout.item_chat_right else R.layout.item_chat_left
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(msg: ChatMessage) {
            tvMessage.text = msg.message
            tvUsername.text = msg.username
            tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
        }
    }
}