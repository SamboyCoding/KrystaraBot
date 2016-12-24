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
import me.samboycoding.krystarabot.gemdb.Trait;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.gemdb.Weapon;
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
 * Represents the ?weapon command
 *
 * @author Emily
 */
public class SqlWeaponCommand extends KrystaraCommand
{

    public SqlWeaponCommand()
    {
        commandName = "weapon";
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
            String weaponName = String.join(" ", arguments);
            QueryRunner run = GemsQueryRunner.getQueryRunner();
            ResultSetHandler<List<Weapon>> weaponHandler = new BeanListHandler<>(Weapon.class);
            List<Weapon> weapons = run.query("SELECT Spells.Name AS SpellName, Spells.Description AS SpellDescription, "
                + "     Spells.BoostRatioText AS SpellBoostRatioText, Spells.MagicScalingText AS SpellMagicScalingText, "
                + "     Spells.Cost AS SpellCost, Spells.Id AS SpellId, Weapons.*"
                + "FROM Weapons "
                + "INNER JOIN Spells ON Spells.Id=Weapons.SpellId AND Spells.Language=Weapons.Language "
                + "WHERE Weapons.Language='en-US' AND Weapons.Name LIKE ? AND Weapons.ReleaseDate<NOW() "
                + "ORDER BY Weapons.Name", weaponHandler,
                weaponName + "%"
                );

            if (weapons.isEmpty())
            {
                chnl.sendMessage("No weapon `" + weaponName + "` found.");
                return;
            }
            else if ((weapons.size() > 1) && (!weapons.get(0).getName().toLowerCase().equals(weaponName.toLowerCase())))
            {
                Stream<String> str = weapons.stream().map(t -> t.getName());
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + weaponName + "\" is ambiguous.", str::iterator);
                return;
            }
            
            Weapon weapon = weapons.get(0);

            URL url = new URL("http://ashtender.com/gems/assets/spells/" + weapon.getSpellId() + ".jpg");

            String spellDesc = weapon.getSpellDescription();
            String spellMagicScalingText = weapon.getSpellMagicScalingText();
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
            String spellBoostRatioText = weapon.getSpellBoostRatioText();
            if (spellBoostRatioText != null)
            {
                spellDesc += spellBoostRatioText;
            }

            //Emojis
            IGuild g = chnl.getGuild();
            
            GemColor[] gemColors = GemColor.fromInteger(weapon.getColors());
            String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);

            String info = "**" + weapon.getName() + "**\n";
            info += weapon.getRarity() + "\n\n";
            info += "**Spell**";
            info += "\n" + weapon.getSpellName() + " (" + weapon.getSpellCost() + " " + String.join(" ", gemColorEmojis) + ")\n" + spellDesc;

            chnl.sendMessage(info);
            chnl.sendFile("", false, url.openStream(), weapon.getFileBase() + ".jpg");
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
        return "Shows information for the specified weapon.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?weapon [name]";
    }

    @Override
    public String getCommand()
    {
        return "weapon";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
