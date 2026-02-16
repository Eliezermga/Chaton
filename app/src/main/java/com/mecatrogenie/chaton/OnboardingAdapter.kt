package com.mecatrogenie.chaton

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class OnboardingAdapter(private val onboardingItems: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_onboarding,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageOnboarding = view.findViewById<LottieAnimationView>(R.id.onboarding_image)
        private val textTitle = view.findViewById<TextView>(R.id.onboarding_title)
        private val textDescription = view.findViewById<TextView>(R.id.onboarding_description)

        fun bind(onboardingItem: OnboardingItem) {
            imageOnboarding.setAnimation(onboardingItem.image)
            textTitle.text = onboardingItem.title
            textDescription.text = onboardingItem.description
        }
    }
}
