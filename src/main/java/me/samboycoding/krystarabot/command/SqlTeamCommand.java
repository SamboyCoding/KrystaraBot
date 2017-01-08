package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.TeamMember;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.gemdb.Weapon;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
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
 * Represent the ?team command
 *
 * @author Sam
 */
public class SqlTeamCommand extends KrystaraCommand
{

    public SqlTeamCommand()
    {
        commandName = "team";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        ArrayList<String> things = new ArrayList<>();
        Arrays.asList(argsFull.split(",")).forEach(new ConsumerImpl<>(things));

        if (things.size() < 4)
        {
            chnl.sendMessage("Usage: `?team [troop1],[troop2],[troop3],[troop4],[banner (optional)]`");
            return;
        }

        QueryRunner run = GemsQueryRunner.getQueryRunner();

        ArrayList<TeamMember> teamMembers = new ArrayList<>();
        Kingdom kingdom = null;
        Boolean hasWeapon = false;

        for (int i = 0; i < 4; i++)
        {
            String thing = things.get(i);

            TeamMember teamMember = GemsQueryRunner.runQueryForSingleResultByName(
                    chnl,
                    "SELECT TeamMembers.Id, TeamMembers.Name, TeamMembers.Colors, TeamMembers.Kind FROM ( "
                    + "    SELECT Id, Name, Colors, ReleaseDate, Language, 'Troop' AS Kind FROM Troops "
                    + "    UNION ALL "
                    + "    SELECT Id, Name, Colors, ReleaseDate, Language, 'Weapon' AS Kind FROM Weapons "
                    + ") TeamMembers "
                    + "WHERE TeamMembers.Language='en-US' AND TeamMembers.ReleaseDate<NOW() AND TeamMembers.Name LIKE ? "
                    + "ORDER BY TeamMembers.Name",
                    "troop or weapon",
                    TeamMember.class,
                    thing
            );

            if (teamMember == null)
            {
                return;
            }

            Boolean isWeapon = teamMember.getKind().equals("Weapon");
            if (hasWeapon && isWeapon)
            {
                chnl.sendMessage("You cannot have two weapons on one team!");
                return;
            }
            hasWeapon = hasWeapon || isWeapon;
            teamMembers.add(teamMember);
        }

        if (things.size() > 4)
        {
            String kingdomName = things.get(4);

            // Search for a kingdom by name or banner name
            kingdom = GemsQueryRunner.runQueryForSingleResultByName(
                    chnl,
                    "SELECT Kingdoms.Id, Kingdoms.Name, Kingdoms.BannerName, Kingdoms.BannerDescription FROM Kingdoms "
                    + "WHERE Kingdoms.Language='en-US' AND Kingdoms.IsUsed AND Kingdoms.IsFullKingdom AND "
                    + "((Kingdoms.Name LIKE ?) OR (Kingdoms.BannerName LIKE ?)) "
                    + "ORDER BY Kingdoms.Name",
                    "kingdom or banner",
                    Kingdom.class,
                    kingdomName
            );

            if (kingdom == null)
            {
                return;
            }
        }

        int colors = 0;
        for (TeamMember teamMember : teamMembers)
        {
            colors |= teamMember.getColors();
        }

        IGuild g = chnl.getGuild();
        GemColor[] gemColors = GemColor.fromInteger(colors);
        String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);
        String[] troopIds = teamMembers.stream().map(m -> Integer.toString(m.getId())).toArray(String[]::new);
        String[] troopNames = teamMembers.stream().map(m ->
        {
            GemColor[] gemColorsThis = GemColor.fromInteger(m.getColors());
            String[] gemColorEmojisThis = Arrays.stream(gemColorsThis).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);
            return "    - " + String.join(" ", gemColorEmojisThis) + " " + m.getName();
        }).toArray(String[]::new);
        String manaColors = "This team uses the following colors: " + String.join(" ", gemColorEmojis);

        String bannerString = "";
        String url = "http://ashtender.com/gems/teams/" + String.join(",", troopIds);

        if (kingdom != null)
        {
            bannerString = "**" + kingdom.getBannerName() + "** (" + kingdom.getName() + ") - " + kingdom.getBannerDescription() + "\n\n";
            url += "?banner=" + kingdom.getId();
        }

        String teamString = sdr.mention() + " created team: \n\n";
        teamString += "**Troops:**\n";
        teamString += String.join("\n", troopNames);
        teamString += "\n\n" + bannerString + manaColors + "\n\n" + url;

        chnl.sendMessage(teamString);
        chnl.sendMessage("Also posted in " + chnl.getGuild().getChannelByID(IDReference.TEAMSCHANNEL).mention());
        chnl.getGuild().getChannelByID(IDReference.TEAMSCHANNEL).sendMessage(teamString);
    }

    @Override
    public String getHelpText()
    {
        return "Creates a team and posts it in #share-your-team. The arguments can only be part of a troop (for example \"valk\" for Valkyrie, or \"Dry\" for Dryad).";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?team [troop1],[troop2],[troop3],[troop4],[banner (optional)]";
    }

    @Override
    public String getCommand()
    {
        return "team";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }

    /**
     * Enhanced consumer
     *
     * @author Sam
     */
    private static class ConsumerImpl<T> implements Consumer<T>
    {

        private final ArrayList<T> things;

        public ConsumerImpl(ArrayList<T> things)
        {
            this.things = things;
        }

        @Override
        public void accept(T t)
        {
            things.add(t);
        }
    }

}
