package com.example.edifyhub.login

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.edifyhub.R
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.edifyhub.admin.AdminDashboardActivity
import com.example.edifyhub.databinding.ActivityLoginBinding
import com.example.edifyhub.student.StudentDashboardActivity
import com.example.edifyhub.teacher.TeacherDashboardActivity
import com.example.edifyhub.passwordReset.EnterEmailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        //forget password page
        binding.forgetUserPassword.setOnClickListener{
            val intent = Intent(this, EnterEmailActivity::class.java)
            startActivity(intent)
        }


        //teacher signup page
        binding.teacherSignUp.setOnClickListener {
            val intent = Intent(this, TeacherSignupActivity::class.java)
            startActivity(intent)
        }

        //student signup page
        binding.studentSignUp.setOnClickListener{
            val intent = Intent(this, StudentSignupActivity::class.java)
            startActivity(intent)
        }


        binding.signinbtn.setOnClickListener {
            val email = binding.loginusername.text.toString()
            val password = binding.loginpassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful) {

                        val userId = firebaseAuth.currentUser?.uid

                        if (userId != null) {
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->

                                    if (document != null && document.exists()) {
                                        val role = document.getString("userRole")
                                        val status = document.getString("status")
                                        when (role) {
                                            "admin" -> {
                                                val intent =
                                                    Intent(this, AdminDashboardActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }

                                            "teacher" -> {
                                                if(status == "pending"){
                                                    Toast.makeText(this, "Wait for approval", Toast.LENGTH_SHORT).show()
                                                }else{
                                                    val intent = Intent(
                                                        this,
                                                        TeacherDashboardActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "student" -> {
                                                val intent = Intent(
                                                    this,
                                                    StudentDashboardActivity::class.java
                                                )
                                                startActivity(intent)
                                                finish()
                                            }

                                            else -> {
                                                Toast.makeText(
                                                    this,
                                                    "Unknown user role",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "User not exists! please Sign Up.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }.addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error fetching user role: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        } else {
                            Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }
}