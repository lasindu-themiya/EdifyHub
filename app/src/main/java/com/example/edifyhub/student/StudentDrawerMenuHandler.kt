import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.example.edifyhub.login.LoginActivity
import com.example.edifyhub.student.StudentDashboardActivity
import com.example.edifyhub.student.StudentProfileUpdateActivity
import com.example.edifyhub.student.StudentQuizListActivity
import com.example.edifyhub.student.StudentTeacherListActivity
import com.example.edifyhub.student.StudentViewAttendedQuizActivity
import com.google.android.material.navigation.NavigationView

class StudentDrawerMenuHandler(
    private val context: Context,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView,
    toolbar: Toolbar
) {

    init {
        // Set up the toolbar and navigation drawer
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
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Retrieve userId from the current activity's intent
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
            // Inside onNavigationItemSelected:
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