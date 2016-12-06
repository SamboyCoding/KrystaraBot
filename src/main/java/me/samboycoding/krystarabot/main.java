package me.samboycoding.krystarabot;

import java.io.File;
import me.samboycoding.krystarabot.utilities.IDReference;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import me.samboycoding.krystarabot.command.KrystaraCommand;
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
    
    private static final ArrayList<KrystaraCommand> commands = new ArrayList<>();
    
    private static IDiscordClient cl;
    public static GameData data = new GameData();
    public static CodesHandler codes = new CodesHandler();
    public static MessageCounterHandler messageCounter = new MessageCounterHandler();
    public static File logFile;

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

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(String[] args) throws DiscordException, RateLimitException, IOException
    {
        new File("logs/").mkdir();
        logFile = new File("logs/output-" + getTimestamp("dd-MM-yyyy") + ".log");
        logFile.createNewFile();
        logToFile("\n\n\n*******NEW SESSION*******\n");
        logToBoth("Attempting to log in... please wait");
        IDReference.MYTOKEN = FileUtils.readFileToString(new File("token.txt"), Charset.defaultCharset());
        logToBoth("Logging in with token: \"" + IDReference.MYTOKEN + "\"");
        cl = getClient(IDReference.MYTOKEN);
        cl.getDispatcher().registerListener(new Listener());
        logToBoth("Logged in and listener registered.");
        //data.importData();
        new Thread(new GameDataLoaderThread(), "GameData Loading Thread").start();
        codes.loadJSON();
        new IDReference(); //Init
        messageCounter.loadJSON();
    }

    public static void registerCommand(Command c)
    {
        registeredCommands.add(c);
    }
    
    public static void registerCommand(KrystaraCommand c)
    {
        logToBoth("Registering" + (c.requiresAdmin() ? " ADMIN" : "") + " command ?" + c.getCommand());
        commands.add(c);
        
        Collections.sort(commands);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Command> getRegisteredCommands()
    {
        return (ArrayList<Command>) registeredCommands.clone();
    }
    
    @SuppressWarnings("unchecked")
    public static ArrayList<KrystaraCommand> getCommands()
    {
        return (ArrayList<KrystaraCommand>) commands.clone();
    }    

    public static void logToBoth(String msg)
    {
        String logEntry = "****" + getTimestamp("[dd/MM/yy | HH:mm:ss] ") + " [BOT MAIN]          " + msg;
        System.out.println(logEntry);
        try
        {
            FileUtils.writeStringToFile(logFile, logEntry + "\n", Charset.defaultCharset(), true);
        } catch (Exception ex)
        {
            //Ignore - cannot write to file.
        }
    }
    
    public static void logToFile(String msg)
    {
        String logEntry = "****" + getTimestamp("[dd/MM/yy | HH:mm:ss] ") + " [BOT MAIN]          " + msg;
        try
        {
            FileUtils.writeStringToFile(logFile, logEntry + "\n", Charset.defaultCharset(), true);
        } catch (Exception ex)
        {
            //Ignore - cannot write to file.
        }
    }

    public static String getTimestamp(String format)
    {
        DateFormat df = new SimpleDateFormat(format);
        Date dateobj = new Date();
        String timestamp = df.format(dateobj);
        return timestamp;
    }
    
    public static Calendar getNow()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return cal;
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
