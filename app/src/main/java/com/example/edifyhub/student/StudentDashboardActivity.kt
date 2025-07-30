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

        // get completed quizzes & success rate
        if (userId != null) {
            userId?.let { safeUserId ->
                val attemptedQuizzesRef = db.collection("users")
                    .document(safeUserId)
                    .collection("attemptedQuizzes")

                val discussionRef = db.collection("users")
                    .document(safeUserId)
                    .collection("discussions")

                attemptedQuizzesRef.get()
                    .addOnSuccessListener { documents ->
                        val totalDocs = documents.size()
                        var sumOfAverages = 0.0

                        for (doc in documents) {
                            val score = doc.getLong("score")?.toDouble() ?: 0.0
                            val total = doc.getLong("total")?.toDouble() ?: 1.0 // avoid divide by zero

                            val quizAverage = score / total
                            val cappedAverage = minOf(quizAverage, 1.0) // cap at 100% if needed

                            sumOfAverages += cappedAverage
                        }

                        val finalAverage = if (totalDocs > 0) sumOfAverages / totalDocs else 0.0
                        val percentage = finalAverage * 100

                        // completed quizzes
                        findViewById<TextView>(R.id.completedQuizzes).text = "$totalDocs"

                        // success rate
                        val successRateText = "${String.format("%.2f", percentage)}%"
                        findViewById<TextView>(R.id.rate).text = successRateText

                    }
                    .addOnFailureListener {
                        findViewById<TextView>(R.id.completedQuizzes).text = "0"
                        findViewById<TextView>(R.id.rate).text = "0.00%"
                    }


                discussionRef.get()
                    .addOnSuccessListener { discussionDocs ->
                        val totalDiscussions = discussionDocs.size()
                        findViewById<TextView>(R.id.postedDiscussions).text = "$totalDiscussions"
                    }
                    .addOnFailureListener {
                        findViewById<TextView>(R.id.postedDiscussions).text = "0"
                    }

            }
        }


        //search quizzes navigation
        val searchQuizzes = findViewById<ImageButton>(R.id.searchQuizzes)
        searchQuizzes.setOnClickListener {
            val intent = Intent(this, StudentQuizListActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        //create discussion navigation
        val createDiscussion = findViewById<ImageButton>(R.id.searchDiscussions)
        createDiscussion.setOnClickListener{
            val intent = Intent(this, StudentCreateDiscussionActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

    }
}