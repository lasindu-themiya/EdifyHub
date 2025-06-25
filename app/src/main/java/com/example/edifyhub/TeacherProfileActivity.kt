package com.example.edifyhub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class TeacherProfileActivity : AppCompatActivity() {

    private lateinit var imageProfile: ImageView
    private lateinit var btnEditProfilePic: ImageView
    private lateinit var etName: EditText
    private lateinit var etAbout: EditText
    private lateinit var etInstitute: EditText
    private lateinit var etSubject: EditText
    private lateinit var btnManageInstitute: Button
    private lateinit var btnSave: Button

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_profile)

        imageProfile = findViewById(R.id.imageProfile)
        btnEditProfilePic = findViewById(R.id.btnEditProfilePic)
        etName = findViewById(R.id.etName)
        etAbout = findViewById(R.id.etAbout)
        etInstitute = findViewById(R.id.etInstitute)
        etSubject = findViewById(R.id.etSubject)
        btnSave = findViewById(R.id.btnSave)
        btnManageInstitute = findViewById(R.id.btnManageInstitute)

        // Load dummy data (replace with real data in your app)
        etName.setText("John Doe")
        etAbout.setText("Experienced physics teacher with 10 years of experience.")
        etInstitute.setText("ABC Institute")
        etSubject.setText("Physics")

        btnManageInstitute.setOnClickListener {
            val intent = Intent(this, ManageInstituteActivity::class.java)
            startActivity(intent)
        }

        btnEditProfilePic.setOnClickListener {
            openImageChooser()
        }

        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data

            // Load image with Glide and apply circle crop
            Glide.with(this)
                .load(selectedImageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(imageProfile)
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

        // TODO: Save profile data to your backend or local DB here
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
    }
}
