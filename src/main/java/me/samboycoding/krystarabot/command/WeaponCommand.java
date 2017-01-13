package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import me.samboycoding.krystarabot.Language;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.Search;
import me.samboycoding.krystarabot.gemdb.Weapon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Represents the ?weapon command
 *
 * @author Emily
 */
public class WeaponCommand extends KrystaraCommand
{

    public WeaponCommand()
    {
        commandName = "weapon";
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

        String weaponName = String.join(" ", arguments);
        Search search = Search.fromQuery("weapons?term=" + URLEncoder.encode(weaponName, "UTF-8"), lang);
        Weapon.Summary weaponSummary = AshClient.getSingleResult(chnl, search.getWeapons(), weaponName, lang);
        if (weaponSummary == null)
        {
            return;
        }

        Weapon weapon = weaponSummary.getDetails(lang);
        String spellDesc = weapon.getSpellDescription();
        String spellMagicScalingText = weapon.getSpellMagicScalingText();
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
        String spellBoostRatioText = weapon.getSpellBoostRatioText();
        if (spellBoostRatioText != null)
        {
            spellDesc += spellBoostRatioText;
        }

        //Emojis
        IGuild g = chnl.getGuild();

        GemColor[] gemColors = GemColor.fromInteger(weapon.getColors());
        String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);

        String info = "";
        info += weapon.getRarity() + "\n\n";
        info += weapon.getSpellName() + " (" + weapon.getSpellCost() + " " + String.join(" ", gemColorEmojis) + ")\n" + spellDesc;

        EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(weapon.getName())
                .withUrl(weapon.getPageUrl())
                .withThumbnail(weapon.getImageUrl())
                .build();
        chnl.sendMessage("", o, false);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified weapon.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?weapon [name]";
    }

    @Override
    public String getCommand()
    {
        return "weapon";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
