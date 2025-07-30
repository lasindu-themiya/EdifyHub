package com.example.edifyhub.student

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

class DiscussionAdapter(
    private val userId: String,
    private var discussions: List<Discussion>,
    private val onRefresh: () -> Unit
) : RecyclerView.Adapter<DiscussionAdapter.DiscussionViewHolder>() {

    inner class DiscussionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        val tvContext: TextView = itemView.findViewById(R.id.tvContext)
        val btnViewImage: Button = itemView.findViewById(R.id.btnViewImage)
        val btnCloseDiscussion: Button = itemView.findViewById(R.id.btnCloseDiscussion)
        val btnChat: Button = itemView.findViewById(R.id.btnChat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_open_discussion_card, parent, false)
        return DiscussionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        val discussion = discussions[position]
        holder.tvName.text = discussion.name
        holder.tvSubject.text = discussion.subject
        holder.tvContext.text = discussion.context

        holder.btnViewImage.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.dialog_view_image, null)
            val imageView = dialogView.findViewById<ImageView>(R.id.dialogImageView)
            Glide.with(holder.itemView.context).load(discussion.imageUrl).centerCrop().into(imageView)

            AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show()
        }

        holder.btnCloseDiscussion.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Close Discussion")
                .setMessage("Are you sure you want to close this discussion?")
                .setPositiveButton("Yes") { _, _ ->
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(userId)
                        .collection("discussions").document(discussion.id)
                        .update("status", "close")
                        .addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "Discussion closed.", Toast.LENGTH_SHORT).show()
                            onRefresh()
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Chat button: no listener for now
    }

    override fun getItemCount(): Int = discussions.size

    fun updateData(newDiscussions: List<Discussion>) {
        discussions = newDiscussions
        notifyDataSetChanged()
    }
}