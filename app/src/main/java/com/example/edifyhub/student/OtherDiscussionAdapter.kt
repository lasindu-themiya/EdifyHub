package com.example.edifyhub.student

import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.edifyhub.R

class OtherDiscussionAdapter(
    private var discussions: List<OtherDiscussion>,
    private val onChatClick: (String, String) -> Unit
) : RecyclerView.Adapter<OtherDiscussionAdapter.ViewHolder>() {

    fun updateData(newList: List<OtherDiscussion>) {
        discussions = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_other_discussion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val discussion = discussions[position]
        holder.tvName.text = discussion.name
        holder.tvSubject.text = discussion.subject
        holder.tvContext.text = discussion.context
        Glide.with(holder.itemView.context)
            .load(discussion.imageUrl)
            .placeholder(R.drawable.placeholder)
            .into(holder.ivImage)
        holder.btnChat.setOnClickListener { onChatClick(discussion.userId, discussion.id) }
    }

    override fun getItemCount() = discussions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        val tvContext: TextView = itemView.findViewById(R.id.tvContext)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val btnChat: Button = itemView.findViewById(R.id.btnChat)
    }
}