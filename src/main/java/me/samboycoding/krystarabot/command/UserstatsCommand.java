package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.List;
import static me.samboycoding.krystarabot.Listener.messageCounter;
import static me.samboycoding.krystarabot.command.CommandType.SERVER;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;

/**
 *
 * @author r3byass
 */
public class UserstatsCommand extends KrystaraCommand
{

    public UserstatsCommand()
    {
        commandName = "userstats";
    }
        
    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if(chnl.getID() != IDReference.BOTCOMMANDSCHANNEL && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            sdr.getOrCreatePMChannel().sendMessage("To reduce spam, userstats can only be used in the #bot-commands channel. Thanks!");
            return;
        }
        
        IUser userstatsUsr;
        if (arguments.isEmpty())
        {
            userstatsUsr = sdr;
        } else
        {
            String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
            userstatsUsr = chnl.getGuild().getUserByID(id);
        }
        String name = userstatsUsr.getName();
        String nickname = userstatsUsr.getNicknameForGuild(chnl.getGuild()).isPresent() ? userstatsUsr.getNicknameForGuild(chnl.getGuild()).get() : userstatsUsr.getName();
        Boolean hasNick = !name.equals(nickname);
        Status state = userstatsUsr.getStatus();
        List<IRole> sdrRoles = userstatsUsr.getRolesForGuild(chnl.getGuild());
        int numRolesSdr = sdrRoles.size() - 1; //-1 to remove @everyone
        int messageCount = messageCounter.getMessageCountForUser(sdr, chnl.getGuild());
        int commandCount = messageCounter.getCommandCountForUser(sdr, chnl.getGuild());

        List<String> sdrRolesNice = new ArrayList<>();
        for (IRole r : sdrRoles)
        {
            if (r.isEveryoneRole())
            {
                continue;
            }
            if (r.getID().equals(IDReference.MUTEDROLE))
            {
                continue;
            }
            sdrRolesNice.add(r.getName());
        }

        String toSend = "```\n--User Info---------";
        toSend += "\nName: " + name;
        toSend += "\nHas Nickname: " + hasNick;
        if (hasNick)
        {
            toSend += "\nNickname: " + nickname;
        }
        toSend += "\nIs playing: " + state.getType().equals(Status.StatusType.GAME);
        toSend += "\nIs streaming: " + state.getType().equals(Status.StatusType.STREAM);
        toSend += "\nStream URL: " + (state.getUrl().isPresent() ? state.getUrl().get() : "None");
        toSend += "\nGame: " + (state.getStatusMessage() == null ? "nothing" : "\"" + state.getStatusMessage() + "\"");
        toSend += "\nNumber of roles: " + numRolesSdr;
        toSend += "\nList of Roles: " + sdrRolesNice.toString();
        toSend += "\nMessages sent: " + messageCount;
        toSend += "\nCommands sent: " + commandCount;
        toSend += "\n```";

        chnl.sendMessage(toSend);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information on you, or the specified user";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?userstats [@user (optional)]";
    }

    @Override
    public String getCommand()
    {
        return "userstats";
    }

    @Override
    public CommandType getCommandType()
    {
        return SERVER;
    }
}
