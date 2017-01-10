package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.Search;
import me.samboycoding.krystarabot.gemdb.Weapon;
import me.samboycoding.krystarabot.utilities.Utilities;
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
public class AshWeaponCommand extends KrystaraCommand
{

    public AshWeaponCommand()
    {
        commandName = "weapon";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            chnl.sendMessage("You cannot do that!");
            return;
        }
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }

        String weaponName = String.join(" ", arguments);
        Search search = Search.fromQuery("weapons?term=" + URLEncoder.encode(weaponName, "UTF-8"));
        Weapon.Summary weaponSummary = AshClient.getSingleResult(chnl, search.getWeapons(), "weapon", weaponName);
        if (weaponSummary == null)
        {
            return;
        }

        Weapon weapon = weaponSummary.getDetails();
        String spellDesc = weapon.getSpellDescription();
        String spellMagicScalingText = weapon.getSpellMagicScalingText();
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
