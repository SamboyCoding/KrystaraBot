package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?platform command
 *
 * @author r3byass
 */
public class PlatformCommand extends KrystaraCommand
{

    public PlatformCommand()
    {
        commandName = "platform";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!chnl.getID().equals(IDReference.BOTCOMMANDSCHANNEL) && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            sdr.getOrCreatePMChannel().sendMessage("To reduce spam, 'platform' can only be used in the #bot-commands channel. Thanks!");
            return;
        }

        String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();

        if (arguments.size() < 1)
        {
            chnl.sendMessage("Please specify a platform.");
            return;
        }
        String role = arguments.get(0).toLowerCase();
        if (role.equals("pc/mobile") || role.equals("pc") || role.equals("mobile"))
        {
            sdr.addRole(chnl.getGuild().getRoleByID(IDReference.PCMOBILEROLE));
            chnl.sendMessage(sdr.mention() + ", you joined **PC/Mobile**");
            Utilities.logEvent(LogType.PLATFORMASSIGN, "**" + nameOfSender + "** assigned themselves to **PC/Mobile**");
        } else
        {
            if (role.equals("console"))
            {
                sdr.addRole(chnl.getGuild().getRoleByID(IDReference.CONSOLEROLE));
                chnl.sendMessage(sdr.mention() + ", you joined **Console**");
                Utilities.logEvent(LogType.PLATFORMASSIGN, "**" + nameOfSender + "** assigned themselves to **Console**");
            } else
            {
                chnl.sendMessage("Please enter a valid platform. Valid platforms are: \"Pc/Mobile\" or \"Console\".");
            }
        }
    }

    @Override
    public String getHelpText()
    {
        return "Assigns you to a platform. You can be on none, one, or both of the platforms at any time.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?platform [pc|mobile|console]";
    }

    @Override
    public String getCommand()
    {
        return "platform";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
