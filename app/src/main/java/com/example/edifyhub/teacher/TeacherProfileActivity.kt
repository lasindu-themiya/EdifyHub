package com.example.edifyhub.teacher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class TeacherProfileActivity : AppCompatActivity() {

    private lateinit var imageProfile: ImageView
    private lateinit var btnEditProfilePic: ImageView
    private lateinit var etName: EditText
    private lateinit var etAbout: EditText
    private lateinit var etInstitute: EditText
    private lateinit var btnManageInstitute: Button
    private lateinit var btnSave: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: TeacherDrawerMenuHandler
    private lateinit var toolbar: Toolbar

    private lateinit var spinnerStream: Spinner
    private lateinit var spinnerSubject: Spinner
    private lateinit var streamAdapter: ArrayAdapter<String>
    private lateinit var subjectAdapter: ArrayAdapter<String>
    private var currentStream: String? = null
    private var currentSubject: String? = null

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
        btnSave = findViewById(R.id.btnSave)
        btnManageInstitute = findViewById(R.id.btnManageInstitute)

        spinnerStream = findViewById(R.id.spinnerStream)
        spinnerSubject = findViewById(R.id.spinnerSubject)

        db = FirebaseFirestore.getInstance()
        streamAdapter = ArrayAdapter(this, R.layout.spinner_item, mutableListOf("Select Stream"))
        streamAdapter.setDropDownViewResource(R.layout.spinner_item)
        spinnerStream.adapter = streamAdapter

        subjectAdapter = ArrayAdapter(this, R.layout.spinner_item, mutableListOf("Select Subject"))
        subjectAdapter.setDropDownViewResource(R.layout.spinner_item)
        spinnerSubject.adapter = subjectAdapter

        fetchStreams()

        spinnerStream.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStream = parent.getItemAtPosition(position).toString()
                if (selectedStream != "Select Stream") {
                    fetchSubjects(selectedStream)
                } else {
                    subjectAdapter.clear()
                    subjectAdapter.add("Select Subject")
                    subjectAdapter.notifyDataSetChanged()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

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

    private fun fetchStreams() {
        db.collection("streams").get()
            .addOnSuccessListener { result ->
                val streams = mutableListOf("Select Stream")
                for (doc in result) {
                    streams.add(doc.id)
                }
                streamAdapter.clear()
                streamAdapter.addAll(streams)
                streamAdapter.notifyDataSetChanged()
                currentStream?.let {
                    val pos = streams.indexOf(it)
                    if (pos >= 0) spinnerStream.setSelection(pos)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch streams!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchSubjects(stream: String) {
        db.collection("streams").document(stream).get()
            .addOnSuccessListener { doc ->
                val subjects = mutableListOf("Select Subject")
                if (doc != null && doc.exists()) {
                    val array = doc.get("subjects") as? List<*>
                    array?.forEach {
                        subjects.add(it.toString())
                    }
                }
                subjectAdapter.clear()
                subjectAdapter.addAll(subjects)
                subjectAdapter.notifyDataSetChanged()
                currentSubject?.let {
                    val pos = subjects.indexOf(it)
                    if (pos >= 0) spinnerSubject.setSelection(pos)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch subjects!", Toast.LENGTH_SHORT).show()
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
                    currentStream = doc.getString("stream")
                    currentSubject = doc.getString("subject")
                    val imageUrl = doc.getString("profileImageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .into(imageProfile)
                    } else {
                        imageProfile.setImageResource(R.drawable.baseline_person_24)
                    }
                    fetchStreams()
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
            selectedImageUri?.let { uri ->
                MediaManager.get().upload(uri)
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {}
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                        override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                            val url = resultData["secure_url"] as? String
                            url?.let {
                                userId?.let { uid ->
                                    db.collection("users").document(uid)
                                        .update("profileImageUrl", it)
                                        .addOnSuccessListener {
                                            Glide.with(this@TeacherProfileActivity)
                                                .load(url)
                                                .apply(RequestOptions.circleCropTransform())
                                                .placeholder(R.drawable.baseline_person_24)
                                                .error(R.drawable.baseline_person_24)
                                                .into(imageProfile)
                                            Toast.makeText(this@TeacherProfileActivity, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        }
                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Toast.makeText(this@TeacherProfileActivity, "Upload failed: ${error?.description}", Toast.LENGTH_SHORT).show()
                        }
                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                    }).dispatch()
            }
        }
    }

    private fun saveProfile() {
        val name = etName.text.toString().trim()
        val about = etAbout.text.toString().trim()
        val institute = etInstitute.text.toString().trim()
        val stream = spinnerStream.selectedItem.toString()
        val subject = spinnerSubject.selectedItem.toString()

        if (name.isEmpty() || institute.isEmpty() || stream == "Select Stream" || subject == "Select Subject") {
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
            "stream" to stream,
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