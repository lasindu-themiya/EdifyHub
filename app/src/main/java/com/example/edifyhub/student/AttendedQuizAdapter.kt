package com.example.edifyhub.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R

class AttendedQuizAdapter(
    private val onViewInfoClick: (AttendedQuiz) -> Unit
) : ListAdapter<AttendedQuiz, AttendedQuizAdapter.AttendedQuizViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendedQuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attended_quiz, parent, false)
        return AttendedQuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendedQuizViewHolder, position: Int) {
        holder.bind(getItem(position), onViewInfoClick)
    }

    class AttendedQuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: AttendedQuiz, onViewInfoClick: (AttendedQuiz) -> Unit) {
            itemView.findViewById<TextView>(R.id.tvQuizName).text = item.quizName
            itemView.findViewById<TextView>(R.id.tvTeacherName).text = "Teacher: ${item.teacherName}"
            itemView.findViewById<TextView>(R.id.tvSubject).text = "Subject: ${item.subject}"
            itemView.findViewById<TextView>(R.id.tvScore).text = "Score: ${item.score}/${item.total}"
            item.timestamp?.let {
                itemView.findViewById<TextView>(R.id.tvDate).text = it.toDate().toString()
            }
            itemView.findViewById<Button>(R.id.btnViewInfo).setOnClickListener {
                onViewInfoClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AttendedQuiz>() {
        override fun areItemsTheSame(oldItem: AttendedQuiz, newItem: AttendedQuiz) =
            oldItem.quizId == newItem.quizId

        override fun areContentsTheSame(oldItem: AttendedQuiz, newItem: AttendedQuiz) =
            oldItem == newItem
    }
}