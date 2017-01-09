package me.samboycoding.krystarabot;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import java.io.File;
import me.samboycoding.krystarabot.utilities.IDReference;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import me.samboycoding.krystarabot.command.KrystaraCommand;
import me.samboycoding.krystarabot.quiz.AshQuizQuestionFactory;
import me.samboycoding.krystarabot.quiz.JsonQuizQuestionFactory;
import me.samboycoding.krystarabot.quiz.QuizHandler;
import me.samboycoding.krystarabot.quiz.QuizQuestionFactory;
import org.apache.commons.io.FileUtils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

/**
 * Main class
 *
 * @author Sam
 */
public class main
{

    private static final TreeMap<String, KrystaraCommand> commands = new TreeMap<>();

    private static IDiscordClient cl;
    public static GameData data = new GameData();
    public static UserDatabaseHandler databaseHandler = new UserDatabaseHandler();
    public static File logFile;
    public static QuizHandler quizH;
    public static QuizQuestionFactory quizQuestionFactory;
    public static ChatterBotSession cleverBot;

    static
    {
        try
        {
            switch (IDReference.ENVIRONMENT)
            {
                case LYYA:
                case DEV:
                    quizQuestionFactory = new AshQuizQuestionFactory();
                    break;
                default:
                    quizQuestionFactory = new JsonQuizQuestionFactory();
                    break;
            }

            quizH = new QuizHandler(quizQuestionFactory);
        } catch (Exception e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

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
    public static void main(String[] args) throws Exception
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
        new Thread(new GameDataLoaderThread(), "GameData Loading Thread").start();
        new IDReference(); //Init
        databaseHandler.loadJSON();
        logToBoth("Initializing Intelligent Talking...");
        ChatterBotFactory factory = new ChatterBotFactory();

        ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT);
        cleverBot = bot1.createSession();

        logToBoth("Intelligent talking loaded!");
    }

    public static void registerCommand(KrystaraCommand c)
    {
        logToBoth("Registering" + (c.requiresAdmin() ? " ADMIN" : "") + " command ?" + c.getCommand());
        commands.put(c.getCommand(), c);
    }

    @SuppressWarnings("unchecked")
    public static TreeMap<String, KrystaraCommand> getCommands()
    {
        return (TreeMap<String, KrystaraCommand>) commands.clone();
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
}
