package com.example.edifyhub.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.databinding.ActivityTeacherSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TeacherSignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherSignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var streamAdapter: ArrayAdapter<String>
    private lateinit var subjectAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // ✅ Empty adapters with default
        streamAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf("Select Stream"))
        streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stream.adapter = streamAdapter

        subjectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf("Select Subject"))
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.subject.adapter = subjectAdapter

        // ✅ Fetch Streams first
        fetchStreams()

        // ✅ When stream selected -> fetch subjects (array field!)
        binding.stream.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStream = parent.getItemAtPosition(position).toString()
                if (selectedStream != "Select Stream") {
                    fetchSubjects(selectedStream)
                } else {
                    subjectAdapter.clear()
                    subjectAdapter.add("Select Subject")
                    subjectAdapter.notifyDataSetChanged()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // ✅ Signup button
        binding.teachersignupbtn.setOnClickListener {
            val username = binding.teacherusername.text.toString()
            val about = binding.teacherabout.text.toString()
            val institute = binding.institute.text.toString()
            val stream = binding.stream.selectedItem.toString()
            val subject = binding.subject.selectedItem.toString()
            val email = binding.teacheremail.text.toString()
            val password = binding.teacherpassword.text.toString()
            val rePassword = binding.teacherrepassword.text.toString()
            val userRole = "teacher"
            val status = "pending"

            if (username.isNotEmpty() && about.isNotEmpty() && institute.isNotEmpty()
                && stream != "Select Stream" && subject != "Select Subject"
                && email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty()
            ) {
                if (password == rePassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userID = task.result?.user?.uid
                                val user = hashMapOf(
                                    "username" to username,
                                    "about Qualifications" to about,
                                    "institute" to institute,
                                    "stream" to stream,
                                    "subject" to subject,
                                    "email" to email,
                                    "userRole" to userRole,
                                    "status" to status
                                )
                                if (userID != null) {
                                    db.collection("users").document(userID).set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "Sign Up Successful! Wait for approval.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Firestore error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields correctly!", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ Sign in link
        binding.signInLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // ✅ Uses your array field!
    private fun fetchStreams() {
        db.collection("streams").get()
            .addOnSuccessListener { result ->
                val streams = mutableListOf("Select Stream")
                for (doc in result) {
                    streams.add(doc.id)
                }
                streamAdapter.clear()
                streamAdapter.addAll(streams)
                streamAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch streams!", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ Uses array field instead of subcollection
    private fun fetchSubjects(stream: String) {
        db.collection("streams").document(stream).get()
            .addOnSuccessListener { doc ->
                val subjects = mutableListOf("Select Subject")
                if (doc != null && doc.exists()) {
                    val array = doc.get("subjects") as? List<*>
                    array?.forEach {
                        subjects.add(it.toString())
                    }
                }
                subjectAdapter.clear()
                subjectAdapter.addAll(subjects)
                subjectAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch subjects!", Toast.LENGTH_SHORT).show()
            }
    }
}
