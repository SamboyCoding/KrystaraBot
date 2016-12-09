package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

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
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!GameData.dataLoaded)
        {
            chnl.sendMessage("Sorry, the data hasn't been loaded (yet). Please try again shortly, and if it still doesn't work, contact one of the bot devs.");
            return;
        }
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

        ArrayList<String> teamTroops = new ArrayList<>();

        Boolean manaRed = false,
                manaBlue = false,
                manaBrown = false,
                manaYellow = false,
                manaGreen = false,
                manaPurple = false;
        
        for (int i = 0; i < 4; i++)
        {
            String troop = things.get(i);

            JSONObject exact = main.data.getTroopByName(troop);
            if (exact != null)
            {
                if (exact.getJSONObject("ManaColors").getBoolean("ColorBlue"))
                {
                    manaBlue = true;
                }
                if (exact.getJSONObject("ManaColors").getBoolean("ColorRed"))
                {
                    manaRed = true;
                }
                if (exact.getJSONObject("ManaColors").getBoolean("ColorBrown"))
                {
                    manaBrown = true;
                }
                if (exact.getJSONObject("ManaColors").getBoolean("ColorYellow"))
                {
                    manaYellow = true;
                }
                if (exact.getJSONObject("ManaColors").getBoolean("ColorGreen"))
                {
                    manaGreen = true;
                }
                if (exact.getJSONObject("ManaColors").getBoolean("ColorPurple"))
                {
                    manaPurple = true;
                }
                teamTroops.add(exact.getString("Name"));
                continue;
            }
            ArrayList<String> results = main.data.searchForTroop(troop);

            if (results.size() > 5)
            {
                chnl.sendMessage("Search term: \"" + troop + "\" is too broad (" + results.size() + " results). Please refine.");
                return;
            }
            if (results.size() > 1)
            {
                chnl.sendMessage("Ambigous troop name \"" + troop + "\". Possible results:\n\n\t\t-" + results.toString().replace("[", "").replace("]", "").replace(", ", "\n\t\t-") + "\n\nPlease refine the search term.");
                return;
            }
            if (results.isEmpty())
            {
                chnl.sendMessage("Unknown troop \"" + troop + "\". Please correct it.");
                return;
            }

            for (String troopN : results)
            {
                JSONObject trp = main.data.getTroopByName(troopN);
                if (trp.getJSONObject("ManaColors").getBoolean("ColorBlue"))
                {
                    manaBlue = true;
                }
                if (trp.getJSONObject("ManaColors").getBoolean("ColorRed"))
                {
                    manaRed = true;
                }
                if (trp.getJSONObject("ManaColors").getBoolean("ColorBrown"))
                {
                    manaBrown = true;
                }
                if (trp.getJSONObject("ManaColors").getBoolean("ColorYellow"))
                {
                    manaYellow = true;
                }
                if (trp.getJSONObject("ManaColors").getBoolean("ColorGreen"))
                {
                    manaGreen = true;
                }
                if (trp.getJSONObject("ManaColors").getBoolean("ColorPurple"))
                {
                    manaPurple = true;
                }
            }
            teamTroops.add(results.get(0));
        }

        String manaColors = "This team uses the following colors: " + (manaBlue ? chnl.getGuild().getEmojiByName("mana_blue").toString() + " " : "");
        manaColors += (manaGreen ? chnl.getGuild().getEmojiByName("mana_green").toString() + " " : "");
        manaColors += (manaRed ? chnl.getGuild().getEmojiByName("mana_red").toString() + " " : "");
        manaColors += (manaYellow ? chnl.getGuild().getEmojiByName("mana_yellow").toString() + " " : "");
        manaColors += (manaPurple ? chnl.getGuild().getEmojiByName("mana_purple").toString() + " " : "");
        manaColors += (manaBrown ? chnl.getGuild().getEmojiByName("mana_brown").toString() : "");
        String teamString;
        String url;
        String bannerString = null;
        if (things.size() == 5)
        {
            String bannerName2 = things.get(4);
            ArrayList<String> banners = main.data.searchForBanner(things.get(4));
            if (banners.size() > 5)
            {
                chnl.sendMessage("Search term: \"" + bannerName2 + "\" is too broad (" + banners.size() + " results). Please refine.");
                return;
            }
            if (banners.size() > 1)
            {
                chnl.sendMessage("Ambigous banner/kingdom name \"" + bannerName2 + "\". Possible results:\n" + banners.toString().replace("[", "").replace("]", "").replace(", ", ",\n") + "\nPlease refine the search term.");
                return;
            }
            if (banners.isEmpty())
            {
                chnl.sendMessage("Unknown banner/kingdom name \"" + bannerName2 + "\". Please correct it.");
                return;
            }

            String banner = banners.get(0);

            int troopId1 = main.data.getTroopByName(teamTroops.get(0)).getInt("Id");
            int troopId2 = main.data.getTroopByName(teamTroops.get(1)).getInt("Id");
            int troopId3 = main.data.getTroopByName(teamTroops.get(2)).getInt("Id");
            int troopId4 = main.data.getTroopByName(teamTroops.get(3)).getInt("Id");
            int bannerId = main.data.getKingdomFromBanner(banner).getInt("Id");
            String bannerDsc = main.data.getKingdomFromBanner(banner).getString("BannerManaDescription");
            String kingdomNme = main.data.getKingdomFromBanner(banner).getString("Name");

            url = "http://ashtender.com/gems/teams/" + troopId1 + "," + troopId2 + "," + troopId3 + "," + troopId4 + "?banner=" + bannerId;
            teamString = sdr.mention() + " created team: \n\n";
            bannerString = "**" + banner + "** (" + kingdomNme + ") - " + bannerDsc + "\n\n";
        } else
        {
            int troopId1 = main.data.getTroopByName(teamTroops.get(0)).getInt("Id");
            int troopId2 = main.data.getTroopByName(teamTroops.get(1)).getInt("Id");
            int troopId3 = main.data.getTroopByName(teamTroops.get(2)).getInt("Id");
            int troopId4 = main.data.getTroopByName(teamTroops.get(3)).getInt("Id");
            url = "http://ashtender.com/gems/teams/" + troopId1 + "," + troopId2 + "," + troopId3 + "," + troopId4;
            teamString = sdr.mention() + " created team: \n\n";
        }

        teamString += "**Troops:**\n\t-" + teamTroops.get(0) + "\n\t-" + teamTroops.get(1) + "\n\t-" + teamTroops.get(2) + "\n\t-" + teamTroops.get(3);
        teamString += "\n\n" + (bannerString != null ? bannerString : "") + manaColors + "\n\n" + url;

        chnl.sendMessage("Team posted in " + chnl.getGuild().getChannelByID(IDReference.TEAMSCHANNEL).mention());
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
