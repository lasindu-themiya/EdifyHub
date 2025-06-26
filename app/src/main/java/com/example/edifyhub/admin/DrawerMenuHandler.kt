package com.example.edifyhub.admin

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.edifyhub.R
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
            val topMarginInDp = 10  // Adjust this value to perfect alignment
            val topMarginInPx = (topMarginInDp * scale + 0.5f).toInt()

            val params = toggleView.layoutParams as Toolbar.LayoutParams
            params.topMargin = topMarginInPx
            toggleView.layoutParams = params
        }

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                Toast.makeText(context, "Dashboard clicked", Toast.LENGTH_SHORT).show()
                // TODO: Start dashboard activity here
            }
            R.id.nav_teachers -> {
                Toast.makeText(context, "Teachers clicked", Toast.LENGTH_SHORT).show()
                // TODO: Start teachers activity here
            }
            R.id.nav_approve -> {
                Toast.makeText(context, "Approvals clicked", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, AdminApprovalActivity::class.java))
            }
            R.id.nav_logout -> {
                Toast.makeText(context, "Logout clicked", Toast.LENGTH_SHORT).show()
                // TODO: Implement logout logic here
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