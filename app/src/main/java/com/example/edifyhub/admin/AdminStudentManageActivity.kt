package com.example.edifyhub.admin

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edifyhub.R
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.edifyhub.email.EmailSender
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class AdminStudentManageActivity : AppCompatActivity() {
    private lateinit var students: MutableList<Student>
    private lateinit var filteredStudents: MutableList<Student>
    private lateinit var adapter: StudentAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: DrawerMenuHandler
    private lateinit var toolbar: Toolbar
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_student_manage)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = DrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        students = mutableListOf()
        filteredStudents = mutableListOf()

        setupRecyclerView()
        setupSearch()

        fetchStudents()
    }

    private fun setupRecyclerView() {
        val rvStudents = findViewById<RecyclerView>(R.id.rvStudents)
        adapter = StudentAdapter(
            filteredStudents,
            onStudentClick = { student -> showStudentDialog(student) },
            onEditClick = { student -> showEditFragment(student) },
            onDeleteClick = { student -> confirmDelete(student) }
        )
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter
    }

    private fun fetchStudents() {
        db.collection("users")
            .whereEqualTo("userRole", "student")
            .get()
            .addOnSuccessListener { result ->
                students.clear()
                for (document in result) {
                    val student = Student(
                        id = document.id,
                        name = document.getString("username") ?: "",
                        age = document.getString("age")?.toIntOrNull() ?: 0,
                        mobile = document.getString("mobile") ?: "",
                        stream = document.getString("stream") ?: "",
                        email = document.getString("email") ?: ""
                    )
                    students.add(student)
                }
                filterStudents("")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching students: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterStudents(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterStudents(query: String) {
        val lowerCaseQuery = query.lowercase().trim()
        filteredStudents.clear()
        if (lowerCaseQuery.isEmpty()) {
            filteredStudents.addAll(students)
        } else {
            filteredStudents.addAll(
                students.filter { it.name.lowercase().contains(lowerCaseQuery) }
            )
        }
        adapter.notifyDataSetChanged()
    }

    private fun showStudentDialog(student: Student) {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_student_details, null)
        dialog.setContentView(view)

        view.findViewById<ImageView>(R.id.imgProfile).setImageResource(student.profilePicRes)
        view.findViewById<TextView>(R.id.tvName).text = student.name
        view.findViewById<TextView>(R.id.tvAge).text = "Age: ${student.age}"
        view.findViewById<TextView>(R.id.tvMobile).text = "Mobile: ${student.mobile}"
        view.findViewById<TextView>(R.id.tvStream).text = "AL Stream: ${student.stream}"
        view.findViewById<TextView>(R.id.tvEmail).text = "Email: ${student.email}"

        dialog.show()
    }

    private fun showEditFragment(student: Student) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, EditStudentFragment.newInstance(student))
            .addToBackStack(null)
            .commit()
        findViewById<android.view.View>(R.id.fragmentContainer).visibility = android.view.View.VISIBLE
    }

    private fun confirmDelete(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.name}? This will permanently remove their data and access.")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteStudent(student)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteStudent(student: Student) {

        db.collection("users").document(student.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "${student.name} has been deleted.", Toast.LENGTH_SHORT).show()

                val subject = "Account Deletion from EdifyHub"
                val body = "Dear ${student.name},\n\nThis email is to inform you that your account has been removed from the EdifyHub platform by an administrator. You will no longer be able to make any interaction with the platform.\nIf you aren't willing to accept this kindly contact system administration lasinduthemiya96@gmail.com.\n\nThank you,\nThe EdifyHub Team"
                EmailSender.sendEmail(student.email, subject, body)

                fetchStudents()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting student: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun onStudentUpdated() {
        supportFragmentManager.popBackStack()
        findViewById<android.view.View>(R.id.fragmentContainer).visibility = android.view.View.GONE
        fetchStudents()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            onStudentUpdated()
        } else if (!drawerHandler.onBackPressed()) {
            super.onBackPressed()
        }
    }
}