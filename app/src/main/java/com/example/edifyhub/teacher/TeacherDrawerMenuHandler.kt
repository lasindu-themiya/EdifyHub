package com.example.edifyhub.teacher

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

class TeacherDrawerMenuHandler(
    private val context: Context,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView,
    private val toolbar: Toolbar,
    private val userId: String? // Add userId here
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
        val intent = when (item.itemId) {
            R.id.nav_dashboard -> Intent(context, TeacherDashboardActivity::class.java)
            R.id.nav_profile -> Intent(context, TeacherProfileActivity::class.java)
            R.id.nav_create_quiz -> Intent(context, CreateQuizActivity::class.java)
            R.id.nav_logout -> Intent(context, LoginActivity::class.java)
            else -> null
        }

        if (intent != null) {
            if (item.itemId != R.id.nav_logout && userId != null) {
                intent.putExtra("USER_ID", userId)
            }
            context.startActivity(intent)
            (context as? AppCompatActivity)?.finish()
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