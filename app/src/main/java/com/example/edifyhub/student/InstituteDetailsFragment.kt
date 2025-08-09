// Kotlin: app/src/main/java/com/example/edifyhub/student/InstituteDetailsFragment.kt
package com.example.edifyhub.student

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.edifyhub.R
import com.google.firebase.firestore.FirebaseFirestore

class InstituteDetailsFragment : Fragment() {
    private var teacherId: String? = null
    private lateinit var db: FirebaseFirestore

    companion object {
        fun newInstance(teacherId: String): InstituteDetailsFragment {
            val fragment = InstituteDetailsFragment()
            fragment.arguments = Bundle().apply { putString("TEACHER_ID", teacherId) }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teacherId = arguments?.getString("TEACHER_ID")
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_institute_details, container, false)
        val tvName = root.findViewById<TextView>(R.id.tvInstituteName)
        val tvAddress = root.findViewById<TextView>(R.id.tvInstituteAddress)
        val tvDescription = root.findViewById<TextView>(R.id.tvInstituteDescription)
        val tvContact = root.findViewById<TextView>(R.id.tvInstituteContact)
        val btnViewLocation = root.findViewById<Button>(R.id.btnViewLocation)

        if (teacherId != null) {
            db.collection("users").document(teacherId!!)
                .get()
                .addOnSuccessListener { doc ->
                    val details = doc.get("instituteDetails") as? Map<*, *>
                    if (details != null) {
                        tvName.text = details["name"] as? String ?: ""
                        tvAddress.text = details["address"] as? String ?: ""
                        tvDescription.text = details["description"] as? String ?: ""
                        tvContact.text = details["contact"] as? String ?: ""
                        val location = details["location"] as? Map<*, *>
                        btnViewLocation.setOnClickListener {
                            val lat = location?.get("latitude") as? Double ?: 0.0
                            val lng = location?.get("longitude") as? Double ?: 0.0
                            val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${details["name"]})")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(requireContext(), "No institute details found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to fetch details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        return root
    }
}