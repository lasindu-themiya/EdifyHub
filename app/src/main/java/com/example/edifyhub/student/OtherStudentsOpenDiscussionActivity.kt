package com.example.edifyhub.student

import StudentDrawerMenuHandler
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView

class OtherStudentsOpenDiscussionActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerHandler: StudentDrawerMenuHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_students_open_discussion)

        toolbar = findViewById(R.id.studentToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Other Students' Open Discussions"

        drawerLayout = findViewById(R.id.studentDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = StudentDrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        if (savedInstanceState == null) {
            val loggedInUserId = intent.getStringExtra("USER_ID")
            val fragment = OtherStudentsOpenDiscussionFragment().apply {
                arguments = Bundle().apply {
                    putString("USER_ID", loggedInUserId)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}