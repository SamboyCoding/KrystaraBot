package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import me.samboycoding.krystarabot.Language;
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
public class KingdomCommand extends KrystaraCommand
{

    public KingdomCommand()
    {
        commandName = "kingdom";
    }

    @Override
    public Boolean isLocalized()
    {
        return true;
    }
    
    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        handleCommand(sdr, chnl, msg, arguments, argsFull, Language.ENGLISH);
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull, Language lang) throws Exception
    {
        if (arguments.size() < 1)
        {
            chnl.sendMessage(lang.localize(Language.LocString.PLEASE_SPECIFY_TERM_TO_SEARCH));
            return;
        }

        String kingdomName = String.join(" ", arguments);
        Search search = Search.fromQuery("kingdoms?term=" + URLEncoder.encode(kingdomName, "UTF-8"), lang);
        Kingdom.Summary kingdomSummary = AshClient.getSingleResult(chnl, search.getKingdoms(), kingdomName, lang);
        if (kingdomSummary == null)
        {
            return;
        }

        Kingdom kingdom = kingdomSummary.getDetails(lang);
        boolean isFullKingdom = kingdom.isFullKingdom();

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
        info += lang.localize(Language.LocString.CATEGORY_TROOPS) + " (" + troopNames.length + "): " + String.join(", ", troopNames) + "\n";
        if (isFullKingdom)
        {
            info += kingdom.getBannerName() + ": " + kingdom.getBannerDescription() + "\n";
        }
        info += "\n**" + lang.localize(Language.LocString.CATEGORY_BONUSES) + "**\n";

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
            info += "**" + lang.localize(Language.LocString.CATEGORY_TRIBUTE) + "**\n";
            info += emojiGold + kingdom.getTributeGold() + "   " + emojiSouls + kingdom.getTributeSouls() + "   "
                    + emojiGlory + kingdom.getTributeGlory() + "\n\n";
            info += lang.localizeFormat(Language.LocString.KINGDOM_LEVEL_GRANTS_BONUS_FORMAT, 10, emojiLevelStat) + "\n";
            info += lang.localize(Language.LocString.EXPLORATION_TRAITSTONE) + " " + kingdom.getExploreTraitstoneName() + " (" + String.join(" ", gemColorEmojis) + ")\n";
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
