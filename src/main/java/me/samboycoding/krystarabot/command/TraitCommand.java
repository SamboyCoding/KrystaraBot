package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.main;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?trait command
 *
 * @author Sam
 */
public class TraitCommand extends KrystaraCommand
{

    public TraitCommand()
    {
        commandName = "trait";
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
        String traitName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
        JSONObject traitInfo = main.data.getTraitByName(traitName);
        if (traitInfo == null)
        {
            chnl.sendMessage("No trait `" + traitName + "` found, " + sdr.mention());
            return;
        }
        traitName = traitInfo.getString("Name");
        String traitDesc = traitInfo.getString("Description");

        String result = "**" + traitName + "**: " + traitDesc + "\n";

        HashSet<JSONObject> troopMap = new HashSet<>();
        for (Object oTroop : GameData.arrayTroops)
        {
            JSONObject troop = (JSONObject)oTroop;
            JSONArray traitTable = troop.getJSONArray("ParsedTraits");
            for (Object oSearchTrait : traitTable)
            {
                JSONObject searchTrait = (JSONObject)oSearchTrait;
                if (searchTrait.getString("Code").equals(traitInfo.getString("Code")))
                {
                    troopMap.add(troop);
                }
            }
        }
        
        if (!troopMap.isEmpty())
        {
            JSONObject[] oTroops = troopMap.toArray(new JSONObject[0]);
            ArrayList<JSONObject> troops = new ArrayList<>(Arrays.asList(oTroops));
            troops.sort((t1, t2) -> t1.getString("Name").compareTo(t2.getString("Name")));
            String[] troopNames = troops.stream().map(t -> t.getString("Name")).toArray(String[]::new);
            result += "Used by: " + String.join(", ", troopNames) + "\n";
        }
        
        chnl.sendMessage(result);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified trait.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?trait [name]";
    }

    @Override
    public String getCommand()
    {
        return "trait";
    }
    
    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }

}
