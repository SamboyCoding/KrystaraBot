package me.samboycoding.krystarabot;

import me.samboycoding.krystarabot.utilities.Utilities;
import me.samboycoding.krystarabot.utilities.IDReference;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.samboycoding.krystarabot.utilities.Command;
import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONObject;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.handle.obj.Status.StatusType;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Main event listener
 *
 * @author Sam
 */
public class Listener {

    @EventSubscriber
    public void onReady(ReadyEvent e) throws DiscordException, RateLimitException, MissingPermissionsException {
        IDiscordClient cl = main.getClient(null);
        try {
            cl.changeUsername("Krystara");
        } catch (DiscordException ex) {
            main.log("Failed to change username. Rate limited most likely.");
        }
        try {
            cl.changeStatus(Status.game("Made by SamboyCoding and MrSnake"));
            main.log("My ID: " + main.getClient(null).getApplicationClientID());
            IDReference.MYID = main.getClient(null).getApplicationClientID();
            main.log("Registering commands...");

            new Command("ping", "Checks how much lag discord + the bot are getting.", false)._register();
            new Command("clear", "Clears the specified amount of messages. \nArguments (1): The amount of messages to delete, between 1 and 100.", true)._register();
            new Command("troop", "Looks up information on the specified troop.\nArguments: What to look up. Can include spaces.", false)._register();
            new Command("trait", "Looks up information on the specified trait.\nArguments: What to look up. Can include spaces.", false)._register();
            new Command("spell", "Looks up information on the specified spell.\nArguments: What to look up. Can include spaces.", false)._register();
            new Command("platform", "Assigns you to a platform. You can be on none, one, or both of the platforms at any time.\nArguments: The platform to join. Valid platforms are: 'pc/mobile' and 'console'.", false)._register();
            new Command("kick", "Kicks the specified user from the server.\nArguments: An @mention of who to kick.", true)._register();
            new Command("ban", "Bans the specified user from the server.\nArguments: An @mention of who to ban.", true)._register();
            new Command("userstats", "Shows information on you, the server, and the roles.", false)._register();
            new Command("warn", "Sends a PM warning to the specified user.\nArguments: an @mention of the user, and the text to warn (can include spaces).", true)._register();
            new Command("code", "Post a code into the #codes channel.", false)._register();
            new Command("dead", "Note a code as dead in the #codes channel.", false)._register();

            main.log("Finished processing readyEvent. Bot is 100% up now.\n\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventSubscriber
    public void onCommand(MessageReceivedEvent e) {
        try {
            if (e.getMessage().getChannel().isPrivate()) {
                e.getMessage().getChannel().sendMessage("Sorry, the bot doesn't support PM commands. Please re-enter the command in a server.");
                return;
            }
            IMessage msg = e.getMessage();
            IUser sdr = msg.getAuthor();
            String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
            IChannel chnl = msg.getChannel();
            String content = msg.getContent();
            IRole admin = msg.getGuild().getRoleByID(IDReference.RoleID.ADMIN.toString());
            IRole dev = msg.getGuild().getRoleByID(IDReference.RoleID.DEV.toString());
            IRole mod = msg.getGuild().getRoleByID(IDReference.RoleID.MODERATOR.toString());

            if (!content.startsWith("?")) {
                //Not a command.
                return;
            }
            String command;
            ArrayList<String> arguments = new ArrayList<>();
            if (content.contains(" ")) {
                command = content.substring(1, content.indexOf(" ")); //From the character after the '?' to the character before the first space.
                arguments.addAll(Arrays.asList(content.trim().substring(content.indexOf(" ") + 1, content.length()).split(" "))); //From the character after the first space, to the end.
            } else {
                command = content.substring(1, content.length()).toLowerCase();
                //Do not change arguments
            }
            main.log("Recieved Command: \"" + command + "\" from user \"" + nameOfSender + "\" in channel \"" + chnl.getName() + "\"");
            switch (command) {
                //?ping
                case "ping":
                    String lagTime = ((Long) (System.currentTimeMillis() - msg.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())).toString();
                    chnl.sendMessage("Pong! `" + lagTime + "ms lag`.");
                    break;
                //?clear [number]    
                case "clear":
                    try {
                        MessageList msgs = chnl.getMessages();
                        if (arguments.size() < 1) {
                            chnl.sendMessage(sdr.mention() + ", that command needs an argument (a number between 1 and 100)");
                            break;
                        }

                        int amount = Integer.parseInt(arguments.get(0));
                        if (amount < 1 || amount > 100) {
                            chnl.sendMessage("Amount must be between 1 and 100");
                            break;
                        }

                        if (amount == 1) {
                            //Cannot delete one with .bulkDelete()
                            msgs.get(1).delete(); //Again, ignore index 0, as it's the command
                            break;
                        }

                        ArrayList<IMessage> toDelete = new ArrayList<>();
                        try {
                            for (int i = 1; i < amount + 1; i++) //Start at 1 to ignore command - it's removed later.
                            {
                                toDelete.add(msgs.get(i));
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                            //Ignored
                        }

                        if (Utilities.canUseAdminCommand(sdr, chnl.getGuild())) {
                            msgs.bulkDelete(toDelete);
                            Utilities.cleanupMessage(chnl.sendMessage(toDelete.size() + " messages deleted (out of " + amount + " requested)"), 3000);
                            chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** cleared " + toDelete.size() + " messages from channel **" + chnl.getName() + "**");
                        } else {
                            chnl.sendMessage("You cannot do that!");
                        }
                    } catch (NumberFormatException ex) {
                        chnl.sendMessage("Invalid number of messages to delete specified. Must be a whole number between 1 and 100");
                    } catch (ArrayIndexOutOfBoundsException ex2) {
                        Utilities.cleanupMessage(chnl.sendMessage("No messages found!"), 3000);
                    } catch (Exception ex3) {
                        chnl.sendMessage("Unknown error!");
                        ex3.printStackTrace();
                    }
                    break;
                //?troop [string]
                case "troop":
                    if (arguments.size() < 1) {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String troopName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    StopWatch troopTimer = new StopWatch();
                    troopTimer.start();
                    JSONObject troopInfo = main.data.getTroopInfo(troopName);
                    troopTimer.stop();
                    if (troopInfo == null) {
                        chnl.sendMessage("No troop info found.");
                        break;
                    }
                    chnl.sendMessage("Found data in `" + troopTimer.getTime() + "ms`. Data: ```JSON\n" + troopInfo.toString(4) + "```");
                    break;
                //?trait [string]    
                case "trait":
                    if (arguments.size() < 1) {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String traitName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    StopWatch traitTimer = new StopWatch();
                    traitTimer.start();
                    JSONObject traitInfo = main.data.getTraitInfo(traitName);
                    traitTimer.stop();
                    if (traitInfo == null) {
                        chnl.sendMessage("No trait info found.");
                        break;
                    }
                    chnl.sendMessage("Found data in `" + traitTimer.getTime() + "ms`. Data: ```JSON\n" + traitInfo.toString(4) + "```");
                    break;
                //?spell [string]
                case "spell":
                    if (arguments.size() < 1) {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String spellName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    StopWatch spellTimer = new StopWatch();
                    spellTimer.start();
                    JSONObject spellInfo = main.data.getSpellInfo(spellName);
                    spellTimer.stop();
                    if (spellInfo == null) {
                        chnl.sendMessage("No spell info found.");
                        break;
                    }
                    chnl.sendMessage("Found data in `" + spellTimer.getTime() + "ms`. Data: ```JSON\n" + spellInfo.toString(4) + "```");
                    break;
                //?platform [string]
                case "platform":
                    if (arguments.size() < 1) {
                        chnl.sendMessage("Please specify a platform.");
                        break;
                    }
                    String role = arguments.get(0).toLowerCase();
                    if (role.equals("pc/mobile")) {
                        sdr.addRole(chnl.getGuild().getRoleByID(IDReference.RoleID.PCMOBILE.toString()));
                        chnl.sendMessage(sdr.mention() + ", you joined **PC/Mobile**");
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** assigned themselves to **PC/Mobile**");
                        break;
                    } else if (role.equals("console")) {
                        sdr.addRole(chnl.getGuild().getRoleByID(IDReference.RoleID.CONSOLE.toString()));
                        chnl.sendMessage(sdr.mention() + ", you joined **Console**");
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** assigned themselves to **Console**");
                        break;
                    } else {
                        chnl.sendMessage("Please enter a valid platform. Valid platforms are: \"Pc/Mobile\" or \"Console\".");
                        break;
                    }
                //?kick [@user]    
                case "kick":
                    if (arguments.size() < 1) {
                        chnl.sendMessage("You need an @mention of a user to kick!");
                        break;
                    }
                    if (Utilities.canUseAdminCommand(sdr, chnl.getGuild())) {
                        String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
                        IUser usr = chnl.getGuild().getUserByID(id);
                        if (usr == null) {
                            chnl.sendMessage("Invaild @mention!");
                            break;
                        }
                        chnl.getGuild().kickUser(usr);
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** kicked user **" + usr.getName() + "**");
                        chnl.sendMessage("User kicked.");
                    } else {
                        chnl.sendMessage("You cannot do that!");
                    }
                    break;
                //?ban [@user]    
                case "ban":
                    if (arguments.size() < 1) {
                        chnl.sendMessage("You need an @mention of a user to ban!");
                        break;
                    }
                    if (Utilities.canUseAdminCommand(sdr, chnl.getGuild())) {
                        String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
                        IUser usr = chnl.getGuild().getUserByID(id);
                        if (usr == null) {
                            chnl.sendMessage("Invaild @mention!");
                            break;
                        }
                        chnl.getGuild().banUser(usr);
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** banned user **" + usr.getName() + "**");
                        chnl.sendMessage("User banned.");
                    } else {
                        chnl.sendMessage("You cannot do that!");
                    }
                    break;
                //?userstats    
                case "userstats":
                    String name = sdr.getName();
                    String nickname = nameOfSender;
                    Boolean hasNick = !name.equals(nickname);
                    Status state = sdr.getStatus();
                    List<IRole> sdrRoles = sdr.getRolesForGuild(chnl.getGuild());
                    int numRolesSdr = sdrRoles.size() - 1; //-1 to remove @everyone
                    List<IRole> guildRoles = chnl.getGuild().getRoles();
                    int numRolesGuild = guildRoles.size() - 1; //Again, -1 to remove @everyone

                    List<String> sdrRolesNice = new ArrayList<>();
                    for (IRole r : sdrRoles) {
                        if (r.isEveryoneRole()) {
                            continue;
                        }
                        if (r.getID().equals(IDReference.RoleID.MUTED.toString())) {
                            continue;
                        }
                        if (r.getName().equals("KrystaraBot")) {
                            continue;
                        }
                        sdrRolesNice.add(r.getName());
                    }

                    String toSend = "```\n---------User Info---------";
                    toSend += "\nName: " + name;
                    toSend += "\nHas Nickname: " + hasNick;
                    if (hasNick) {
                        toSend += "\nNickname: " + nickname;
                    }
                    toSend += "\nIs playing: " + state.getType().equals(StatusType.GAME);
                    toSend += "\nIs streaming: " + state.getType().equals(StatusType.STREAM);
                    toSend += "\nStream URL: " + (state.getUrl().isPresent() ? state.getUrl().get() : "None");
                    toSend += "\nGame: \"" + (state.getStatusMessage() == null ? "nothing" : state.getStatusMessage()) + "\"";
                    toSend += "\nNumber of roles: " + numRolesSdr;
                    toSend += "\nList of Roles: " + sdrRolesNice.toString();
                    toSend += "\n---------Server Info---------";
                    toSend += "\nNumber of roles: " + numRolesGuild;
                    toSend += "\nNumber of members: " + chnl.getGuild().getUsers().size();
                    toSend += "\n---------Roles Info---------";
                    for (IRole r2 : guildRoles) {
                        if (r2.isEveryoneRole()) {
                            continue;
                        }
                        if (r2.getID().equals(IDReference.RoleID.MUTED.toString())) {
                            continue;
                        }
                        if (r2.getName().equals("KrystaraBot")) {
                            continue;
                        }
                        toSend += "\nNumber of users in role " + r2.getName() + ": " + chnl.getGuild().getUsersByRole(r2).size();
                    }

                    toSend += "\n```";

                    chnl.sendMessage(toSend);
                    break;
                //?warn [@user] [message]
                case "warn":
                    if (arguments.size() < 2) {
                        chnl.sendMessage("You need a minimum of an @mention and a message to send. (That's a minimum of two arguments)");
                    }
                    if (Utilities.canUseAdminCommand(sdr, chnl.getGuild())) {
                        String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
                        IUser usr = chnl.getGuild().getUserByID(id);
                        if (usr == null) {
                            chnl.sendMessage("Invaild @mention!");
                            break;
                        }
                        @SuppressWarnings("unchecked")
                        ArrayList<String> messageArray = (ArrayList<String>) arguments.clone();

                        messageArray.remove(0); //Remove the @mention
                        String message = messageArray.toString().replace("[", "").replace("]", "").replace(",", "");

                        usr.getOrCreatePMChannel().sendMessage("Warning from user **" + nameOfSender + "** in channel **" + chnl.getName() + "**. Text:```\n" + message + "```");
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + (usr.getNicknameForGuild(chnl.getGuild()).isPresent() ? usr.getNicknameForGuild(chnl.getGuild()).get() : usr.getName()) + "** was warned by **" + nameOfSender + "**. Message: ```\n" + message + "```");
                    } else {
                        chnl.sendMessage("You cannot do that!");
                    }
                    break;
                //?code [string] 
                case "code":
                    if (arguments.size() < 1) {
                        chnl.sendMessage("You have to enter a code first!");
                        break;
                    }
                    if (arguments.get(0).length() == 10) {
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.CODES.toString()).sendMessage("new code: " + arguments.get(0).toUpperCase());
                        break;
                    } else {
                        chnl.sendMessage("Please check your code - it has to be 10 characters!");
                        break;
                    }
                //?dead [string]    
                case "dead":
                    if (arguments.size() < 1) {
                        chnl.sendMessage("You have to enter a code first!");
                        break;
                    }
                    if (arguments.get(0).length() == 10) {
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.CODES.toString()).sendMessage(arguments.get(0).toUpperCase() + " is dead!");
                        break;
                    } else {
                        chnl.sendMessage("Please check your code - it has to be 10 characters!");
                        break;
                    }
                case "help":
                    toSend = "I recognize the following commands: \n";
                    int hidden = 0;
                    for (Command c : main.getRegisteredCommands()) {
                        if (c.requiresAdmin() && !Utilities.canUseAdminCommand(sdr, chnl.getGuild())) {
                            hidden++;
                            continue; //Don't show commands the user cannot do.
                        }
                        toSend += "```" + c.getName() + ": " + c.getDescription();

                        if (c.getAliases().length > 0) {
                            toSend += "\nAliases: ";
                            for (String alias : c.getAliases()) {
                                toSend += alias + ",";
                            }
                            toSend = toSend.substring(0, toSend.length() - 1);
                        }
                        toSend += "```";
                    }
                    if(hidden != 0) toSend += "\n\n(" + hidden + " commands not shown because you do not have a high-enough rank on the specified server)";
                    sdr.getOrCreatePMChannel().sendMessage(toSend);
                    chnl.sendMessage(sdr.mention() + ", I've sent you a list of commands over PM.");
                    break;
                default:
                    chnl.sendMessage("Invalid command \"" + command + "\"");
                    break;
            }
            msg.delete(); //Cleanup command
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
