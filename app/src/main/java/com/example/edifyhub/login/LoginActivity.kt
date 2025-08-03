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

        // Navigation buttons
        binding.forgetUserPassword.setOnClickListener {
            startActivity(Intent(this, EnterEmailActivity::class.java))
        }
        binding.teacherSignUp.setOnClickListener {
            startActivity(Intent(this, TeacherSignupActivity::class.java))
        }
        binding.studentSignUp.setOnClickListener {
            startActivity(Intent(this, StudentSignupActivity::class.java))
        }

        // Email/password SIGN-IN
        binding.signinbtn.setOnClickListener {
            val email = binding.loginusername.text.toString().trim()
            val password = binding.loginpassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        // Load role from Firestore and redirect accordingly
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val role = document.getString("userRole")
                                    val status = document.getString("status")
                                    when (role) {
                                        "admin" -> startDashboard(AdminDashboardActivity::class.java, userId)
                                        "teacher" -> {
                                            if (status == "pending") {
                                                Toast.makeText(this, "Sent for approval", Toast.LENGTH_SHORT).show()
                                            } else {
                                                startDashboard(TeacherDashboardActivity::class.java, userId)
                                            }
                                        }
                                        "student" -> startDashboard(StudentDashboardActivity::class.java, userId)
                                        else -> Toast.makeText(this, "Unknown user role", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this, "User does not exist! Please sign up.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "User ID is null!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Google Sign-In setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleLogin.setOnClickListener {
            signInGoogle()
        }

        // GitHub Sign-In
        binding.githubLogin.setOnClickListener {
            val provider = OAuthProvider.newBuilder("github.com")
            val pendingResultTask = firebaseAuth.pendingAuthResult
            if (pendingResultTask != null) {
                pendingResultTask.addOnSuccessListener {
                    handleGitHubResult()
                }.addOnFailureListener {
                    Toast.makeText(this, "GitHub Sign In Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                firebaseAuth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener {
                        handleGitHubResult()
                    }.addOnFailureListener {
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
            handleGoogleSignInResult(task)
        }
    }

    // Handle Google sign-in flow with full role & provider checks
    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        if (!task.isSuccessful) {
            Toast.makeText(this, "Google sign-in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            return
        }

        val account = task.result
        val email = account?.email
        if (email == null) {
            Toast.makeText(this, "Google account email is null!", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if email already exists and which providers are linked
        firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                val methods = result.signInMethods ?: emptyList()

                // Check Firestore for user role by email
                db.collection("users").whereEqualTo("email", email).get()
                    .addOnSuccessListener { query ->
                        if (!query.isEmpty) {
                            val userDoc = query.documents[0]
                            val role = userDoc.getString("userRole") ?: ""

                            // Role-based access control
                            if (role == "teacher") {
                                // Teachers are NOT allowed to sign in with Google
                                Toast.makeText(
                                    this,
                                    "Teachers must sign in with Email and Password only.",
                                    Toast.LENGTH_LONG
                                ).show()
                                googleSignInClient.signOut()
                                firebaseAuth.signOut()
                                return@addOnSuccessListener
                            }

                            if (role == "student") {
                                // If email exists with email/password only (no google linked), block google login
                                if (methods.contains("password") && !methods.contains("google.com")) {
                                    Toast.makeText(
                                        this,
                                        "This email is registered with Email/Password. Please sign in using Email/Password.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    googleSignInClient.signOut()
                                    firebaseAuth.signOut()
                                    return@addOnSuccessListener
                                }

                                // Allowed: Student Google sign-in
                                signInWithGoogleCredential(account)
                            } else {
                                Toast.makeText(this, "Unknown user role or no permission for Google sign-in.", Toast.LENGTH_SHORT).show()
                                googleSignInClient.signOut()
                                firebaseAuth.signOut()
                            }
                        } else {
                            // New user email - allow Google sign-in for students
                            signInWithGoogleCredential(account)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error checking user role: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error checking sign-in methods: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signInWithGoogleCredential(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                startDashboard(StudentDashboardActivity::class.java, userId)
                            } else {
                                startActivity(Intent(this, StudentProfileUpdateActivity::class.java).putExtra("USER_ID", userId))
                            }
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Google Authentication failed: ${authResult.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // GitHub sign-in with same checks as Google
    private fun handleGitHubResult() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("userRole")
                        if (role == "teacher") {
                            Toast.makeText(this, "Teachers must sign in with Email and Password only.", Toast.LENGTH_LONG).show()
                            firebaseAuth.signOut()
                            return@addOnSuccessListener
                        }
                        // For students, proceed to dashboard
                        startDashboard(StudentDashboardActivity::class.java, userId)
                    } else {
                        startActivity(Intent(this, StudentProfileUpdateActivity::class.java).putExtra("USER_ID", userId))
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startDashboard(activityClass: Class<*>, userId: String) {
        val intent = Intent(this, activityClass)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }
}
