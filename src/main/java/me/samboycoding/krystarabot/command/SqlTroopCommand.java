/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.command;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.sql.DataSource;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.Trait;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.io.FileUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 *
 * @author julians
 */
public class SqlTroopCommand extends QuestionCommand
{
    public SqlTroopCommand()
    {
        commandName = "troop";

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
            String troopName = String.join(" ", arguments);
            QueryRunner run = GemsQueryRunner.getQueryRunner();
            ResultSetHandler<List<Troop>> troopHandler = new BeanListHandler<>(Troop.class);
            List<Troop> troops = run.query("SELECT TroopStats.*, Kingdoms.Name AS KingdomName, "
                + "     Spells.Name AS SpellName, Spells.Description AS SpellDescription, "
                + "     Spells.BoostRatioText AS SpellBoostRatioText, Spells.MagicScalingText AS SpellMagicScalingText, "
                + "     Spells.Cost AS SpellCost, Troops.*"
                + "FROM Troops "
                + "INNER JOIN TroopStats ON TroopStats.TroopId=Troops.Id "
                + "INNER JOIN Kingdoms ON Kingdoms.Id=Troops.KingdomId AND Kingdoms.Language=Troops.Language "
                + "INNER JOIN Spells ON Spells.Id=Troops.SpellId AND Spells.Language=Troops.Language "
                + "WHERE Troops.Language='en-US' AND Troops.Name LIKE ? AND Troops.ReleaseDate<NOW() "
                + "ORDER BY Troops.Name", troopHandler,
                troopName + "%"
                );

            if (troops.isEmpty())
            {
                chnl.sendMessage("No troop `" + troopName + "` found.");
                return;
            }
            else if ((troops.size() > 1) && (!troops.get(0).getName().toLowerCase().equals(troopName.toLowerCase())))
            {
                Stream<String> str = troops.stream().map(t -> t.getName());
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + troopName + "\" is ambiguous.", str::iterator);
                return;
            }
            
            Troop troop = troops.get(0);
            
            ResultSetHandler<List<Trait>> traitHandler = new BeanListHandler<>(Trait.class);
            List<Trait> traits = run.query("SELECT Traits.* FROM TroopTraits "
                + "INNER JOIN Traits ON Traits.Code=TroopTraits.Code "
                + "WHERE Traits.Language='en-US' AND TroopTraits.TroopId=? AND TroopTraits.CostIndex=0 "
                + "ORDER BY TraitIndex", traitHandler,
                troop.getId()
                );

            String spellDesc = troop.getSpellDescription();
            String spellMagicScalingText = troop.getSpellMagicScalingText();
            if (spellMagicScalingText != null)
            {
                if (!spellDesc.contains("{2}"))
                {
                    spellDesc = spellDesc.replace("{1}", spellMagicScalingText);
                }
                else
                {
                    spellDesc = spellDesc.replace("{1}", "(half)");
                    spellDesc = spellDesc.replace("{2}", spellMagicScalingText);
                }
            }
            String spellBoostRatioText = troop.getSpellBoostRatioText();
            if (spellBoostRatioText != null)
            {
                spellDesc += spellBoostRatioText;
            }

            //Emojis
            IGuild g = chnl.getGuild();
            String emojiArmor = g.getEmojiByName("gow_armor").toString();
            String emojiLife = g.getEmojiByName("gow_life").toString();
            String emojiAttack = g.getEmojiByName("gow_attack").toString();
            String emojiMagic = g.getEmojiByName("gow_magic").toString();
            
            GemColor[] gemColors = GemColor.fromInteger(troop.getColors());
            String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);

            String[] traitNames = traits.stream().map(t -> t.getName()).toArray(String[]::new);
            
            String info = "";
            info += troop.getRarity() + " _" + troop.getKingdomName() + "_ " + troop.getType() + "\n";
            info += "(" + String.join(", ", traitNames) + ")\n\n";
            info += "_" + troop.getSpellName() + "_ (" + troop.getSpellCost() + " " + String.join(" ", gemColorEmojis) + ")\n" + spellDesc + "\n\n";

            info += emojiArmor + troop.getMaxArmor() + "   " + emojiLife + troop.getMaxLife() + 
                "   " + emojiAttack + troop.getMaxAttack() + "   " + emojiMagic + troop.getMaxMagic() + "\n";
            info += "_" + troop.getDescription() + "_";

            EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(troop.getName())
                .withUrl("http://ashtender.com/gems/troops/" + troop.getId())
                .withThumbnail("http://ashtender.com/gems/assets/cards/" + troop.getFileBase() + ".jpg")
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
        return "Shows information for the specified troop.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
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
        return MOD;
    }
}
