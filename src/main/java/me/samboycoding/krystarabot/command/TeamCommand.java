package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
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

        ArrayList<String> teamEntries = new ArrayList<>();

        Boolean manaRed = false,
                manaBlue = false,
                manaBrown = false,
                manaYellow = false,
                manaGreen = false,
                manaPurple = false;

        for (int i = 0; i < 4; i++)
        {
            String thing = things.get(i);

            JSONObject exact = main.data.getTroopByName(thing);
            if (exact == null)
            {
                exact = main.data.getWeaponByName(thing); //Try to get exact weapon, if no exact troop found.
            }
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
                teamEntries.add(exact.getString("Name"));
                continue;
            }
            ArrayList<String> results = main.data.searchForTroop(thing);
            results.addAll(main.data.searchForWeapon(thing)); //Search for weapons too

            if (results.size() > 5)
            {
                chnl.sendMessage("Search term \"" + thing + "\" is ambiguous (" + results.size() + " results). Please refine your search.");
                return;
            }
            if (results.size() > 1)
            {
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + thing + "\" is ambiguous.", results);
                return;
            }
            if (results.isEmpty())
            {
                chnl.sendMessage("Unknown troop/weapon \"" + thing + "\". Please correct it.");
                return;
            }

            for (String thingN : results)
            {
                JSONObject thng = main.data.getTroopByName(thingN);
                if (thng.getJSONObject("ManaColors").getBoolean("ColorBlue"))
                {
                    manaBlue = true;
                }
                if (thng.getJSONObject("ManaColors").getBoolean("ColorRed"))
                {
                    manaRed = true;
                }
                if (thng.getJSONObject("ManaColors").getBoolean("ColorBrown"))
                {
                    manaBrown = true;
                }
                if (thng.getJSONObject("ManaColors").getBoolean("ColorYellow"))
                {
                    manaYellow = true;
                }
                if (thng.getJSONObject("ManaColors").getBoolean("ColorGreen"))
                {
                    manaGreen = true;
                }
                if (thng.getJSONObject("ManaColors").getBoolean("ColorPurple"))
                {
                    manaPurple = true;
                }
            }
            teamEntries.add(results.get(0));
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
                chnl.sendMessage("Search term \"" + bannerName2 + "\" is ambiguous (" + banners.size() + " results). Please refine your search.");
                return;
            }
            if (banners.size() > 1)
            {
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + bannerName2 + "\" is ambiguous.", banners);
                return;
            }
            if (banners.isEmpty())
            {
                chnl.sendMessage("Unknown banner/kingdom name \"" + bannerName2 + "\". Please correct it.");
                return;
            }

            String banner = banners.get(0);

            Integer troopId1 = null;
            Integer troopId2 = null;
            Integer troopId3 = null;
            Integer troopId4 = null;

            for (String t : teamEntries)
            {
                JSONObject info;
                if (main.data.getTroopByName(t) == null)
                {
                    info = main.data.getWeaponByName(t);
                } else
                {
                    info = main.data.getTroopByName(t);
                }

                if (troopId1 == null)
                {
                    troopId1 = info.getInt("Id");
                } else if (troopId2 == null)
                {
                    troopId2 = info.getInt("Id");
                } else if (troopId3 == null)
                {
                    troopId3 = info.getInt("Id");
                } else if (troopId4 == null)
                {
                    troopId4 = info.getInt("Id");
                }
            }

            int bannerId = main.data.getKingdomFromBanner(banner).getInt("Id");
            String bannerDsc = main.data.getKingdomFromBanner(banner).getString("BannerManaDescription");
            String kingdomNme = main.data.getKingdomFromBanner(banner).getString("Name");

            url = "http://ashtender.com/gems/teams/" + troopId1 + "," + troopId2 + "," + troopId3 + "," + troopId4 + "?banner=" + bannerId;
            teamString = sdr.mention() + " created team: \n\n";
            bannerString = "**" + banner + "** (" + kingdomNme + ") - " + bannerDsc + "\n\n";
        } else
        {
            Integer troopId1 = null;
            Integer troopId2 = null;
            Integer troopId3 = null;
            Integer troopId4 = null;

            for (String t : teamEntries)
            {
                JSONObject info;
                if (main.data.getTroopByName(t) == null)
                {
                    info = main.data.getWeaponByName(t);
                } else
                {
                    info = main.data.getTroopByName(t);
                }

                if (troopId1 == null)
                {
                    troopId1 = info.getInt("Id");
                } else if (troopId2 == null)
                {
                    troopId2 = info.getInt("Id");
                } else if (troopId3 == null)
                {
                    troopId3 = info.getInt("Id");
                } else if (troopId4 == null)
                {
                    troopId4 = info.getInt("Id");
                }
            }

            url = "http://ashtender.com/gems/teams/" + troopId1 + "," + troopId2 + "," + troopId3 + "," + troopId4;
            teamString = sdr.mention() + " created team: \n\n";
        }

        teamString += "**Troops:**";

        boolean weaponProcessed = false;
        for (String t : teamEntries)
        {
            JSONObject info;
            if (main.data.getTroopByName(t) == null)
            {
                info = main.data.getWeaponByName(t);
            } else
            {
                info = main.data.getTroopByName(t);
            }

            String toAdd = "";

            if (info.getBoolean("IsWeapon"))
            {
                if (weaponProcessed)
                {
                    //Two weapons?
                    chnl.sendMessage("You cannot have two weapons on one team!");
                    return;
                }

                toAdd = "Hero (" + info.getString("Name") + ")";
                weaponProcessed = true;
            }

            if (toAdd.equals(""))
            {
                toAdd = info.getString("Name");
            }

            teamString += "\n\t-" + toAdd;
        }
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
