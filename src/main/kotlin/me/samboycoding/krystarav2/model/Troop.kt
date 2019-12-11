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
    val pageUrl: String?,
    val traits: Array<TraitData>
) {
    val spell: Spell?
        get() = if (spellId == null) null else GowDbService.instance.getSpell(spellId).execute().body()

    val kingdom: Kingdom?
        get() = if(kingdomId == null) null else GowDbService.instance.getKingdom(kingdomId).execute().body()

    data class TraitData(
        val name: String,
        val code: String,
        val description: String,
        val costs: Array<Cost>
    ) {
        data class Cost(
            val id: Int,
            val name: String,
            val count: Int
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TraitData

            if (name != other.name) return false
            if (code != other.code) return false
            if (description != other.description) return false
            if (!costs.contentEquals(other.costs)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + code.hashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + costs.contentHashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Troop

        if (id != other.id) return false
        if (name != other.name) return false
        if (rarity != other.rarity) return false
        if (kingdomId != other.kingdomId) return false
        if (type != other.type) return false
        if (colors != other.colors) return false
        if (spellId != other.spellId) return false
        if (description != other.description) return false
        if (rarityId != other.rarityId) return false
        if (typeCode1 != other.typeCode1) return false
        if (typeCode2 != other.typeCode2) return false
        if (maxArmor != other.maxArmor) return false
        if (maxLife != other.maxLife) return false
        if (maxAttack != other.maxAttack) return false
        if (maxMagic != other.maxMagic) return false
        if (kingdomName != other.kingdomName) return false
        if (imageUrl != other.imageUrl) return false
        if (pageUrl != other.pageUrl) return false
        if (!traits.contentEquals(other.traits)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + (rarity?.hashCode() ?: 0)
        result = 31 * result + (kingdomId ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (colors ?: 0)
        result = 31 * result + (spellId ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (rarityId ?: 0)
        result = 31 * result + (typeCode1?.hashCode() ?: 0)
        result = 31 * result + (typeCode2?.hashCode() ?: 0)
        result = 31 * result + (maxArmor ?: 0)
        result = 31 * result + (maxLife ?: 0)
        result = 31 * result + (maxAttack ?: 0)
        result = 31 * result + (maxMagic ?: 0)
        result = 31 * result + (kingdomName?.hashCode() ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (pageUrl?.hashCode() ?: 0)
        result = 31 * result + traits.contentHashCode()
        return result
    }
}