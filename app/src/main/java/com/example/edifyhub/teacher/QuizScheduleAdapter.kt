package com.example.edifyhub.teacher

import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import java.text.SimpleDateFormat
import java.util.*

class QuizScheduleAdapter(private val teacherId: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<QuizScheduleItem>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun submitList(newItems: List<QuizScheduleItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is QuizScheduleItem.Header -> 0
            is QuizScheduleItem.QuizItem -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_quiz, parent, false)
            QuizViewHolder(view)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is QuizScheduleItem.Header -> (holder as HeaderViewHolder).bind(item.title)
            is QuizScheduleItem.QuizItem -> (holder as QuizViewHolder).bind(item.quiz, dateFormat, teacherId)
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.header_title)
        fun bind(text: String) {
            title.text = text
        }
    }

    class QuizViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.quiz_name)
        private val subject: TextView = view.findViewById(R.id.quiz_subject)
        private val numQuestions: TextView = view.findViewById(R.id.quiz_num_questions)
        private val numAnswers: TextView = view.findViewById(R.id.quiz_num_answers)
        private val paid: TextView = view.findViewById(R.id.quiz_paid)
        private val amount: TextView = view.findViewById(R.id.quiz_amount)
        private val createdAt: TextView = view.findViewById(R.id.quiz_created_at)
        private val scheduledAt: TextView = view.findViewById(R.id.quiz_scheduled_at)
        private val meetingAt: TextView = view.findViewById(R.id.quiz_meeting_at)
        private val btnViewAttendees: Button = view.findViewById(R.id.btnViewAttendees)

        fun bind(quiz: Quiz, dateFormat: SimpleDateFormat, teacherId: String?) {
            name.text = quiz.name
            subject.text = quiz.subject
            numQuestions.text = "Questions: ${quiz.numQuestions}"
            numAnswers.text = "Answers per Q: ${quiz.numAnswers}"
            paid.text = "Paid: ${if (quiz.paid) "Yes" else "No"}"
            amount.text = "Amount: ${quiz.amount}"
            createdAt.text = "Created: ${quiz.createdAt?.let { dateFormat.format(it) } ?: "N/A"}"
            scheduledAt.text = "Scheduled: ${quiz.scheduledAt?.let { dateFormat.format(it) } ?: "N/A"}"
            meetingAt.text = "Meeting: ${quiz.meetingAt?.let { dateFormat.format(it) } ?: "N/A"}"

            btnViewAttendees.setOnClickListener {
                val fragment = QuizAttendeesFragment.newInstance(quiz.id, teacherId ?: "")
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}