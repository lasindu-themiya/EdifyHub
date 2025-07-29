package com.example.edifyhub.student

import StudentDrawerMenuHandler
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.example.edifyhub.login.StudentSignupActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerHandler: StudentDrawerMenuHandler

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        db = FirebaseFirestore.getInstance()

        // Retrieve user ID from intent
        userId = intent.getStringExtra("USER_ID")

        toolbar = findViewById(R.id.studentToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.studentDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = StudentDrawerMenuHandler(this, drawerLayout, navigationView, toolbar)


//        val completedQuizzes = 64
        val upComingQuizzes = 27
        val postedDiscussions = 30

//        findViewById<TextView>(R.id.completedQuizzes).text = completedQuizzes.toString()
        findViewById<TextView>(R.id.upComingQuizzes).text = upComingQuizzes.toString()
        findViewById<TextView>(R.id.postedDiscussions).text = postedDiscussions.toString()


        if(userId != null){
            db.collection("users").document(userId!!).get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists()){
                        val username = document.getString("username") ?: "student"
                        findViewById<TextView>(R.id.username).text = "Hello, $username"
                    }else{
                        findViewById<TextView>(R.id.username).text = "Hello, Student"
                    }
                }
                .addOnFailureListener { exception ->
                    findViewById<TextView>(R.id.username).text = "Hello, Student"
                }
        }

        // get completed quizzes
        if (userId != null) {
            userId?.let { safeUserId ->
                db.collection("users")
                    .document(safeUserId)
                    .collection("attemptedQuizzes")
                    .get()
                    .addOnSuccessListener { documents ->
                        val total = documents.size()
                        findViewById<TextView>(R.id.completedQuizzes).text = "$total"
                    }
                    .addOnFailureListener {
                        findViewById<TextView>(R.id.completedQuizzes).text = "0"
                    }
                }
        }



        val searchQuizzes = findViewById<ImageButton>(R.id.searchQuizzes)
        searchQuizzes.setOnClickListener {
            val intent = Intent(this, StudentQuizListActivity::class.java)
            // Pass userId to next activity if needed
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }
}