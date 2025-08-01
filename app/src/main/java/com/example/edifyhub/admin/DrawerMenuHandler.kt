package com.example.edifyhub.admin

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.edifyhub.R
import com.example.edifyhub.login.LoginActivity
import com.google.android.material.navigation.NavigationView

class DrawerMenuHandler(
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

        // Adjust toggle vertical margin for perfect alignment with title
        val toggleView = toolbar.getChildAt(0)
        if (toggleView != null) {
            val scale = context.resources.displayMetrics.density
            val topMarginInDp = 10
            val topMarginInPx = (topMarginInDp * scale + 0.5f).toInt()

            val params = toggleView.layoutParams as Toolbar.LayoutParams
            params.topMargin = topMarginInPx
            toggleView.layoutParams = params
        }

        // Set static admin info in nav header
        val headerView = navigationView.getHeaderView(0)
        val navUserName = headerView.findViewById<TextView>(R.id.navUserName)
        val navUserEmail = headerView.findViewById<TextView>(R.id.navUserEmail)
        val navUserImage = headerView.findViewById<ImageView>(R.id.imageProfile)

        navUserName.text = "Admin"
        navUserEmail.text = "admin@gmail.com"

        // Use a visually appealing avatar from the internet
        val avatarUrl = "https://ui-avatars.com/api/?name=Admin&background=FF9800&color=fff&size=128&bold=true"
        Glide.with(context)
            .load(avatarUrl)
            .circleCrop()
            .placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24)
            .into(navUserImage)

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                Toast.makeText(context, "Dashboard clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, AdminDashboardActivity::class.java))
                (context as? AppCompatActivity)?.finish()
            }
            R.id.nav_managestudent -> {
                Toast.makeText(context, "Manage Students clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, AdminStudentManageActivity::class.java))
                (context as? AppCompatActivity)?.finish()
            }
            R.id.nav_manageteachers -> {
                Toast.makeText(context, "Manage Teachers clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, AdminTeacherManageActivity::class.java))
                (context as? AppCompatActivity)?.finish()
            }
            R.id.nav_approve -> {
                Toast.makeText(context, "Approvals clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, AdminApprovalActivity::class.java))
                (context as? AppCompatActivity)?.finish()
            }
            R.id.nav_logout -> {
                Toast.makeText(context, "Logout clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
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