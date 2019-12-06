package me.samboycoding.krystarav2

import me.samboycoding.krystarav2.commands.BaseCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object CommandHandler {
    val commands: ArrayList<BaseCommand> = arrayListOf(

    )

    fun handleEvent(event: MessageReceivedEvent) {
        val msg = event.message
        val guild = msg.guild

        if(!msg.contentRaw.startsWith("?")) return

        val data = msg.getCommandData(guild)

        KrystaraBot.botLogger.info("Received command ${data.commandExecuted}")

        //Find command
        val cmd = commands.find { it.getCommandWords().contains(data.commandExecuted) }

        //Execute if non-null
        try {
            cmd?.doExecute(msg, msg.member!!, guild, msg.textChannel)
        } catch (e: Exception) {
            KrystaraBot.botLogger.error("Exception executing command:", e)
            try {
                msg.channel.sendMessage(cmd!!.getString("exceptionExecutingCommand")).submit()
            } catch (e: Exception) {
                //Ignore, possible outage or something
            }
        }
    }
}