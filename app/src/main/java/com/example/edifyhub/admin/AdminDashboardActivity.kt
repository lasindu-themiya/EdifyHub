package com.example.edifyhub.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: DrawerMenuHandler
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        drawerHandler = DrawerMenuHandler(this, drawerLayout, navigationView, toolbar)


        // Example data
        val studentCount = 120
        val teacherCount = 15
        val monthlyRevenue = 250000

        findViewById<TextView>(R.id.tvStudentCount).text = studentCount.toString()
        findViewById<TextView>(R.id.tvTeacherCount).text = teacherCount.toString()
        findViewById<TextView>(R.id.tvMonthlyRevenue).text = "Rs. $monthlyRevenue"

        setupLineChart()
    }

    private fun setupLineChart() {
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        val entries = listOf(
            Entry(1f, 20000f),
            Entry(2f, 30000f),
            Entry(3f, 25000f),
            Entry(4f, 40000f),
            Entry(5f, 35000f),
            Entry(6f, 50000f),
            Entry(7f, 45000f),
            Entry(8f, 60000f),
            Entry(9f, 55000f),
            Entry(10f, 70000f),
            Entry(11f, 65000f),
            Entry(12f, 80000f)
        )
        val dataSet = LineDataSet(entries, "Revenue (Rs.)")
        dataSet.color = getColor(R.color.primary)
        dataSet.valueTextColor = getColor(R.color.text_primary)
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f
        dataSet.setCircleColor(getColor(R.color.primary))
        dataSet.setDrawFilled(true)
        dataSet.fillColor = getColor(R.color.primary)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        lineChart.xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(months)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.animateY(1000)
        lineChart.invalidate()
    }
}