package com.mecatrogenie.chaton

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import me.relex.circleindicator.CircleIndicator3

class OnboardingActivity : AppCompatActivity() {

    private lateinit var onboardingViewPager: ViewPager2
    private lateinit var nextButton: MaterialButton
    private lateinit var skipButton: MaterialButton
    private lateinit var getStartedButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if onboarding has been completed before
        if (onboardingFinished()) {
            goToSignInActivity()
            return
        }

        setContentView(R.layout.activity_onboarding)

        requestNotificationPermission()

        onboardingViewPager = findViewById(R.id.onboarding_view_pager)
        nextButton = findViewById(R.id.next_button)
        skipButton = findViewById(R.id.skip_button)
        getStartedButton = findViewById(R.id.get_started_button)

        val onboardingItems = listOf(
            OnboardingItem(
                image = R.raw.messageeasy,
                title = "Messagerie facile",
                description = "Discutez avec vos amis et votre famille en toute simplicité."
            ),
            OnboardingItem(
                image = R.raw.messagefun,
                title = "Messagerie amusante",
                description = "Exprimez-vous avec des emojis, des GIFs et des autocollants."
            ),
            OnboardingItem(
                image = R.raw.mynotification,
                title = "Notifications instantanées",
                description = "Ne manquez jamais un message important."
            )
        )

        val onboardingAdapter = OnboardingAdapter(onboardingItems)
        onboardingViewPager.adapter = onboardingAdapter

        val indicator = findViewById<CircleIndicator3>(R.id.dots_indicator)
        indicator.setViewPager(onboardingViewPager)

        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingAdapter.itemCount - 1) {
                    nextButton.visibility = View.GONE
                    skipButton.visibility = View.GONE
                    getStartedButton.visibility = View.VISIBLE
                } else {
                    nextButton.visibility = View.VISIBLE
                    skipButton.visibility = View.VISIBLE
                    getStartedButton.visibility = View.GONE
                }
            }
        })

        nextButton.setOnClickListener {
            if (onboardingViewPager.currentItem < onboardingAdapter.itemCount - 1) {
                onboardingViewPager.currentItem += 1
            }
        }

        skipButton.setOnClickListener {
            finishOnboarding()
        }

        getStartedButton.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        // Mark onboarding as finished
        val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("finished", true)
            apply()
        }
        goToSignInActivity()
    }

    private fun onboardingFinished(): Boolean {
        val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
        return sharedPref.getBoolean("finished", false)
    }

    private fun goToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Les notifications sont désactivées. Vous pourriez manquer des messages importants.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
