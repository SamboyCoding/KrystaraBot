package me.samboycoding.krystarabot.command;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.Spell;
import me.samboycoding.krystarabot.gemdb.Trait;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?spell command
 *
 * @author Emily
 */
public class SqlSpellCommand extends KrystaraCommand
{

    public SqlSpellCommand()
    {
        commandName = "spell";
    }
    
    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }
        
        try
        {
            String spellName = String.join(" ", arguments);
            QueryRunner run = GemsQueryRunner.getQueryRunner();
            ResultSetHandler<List<Spell>> spellHandler = new BeanListHandler<>(Spell.class);
            List<Spell> spells = run.query("SELECT Spells.*, Troops.Name AS TroopName, COALESCE(GREATEST(Troops.Colors, Weapons.Colors), Troops.Colors, Weapons.Colors) AS Colors "
                + "FROM Spells "
                + "LEFT JOIN Troops ON Troops.SpellId=Spells.Id AND Troops.Language=Spells.Language AND Troops.ReleaseDate<NOW() "
                + "LEFT JOIN Weapons ON Weapons.SpellId=Spells.Id AND Weapons.Language=Spells.Language AND Weapons.ReleaseDate<NOW() "
                + "WHERE Spells.Language='en-US' AND Spells.Name LIKE ?"
                + "ORDER BY Spells.Name", spellHandler,
                spellName + "%"
                );

            if (spells.isEmpty())
            {
                chnl.sendMessage("No spell `" + spellName + "` found.");
                return;
            }
            else if ((spells.size() > 1) && (!spells.get(0).getName().toLowerCase().equals(spellName.toLowerCase())))
            {
                Stream<String> str = spells.stream().map(t -> t.getName());
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + spellName + "\" is ambiguous.", str::iterator);
                return;
            }
            
            Spell spell = spells.get(0);

            String spellDesc = spell.getDescription();
            String spellMagicScalingText = spell.getMagicScalingText();
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
            String spellBoostRatioText = spell.getBoostRatioText();
            if (spellBoostRatioText != null)
            {
                spellDesc += spellBoostRatioText;
            }

            //Emojis
            IGuild g = chnl.getGuild();
            
            GemColor[] gemColors = GemColor.fromInteger(spell.getColors());
            String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);

            String info = "**" + spell.getName() + "** (" + spell.getCost() + " " + String.join(" ", gemColorEmojis) + "): " + spellDesc + "\n";
            if (spell.getTroopName() != null)
            {
                info += "Used by: " + spell.getTroopName() + "\n";
            }
            chnl.sendMessage(info);
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
        return "Shows information for the specified spell.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?spell [name]";
    }

    @Override
    public String getCommand()
    {
        return "spell";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
