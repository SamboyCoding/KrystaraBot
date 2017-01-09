package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Search;
import me.samboycoding.krystarabot.gemdb.TeamMember;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.gemdb.Weapon;
import me.samboycoding.krystarabot.utilities.IDReference;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Represent the ?team command
 *
 * @author Sam
 */
public class AshTeamCommand extends KrystaraCommand
{

    public AshTeamCommand()
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
            chnl.sendMessage("Usage: `?team [troop1],[troop2],[troop3],[troop4],[banner (optional)],[name (optional)]`");
            return;
        }

        QueryRunner run = GemsQueryRunner.getQueryRunner();

        ArrayList<TeamMember> teamMembers = new ArrayList<>();
        Kingdom kingdom = null;
        Boolean hasWeapon = false;
        String teamName = null;

        for (int i = 0; i < 4; i++)
        {
            String thing = things.get(i).trim();

            // Search for the thing
            Search search = AshClient.query("searches/all?term=" + URLEncoder.encode(thing, "UTF-8"), Search.class);
            ArrayList<Search.SearchResult> searchResults = new ArrayList<>();
            searchResults.addAll(search.getTroops());
            searchResults.addAll(search.getWeapons());
            Search.SearchResult searchTeamMember = AshClient.getSingleResult(chnl, searchResults, "troop or weapon", thing, Search.SearchResult.class);
            if (searchTeamMember == null)
            {
                return;
            }

            Boolean isWeapon = (searchTeamMember instanceof Search.Weapon);
            if (hasWeapon && isWeapon)
            {
                chnl.sendMessage("You cannot have two weapons on one team!");
                return;
            }
            hasWeapon = hasWeapon || isWeapon;
            TeamMember teamMember;
            if (isWeapon)
            {
                teamMember = AshClient.query("weapons/" + searchTeamMember.getId() + "/details", Weapon.class);
            }
            else
            {
                teamMember = AshClient.query("troops/" + searchTeamMember.getId() + "/details", Troop.class);
            }
            teamMembers.add(teamMember);
        }

        if (things.size() > 4)
        {
            String kingdomName = things.get(4).trim();

            // Search for a kingdom by name or banner name
            Search search = AshClient.query("searches/kingdoms?term=" + URLEncoder.encode(kingdomName, "UTF-8"), Search.class);
            Search.Kingdom searchKingdom = AshClient.getSingleResult(chnl, search.getKingdoms(), "kingdom", kingdomName, Search.Kingdom.class);
            if (searchKingdom == null)
            {
                return;
            }
            
            kingdom = AshClient.query("kingdoms/" + searchKingdom.getId() + "/details", Kingdom.class);
            if (!kingdom.getIsFullKingdom())
            {
                chnl.sendMessage("The kingdom \"" + kingdom.getName() + "\" has no banner.");
                return;
            }
        }
        
        if (things.size() > 5)
        {
            teamName = things.get(5).trim();
        }
        else
        {
            String[] teamTroopNames = teamMembers.stream().map(m -> m.getName()).toArray(String[]::new);
            teamName = String.join(", ", teamTroopNames);
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

        EmbedBuilder b = new EmbedBuilder()
            .withDesc(teamString)
            .withTitle(teamName)
            .withUrl(url);
        
        if (kingdom != null)
        {
            b = b.withThumbnail(kingdom.getBannerImageUrl());
        }
        
        EmbedObject o = b.build();
        chnl.sendMessage("", o, false);
        chnl.sendMessage("Also posted in " + chnl.getGuild().getChannelByID(IDReference.TEAMSCHANNEL).mention());
        chnl.getGuild().getChannelByID(IDReference.TEAMSCHANNEL).sendMessage("", o, false);
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
        return "?team [troop1],[troop2],[troop3],[troop4],[banner (optional)],[name (optional)]";
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
