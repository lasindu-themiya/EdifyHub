package com.example.edifyhub.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class AdminTeacherManageActivity : AppCompatActivity() {
    private lateinit var teachers: MutableList<Teacher>
    private lateinit var adapter: TeacherAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: DrawerMenuHandler
    private lateinit var toolbar: Toolbar
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_teacher_manage)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = DrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        teachers = mutableListOf()
        setupRecyclerView()
        fetchTeachers()
    }

    private fun setupRecyclerView() {
        val rvTeachers = findViewById<RecyclerView>(R.id.rvTeachers)
        adapter = TeacherAdapter(
            teachers,
            onEdit = { teacher -> showEditFragment(teacher) },
            onReject = { teacher -> confirmReject(teacher) }
        )
        rvTeachers.layoutManager = LinearLayoutManager(this)
        rvTeachers.adapter = adapter
    }

    private fun fetchTeachers() {
        db.collection("users")
            .whereEqualTo("userRole", "teacher")
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { result ->
                teachers.clear()
                for (document in result) {
                    val teacher = Teacher(
                        id = document.id,
                        name = document.getString("username") ?: "",
                        subject = document.getString("subject") ?: "",
                        email = document.getString("email") ?: "",
                        status = document.getString("status") ?: "approved",
                        profilePicRes = R.drawable.ic_profile
                    )
                    teachers.add(teacher)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching teachers: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditFragment(teacher: Teacher) {
        // Hide the list
        findViewById<RecyclerView>(R.id.rvTeachers).visibility = View.GONE
        findViewById<View>(R.id.fragmentContainer).visibility = View.VISIBLE

        val fragment = EditTeacherFragment(teacher) { updatedTeacher ->
            // Update the list and refresh UI
            val index = teachers.indexOfFirst { it.id == updatedTeacher.id }
            if (index != -1) {
                teachers[index] = updatedTeacher
                adapter.notifyItemChanged(index)
            }
            // Hide fragment and show list
            supportFragmentManager.beginTransaction()
                .remove(supportFragmentManager.findFragmentById(R.id.fragmentContainer)!!)
                .commit()
            findViewById<View>(R.id.fragmentContainer).visibility = View.GONE
            findViewById<RecyclerView>(R.id.rvTeachers).visibility = View.VISIBLE
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun confirmReject(teacher: Teacher) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Reject Teacher")
            .setMessage("Are you sure you want to reject ${teacher.name}?")
            .setPositiveButton("Reject") { dialog, _ ->
                rejectTeacher(teacher)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun rejectTeacher(teacher: Teacher) {
        AdminApprovalActivity.rejectTeacherAdminManage(
            context = this,
            teacherId = teacher.id,
            teacherEmail = teacher.email,
            teacherName = teacher.name,
            onSuccess = {
                Toast.makeText(this, "${teacher.name} has been rejected.", Toast.LENGTH_SHORT).show()
                fetchTeachers()
            },
            onFailure = { e ->
                Toast.makeText(this, "Error rejecting teacher: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}