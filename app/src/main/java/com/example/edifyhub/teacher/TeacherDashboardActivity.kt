package com.example.edifyhub.teacher

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView

class TeacherDashboardActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: TeacherDrawerMenuHandler
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dashboard)

        // ✅ Fix toolbar and drawer IDs to match layout
        toolbar = findViewById(R.id.teacherToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.teacherDrawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // ✅ Assuming TeacherDrawerMenuHandler is implemented properly
        drawerHandler = TeacherDrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        // Sample data setup
        val quizeCount = 70
        val classesCount = 19
        val monthlyRevenue = 230500

        findViewById<TextView>(R.id.totalQuizesCount).text = quizeCount.toString()
        findViewById<TextView>(R.id.upComingClassesCount).text = classesCount.toString()
        findViewById<TextView>(R.id.teacherTotalRevenue).text = "Rs. $monthlyRevenue"

        setupLineChart()
    }

    private fun setupLineChart() {
        val lineChart = findViewById<LineChart>(R.id.teacherLineChart)
        val entries = listOf(
            Entry(0f, 20500f),
            Entry(1f, 30000f),
            Entry(2f, 25000f),
            Entry(3f, 40000f),
            Entry(4f, 35000f),
            Entry(5f, 50000f),
            Entry(6f, 45000f),
            Entry(7f, 60000f),
            Entry(8f, 55000f),
            Entry(9f, 70000f),
            Entry(10f, 65000f),
            Entry(11f, 80000f)
        )
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
}
