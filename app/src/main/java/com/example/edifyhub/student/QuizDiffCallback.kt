import androidx.recyclerview.widget.DiffUtil
import com.example.edifyhub.student.QuizItem

class QuizDiffCallback : DiffUtil.ItemCallback<QuizItem>() {
    override fun areItemsTheSame(oldItem: QuizItem, newItem: QuizItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: QuizItem, newItem: QuizItem) = oldItem == newItem
}