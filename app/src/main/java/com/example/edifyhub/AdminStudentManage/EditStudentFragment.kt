package com.example.edifyhub.AdminStudentManage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.edifyhub.R

class EditStudentFragment : Fragment() {
    private lateinit var student: Student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
            // Update student details
            student.name = etName.text.toString()
            student.age = etAge.text.toString().toIntOrNull() ?: student.age
            student.mobile = etMobile.text.toString()
            student.stream = etStream.text.toString()
            student.email = etEmail.text.toString()
            // Optionally notify activity/adapter
            requireActivity().findViewById<View>(R.id.fragmentContainer).visibility = View.GONE
        }

        return view
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