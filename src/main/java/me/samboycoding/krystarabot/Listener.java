package me.samboycoding.krystarabot;

import java.net.URL;
import me.samboycoding.krystarabot.utilities.Utilities;
import me.samboycoding.krystarabot.utilities.IDReference;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.samboycoding.krystarabot.utilities.Command;
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
            cl.changeStatus(Status.game("?help"));
            main.log("My ID: " + main.getClient(null).getApplicationClientID());
            IDReference.MYID = main.getClient(null).getApplicationClientID();
            main.log("Registering commands...");

            new Command("?ping", "Checks how much lag discord + the bot are getting.", false)._register();
            new Command("?clear [amount]", "Clears the specified amount of messages.", true)._register();
            new Command("?troop [name]", "Looks up information on the specified troop.", false)._register();
            new Command("?trait [name]", "Looks up information on the specified trait.", false)._register();
            new Command("?spell [name]", "Looks up information on the specified spell.", false)._register();
            new Command("?class [name]", "Looks up information on the specified hero class.", false)._register();
            new Command("?platform [\"pc/mobile\" / \"console\"]", "Assigns you to a platform. You can be on none, one, or both of the platforms at any time.", false)._register();
            new Command("?kick [@user]", "Kicks the specified user from the server.", true)._register();
            new Command("?ban [@user]", "Bans the specified user from the server.", true)._register();
            new Command("?userstats", "Shows information on you, the server, and the roles.", false)._register();
            new Command("?warn [@user] [message]", "Sends a PM warning to the specified user.", true)._register();
            new Command("?code [code]", "Post a code into the #codes channel.", false)._register();
            new Command("?dead [code]", "Note a code as dead in the #codes channel.", false)._register();

            main.log("Finished processing readyEvent. Bot is 100% up now.\n\n");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @EventSubscriber
    public void onCommand(MessageReceivedEvent e)
    {
        try
        {
            if (e.getMessage().getChannel().isPrivate())
            {
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
            main.log("Recieved Command: \"" + command + "\" from user \"" + nameOfSender + "\" in channel \"" + chnl.getName() + "\"");
            switch (command)
            {
                //<editor-fold defaultstate="collapsed" desc="Ping">
                //?ping
                case "ping":
                    String lagTime = ((Long) (System.currentTimeMillis() - msg.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())).toString();
                    chnl.sendMessage("Pong! `" + lagTime + "ms lag`.");
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Clear">
                //?clear [number]    
                case "clear":
                    try
                    {
                        MessageList msgs = chnl.getMessages();
                        if (arguments.size() < 1)
                        {
                            chnl.sendMessage(sdr.mention() + ", that command needs an argument (a number between 1 and 100)");
                            break;
                        }

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

                        if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
                        {
                            msgs.bulkDelete(toDelete);
                            Utilities.cleanupMessage(chnl.sendMessage(toDelete.size() + " messages deleted (out of " + amount + " requested)"), 3000);
                            chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** cleared " + toDelete.size() + " messages from channel **" + chnl.getName() + "**");
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
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Troop">
                //?troop [string]
                case "troop":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String troopName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    JSONObject troopInfo = main.data.getTroopInfo(troopName);
                    if (troopInfo == null)
                    {
                        chnl.sendMessage("No troop `" + troopName + "` found, " + sdr.mention());
                        break;
                    }
                    String desc = troopInfo.getString("Description").replace("\n", "");
                    troopName = troopInfo.getString("Name");
                    String kingdom = troopInfo.getString("Kingdom");
                    String rarity = troopInfo.getString("Rarity");
                    String type;
                    String type1 = troopInfo.getString("Type_1");
                    String type2 = troopInfo.getString("Type_2");
                    String spell = troopInfo.getString("Spell");
                    int summonCost = troopInfo.getInt("Cost");
                    String trait1 = troopInfo.getString("Trait_1");
                    String trait2 = troopInfo.getString("Trait_2");
                    String trait3 = troopInfo.getString("Trait_3");
                    int armor = troopInfo.getJSONArray("ArmorPerLevel").getInt(19);
                    int life = troopInfo.getJSONArray("HealthPerLevel").getInt(19);
                    int attack = troopInfo.getJSONArray("AttackPerLevel").getInt(19);
                    int magic = troopInfo.getJSONArray("MagicPerLevel").getInt(19);
                    String troopId = troopInfo.getString("FileBase");
                    URL URL = new URL("http://ashtender.com/gems/assets/cards/" + troopId + ".jpg");

                    //Emojis
                    String emojiArmor = chnl.getGuild().getEmojiByName("gow_armor").toString();
                    String emojiLife = chnl.getGuild().getEmojiByName("gow_life").toString();
                    String emojiAttack = chnl.getGuild().getEmojiByName("gow_attack").toString();
                    String emojiMagic = chnl.getGuild().getEmojiByName("gow_magic").toString();

                    ArrayList<String> manaTypes = new ArrayList<>();
                  
                    if (troopInfo.getJSONObject("ManaColors").getBoolean("ColorBlue"))
                    {
                        manaTypes.add(chnl.getGuild().getEmojiByName("mana_blue").toString());
                    }
                    if (troopInfo.getJSONObject("ManaColors").getBoolean("ColorRed"))
                    {
                        manaTypes.add(chnl.getGuild().getEmojiByName("mana_red").toString());
                    }
                    if (troopInfo.getJSONObject("ManaColors").getBoolean("ColorBrown"))
                    {
                        manaTypes.add(chnl.getGuild().getEmojiByName("mana_brown").toString());
                    }
                    if (troopInfo.getJSONObject("ManaColors").getBoolean("ColorPurple"))
                    {
                        manaTypes.add(chnl.getGuild().getEmojiByName("mana_purple").toString());
                    }
                    if (troopInfo.getJSONObject("ManaColors").getBoolean("ColorYellow"))
                    {
                        manaTypes.add(chnl.getGuild().getEmojiByName("mana_yellow").toString());
                    }
                    if (troopInfo.getJSONObject("ManaColors").getBoolean("ColorGreen"))
                    {
                        manaTypes.add(chnl.getGuild().getEmojiByName("mana_green").toString());
                    }

                    if(type2.equals("None")) {
                        type = type1;
                    } else {
                        type = type1 + "/" + type2;
                    }
                    
                    String info = "**" + troopName + "**\n(" + rarity + " from " + kingdom + ", Type: " + type + ")\nDescription: " + desc + "\nMana: ";
                    info += manaTypes.toString().replace("[", "").replace("]", "").replace(", ", "");
                    info += "\nSpell: " + spell + "     Cost:" + summonCost + "\nTraits: " + trait1 + ", " + trait2 + ", " + trait3 + "\nLevel 20: " + emojiArmor + " " + armor + "    " + emojiLife + " " + life + "    " + emojiAttack + " " + attack + "    " + emojiMagic + " " + magic;

                    chnl.sendMessage(info);
                    chnl.sendFile(URL, troopId + ".jpg");
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Trait">
                //?trait [string]    
                case "trait":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String traitName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    JSONObject traitInfo = main.data.getTraitInfo(traitName);
                    if (traitInfo == null)
                    {
                        chnl.sendMessage("No trait `" + traitName + "` found, " + sdr.mention());
                        break;
                    }
                    traitName = traitInfo.getString("Name");
                    String traitDesc = traitInfo.getString("Description");

                    chnl.sendMessage("**" + traitName + "**: " + traitDesc);
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Spell">
                //?spell [string]
                case "spell":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String spellName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    JSONObject spellInfo = main.data.getSpellInfo(spellName);
                    if (spellInfo == null)
                    {
                        chnl.sendMessage("No spell `" + spellName + "` found, " + sdr.mention());
                        break;
                    }

                    spellName = spellInfo.getString("Name");
                    String spellDesc = spellInfo.getString("Description");
                    int spellCost = spellInfo.getInt("Cost");

                    chnl.sendMessage("**" + spellName + " (" + spellCost + "):** " + spellDesc);
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Class">
                //?class [string]
                case "class":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String className = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    JSONObject classInfo = main.data.getClassInfo(className);

                    if (classInfo == null)
                    {
                        chnl.sendMessage("No hero class `" + className + "` found, " + sdr.mention());
                        break;
                    }

                    className = classInfo.getString("Name");
                    String classKingdom = classInfo.getString("Kingdom");
                    String classTrait1 = classInfo.getString("Trait_1");
                    String classTrait2 = classInfo.getString("Trait_2");
                    String classTrait3 = classInfo.getString("Trait_3");
                    String classAugment1 = classInfo.getString("Augment_1");
                    String classAugment2 = classInfo.getString("Augment_2");
                    String classAugment3 = classInfo.getString("Augment_3");

                    chnl.sendMessage("**" + className + "** (" + classKingdom + ")\nTraits: " + classTrait1 + ", " + classTrait2 + ", " + classTrait3 + "\nAugments: " + classAugment1 + ", " + classAugment2 + ", " + classAugment3);
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Kingdom">
                //?kindom [string]
                case "kingdom":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a name to search for!");
                        break;
                    }
                    String kingdomName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
                    JSONObject kingdomInfo = main.data.getKingdomInfo(kingdomName);
                    if (kingdomInfo == null)
                    {
                        chnl.sendMessage("No kingdom `" + kingdomName + "` found, " + sdr.mention());
                        break;
                    }

                    kingdomName = kingdomInfo.getString("Name");
                    int numTroops = kingdomInfo.getJSONArray("Troops").length();
                    String bannerName = kingdomInfo.getString("BannerName");
                    String bannerDesc = kingdomInfo.getString("BannerDescription");
                    String bonus2 = kingdomInfo.getString("Bonus_2");
                    String bonus3 = kingdomInfo.getString("Bonus_3");
                    String bonus4 = kingdomInfo.getString("Bonus_4");
                    String bonus2Desc = kingdomInfo.getString("Bonus_2_Description");
                    String bonus3Desc = kingdomInfo.getString("Bonus_3_Description");
                    String bonus4Desc = kingdomInfo.getString("Bonus_4_Description");
                    String kingdomId = kingdomInfo.getString("FileBase");
                    if (bannerName.equals("Unnamed Banner"))
                    {
                        chnl.sendMessage("**" + kingdomName + "**\nTroops: " + numTroops + "\nBanner: None" + "\nBonus x2: " + bonus2 + " (" + bonus2Desc + ")\nBonus x3: " + bonus3 + " (" + bonus3Desc + ")\nBonus x4: " + bonus4 + " (" + bonus4Desc + ")");
                    } else
                    {
                        URL bannerURL = new URL("http://ashtender.com/gems/assets/banners/" + kingdomId + ".png");

                        chnl.sendMessage("**" + kingdomName + "**\nTroops: " + numTroops + "\nBanner: " + bannerName + " (" + bannerDesc + ")\nBonus x2: " + bonus2 + " (" + bonus2Desc + ")\nBonus x3: " + bonus3 + " (" + bonus3Desc + ")\nBonus x4: " + bonus4 + " (" + bonus4Desc + ")");
                        chnl.sendFile(bannerURL, kingdomId + ".png");
                    }
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Search">
                //?search [kingdoms|troops|traits|spells] [string]
                case "search":
                    if (arguments.size() < 2)
                    {
                        chnl.sendMessage("You need to specify a type, and a search term! Do ?help for a list of types.");
                        break;
                    }
                    String type = arguments.get(0).trim().toLowerCase();
                    @SuppressWarnings("unchecked") ArrayList<String> searchTermArray = (ArrayList<String>) arguments.clone();

                    String searchTerm = searchTermArray.toString().replace("[", "").replace("]", "").replace(",", "");
                    ArrayList<String> results = null;
                    main.log(type);
                    switch (type)
                    {
                        case "troops":
                            main.log("troops");
                            results = main.data.searchForTroop(searchTerm);
                            break;
                        case "traits":
                            main.log("traits");
                            results = main.data.searchForTrait(searchTerm);
                            break;
                        case "spells":
                            main.log("spells");
                            results = main.data.searchForSpell(searchTerm);
                            break;
                        case "kingdoms":
                            main.log("kingdoms");
                            results = main.data.searchForKingdom(searchTerm);
                            break;
                        default:
                            main.log("default");
                            chnl.sendMessage("`" + type + "` is not a valid search type, " + sdr.mention() + ". Do `?help` for a list of search terms.");
                            break;
                    }
                    if (results == null)
                    {
                        break;
                    }

                    chnl.sendMessage("Results: " + results.toString().replace("[", "").replace("]", ""));
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Platform">
                //?platform [string]
                case "platform":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("Please specify a platform.");
                        break;
                    }
                    String role = arguments.get(0).toLowerCase();
                    if (role.equals("pc/mobile"))
                    {
                        sdr.addRole(chnl.getGuild().getRoleByID(IDReference.RoleID.PCMOBILE.toString()));
                        chnl.sendMessage(sdr.mention() + ", you joined **PC/Mobile**");
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** assigned themselves to **PC/Mobile**");
                        break;
                    } else if (role.equals("console"))
                    {
                        sdr.addRole(chnl.getGuild().getRoleByID(IDReference.RoleID.CONSOLE.toString()));
                        chnl.sendMessage(sdr.mention() + ", you joined **Console**");
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** assigned themselves to **Console**");
                        break;
                    } else
                    {
                        chnl.sendMessage("Please enter a valid platform. Valid platforms are: \"Pc/Mobile\" or \"Console\".");
                        break;
                    }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Kick">
                //?kick [@user]    
                case "kick":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You need an @mention of a user to kick!");
                        break;
                    }
                    if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
                    {
                        String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
                        IUser usr = chnl.getGuild().getUserByID(id);
                        if (usr == null)
                        {
                            chnl.sendMessage("Invaild @mention!");
                            break;
                        }
                        chnl.getGuild().kickUser(usr);
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** kicked user **" + usr.getName() + "**");
                        chnl.sendMessage("User kicked.");
                    } else
                    {
                        chnl.sendMessage("You cannot do that!");
                    }
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Ban">
                //?ban [@user]    
                case "ban":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You need an @mention of a user to ban!");
                        break;
                    }
                    if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
                    {
                        String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
                        IUser usr = chnl.getGuild().getUserByID(id);
                        if (usr == null)
                        {
                            chnl.sendMessage("Invaild @mention!");
                            break;
                        }
                        chnl.getGuild().banUser(usr);
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** banned user **" + usr.getName() + "**");
                        chnl.sendMessage("User banned.");
                    } else
                    {
                        chnl.sendMessage("You cannot do that!");
                    }
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Userstats">
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
                    for (IRole r : sdrRoles)
                    {
                        if (r.isEveryoneRole())
                        {
                            continue;
                        }
                        if (r.getID().equals(IDReference.RoleID.MUTED.toString()))
                        {
                            continue;
                        }
                        if (r.getName().equals("KrystaraBot"))
                        {
                            continue;
                        }
                        sdrRolesNice.add(r.getName());
                    }

                    String toSend = "```\n--User Info---------";
                    toSend += "\nName: " + name;
                    toSend += "\nHas Nickname: " + hasNick;
                    if (hasNick)
                    {
                        toSend += "\nNickname: " + nickname;
                    }
                    toSend += "\nIs playing: " + state.getType().equals(StatusType.GAME);
                    toSend += "\nIs streaming: " + state.getType().equals(StatusType.STREAM);
                    toSend += "\nStream URL: " + (state.getUrl().isPresent() ? state.getUrl().get() : "None");
                    toSend += "\nGame: \"" + (state.getStatusMessage() == null ? "nothing" : state.getStatusMessage()) + "\"";
                    toSend += "\nNumber of roles: " + numRolesSdr;
                    toSend += "\nList of Roles: " + sdrRolesNice.toString();
                    toSend += "\n--Server Info---------";
                    toSend += "\nRoles: " + numRolesGuild;
                    toSend += "\nChannels: " + chnl.getGuild().getChannels().size();
                    toSend += "\nMembers: " + chnl.getGuild().getUsers().size();
                    toSend += "\n--Roles Info---------";
                    for (IRole r2 : guildRoles)
                    {
                        if (r2.isEveryoneRole())
                        {
                            continue;
                        }
                        if (r2.getID().equals(IDReference.RoleID.MUTED.toString()))
                        {
                            continue;
                        }
                        if (r2.getName().equals("KrystaraBot"))
                        {
                            continue;
                        }
                        toSend += "\n" + chnl.getGuild().getUsersByRole(r2).size() + "x " + r2.getName();
                    }

                    toSend += "\n```";

                    chnl.sendMessage(toSend);
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Warn">
                //?warn [@user] [message]
                case "warn":
                    if (arguments.size() < 2)
                    {
                        chnl.sendMessage("You need a minimum of an @mention and a message to send. (That's a minimum of two arguments)");
                    }
                    if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
                    {
                        String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
                        IUser usr = chnl.getGuild().getUserByID(id);
                        if (usr == null)
                        {
                            chnl.sendMessage("Invaild @mention!");
                            break;
                        }
                        @SuppressWarnings("unchecked")
                        ArrayList<String> messageArray = (ArrayList<String>) arguments.clone();

                        messageArray.remove(0); //Remove the @mention
                        String message = messageArray.toString().replace("[", "").replace("]", "").replace(",", "");

                        usr.getOrCreatePMChannel().sendMessage("Warning from user **" + nameOfSender + "** in channel **" + chnl.getName() + "**. Text:```\n" + message + "```");
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + (usr.getNicknameForGuild(chnl.getGuild()).isPresent() ? usr.getNicknameForGuild(chnl.getGuild()).get() : usr.getName()) + "** was warned by **" + nameOfSender + "**. Message: ```\n" + message + "```");
                    } else
                    {
                        chnl.sendMessage("You cannot do that!");
                    }
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Code">
                //?code [string] 
                case "code":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You have to enter a code first!");
                        break;
                    }
                    if (arguments.get(0).length() == 10)
                    {
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.CODES.toString()).sendMessage("new code: " + arguments.get(0).toUpperCase());
                        break;
                    } else
                    {
                        chnl.sendMessage("Please check your code - it has to be 10 characters!");
                        break;
                    }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Dead">
                //?dead [string]    
                case "dead":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You have to enter a code first!");
                        break;
                    }
                    if (arguments.get(0).length() == 10)
                    {
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.CODES.toString()).sendMessage(arguments.get(0).toUpperCase() + " is dead!");
                        break;
                    } else
                    {
                        chnl.sendMessage("Please check your code - it has to be 10 characters!");
                        break;
                    }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Help">
                //?help
                case "help":
                    toSend = "I recognize the following commands: \n";
                    int hidden = 0;
                    for (Command c : main.getRegisteredCommands())
                    {
                        if (c.requiresAdmin() && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
                        {
                            hidden++;
                            continue; //Don't show commands the user cannot do.
                        }
                        toSend += "```" + c.getName() + ": " + c.getDescription();

                        toSend += "```";
                    }
                    if (hidden != 0)
                    {
                        toSend += "\n\n(" + hidden + " commands not shown because you do not have a high-enough rank on the specified server)";
                    }
                    sdr.getOrCreatePMChannel().sendMessage(toSend);
                    chnl.sendMessage(sdr.mention() + ", I've sent you a list of commands over PM.");
                    break;
                //</editor-fold>
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
