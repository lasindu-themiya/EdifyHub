package com.example.edifyhub.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.databinding.ActivityStudentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentSignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentSignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // ✅ Fetch Streams from Firestore dynamically
        db.collection("streams")
            .get()
            .addOnSuccessListener { result ->
                val streamList = mutableListOf<String>()
                streamList.add("Select a stream") // optional default

                for (document in result) {
                    streamList.add(document.id) // your stream name is the doc ID
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, streamList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.stream.adapter = adapter

            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to load streams: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        binding.signupbtn.setOnClickListener {

            val username = binding.username.text.toString()
            val age = binding.age.text.toString()
            val mobile = binding.mobile.text.toString()
            val stream = binding.stream.selectedItem.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val repassword = binding.repassword.text.toString()
            val userRole = "student"

            if (stream == "Select a stream") {
                Toast.makeText(this, "Please select a valid A/L Stream!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isNotEmpty() && age.isNotEmpty() && mobile.isNotEmpty() &&
                stream.isNotEmpty() && email.isNotEmpty() &&
                password.isNotEmpty() && repassword.isNotEmpty()
            ) {
                if (password == repassword) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val userId = task.result?.user?.uid
                                Log.d("DEBUG", "UserID: $userId")

                                val user = hashMapOf(
                                    "username" to username,
                                    "age" to age,
                                    "mobile" to mobile,
                                    "stream" to stream,
                                    "email" to email,
                                    "userRole" to userRole
                                )

                                if (userId != null) {
                                    db.collection("users").document(userId).set(user)
                                        .addOnSuccessListener {
                                            Log.d("DEBUG", "Firestore write successful!")
                                            Toast.makeText(
                                                this,
                                                "Signup successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()

                                        }.addOnFailureListener { e ->
                                            Log.e("DEBUG", "Firestore write failed: ${e.message}")
                                            Toast.makeText(
                                                this,
                                                "Firestore error: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                } else {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signInLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
