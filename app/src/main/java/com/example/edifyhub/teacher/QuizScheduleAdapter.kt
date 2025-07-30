package com.example.edifyhub.teacher

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore
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
        private val btnHostTeamsWeb: Button = view.findViewById(R.id.btnHostTeamsWeb)
        private val tvMeetingLink: TextView = view.findViewById(R.id.tvMeetingLink)

        fun bind(quiz: Quiz, dateFormat: SimpleDateFormat, teacherId: String?) {
            // 1. Bind quiz data
            name.text = quiz.name
            subject.text = quiz.subject
            numQuestions.text = "Questions: ${quiz.numQuestions}"
            numAnswers.text = "Answers per Q: ${quiz.numAnswers}"
            paid.text = "Paid: ${if (quiz.paid) "Yes" else "No"}"
            amount.text = "Amount: ${quiz.amount}"
            createdAt.text = "Created: ${quiz.createdAt?.let { dateFormat.format(it) } ?: "N/A"}"
            scheduledAt.text = "Scheduled: ${quiz.scheduledAt?.let { dateFormat.format(it) } ?: "N/A"}"
            meetingAt.text = "Meeting: ${quiz.meetingAt?.let { dateFormat.format(it) } ?: "N/A"}"

            // 2. Attendees button
            btnViewAttendees.setOnClickListener {
                val fragment = QuizAttendeesFragment.newInstance(quiz.id, teacherId ?: "")
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            // 3. Dynamic button and meeting link logic
            if (!quiz.meetingJoinUrl.isNullOrEmpty()) {
                // Show meeting link text and set button as "Join Meeting"
                tvMeetingLink.visibility = View.VISIBLE
                tvMeetingLink.text = "Meeting Link: ${quiz.meetingJoinUrl}"
                tvMeetingLink.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(quiz.meetingJoinUrl))
                    itemView.context.startActivity(intent)
                }
                btnHostTeamsWeb.text = "Join Meeting"
                btnHostTeamsWeb.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(quiz.meetingJoinUrl))
                    itemView.context.startActivity(intent)
                }
            } else {
                // Hide meeting link and set button as "Schedule Meeting"
                tvMeetingLink.visibility = View.GONE
                btnHostTeamsWeb.text = "Schedule Meeting"
                btnHostTeamsWeb.setOnClickListener {
                    // a. Open Teams meeting creator in browser
                    val subject = Uri.encode(quiz.name ?: "Quiz Meeting")
                    val url = "https://teams.microsoft.com/l/meeting/new?subject=$subject"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    try { itemView.context.startActivity(intent) }
                    catch (e: Exception) {
                        intent.setPackage(null)
                        itemView.context.startActivity(intent)
                    }

                    // b. Prompt to paste meeting link after user creates it
                    val editText = EditText(itemView.context)
                    editText.hint = "Paste Teams meeting link here"
                    editText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_URI

                    AlertDialog.Builder(itemView.context)
                        .setTitle("Paste Meeting Link")
                        .setMessage("After creating the meeting in Teams, copy the meeting link and paste it below.")
                        .setView(editText)
                        .setPositiveButton("Save") { _, _ ->
                            val link = editText.text.toString().trim()
                            if (link.isNotEmpty() && teacherId != null) {
                                // c. Save meeting link to Firestore
                                val db = FirebaseFirestore.getInstance()
                                db.collection("users").document(teacherId)
                                    .collection("quizzes").document(quiz.id)
                                    .update(mapOf("meetingJoinUrl" to link))
                                    .addOnSuccessListener {
                                        Toast.makeText(itemView.context, "Meeting link saved!", Toast.LENGTH_SHORT).show()
                                        tvMeetingLink.text = "Meeting Link: $link"
                                        tvMeetingLink.visibility = View.VISIBLE
                                        btnHostTeamsWeb.text = "Join Meeting"
                                        btnHostTeamsWeb.setOnClickListener {
                                            val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                            itemView.context.startActivity(intent2)
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(itemView.context, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }
    }
}