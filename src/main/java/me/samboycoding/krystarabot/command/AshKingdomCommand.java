package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.Bonus;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Search;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Represents the ?kingdom command
 *
 * @author r3byass
 */
public class AshKingdomCommand extends KrystaraCommand
{

    public AshKingdomCommand()
    {
        commandName = "kingdom";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }

        String kingdomName = String.join(" ", arguments);
        Search search = AshClient.query("searches/kingdoms?term=" + URLEncoder.encode(kingdomName, "UTF-8"), Search.class);
        Search.Kingdom searchKingdom = AshClient.getSingleResult(chnl, search.getKingdoms(), "kingdom", kingdomName, Search.Kingdom.class);
        if (searchKingdom == null)
        {
            return;
        }
        Kingdom kingdom = AshClient.query("kingdoms/" + searchKingdom.getId() + "/details", Kingdom.class);

        boolean isFullKingdom = kingdom.getIsFullKingdom();

        //Emojis
        IGuild g = chnl.getGuild();
        String emojiArmor = g.getEmojiByName("gow_armor").toString();
        String emojiLife = g.getEmojiByName("gow_life").toString();
        String emojiAttack = g.getEmojiByName("gow_attack").toString();
        String emojiMagic = g.getEmojiByName("gow_magic").toString();
        String emojiGold = g.getEmojiByName("gow_gold").toString();
        String emojiSouls = g.getEmojiByName("gow_soul").toString();
        String emojiGlory = g.getEmojiByName("gow_glory").toString();
        String emojiLevelStat = null;
        if (isFullKingdom)
        {
            emojiLevelStat = g.getEmojiByName("gow_" + kingdom.getLevelStat()).toString();
        }

        GemColor[] gemColors = GemColor.fromInteger(kingdom.getExploreTraitstoneColors());
        String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);

        String[] troopNames = kingdom.getTroops().stream().map(t -> t.getName()).toArray(String[]::new);

        String info = "";
        info += "Troops (" + troopNames.length + "): " + String.join(", ", troopNames) + "\n";
        if (isFullKingdom)
        {
            info += kingdom.getBannerName() + ": " + kingdom.getBannerDescription() + "\n";
        }
        info += "\n**Bonuses**\n";

        for (Bonus bonus : kingdom.getBonuses())
        {
            ArrayList<String> bonusStats = new ArrayList<>();
            info += "x" + bonus.getTroopCount() + ": " + bonus.getName() + " - ";
            if (bonus.getArmor() > 0)
            {
                bonusStats.add("+" + bonus.getArmor() + emojiArmor);
            }
            if (bonus.getLife() > 0)
            {
                bonusStats.add("+" + bonus.getLife() + emojiLife);
            }
            if (bonus.getAttack() > 0)
            {
                bonusStats.add("+" + bonus.getAttack() + emojiAttack);
            }
            if (bonus.getMagic() > 0)
            {
                bonusStats.add("+" + bonus.getMagic() + emojiMagic);
            }
            info += String.join("  ", bonusStats) + "\n";
        }
        if (isFullKingdom)
        {
            info += "\n";
            info += "**Tribute**\n";
            info += emojiGold + kingdom.getTributeGold() + "   " + emojiSouls + kingdom.getTributeSouls() + "   "
                    + emojiGlory + kingdom.getTributeGlory() + "\n\n";
            info += "Kingdom level 10 grants +1" + emojiLevelStat + " to all troops.\n";
            info += "Exploration traitstone: " + kingdom.getExploreTraitstoneName() + " (" + String.join(" ", gemColorEmojis) + ")\n";
        }

        EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(kingdom.getName())
                .withUrl(kingdom.getPageUrl())
                .withThumbnail(kingdom.getImageUrl())
                .build();
        chnl.sendMessage("", o, false);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified kingdom.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?kingdom [name]";
    }

    @Override
    public String getCommand()
    {
        return "kingdom";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
