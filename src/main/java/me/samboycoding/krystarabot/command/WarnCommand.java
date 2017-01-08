package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author r3byass
 */
public class WarnCommand extends KrystaraCommand
{

    public WarnCommand()
    {
        commandName = "warn";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();

        if (arguments.size() < 2)
        {
            chnl.sendMessage("You need a minimum of an @mention and a message to send. (That's a minimum of two arguments)");
        }
        if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
            IUser usr = chnl.getGuild().getUserByID(id);
            if (usr == null)
            {
                chnl.sendMessage("Invaild @mention!");
                return;
            }

            String nameOfUser = usr.getNicknameForGuild(msg.getGuild()).isPresent() ? usr.getNicknameForGuild(msg.getGuild()).get() : usr.getName();

            @SuppressWarnings("unchecked")
            ArrayList<String> messageArray = (ArrayList<String>) arguments.clone();

            messageArray.remove(0); //Remove the @mention
            String message = messageArray.toString().replace("[", "").replace("]", "").replace(",", "");

            usr.getOrCreatePMChannel().sendMessage("Warning from user **" + nameOfSender + "** in channel **" + chnl.getName() + "**. Text:```\n" + message + "```");

            Utilities.logEvent(LogType.WARN, "**" + nameOfUser + "** was warned by **" + nameOfSender + "**. Message: ```\n" + message + "```");
            msg.delete();
        } else
        {
            chnl.sendMessage("You cannot do that!");
        }
    }

    @Override
    public String getHelpText()
    {
        return "Sends a PM warning to the specified user.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?warn [@user] [message]";
    }

    @Override
    public String getCommand()
    {
        return "warn";
    }

    @Override
    public CommandType getCommandType()
    {
        return MOD;
    }
}
