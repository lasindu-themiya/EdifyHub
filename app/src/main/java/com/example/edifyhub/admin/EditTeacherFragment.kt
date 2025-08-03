package com.example.edifyhub.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.edifyhub.R

class EditTeacherFragment : DialogFragment() {

    private var teacher: Teacher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            teacher = it.getSerializable("teacher") as? Teacher
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_teacher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etTeacherName)
        val etSubject = view.findViewById<EditText>(R.id.etTeacherSubject)
        val etEmail = view.findViewById<EditText>(R.id.etTeacherEmail)
        val btnSave = view.findViewById<Button>(R.id.btnSaveTeacher)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        teacher?.let {
            etName.setText(it.name)
            etSubject.setText(it.subject)
            etEmail.setText(it.email)
        }

        btnSave.setOnClickListener {
            val updatedName = etName.text.toString()
            val updatedSubject = etSubject.text.toString()
            val updatedEmail = etEmail.text.toString()

            if (updatedName.isNotEmpty() && updatedSubject.isNotEmpty()) {
                val updatedTeacher = teacher?.copy(name = updatedName, subject = updatedSubject, email = updatedEmail)
                if (updatedTeacher != null) {
                    setFragmentResult("editTeacherRequest", bundleOf("updatedTeacher" to updatedTeacher))
                }
                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(teacher: Teacher): EditTeacherFragment {
            val fragment = EditTeacherFragment()
            val args = Bundle()
            args.putSerializable("teacher", teacher)
            fragment.arguments = args
            return fragment
        }
    }
}