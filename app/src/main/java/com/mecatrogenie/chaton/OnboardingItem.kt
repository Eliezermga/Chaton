package com.mecatrogenie.chaton

import androidx.annotation.RawRes

data class OnboardingItem(
    @RawRes val image: Int,
    val title: String,
    val description: String
)
