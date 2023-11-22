package com.azrosk.sell_it.shared.splash_screen

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.sell_it.MainActivity
import com.azrosk.sell_it.R
import com.azrosk.sell_it.admin.AdminActivity
import com.azrosk.sell_it.databinding.ActivitySplashScreenBinding
import com.azrosk.sell_it.shared.auth.AuthActivity
import com.azrosk.sell_it.util.AuthManager
import com.azrosk.sell_it.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity(R.layout.activity_splash_screen) {
    private val binding by viewBinding(ActivitySplashScreenBinding::bind)

    @Inject
    lateinit var authManager: AuthManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()

    }

    private fun setAnimation() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val animationDuration = 5200L // Duration of the longest animation

        val cartAnimation = ObjectAnimator.ofFloat(
            binding.imageViewCart,
            "translationX",
            -screenWidth,
            screenWidth / 1f
        ).apply {
            duration = animationDuration
        }

        val sellAnimation = ObjectAnimator.ofFloat(
            binding.textViewSell,
            "translationX",
            -screenWidth,
            screenWidth / 3f
        ).apply {
            duration = 1200
        }

        val itAnimation = ObjectAnimator.ofFloat(
            binding.textViewIt,
            "translationX",
            screenWidth,
            screenWidth / 3f
        ).apply {
            duration = 2200
        }

        val animatorSet = AnimatorSet().apply {
            playTogether(cartAnimation, sellAnimation, itAnimation)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    getUser()
                }
            })
        }

        animatorSet.start()
    }

    private fun getUser() {
        if (!authManager.isLoggedIn()) {
            navToAuthActivity()
        } else getRole()
    }

    private fun getRole() {
        when (authManager.getRole()) {
            Constants.ADMIN -> navToAdminActivity()
            Constants.USER -> nanoMainActivity()
            else -> navToAuthActivity()
        }
    }

    private fun nanoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    private fun navToAdminActivity() {
        startActivity(Intent(this, AdminActivity::class.java))
        this.finish()
    }

    private fun navToAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        this.finish()
    }

}