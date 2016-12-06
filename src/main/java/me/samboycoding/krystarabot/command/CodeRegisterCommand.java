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
public class CodeRegisterCommand extends KrystaraCommand
{
    
    public CodeRegisterCommand()
    {
        commandName = "coderegister";
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
        chnl.sendMessage("Successfully " + (arg.equals("yes") ? "registered" : "unregistered") + " for PM codes.");
    }

    @Override
    public String getHelpText()
    {
        return "Toggles interest in new GOW codes. If this is set to \"Yes\", then when a new code is posted you will get a PM.";
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
        return "?coderegister [yes|no]";
    }

    @Override
    public String getCommand()
    {
        return "coderegister";
    }

}
