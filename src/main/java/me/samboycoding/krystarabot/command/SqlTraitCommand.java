package me.samboycoding.krystarabot.command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.HeroClass;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Spell;
import me.samboycoding.krystarabot.gemdb.Trait;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?trait command
 *
 * @author Sam
 */
public class SqlTraitCommand extends KrystaraCommand
{

    public SqlTraitCommand()
    {
        commandName = "trait";
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
            String traitName = String.join(" ", arguments);
            Trait trait = GemsQueryRunner.runQueryForSingleResultByName(
                    chnl,
                    "SELECT Traits.* "
                    + "FROM Traits "
                    + "WHERE Traits.Language='en-US' AND Traits.Name LIKE ? "
                    + "ORDER BY Traits.Name",
                    "trait",
                    Trait.class,
                    traitName
            );

            if (trait == null)
            {
                return;
            }

            QueryRunner run = GemsQueryRunner.getQueryRunner();
            ResultSetHandler<List<Troop>> troopHandler = new BeanListHandler<>(Troop.class);
            List<Troop> troops = run.query("SELECT Troops.Name "
                    + "FROM Troops "
                    + "INNER JOIN TroopTraits ON TroopTraits.TroopId=Troops.Id AND TroopTraits.CostIndex=0 "
                    + "INNER JOIN Traits ON Traits.Code=TroopTraits.Code AND Traits.Language=Troops.Language "
                    + "WHERE Troops.Language='en-US' AND Traits.Code=? "
                    + "ORDER BY Troops.Name", troopHandler,
                    trait.getCode()
            );

            ResultSetHandler<List<HeroClass>> heroClassHandler = new BeanListHandler<>(HeroClass.class);
            List<HeroClass> heroClasses = run.query("SELECT Classes.Name "
                    + "FROM Classes "
                    + "INNER JOIN TroopTraits ON TroopTraits.TroopId=Classes.Id AND TroopTraits.CostIndex=0 "
                    + "INNER JOIN Traits ON Traits.Code=TroopTraits.Code AND Traits.Language=Classes.Language "
                    + "WHERE Classes.Language='en-US' AND Traits.Code=? "
                    + "ORDER BY Classes.Name", heroClassHandler,
                    trait.getCode()
            );

            String info = "**" + trait.getName() + ":** " + trait.getDescription() + "\n";
            if (!troops.isEmpty())
            {
                String[] troopNames = troops.stream().map(t -> t.getName()).toArray(String[]::new);
                info += "Used by: " + String.join(", ", troopNames) + "\n";
            }
            if (!heroClasses.isEmpty())
            {
                String[] heroClassNames = heroClasses.stream().map(c -> c.getName()).toArray(String[]::new);
                info += "Used by: " + String.join(", ", heroClassNames) + "\n";
            }
            chnl.sendMessage(info);
        } catch (IOException e)
        {
            chnl.sendMessage("Credentials file could not be found.");
            throw e;
        } catch (SQLException e2)
        {
            chnl.sendMessage("Database query failed.");
            throw e2;
        }

    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified trait.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?trait [name]";
    }

    @Override
    public String getCommand()
    {
        return "trait";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }

}
