package me.samboycoding.krystarabot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Main event listener
 *
 * @author Sam
 */
public class Listener
{

    @EventSubscriber
    public void onReady(ReadyEvent e) throws DiscordException, RateLimitException, MissingPermissionsException
    {
        IDiscordClient cl = main.getClient(null);
        try
        {
            cl.changeUsername("Krystara");
        } catch (DiscordException ex)
        {
            main.log("Failed to change username. Rate limited most likely.");
        }
        try
        {
            cl.changeStatus(Status.game("Made by SamboyCoding and MrSnake"));
            main.log("My ID: " + main.getClient(null).getApplicationClientID());
            IDReference.MYID = main.getClient(null).getApplicationClientID();
            //logToLogChannel("Bot started", cl.getGuildByID(IDReference.SERVERID));
            main.log("Finished processing readyEvent. Bot is 100% up now.\n\n");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void logToLogChannel(String msg, IGuild srv) throws MissingPermissionsException, RateLimitException, DiscordException
    {
        try
        {
            DateFormat df = new SimpleDateFormat("[dd/MM/yy | HH:mm:ss] ");
            Date dateobj = new Date();
            String timestamp = df.format(dateobj);
            if (msg.length() > 1999)
            {
                msg = msg.substring(0, 1900);
                msg += "\n-----SNIPPED TO FIT 2000 CHAR LIMIT-----";
            }
            srv.getChannelByID(IDReference.LOGCHANNELID).sendMessage(timestamp + msg);
        } catch (Exception e)
        {
            main.log("Something went wrong, while logging something. PERMISSIONS?!");
            main.log("**********BEGIN ERROR REPORT**********");
            e.printStackTrace(); //
            main.log("**********END ERROR REPORT**********");
        }
    }

    @EventSubscriber
    public void onCommand(MessageReceivedEvent e)
    {
        try
        {

            IMessage msg = e.getMessage();
            IUser sdr = msg.getAuthor();
            String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
            IChannel chnl = msg.getChannel();
            String content = msg.getContent();
            if (!content.startsWith("?"))
            {
                //Not a command.
                return;
            }
            String command;
            String arguments;
            if (content.contains(" "))
            {
                command = content.substring(1, content.indexOf(" ")); //From the character after the '?' to the character before the first space.
                arguments = content.trim().substring(content.indexOf(" ") + 1, content.length()); //From the character after the first space, to the end.
            } else
            {
                command = content.substring(1, content.length());
                arguments = "";
            }
            System.out.println("Recieved Command: " + command + " from user \"" + nameOfSender + "\"");
            switch (command)
            {
                case "ping":
                    String lagTime = ((Long) (System.currentTimeMillis() - msg.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())).toString();
                    chnl.sendMessage("Pong! `" + lagTime + "ms lag`.");
                    break;
                default:
                    chnl.sendMessage("Invalid command \"" + command + "\"");
                    break;
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
