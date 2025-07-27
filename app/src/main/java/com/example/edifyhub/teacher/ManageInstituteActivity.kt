package com.example.edifyhub.teacher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ManageInstituteActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etDescription: EditText
    private lateinit var etContact: EditText
    private lateinit var btnSave: MaterialButton

    private lateinit var tvSelectedLocation: TextView
    private lateinit var btnPickLocation: MaterialButton

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: TeacherDrawerMenuHandler
    private lateinit var toolbar: Toolbar
    private var userId: String? = null

    private lateinit var db: FirebaseFirestore

    private var selectedLat: Double? = null
    private var selectedLng: Double? = null
    private var selectedAddress: String? = null

    companion object {
        private const val REQUEST_LOCATION_PICK = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_institute)

        toolbar = findViewById(R.id.teacherToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.teacherDrawerLayout)
        navigationView = findViewById(R.id.navigationView)

        userId = intent.getStringExtra("USER_ID")
        drawerHandler = TeacherDrawerMenuHandler(this, drawerLayout, navigationView, toolbar, userId)

        etName = findViewById(R.id.etInstituteName)
        etAddress = findViewById(R.id.etInstituteAddress)
        etDescription = findViewById(R.id.etInstituteDescription)
        etContact = findViewById(R.id.etInstituteContact)
        btnSave = findViewById(R.id.btnSaveInstitute)

        tvSelectedLocation = findViewById(R.id.tvSelectedLocation)
        btnPickLocation = findViewById(R.id.btnPickLocation)

        db = FirebaseFirestore.getInstance()

        // Fetch and pre-fill institute name from teacher profile
        if (userId != null) {
            db.collection("users").document(userId!!)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val instituteName = doc.getString("institute") ?: ""
                        etName.setText(instituteName)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch institute: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnPickLocation.setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            startActivityForResult(intent, REQUEST_LOCATION_PICK)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val address = etAddress.text.toString()
            val description = etDescription.text.toString()
            val contact = etContact.text.toString()

            if (name.isEmpty() || address.isEmpty() || description.isEmpty()
                || contact.isEmpty() || selectedLat == null || selectedLng == null || selectedAddress.isNullOrEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields and pick a location", Toast.LENGTH_SHORT).show()
            } else if (userId == null) {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            } else {
                val instituteDetails = mapOf(
                    "name" to name,
                    "address" to address,
                    "description" to description,
                    "contact" to contact,
                    "location" to mapOf(
                        "latitude" to selectedLat,
                        "longitude" to selectedLng,
                        "address" to selectedAddress
                    )
                )
                // Update both instituteDetails and root institute attribute
                db.collection("users").document(userId!!)
                    .update(
                        mapOf(
                            "instituteDetails" to instituteDetails,
                            "institute" to name
                        )
                    )
                    .addOnSuccessListener {
                        Toast.makeText(this, "Institute details updated!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedLat = data.getDoubleExtra("latitude", 0.0)
            selectedLng = data.getDoubleExtra("longitude", 0.0)
            selectedAddress = data.getStringExtra("address")
            tvSelectedLocation.text = selectedAddress ?: "Location selected"
        }
    }
}