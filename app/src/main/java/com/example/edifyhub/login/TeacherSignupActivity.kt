package com.example.edifyhub.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.edifyhub.R
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.databinding.ActivityTeacherSignupBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TeacherSignupActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityTeacherSignupBinding
    private  lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeacherSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        binding.teachersignupbtn.setOnClickListener{

            val username = binding.teacherusername.text.toString()
            val about = binding.teacherabout.text.toString()
            val institute = binding.institute.text.toString()
            val subject = binding.subject.text.toString()
            val email = binding.teacheremail.text.toString()
            val password = binding.teacherpassword.text.toString()
            val rePassword = binding.teacherrepassword.text.toString()
            val userRole = "teacher".toString()
            val status = "pending".toString()

            if(username.isNotEmpty() && about.isNotEmpty() && institute.isNotEmpty() && subject.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty()){

                if(password == rePassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
                        if(task.isSuccessful){

                            val userID = task.result?.user?.uid

                            val user = hashMapOf(
                                "username" to username,
                                "about Qualifications" to about,
                                "institute" to institute,
                                "subject" to subject,
                                "email" to email,
                                "userRole" to userRole,
                                "status" to status
                            )

                            if(userID != null){
                                db.collection("users").document(userID).set(user).addOnSuccessListener {

                                    Toast.makeText(this, "Sign Up Successfull! Wait for approval.", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)

                                }.addOnFailureListener{ e ->

                                    Toast.makeText(this, "Firestore error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }


                }else{
                    Toast.makeText(this, "Password is not matching!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }

        }


        //directing to sign in page
        binding.signInLink.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}