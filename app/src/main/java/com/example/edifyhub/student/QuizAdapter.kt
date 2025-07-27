package com.example.edifyhub.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import java.text.SimpleDateFormat
import java.util.*

class QuizAdapter(
    private val onAttendClick: (QuizItem) -> Unit,
    private val onViewQuizClick: (QuizItem) -> Unit,
    private val onPayClick: (QuizItem) -> Unit,
    private val attemptedQuizIds: Set<String>,
    private val paidQuizIds: Set<String>
) : ListAdapter<QuizItem, QuizAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position), onAttendClick, onViewQuizClick, onPayClick, attemptedQuizIds, paidQuizIds)
    }

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quizName: TextView = itemView.findViewById(R.id.quizName)
        private val subject: TextView = itemView.findViewById(R.id.quizSubject)
        private val teacher: TextView = itemView.findViewById(R.id.quizTeacher)
        private val scheduled: TextView = itemView.findViewById(R.id.quizScheduled)
        private val meeting: TextView = itemView.findViewById(R.id.quizMeeting)
        private val amount: TextView = itemView.findViewById(R.id.quizAmount)
        private val btnAttend: Button = itemView.findViewById(R.id.btnAttend)

        fun bind(
            item: QuizItem,
            onAttendClick: (QuizItem) -> Unit,
            onViewQuizClick: (QuizItem) -> Unit,
            onPayClick: (QuizItem) -> Unit,
            attemptedQuizIds: Set<String>,
            paidQuizIds: Set<String>
        ) {
            quizName.text = item.name
            subject.text = "Subject: ${item.subject}"
            teacher.text = "Teacher: ${item.teacherName}"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            scheduled.text = "Scheduled: ${sdf.format(item.scheduledAt)}"
            meeting.text = "Meeting: ${sdf.format(item.meetingAt)}"
            amount.text = if (item.amount == 0.0) "Free" else "Rs. ${item.amount}"

            val now = Date()
            val calendar = Calendar.getInstance()
            calendar.time = item.scheduledAt
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val endDate = calendar.time
            val canAttend = now >= item.scheduledAt && now <= endDate

            when {
                attemptedQuizIds.contains(item.id) -> {
                    btnAttend.text = "View Quiz"
                    btnAttend.isEnabled = true
                    btnAttend.setOnClickListener { onViewQuizClick(item) }
                }
                item.amount > 0.0 && !paidQuizIds.contains(item.id) -> {
                    btnAttend.text = "Pay"
                    btnAttend.isEnabled = true
                    btnAttend.setOnClickListener { onPayClick(item) }
                }
                else -> {
                    btnAttend.text = "Attend"
                    btnAttend.isEnabled = canAttend
                    btnAttend.setOnClickListener {
                        if (btnAttend.isEnabled) onAttendClick(item)
                    }
                }
            }
        }
    }
}