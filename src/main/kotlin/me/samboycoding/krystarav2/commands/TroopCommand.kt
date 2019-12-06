package me.samboycoding.krystarav2.commands

import me.samboycoding.krystarav2.network.GowDbService
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

class TroopCommand : BaseCommand() {
    override fun execute(msg: Message, author: Member, guild: Guild, channel: TextChannel) {
        if (context.argsCount < 1)
            return channel.doSend("Need to specify a troop name.")

        val response = GowDbService.instance.searchTroops(context.getArg(0, true)!!).execute()
        val troops = response.body()?.troops ?: return channel.doSend("Troop not found.")

        if(troops.size > 1) return channel.doSend("Ambiguous results. Please refine your search term.")

        val troop = troops.first()
        val kingdom = troop.kingdom!!
        val spell = troop.spell!!

        val spellDesc = spell.description?.replace("{1}", spell.magicScalingText ?: "[Err]")

        val embed = EmbedBuilder()
            .setTitle(troop.name)
            .setDescription("${troop.description}\n\n" +
                    "**Kingdom:** ${kingdom.name}" +
                    "\n**Rarity:** ${troop.rarity}" +
                    "\n**Type:** ${troop.type}")
            .addField("Spell", "${spell.name}:" +
                    "\n${spellDesc}", false)

        //TODO: Change the call to /troops/:id/details and load trait data.
    }

    override fun getCommandWords(): List<String> {
        //TODO: Make this go through all the languages
        return listOf("troop")
    }
}