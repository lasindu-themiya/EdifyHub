package com.example.edifyhub.teacher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class TeacherProfileActivity : AppCompatActivity() {

    private lateinit var imageProfile: ImageView
    private lateinit var btnEditProfilePic: ImageView
    private lateinit var etName: EditText
    private lateinit var etAbout: EditText
    private lateinit var etInstitute: EditText
    private lateinit var etSubject: EditText
    private lateinit var btnManageInstitute: Button
    private lateinit var btnSave: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: TeacherDrawerMenuHandler
    private lateinit var toolbar: Toolbar

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    private lateinit var db: FirebaseFirestore
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_profile)

        toolbar = findViewById(R.id.teacherToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.teacherDrawerLayout)
        navigationView = findViewById(R.id.navigationView)

        userId = intent.getStringExtra("USER_ID")
        drawerHandler = TeacherDrawerMenuHandler(this, drawerLayout, navigationView, toolbar, userId)

        imageProfile = findViewById(R.id.imageProfile)
        btnEditProfilePic = findViewById(R.id.btnEditProfilePic)
        etName = findViewById(R.id.etName)
        etAbout = findViewById(R.id.etAbout)
        etInstitute = findViewById(R.id.etInstitute)
        etSubject = findViewById(R.id.etSubject)
        btnSave = findViewById(R.id.btnSave)
        btnManageInstitute = findViewById(R.id.btnManageInstitute)

        db = FirebaseFirestore.getInstance()

        loadProfile()

        btnManageInstitute.setOnClickListener {
            val intent = Intent(this, ManageInstituteActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        btnEditProfilePic.setOnClickListener {
            openImageChooser()
        }

        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadProfile() {
        if (userId == null) return
        db.collection("users").document(userId!!)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    etName.setText(doc.getString("username") ?: "")
                    etAbout.setText(doc.getString("about Qualifications") ?: "")
                    etInstitute.setText(doc.getString("institute") ?: "")
                    etSubject.setText(doc.getString("subject") ?: "")
                    // Optionally load profile image if you store it
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            Glide.with(this)
                .load(selectedImageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(imageProfile)
            // You can upload the image to Firebase Storage and save the URL in Firestore if needed
        }
    }

    private fun saveProfile() {
        val name = etName.text.toString().trim()
        val about = etAbout.text.toString().trim()
        val institute = etInstitute.text.toString().trim()
        val subject = etSubject.text.toString().trim()

        if (name.isEmpty() || institute.isEmpty() || subject.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf(
            "username" to name,
            "about Qualifications" to about,
            "institute" to institute,
            "subject" to subject
        )

        db.collection("users").document(userId!!)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}