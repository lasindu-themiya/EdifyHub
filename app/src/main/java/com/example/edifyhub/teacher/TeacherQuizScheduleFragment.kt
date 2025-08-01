package com.example.edifyhub.teacher

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.view.MotionEvent
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class TeacherQuizScheduleFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuizScheduleAdapter
    private val allItems = mutableListOf<QuizScheduleItem>()
    private var userId: String? = null
    private var selectedDate: Date? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("userId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_teacher_quiz_schedule, container, false)
        recyclerView = root.findViewById(R.id.quiz_schedule_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuizScheduleAdapter(userId)
        recyclerView.adapter = adapter

        val searchView = root.findViewById<SearchView>(R.id.searchViewDate)
        val searchEditText = searchView.findViewById<android.widget.EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(resources.getColor(R.color.primary, null))
        searchEditText.setHintTextColor(resources.getColor(R.color.text_primary, null))

        searchView.isFocusable = false
        searchView.isFocusableInTouchMode = false
        searchView.setIconifiedByDefault(false)
        searchView.clearFocus()
        searchView.setOnQueryTextFocusChangeListener { _, _ -> searchView.clearFocus() }

        // Prevent keyboard and double events
        searchEditText.isFocusable = false
        searchEditText.isFocusableInTouchMode = false

        searchEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val cal = Calendar.getInstance()
                DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    cal.set(year, month, dayOfMonth, 0, 0, 0)
                    selectedDate = cal.time
                    searchView.setQuery(dateFormat.format(selectedDate!!), false)
                    filterAndSortQuizzes()
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                return@setOnTouchListener true
            }
            false
        }

        searchView.setOnTouchListener(null)
        searchView.setOnClickListener(null)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        fetchQuizzes()
        return root
    }

    override fun onResume() {
        super.onResume()
        fetchQuizzes()
    }

    private fun fetchQuizzes() {
        if (userId == null) return
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId!!)
            .collection("quizzes")
            .orderBy("scheduledAt", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val now = Calendar.getInstance()
                val today = now.clone() as Calendar
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                today.set(Calendar.MILLISECOND, 0)
                val tomorrow = today.clone() as Calendar
                tomorrow.add(Calendar.DATE, 1)

                val past = mutableListOf<Quiz>()
                val present = mutableListOf<Quiz>()
                val upcoming = mutableListOf<Quiz>()

                for (doc in result) {
                    val quiz = Quiz(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        subject = doc.getString("subject") ?: "",
                        numQuestions = doc.getLong("numQuestions")?.toInt() ?: 0,
                        numAnswers = doc.getLong("numAnswers")?.toInt() ?: 0,
                        paid = doc.getBoolean("paid") ?: false,
                        amount = doc.getDouble("amount"),
                        createdAt = doc.getTimestamp("createdAt")?.toDate(),
                        scheduledAt = doc.getTimestamp("scheduledAt")?.toDate(),
                        meetingAt = doc.getTimestamp("meetingAt")?.toDate(),
                        meetingJoinUrl = doc.getString("meetingJoinUrl") ?: "",
                    )
                    val sched = quiz.scheduledAt
                    if (sched != null) {
                        when {
                            sched.before(today.time) -> past.add(quiz)
                            sched.after(today.time) && sched.before(tomorrow.time) -> present.add(quiz)
                            sched.after(tomorrow.time) -> upcoming.add(quiz)
                        }
                    } else {
                        upcoming.add(quiz)
                    }
                }

                allItems.clear()
                if (present.isNotEmpty()) {
                    allItems.add(QuizScheduleItem.Header("Present"))
                    allItems.addAll(present.map { QuizScheduleItem.QuizItem(it) })
                }
                if (upcoming.isNotEmpty()) {
                    allItems.add(QuizScheduleItem.Header("Upcoming"))
                    allItems.addAll(upcoming.map { QuizScheduleItem.QuizItem(it) })
                }
                if (past.isNotEmpty()) {
                    allItems.add(QuizScheduleItem.Header("Past"))
                    allItems.addAll(past.map { QuizScheduleItem.QuizItem(it) })
                }
                adapter.submitList(allItems.toList())
            }
    }

    private fun filterAndSortQuizzes() {
        if (selectedDate == null) {
            adapter.submitList(allItems.toList())
            return
        }
        val filtered = allItems.filter {
            it is QuizScheduleItem.QuizItem &&
                    (it.quiz.meetingAt != null && dateFormat.format(it.quiz.meetingAt!!) == dateFormat.format(selectedDate!!))
        }.sortedBy {
            (it as QuizScheduleItem.QuizItem).quiz.meetingAt
        }
        val result = mutableListOf<QuizScheduleItem>()
        if (filtered.isNotEmpty()) {
            result.add(QuizScheduleItem.Header("Filtered by ${dateFormat.format(selectedDate!!)} Meetings"))
            result.addAll(filtered)
        }
        adapter.submitList(result)
    }
}