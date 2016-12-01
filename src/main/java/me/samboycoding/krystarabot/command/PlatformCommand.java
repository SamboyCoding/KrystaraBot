package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import me.samboycoding.krystarabot.utilities.IDReference;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?platform command
 * @author r3byass
 */
public class PlatformCommand implements IKrystaraCommand
{

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
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
            chnl.getGuild().getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**[ROLE]** - **" + nameOfSender + "** assigned themselves to **PC/Mobile**");
        } else
        {
            if (role.equals("console"))
            {
                sdr.addRole(chnl.getGuild().getRoleByID(IDReference.CONSOLEROLE));
                chnl.sendMessage(sdr.mention() + ", you joined **Console**");
                chnl.getGuild().getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**[ROLE]** - **" + nameOfSender + "** assigned themselves to **Console**");
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
        return "?plaform [pc|mobile|console]";
    }

    @Override
    public String getCommand()
    {
        return "platform";
    }

}
