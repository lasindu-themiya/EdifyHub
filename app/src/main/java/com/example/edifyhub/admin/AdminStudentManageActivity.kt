package com.example.edifyhub.admin

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edifyhub.R
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminStudentManageActivity : AppCompatActivity() {
    private lateinit var students: MutableList<Student>
    private lateinit var filteredStudents: MutableList<Student>
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_student_manage)

        students = mutableListOf(
            Student("1", "Alice", 18, "0712345678", "Science", "alice@email.com", R.drawable.ic_profile),
            Student("2", "Bob", 19, "0723456789", "Commerce", "bob@email.com", R.drawable.ic_profile),
            Student("3", "Charlie", 18, "0734567890", "Arts", "charlie@email.com", R.drawable.ic_profile),
            Student("4", "Diana", 20, "0745678901", "Science", "diana@email.com", R.drawable.ic_profile),
            Student("5", "Ethan", 17, "0756789012", "Commerce", "ethan@email.com", R.drawable.ic_profile),
            Student("6", "Fiona", 18, "0767890123", "Arts", "fiona@email.com", R.drawable.ic_profile),
            Student("7", "George", 19, "0778901234", "Science", "george@email.com", R.drawable.ic_profile),
            Student("8", "Hannah", 18, "0789012345", "Commerce", "hannah@email.com", R.drawable.ic_profile),
            Student("9", "Ian", 20, "0790123456", "Arts", "ian@email.com", R.drawable.ic_profile),
            Student("10", "Julia", 17, "0701234567", "Science", "julia@email.com", R.drawable.ic_profile),
            Student("11", "Kevin", 18, "0712345671", "Commerce", "kevin@email.com", R.drawable.ic_profile),
            Student("12", "Laura", 19, "0723456782", "Arts", "laura@email.com", R.drawable.ic_profile),
            Student("13", "Mike", 18, "0734567893", "Science", "mike@email.com", R.drawable.ic_profile),
            Student("14", "Nina", 20, "0745678904", "Commerce", "nina@email.com", R.drawable.ic_profile),
            Student("15", "Oscar", 17, "0756789015", "Arts", "oscar@email.com", R.drawable.ic_profile),
            Student("16", "Paula", 18, "0767890126", "Science", "paula@email.com", R.drawable.ic_profile),
            Student("17", "Quentin", 19, "0778901237", "Commerce", "quentin@email.com", R.drawable.ic_profile),
            Student("18", "Rachel", 18, "0789012348", "Arts", "rachel@email.com", R.drawable.ic_profile),
            Student("19", "Sam", 20, "0790123459", "Science", "sam@email.com", R.drawable.ic_profile),
            Student("20", "Tina", 17, "0701234560", "Commerce", "tina@email.com", R.drawable.ic_profile)
        )

        filteredStudents = students.toMutableList()

        val rvStudents = findViewById<RecyclerView>(R.id.rvStudents)
        adapter = StudentAdapter(
            filteredStudents,
            onStudentClick = { student -> showStudentDialog(student) },
            onEditClick = { student -> showEditFragment(student) },
            onDeleteClick = { student -> confirmDelete(student) }
        )
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter

        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                filteredStudents.clear()
                if (query.isEmpty()) {
                    filteredStudents.addAll(students)
                } else {
                    filteredStudents.addAll(
                        students.filter { it.name.lowercase().contains(query) }
                    )
                }
                adapter.notifyDataSetChanged()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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
            .commit()
        findViewById<android.view.View>(R.id.fragmentContainer).visibility = android.view.View.VISIBLE
    }

    private fun confirmDelete(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.name}?")
            .setPositiveButton("Delete") { dialog, _ ->
                students.remove(student)
                filteredStudents.remove(student)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}