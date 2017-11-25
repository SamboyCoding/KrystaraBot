package me.samboycoding.krystarabot;

import me.samboycoding.krystarabot.command.*;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.NicknameChangedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.*;

import java.awt.*;
import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * Main event listener
 *
 * @author Sam
 */
public class Listener
{

    public static UserDatabaseHandler dbHandler = Main.databaseHandler;

    //<editor-fold defaultstate="collapsed" desc="ReadyEvent handler">
    @EventSubscriber
    public void onReady(ReadyEvent e) throws DiscordException, RateLimitException, MissingPermissionsException
    {
        IDiscordClient cl = Main.getClient(null);
        Main.logToBoth("Beginning ReadyEvent Init...");
        try
        {
            Main.logToBoth("Attempting to change username, please wait...");
            switch (IDReference.ENVIRONMENT)
            {
                case LIVE:
                    Main.logToBoth("Logging in to LIVE server.");
                    RequestBuffer.request(() -> cl.changeUsername("Krystara"));
                    break;
                case DEV:
                    Main.logToBoth("Logging in to TESTING server.");
                    RequestBuffer.request(() -> cl.changeUsername("Krystara *Testing*"));
                    break;
                default:
                    Main.logToBoth("Logging in to LYYATESTING server.");
                    RequestBuffer.request(() -> cl.changeUsername("Krystara *LyyaTesting*"));
                    break;
            }
            //Main.logToBoth("Changing image...");
            //RequestBuffer.request(() -> cl.changeAvatar(Image.forUrl("png", "http://repo.samboycoding.me/static/krystarabot_icon.png")));
        } catch (DiscordException ex)
        {
            Main.logToBoth("Failed to change username. Rate limited most likely. Message: " + ex.getMessage());
        }
        try
        {
            Main.logToBoth("Setting status...");
            cl.changePlayingText("?help");
            Main.logToBoth("My ID: " + Main.getClient(null).getApplicationClientID());
            IDReference.MYID = Main.getClient(null).getOurUser().getLongID();
            Main.logToBoth("Registering commands...");

            Main.registerCommand(new BanCommand());
            Main.registerCommand(new ClassCommand());
            Main.registerCommand(new ClearCommand());
            Main.registerCommand(new HelpCommand());
            Main.registerCommand(new KickCommand());
            Main.registerCommand(new KingdomCommand());
            Main.registerCommand(new LanguageCommand());
            Main.registerCommand(new ListQuestionsCommand());
            Main.registerCommand(new PingCommand());
            Main.registerCommand(new PlatformCommand());
            Main.registerCommand(new QuestionCommand());
            Main.registerCommand(new QuizCommand());
            Main.registerCommand(new SearchCommand());
            Main.registerCommand(new ServerstatsCommand());
            Main.registerCommand(new SpellCommand());
            Main.registerCommand(new StopQuizCommand());
            Main.registerCommand(new TeamCommand());
            Main.registerCommand(new Top10Command());
            Main.registerCommand(new TraitCommand());
            Main.registerCommand(new TraitsCommand());
            Main.registerCommand(new TraitstoneCommand());
            Main.registerCommand(new TroopCommand());
            Main.registerCommand(new UserstatsCommand());
            Main.registerCommand(new WarnCommand());
            Main.registerCommand(new WeaponCommand());
            
            String timestamp = "";
            Calendar now = Main.getNow();
            timestamp += now.get(Calendar.DATE) + " of ";
            timestamp += new DateFormatSymbols().getMonths()[now.get(Calendar.MONTH)];
            timestamp += " " + now.get(Calendar.YEAR) + " at ";
            timestamp += Main.getTimestamp("hh:mm:ss") + ".";
            
            EmbedObject o = new EmbedBuilder()
                    .withAuthorName("KrystaraBot")
                    .withAuthorIcon("http://repo.samboycoding.me/static/krystarabot_icon.png")
                    .withColor(Color.green)
                    .withDesc("Bot started on " + timestamp)
                    .withTitle("Hello, world!")
                    .build();
            
            e.getClient().getGuildByID(IDReference.SERVERID).getChannelByID(IDReference.LOGSCHANNEL).sendMessage("", o, false);

            Main.logToBoth("Finished processing readyEvent. Bot is 100% up now.\n\n");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    //</editor-fold>

    @EventSubscriber
    public void onCommand(MessageReceivedEvent e)
    {
        IMessage msg = e.getMessage();
        IUser sdr = msg.getAuthor();
        IChannel chnl = msg.getChannel();
        
        try
        {
            if (chnl.isPrivate())
            {
                if (msg.getContent().startsWith("?"))
                {
                    chnl.sendMessage("Sorry, the bot doesn't support PM commands. Please re-enter the command in a server.");
                } else
                {
                    chnl.setTypingStatus(true);
                    String result = Main.cleverBot.think(msg.getContent());
                    chnl.setTypingStatus(false);
                    chnl.sendMessage(result);
                }
                return;
            }
            if (e.getMessage().getAuthor().getLongID() == IDReference.MYID)
            {
                return; //Do not process own messages. (I don't think this happens, but still.)
            }
            if (e.getMessage().getChannel().getLongID() == 247417978440777728L)
            {
                //Dev #bot-updates channel
                return;
            }

            if (chnl.equals(Main.quizH.getQuizChannel()) && Main.quizH.isQuizRunning())
            {
                Main.quizH.handleAnswer(msg);
                return;
            }
            
            String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()) != null ? sdr.getNicknameForGuild(msg.getGuild()) : sdr.getName();
            String content = msg.getContent();

            //Message Counter
            dbHandler.countMessage(sdr, chnl.getGuild());
            
            if (!content.startsWith("?"))
            {
                //Not a command.
                return;
            }
            
            dbHandler.countCommand(sdr, chnl.getGuild());
            
            String command;
            ArrayList<String> arguments = new ArrayList<>();
            String argumentsFull = "";
            Language lang = null;
            
            if (content.contains(" "))
            {
                //If it contains a space, process all the arguments.
                command = content.substring(1, content.indexOf(" ")).toLowerCase(); //From the character after the '?' to the character before the first space.
                arguments.addAll(Arrays.asList(content.trim().substring(content.indexOf(" ") + 1, content.length()).split(" "))); //From the character after the first space, to the end.
                argumentsFull = content.trim().substring(content.indexOf(" ") + 1, content.length());
            } else
            {
                //Otherwise, leave them blank.
                command = content.substring(1, content.length()).toLowerCase();
            }

            Main.logToBoth("Recieved Command: \"" + command + "\" from user \"" + nameOfSender + "\" in channel \"" + chnl.getName() + "\"");

            TreeMap<String, KrystaraCommand> commands = Main.getCommands();
            
            doCommand(commands, command, sdr, chnl, msg, arguments, argumentsFull);
        } catch (InvalidParameterException ipe)
        {
            try
            {
                chnl.sendMessage(ipe.getMessage());
            } catch (Exception doubleException)
            {
                Main.logToBoth("Exception logging exception! Original exception: ");
                ipe.printStackTrace();
                Main.logToBoth("Exception while logging: ");
                doubleException.printStackTrace();
            }
        } catch (RateLimitException rle)
        {
            try
            {
                Main.logToBoth("Rate limited! Time until un-ratelimited: " + rle.getRetryDelay() + "ms");
                Main.getClient(null).getGuildByID(IDReference.SERVERID).getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**[RATELIMIT]** - Bot needs to slow down! We're rate limited for another " + rle.getRetryDelay() + " milliseconds, please tell SamboyCoding or MrSnake that the following section is too fast: " + rle.getMethod());
            } catch (Exception e2)
            {
                Main.logToBoth("Exception sending ratelimit warning!");
                e2.printStackTrace();
            }
        } catch (Exception ex)
        {
            try
            {
                chnl.sendMessage("Something went wrong! Please direct one of the bot devs to the logs channel!");
                
                String exceptionName = ex.getClass().getName();
                String fileName = ex.getStackTrace()[0].toString();
                
                chnl.getGuild().getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**Error Occurred** (" + (ex.getMessage() == null ? "No further information" : ex.getMessage()) + "): ```\n" + exceptionName + " occurred at " + fileName + "\n```");
                ex.printStackTrace();
            } catch (Exception doubleException)
            {
                Main.logToBoth("Exception logging exception! Original exception: ");
                ex.printStackTrace();
                Main.logToBoth("Exception while logging: ");
                doubleException.printStackTrace();
            }
        }
    }
    //</editor-fold>

    private void doCommand(TreeMap<String, KrystaraCommand> commands, String command, IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argumentsFull) throws Exception
    {
        // Localized; try to get a language
        String[] parts = command.split("\\.");
        command = parts[0];
        
        if (!commands.containsKey(command))
        {
            throw new InvalidParameterException("Invalid command \"" + command + "\". Type `?help` for a list of commands.");
        }
        
        KrystaraCommand commandObj = commands.get(command);
        Language lang = null;
        
        if (parts.length > 1)
        {
            // User attempted to select a language; attempt to get the requested language
            lang = Language.fromShortCode(parts[1]);
        }
        
        if ((lang == null) && commandObj.isLocalized())
        {
            // No language was explicitly selected, but default to the language of the current channel
            if (chnl.getLongID() == IDReference.CHATFRENCH)
            {
                lang = Language.FRENCH;
            } else if (chnl.getLongID() == IDReference.CHATGERMAN)
            {
                lang = Language.GERMAN;
            } else if (chnl.getLongID() == IDReference.CHATITALIAN)
            {
                lang = Language.ITALIAN;
            } else if (chnl.getLongID() == IDReference.CHATSPANISH)
            {
                lang = Language.SPANISH;
            }
        }
        
        if (lang != null)
        {
            if (!commandObj.isLocalized())
            {
                // This command doesn't support a language
                throw new InvalidParameterException("This command does not accept a language suffix.");
            }
            commands.get(command).handleCommand(sdr, chnl, msg, arguments, argumentsFull, lang);
        } else
        {
            commands.get(command).handleCommand(sdr, chnl, msg, arguments, argumentsFull);
        }
    }
    
    @EventSubscriber
    public void onJoin(UserJoinEvent e)
    {
        try
        {
            String nameOfUser = e.getUser().getNicknameForGuild(e.getGuild()) != null ? e.getUser().getNicknameForGuild(e.getGuild()) : e.getUser().getName();
            Utilities.logEvent(LogType.USERJOIN, "User **" + nameOfUser + "** has joined the server!");
            
            if (e.getGuild().getUsers().size() % 100 == 0)
            {
                Utilities.logEvent(LogType.MILESTONE, "The server now has " + e.getGuild().getUsers().size() + " users!");
            }
        } catch (Exception ignored)
        {
            //Ignore.
        }
    }
    
    @EventSubscriber
    public void talk(MentionEvent e) throws Exception
    {
        if (e.getMessage().getContent().contains(e.getMessage().getGuild().getEveryoneRole().mention()))
        {
            //@everyone mentioned, not me personally.
            return;
        }
        if (e.getMessage().getChannel().getLongID() == IDReference.BOTCOMMANDSCHANNEL)
        {
            //Message sent in bot-commands
            String message = e.getMessage().getContent().replace(Main.getClient(null).getOurUser().mention() + " ", "");
            
            e.getMessage().getChannel().setTypingStatus(true);
            String result = Main.cleverBot.think(message);
            e.getMessage().getChannel().setTypingStatus(false);
            e.getMessage().getChannel().sendMessage(result);
        } else
        {
            if (!e.getMessage().getChannel().isPrivate())
            {
                //Message sent in a channel that's not bot-commands
                e.getMessage().delete();
                e.getMessage().getAuthor().getOrCreatePMChannel().sendMessage("Sorry, I don't talk in any channel apart from #bot-commands. Alternatively you can talk to me here, in PM, but don't @mention me... I don't need it in here.");
            } else
            {
                //@Mentioned in a PM
                e.getMessage().getChannel().sendMessage("Don't @mention me in PM! I don't like it. Just say what you want to say. ;(");
            }
        }
    }
    
    @EventSubscriber
    public void onLeave(UserLeaveEvent e)
    {
        try
        {
            String nameOfUser = e.getUser().getNicknameForGuild(e.getGuild()) != null ? e.getUser().getNicknameForGuild(e.getGuild()) : e.getUser().getName();
            
            Utilities.logEvent(LogType.USERLEAVE, "User **" + nameOfUser + "** left the server!");
        } catch (Exception ignored)
        {
            //Ignore.
        }
    }
    
    @EventSubscriber
    public void onChangeName(NicknameChangedEvent e)
    {
        try
        {
            String old = e.getOldNickname().isPresent() ? e.getOldNickname().get() : e.getUser().getName();
            String newName = e.getNewNickname().isPresent() ? e.getNewNickname().get() : e.getUser().getName();
            
            Utilities.logEvent(LogType.RENAME, "User **" + old + "** changed their name to **" + newName + "**");
        } catch (Exception ignored)
        {
            //Ignore.
        }
    }
}
