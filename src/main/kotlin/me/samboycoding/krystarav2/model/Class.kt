package me.samboycoding.krystarav2.model

data class Class(
    val id: Int,
    val name: String,
    val rarity: String,
    val kingdomId: Int,
    val type: String,
    val kingdomName: String,
    val weaponId: Int,
    val weaponName: String,
    val imageUrl: String,
    val pageUrl: String
)