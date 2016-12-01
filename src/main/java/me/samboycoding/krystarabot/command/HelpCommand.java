package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.SERVER;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

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

        for (CommandType c : CommandType.values())
        {
            helpText += c.toString() + "\n" + Utilities.repeatString("-", 60) + "\n";
            for (KrystaraCommand cmd : main.getCommands())
            {
                if (cmd.getCommandType() != c)
                {
                    continue;
                }
                helpText += "**" + cmd.getUsage() + "**: " + cmd.getHelpText() + "\n";
            }
            helpText += "\n";
        }
        
        helpText += Utilities.repeatString("-", 60);

        msg.delete();
        int charsSent = 0;
        if (helpText.length() > 2000)
        {
            while (charsSent < helpText.length())
            {
                if (helpText.length() > 2000)
                {
                    String temp = helpText.substring(0, 1999);
                    int lastNewline = temp.lastIndexOf("\n");
                    String toSend = helpText.substring(0, lastNewline + 1); //+1 to include \n char
                    sdr.getOrCreatePMChannel().sendMessage(toSend);
                    charsSent += toSend.length();
                    helpText = helpText.substring(lastNewline + 1);
                } else
                {
                    sdr.getOrCreatePMChannel().sendMessage(helpText);
                    charsSent += helpText.length();
                }
            }
        }
        sdr.getOrCreatePMChannel().sendMessage(helpText);
        chnl.sendMessage(sdr.mention() + ", check your PMs for a command list.");
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

    @Override
    public CommandType getCommandType()
    {
        return SERVER;
    }
}
