package me.samboycoding.krystarav2.model

data class Weapon(
    val id: Int,
    val name: String,
    val rarity: String?,
    val colors: Int?,
    val spellId: Int?,
    val rarityId: String?,
    val kingdomId: Int?,
    val kingdomName: String?,
    val imageUrl: String?,
    val pageUrl: String?
)