package me.samboycoding.krystarav2.model

data class Kingdom(
    val id: Int,
    val name: String,
    val bannerName: String?,
    val isUsed: Boolean?,
    val isFullKingdom: Boolean?,
    val bannerImageUrl: String?,
    val imageUrl: String?,
    val pageUrl: String?,
    val bgImageUrl: String?
)