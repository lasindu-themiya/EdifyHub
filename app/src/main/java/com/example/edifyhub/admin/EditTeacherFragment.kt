package com.example.edifyhub.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

class EditTeacherFragment(
    private val teacher: Teacher,
    private val onTeacherUpdated: (Teacher) -> Unit
) : DialogFragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_teacher, container, false)

        val etName = view.findViewById<EditText>(R.id.etTeacherName)
        val etSubject = view.findViewById<EditText>(R.id.etTeacherSubject)
        val etEmail = view.findViewById<EditText>(R.id.etTeacherEmail)
        val btnSave = view.findViewById<Button>(R.id.btnSaveTeacher)

        etName.setText(teacher.name)
        etSubject.setText(teacher.subject)
        etEmail.setText(teacher.email)

        btnSave.setOnClickListener {
            val updatedTeacher = teacher.copy(
                name = etName.text.toString(),
                subject = etSubject.text.toString(),
                email = etEmail.text.toString(),
            )
            updateTeacherInFirestore(updatedTeacher)
        }

        return view
    }

    private fun updateTeacherInFirestore(updatedTeacher: Teacher) {
        db.collection("users").document(updatedTeacher.id)
            .update(
                mapOf(
                    "username" to updatedTeacher.name,
                    "subject" to updatedTeacher.subject,
                    "email" to updatedTeacher.email,
                )
            )
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Teacher updated", Toast.LENGTH_SHORT).show()
                onTeacherUpdated(updatedTeacher)
                dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}