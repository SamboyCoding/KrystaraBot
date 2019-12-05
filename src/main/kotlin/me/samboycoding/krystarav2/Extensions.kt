package me.samboycoding.krystarav2

import me.samboycoding.krystarav2.commands.CommandData
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.VoiceChannel


fun Message.getCommandData(guild: Guild): CommandData {
    val withoutPrefix = contentRaw.removePrefix("!")

    return CommandData(withoutPrefix, guild, this)
}

fun Member.hasAnyPermission(vararg permissions: Permission): Boolean =
    permissions.map { p -> hasPermission(p) }.contains(true)

fun Member.isModerator(): Boolean = hasAnyPermission(
    Permission.MANAGE_ROLES,
    Permission.KICK_MEMBERS,
    Permission.BAN_MEMBERS,
    Permission.ADMINISTRATOR,
    Permission.MANAGE_SERVER
)

fun VoiceChannel.join() {
    guild.audioManager.openAudioConnection(this)
}
