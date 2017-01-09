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
import java.net.URLEncoder;
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
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.Search;
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
public class AshTroopCommand extends QuestionCommand
{

    public AshTroopCommand()
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
            Search search = AshClient.query("searches/troops?term=" + URLEncoder.encode(troopName, "UTF-8"), Search.class);
            Search.Troop searchTroop = AshClient.getSingleResult(chnl, search.getTroops(), "troop", troopName, Search.Troop.class);
            if (searchTroop == null)
            {
                return;
            }
            Troop troop = AshClient.query("troops/" + searchTroop.getId() + "/details", Troop.class);

            String spellDesc = troop.getSpellDescription();
            String spellMagicScalingText = troop.getSpellMagicScalingText();
            if (spellMagicScalingText != null)
            {
                if (!spellDesc.contains("{2}"))
                {
                    spellDesc = spellDesc.replace("{1}", spellMagicScalingText);
                } else
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

            String[] traitNames = troop.getTraits().stream().map(t -> t.getName()).toArray(String[]::new);

            String info = "";
            info += troop.getRarity() + " _" + troop.getKingdomName() + "_ " + troop.getType() + "\n";
            info += "(" + String.join(", ", traitNames) + ")\n\n";
            info += "_" + troop.getSpellName() + "_ (" + troop.getSpellCost() + " " + String.join(" ", gemColorEmojis) + ")\n" + spellDesc + "\n\n";

            info += emojiArmor + troop.getMaxArmor() + "   " + emojiLife + troop.getMaxLife()
                    + "   " + emojiAttack + troop.getMaxAttack() + "   " + emojiMagic + troop.getMaxMagic() + "\n";
            info += "_" + troop.getDescription() + "_";

            EmbedObject o = new EmbedBuilder()
                    .withDesc(info)
                    .withTitle(troop.getName())
                    .withUrl(troop.getPageUrl())
                    .withThumbnail(troop.getImageUrl())
                    .build();
            chnl.sendMessage("", o, false);
        } catch (IOException e)
        {
            chnl.sendMessage("Credentials file could not be found.");
            throw e;
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
