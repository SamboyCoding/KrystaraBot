package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import me.samboycoding.krystarabot.Language;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.SummaryBase;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Search;
import me.samboycoding.krystarabot.gemdb.TeamMember;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.gemdb.Weapon;
import me.samboycoding.krystarabot.utilities.IDReference;
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
public class TeamCommand extends KrystaraCommand
{

    public TeamCommand()
    {
        commandName = "team";
    }

    @Override
    public Boolean isLocalized()
    {
        return true;
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        handleCommand(sdr, chnl, msg, arguments, argsFull, Language.ENGLISH);
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull, Language lang) throws Exception
    {
        ArrayList<String> things = new ArrayList<>();
        Arrays.asList(argsFull.split(",")).forEach(new ConsumerImpl<>(things));

        if (things.size() < 4)
        {
            chnl.sendMessage("Usage: `?team [troop1],[troop2],[troop3],[troop4],[banner (optional)],[name (optional)]`");
            return;
        }

        chnl.setTypingStatus(true);

        ArrayList<TeamMember> teamMembers = new ArrayList<>();
        Kingdom kingdom = null;
        Boolean hasWeapon = false;
        String teamName = null;

        for (int i = 0; i < 4; i++)
        {
            String thing = things.get(i).trim();

            // Search for the thing
            Search search = Search.fromQuery("all?term=" + URLEncoder.encode(thing, "UTF-8"), lang);
            ArrayList<SummaryBase> searchResults = new ArrayList<>();
            searchResults.addAll(search.getTroops());
            searchResults.addAll(search.getWeapons());
            SummaryBase teamMemberSummary = AshClient.getSingleResult(chnl, searchResults, thing, lang);
            if (teamMemberSummary == null)
            {
                return;
            }

            Boolean isWeapon = (teamMemberSummary instanceof Weapon.Summary);
            if (hasWeapon && isWeapon)
            {
                chnl.sendMessage(lang.localize(Language.LocString.ONLY_ONE_WEAPON_PER_TEAM));
                return;
            }
            hasWeapon = hasWeapon || isWeapon;
            TeamMember teamMember;
            if (isWeapon)
            {
                teamMember = ((Weapon.Summary) teamMemberSummary).getDetails(lang);
            } else
            {
                teamMember = ((Troop.Summary) teamMemberSummary).getDetails(lang);
            }
            teamMembers.add(teamMember);
        }

        if (things.size() > 4)
        {
            String kingdomName = things.get(4).trim();

            // Search for a kingdom by name or banner name
            Search search = Search.fromQuery("kingdoms?term=" + URLEncoder.encode(kingdomName, "UTF-8"), lang);
            Kingdom.Summary kingdomSummary = AshClient.getSingleResult(chnl, search.getKingdoms(), kingdomName, lang);
            if (kingdomSummary == null)
            {
                chnl.sendMessage(lang.localize(Language.LocString.USING_FOR_TEAM_NAME_INSTEAD));
                teamName = kingdomName;
            } else
            {
                kingdom = kingdomSummary.getDetails(lang);
                if (!kingdom.isFullKingdom())
                {
                    chnl.sendMessage(lang.localizeFormat(Language.LocString.KINGDOM_HAS_NO_BANNER_FORMAT, kingdom.getName()));
                    return;
                }
            }
        }

        if (teamName == null)
        {
            if (things.size() > 5)
            {
                teamName = things.get(5).trim();
            } else
            {
                String[] teamTroopNames = teamMembers.stream().map(m -> m.getName()).toArray(String[]::new);
                teamName = String.join(", ", teamTroopNames);
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
        String manaColors = lang.localize(Language.LocString.TEAM_USES_THESE_COLORS) + " " + String.join(" ", gemColorEmojis);

        String bannerString = "";
        String url = "http://ashtender.com/gems/teams/" + String.join(",", troopIds);

        if (kingdom != null)
        {
            bannerString = "**" + kingdom.getBannerName() + "** (" + kingdom.getName() + ") - " + kingdom.getBannerDescription() + "\n\n";
            url += "?banner=" + kingdom.getId();
        }

        String teamString = lang.localizeFormat(Language.LocString.USER_CREATED_TEAM_FORMAT, sdr.mention()) + "\n\n";
        teamString += "**" + lang.localize(Language.LocString.CATEGORY_TROOPS) + "**:\n";
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
        IChannel teamChannel = chnl.getGuild().getChannelByID(IDReference.TEAMSCHANNEL);
        if (chnl != teamChannel)
        {
            chnl.sendMessage(lang.localizeFormat(Language.LocString.ALSO_POSTED_IN_FORMAT, teamChannel.mention()));
            teamChannel.sendMessage("", o, false);
        }

        chnl.setTypingStatus(false);
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
