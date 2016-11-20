package me.samboycoding.krystarabot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import me.samboycoding.krystarabot.utilities.Utilities;
import me.samboycoding.krystarabot.utilities.IDReference;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import me.samboycoding.krystarabot.utilities.AdminCommand;
import me.samboycoding.krystarabot.utilities.Command;
import me.samboycoding.krystarabot.utilities.ImageUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.handle.obj.Status.StatusType;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
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

    public static MessageCounterHandler messageCounter = main.messageCounter;

    @EventSubscriber
    public void onReady(ReadyEvent e) throws DiscordException, RateLimitException, MissingPermissionsException
    {
        IDiscordClient cl = main.getClient(null);
        try
        {
            cl.changeUsername("Krystara");
            cl.changeAvatar(Image.forUrl("png", "http://repo.samboycoding.me/static/krystarabot_icon.png"));
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

            new Command("?ping", "Check if the bot is able to respond to commands.", false)._register();
            new Command("?troop [name]", "Shows information for the specified troop.", false)._register();
            new Command("?trait [name]", "Shows information for the specified trait.", false)._register();
            new Command("?spell [name]", "Shows information for the specified spell.", false)._register();
            new Command("?class [name]", "Shows information for the specified hero class.", false)._register();
            new Command("?kingdom [name]", "Shows information for the specified kingdom.", false)._register();
            new Command("?search [text]", "Search for troops, traits, spells, hero classes or kingdoms containing the specified text.", false)._register();
            new Command("?platform [pc|mobile|console]", "Assigns you to a platform. You can be on none, one, or both of the platforms at any time.", false)._register();
            new Command("?userstats [optional @mention]", "Shows information on you, or the specified user", false)._register();
            new Command("?serverstats", "Shows information on the server.", false)._register();
            new Command("?newcode [code]", "Post a new code into the #codes channel.", false)._register();
            new Command("?codes", "Lists the currently \"Alive\" codes.", false)._register();
            new Command("?dead [code]", "Report a code as dead in the #codes channel.", false)._register();
            new Command("?top10", "Shows the 10 most talkative (i.e. those that sent the most messages) on the server.", false)._register();

            main.log("Registering Admin commands...");
            new AdminCommand("?kick [@user]", "Kicks the specified user from the server.", true)._register();
            new AdminCommand("?ban [@user]", "Bans the specified user from the server.", true)._register();
            new AdminCommand("?clear [amount (1-100)]", "Deletes the specified amount of messages.", true)._register();
            new AdminCommand("?warn [@user] [message]", "Sends a PM warning to the specified user.", true)._register();
            new AdminCommand("?clearcache", "Clears cached scaled/stitched images. NOT FOR USE BY NON-DEVS!", true)._register();
            new AdminCommand("?buildcache", "Builds a cache of scaled/stitched images. NOT FOR USE BY NON-DEVS!", true)._register();

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
            if (e.getMessage().getAuthor().getID().equals(IDReference.MYID))
            {
                return; //Do not process own messages. (I don't think this happens, but still.)
            }
            if (e.getMessage().getChannel().getID().equals("247417978440777728"))
            {
                //Dev #bot-updates channel
                return;
            }
            IMessage msg = e.getMessage();
            IUser sdr = msg.getAuthor();
            String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
            IChannel chnl = msg.getChannel();
            String content = msg.getContent();

            //Message Counter
            messageCounter.countMessage(sdr, chnl.getGuild());

            if (!content.startsWith("?"))
            {
                //Not a command.
                return;
            }

            messageCounter.countCommand(sdr, chnl.getGuild());

            if (!chnl.getID().equals(IDReference.ChannelID.BOTCOMMANDS.toString()) && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
            {
                //Not admin, and not in #bot-commands
                sdr.getOrCreatePMChannel().sendMessage("Please only use commands in #bot-commands. Thank you.");
                msg.delete();
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
                    long lagTime = ((Long) (System.currentTimeMillis() - msg.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                    if (lagTime < 0)
                    {
                        lagTime += lagTime + lagTime; //Makes it positive.
                    }
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
                            chnl.sendMessage("Amount to delete must be between 1 and 100");
                            break;
                        }

                        if (amount == 1)
                        {
                            //Cannot delete one with .bulkDelete()
                            msgs.get(0).delete(); //Again, ignore index 0, as it's the command
                            Utilities.cleanupMessage(chnl.sendMessage("1 message deleted. This message will self-destruct in 3 seconds..."), 3000);
                            chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** cleared 1 message from channel **" + chnl.getName() + "**");
                            msg.delete();
                            break;
                        }

                        ArrayList<IMessage> toDelete = new ArrayList<>();
                        try
                        {
                            for (int i = 0; i < amount; i++) //Start at 1 to ignore command - it's removed later.
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
                            Utilities.cleanupMessage(chnl.sendMessage(toDelete.size() + " messages deleted (out of " + amount + " requested). This message will self-destruct in 10 seconds..."), 10000);
                            chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** cleared " + toDelete.size() + " messages from channel **" + chnl.getName() + "**");
                        } else
                        {
                            chnl.sendMessage("You cannot do that!");
                        }

                        msg.delete();
                    } catch (NumberFormatException ex)
                    {
                        chnl.sendMessage("Invalid number of messages to delete specified. Must be a whole number between 1 and 100");
                    } catch (ArrayIndexOutOfBoundsException ex2)
                    {
                        Utilities.cleanupMessage(chnl.sendMessage("No messages found!"), 3000);
                    }
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Clearcache">
                case "clearcache":
                    if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
                    {
                        msg.delete(); //Delete silently.
                        break;
                    }
                    File kingdomsDir = new File("images/kingdoms/");
                    if (!kingdomsDir.isDirectory())
                    {
                        chnl.sendMessage("!!!!Kingdoms Directory is NOT a directory?!?!!!!");
                        break;
                    }
                    File classesDir = new File("images/classes/");
                    if (!classesDir.isDirectory())
                    {
                        chnl.sendMessage("!!!!Classes Directory is NOT a directory?!?!!!!");
                        break;
                    }

                    int count = 0;
                    long start = System.currentTimeMillis();
                    for (File f : kingdomsDir.listFiles())
                    {
                        if (f.getName().contains("stitched") || f.getName().contains("scaled"))
                        {
                            f.delete();
                            count++;
                        }
                    }
                    for (File f : classesDir.listFiles())
                    {
                        if (f.getName().contains("scaled"))
                        {
                            f.delete();
                            count++;
                        }
                    }
                    if (count == 0)
                    {
                        chnl.sendMessage("Cache already empty.");
                    } else
                    {
                        long time = System.currentTimeMillis() - start;
                        System.out.println(time);
                        float rate = (float) count / (float) time;

                        chnl.sendMessage("Cleared image cache. Removed " + count + " files that were stitched or scaled, leaving only the raw files, in " + time + " milliseconds, at a rate of " + rate + " image(s)/millisecond.");
                    }
                    msg.delete();
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Buildcache">
                case "buildcache":
                    if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
                    {
                        msg.delete(); //Delete silently.
                        break;
                    }
                    kingdomsDir = new File("images/kingdoms/");
                    if (!kingdomsDir.isDirectory())
                    {
                        chnl.sendMessage("!!!!Kingdoms Directory is NOT a directory?!?!!!!");
                        break;
                    }
                    classesDir = new File("images/classes/");
                    if (!classesDir.isDirectory())
                    {
                        chnl.sendMessage("!!!!Classes Directory is NOT a directory?!?!!!!");
                        break;
                    }

                    int genCount = 0;
                    start = System.currentTimeMillis();

                    JSONArray kingdoms = GameData.arrayKingdoms;
                    JSONArray classes = GameData.arrayClasses;

                    for (Iterator<Object> it = kingdoms.iterator(); it.hasNext();)
                    {
                        JSONObject kingdom = (JSONObject) it.next();

                        String thisId = kingdom.getString("FileBase");

                        if (kingdom.getString("BannerName").equals("Unnamed Banner"))
                        {
                            //No banner, just generate scaled.
                            File raw = new File("images/kingdoms/" + thisId + ".png");
                            File scaled = new File("images/kingdoms/" + thisId + "_scaled.png");
                            if (scaled.exists())
                            {
                                continue;
                            }
                            BufferedImage kingdomIcon = ImageUtils.scaleImage(0.5f, 0.5f, ImageIO.read(raw));
                            ImageUtils.writeImageToFile(kingdomIcon, "png", scaled);

                            genCount++;
                        } else
                        {
                            File stitched = new File("images/kingdoms/" + thisId + "_stitched.png");
                            if (stitched.exists())
                            {
                                continue;
                            }
                            File left = new File("images/kingdoms/" + thisId + ".png");
                            File right = new File("images/banner/" + thisId + ".png");

                            if (!left.exists())
                            {
                                main.log("Unable to generate image for kingdom " + kingdom.getString("Name") + " because the file: " + left.getName() + " doesn't exist!");
                                continue;
                            }
                            if (!right.exists())
                            {
                                main.log("Unable to generate image for kingdom " + kingdom.getString("Name") + " because the file: " + left.getName() + " doesn't exist!");
                                continue;
                            }
                            ImageUtils.writeImageToFile(ImageUtils.scaleImage(0.5f, 0.5f, ImageUtils.joinHorizontal(left, right)), "png", stitched);

                            genCount++;
                        }
                    }

                    for (Iterator<Object> it2 = classes.iterator(); it2.hasNext();)
                    {
                        JSONObject hClass = (JSONObject) it2.next();
                        String thisId = hClass.getString("Name").toLowerCase();

                        File original = new File("images/classes/" + thisId + ".png");
                        if (!original.exists())
                        {
                            main.log("Unable to generate image for class " + thisId + ", becuase the file: " + original.getName() + " doesn't exist!");
                            continue;
                        }

                        File scaled = new File("images/classes/" + thisId + "_scaled.png");
                        ImageUtils.writeImageToFile(ImageUtils.scaleImage(0.5f, 0.5f, ImageIO.read(original)), "png", scaled);

                        genCount++;
                    }

                    if (genCount == 0)
                    {
                        chnl.sendMessage("Cache already populated.");
                    } else
                    {
                        long time = System.currentTimeMillis() - start;
                        float timeSeconds = (float) time / 1000f;
                        float rate = (float) genCount / timeSeconds;

                        chnl.sendMessage("Populated image cache. Generated " + genCount + " files, in " + time + " milliseconds, at a rate of " + rate + " image(s)/second.");
                    }
                    msg.delete();
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
                    String troopType;
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
                    //get spell description
                    JSONObject troopSpell = main.data.getSpellInfo(spell);
                    String troopSpellDesc = troopSpell.getString("Description");

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

                    if (type2.equals("None"))
                    {
                        troopType = type1;
                    } else
                    {
                        troopType = type1 + "/" + type2;
                    }

                    String info = "**" + troopName + "** (" + desc + ")\n" + rarity + " from " + kingdom + ", Type: " + troopType + "\nMana: ";
                    info += manaTypes.toString().replace("[", "").replace("]", "").replace(", ", "");
                    info += "\nSpell: " + spell + " (" + summonCost + ")\n" + troopSpellDesc + "\nTraits: " + trait1 + ", " + trait2 + ", " + trait3 + "\nLevel 20: " + emojiArmor + " " + armor + "    " + emojiLife + " " + life + "    " + emojiAttack + " " + attack + "    " + emojiMagic + " " + magic;

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

                    File classIcon = new File("images/classes/" + className.toLowerCase() + ".png");
                    if (classIcon.exists())
                    {
                        File classIconShrunk = new File("images/classes/" + className.toLowerCase() + "_scaled.png");
                        ImageUtils.writeImageToFile(ImageUtils.scaleImage(0.5f, 0.5f, ImageIO.read(classIcon)), "png", classIconShrunk);
                        chnl.sendFile(classIconShrunk);
                    }
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
                    String troops = kingdomInfo.getJSONArray("Troops").toString().replace("[", "").replace("]", "").replace(",", ", ").replace("\"", "");

                    if (bannerName.equals("Unnamed Banner"))
                    {
                        //No banner, do not stitch.
                        File logo = new File("images/kingdoms/" + kingdomId + ".png");
                        File scaled = new File("images/kingdoms/" + kingdomId + "_scaled.png");
                        BufferedImage kingdomIcon = ImageUtils.scaleImage(0.5f, 0.5f, ImageIO.read(logo));
                        ImageUtils.writeImageToFile(kingdomIcon, "png", scaled);
                        chnl.sendFile(scaled);
                        chnl.sendMessage("**" + kingdomName + "**\nTroops (" + numTroops + "): " + troops + "\nNo Banner\n\n**Kingdom Bonus**\n" + bonus2 + " - " + bonus2Desc + "\n" + bonus3 + " - " + bonus3Desc + "\n" + bonus4 + " - " + bonus4Desc);
                    } else
                    {
                        File stitched = new File("images/kingdoms/" + kingdomId + "_stitched.png");
                        File left = new File("images/kingdoms/" + kingdomId + ".png");
                        File right = new File("images/banner/" + kingdomId + ".png");
                        if (!stitched.exists())
                        {
                            ImageUtils.writeImageToFile(ImageUtils.scaleImage(0.5f, 0.5f, ImageUtils.joinHorizontal(left, right)), "png", stitched);
                        }
                        chnl.sendFile(stitched);
                        chnl.sendMessage("**" + kingdomName + "**\nTroops (" + numTroops + "): " + troops + "\n" + bannerName + " - " + bannerDesc + "\n\n**Kingdom Bonus**\n" + bonus2 + " - " + bonus2Desc + "\n" + bonus3 + " - " + bonus3Desc + "\n" + bonus4 + " - " + bonus4Desc);
                    }
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Search">
                //?search [kingdoms|troops|traits|spells] [string]
                case "search":
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You need to specify a search term!");
                        break;
                    }
                    String searchTerm = arguments.toString().replace("[", "").replace("]", "").replace(",", "");

                    if (searchTerm.length() < 4)
                    {
                        chnl.sendMessage("Search term must be at least 4 characters long.");
                        break;
                    }
                    ArrayList<String> troopResults = new ArrayList<>();
                    ArrayList<String> traitResults = new ArrayList<>();
                    ArrayList<String> spellResults = new ArrayList<>();
                    ArrayList<String> kingdomResults = new ArrayList<>();
                    ArrayList<String> classResults = new ArrayList<>();

                    troopResults.addAll(main.data.searchForTroop(searchTerm));
                    traitResults.addAll(main.data.searchForTrait(searchTerm));
                    spellResults.addAll(main.data.searchForSpell(searchTerm));
                    kingdomResults.addAll(main.data.searchForKingdom(searchTerm));
                    classResults.addAll(main.data.searchForClass(searchTerm));

                    String troopRes = troopResults.isEmpty() ? "None" : troopResults.toString().replace("[", "").replace("]", "").replace("\"", "");
                    String traitRes = traitResults.isEmpty() ? "None" : traitResults.toString().replace("[", "").replace("]", "").replace("\"", "");
                    String spellRes = spellResults.isEmpty() ? "None" : spellResults.toString().replace("[", "").replace("]", "").replace("\"", "");
                    String kingdomRes = kingdomResults.isEmpty() ? "None" : kingdomResults.toString().replace("[", "").replace("]", "").replace("\"", "");
                    String classRes = classResults.isEmpty() ? "None" : classResults.toString().replace("[", "").replace("]", "").replace("\"", "");

                    String searchOutput = "Search results for `" + searchTerm + "`:\n\n";
                    if (!troopRes.equals("None"))
                    {
                        searchOutput += "**Troops**:\n" + troopRes + "\n\n";
                    }
                    if (!traitRes.equals("None"))
                    {
                        searchOutput += "**Traits**:\n" + traitRes + "\n\n";
                    }
                    if (!spellRes.equals("None"))
                    {
                        searchOutput += "**Spells**:\n" + spellRes + "\n\n";
                    }
                    if (!kingdomRes.equals("None"))
                    {
                        searchOutput += "**Kingdoms**:\n" + kingdomRes + "\n\n";
                    }
                    if (!classRes.equals("None"))
                    {
                        searchOutput += "**Hero Classes**:\n" + classRes + "\n\n";
                    }

                    chnl.sendMessage(searchOutput);
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
                    if (role.equals("pc/mobile") || role.equals("pc") || role.equals("mobile"))
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
                        msg.delete();
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
                        msg.delete();
                    } else
                    {
                        chnl.sendMessage("You cannot do that!");
                    }
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Userstats">
                //?userstats    
                case "userstats":
                    IUser userstatsUsr;
                    if (arguments.isEmpty())
                    {
                        userstatsUsr = sdr;
                    } else
                    {
                        String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
                        userstatsUsr = chnl.getGuild().getUserByID(id);
                    }
                    String name = userstatsUsr.getName();
                    String nickname = userstatsUsr.getNicknameForGuild(chnl.getGuild()).isPresent() ? userstatsUsr.getNicknameForGuild(chnl.getGuild()).get() : userstatsUsr.getName();
                    Boolean hasNick = !name.equals(nickname);
                    Status state = userstatsUsr.getStatus();
                    List<IRole> sdrRoles = userstatsUsr.getRolesForGuild(chnl.getGuild());
                    int numRolesSdr = sdrRoles.size() - 1; //-1 to remove @everyone
                    int messageCount = messageCounter.getMessageCountForUser(sdr, chnl.getGuild());
                    int commandCount = messageCounter.getCommandCountForUser(sdr, chnl.getGuild());

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
                    toSend += "\nGame: " + (state.getStatusMessage() == null ? "nothing" : "\"" + state.getStatusMessage() + "\"");
                    toSend += "\nNumber of roles: " + numRolesSdr;
                    toSend += "\nList of Roles: " + sdrRolesNice.toString();
                    toSend += "\nMessages sent: " + messageCount;
                    toSend += "\nCommands sent: " + commandCount;
                    toSend += "\n```";

                    chnl.sendMessage(toSend);
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Serverstats">
                //?serverstats
                case "serverstats":
                    List<IRole> guildRoles = chnl.getGuild().getRoles();
                    int numRolesGuild = guildRoles.size() - 1; //Again, -1 to remove @everyone

                    String toSendServer = "```\n--Server Info---------";
                    toSendServer += "\nRoles: " + numRolesGuild;
                    toSendServer += "\nChannels: " + chnl.getGuild().getChannels().size();
                    toSendServer += "\nMembers: " + chnl.getGuild().getUsers().size();

                    int msgCount = 0;
                    int cmdCount = 0;
                    for (String id : messageCounter.getUserIDList(chnl.getGuild()))
                    {
                        msgCount += messageCounter.getMessageCountForUser(chnl.getGuild().getUserByID(id), chnl.getGuild());
                        cmdCount += messageCounter.getCommandCountForUser(chnl.getGuild().getUserByID(id), chnl.getGuild());
                    }

                    toSendServer += "\nMessages sent: " + msgCount;
                    toSendServer += "\nCommands sent: " + cmdCount;
                    toSendServer += "\n--Roles Info---------";

                    int unassigned = chnl.getGuild().getUsers().size();
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
                        toSendServer += "\n" + chnl.getGuild().getUsersByRole(r2).size() + "x " + r2.getName();
                    }
                    for (IUser u : chnl.getGuild().getUsers())
                    {
                        if (Utilities.userHasRole(chnl.getGuild(), u, chnl.getGuild().getRoleByID(IDReference.RoleID.CONSOLE.toString())) || Utilities.userHasRole(chnl.getGuild(), u, chnl.getGuild().getRoleByID(IDReference.RoleID.PCMOBILE.toString())))
                        {
                            unassigned--;
                        }
                    }
                    toSendServer += "\n" + unassigned + "x Not Assigned";
                    toSendServer += "\n```";

                    chnl.sendMessage(toSendServer);
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
                        msg.delete();
                    } else
                    {
                        chnl.sendMessage("You cannot do that!");
                    }
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Newcode">
                //?newcode [string] 
                case "newcode":
                    String newEmoji = ":new:";
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You have to enter a code first!");
                        break;
                    }
                    String code = arguments.get(0);
                    if (code.length() == 10)
                    {
                        if (main.codes.isCodePresent(code))
                        {
                            chnl.sendMessage("That code is already present! Did you mean `?dead [code]`?");
                            break;
                        }
                        main.codes.addCode(code);
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.CODES.toString()).sendMessage(newEmoji + " Code: `" + arguments.get(0).toUpperCase() + "` " + newEmoji);
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** posted '" + code + "' as new code.");
                        break;
                    } else
                    {
                        chnl.sendMessage("Please check your code - it has to be 10 characters long, and yours is " + code.length() + "!");
                        break;
                    }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Dead">
                //?dead [string]    
                case "dead":
                    String skullEmoji = chnl.getGuild().getEmojiByName("gow_skull").toString();
                    if (arguments.size() < 1)
                    {
                        chnl.sendMessage("You have to enter a code first!");
                        break;
                    }
                    String code2 = arguments.get(0);
                    if (code2.length() == 10)
                    {
                        if (!main.codes.isCodePresent(code2))
                        {
                            chnl.sendMessage("No code `" + code2 + "` found!");
                            break;
                        }
                        if (main.codes.isCodeDead(code2))
                        {
                            chnl.sendMessage("That code is already marked as dead!");
                            break;
                        }
                        main.codes.makeCodeDead(code2);
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.CODES.toString()).sendMessage(skullEmoji + " Code `" + arguments.get(0).toUpperCase() + "` is dead! " + skullEmoji);
                        chnl.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("**" + nameOfSender + "** reported '" + code2 + "' as dead.");
                        break;
                    } else
                    {
                        chnl.sendMessage("Please check your code - it has to be 10 characters long, and yours is " + code2.length() + "!");
                        break;
                    }
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Codes">
                //?codes
                case "codes":
                    ArrayList<String> codes = main.codes.getLiveCodes();
                    if (codes.isEmpty())
                    {
                        sdr.getOrCreatePMChannel().sendMessage("No codes are currently \"Alive\".");
                        break;
                    }
                    sdr.getOrCreatePMChannel().sendMessage("Currently \"Alive\" codes: `" + codes.toString().replace("[", "").replace("]", "").replace("\"", "") + "`.");
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Top10">
                case "top10":
                    LinkedHashMap<IUser, Integer> unordered = new LinkedHashMap<>();
                    ValueComparator comp = new ValueComparator((Map<IUser, Integer>) unordered);
                    TreeMap<IUser, Integer> ordered = new TreeMap<>(comp);

                    for (String id : messageCounter.getUserIDList(chnl.getGuild()))
                    {
                        if (id.equals(IDReference.MYID))
                        {
                            continue; //Skip the bot.
                        }
                        IUser current = chnl.getGuild().getUserByID(id);

                        unordered.put(current, messageCounter.getMessageCountForUser(current, chnl.getGuild()));
                    }
                    main.log("Top10: Unordered: " + unordered);
                    ordered.putAll(unordered); //Now it's sorted, by values
                    main.log("Top10: Ordered: " + ordered);

                    String toSend1 = "```\nTOP 10 USERS (BY MESSAGE COUNT) IN SERVER\nName" + Utilities.repeatString(" ", 56) + "Number of messages\n";

                    int count1 = 0;
                    int numSpaces = 60;
                    for (IUser u : ordered.descendingKeySet())
                    {
                        count1++;
                        toSend1 += "\n" + u.getName() + Utilities.repeatString(" ", numSpaces - u.getName().length()) + unordered.get(u);
                        if (count1 > 10)
                        {
                            break;
                        }
                    }

                    toSend1 += "\n```";

                    chnl.sendMessage(toSend1);
                    break;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Help">
                //?help
                case "help":
                    toSendServer = "I recognize the following commands: \n";
                    for (Command c : main.getRegisteredCommands())
                    {
                        toSendServer += "\n**" + c.getName() + "**: " + c.getDescription();
                    }
                    if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
                    {
                        toSendServer += "\n\nAdmin Commands (These actions WILL be logged):\n";
                        for (AdminCommand ac : main.getRegisteredAdminCommands())
                        {
                            toSendServer += "\n**" + ac.getName() + "**: " + ac.getDescription();
                        }
                    } else
                    {
                        toSendServer += "\n\n(" + main.getRegisteredAdminCommands().size() + " commands not shown because you are not a high-enough rank)";
                    }
                    toSendServer += "\n-------------------------------------------------------------";

                    sdr.getOrCreatePMChannel().sendMessage(toSendServer);
                    chnl.sendMessage(sdr.mention() + ", I've sent you a list of commands over PM.");
                    break;
                //</editor-fold>
                default:
                    chnl.sendMessage("Invalid command \"" + command + "\"");
                    break;
            }
            //msg.delete(); //Cleanup command
        } catch (RateLimitException rle)
        {
            try
            {
                main.log("Rate limited! Time until un-ratelimited: " + rle.getRetryDelay());
                main.getClient(null).getGuildByID(IDReference.SERVERID).getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("Bot needs to slow down! We're rate limited for another " + rle.getRetryDelay() + " milliseconds, please tell SamboyCoding or MrSnake that the following section is too fast: " + rle.getRoute());
            } catch (Exception e2)
            {
                main.log("Exception sending ratelimit warning!");
                e2.printStackTrace();
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @EventSubscriber
    public void onJoin(UserJoinEvent e
    )
    {
        try
        {
            String nameOfUser = e.getUser().getNicknameForGuild(e.getGuild()).isPresent() ? e.getUser().getNicknameForGuild(e.getGuild()).get() : e.getUser().getName();
            e.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("User **" + nameOfUser + "** joined the server!");
        } catch (Exception ignored)
        {
            //Ignore.
        }
    }

    @EventSubscriber
    public void onLeave(UserLeaveEvent e
    )
    {
        try
        {
            String nameOfUser = e.getUser().getNicknameForGuild(e.getGuild()).isPresent() ? e.getUser().getNicknameForGuild(e.getGuild()).get() : e.getUser().getName();
            e.getGuild().getChannelByID(IDReference.ChannelID.LOGS.toString()).sendMessage("User **" + nameOfUser + "** left the server!");
        } catch (Exception ignored)
        {
            //Ignore.
        }
    }
}

class ValueComparator implements Comparator<IUser>
{

    Map<IUser, Integer> base;

    public ValueComparator(Map<IUser, Integer> base)
    {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(IUser a, IUser b)
    {
        if (base.get(a) >= base.get(b))
        {
            return 1;
        } else
        {
            return -1;
        } // returning 0 would merge keys
    }
}
