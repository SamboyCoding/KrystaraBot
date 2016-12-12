package me.samboycoding.krystarabot.utilities;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import me.samboycoding.krystarabot.main;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Various utilities
 *
 * @author Sam
 */
public class Utilities
{

    /**
     * Logs an event to the log channel, using an embed.
     *
     * @param eventType The type of the event. See
     * {@link me.samboycoding.krystarabot.utilities.LogType}
     * @param text
     * @throws sx.blah.discord.util.RateLimitException If the bot is ratelimited
     * @throws sx.blah.discord.util.DiscordException If a misc. error occurs
     * @throws sx.blah.discord.util.MissingPermissionsException If the bot is
     * missing the SENDMESSAGES permission.
     */
    public static void logEvent(LogType eventType, String text) throws RateLimitException, DiscordException, MissingPermissionsException
    {
        EmbedBuilder bldr;
        if (eventType == null)
        {
            bldr = new EmbedBuilder()
                    .withAuthorName("KrystaraBot")
                    .withAuthorIcon("http://repo.samboycoding.me/static/krystarabot_icon.png")
                    .withTitle("")
                    .withColor(Color.gray)
                    .withDesc(text);
        } else
        {
            bldr = new EmbedBuilder()
                    .withAuthorName("KrystaraBot")
                    .withAuthorIcon("http://repo.samboycoding.me/static/krystarabot_icon.png")
                    .withTitle(eventType.logTitle)
                    .withColor(eventType.color)
                    .withDesc(text);
        }

        main.getClient(null).getGuildByID(IDReference.SERVERID).getChannelByID(IDReference.LOGSCHANNEL).sendMessage("", bldr.build(), false);
    }

    /**
     * Deletes the specified message after the specified delay. This spawns a
     * new thread that terminates after *delay* milliseconds. Exceptions will be
     * ignored.
     *
     * @param msg The message to delete.
     * @param delay The time, in milliseconds, to wait before deleting the
     * message.
     */
    public static void cleanupMessage(IMessage msg, int delay)
    {
        new Timer("MsgSelfDestruct" + System.currentTimeMillis()).schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    msg.delete();
                } catch (Exception e)
                {
                    //Ignore
                }
            }
        }, delay);
    }

    /**
     * Checks if the specified user has the specified role on the specified
     * guild. Exceptions are ignored. For users with a large amount of roles,
     * this may take some time.
     *
     * @param srv The guild to check
     * @param usr The user to check
     * @param role The role to check for
     * @return true if the role is found, false otherwise, including if an
     * exception is thrown.
     */
    public static Boolean userHasRole(IGuild srv, IUser usr, IRole role)
    {
        try
        {
            List<IRole> roles = usr.getRolesForGuild(srv);
            for (IRole r : roles)
            {
                if (r.equals(role))
                {
                    return true;
                }
            }
            return false;
        } catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Returns true if the user has either the admin, dev, or moderator role
     *
     * @param usr The user to check
     * @param server The server to check on.
     * @return Whether the user can use admin commands.
     */
    public static Boolean canUseAdminCommand(IUser usr, IGuild server)
    {
        IRole admin = server.getRoleByID(IDReference.ADMINROLE);
        IRole dev = server.getRoleByID(IDReference.DEVROLE);
        IRole mod = server.getRoleByID(IDReference.MODROLE);

        return userHasRole(server, usr, admin) || userHasRole(server, usr, dev) || userHasRole(server, usr, mod);
    }

    /**
     * Repeats the specified string the specified amount of times.
     *
     * @param s The string to repeat
     * @param amount The amount of times to repeat it
     * @return A string containing the specified string repeated the specified
     * number of times, with no break characters in between.
     */
    public static String repeatString(String s, int amount)
    {
        String res = "";

        for (int i = 0; i < amount; i++)
        {
            res += s;
        }

        return res;
    }

    /**
     * Very complicated function to get the seed from a random. Thanks StackExchange!
     * @param random The random to get the seed for
     * @return  The seed for the random.
     */
    public static long getSeed(Random random)
    {
        byte[] ba0, ba1, bar;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(new Random(0));
            ba0 = baos.toByteArray();
            baos = new ByteArrayOutputStream(128);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(new Random(-1));
            ba1 = baos.toByteArray();
            baos = new ByteArrayOutputStream(128);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(random);
            bar = baos.toByteArray();
        } catch (IOException e)
        {
            throw new RuntimeException("IOException: " + e);
        }
        if (ba0.length != ba1.length || ba0.length != bar.length)
        {
            throw new RuntimeException("Bad serialized length");
        }
        int i = 0;
        while (i < ba0.length && ba0[i] == ba1[i])
        {
            i++;
        }
        int j = ba0.length;
        while (j > 0 && ba0[j - 1] == ba1[j - 1])
        {
            j--;
        }
        if (j - i != 6)
        {
            throw new RuntimeException("6 differing bytes not found");
        }
        return ((bar[i] & 255L) << 40 | (bar[i + 1] & 255L) << 32
                | (bar[i + 2] & 255L) << 24 | (bar[i + 3] & 255L) << 16
                | (bar[i + 4] & 255L) << 8 | (bar[i + 5] & 255L)) ^ 0x5DEECE66DL;
    }
}
