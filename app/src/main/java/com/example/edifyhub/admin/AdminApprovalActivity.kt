package com.example.edifyhub.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.example.edifyhub.email.EmailSender
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

                val subject = "Your EdifyHub Application is Approved"
                val body = "Dear ${teacher.name},\n\nCongratulations! Your application to become a teacher on EdifyHub Education Platform has been Successfully approved. You can now log in and start using the platform.\n\nBest regards,\nThe EdifyHub Team"
                EmailSender.sendEmail(teacher.email, subject, body)

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

                val subject = "Update on your EdifyHub Application"
                val body = "Dear ${teacher.name},\n\nThank you for your interest in EdifyHub. After careful consideration, we regret to inform you that we cannot proceed with your application at this time, Please be kind enough to try again in later times.\nIf you are unsure about this decision please be kind enough to contact lasinduthemiya96@gmail.com for reconsideration.\n\nBest regards,\nThe EdifyHub Team"
                EmailSender.sendEmail(teacher.email, subject, body)

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