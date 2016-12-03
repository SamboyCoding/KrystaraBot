package me.samboycoding.krystarabot;

import me.samboycoding.krystarabot.utilities.Utilities;
import me.samboycoding.krystarabot.utilities.IDReference;
import java.util.ArrayList;
import java.util.Arrays;
import me.samboycoding.krystarabot.command.BanCommand;
import me.samboycoding.krystarabot.command.BuildcacheCommand;
import me.samboycoding.krystarabot.command.ClassCommand;
import me.samboycoding.krystarabot.command.ClearCommand;
import me.samboycoding.krystarabot.command.ClearcacheCommand;
import me.samboycoding.krystarabot.command.CodesCommand;
import me.samboycoding.krystarabot.command.DeadCommand;
import me.samboycoding.krystarabot.command.HelpCommand;
import me.samboycoding.krystarabot.command.KickCommand;
import me.samboycoding.krystarabot.command.KingdomCommand;
import me.samboycoding.krystarabot.command.KrystaraCommand;
import me.samboycoding.krystarabot.command.NewcodeCommand;
import me.samboycoding.krystarabot.command.PingCommand;
import me.samboycoding.krystarabot.command.PlatformCommand;
import me.samboycoding.krystarabot.command.ReloadDataCommand;
import me.samboycoding.krystarabot.command.SearchCommand;
import me.samboycoding.krystarabot.command.ServerstatsCommand;
import me.samboycoding.krystarabot.command.SpellCommand;
import me.samboycoding.krystarabot.command.TeamCommand;
import me.samboycoding.krystarabot.command.Top10Command;
import me.samboycoding.krystarabot.command.TraitCommand;
import me.samboycoding.krystarabot.command.TroopCommand;
import me.samboycoding.krystarabot.command.UserstatsCommand;
import me.samboycoding.krystarabot.command.WarnCommand;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.NickNameChangeEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
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

    public static MessageCounterHandler messageCounter = main.messageCounter;

    @EventSubscriber
    public void onReady(ReadyEvent e) throws DiscordException, RateLimitException, MissingPermissionsException
    {
        IDiscordClient cl = main.getClient(null);
        main.logToBoth("Beginning ReadyEvent Init...");
        try
        {
            if(IDReference.LIVE)
            {
                main.logToBoth("Logging in to LIVE server.");
                cl.changeUsername("Krystara");
            } else
            {
                main.logToBoth("Logging in to TESTING server.");
                cl.changeUsername("Krystara *Testing*");
            }
            cl.changeAvatar(Image.forUrl("png", "http://repo.samboycoding.me/static/krystarabot_icon.png"));
        } catch (DiscordException ex)
        {
            main.logToBoth("Failed to change username. Rate limited most likely. Message: " + ex.getMessage());
        }
        try
        {
            cl.changeStatus(Status.game("?help"));
            main.logToBoth("My ID: " + main.getClient(null).getApplicationClientID());
            IDReference.MYID = main.getClient(null).getApplicationClientID();
            main.logToBoth("Registering commands...");

            main.registerCommand(new BanCommand());
            main.registerCommand(new BuildcacheCommand());
            main.registerCommand(new ClassCommand());
            main.registerCommand(new ClearCommand());
            main.registerCommand(new ClearcacheCommand());
            main.registerCommand(new CodesCommand());
            main.registerCommand(new DeadCommand());
            main.registerCommand(new HelpCommand());
            main.registerCommand(new KickCommand());
            main.registerCommand(new KingdomCommand());
            main.registerCommand(new NewcodeCommand());
            main.registerCommand(new PingCommand());
            main.registerCommand(new PlatformCommand());
            main.registerCommand(new ReloadDataCommand());
            main.registerCommand(new SearchCommand());
            main.registerCommand(new ServerstatsCommand());
            main.registerCommand(new SpellCommand());
            main.registerCommand(new TeamCommand());
            main.registerCommand(new Top10Command());
            main.registerCommand(new TraitCommand());
            main.registerCommand(new TroopCommand());
            main.registerCommand(new UserstatsCommand());
            main.registerCommand(new WarnCommand());
            
            main.logToBoth("Finished processing readyEvent. Bot is 100% up now.\n\n");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @EventSubscriber
    public void onCommand(MessageReceivedEvent e)
    {
        IMessage msg = e.getMessage();
        IUser sdr = msg.getAuthor();
        IChannel chnl = msg.getChannel();
        try
        {
            if (e.getMessage().getChannel().isPrivate())
            {
                e.getMessage().getChannel().sendMessage("Sorry, the bot doesn't support PM commands. Please re-enter the command in a server.");
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
            String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
            String content = msg.getContent();

            //Message Counter
            messageCounter.countMessage(sdr, chnl.getGuild());

            if (!content.startsWith("?"))
            {
                //Not a command.
                return;
            }

            messageCounter.countCommand(sdr, chnl.getGuild());

            if (!chnl.getID().equals(IDReference.BOTCOMMANDSCHANNEL) && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
            {
                //Not admin, and not in #bot-commands
                sdr.getOrCreatePMChannel().sendMessage("Please only use commands in #bot-commands. Thank you.");
                msg.delete();
                return;
            }

            String command;
            ArrayList<String> arguments = new ArrayList<>();
            String argumentsFull = "";
            if (content.contains(" "))
            {
                command = content.substring(1, content.indexOf(" ")).toLowerCase(); //From the character after the '?' to the character before the first space.
                arguments.addAll(Arrays.asList(content.trim().substring(content.indexOf(" ") + 1, content.length()).split(" "))); //From the character after the first space, to the end.
                argumentsFull = content.trim().substring(content.indexOf(" ") + 1, content.length());
            } else
            {
                command = content.substring(1, content.length()).toLowerCase();
            }

            main.logToBoth("Recieved Command: \"" + command + "\" from user \"" + nameOfSender + "\" in channel \"" + chnl.getName() + "\"");

            ArrayList<KrystaraCommand> commands = main.getCommands();

            boolean validCommand = false;
            for (KrystaraCommand c : commands)
            {
                if (c.getCommand().equals(command))
                {
                    validCommand = true;
                    c.handleCommand(sdr, chnl, msg, arguments, argumentsFull);
                }
            }

            if (!validCommand)
            {
                chnl.sendMessage("Invalid command \"" + command + "\"");
            }

        } catch (RateLimitException rle)
        {
            try
            {
                main.logToBoth("Rate limited! Time until un-ratelimited: " + rle.getRetryDelay());
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
                main.logToBoth("Exception logging: ");
                doubleException.printStackTrace();
            }
        }
    }

    @EventSubscriber
    public void onJoin(UserJoinEvent e)
    {
        try
        {
            String nameOfUser = e.getUser().getNicknameForGuild(e.getGuild()).isPresent() ? e.getUser().getNicknameForGuild(e.getGuild()).get() : e.getUser().getName();
            e.getGuild().getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**--->>>** User **" + nameOfUser + "** joined the server!");
            //log message every 100 member joins
            if (e.getGuild().getUsers().size() % 100 == 0)
            {
                e.getGuild().getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**[MILESTONE]** The server has now " + e.getGuild().getUsers().size() + " users!");
            }
        } catch (Exception ignored)
        {
            //Ignore.
        }
    }

    @EventSubscriber
    public void onLeave(UserLeaveEvent e)
    {
        try
        {
            String nameOfUser = e.getUser().getNicknameForGuild(e.getGuild()).isPresent() ? e.getUser().getNicknameForGuild(e.getGuild()).get() : e.getUser().getName();
            e.getGuild().getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**<<<---** User **" + nameOfUser + "** left the server!");
        } catch (Exception ignored)
        {
            //Ignore.
        }
    }

    @EventSubscriber
    public void onChangeName(NickNameChangeEvent e)
    {
        try
        {
            String old = e.getOldNickname().isPresent() ? e.getOldNickname().get() : e.getUser().getName();
            String newName = e.getNewNickname().isPresent() ? e.getNewNickname().get() : e.getUser().getName();
            e.getGuild().getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**[RENAME]** User **" + old + "** changed their name to **" + newName + "**");
        } catch (Exception ignored)
        {
            //Ignore.
        }
    }
}
