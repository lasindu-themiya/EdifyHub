package com.example.edifyhub

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ManageInstituteActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etDescription: EditText
    private lateinit var etLocation: EditText
    private lateinit var etContact: EditText
    private lateinit var btnSave: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_institute)

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
