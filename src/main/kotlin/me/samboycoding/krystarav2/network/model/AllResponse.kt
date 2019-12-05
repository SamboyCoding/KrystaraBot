package me.samboycoding.krystarav2.network.model

import me.samboycoding.krystarav2.model.*

data class AllResponse(
    val troops: ArrayList<Troop>,
    val traits: ArrayList<Trait>,
    val spells: ArrayList<Spell>,
    val kingdoms: ArrayList<Kingdom>,
    val classes: ArrayList<Class>,
    val weapons: ArrayList<Weapon>
)