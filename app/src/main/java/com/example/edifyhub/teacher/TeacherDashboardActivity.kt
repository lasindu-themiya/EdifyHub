package com.example.edifyhub.teacher

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.example.edifyhub.student.StudentQuizListActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class TeacherDashboardActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: TeacherDrawerMenuHandler
    private lateinit var toolbar: Toolbar
    private lateinit var db: FirebaseFirestore
    private var userId: String? = null

    private val monthlyRevenue = DoubleArray(12) { 0.0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dashboard)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("USER_ID")

        toolbar = findViewById(R.id.teacherToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.teacherDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = TeacherDrawerMenuHandler(this, drawerLayout, navigationView, toolbar, userId)

        if (userId != null) {
            fetchMonthlyRevenue(userId!!)
            loadTotalQuizzes(userId!!)
        }

        setupLineChart()

        // Example placeholders
        findViewById<TextView>(R.id.upComingClassesCount).text = "19"

        //create quizzes navigation
        val createQuizzes = findViewById<ImageButton>(R.id.createQuizzes)
        createQuizzes.setOnClickListener {
            val intent = Intent(this, CreateQuizActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }


    }

    private fun fetchMonthlyRevenue(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("quizzes")
            .whereEqualTo("paid", true)
            .get()
            .addOnSuccessListener { quizzes ->
                if (quizzes.isEmpty) {
                    updateRevenueUI()
                    return@addOnSuccessListener
                }

                var processed = 0
                quizzes.forEach { quiz ->
                    val amount = quiz.getLong("amount")?.toDouble() ?: 0.0
                    val quizId = quiz.id

                    db.collection("users")
                        .document(userId)
                        .collection("quizzes")
                        .document(quizId)
                        .collection("attempts")
                        .get()
                        .addOnSuccessListener { attempts ->
                            attempts.forEach { attempt ->
                                val timestamp = attempt.getTimestamp("timestamp")?.toDate()
                                if (timestamp != null) {
                                    val cal = Calendar.getInstance()
                                    cal.time = timestamp
                                    val month = cal.get(Calendar.MONTH) // 0 = Jan

                                    monthlyRevenue[month] += amount
                                }
                            }

                            processed++
                            if (processed == quizzes.size()) {
                                updateRevenueUI()
                            }
                        }
                        .addOnFailureListener {
                            processed++
                            if (processed == quizzes.size()) {
                                updateRevenueUI()
                            }
                        }
                }
            }
    }

    private fun updateRevenueUI() {
        // Update total revenue text
        val total = monthlyRevenue.sum()
        findViewById<TextView>(R.id.teacherTotalRevenue).text = "Rs. ${String.format("%.2f", total)}"

        // Now refresh the chart with real data
        setupLineChart()
    }

    private fun setupLineChart() {
        val lineChart = findViewById<LineChart>(R.id.teacherLineChart)
        val entries = monthlyRevenue.mapIndexed { index, revenue ->
            Entry(index.toFloat(), revenue.toFloat())
        }

        val dataSet = LineDataSet(entries, "Revenue (Rs.)").apply {
            color = getColor(R.color.primary)
            valueTextColor = getColor(R.color.text_primary)
            lineWidth = 3f
            circleRadius = 5f
            setCircleColor(getColor(R.color.primary))
            setDrawFilled(true)
            fillColor = getColor(R.color.primary)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        lineChart.apply {
            data = LineData(dataSet)
            xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            )
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }


    private fun loadTotalQuizzes(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("quizzes")
            .get()
            .addOnSuccessListener { documents ->
                findViewById<TextView>(R.id.totalQuizesCount).text = "${documents.size()}"
            }
            .addOnFailureListener {
                findViewById<TextView>(R.id.totalQuizesCount).text = "0"
            }
    }
}
