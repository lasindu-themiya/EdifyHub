// File: java/com/example/edifyhub/payment/PayHerePaymentActivity.kt
package com.example.edifyhub.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.edifyhub.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import lk.payhere.androidsdk.PHConfigs
import lk.payhere.androidsdk.PHConstants
import lk.payhere.androidsdk.PHMainActivity
import lk.payhere.androidsdk.PHResponse
import lk.payhere.androidsdk.model.InitRequest
import lk.payhere.androidsdk.model.Item
import lk.payhere.androidsdk.model.StatusResponse

class PayHerePaymentActivity : Activity() {

    companion object {
        const val PAYMENT_REQUEST_CODE = 11010
    }

    private lateinit var txtQuizTitle: TextView
    private lateinit var txtAmount: TextView
    private lateinit var txtResult: TextView
    private lateinit var payButton: Button

    private var quizId: String? = null
    private var quizName: String? = null
    private var quizAmount: Double = 0.0
    private var teacherId: String? = null
    private var teacherName: String? = null

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payhere_payment)

        txtQuizTitle = findViewById(R.id.txtQuizTitle)
        txtAmount = findViewById(R.id.txtAmount)
        txtResult = findViewById(R.id.txtResult)
        payButton = findViewById(R.id.btnPayHere)

        quizId = intent.getStringExtra("quizId")
        quizName = intent.getStringExtra("quizName")
        quizAmount = intent.getDoubleExtra("quizAmount", 0.0)
        teacherId = intent.getStringExtra("teacherId")
        teacherName = intent.getStringExtra("teacherName")

        txtQuizTitle.text = quizName ?: "Quiz"
        val totalAmount = quizAmount * 1.033
        txtAmount.text = "Amount: Rs. %.2f (incl. 3.3%% fee)".format(totalAmount)

        payButton.setOnClickListener {
            if (teacherId != null) {
                fetchTeacherPaymentDetailsAndPay(totalAmount)
            } else {
                Toast.makeText(this, "Teacher info missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchTeacherPaymentDetailsAndPay(totalAmount: Double) {
        db.collection("users").document(teacherId!!)
            .collection("paymentDetails").document("bank")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val accHolder = doc.getString("accountHolder") ?: ""
                    val accNo = doc.getString("accountNo") ?: ""
                    val bankName = doc.getString("bankName") ?: ""
                    val branchName = doc.getString("branchName") ?: ""
                    startPayHerePayment(accHolder, accNo, bankName, branchName, totalAmount)
                } else {
                    Toast.makeText(this, "Teacher payment details not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch teacher payment details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startPayHerePayment(
        accHolder: String,
        accNo: String,
        bankName: String,
        branchName: String,
        totalAmount: Double
    ) {
        val req = InitRequest().apply {
            merchantId = "1231406" // Replace with your Merchant ID
            currency = "LKR"
            amount = totalAmount
            orderId = quizId ?: "quiz"
            itemsDescription = quizName ?: "Quiz Payment"
            custom1 = "Teacher: $teacherName, Acc: $accHolder, $accNo, $bankName, $branchName"
            custom2 = "QuizId: $quizId"
            notifyUrl = "https://yourdomain.com/notify"
            customer.firstName = "Acc Holder Name : $accHolder"
            customer.lastName = ""
            customer.email = "Acc No : $accNo" // Optionally fetch teacher email
            customer.phone = ""
            customer.address.address = "Bank Name : $bankName"
            customer.address.city = "Branch Name : $branchName"
            customer.address.country = "Sri Lanka"
            items.add(Item(null, quizName ?: "Quiz", 1, totalAmount))
        }

        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL)
        val intent = Intent(this, PHMainActivity::class.java)
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req)
        startActivityForResult(intent, PAYMENT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            @Suppress("UNCHECKED_CAST")
            val response = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT) as? PHResponse<StatusResponse>
            if (resultCode == RESULT_OK && response?.isSuccess == true) {
                markQuizAsPaid()
                val msg = "Payment Successfull"
                txtResult.text = msg
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            } else if (resultCode == RESULT_CANCELED) {
                val msg = response?.toString() ?: "User canceled the request"
                txtResult.text = msg
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            } else {
                val msg = "Payment Failed: $response"
                txtResult.text = msg
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun markQuizAsPaid() {
        if (userId == null || quizId == null) return
        db.collection("users").document(userId)
            .collection("paidQuizzes").document(quizId!!)
            .set(mapOf("paid" to true))
            .addOnSuccessListener {
                payButton.isEnabled = false
                payButton.text = "Paid"
                txtResult.text = "Payment successful! Redirecting you to the Quiz Page..."
                payButton.postDelayed({
                    redirectToQuizzes()
                }, 1500)
            }
    }

    private fun redirectToQuizzes() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}