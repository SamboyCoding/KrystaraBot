package me.samboycoding.krystarabot.command;

import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

import static me.samboycoding.krystarabot.Listener.dbHandler;
import static me.samboycoding.krystarabot.command.CommandType.SERVER;

/**
 * Represents the ?serverstats command
 *
 * @author r3byass
 */
public class ServerstatsCommand extends KrystaraCommand
{

    public ServerstatsCommand()
    {
        commandName = "serverstats";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (chnl.getLongID() != IDReference.BOTCOMMANDSCHANNEL && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            sdr.getOrCreatePMChannel().sendMessage("To reduce spam, serverstats can only be used in the #bot-commands channel. Thanks!");
            return;
        }

        List<IRole> guildRoles = chnl.getGuild().getRoles();
        int numRolesGuild = guildRoles.size() - 1; //Again, -1 to remove @everyone

        String toSendServer = "```\n--Server Info---------";
        toSendServer += "\nRoles: " + numRolesGuild;
        toSendServer += "\nChannels: " + chnl.getGuild().getChannels().size();
        toSendServer += "\nMembers: " + chnl.getGuild().getUsers().size();

        int msgCount = 0;
        int cmdCount = 0;
        for (String id : dbHandler.getUserIDList(chnl.getGuild()))
        {
            msgCount += dbHandler.getMessageCountForUser(chnl.getGuild().getUserByID(Long.parseLong(id)), chnl.getGuild());
            cmdCount += dbHandler.getCommandCountForUser(chnl.getGuild().getUserByID(Long.parseLong(id)), chnl.getGuild());
        }

        toSendServer += "\nMessages sent: " + msgCount;
        toSendServer += "\nCommands sent: " + cmdCount;
        toSendServer += "\n--Roles Info---------";

        int unassigned = chnl.getGuild().getUsers().size();
        for (IRole r2 : guildRoles)
        {
            if (r2.isEveryoneRole())
            {
                continue;
            }
            if (r2.getName().equals("KrystaraBot"))
            {
                continue;
            }
            toSendServer += "\n" + chnl.getGuild().getUsersByRole(r2).size() + "x " + r2.getName();
        }
        unassigned = chnl.getGuild().getUsers().stream().filter((u) -> (Utilities.userHasRole(chnl.getGuild(), u, chnl.getGuild().getRoleByID(IDReference.CONSOLEROLE)) || Utilities.userHasRole(chnl.getGuild(), u, chnl.getGuild().getRoleByID(IDReference.PCMOBILEROLE)))).map((_item) -> 1).reduce(unassigned, (accumulator, _item) -> accumulator - 1);
        toSendServer += "\n" + unassigned + "x Not Assigned";
        toSendServer += "\n```";

        chnl.sendMessage(toSendServer);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information on the server.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?serverstats";
    }

    @Override
    public String getCommand()
    {
        return "serverstats";
    }

    @Override
    public CommandType getCommandType()
    {
        return SERVER;
    }
}
