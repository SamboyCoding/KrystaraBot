package me.samboycoding.krystarav2.model

data class Troop(
    val id: Int,
    val name: String,
    val rarity: String?,
    val kingdomId: Int?,
    val type: String?,
    val colors: Int?,
    val spellId: Int?,
    val description: String?,
    val rarityId: Int?,
    val typeCode1: String?,
    val typeCode2: String?,
    val maxArmor: Int?,
    val maxLife: Int?,
    val maxAttack: Int?,
    val maxMagic: Int?,
    val kingdomName: String?,
    val imageUrl: String?,
    val pageUrl: String?
)