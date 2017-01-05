package me.samboycoding.krystarabot.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.gemdb.Bonus;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Trait;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.ImageUtils;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.json.JSONObject;
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
public class SqlKingdomCommand extends KrystaraCommand
{
    
    public SqlKingdomCommand()
    {
        commandName = "kingdom";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            chnl.sendMessage("You cannot do that!");
            return;
        }
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }
        
        try
        {
            String kingdomName = String.join(" ", arguments);
            Kingdom kingdom = GemsQueryRunner.runQueryForSingleResultByName(
                chnl, 
                "SELECT Kingdoms.*, Traitstones.Name AS ExploreTraitstoneName, Traitstones.Colors AS ExploreTraitstoneColors FROM Kingdoms "
                + "LEFT JOIN Traitstones ON Kingdoms.ExploreTraitstoneId=Traitstones.Id AND Traitstones.Language=Kingdoms.Language "
                + "WHERE Kingdoms.Language='en-US' AND Kingdoms.Name LIKE ? AND Kingdoms.IsUsed "
                + "ORDER BY Kingdoms.Name", 
                "kingdom", 
                Kingdom.class, 
                kingdomName
                );
            
            if (kingdom == null)
            {
                return;
            }

            boolean isFullKingdom = kingdom.getIsFullKingdom();
            
            QueryRunner run = GemsQueryRunner.getQueryRunner();
            ResultSetHandler<List<Troop>> troopHandler = new BeanListHandler<>(Troop.class);
            List<Troop> troops = run.query("SELECT Troops.Name FROM Troops "
                + "WHERE Troops.Language='en-US' AND Troops.KingdomId=? AND Troops.ReleaseDate<NOW() "
                + "ORDER BY Name", troopHandler,
                kingdom.getId()
                );

            ResultSetHandler<List<Bonus>> bonusHandler = new BeanListHandler<>(Bonus.class);
            List<Bonus> bonuses = run.query("SELECT Bonuses.* FROM Bonuses "
                + "WHERE Bonuses.Language='en-US' AND Bonuses.KingdomId=? "
                + "ORDER BY TroopCount", bonusHandler,
                kingdom.getId()
                );

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

            String[] troopNames = troops.stream().map(t -> t.getName()).toArray(String[]::new);
            
            String info = "";
            info += "Troops (" + troopNames.length + "): " + String.join(", ", troopNames) + "\n";
            if (isFullKingdom)
            {
                info += kingdom.getBannerName() + ": " + kingdom.getBannerDescription() + "\n";
            }
            info += "\n**Bonuses**\n";

            for (Bonus bonus : bonuses)
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
                info += emojiGold + kingdom.getTributeGold() + "   " + emojiSouls + kingdom.getTributeSouls() + "   " +
                    emojiGlory + kingdom.getTributeGlory() + "\n\n";
                info += "Kingdom level 10 grants +1" + emojiLevelStat + " to all troops.\n";
                info += "Exploration traitstone: " + kingdom.getExploreTraitstoneName() + " (" + String.join(" ", gemColorEmojis) + ")\n";
            }

            EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(kingdom.getName())
                .withUrl("http://ashtender.com/gems/kingdoms/" + kingdom.getId())
                .withThumbnail("http://ashtender.com/gems/assets/shields/" + kingdom.getFileBase() + ".png")
                .build();
            chnl.sendMessage("", o, false);
        }
        catch (IOException e)
        {
            chnl.sendMessage("Credentials file could not be found.");
            throw e;
        }
        catch (SQLException e2)
        {
            chnl.sendMessage("Database query failed.");
            throw e2;
        }
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
