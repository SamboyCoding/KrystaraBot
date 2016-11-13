package me.samboycoding.krystarabot;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.Role;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RoleBuilder;

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
            logToLogChannel("Bot started", main.getClient(null).getGuildByID(IDReference.SERVERID));
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
            main.log("Something went wrong, while reporting that something went wrong. PERMISSIONS?!");
            main.log("**********BEGIN ERROR REPORT**********");
            main.log(e.getLocalizedMessage());
            for (StackTraceElement e2 : e.getStackTrace())
            {
                main.log(e2.toString());
            }
            main.log("**********END ERROR REPORT**********");
        }
    }
}
