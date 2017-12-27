/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.main

import android.content.Intent
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import de.dreier.mytargets.R
import de.dreier.mytargets.base.fragments.EditableListFragmentBase
import de.dreier.mytargets.base.navigation.IntentWrapper
import de.dreier.mytargets.base.navigation.NavigationController
import de.dreier.mytargets.databinding.ActivityMainBinding
import de.dreier.mytargets.features.arrows.EditArrowListFragment
import de.dreier.mytargets.features.bows.EditBowListFragment
import de.dreier.mytargets.features.settings.ESettingsScreens.MAIN
import de.dreier.mytargets.features.settings.ESettingsScreens.PROFILE
import de.dreier.mytargets.features.settings.SettingsActivity
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.timer.TimerFragment
import de.dreier.mytargets.features.training.overview.TrainingsFragment
import de.dreier.mytargets.utils.IntentWrapper
import de.dreier.mytargets.utils.Utils
import de.dreier.mytargets.utils.Utils.getCurrentLocale
import im.delight.android.languages.Language

/**
 * Shows the apps main screen, which contains a bottom navigation for switching between trainings,
 * bows and arrows, as well as an navigation drawer for hosting settings and the timer quick access.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navigationController: NavigationController
    internal lateinit var binding: ActivityMainBinding
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var onDrawerClosePendingIntent: IntentWrapper? = null
    /**
     * Ensures that espresso is waiting until the launched intent is sent from the navigation drawer.
     */
    val espressoIdlingResourceForMainActivity = CountingIdlingResource("delayed_activity")
    private var countryCode: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_CustomToolbar)
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE)
        countryCode = getCountryCode()
        super.onCreate(savedInstanceState)
        navigationController = NavigationController(this)
        if (SettingsManager.shouldShowIntroActivity) {
            SettingsManager.shouldShowIntroActivity = false
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        setupBottomNavigation()
        setupNavigationDrawer()
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.content_frame, TrainingsFragment())
                    .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_LANGUAGE, countryCode)
    }

    private fun getCountryCode(): String {
        return getCurrentLocale(this).toString()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countryCode = savedInstanceState.getString(KEY_LANGUAGE)
    }

    override fun onResume() {
        super.onResume()
        if (countryCode != null && getCountryCode() != countryCode) {
            countryCode = getCountryCode()
            recreate()
        }
        setupNavigationHeaderView()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var fragment: EditableListFragmentBase<*, *>? = null
            when (item.itemId) {
                R.id.action_arrows -> fragment = EditArrowListFragment()
                R.id.action_bows -> fragment = EditBowListFragment()
                R.id.action_trainings -> fragment = TrainingsFragment()
                else -> {
                }
            }
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit()
            true
        }
    }

    private fun setupNavigationDrawer() {
        if (Utils.isLollipop) {
            window.statusBarColor = Color.TRANSPARENT
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_timer -> closeDrawerAndStart(TimerFragment.getIntent(false))
                R.id.nav_settings -> closeDrawerAndStart(SettingsActivity.getIntent(MAIN))
                R.id.nav_help_and_feedback -> closeDrawerAndStart(navigationController.navigateToHelp())
                else -> {
                }
            }
            false
        }

        drawerToggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                if (onDrawerClosePendingIntent != null) {
                    onDrawerClosePendingIntent!!.withContext(this@MainActivity).start()
                    onDrawerClosePendingIntent = null
                    espressoIdlingResourceForMainActivity.decrement()
                }
            }
        }
        binding.drawerLayout.addDrawerListener(drawerToggle!!)
    }

    private fun setupNavigationHeaderView() {
        val headerLayout = binding.navigationView.getHeaderView(0)
        val userName = headerLayout.findViewById<TextView>(R.id.username)
        val userDetails = headerLayout.findViewById<TextView>(R.id.user_details)
        val profileFullName = SettingsManager.profileFullName
        if (profileFullName.trim { it <= ' ' }.isEmpty()) {
            userName.setText(R.string.click_to_enter_profile)
        } else {
            userName.text = profileFullName
        }
        userDetails.text = SettingsManager.profileClub
        headerLayout.setOnClickListener { closeDrawerAndStart(SettingsActivity.getIntent(PROFILE)) }
    }

    private fun closeDrawerAndStart(intent: IntentWrapper) {
        onDrawerClosePendingIntent = intent
        espressoIdlingResourceForMainActivity.increment()
        binding.drawerLayout.closeDrawers()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        val KEY_LANGUAGE = "language"
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
