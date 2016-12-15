package me.samboycoding.krystarabot.command;

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
 * Represents the ?spell command
 *
 * @author Sam
 */
public class SpellCommand extends KrystaraCommand
{

    public SpellCommand()
    {
        commandName = "spell";
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
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }
        String spellName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
        ArrayList<String> results = main.data.searchForSpell(spellName);
        
        if(results.isEmpty())
        {
            chnl.sendMessage("No spell `" + spellName + "` found, " + sdr.mention());
            return;
        }
        if(results.size() > 5)
        {
            chnl.sendMessage("Search term is ambiguous (" + results.size() + " results). Please refine your search.");
            return;
        }
        if(results.size() > 1)
        {
            Utilities.sendDisambiguationMessage(chnl, "Search term \"" + spellName + "\" is ambiguous.", results);
            return;
        }
        
        JSONObject spellInfo = main.data.getSpellInfo(results.get(0));

        spellName = spellInfo.getString("Name");
        String spellDesc = spellInfo.getString("Description");
        int spellCost = spellInfo.getInt("Cost");

        chnl.sendMessage("**" + spellName + " (" + spellCost + "):** " + spellDesc);
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
