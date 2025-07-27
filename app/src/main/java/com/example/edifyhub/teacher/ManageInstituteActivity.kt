package com.example.edifyhub.teacher

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class ManageInstituteActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etDescription: EditText
    private lateinit var etLocation: EditText
    private lateinit var etContact: EditText
    private lateinit var btnSave: MaterialButton

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: TeacherDrawerMenuHandler
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_institute)

        toolbar = findViewById(R.id.teacherToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.teacherDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = TeacherDrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        etName = findViewById(R.id.etInstituteName)
        etAddress = findViewById(R.id.etInstituteAddress)
        etDescription = findViewById(R.id.etInstituteDescription)
        etLocation = findViewById(R.id.etInstituteLocation)
        etContact = findViewById(R.id.etInstituteContact)
        btnSave = findViewById(R.id.btnSaveInstitute)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val address = etAddress.text.toString()
            val description = etDescription.text.toString()
            val location = etLocation.text.toString()
            val contact = etContact.text.toString()

            if (name.isEmpty() || address.isEmpty() || description.isEmpty()
                || location.isEmpty() || contact.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Institute details saved!", Toast.LENGTH_SHORT).show()
                // TODO: Save data to DB or Firebase
                finish()
            }
        }
    }
}