package me.samboycoding.krystarabot.command;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.HeroClass;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Search;
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
public class AshTraitCommand extends KrystaraCommand {

    public AshTraitCommand() {
        commandName = "trait";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception {
        if (arguments.size() < 1) {
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }

        String traitName = String.join(" ", arguments);
        Search search = AshClient.query("searches/traits?term=" + URLEncoder.encode(traitName, "UTF-8"), Search.class);
        Search.Trait searchTrait = AshClient.getSingleResult(chnl, search.getTraits(), "trait", traitName, Search.Trait.class);
        if (searchTrait == null) {
            return;
        }
        Trait trait = AshClient.query("traits/" + searchTrait.getCode() + "/details", Trait.class);

        String info = "**" + trait.getName() + ":** " + trait.getDescription() + "\n";
        if (!trait.getTroops().isEmpty()) {
            String[] troopNames = trait.getTroops().stream().map(t -> t.getName()).toArray(String[]::new);
            info += "Used by: " + String.join(", ", troopNames) + "\n";
        }
        if (!trait.getHeroClasses().isEmpty()) {
            String[] heroClassNames = trait.getHeroClasses().stream().map(c -> c.getName()).toArray(String[]::new);
            info += "Used by: " + String.join(", ", heroClassNames) + "\n";
        }
        chnl.sendMessage(info);
    }

    @Override
    public String getHelpText() {
        return "Shows information for the specified trait.";
    }

    @Override
    public Boolean requiresAdmin() {
        return false;
    }

    @Override
    public String getUsage() {
        return "?trait [name]";
    }

    @Override
    public String getCommand() {
        return "trait";
    }

    @Override
    public CommandType getCommandType() {
        return GOW;
    }

}
