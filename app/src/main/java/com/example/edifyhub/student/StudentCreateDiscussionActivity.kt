package com.example.edifyhub.student

import StudentDrawerMenuHandler
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView

class StudentCreateDiscussionActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerHandler: StudentDrawerMenuHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_create_discussion)

        toolbar = findViewById(R.id.studentToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Create Discussion"

        drawerLayout = findViewById(R.id.studentDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = StudentDrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        // Load fragment only if not already loaded
        if (savedInstanceState == null) {
            val userId = intent.getStringExtra("USER_ID")
            val fragment = StudentCreateDiscussionFragment().apply {
                arguments = Bundle().apply {
                    putString("USER_ID", userId)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}