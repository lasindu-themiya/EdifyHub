package com.example.edifyhub.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

class EditStudentFragment : Fragment() {
    private lateinit var student: Student
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the student object passed from the activity
        arguments?.let {
            // Note: Using getSerializable is deprecated. For new code, consider using Parcelable.
            @Suppress("DEPRECATION")
            student = it.getSerializable("student") as Student
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_student, container, false)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etAge = view.findViewById<EditText>(R.id.etAge)
        val etMobile = view.findViewById<EditText>(R.id.etMobile)
        val etStream = view.findViewById<EditText>(R.id.etStream)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        etName.setText(student.name)
        etAge.setText(student.age.toString())
        etMobile.setText(student.mobile)
        etStream.setText(student.stream)
        etEmail.setText(student.email)

        btnSave.setOnClickListener {
            updateStudentDetails(
                etName.text.toString(),
                etAge.text.toString(),
                etMobile.text.toString(),
                etStream.text.toString(),
                etEmail.text.toString()
            )
        }

        return view
    }

    private fun updateStudentDetails(name: String, age: String, mobile: String, stream: String, email: String) {
        val ageInt = age.toIntOrNull()

        if (name.isBlank() || ageInt == null) {
            Toast.makeText(context, "Name and a valid Age are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mapOf(
            "username" to name,
            "age" to age,
            "mobile" to mobile,
            "stream" to stream,
            "email" to email
        )

        db.collection("users").document(student.id)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(context, "Student details updated successfully.", Toast.LENGTH_SHORT).show()
                (activity as? AdminStudentManageActivity)?.onStudentUpdated()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error updating details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(student: Student): EditStudentFragment {
            val fragment = EditStudentFragment()
            val args = Bundle()
            args.putSerializable("student", student)
            fragment.arguments = args
            return fragment
        }
    }
}