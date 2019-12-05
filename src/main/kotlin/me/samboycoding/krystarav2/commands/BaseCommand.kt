package me.samboycoding.krystarav2.commands

import me.samboycoding.krystarav2.getCommandData
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*

abstract class BaseCommand {
    private lateinit var context: CommandData

    protected open fun getCommandWord(): String {
        throw NotImplementedError("Command must either override getCommandWord or override getCommandWords to not call getCommandWord.")
    }

    open fun getCommandWords(): List<String> {
        return arrayListOf(getCommandWord())
    }

    protected abstract fun execute(
        msg: Message,
        author: Member,
        guild: Guild,
        channel: TextChannel
    )

    fun doExecute(msg: Message, author: Member, guild: Guild, channel: TextChannel) {
        synchronized(this) {
            context = msg.getCommandData(guild)
            execute(msg, author, guild, channel)
        }
    }

    fun getString(key: String): String {
        //Channel override if exists else guild config locale
        //TODO
        return ""
    }

    protected fun getString(key: String, vararg objects: Any): String {
        val unformatted = getString(key)
        return unformatted.format(*objects)
    }

    protected fun Message.sendMissingPermissionMessage() {
        channel.doSend(getString("missingPermission", author.asMention))
    }

    protected fun failIfBotMissingPerm(perm: Permission): Boolean {
        if (!context.guild.selfMember.hasPermission(perm)) {
            context.message.sendMisconfigurationMessage(perm.name)
            return false
        }

        return true
    }


    private fun Message.sendMisconfigurationMessage(missing: String) {
        channel.doSend(getString("botMissingPermission", missing))
    }

    protected fun MessageChannel.doSend(message: String) {
        sendMessage(message).queue()
    }
}