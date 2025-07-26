package com.example.edifyhub.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class AdminApprovalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeacherAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerHandler: DrawerMenuHandler
    private val db = FirebaseFirestore.getInstance()
    private val teacherList = mutableListOf<TeacherSignupRequestModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_approval)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = DrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        recyclerView = findViewById(R.id.teacherRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TeacherAdapter(
            teacherList,
            onApprove = { teacher -> approveTeacher(teacher) },
            onReject = { teacher -> rejectTeacher(teacher) }
        )
        recyclerView.adapter = adapter

        fetchPendingTeachers()
    }

    private fun fetchPendingTeachers() {
        db.collection("users")
            .whereEqualTo("userRole", "teacher")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { result ->
                teacherList.clear()
                for (doc in result) {
                    val teacher = TeacherSignupRequestModel(
                        id = doc.id,
                        name = doc.getString("username") ?: "",
                        email = doc.getString("email") ?: "",
                        subject = doc.getString("subject") ?: "",
                        institute = doc.getString("institute") ?: ""
                    )
                    teacherList.add(teacher)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun approveTeacher(teacher: TeacherSignupRequestModel) {
        db.collection("users").document(teacher.id)
            .update("status", "approved")
            .addOnSuccessListener {
                Toast.makeText(this, "Approved: ${teacher.name}", Toast.LENGTH_SHORT).show()
                fetchPendingTeachers()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun rejectTeacher(teacher: TeacherSignupRequestModel) {
        db.collection("users").document(teacher.id)
            .update("status", "rejected")
            .addOnSuccessListener {
                Toast.makeText(this, "Rejected: ${teacher.name}", Toast.LENGTH_SHORT).show()
                fetchPendingTeachers()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        if (!drawerHandler.onBackPressed()) {
            super.onBackPressed()
        }
    }
}