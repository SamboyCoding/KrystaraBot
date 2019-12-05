package me.samboycoding.krystarav2.model

data class Spell(
    val id: Int,
    val name: String,
    val description: String?,
    val boostRatioText: String?,
    val magicScalingText: String?,
    val cost: Int?,
    val imageUrl: String?
)