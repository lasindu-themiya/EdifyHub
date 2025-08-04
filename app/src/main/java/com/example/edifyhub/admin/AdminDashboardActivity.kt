package com.example.edifyhub.admin

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: DrawerMenuHandler
    private lateinit var toolbar: Toolbar
    private lateinit var lineChart: LineChart

    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        lineChart = findViewById(R.id.lineChart)
        drawerHandler = DrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        // Fetch the new dynamic data instead of setting static text
        fetchPendingApprovalsCount()
        fetchUserCounts()
        setupTeacherStreamChart()
    }

    private fun fetchPendingApprovalsCount() {
        val tvPendingApprovals = findViewById<TextView>(R.id.tvPendingApprovalsCount) // Use the new ID

        db.collection("users")
            .whereEqualTo("userRole", "teacher")
            .whereEqualTo("status", "pending") // Assuming "pending" is the status for new teachers
            .get()
            .addOnSuccessListener { result ->
                tvPendingApprovals.text = result.size().toString()
            }
            .addOnFailureListener { e ->
                Log.w("AdminDashboard", "Error getting pending approvals count.", e)
                tvPendingApprovals.text = "0"
            }
    }

    private fun fetchUserCounts() {
        val tvStudentCount = findViewById<TextView>(R.id.tvStudentCount)
        val tvTeacherCount = findViewById<TextView>(R.id.tvTeacherCount)

        db.collection("users")
            .whereEqualTo("userRole", "student")
            .get()
            .addOnSuccessListener { result ->
                tvStudentCount.text = result.size().toString()
            }

        db.collection("users")
            .whereEqualTo("userRole", "teacher")
            .get()
            .addOnSuccessListener { result ->
                tvTeacherCount.text = result.size().toString()
            }
    }

    private fun setupTeacherStreamChart() {
        scope.launch {
            try {
                val streamDocs = db.collection("streams").get().await()
                val allStreams = streamDocs.documents.map { it.id }.sorted()

                if (allStreams.isEmpty()) {
                    Log.d("AdminDashboard", "No streams found in the 'streams' collection.")
                    return@launch
                }

                val teacherDocs = db.collection("users")
                    .whereEqualTo("userRole", "teacher")
                    .get()
                    .await()

                val teacherStreamCounts = teacherDocs.documents
                    .mapNotNull { it.getString("stream") }
                    .groupBy { it }
                    .mapValues { it.value.size }

                val entries = mutableListOf<Entry>()
                val labels = mutableListOf<String>()

                allStreams.forEachIndexed { index, streamName ->
                    val count = teacherStreamCounts[streamName]?.toFloat() ?: 0f
                    entries.add(Entry(index.toFloat(), count))
                    labels.add(streamName)
                }

                val dataSet = LineDataSet(entries, "Teachers per Stream")
                configureDataSet(dataSet)

                val lineData = LineData(dataSet)

                withContext(Dispatchers.Main) {
                    configureChart(labels)
                    lineChart.data = lineData
                    lineChart.invalidate()
                    lineChart.animateY(1200)
                }

            } catch (e: Exception) {
                Log.e("AdminDashboard", "Error setting up teacher stream chart", e)
            }
        }
    }

    private fun configureDataSet(dataSet: LineDataSet) {
        dataSet.color = ContextCompat.getColor(this, R.color.primary)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.text_primary)
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 5f
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.primary))
        dataSet.valueTextSize = 12f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = ContextCompat.getColor(this, R.color.primary)
        dataSet.fillAlpha = 80
        dataSet.mode = LineDataSet.Mode.LINEAR
    }

    private fun configureChart(labels: List<String>) {
        lineChart.description.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.legend.isEnabled = true
        lineChart.legend.textSize = 12f

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setLabelCount(labels.size, false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < labels.size) labels[index] else ""
            }
        }
        xAxis.textColor = ContextCompat.getColor(this, R.color.text_secondary)
        xAxis.textSize = 11f

        val yAxis = lineChart.axisLeft
        yAxis.granularity = 1f
        yAxis.axisMinimum = 0f
        yAxis.textColor = ContextCompat.getColor(this, R.color.text_secondary)
    }
}