package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.samboycoding.krystarabot.Language;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.Nameable;
import me.samboycoding.krystarabot.gemdb.Search;
import me.samboycoding.krystarabot.gemdb.Spell;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Represents the ?spell command
 *
 * @author Emily
 */
public class SpellCommand extends KrystaraCommand
{

    public SpellCommand()
    {
        commandName = "spell";
    }

    private String getListAsString(List<? extends Nameable> list)
    {
        String[] names = list.stream().map(t -> t.getName()).toArray(String[]::new);
        return String.join(", ", names);
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
            chnl.sendMessage(lang.localize(Language.LocString.PLEASE_SPECIFY_TERM_TO_SEARCH));
            return;
        }

        String spellName = String.join(" ", arguments);
        Search search = Search.fromQuery("spells?term=" + URLEncoder.encode(spellName, "UTF-8"), lang);
        Search.SpellSummary spellSummary = AshClient.getSingleResult(chnl, search.getSpells(), spellName, lang);
        if (spellSummary == null)
        {
            return;
        }

        Spell spell = spellSummary.getDetails(lang);
        String spellDesc = spell.getDescription();
        String spellMagicScalingText = spell.getMagicScalingText();
        if (spellMagicScalingText != null)
        {
            if (!spellDesc.contains("{2}"))
            {
                spellDesc = spellDesc.replace("{1}", spellMagicScalingText);
            } else
            {
                spellDesc = spellDesc.replace("{1}", "(" + lang.localize(Language.LocString.MAGIC_SCALING_HALF) + ")");
                spellDesc = spellDesc.replace("{2}", spellMagicScalingText);
            }
        }
        String spellBoostRatioText = spell.getBoostRatioText();
        if (spellBoostRatioText != null)
        {
            spellDesc += spellBoostRatioText;
        }

        //Emojis
        IGuild g = chnl.getGuild();

        GemColor[] gemColors = GemColor.fromInteger(spell.getColors());
        String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);

        String info = "(" + spell.getCost() + " " + String.join(" ", gemColorEmojis) + "): " + spellDesc + "\n";
        if (!spell.getTroops().isEmpty())
        {
            info += lang.localize(Language.LocString.USED_BY_TROOPS) + " " + getListAsString(spell.getTroops()) + "\n";
        }
        if (!spell.getWeapons().isEmpty())
        {
            info += lang.localize(Language.LocString.USED_BY_WEAPONS) + " " + getListAsString(spell.getWeapons()) + "\n";
        }

        EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(spell.getName())
                .withThumbnail(spell.getImageUrl())
                .build();
        chnl.sendMessage("", o, false);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified spell.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?spell [name]";
    }

    @Override
    public String getCommand()
    {
        return "spell";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
