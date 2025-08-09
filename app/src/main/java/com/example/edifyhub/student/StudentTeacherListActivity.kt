package com.example.edifyhub.student

import StudentDrawerMenuHandler
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView

class StudentTeacherListActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_teacher_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        val userId = intent.getStringExtra("USER_ID")
        StudentDrawerMenuHandler(this, drawerLayout, navView, toolbar)

        if (savedInstanceState == null) {
            val fragment = StudentTeacherListFragment().apply {
                arguments = Bundle().apply {
                    putString("USER_ID", userId)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}