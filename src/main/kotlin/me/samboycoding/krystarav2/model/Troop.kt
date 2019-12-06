package me.samboycoding.krystarav2.model

import me.samboycoding.krystarav2.network.GowDbService

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
) {
    val spell: Spell?
        get() = if (spellId == null) null else GowDbService.instance.getSpell(spellId).execute().body()?.spells?.first()

    val kingdom: Kingdom?
        get() = if(kingdomId == null) null else GowDbService.instance.getKingdom(kingdomId).execute().body()?.kingdoms?.first()


}