package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?coderegister command
 *
 * @author Sam
 */
public class ReigsterCodeCommand extends KrystaraCommand
{
    
    public ReigsterCodeCommand()
    {
        commandName = "registercode";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if(arguments.size() < 1)
        {
            chnl.sendMessage("Please specify either 'yes' or 'no'");
            return;
        }
        String arg = arguments.get(0);
        
        if(!arg.equals("yes") && !arg.equals("no"))
        {
            chnl.sendMessage("Please specify either 'yes' or 'no'");
            return;
        }
        
        main.databaseHandler.setReceivesCodes(chnl.getGuild(), sdr, arg.equals("yes"));
        sdr.getOrCreatePMChannel().sendMessage("Successfully " + (arg.equals("yes") ? "registered" : "unregistered") + " for PM codes.");
        msg.delete();
    }

    @Override
    public String getHelpText()
    {
        return "Toggles interest in new GOW codes. If this is set to \"Yes\", every new code will be delivered to you via PM.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }

    @Override
    public String getUsage()
    {
        return "?registercode [yes|no]";
    }

    @Override
    public String getCommand()
    {
        return "registercode";
    }

}
