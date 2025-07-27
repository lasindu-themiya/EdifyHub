import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.example.edifyhub.student.QuizItem
import java.text.SimpleDateFormat
import java.util.*

class QuizAdapter : ListAdapter<QuizItem, QuizAdapter.QuizViewHolder>(QuizDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quizName: TextView = itemView.findViewById(R.id.quizName)
        private val subject: TextView = itemView.findViewById(R.id.quizSubject)
        private val teacher: TextView = itemView.findViewById(R.id.quizTeacher)
        private val scheduled: TextView = itemView.findViewById(R.id.quizScheduled)
        private val meeting: TextView = itemView.findViewById(R.id.quizMeeting)
        private val amount: TextView = itemView.findViewById(R.id.quizAmount)

        fun bind(item: QuizItem) {
            quizName.text = item.name
            subject.text = "Subject: ${item.subject}"
            teacher.text = "Teacher: ${item.teacherName}"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            scheduled.text = "Scheduled: ${sdf.format(item.scheduledAt)}"
            meeting.text = "Meeting: ${sdf.format(item.meetingAt)}"
            amount.text = if (item.amount == 0.0) "Free" else "Rs. ${item.amount}"
        }
    }
}