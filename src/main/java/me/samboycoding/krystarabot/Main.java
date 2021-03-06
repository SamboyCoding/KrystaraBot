package me.samboycoding.krystarabot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import me.samboycoding.krystarabot.command.KrystaraCommand;
import me.samboycoding.krystarabot.quiz.AshQuizQuestionFactory;
import me.samboycoding.krystarabot.quiz.QuizHandler;
import me.samboycoding.krystarabot.quiz.QuizQuestionFactory;
import me.samboycoding.krystarabot.utilities.IDReference;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

/**
 * Main class
 *
 * @author Sam
 */
public class Main {

    public static final UserDatabaseHandler databaseHandler = new UserDatabaseHandler();
    public static final QuizQuestionFactory quizQuestionFactory = new AshQuizQuestionFactory();
    public static final QuizHandler quizH = new QuizHandler(quizQuestionFactory);
    private static final TreeMap<String, KrystaraCommand> commands = new TreeMap<>();
    static ChatterBotSession cleverBot;
    private static File logFile;
    private static IDiscordClient cl;

    public static IDiscordClient getClient(String token) {
        if (cl != null) {
            return cl;
        }
        if (token == null) {
            throw new IllegalStateException("Not logged in and no token specified!");
        }
        // Returns an instance of the Discord client
        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
        clientBuilder.withToken(token); // Adds the login info to the builder

        return clientBuilder.login(); // Creates the client instance and logs the client in

    }

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(String[] args) throws Exception {
        new File("logs/").mkdir();
        logFile = new File("logs/output-" + getTimestamp("dd-MM-yyyy") + ".log");
        logFile.createNewFile();
        logToFile("\n\n\n*******NEW SESSION*******\n");
        logToBoth("Attempting to log in... please wait");
        IDReference.MYTOKEN = FileUtils.readFileToString(new File("token.txt"), Charset.defaultCharset());
        logToBoth("Logging in with token: \"" + IDReference.MYTOKEN + "\"");
        cl = getClient(IDReference.MYTOKEN);
        cl.getDispatcher().registerListener(new Listener());
        ((Logger) LoggerFactory.getLogger(Discord4J.class)).setLevel(Level.INFO);
        logToBoth("Logged in and listener registered.");
        new IDReference(); //Init
        databaseHandler.loadJSON();
        logToBoth("Initializing Intelligent Talking...");
        ChatterBotFactory factory = new ChatterBotFactory();

        ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT, "CCCmyn6_ngkBj91SpAf0qjiWEMw");
        cleverBot = bot1.createSession();

        logToBoth("Intelligent talking loaded!");
    }

    static void registerCommand(KrystaraCommand c) {
        logToBoth("Registering" + (c.requiresAdmin() ? " ADMIN" : "") + " command ?" + c.getCommand());
        commands.put(c.getCommand(), c);
    }

    @SuppressWarnings("unchecked")
    public static TreeMap<String, KrystaraCommand> getCommands() {
        return (TreeMap<String, KrystaraCommand>) commands.clone();
    }

    public static void logToBoth(String msg) {
        String logEntry = "****" + getTimestamp("[dd/MM/yy | HH:mm:ss] ") + " [BOT MAIN]          " + msg;
        System.out.println(logEntry);
        try {
            FileUtils.writeStringToFile(logFile, logEntry + "\n", Charset.defaultCharset(), true);
        } catch (Exception ex) {
            //Ignore - cannot write to file.
        }
    }

    private static void logToFile(String msg) {
        String logEntry = "****" + getTimestamp("[dd/MM/yy | HH:mm:ss] ") + " [BOT MAIN]          " + msg;
        try {
            FileUtils.writeStringToFile(logFile, logEntry + "\n", Charset.defaultCharset(), true);
        } catch (Exception ex) {
            //Ignore - cannot write to file.
        }
    }

    static String getTimestamp(String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date dateobj = new Date();
        return df.format(dateobj);
    }

    public static Calendar getNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return cal;
    }
}
