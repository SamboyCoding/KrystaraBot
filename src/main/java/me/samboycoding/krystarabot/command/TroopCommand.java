package me.samboycoding.krystarabot.command;

import java.net.URL;
import java.util.ArrayList;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.main;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?troop command
 *
 * @author Sam
 */
public class TroopCommand extends KrystaraCommand
{
    
    public TroopCommand()
    {
        commandName = "troop";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!GameData.dataLoaded)
        {
            chnl.sendMessage("Sorry, the data hasn't been loaded (yet). Please try again shortly, and if it still doesn't work, contact one of the bot devs.");
            return;
        }
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }
        String troopName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
        JSONObject troopInfo = main.data.getTroopInfo(troopName);
        if (troopInfo == null)
        {
            chnl.sendMessage("No troop `" + troopName + "` found, " + sdr.mention());
            return;
        }
        if (!troopInfo.getBoolean("IsVisible"))
        {
            chnl.sendMessage("That troop isn't yet released! Check back at a later date (try next monday) to see its stats!");
            return;
        }
        String desc = troopInfo.getString("Description").replace("\n", "");
        troopName = troopInfo.getString("Name");
        String kingdom = troopInfo.getString("Kingdom");
        String rarity = troopInfo.getString("TroopRarity");
        String troopType;
        String type1 = troopInfo.getString("TroopType");
        String type2 = troopInfo.getString("TroopType2");
        String spell = troopInfo.getJSONObject("Spell").getString("Name");
        int summonCost = troopInfo.getJSONObject("Spell").getInt("Cost");

        String trait1;
        String trait2;
        String trait3;

        switch (troopInfo.getJSONArray("ParsedTraits").length())
        {
            case 0:
                trait1 = "None";
                trait2 = "None";
                trait3 = "None";
                break;
            case 1:
                trait1 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(0).getString("Name");
                trait2 = "None";
                trait3 = "None";
                break;
            case 2:
                trait1 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(0).getString("Name");
                trait2 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(1).getString("Name");
                trait3 = "None";
                break;
            case 3:
                trait1 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(0).getString("Name");
                trait2 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(1).getString("Name");
                trait3 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(2).getString("Name");
                break;
            default:
                //4+ - only take first 3
                trait1 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(0).getString("Name");
                trait2 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(1).getString("Name");
                trait3 = troopInfo.getJSONArray("ParsedTraits").getJSONObject(2).getString("Name");
                break;
        }

        int armor = main.data.getLevel20ForProperty(troopInfo.getInt("Armor_Base"), troopInfo.getJSONArray("ArmorIncrease"), troopInfo.getJSONArray("Ascension_Armor"));
        int life = main.data.getLevel20ForProperty(troopInfo.getInt("Health_Base"), troopInfo.getJSONArray("HealthIncrease"), troopInfo.getJSONArray("Ascension_Health"));
        int attack = main.data.getLevel20ForProperty(troopInfo.getInt("Attack_Base"), troopInfo.getJSONArray("AttackIncrease"), troopInfo.getJSONArray("Ascension_Attack"));
        int magic = main.data.getLevel20ForProperty(troopInfo.getInt("SpellPower_Base"), troopInfo.getJSONArray("SpellPowerIncrease"), null);
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
        chnl.sendFile("", false, URL.openStream(), troopId + ".jpg");
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified troop.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?troop [name]";
    }

    @Override
    public String getCommand()
    {
        return "troop";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
