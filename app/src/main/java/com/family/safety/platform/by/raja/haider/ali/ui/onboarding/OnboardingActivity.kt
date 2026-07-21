package com.family.safety.platform.by.raja.haider.ali.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.auth.LoginActivity
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    private lateinit var prefManager: PreferenceManager

    private val onboardingItems = listOf(
        OnboardingItem(R.drawable.ic_shield, "Family Protection", "Keep your loved ones safe with real-time monitoring and alerts."),
        OnboardingItem(R.drawable.ic_nav_location, "Location Tracking", "Know where your family members are at all times with live GPS tracking."),
        OnboardingItem(R.drawable.ic_nav_devices, "App Management", "Control screen time, block apps, and manage digital wellbeing."),
        OnboardingItem(R.drawable.ic_link, "Smart Automation", "Set rules and schedules for automatic device management.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        prefManager = PreferenceManager(this)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)
        val btnSkip = findViewById<MaterialButton>(R.id.btnSkip)

        val adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
                if (position == onboardingItems.lastIndex) {
                    btnNext.text = getString(R.string.btn_get_started)
                    btnSkip.visibility = android.view.View.INVISIBLE
                } else {
                    btnNext.text = getString(R.string.btn_next)
                    btnSkip.visibility = android.view.View.VISIBLE
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < onboardingItems.lastIndex) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                completeOnboarding()
            }
        }

        btnSkip.setOnClickListener { completeOnboarding() }
    }

    private fun updateDots(position: Int) {
        val dots = listOf(R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4)
        dots.forEachIndexed { index, dotId ->
            val dot = findViewById<android.view.View>(dotId)
            if (index == position) {
                dot.layoutParams.width = 24.dp
                dot.background = ContextCompat.getDrawable(this, R.drawable.progress_gradient)
            } else {
                dot.layoutParams.width = 8.dp
                dot.background = ContextCompat.getDrawable(this, R.drawable.bg_dot_inactive)
            }
            dot.requestLayout()
        }
    }

    private fun completeOnboarding() {
        prefManager.onboardingComplete = true
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}

data class OnboardingItem(val iconRes: Int, val title: String, val description: String)

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val ivOnboarding: ImageView = view.findViewById(R.id.ivOnboarding)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.ivOnboarding.setImageResource(item.iconRes)
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description
    }

    override fun getItemCount() = items.size
}
