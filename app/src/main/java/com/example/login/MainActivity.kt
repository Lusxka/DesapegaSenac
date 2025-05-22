package com.example.login

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnIrParaAdminProdutos: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        btnIrParaAdminProdutos = findViewById(R.id.btnIrParaAdminProdutos)
        
        // Check if user is admin (implement your logic here)
        val isAdmin = false // Replace with actual admin check
        if (isAdmin) {
            btnIrParaAdminProdutos.visibility = View.VISIBLE
            navigationView.menu.findItem(R.id.nav_admin).isVisible = true
        }

        btnIrParaAdminProdutos.setOnClickListener {
            val intent = Intent(this, AdminProdutosActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle home navigation
            }
            R.id.nav_about -> {
                // Handle about navigation
            }
            R.id.nav_account -> {
                // Handle account navigation
            }
            R.id.nav_privacy -> {
                // Handle privacy navigation
            }
            R.id.nav_admin -> {
                if (btnIrParaAdminProdutos.visibility == View.VISIBLE) {
                    val intent = Intent(this, AdminProdutosActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}