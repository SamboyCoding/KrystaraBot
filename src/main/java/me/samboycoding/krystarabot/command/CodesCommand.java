package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import me.samboycoding.krystarabot.main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?codes command
 *
 * @author r3byass
 */
public class CodesCommand implements IKrystaraCommand
{

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        ArrayList<String> codes = main.codes.getLiveCodes();
        if (codes.isEmpty())
        {
            sdr.getOrCreatePMChannel().sendMessage("No codes are currently \"Alive\".");
            return;
        }
        msg.delete();
        sdr.getOrCreatePMChannel().sendMessage("Currently \"Alive\" codes: `" + codes.toString().replace("[", "").replace("]", "").replace("\"", "") + "`.");
    }

    @Override
    public String getHelpText()
    {
        return "Lists all the currently \"Alive\" codes.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?codes";
    }

    @Override
    public String getCommand()
    {
        return "codes";
    }

}
