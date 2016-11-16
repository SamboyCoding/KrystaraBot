package me.samboycoding.krystarabot;

import me.samboycoding.krystarabot.utilities.IDReference;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import me.samboycoding.krystarabot.utilities.Command;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 * Main class
 *
 * @author Sam
 */
public class main
{

    private static final ArrayList<Command> registeredCommands = new ArrayList<>();
    private static IDiscordClient cl;
    public static GameData data = new GameData();

    public static IDiscordClient getClient(String token)
    {
        if (cl != null)
        {
            return cl;
        }
        if (token == null)
        {
            System.err.println("Not logged in and no token specified!");
            return null;
        }
        try
        {
            // Returns an instance of the Discord client
            ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
            clientBuilder.withToken(token); // Adds the login info to the builder

            return clientBuilder.login(); // Creates the client instance and logs the client in

        } catch (DiscordException ex)
        {
            System.err.println("Error getting client! Details:");
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws DiscordException, RateLimitException, IOException
    {
        log("Attempting to log in... please wait");
        cl = getClient(IDReference.MYTOKEN);
        cl.getDispatcher().registerListener(new Listener());
        log("Logged in and listener registered.");
        data.importData();
    }
    
    public static void registerCommand(Command c)
    {
        registeredCommands.add(c);
    }
    
    public static Boolean removeCommand(Command c)
    {
        return registeredCommands.remove(c);
    }

    public static void log(String msg)
    {
        DateFormat df = new SimpleDateFormat("[dd/MM/yy | HH:mm:ss] ");
        Date dateobj = new Date();
        String timestamp = df.format(dateobj);
        System.out.println("****" + timestamp + " [BOT MAIN]          " + msg);
    }
}
