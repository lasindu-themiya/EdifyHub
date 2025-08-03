package com.example.edifyhub.student

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.util.*

class StudentCreateDiscussionFragment : Fragment() {

    private var userId: String? = null
    private lateinit var etDiscussionName: EditText
    private lateinit var spinnerSubject: Spinner
    private lateinit var etContext: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var btnUploadImage: Button
    private lateinit var btnTakePhoto: Button
    private lateinit var btnCreateDiscussion: Button

    private var selectedImageUri: Uri? = null
    private var imageUrl: String? = null
    private val PICK_IMAGE_REQUEST = 1001
    private val TAKE_PHOTO_REQUEST = 1002

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_student_create_discussion, container, false)
        userId = arguments?.getString("USER_ID")

        etDiscussionName = view.findViewById(R.id.etDiscussionName)
        spinnerSubject = view.findViewById(R.id.spinnerSubject)
        etContext = view.findViewById(R.id.etContext)
        imagePreview = view.findViewById(R.id.imagePreview)
        btnUploadImage = view.findViewById(R.id.btnUploadImage)
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto)
        btnCreateDiscussion = view.findViewById(R.id.btnCreateDiscussion)

        val db = FirebaseFirestore.getInstance()
        val subjectList = mutableListOf<String>()
        subjectList.add("Select Subject")
        val subjectAdapter =
            object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, subjectList) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    textView.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                            context,
                            R.color.text_primary
                        )
                    )
                    return view
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    textView.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                            context,
                            R.color.text_primary
                        )
                    )
                    view.setBackgroundColor(
                        androidx.core.content.ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    return view
                }
            }
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = subjectAdapter

        // Fetch student's stream and subjects
        if (userId != null) {
            db.collection("users").document(userId!!)
                .get()
                .addOnSuccessListener { userDoc ->
                    val stream = userDoc.getString("stream")
                    if (stream != null) {
                        db.collection("streams").document(stream)
                            .get()
                            .addOnSuccessListener { streamDoc ->
                                val subjects = streamDoc.get("subjects") as? List<*>
                                subjectList.clear()
                                subjectList.add("Select Subject")
                                if (subjects != null) {
                                    subjectList.addAll(subjects.filterIsInstance<String>())
                                }
                                subjectAdapter.notifyDataSetChanged()
                            }
                    }
                }
        }

        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnTakePhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }

        btnCreateDiscussion.setOnClickListener {
            val name = etDiscussionName.text.toString().trim()
            val subject = spinnerSubject.selectedItem?.toString() ?: ""
            val contextText = etContext.text.toString().trim()

            if (name.isEmpty() || subject.isEmpty() || contextText.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // If image is selected but not uploaded yet, prevent creation
            if (selectedImageUri != null && imageUrl.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please wait for image upload to finish.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val discussion = hashMapOf(
                "name" to name,
                "subject" to subject,
                "context" to contextText,
                "imageUrl" to (imageUrl ?: ""),
                "createdBy" to userId,
                "createdAt" to Date(),
                "status" to "open"
            )

            // Save under user's subcollection
            db.collection("users").document(userId!!)
                .collection("discussions")
                .add(discussion)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Discussion created!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), StudentCreateDiscussionActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == PICK_IMAGE_REQUEST && data != null) {
            selectedImageUri = data.data
            selectedImageUri?.let { uri ->
                uploadImageToCloudinary(uri)
            }
        } else if (requestCode == TAKE_PHOTO_REQUEST && data != null) {
            val bitmap = data.extras?.get("data") as? Bitmap
            bitmap?.let {
                val file = File(requireContext().cacheDir, "photo_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file)
                selectedImageUri = uri
                uploadImageToCloudinary(uri)
            }
        }
    }

    private fun uploadImageToCloudinary(uri: Uri) {
        Glide.with(this).load(uri).centerCrop().into(imagePreview)
        btnCreateDiscussion.isEnabled = false // Disable until upload finishes
        MediaManager.get().upload(uri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    imageUrl = resultData["secure_url"] as? String
                    btnCreateDiscussion.isEnabled = true
                    Toast.makeText(requireContext(), "Image uploaded!", Toast.LENGTH_SHORT).show()
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    btnCreateDiscussion.isEnabled = true
                    Toast.makeText(requireContext(), "Upload failed: ${error?.description}", Toast.LENGTH_SHORT).show()
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    btnCreateDiscussion.isEnabled = true
                }
            }).dispatch()
    }
}