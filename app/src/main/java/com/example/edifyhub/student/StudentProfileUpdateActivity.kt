package com.example.edifyhub.student

import StudentDrawerMenuHandler
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentProfileUpdateActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerHandler: StudentDrawerMenuHandler

    private lateinit var imageProfile: ImageView
    private lateinit var btnEditProfilePic: ImageView
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etMobile: EditText
    private lateinit var spinnerALStream: Spinner
    private lateinit var btnSave: MaterialButton

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private var profileImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile_update)

        toolbar = findViewById(R.id.studentToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.studentDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = StudentDrawerMenuHandler(this, drawerLayout, navigationView, toolbar)

        imageProfile = findViewById(R.id.imageProfile)
        btnEditProfilePic = findViewById(R.id.btnEditProfilePic)
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etMobile = findViewById(R.id.etMobile)
        spinnerALStream = findViewById(R.id.spinnerALStream)
        btnSave = findViewById(R.id.btnSave)

        val userId = intent.getStringExtra("USER_ID")
        val db = FirebaseFirestore.getInstance()
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        // Populate Spinner with streams from Firestore
        val streamList = mutableListOf<String>()
        streamList.add("Select a stream")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, streamList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerALStream.adapter = adapter

        db.collection("streams")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    streamList.add(document.id)
                }
                adapter.notifyDataSetChanged()

                // Pre-select current stream if editing
                if (userId != null) {
                    db.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            val currentStream = userDoc.getString("stream")
                            val index = streamList.indexOf(currentStream)
                            if (index >= 0) spinnerALStream.setSelection(index)
                            etName.setText(userDoc.getString("username") ?: "")
                            etAge.setText(userDoc.getString("age") ?: "")
                            etMobile.setText(userDoc.getString("mobile") ?: "")
                            profileImageUrl = userDoc.getString("profileImageUrl")
                            if (!profileImageUrl.isNullOrEmpty()) {
                                Glide.with(this)
                                    .load(profileImageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .placeholder(R.drawable.baseline_person_24)
                                    .error(R.drawable.baseline_person_24)
                                    .into(imageProfile)
                            } else {
                                imageProfile.setImageResource(R.drawable.baseline_person_24)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load streams: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        btnEditProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSave.setOnClickListener {
            val username = etName.text.toString()
            val age = etAge.text.toString()
            val mobile = etMobile.text.toString()
            val stream = spinnerALStream.selectedItem.toString()
            val userRole = "student"
            val email = firebaseUser?.email ?: ""

            if (stream == "Select a stream") {
                Toast.makeText(this, "Please select a valid A/L Stream!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isNotEmpty() && age.isNotEmpty() && mobile.isNotEmpty() && stream.isNotEmpty()) {
                if (userId != null) {
                    val user = hashMapOf(
                        "username" to username,
                        "age" to age,
                        "mobile" to mobile,
                        "stream" to stream,
                        "userRole" to userRole,
                        "email" to email,
                        "profileImageUrl" to (profileImageUrl ?: "")
                    )
                    db.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, StudentDashboardActivity::class.java)
                            intent.putExtra("USER_ID", userId)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            selectedImageUri?.let { uri ->
                MediaManager.get().upload(uri)
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {}
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                        override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                            val url = resultData["secure_url"] as? String
                            url?.let {
                                profileImageUrl = it
                                Glide.with(this@StudentProfileUpdateActivity)
                                    .load(it)
                                    .apply(RequestOptions.circleCropTransform())
                                    .placeholder(R.drawable.baseline_person_24)
                                    .error(R.drawable.baseline_person_24)
                                    .into(imageProfile)
                                val userId = intent.getStringExtra("USER_ID")
                                if (userId != null) {
                                    FirebaseFirestore.getInstance().collection("users").document(userId)
                                        .update("profileImageUrl", it)
                                }
                                Toast.makeText(this@StudentProfileUpdateActivity, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Toast.makeText(this@StudentProfileUpdateActivity, "Upload failed: ${error?.description}", Toast.LENGTH_SHORT).show()
                        }
                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                    }).dispatch()
            }
        }
    }
}