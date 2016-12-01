package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import me.samboycoding.krystarabot.main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Represents the ?help command
 *
 * @author Sam
 */
public class HelpCommand extends KrystaraCommand
{

    public HelpCommand()
    {
        commandName = "help";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        ArrayList<KrystaraCommand> cmdList = main.getCommands();

        String helpText = "I recognize the following commands:\n\n";
        
        for (KrystaraCommand normalCmd : cmdList)
        {
            if (normalCmd.requiresAdmin())
            {
                continue;
            }
            
            helpText += "**" + normalCmd.getUsage() + "**: " + normalCmd.getHelpText() + "\n";
        }
        
        helpText += "\nAdmin Commands (These WILL be logged):\n\n";
        
        for (KrystaraCommand adminCommand : cmdList)
        {
            if (!adminCommand.requiresAdmin())
            {
                continue;
            }
            
            helpText += "**" + adminCommand.getUsage() + "**: " + adminCommand.getHelpText() + "\n";
        }
        
        sdr.getOrCreatePMChannel().sendMessage(helpText);
    }

    @Override
    public String getHelpText()
    {
        return "Shows this help text.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?help";
    }

    @Override
    public String getCommand()
    {
        return "help";
    }

}
