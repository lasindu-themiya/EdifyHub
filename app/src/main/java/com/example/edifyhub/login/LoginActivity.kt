package com.example.edifyhub.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.edifyhub.R
import com.example.edifyhub.admin.AdminDashboardActivity
import com.example.edifyhub.databinding.ActivityLoginBinding
import com.example.edifyhub.passwordReset.EnterEmailActivity
import com.example.edifyhub.student.StudentDashboardActivity
import com.example.edifyhub.student.StudentProfileUpdateActivity
import com.example.edifyhub.teacher.TeacherDashboardActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Forget password page
        binding.forgetUserPassword.setOnClickListener {
            val intent = Intent(this, EnterEmailActivity::class.java)
            startActivity(intent)
        }

        // Teacher signup page
        binding.teacherSignUp.setOnClickListener {
            val intent = Intent(this, TeacherSignupActivity::class.java)
            startActivity(intent)
        }

        // Student signup page
        binding.studentSignUp.setOnClickListener {
            val intent = Intent(this, StudentSignupActivity::class.java)
            startActivity(intent)
        }

        // Sign in with email and password
        binding.signinbtn.setOnClickListener {
            val email = binding.loginusername.text.toString()
            val password = binding.loginpassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    if (document != null && document.exists()) {
                                        val role = document.getString("userRole")
                                        val status = document.getString("status")
                                        when (role) {
                                            "admin" -> {
                                                val intent = Intent(this, AdminDashboardActivity::class.java)
                                                intent.putExtra("USER_ID", userId)
                                                startActivity(intent)
                                                finish()
                                            }
                                            "teacher" -> {
                                                if (status == "pending") {
                                                    Toast.makeText(this, "Sent for approval", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                                                    intent.putExtra("USER_ID", userId)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                            "student" -> {
                                                val intent = Intent(this, StudentDashboardActivity::class.java)
                                                intent.putExtra("USER_ID", userId)
                                                startActivity(intent)
                                                finish()
                                            }
                                            else -> {
                                                Toast.makeText(this, "Unknown user role", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this, "User not exists! please Sign Up.", Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener { e ->
                                    Toast.makeText(this, "Error fetching user role: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Please Signup first!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }

        // Google sign in section
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleLogin.setOnClickListener {
            signInGoogle()
        }


        //GitHub sign in section
        binding.githubLogin.setOnClickListener {
            val provider = OAuthProvider.newBuilder("github.com")
            val pendingResultTask = firebaseAuth.pendingAuthResult
            if (pendingResultTask != null) {
                pendingResultTask
                    .addOnSuccessListener { authResult ->
                        handleGitHubResult()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "GitHub Sign In Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                firebaseAuth
                    .startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener {
                        handleGitHubResult()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "GitHub Sign In Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }


    }

    private fun signInGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                // Existing user, go to dashboard
                                val intent = Intent(this, StudentDashboardActivity::class.java)
                                intent.putExtra("USER_ID", userId)
                                startActivity(intent)
                            } else {
                                // New user, go to profile update
                                val intent = Intent(this, StudentProfileUpdateActivity::class.java)
                                intent.putExtra("USER_ID", userId)
                                startActivity(intent)
                            }
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, authResult.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    //GitHub section
    private fun handleGitHubResult() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val intent = Intent(this, StudentDashboardActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, StudentProfileUpdateActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    }
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
        }
    }



}