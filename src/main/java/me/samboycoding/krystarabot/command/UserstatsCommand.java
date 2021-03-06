package me.samboycoding.krystarabot.command;

import me.samboycoding.krystarabot.Main;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.List;

import static me.samboycoding.krystarabot.Listener.dbHandler;
import static me.samboycoding.krystarabot.command.CommandType.SERVER;

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
        if (chnl.getLongID() != IDReference.BOTCOMMANDSCHANNEL && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
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
            userstatsUsr = chnl.getGuild().getUserByID(Long.parseLong(id));
            if (userstatsUsr == null)
            {
                chnl.sendMessage("Invalid @mention! Please @mention a valid user!");
                return;
            }
        }
        String name = userstatsUsr.getName();
        String nickname = userstatsUsr.getNicknameForGuild(chnl.getGuild()) != null ? userstatsUsr.getNicknameForGuild(chnl.getGuild()) : userstatsUsr.getName();
        Boolean hasNick = !name.equals(nickname);
        IPresence state = userstatsUsr.getPresence();
        List<IRole> sdrRoles = userstatsUsr.getRolesForGuild(chnl.getGuild());
        int numRolesSdr = sdrRoles.size() - 1; //-1 to remove @everyone
        int messageCount = dbHandler.getMessageCountForUser(sdr, chnl.getGuild());
        int commandCount = dbHandler.getCommandCountForUser(sdr, chnl.getGuild());

        List<String> sdrRolesNice = new ArrayList<>();
        for (IRole r : sdrRoles)
        {
            if (r.isEveryoneRole())
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
        toSend += "\nIs playing: " + state.getPlayingText().isPresent();
        toSend += "\nIs streaming: " + state.getStreamingUrl().isPresent();
        toSend += "\nStream URL: " + (state.getStreamingUrl().isPresent() ? state.getStreamingUrl().get() : "None");
        toSend += "\nGame: " + (state.getPlayingText().isPresent() ? "\"" + state.getPlayingText().get() + "\"" : "nothing");
        toSend += "\nNumber of roles: " + numRolesSdr;
        toSend += "\nList of Roles: " + sdrRolesNice.toString();
        toSend += "\nMessages sent: " + messageCount;
        toSend += "\nCommands sent: " + commandCount;
        toSend += "\nQuiz Points: " + Main.databaseHandler.getQuizScore(userstatsUsr, chnl.getGuild());
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
