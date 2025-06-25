package com.example.edifyhub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.teacherProfileManage.TeacherProfileActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Immediately start TeacherProfileActivity
        val intent = Intent(this, TeacherProfileActivity::class.java)
        startActivity(intent)

        // Optionally close this activity if you don't want it in back stack
        finish()
    }
}
