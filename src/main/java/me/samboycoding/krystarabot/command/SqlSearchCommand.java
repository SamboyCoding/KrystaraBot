package me.samboycoding.krystarabot.command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.HeroClass;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Spell;
import me.samboycoding.krystarabot.gemdb.Trait;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.gemdb.Weapon;
import me.samboycoding.krystarabot.main;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author r3byass
 */
public class SqlSearchCommand extends KrystaraCommand
{
    public SqlSearchCommand()
    {
        commandName = "search";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a search term!");
            return;
        }

        try
        {
            String searchTerm = String.join(" ", arguments);
            QueryRunner run = GemsQueryRunner.getQueryRunner();

            if (searchTerm.length() < 4)
            {
                chnl.sendMessage("Search term must be at least 4 characters long.");
                return;
            }

            ResultSetHandler<List<Troop>> troopHandler = new BeanListHandler<>(Troop.class);
            List<Troop> troops = run.query("SELECT Troops.Name FROM Troops "
                + "WHERE Troops.Language='en-US' AND Troops.ReleaseDate<NOW() AND ((Troops.Name LIKE ?) OR (Troops.Name SOUNDS LIKE ?)) "
                + "ORDER BY Troops.Name", troopHandler,
                searchTerm + "%",
                searchTerm
                );
            ResultSetHandler<List<Trait>> traitHandler = new BeanListHandler<>(Trait.class);
            List<Trait> traits = run.query("SELECT Traits.Name, CONCAT_WS(', ', GROUP_CONCAT(Troops.Name ORDER BY Troops.Name SEPARATOR ', '), GROUP_CONCAT(Classes.Name ORDER BY Classes.Name SEPARATOR ', ')) AS Description FROM Traits " +
                "LEFT JOIN TroopTraits ON TroopTraits.Code=Traits.Code AND TroopTraits.CostIndex=0 " +
                "LEFT JOIN Troops ON Troops.Language=Traits.Language AND Troops.ReleaseDate<NOW() AND Troops.Id=TroopTraits.TroopId " +
                "LEFT JOIN Classes ON Classes.Language=Traits.Language AND Classes.ReleaseDate<NOW() AND Classes.Id=TroopTraits.TroopId " +
                "WHERE Traits.Language='en-US' AND ((Traits.Name LIKE ?) OR (Traits.Name SOUNDS LIKE ?))" +
                "GROUP BY Traits.Code " +
                "ORDER BY Traits.Name", traitHandler,
                searchTerm + "%",
                searchTerm
                );
            ResultSetHandler<List<Spell>> spellHandler = new BeanListHandler<>(Spell.class);
            List<Spell> spells = run.query("SELECT Spells.Name, Troops.Name AS Description FROM Spells "
                + "INNER JOIN Troops ON Troops.Language=Spells.Language AND Troops.SpellId=Spells.Id "
                + "WHERE Spells.Language='en-US' AND Troops.ReleaseDate<NOW() AND ((Spells.Name LIKE ?) OR (Spells.Name SOUNDS LIKE ?)) "
                + "ORDER BY Spells.Name", spellHandler,
                searchTerm + "%",
                searchTerm
                );
            ResultSetHandler<List<Kingdom>> kingdomHandler = new BeanListHandler<>(Kingdom.class);
            List<Kingdom> kingdoms = run.query("SELECT Kingdoms.Name FROM Kingdoms "
                + "WHERE Kingdoms.Language='en-US' AND Kingdoms.IsUsed AND ((Kingdoms.Name LIKE ?) OR (Kingdoms.Name SOUNDS LIKE ?)) "
                + "ORDER BY Kingdoms.Name", kingdomHandler,
                searchTerm + "%",
                searchTerm
                );
            ResultSetHandler<List<HeroClass>> heroClassHandler = new BeanListHandler<>(HeroClass.class);
            List<HeroClass> heroClasses = run.query("SELECT Classes.Name FROM Classes "
                + "WHERE Classes.Language='en-US' AND Classes.ReleaseDate<NOW() AND ((Classes.Name LIKE ?) OR (Classes.Name SOUNDS LIKE ?)) "
                + "ORDER BY Classes.Name", heroClassHandler,
                searchTerm + "%",
                searchTerm
                );
            ResultSetHandler<List<Weapon>> weaponHandler = new BeanListHandler<>(Weapon.class);
            List<Weapon> weapons = run.query("SELECT Weapons.Name FROM Weapons "
                + "WHERE Weapons.Language='en-US' AND Weapons.ReleaseDate<NOW() AND ((Weapons.Name LIKE ?) OR (Weapons.Name SOUNDS LIKE ?)) "
                + "ORDER BY Weapons.Name", weaponHandler,
                searchTerm + "%",
                searchTerm
                );
            String[] troopNames = troops.stream().map(t -> "    - " + t.getName()).toArray(String[]::new);
            String[] traitNames = traits.stream().map(t -> "    - " + t.getName() + " (" + t.getDescription() + ")").toArray(String[]::new);
            String[] spellNames = spells.stream().map(t -> "    - " + t.getName() + " (" + t.getDescription() + ")").toArray(String[]::new);
            String[] kingdomNames = kingdoms.stream().map(t -> "    - " + t.getName()).toArray(String[]::new);
            String[] heroClassNames = heroClasses.stream().map(t -> "    - " + t.getName()).toArray(String[]::new);
            String[] weaponNames = weapons.stream().map(t -> "    - " + t.getName()).toArray(String[]::new);
            String searchOutput = "Search results for `" + searchTerm + "`:\n\n";
            if (troopNames.length > 0)
            {
                searchOutput += "**Troops**:\n" + String.join("\n", troopNames) + "\n\n";
            }
            if (weaponNames.length > 0)
            {
                searchOutput += "**Weapons**:\n" + String.join("\n", weaponNames) + "\n\n";
            }
            if (heroClassNames.length > 0)
            {
                searchOutput += "**Hero Classes**:\n" + String.join("\n", heroClassNames) + "\n\n";
            }
            if (kingdomNames.length > 0)
            {
                searchOutput += "**Kingdoms**:\n" + String.join("\n", kingdomNames) + "\n\n";
            }
            if (spellNames.length > 0)
            {
                searchOutput += "**Spells**:\n" + String.join("\n", spellNames) + "\n\n";
            }
            if (traitNames.length > 0)
            {
                searchOutput += "**Traits**:\n" + String.join("\n", traitNames) + "\n\n";
            }

            chnl.sendMessage(searchOutput);
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
        return "Search for troops, traits, spells, hero classes or kingdoms containing the specified text.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?search [text]";
    }

    @Override
    public String getCommand()
    {
        return "search";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
