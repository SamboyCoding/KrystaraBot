package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.TreeMap;
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
        TreeMap<String, KrystaraCommand> cmdList = main.getCommands();

        ArrayList<String> helpText = new ArrayList<>();
        
        helpText.add("Every command must start with '**?**' followed by the command name. Some commands have required or optional parameters shown in square brackets.\n\n"
                + "Here is a list of all commands you can use:\n\n");
        
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
            for (KrystaraCommand cmd : cmdList.values())
            {
                if (cmd.getCommandType() != c)
                {
                    continue;
                }
                cmdTypeHelp += cmd.getUsage() + ": " + cmd.getHelpText() + "\n"; //command + helptext
            }
            cmdTypeHelp += "```\n";
            helpText.add(cmdTypeHelp);
        }
        
        Utilities.sendLargeMessage(sdr.getOrCreatePMChannel(), helpText);

        //helpText += Utilities.repeatString("-", 60) + "```";
        msg.delete();
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
