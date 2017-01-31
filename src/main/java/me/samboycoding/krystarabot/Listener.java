package me.samboycoding.krystarabot;

import java.awt.Color;
import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import me.samboycoding.krystarabot.utilities.IDReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TreeMap;
import me.samboycoding.krystarabot.command.*;
import static me.samboycoding.krystarabot.utilities.IDReference.RuntimeEnvironment.DEV;
import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.NicknameChangedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Main event listener
 *
 * @author Sam
 */
public class Listener
{

    public static UserDatabaseHandler dbHandler = main.databaseHandler;

    //<editor-fold defaultstate="collapsed" desc="ReadyEvent handler">
    @EventSubscriber
    public void onReady(ReadyEvent e) throws DiscordException, RateLimitException, MissingPermissionsException
    {
        IDiscordClient cl = main.getClient(null);
        main.logToBoth("Beginning ReadyEvent Init...");
        try
        {
            main.logToBoth("Attempting to change username, please wait...");
            switch (IDReference.ENVIRONMENT)
            {
                case LIVE:
                    main.logToBoth("Logging in to LIVE server.");
                    cl.changeUsername("Krystara");
                    break;
                case DEV:
                    main.logToBoth("Logging in to TESTING server.");
                    cl.changeUsername("Krystara *Testing*");
                    break;
                default:
                    main.logToBoth("Logging in to LYYATESTING server.");
                    cl.changeUsername("Krystara *LyyaTesting*");
                    break;
            }
            main.logToBoth("Changing image...");
            cl.changeAvatar(Image.forUrl("png", "http://repo.samboycoding.me/static/krystarabot_icon.png"));
        } catch (DiscordException ex)
        {
            main.logToBoth("Failed to change username. Rate limited most likely. Message: " + ex.getMessage());
        }
        try
        {
            main.logToBoth("Setting status...");
            cl.changeStatus(Status.game("?help"));
            main.logToBoth("My ID: " + main.getClient(null).getApplicationClientID());
            IDReference.MYID = main.getClient(null).getApplicationClientID();
            main.logToBoth("Registering commands...");

            main.registerCommand(new BanCommand());
            main.registerCommand(new ClassCommand());
            main.registerCommand(new ClearCommand());
            main.registerCommand(new HelpCommand());
            main.registerCommand(new KickCommand());
            main.registerCommand(new KingdomCommand());
            main.registerCommand(new LanguageCommand());
            main.registerCommand(new ListQuestionsCommand());
            main.registerCommand(new PingCommand());
            main.registerCommand(new PlatformCommand());
            main.registerCommand(new QuestionCommand());
            main.registerCommand(new QuizCommand());
            main.registerCommand(new SearchCommand());
            main.registerCommand(new ServerstatsCommand());
            main.registerCommand(new SpellCommand());
            main.registerCommand(new StopQuizCommand());
            main.registerCommand(new TeamCommand());
            main.registerCommand(new Top10Command());
            main.registerCommand(new TraitCommand());
            main.registerCommand(new TraitsCommand());
            main.registerCommand(new TraitstoneCommand());
            main.registerCommand(new TroopCommand());
            main.registerCommand(new UserstatsCommand());
            main.registerCommand(new WarnCommand());
            main.registerCommand(new WeaponCommand());

            String timestamp = "";
            Calendar now = main.getNow();
            timestamp += now.get(Calendar.DATE) + " of ";
            timestamp += new DateFormatSymbols().getMonths()[now.get(Calendar.MONTH)];
            timestamp += " " + now.get(Calendar.YEAR) + " at ";
            timestamp += main.getTimestamp("hh:mm:ss") + ".";

            EmbedObject o = new EmbedBuilder()
                    .withAuthorName("KrystaraBot")
                    .withAuthorIcon("http://repo.samboycoding.me/static/krystarabot_icon.png")
                    .withColor(Color.green)
                    .withDesc("Bot started on " + timestamp)
                    .withTitle("Hello, world!")
                    .build();

            e.getClient().getGuildByID(IDReference.SERVERID).getChannelByID(IDReference.LOGSCHANNEL).sendMessage("", o, false);

            main.logToBoth("Finished processing readyEvent. Bot is 100% up now.\n\n");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Command Handler">
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
                    String result = main.cleverBot.think(msg.getContent());
                    chnl.setTypingStatus(false);
                    chnl.sendMessage(result);
                }
                return;
            }
            if (e.getMessage().getAuthor().getID().equals(IDReference.MYID))
            {
                return; //Do not process own messages. (I don't think this happens, but still.)
            }
            if (e.getMessage().getChannel().getID().equals("247417978440777728"))
            {
                //Dev #bot-updates channel
                return;
            }

            if (chnl.equals(main.quizH.getQuizChannel()) && main.quizH.isQuizRunning())
            {
                main.quizH.handleAnswer(msg);
                return;
            }

            String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
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

            main.logToBoth("Recieved Command: \"" + command + "\" from user \"" + nameOfSender + "\" in channel \"" + chnl.getName() + "\"");
            
            TreeMap<String, KrystaraCommand> commands = main.getCommands();

            doCommand(commands, command, sdr, chnl, msg, arguments, argumentsFull);
        } catch (InvalidParameterException ipe)
        {
            try
            {
                chnl.sendMessage(ipe.getMessage());
            } catch (Exception doubleException)
            {
                main.logToBoth("Exception logging exception! Original exception: ");
                ipe.printStackTrace();
                main.logToBoth("Exception while logging: ");
                doubleException.printStackTrace();
            }
        } catch (RateLimitException rle)
        {
            try
            {
                main.logToBoth("Rate limited! Time until un-ratelimited: " + rle.getRetryDelay() + "ms");
                main.getClient(null).getGuildByID(IDReference.SERVERID).getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**[RATELIMIT]** - Bot needs to slow down! We're rate limited for another " + rle.getRetryDelay() + " milliseconds, please tell SamboyCoding or MrSnake that the following section is too fast: " + rle.getMethod());
            } catch (Exception e2)
            {
                main.logToBoth("Exception sending ratelimit warning!");
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
                main.logToBoth("Exception logging exception! Original exception: ");
                ex.printStackTrace();
                main.logToBoth("Exception while logging: ");
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
            if (chnl.getID().equals(IDReference.CHATFRENCH))
            {
                lang = Language.FRENCH;
            }
            else if (chnl.getID().equals(IDReference.CHATGERMAN))
            {
                lang = Language.GERMAN;
            }
            else if (chnl.getID().equals(IDReference.CHATITALIAN))
            {
                lang = Language.ITALIAN;
            }
            else if (chnl.getID().equals(IDReference.CHATSPANISH))
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
        }
        else
        {
            commands.get(command).handleCommand(sdr, chnl, msg, arguments, argumentsFull);
        }
    }

    @EventSubscriber
    public void onJoin(UserJoinEvent e)
    {
        try
        {
            String nameOfUser = e.getUser().getNicknameForGuild(e.getGuild()).isPresent() ? e.getUser().getNicknameForGuild(e.getGuild()).get() : e.getUser().getName();
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
        if (e.getMessage().getChannel().getID().equals(IDReference.BOTCOMMANDSCHANNEL))
        {
            //Message sent in bot-commands
            String message = e.getMessage().getContent().replace(main.getClient(null).getOurUser().mention() + " ", "");

            e.getMessage().getChannel().setTypingStatus(true);
            String result = main.cleverBot.think(message);
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
            String nameOfUser = e.getUser().getNicknameForGuild(e.getGuild()).isPresent() ? e.getUser().getNicknameForGuild(e.getGuild()).get() : e.getUser().getName();

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
