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
        Arrays.asList(argsFull.split(",")).forEach(new Consumer<String>()
        {
            @Override
            public void accept(String t)
            {
                things.add(t.trim());
            }
        });

        if (things.size() < 4)
        {
            chnl.sendMessage("Usage: `?team [troop1],[troop2],[troop3],[troop4],[banner (optional)]`");
            return;
        }

        QueryRunner run = GemsQueryRunner.getQueryRunner();

        ResultSetHandler<List<Troop>> troopHandler = new BeanListHandler<>(Troop.class);
        ResultSetHandler<List<Weapon>> weaponHandler = new BeanListHandler<>(Weapon.class);

        ArrayList<TeamMember> teamMembers = new ArrayList<>();
        Kingdom kingdom = null;
        Boolean hasWeapon = false;

        for (int i = 0; i < 4; i++)
        {
            String thing = things.get(i);

            List<Troop> troops = run.query("SELECT Troops.Id, Troops.Name, Troops.Colors FROM Troops "
                + "WHERE Troops.Language='en-US' AND Troops.ReleaseDate<NOW() AND Troops.Name LIKE ? "
                + "ORDER BY Troops.Name", troopHandler,
                thing + "%"
                );
            List<Weapon> weapons = run.query("SELECT Weapons.Id, Weapons.Name, Weapons.Colors FROM Weapons "
                + "WHERE Weapons.Language='en-US' AND Weapons.ReleaseDate<NOW() AND Weapons.Name LIKE ? "
                + "ORDER BY Weapons.Name", weaponHandler,
                thing + "%"
                );
            ArrayList<TeamMember> candidates = new ArrayList<>();
            candidates.addAll(troops);
            candidates.addAll(weapons);
            
            if (candidates.isEmpty())
            {
                chnl.sendMessage("No troop or weapon `" + thing + "` found.");
                return;
            }

            TeamMember teamMember = null;
            if (!troops.isEmpty() && troops.get(0).getName().toLowerCase().equals(thing.toLowerCase()))
            {
                teamMember = troops.get(0);
            }
            else if (!weapons.isEmpty() && weapons.get(0).getName().toLowerCase().equals(thing.toLowerCase()))
            {
                teamMember = weapons.get(0);
            }
            else if (candidates.size() == 1)
            {
                teamMember = candidates.get(0);
            }
            else
            {
                // Ambiguity
                Stream<String> str = candidates.stream().map(t -> t.getName());
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + thing + "\" is ambiguous.", str::iterator);
                return;
            }
            
            Boolean isWeapon = teamMember instanceof Weapon;
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
            ResultSetHandler<List<Kingdom>> kingdomHandler = new BeanListHandler<>(Kingdom.class);
            List<Kingdom> kingdoms = run.query("SELECT Kingdoms.Id, Kingdoms.Name, Kingdoms.BannerName, Kingdoms.BannerDescription FROM Kingdoms "
                + "WHERE Kingdoms.Language='en-US' AND Kingdoms.IsUsed AND Kingdoms.IsFullKingdom AND "
                    + "((Kingdoms.Name LIKE ?) OR (Kingdoms.BannerName LIKE ?)) "
                + "ORDER BY Kingdoms.Name", kingdomHandler,
                "%" + kingdomName + "%",
                "%" + kingdomName + "%"
                );
            
            if (kingdoms.isEmpty())
            {
                chnl.sendMessage("No kingdom or banner `" + kingdomName + "` found.");
                return;
            }
            else if ((kingdoms.size() > 1) && (!kingdoms.get(0).getName().toLowerCase().equals(kingdomName.toLowerCase())))
            {
                Stream<String> str = kingdoms.stream().map(t -> t.getName());
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + kingdomName + "\" is ambiguous.", str::iterator);
                return;
            }
            kingdom = kingdoms.get(0);
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
        String[] troopNames = teamMembers.stream().map(m -> {
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

}
