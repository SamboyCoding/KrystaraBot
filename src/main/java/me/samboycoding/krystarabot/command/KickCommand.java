package me.samboycoding.krystarabot.command;

import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

import static me.samboycoding.krystarabot.command.CommandType.MOD;

/**
 *
 * @author r3byass
 */
public class KickCommand extends KrystaraCommand
{

    public KickCommand()
    {
        commandName = "kick";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()) != null ? sdr.getNicknameForGuild(msg.getGuild()) : sdr.getName();

        if (msg.getMentions().size() < 1)
        {
            chnl.sendMessage("You need an @mention of a user to kick!");
            return;
        }
        if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            IUser usr = msg.getMentions().get(0);

            if (usr == null)
            {
                chnl.sendMessage("Invaild @mention!");
                return;
            }

            String nameOfUser = usr.getNicknameForGuild(msg.getGuild()) != null ? usr.getNicknameForGuild(msg.getGuild()) : usr.getName();

            chnl.getGuild().kickUser(usr);

            Utilities.logEvent(LogType.KICK, "**" + nameOfSender + "** kicked user **" + nameOfUser + "**");
            chnl.sendMessage("User \"" + nameOfUser + "\" kicked.");
            msg.delete();
        } else
        {
            chnl.sendMessage("You cannot do that!");
        }
    }

    @Override
    public String getHelpText()
    {
        return "Kicks the specified user from the server.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?kick [@user]";
    }

    @Override
    public String getCommand()
    {
        return "kick";
    }

    @Override
    public CommandType getCommandType()
    {
        return MOD;
    }
}
