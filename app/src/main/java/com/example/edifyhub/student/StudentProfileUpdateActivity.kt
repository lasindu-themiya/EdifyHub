package com.example.edifyhub.student

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R

class StudentProfileUpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile_update)

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