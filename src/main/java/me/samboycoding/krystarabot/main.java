package me.samboycoding.krystarabot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
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

    private static IDiscordClient cl;
    private static GameData data = new GameData();

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
    
    public static void log(String msg)
    {
        System.out.println("****[BOT MAIN]          " + msg);
    }
}
