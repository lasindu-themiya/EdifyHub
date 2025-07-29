package com.example.edifyhub.teacher

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TeacherPaymentDetailsActivity : AppCompatActivity() {

    private lateinit var etAccountHolder: EditText
    private lateinit var etAccountNo: EditText
    private lateinit var etBankName: EditText
    private lateinit var etBranchName: EditText
    private lateinit var btnSave: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerHandler: TeacherDrawerMenuHandler
    private lateinit var toolbar: Toolbar

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_payment_details)

        toolbar = findViewById(R.id.teacherToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.teacherDrawerLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerHandler = TeacherDrawerMenuHandler(this, drawerLayout, navigationView, toolbar, userId)


        etAccountHolder = findViewById(R.id.etAccountHolder)
        etAccountNo = findViewById(R.id.etAccountNo)
        etBankName = findViewById(R.id.etBankName)
        etBranchName = findViewById(R.id.etBranchName)
        btnSave = findViewById(R.id.btnSave)

        // Fetch and show existing payment details if available
        if (userId != null) {
            db.collection("users").document(userId)
                .collection("paymentDetails").document("bank")
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        etAccountHolder.setText(doc.getString("accountHolder") ?: "")
                        etAccountNo.setText(doc.getString("accountNo") ?: "")
                        etBankName.setText(doc.getString("bankName") ?: "")
                        etBranchName.setText(doc.getString("branchName") ?: "")
                    }
                }
        }

        btnSave.setOnClickListener {
            val accountHolder = etAccountHolder.text.toString().trim()
            val accountNo = etAccountNo.text.toString().trim()
            val bankName = etBankName.text.toString().trim()
            val branchName = etBranchName.text.toString().trim()

            if (accountHolder.isEmpty() || accountNo.isEmpty() || bankName.isEmpty() || branchName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val details = hashMapOf(
                "accountHolder" to accountHolder,
                "accountNo" to accountNo,
                "bankName" to bankName,
                "branchName" to branchName
            )

            db.collection("users").document(userId)
                .collection("paymentDetails").document("bank")
                .set(details)
                .addOnSuccessListener {
                    Toast.makeText(this, "Details saved", Toast.LENGTH_SHORT).show()
                    finish()
                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}