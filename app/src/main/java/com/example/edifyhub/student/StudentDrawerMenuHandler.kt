import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.edifyhub.R
import com.example.edifyhub.login.LoginActivity
import com.example.edifyhub.student.OtherStudentsOpenDiscussionActivity
import com.example.edifyhub.student.StudentCreateDiscussionActivity
import com.example.edifyhub.student.StudentDashboardActivity
import com.example.edifyhub.student.StudentProfileUpdateActivity
import com.example.edifyhub.student.StudentQuizListActivity
import com.example.edifyhub.student.StudentTeacherListActivity
import com.example.edifyhub.student.StudentViewAttendedQuizActivity
import com.example.edifyhub.student.StudentViewOpenDiscussionActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class StudentDrawerMenuHandler(
    private val context: Context,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView,
    toolbar: Toolbar
) {

    init {
        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            (context as AppCompatActivity),
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            onNavigationItemSelected(item)
        }

        // Automatically populate header when handler is created
        val userId = (context as? AppCompatActivity)?.intent?.getStringExtra("USER_ID")
        populateNavHeader(userId)
    }

    fun populateNavHeader(userId: String?) {
        val headerView = navigationView.getHeaderView(0)
        val navUserName = headerView.findViewById<TextView>(R.id.navUserName)
        val navUserEmail = headerView.findViewById<TextView>(R.id.navUserEmail)
        val navUserImage = headerView.findViewById<ImageView>(R.id.imageProfile)

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { userDoc ->
                    navUserName.text = userDoc.getString("username") ?: ""
                    navUserEmail.text = userDoc.getString("email") ?: ""
                    val profileImageUrl = userDoc.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(context)
                            .load(profileImageUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .into(navUserImage)
                    } else {
                        navUserImage.setImageResource(R.drawable.baseline_person_24)
                    }
                }
        }
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        val userId = (context as? AppCompatActivity)?.intent?.getStringExtra("USER_ID")

        when (item.itemId) {
            R.id.nav_student_dashboard -> {
                Toast.makeText(context, "Dashboard clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, StudentDashboardActivity::class.java)
                intent.putExtra("USER_ID", userId)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_student_profile -> {
                Toast.makeText(context, "Profile clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, StudentProfileUpdateActivity::class.java)
                intent.putExtra("USER_ID", userId)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_student_quizzes -> {
                Toast.makeText(context, "Quizzes clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, StudentQuizListActivity::class.java)
                intent.putExtra("USER_ID", userId)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_student_attempted_quizzes -> {
                Toast.makeText(context, "Attempted Quizzes clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, StudentViewAttendedQuizActivity::class.java)
                intent.putExtra("USER_ID", userId)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_student_view_teachers -> {
                Toast.makeText(context, "View Teachers clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, StudentTeacherListActivity::class.java)
                intent.putExtra("USER_ID", userId)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_create_discussion -> {
                Toast.makeText(context, "Create Discussion clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, StudentCreateDiscussionActivity::class.java)
                intent.putExtra("USER_ID", userId)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_open_discussions -> {
                Toast.makeText(context, "Open Discussions clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, StudentViewOpenDiscussionActivity::class.java)
                intent.putExtra("USER_ID", userId)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_other_students_open_discussions -> {
                Toast.makeText(context, "Other Students' Open Discussions clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, OtherStudentsOpenDiscussionActivity::class.java)
                intent.putExtra("USER_ID", userId)
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            R.id.nav_logout -> {
                Toast.makeText(context, "Logout clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                (context as? AppCompatActivity)?.finish()
                drawerLayout.closeDrawers()
                return true
            }
            else -> return false
        }
    }
}