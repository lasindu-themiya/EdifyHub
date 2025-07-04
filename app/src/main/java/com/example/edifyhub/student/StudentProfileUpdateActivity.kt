package com.example.edifyhub.student

import StudentDrawerMenuHandler
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView

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

        // TODO: Add logic for editing profile picture and saving profile
    }
}