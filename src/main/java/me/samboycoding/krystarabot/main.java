package me.samboycoding.krystarabot;

import java.io.File;
import me.samboycoding.krystarabot.utilities.IDReference;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import me.samboycoding.krystarabot.utilities.AdminCommand;
import me.samboycoding.krystarabot.utilities.Command;
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

    private static final ArrayList<Command> registeredCommands = new ArrayList<>();
    private static final ArrayList<AdminCommand> registeredAdminCommands = new ArrayList<>();
    private static IDiscordClient cl;
    public static GameData data = new GameData();
    public static CodesHandler codes = new CodesHandler();
    public static MessageCounterHandler messageCounter = new MessageCounterHandler();

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
        IDReference.MYTOKEN = FileUtils.readFileToString(new File("token.txt"), Charset.defaultCharset());
        log("Logging in with token: \"" + IDReference.MYTOKEN + "\"");
        cl = getClient(IDReference.MYTOKEN);
        cl.getDispatcher().registerListener(new Listener());
        log("Logged in and listener registered.");
        data.importData();
        codes.loadJSON();
        messageCounter.loadJSON();
    }
    
    public static void registerCommand(Command c)
    {
        registeredCommands.add(c);
    }
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Command> getRegisteredCommands()
    {
        return (ArrayList<Command>) registeredCommands.clone();
    }

    public static void log(String msg)
    {
        DateFormat df = new SimpleDateFormat("[dd/MM/yy | HH:mm:ss] ");
        Date dateobj = new Date();
        String timestamp = df.format(dateobj);
        System.out.println("****" + timestamp + " [BOT MAIN]          " + msg);
    }

    public static void registerAdminCommand(AdminCommand c)
    {
        registeredAdminCommands.add(c);
    }
    
    @SuppressWarnings("unchecked")
    public static ArrayList<AdminCommand> getRegisteredAdminCommands()
    {
        return (ArrayList<AdminCommand>) registeredAdminCommands.clone();
    }
}
