package me.samboycoding.krystarav2

import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.simple.SimpleLoggerFactory
import java.lang.System.getenv

class KrystaraBot {
    lateinit var token: String
    lateinit var jda: JDA

    fun init(token: String) {
        this.token = token

        val manager = ReactiveEventManager()

        manager.on<ReadyEvent>()
            .next()
            .subscribe(this::onReady)

        manager.on<MessageReceivedEvent>()
            .subscribe(CommandHandler::handleEvent)

        jda = JDABuilder()
            .setToken(token)
            .setEventManager(manager)
            .build()
    }

    private fun onReady(event: ReadyEvent) {
        botLogger.info("Ready in ${event.guildTotalCount} guilds")
    }

    companion object {
        val botLogger: Logger = SimpleLoggerFactory().getLogger("Bot")
        val instance = KrystaraBot()
    }
}

fun main() {
    val token = getenv("DISCORD_TOKEN") ?: throw AssertionError("Discord token is null")

    KrystaraBot.instance.init(token)
}