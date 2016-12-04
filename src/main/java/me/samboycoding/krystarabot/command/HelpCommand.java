package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.SERVER;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

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

        String helpText = "Every command must start with '**?**' followed by the command name. Some commands have required or optional parameters shown in square brackets.\n\n"
                + "Here is a list of all commands you can use:\n\n";
        
        sendMessageSafe(helpText, sdr.getOrCreatePMChannel());

        for (CommandType c : CommandType.values())
        {
            String cmdTypeHelp = "```\n";
            if (c == CommandType.BOTDEV && !Utilities.userHasRole(chnl.getGuild(), sdr, chnl.getGuild().getRolesByName("Bot-Dev").get(0)))
            {
                //Botdev commands, but not botdev
                continue;
            }
            if (c == CommandType.MOD && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
            {
                //Mod commands, but cannot use
                continue;
            }
            cmdTypeHelp += c.toString() + "\n" + Utilities.repeatString("-", 60) + "\n"; //command category header
            for (KrystaraCommand cmd : main.getCommands())
            {
                if (cmd.getCommandType() != c)
                {
                    continue;
                }
                cmdTypeHelp += cmd.getUsage() + ": " + cmd.getHelpText() + "\n"; //command + helptext
            }
            cmdTypeHelp += "```\n";
            sendMessageSafe(cmdTypeHelp, sdr.getOrCreatePMChannel());
        }

        //helpText += Utilities.repeatString("-", 60) + "```";
        msg.delete();
        chnl.sendMessage(sdr.mention() + ", check your PMs for a command list.");
    }

    /**
     * Sends the specified message to the specified channel, splitting it up if
     * necessary to get it below the 2000 char limit
     *
     * @param message The message to send
     * @param chnl The channel to send it to
     * @throws DiscordException If there is a miscellaneous error during sending
     * @throws RateLimitException If the bot is rate-limited
     * @throws MissingPermissionsException If the bot is missing the
     * SENDMESSAGES permission
     */
    private void sendMessageSafe(String message, IChannel chnl) throws DiscordException, RateLimitException, MissingPermissionsException
    {
        int charsSent = 0;
        if (message.length() > 2000)
        {
            while (charsSent < message.length())
            {
                if (message.length() > 2000)
                {
                    String temp = message.substring(0, 1999);
                    int lastNewline = temp.lastIndexOf("\n");
                    String toSend = message.substring(0, lastNewline + 1); //+1 to include \n char
                    chnl.sendMessage(toSend);
                    charsSent += toSend.length();
                    message = message.substring(lastNewline + 1);
                } else
                {
                    chnl.sendMessage(message);
                    charsSent += message.length();
                }
            }
        }
        chnl.sendMessage(message);
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
