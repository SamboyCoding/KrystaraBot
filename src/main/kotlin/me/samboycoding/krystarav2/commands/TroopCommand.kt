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

        channel.sendTyping().queue()

        println("Searching for troop...")
        val response = GowDbService.instance.searchTroops(context.getArg(0, true)!!).execute()
        val troops = response.body()?.troops ?: return channel.doSend("Troop not found.")

        if (troops.size > 1) return channel.doSend("Ambiguous results. Please refine your search term.")

        val searchResult = troops.first()

        println("Fetching troop data...")
        val troop = GowDbService.instance.getTroop(searchResult.id).execute().body()
            ?: return channel.doSend("Failed to load troop data, sorry!")

        println("Fetching kingdom...")
        val kingdom = troop.kingdom!!
        println("Fetching spell...")
        val spell = troop.spell!!

        println("Generating desc...")
        val spellDesc = spell.description?.replace("{1}", spell.magicScalingText ?: "[Err]")

        println("Building embed...")
        val embed = EmbedBuilder()
            .setTitle(troop.name)
            .setDescription(
                "${troop.description}\n\n" +
                        "**Kingdom:** ${kingdom.name}" +
                        "\n**Rarity:** ${troop.rarity}" +
                        "\n**Type:** ${troop.type}"
            )
            .addField("Spell: ${spell.name}", "$spellDesc ${spell.boostRatioText ?: ""}", false)
            .setThumbnail(troop.imageUrl)

        println("Adding traits...")
        for (trait in troop.traits) {
            embed.addField("Trait: ${trait.name}",
                "**Description:** ${trait.description}" +
                        "\n**Costs:**\n${trait.costs.joinToString("\n") { "${it.count} x ${it.name}" }}", false
            )
        }

        println("Sending msg...")
        channel.sendMessage(embed.build()).queue()
    }

    override fun getCommandWords(): List<String> {
        //TODO: Make this go through all the languages
        return listOf("troop")
    }
}