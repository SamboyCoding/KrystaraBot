package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import static me.samboycoding.krystarabot.command.CommandType.SERVER;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?top10 command
 *
 * @author r3byass
 */
public class Top10Command extends KrystaraCommand
{

    public Top10Command()
    {
        commandName = "top10";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!chnl.getID().equals(IDReference.BOTCOMMANDSCHANNEL) && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            sdr.getOrCreatePMChannel().sendMessage("To reduce spam, top10 can only be used in the #bot-commands channel. Thanks!");
            return;
        }

        String operation = null;

        if (arguments.isEmpty())
        {
            operation = "messages";
        } else
        {
            String arg = arguments.get(0).toLowerCase().trim();

            switch (arg)
            {
                case "messages":
                case "chat":
                    operation = "messages";
                    break;
                case "quiz":
                    operation = "quiz";
                    break;
                default:
                    chnl.sendMessage("No operation `" + arg + "` found. Please use 'messages' or 'quiz'");
                    return;
            }
        }

        LinkedHashMap<IUser, Integer> unordered = new LinkedHashMap<>();
        ValueComparator comp = new ValueComparator((Map<IUser, Integer>) unordered);
        TreeMap<IUser, Integer> ordered = new TreeMap<>(comp);

        // Ignore bot and bot-dev IDs
        ArrayList<String> ignoreIds = new ArrayList<>(Arrays.asList(
                IDReference.MYID, "190663943260340224", "102450956045668352", "234202516676542464"
        ));

        switch (operation)
        {
            case "messages":

                main.databaseHandler.getUserIDList(chnl.getGuild()).stream().filter((id)
                        -> !(ignoreIds.contains(id))).map((id)
                        -> chnl.getGuild().getUserByID(id)).forEach((current)
                        ->
                {
                    unordered.put(current, main.databaseHandler.getMessageCountForUser(current, chnl.getGuild()));
                });

                ordered.putAll(unordered); //Now it's sorted, by values

                String toSend = "```\nTOP 10 USERS (BY MESSAGE COUNT) IN SERVER\nName" + Utilities.repeatString(" ", 56) + "Number of messages\n";

                int count = 0;
                int numSpaces = 60;
                for (IUser u : ordered.descendingKeySet())
                {
                    String usrName = (u.getNicknameForGuild(chnl.getGuild()) != null ? u.getNicknameForGuild(chnl.getGuild()) : u.getName()).replaceAll("[^A-Za-z0-9 ]", "").trim();
                    count++;

                    toSend += "\n" + usrName + Utilities.repeatString(" ", numSpaces - usrName.length()) + unordered.get(u);
                    if (count > 10)
                    {
                        break;
                    }
                }

                toSend += "\n```";

                chnl.sendMessage(toSend);
                break;
            case "quiz":

                main.databaseHandler.getUserIDList(chnl.getGuild()).stream().filter((id) -> !(id.equals(IDReference.MYID))).map((id) -> chnl.getGuild().getUserByID(id)).forEach((current)
                        ->
                {
                    //Skip the bot.
                    unordered.put(current, main.databaseHandler.getQuizScore(current, chnl.getGuild()));
                });

                ordered.putAll(unordered); //Now it's sorted, by values

                String toSend1 = "```\nTOP 10 USERS (BY QUIZ SCORE) IN SERVER\nName" + Utilities.repeatString(" ", 56) + "Quiz Score\n";

                int count1 = 0;
                numSpaces = 60;
                for (IUser u : ordered.descendingKeySet())
                {
                    String usrName = (u.getNicknameForGuild(chnl.getGuild()) != null ? u.getNicknameForGuild(chnl.getGuild()) : u.getName()).replaceAll("[^A-Za-z0-9 ]", "").trim();
                    count1++;

                    toSend1 += "\n" + usrName + Utilities.repeatString(" ", numSpaces - usrName.length()) + unordered.get(u);
                    if (count1 > 10)
                    {
                        break;
                    }
                }

                toSend1 += "\n```";

                chnl.sendMessage(toSend1);
                break;
        }
    }

    @Override
    public String getHelpText()
    {
        return "Shows the top 10 users on the server, either by quiz points or messages sent.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?top10 [quiz|messages]";
    }

    @Override
    public String getCommand()
    {
        return "top10";
    }

    @Override
    public CommandType getCommandType()
    {
        return SERVER;

    }

    public static class ValueComparator implements Comparator<IUser>
    {

        Map<IUser, Integer> base;

        public ValueComparator(Map<IUser, Integer> base)
        {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.    
        @Override
        public int compare(IUser a, IUser b)
        {
            if (base.get(a) >= base.get(b))
            {
                return 1;
            } else
            {
                return -1;
            } // returning 0 would merge keys
        }
    }
}
