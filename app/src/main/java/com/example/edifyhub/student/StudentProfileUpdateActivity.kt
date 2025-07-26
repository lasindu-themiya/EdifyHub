package com.example.edifyhub.student

import StudentDrawerMenuHandler
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentProfileUpdateActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerHandler: StudentDrawerMenuHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile_update)

        toolbar = findViewById(R.id.studentToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.studentDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = StudentDrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        val imageProfile = findViewById<ImageView>(R.id.imageProfile)
        val btnEditProfilePic = findViewById<ImageView>(R.id.btnEditProfilePic)
        val etName = findViewById<EditText>(R.id.etName)
        val etAge = findViewById<EditText>(R.id.etAge)
        val etMobile = findViewById<EditText>(R.id.etMobile)
        val etALStream = findViewById<EditText>(R.id.etALStream)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        val userId = intent.getStringExtra("USER_ID")
        val db = FirebaseFirestore.getInstance()
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        btnSave.setOnClickListener {
            val username = etName.text.toString()
            val age = etAge.text.toString()
            val mobile = etMobile.text.toString()
            val stream = etALStream.text.toString()
            val userRole = "student"
            val email = firebaseUser?.email ?: ""

            if (username.isNotEmpty() && age.isNotEmpty() && mobile.isNotEmpty() && stream.isNotEmpty()) {
                if (userId != null) {
                    val user = hashMapOf(
                        "username" to username,
                        "age" to age,
                        "mobile" to mobile,
                        "stream" to stream,
                        "userRole" to userRole,
                        "email" to email
                    )
                    db.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, StudentDashboardActivity::class.java)
                            intent.putExtra("USER_ID", userId)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }

        // TODO: Add logic for editing profile picture if needed
    }
}

