package com.example.edifyhub.student

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import java.text.SimpleDateFormat
import java.util.*

class AttendedQuizAdapter(
    private val onViewQuiz: (AttendedQuiz) -> Unit
) : RecyclerView.Adapter<AttendedQuizAdapter.AttendedQuizViewHolder>() {

    private val items = mutableListOf<AttendedQuiz>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun submitList(list: List<AttendedQuiz>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendedQuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attended_quiz, parent, false)
        return AttendedQuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendedQuizViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class AttendedQuizViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvQuizName: TextView = view.findViewById(R.id.tvQuizName)
        private val tvSubject: TextView = view.findViewById(R.id.tvSubject)
        private val tvTeacherName: TextView = view.findViewById(R.id.tvTeacherName)
        private val tvScore: TextView = view.findViewById(R.id.tvScore)
        private val tvDate: TextView = view.findViewById(R.id.tvDate)
        private val btnViewInfo: Button = view.findViewById(R.id.btnViewInfo)
        private val btnJoinMeeting: Button = view.findViewById(R.id.btnJoinMeeting)
        private val tvMeetingAt: TextView = view.findViewById(R.id.tvMeetingAt)
        private val tvMeetingLink: TextView = view.findViewById(R.id.tvMeetingLink)

        fun bind(quiz: AttendedQuiz) {
            tvQuizName.text = quiz.quizName
            tvSubject.text = "Subject: ${quiz.subject}"
            tvTeacherName.text = "By: ${quiz.teacherName}"
            tvScore.text = "Score: ${quiz.score}/${quiz.total}"
            tvDate.text = quiz.timestamp?.toDate()?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "Date: N/A"

            if (quiz.meetingAt != null) {
                tvMeetingAt.visibility = View.VISIBLE
                tvMeetingAt.text = "Meeting At: ${dateFormat.format(quiz.meetingAt)}"
            } else {
                tvMeetingAt.visibility = View.GONE
            }

            tvMeetingLink.visibility = View.GONE

            if (!quiz.meetingJoinUrl.isNullOrEmpty()) {
                btnJoinMeeting.visibility = View.VISIBLE

                val now = System.currentTimeMillis()
                val meetingTime = quiz.meetingAt?.time ?: 0L
                val oneHourMillis = 60 * 60 * 1000L

                when {
                    now < meetingTime -> {
                        btnJoinMeeting.setBackgroundColor(
                            btnJoinMeeting.context.getColor(android.R.color.holo_red_dark)
                        )
                        btnJoinMeeting.text = "Join Meeting"
                        btnJoinMeeting.setOnClickListener {
                            Toast.makeText(it.context, "Meeting not started yet!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    now in meetingTime until (meetingTime + oneHourMillis) -> {
                        btnJoinMeeting.setBackgroundColor(
                            btnJoinMeeting.context.getColor(R.color.primary)
                        )
                        btnJoinMeeting.text = "Join Meeting"
                        btnJoinMeeting.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(quiz.meetingJoinUrl))
                            it.context.startActivity(intent)
                        }
                    }
                    now >= meetingTime + oneHourMillis -> {
                        btnJoinMeeting.setBackgroundColor(
                            btnJoinMeeting.context.getColor(android.R.color.holo_red_dark)
                        )
                        btnJoinMeeting.text = "Meeting Finished"
                        btnJoinMeeting.setOnClickListener {
                            Toast.makeText(it.context, "The meeting has already finished.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                btnJoinMeeting.visibility = View.GONE
            }

            btnViewInfo.setOnClickListener {
                onViewQuiz(quiz)
            }
        }
    }
}