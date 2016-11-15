package me.samboycoding.krystarabot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONObject;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Main event listener
 *
 * @author Sam
 */
public class Listener
{

    @EventSubscriber
    public void onReady(ReadyEvent e) throws DiscordException, RateLimitException, MissingPermissionsException
    {
        IDiscordClient cl = main.getClient(null);
        try
        {
            cl.changeUsername("Krystara");
        } catch (DiscordException ex)
        {
            main.log("Failed to change username. Rate limited most likely.");
        }
        try
        {
            cl.changeStatus(Status.game("Made by SamboyCoding and MrSnake"));
            main.log("My ID: " + main.getClient(null).getApplicationClientID());
            IDReference.MYID = main.getClient(null).getApplicationClientID();
            //logToLogChannel("Bot started", cl.getGuildByID(IDReference.SERVERID));
            main.log("Finished processing readyEvent. Bot is 100% up now.\n\n");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void logToLogChannel(String msg, IGuild srv) throws MissingPermissionsException, RateLimitException, DiscordException
    {
        try
        {
            DateFormat df = new SimpleDateFormat("[dd/MM/yy | HH:mm:ss] ");
            Date dateobj = new Date();
            String timestamp = df.format(dateobj);
            if (msg.length() > 1999)
            {
                msg = msg.substring(0, 1900);
                msg += "\n-----SNIPPED TO FIT 2000 CHAR LIMIT-----";
            }
            srv.getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage(timestamp + msg);
        } catch (Exception e)
        {
            main.log("Something went wrong, while logging something. PERMISSIONS?!");
            main.log("**********BEGIN ERROR REPORT**********");
            e.printStackTrace();
            main.log("**********END ERROR REPORT**********");
        }
    }

    @EventSubscriber
    public void onCommand(MessageReceivedEvent e)
    {
        try
        {

            IMessage msg = e.getMessage();
            IUser sdr = msg.getAuthor();
            String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
            IChannel chnl = msg.getChannel();
            String content = msg.getContent();
            if (!content.startsWith("?"))
            {
                //Not a command.
                return;
            }
            String command;
            ArrayList<String> arguments = new ArrayList<>();
            if (content.contains(" "))
            {
                command = content.substring(1, content.indexOf(" ")); //From the character after the '?' to the character before the first space.
                arguments.addAll(Arrays.asList(content.trim().substring(content.indexOf(" ") + 1, content.length()).split(" "))); //From the character after the first space, to the end.
            } else
            {
                command = content.substring(1, content.length()).toLowerCase();
                //Do not change arguments
            }
            main.log("Recieved Command: " + command + " from user \"" + nameOfSender + "\" in channel \"" + chnl.getName() + "\"");
            switch (command)
            {
                case "ping":
                    String lagTime = ((Long) (System.currentTimeMillis() - msg.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())).toString();
                    chnl.sendMessage("Pong! `" + lagTime + "ms lag`.");
                    break;
                case "clear":
                    try
                    {
                        MessageList msgs = chnl.getMessages();
                        if (arguments.size() < 1)
                        {
                            chnl.sendMessage(sdr.mention() + ", that command needs an argument (a number between 1 and 100)");
                            break;
                        }

                        IRole admin = msg.getGuild().getRoleByID(IDReference.RoleID.ADMIN.toString());
                        IRole dev = msg.getGuild().getRoleByID(IDReference.RoleID.DEV.toString());
                        IRole mod = msg.getGuild().getRoleByID(IDReference.RoleID.MODERATOR.toString());
                        int amount = Integer.parseInt(arguments.get(0));
                        if (amount < 1 || amount > 100)
                        {
                            chnl.sendMessage("Amount must be between 1 and 100");
                            break;
                        }

                        if (amount == 1)
                        {
                            //Cannot delete one with .bulkDelete()
                            msgs.get(1).delete(); //Again, ignore index 0, as it's the command
                            break;
                        }

                        ArrayList<IMessage> toDelete = new ArrayList<>();
                        try
                        {
                            for (int i = 1; i < amount + 1; i++) //Start at 1 to ignore command - it's removed later.
                            {
                                toDelete.add(msgs.get(i));
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored)
                        {
                            //Ignored
                        }

                        if (Utilities.userHasRole(msg.getGuild(), sdr, admin) || Utilities.userHasRole(msg.getGuild(), sdr, dev) || Utilities.userHasRole(msg.getGuild(), sdr, mod))
                        {
                            msgs.bulkDelete(toDelete);
                            Utilities.cleanupMessage(chnl.sendMessage(toDelete.size() + " messages deleted (out of " + amount + " requested)"), 3000);
                        } else
                        {
                            chnl.sendMessage("You cannot do that!");
                        }
                    } catch (NumberFormatException ex)
                    {
                        chnl.sendMessage("Invalid number of messages to delete specified. Must be a whole number between 1 and 100");
                    } catch (ArrayIndexOutOfBoundsException ex2)
                    {
                        Utilities.cleanupMessage(chnl.sendMessage("No messages found!"), 3000);
                    } catch (Exception ex3)
                    {
                        chnl.sendMessage("Unknown error!");
                        ex3.printStackTrace();
                    }
                    break;
                case "troop":
                    if(arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String troopName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    StopWatch troopTimer = new StopWatch();
                    troopTimer.start();
                    JSONObject troopInfo = main.data.getTroopInfo(troopName);
                    troopTimer.stop();
                    if(troopInfo == null)
                    {
                        chnl.sendMessage("No troop info found.");
                        break;
                    }
                    chnl.sendMessage("Found data in `" + troopTimer.getTime() + "ms`. Data: ```JSON\n" + troopInfo.toString(4) + "```");
                    break;
                case "trait":
                    if(arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String traitName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    StopWatch traitTimer = new StopWatch();
                    traitTimer.start();
                    JSONObject traitInfo = main.data.getTraitInfo(traitName);
                    traitTimer.stop();
                    if(traitInfo == null)
                    {
                        chnl.sendMessage("No trait info found.");
                        break;
                    }
                    chnl.sendMessage("Found data in `" + traitTimer.getTime() + "ms`. Data: ```JSON\n" + traitInfo.toString(4) + "```");
                    break;
                case "spell":
                    if(arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String spellName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    StopWatch spellTimer = new StopWatch();
                    spellTimer.start();
                    JSONObject spellInfo = main.data.getSpellInfo(spellName);
                    spellTimer.stop();
                    if(spellInfo == null)
                    {
                        chnl.sendMessage("No spell info found.");
                        break;
                    }
                    chnl.sendMessage("Found data in `" + spellTimer.getTime() + "ms`. Data: ```JSON\n" + spellInfo.toString(4) + "```");
                    break;
                default:
                    chnl.sendMessage("Invalid command \"" + command + "\"");
                    break;
            }
            msg.delete(); //Cleanup command
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
