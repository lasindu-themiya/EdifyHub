package com.example.edifyhub.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import java.text.SimpleDateFormat
import java.util.*

// A sealed class to represent items in the list: either a message or a date header
sealed class ChatListItem {
    data class MessageItem(val message: ChatMessage) : ChatListItem()
    data class DateHeaderItem(val date: String) : ChatListItem()
}

class ChatAdapter(private val currentUserId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItems: List<ChatListItem> = emptyList()

    companion object {
        private const val VIEW_TYPE_OTHER = 0
        private const val VIEW_TYPE_ME = 1
        private const val VIEW_TYPE_DATE_HEADER = 2
    }

    fun submitList(messages: List<ChatMessage>) {
        val items = mutableListOf<ChatListItem>()
        var lastDate: String? = null

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        for (message in messages) {
            val currentDate = dateFormat.format(Date(message.timestamp))
            if (currentDate != lastDate) {
                items.add(ChatListItem.DateHeaderItem(currentDate))
                lastDate = currentDate
            }
            items.add(ChatListItem.MessageItem(message))
        }
        this.listItems = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = listItems[position]) {
            is ChatListItem.DateHeaderItem -> VIEW_TYPE_DATE_HEADER
            is ChatListItem.MessageItem -> {
                if (item.message.userId == currentUserId) VIEW_TYPE_ME else VIEW_TYPE_OTHER
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ME -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_right, parent, false)
                MessageViewHolder(view)
            }
            VIEW_TYPE_OTHER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_left, parent, false)
                MessageViewHolder(view)
            }
            VIEW_TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = listItems[position]) {
            is ChatListItem.MessageItem -> (holder as MessageViewHolder).bind(item.message)
            is ChatListItem.DateHeaderItem -> (holder as DateHeaderViewHolder).bind(item.date)
        }
    }

    override fun getItemCount() = listItems.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvUsername: TextView? = itemView.findViewById(R.id.tvUsername) // Nullable for safety
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(msg: ChatMessage) {
            tvMessage.text = msg.message
            tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
            // Only set username if the view has a username text view and it's not the "You" bubble
            if (tvUsername?.text != "You") {
                tvUsername?.text = msg.username
            }
        }
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDateHeader: TextView = itemView.findViewById(R.id.tvDateHeader)
        fun bind(date: String) {
            tvDateHeader.text = date
        }
    }
}