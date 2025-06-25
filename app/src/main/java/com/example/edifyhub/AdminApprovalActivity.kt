package com.example.edifyhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView

class AdminApprovalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeacherAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerHandler: DrawerMenuHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_approval)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Setup navigation drawer toggle and listener
        drawerHandler = DrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        recyclerView = findViewById(R.id.teacherRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sampleRequests = mutableListOf(
            TeacherSignupRequestModel("1", "John Doe", "john@example.com", "Physics", "ABC Institute"),
            TeacherSignupRequestModel("2", "Jane Smith", "jane@example.com", "Mathematics", "XYZ Academy"),
            TeacherSignupRequestModel("3", "Ayesha Khan", "ayesha@example.com", "Biology", "Brilliant College")
        )

        adapter = TeacherAdapter(
            sampleRequests,
            onApprove = { teacher -> println("✅ Approved: ${teacher.name}") },
            onReject = { teacher -> println("❌ Rejected: ${teacher.name}") }
        )

        recyclerView.adapter = adapter
    }

    override fun onBackPressed() {
        if (!drawerHandler.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
