package com.example.edifyhub.student

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
import com.example.edifyhub.login.LoginActivity
import com.google.android.material.navigation.NavigationView

class StudentDrawerMenuHandler(
    private val context: Context,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView,
    private val toolbar: Toolbar
) : NavigationView.OnNavigationItemSelectedListener {

    private val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
        context as AppCompatActivity,
        drawerLayout,
        toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
    )

    init {
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Optional: Adjust toggle vertical margin for alignment
        val toggleView = toolbar.getChildAt(0)
        if (toggleView != null) {
            val scale = context.resources.displayMetrics.density
            val topMarginInDp = 10
            val topMarginInPx = (topMarginInDp * scale + 0.5f).toInt()
            val params = toggleView.layoutParams as Toolbar.LayoutParams
            params.topMargin = topMarginInPx
            toggleView.layoutParams = params
        }

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_student_dashboard -> {
                Toast.makeText(context, "Dashboard clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, StudentDashboardActivity::class.java))
                (context as? AppCompatActivity)?.finish()
            }
            R.id.nav_student_profile -> {
                Toast.makeText(context, "Profile clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, StudentProfileUpdateActivity::class.java))
                (context as? AppCompatActivity)?.finish()
            }
            R.id.nav_logout -> {
                Toast.makeText(context, "Logout clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                (context as? AppCompatActivity)?.finish()
            }
        }
        drawerLayout.closeDrawers()
        return true
    }

    fun onBackPressed(): Boolean {
        return if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView)
            true
        } else {
            false
        }
    }
}