package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import me.samboycoding.krystarabot.GameData;
import me.samboycoding.krystarabot.main;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?trait command
 *
 * @author Sam
 */
public class TraitCommand implements IKrystaraCommand
{

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
        JSONObject traitInfo = main.data.getTraitInfo(traitName);
        if (traitInfo == null)
        {
            chnl.sendMessage("No trait `" + traitName + "` found, " + sdr.mention());
            return;
        }
        traitName = traitInfo.getString("Name");
        String traitDesc = traitInfo.getString("Description");

        chnl.sendMessage("**" + traitName + "**: " + traitDesc);
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

}
