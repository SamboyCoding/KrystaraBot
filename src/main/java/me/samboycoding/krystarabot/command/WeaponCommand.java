package me.samboycoding.krystarabot.command;

import java.net.URL;
import java.util.ArrayList;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?troop command
 *
 * @author Sam
 */
public class WeaponCommand extends KrystaraCommand
{

    public WeaponCommand()
    {
        commandName = "weapon";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!GameData.dataLoaded)
        {
            chnl.sendMessage("Sorry, the data hasn't been loaded (yet). Please try again shortly, and if it still doesn't work, contact one of the bot devs.");
            return;
        }
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a weapon to search for!");
            return;
        }

        JSONObject weaponInfo;

        String weaponName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
        weaponInfo = main.data.getTroopByName(weaponName);

        if (weaponInfo == null)
        {
            ArrayList<String> results = main.data.searchForWeapon(weaponName);

            if (results.isEmpty())
            {
                chnl.sendMessage("No weapon `" + weaponName + "` found, " + sdr.mention());
                return;
            }
            if (results.size() > 5)
            {
                chnl.sendMessage("Search term is ambiguous (" + results.size() + " results). Please refine your search.");
                return;
            }
            if (results.size() > 1)
            {
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + weaponName + "\" is ambiguous.", results);
                return;
            }

            weaponInfo = main.data.getWeaponByName(results.get(0));
        }

        weaponName = weaponInfo.getString("Name");
        String rarity = weaponInfo.getString("WeaponRarity");
        String spell = weaponInfo.getJSONObject("Spell").getString("Name");
        int summonCost = weaponInfo.getJSONObject("Spell").getInt("Cost");

        //get spell description
        JSONObject weaponSpell = main.data.getSpellInfo(spell);
        String weaponSpellDesc = weaponSpell.getString("Description");

        ArrayList<String> manaTypes = new ArrayList<>();

        if (weaponInfo.getJSONObject("ManaColors").getBoolean("ColorBlue"))
        {
            manaTypes.add(chnl.getGuild().getEmojiByName("mana_blue").toString());
        }
        if (weaponInfo.getJSONObject("ManaColors").getBoolean("ColorRed"))
        {
            manaTypes.add(chnl.getGuild().getEmojiByName("mana_red").toString());
        }
        if (weaponInfo.getJSONObject("ManaColors").getBoolean("ColorBrown"))
        {
            manaTypes.add(chnl.getGuild().getEmojiByName("mana_brown").toString());
        }
        if (weaponInfo.getJSONObject("ManaColors").getBoolean("ColorPurple"))
        {
            manaTypes.add(chnl.getGuild().getEmojiByName("mana_purple").toString());
        }
        if (weaponInfo.getJSONObject("ManaColors").getBoolean("ColorYellow"))
        {
            manaTypes.add(chnl.getGuild().getEmojiByName("mana_yellow").toString());
        }
        if (weaponInfo.getJSONObject("ManaColors").getBoolean("ColorGreen"))
        {
            manaTypes.add(chnl.getGuild().getEmojiByName("mana_green").toString());
        }

        String info = "**" + weaponName + "**\n" + rarity + " weapon\n\n**Spell**";
        info += "\n" + spell + " (" + summonCost + ")\n" + weaponSpellDesc;
        info += "\n" + manaTypes.toString().replace("[", "").replace("]", "").replace(", ", "");

        chnl.sendMessage(info);
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
