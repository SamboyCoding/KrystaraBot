/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import me.samboycoding.krystarabot.Language;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.Search;
import me.samboycoding.krystarabot.gemdb.Troop;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class TroopCommand extends QuestionCommand
{

    public TroopCommand()
    {
        commandName = "troop";

    }

    @Override
    public Boolean isLocalized()
    {
        return true;
    }
    
    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        handleCommand(sdr, chnl, msg, arguments, argsFull, Language.ENGLISH);
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull, Language lang) throws Exception
    {
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }

        String troopName = String.join(" ", arguments);
        Search search = Search.fromQuery("troops?term=" + URLEncoder.encode(troopName, "UTF-8"), lang);
        Troop.Summary troopSummary = AshClient.getSingleResult(chnl, search.getTroops(), troopName, lang);
        if (troopSummary == null)
        {
            return;
        }

        Troop troop = troopSummary.getDetails(lang);
        String spellDesc = troop.getSpellDescription();
        String spellMagicScalingText = troop.getSpellMagicScalingText();
        if (spellMagicScalingText != null)
        {
            if (!spellDesc.contains("{2}"))
            {
                spellDesc = spellDesc.replace("{1}", spellMagicScalingText);
            } else
            {
                spellDesc = spellDesc.replace("{1}", "(half)");
                spellDesc = spellDesc.replace("{2}", spellMagicScalingText);
            }
        }
        String spellBoostRatioText = troop.getSpellBoostRatioText();
        if (spellBoostRatioText != null)
        {
            spellDesc += spellBoostRatioText;
        }

        //Emojis
        IGuild g = chnl.getGuild();
        String emojiArmor = g.getEmojiByName("gow_armor").toString();
        String emojiLife = g.getEmojiByName("gow_life").toString();
        String emojiAttack = g.getEmojiByName("gow_attack").toString();
        String emojiMagic = g.getEmojiByName("gow_magic").toString();

        GemColor[] gemColors = GemColor.fromInteger(troop.getColors());
        String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);

        String[] traitNames = troop.getTraits().stream().map(t -> t.getName()).toArray(String[]::new);

        String info = "";
        info += troop.getRarity() + " _" + troop.getKingdomName() + "_ " + troop.getType() + "\n";
        info += "(" + String.join(", ", traitNames) + ")\n\n";
        info += "_" + troop.getSpellName() + "_ (" + troop.getSpellCost() + " " + String.join(" ", gemColorEmojis) + ")\n" + spellDesc + "\n\n";

        info += emojiArmor + troop.getMaxArmor() + "   " + emojiLife + troop.getMaxLife()
                + "   " + emojiAttack + troop.getMaxAttack() + "   " + emojiMagic + troop.getMaxMagic() + "\n";
        info += "_" + troop.getDescription() + "_";

        EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(troop.getName())
                .withUrl(troop.getPageUrl())
                .withThumbnail(troop.getImageUrl())
                .build();
        chnl.sendMessage("", o, false);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified troop.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?troop [name]";
    }

    @Override
    public String getCommand()
    {
        return "troop";
    }

    @Override
    public CommandType getCommandType()
    {
        return MOD;
    }
}
