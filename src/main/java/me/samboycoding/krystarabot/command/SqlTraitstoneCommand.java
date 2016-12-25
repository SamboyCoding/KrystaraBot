package me.samboycoding.krystarabot.command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.HeroClass;
import me.samboycoding.krystarabot.gemdb.Kingdom;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?trait command
 *
 * @author Emily Ash
 */
public class SqlTraitstoneCommand extends KrystaraCommand
{
    /**
     * Represents the rarity of a given traitstone.
     */
    private static enum Rarity
    {
        Minor,
        Major,
        Runic,
        Arcane,
        Celestial
    }
        
    /**
     * Represents a traitstone type.
     * The order is important; do not modify.
     */
    private static enum Traitstone
    {
        MinorWater("Minor Water Traitstone",        Rarity.Minor,     new GemColor[] { GemColor.Blue }),
        MinorNature("Minor Nature Traitstone",      Rarity.Minor,     new GemColor[] { GemColor.Green }),
        MinorFire("Minor Fire Traitstone",          Rarity.Minor,     new GemColor[] { GemColor.Red }),
        MinorWind("Minor Wind Traitstone",          Rarity.Minor,     new GemColor[] { GemColor.Yellow }),
        MinorMagic("Minor Magic Traitstone",        Rarity.Minor,     new GemColor[] { GemColor.Purple }),
        MinorEarth("Minor Earth Traitstone",        Rarity.Minor,     new GemColor[] { GemColor.Brown }),
        
        MajorWater("Major Water Traitstone",        Rarity.Major,     new GemColor[] { GemColor.Blue }),
        MajorNature("Major Nature Traitstone",      Rarity.Major,     new GemColor[] { GemColor.Green }),
        MajorFire("Major Fire Traitstone",          Rarity.Major,     new GemColor[] { GemColor.Red }),
        MajorWind("Major Wind Traitstone",          Rarity.Major,     new GemColor[] { GemColor.Yellow }),
        MajorMagic("Major Magic Traitstone",        Rarity.Major,     new GemColor[] { GemColor.Purple }),
        MajorEarth("Major Earth Traitstone",        Rarity.Major,     new GemColor[] { GemColor.Brown }),
        
        RunicWater("Runic Water Traitstone",        Rarity.Runic,     new GemColor[] { GemColor.Blue }),
        RunicNature("Runic Nature Traitstone",      Rarity.Runic,     new GemColor[] { GemColor.Green }),
        RunicFire("Runic Fire Traitstone",          Rarity.Runic,     new GemColor[] { GemColor.Red }),
        RunicWind("Runic Wind Traitstone",          Rarity.Runic,     new GemColor[] { GemColor.Yellow }),
        RunicMagic("Runic Magic Traitstone",        Rarity.Runic,     new GemColor[] { GemColor.Purple }),
        RunicEarth("Runic Earth Traitstone",        Rarity.Runic,     new GemColor[] { GemColor.Brown }),
        
        ArcaneStoic("Arcane Stoic Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Blue }),
        ArcaneSwamp("Arcane Swamp Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Blue, GemColor.Green }),
        ArcaneBlood("Arcane Blood Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Blue, GemColor.Red }),
        ArcaneBlade("Arcane Blade Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Blue, GemColor.Yellow }),
        ArcaneSpirit("Arcane Spirit Traitstone",    Rarity.Arcane,    new GemColor[] { GemColor.Blue, GemColor.Purple }),
        ArcaneShield("Arcane Shield Traitstone",    Rarity.Arcane,    new GemColor[] { GemColor.Blue, GemColor.Brown }),
        ArcaneStealth("Arcane Stealth Traitstone",  Rarity.Arcane,    new GemColor[] { GemColor.Green }),
        ArcaneBeast("Arcane Beast Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Green, GemColor.Red }),
        ArcaneLight("Arcane Light Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Green, GemColor.Yellow }),
        ArcaneVenom("Arcane Venom Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Green, GemColor.Purple }),
        ArcaneForest("Arcane Forest Traitstone",    Rarity.Arcane,    new GemColor[] { GemColor.Green, GemColor.Brown }),
        ArcaneRage("Arcane Rage Traitstone",        Rarity.Arcane,    new GemColor[] { GemColor.Red }),
        ArcaneStorm("Arcane Storm Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Red, GemColor.Yellow }),
        ArcaneDark("Arcane Dark Traitstone",        Rarity.Arcane,    new GemColor[] { GemColor.Red, GemColor.Purple }),
        ArcaneLava("Arcane Lava Traitstone",        Rarity.Arcane,    new GemColor[] { GemColor.Red, GemColor.Brown }),
        ArcaneSummer("Arcane Summer Traitstone",    Rarity.Arcane,    new GemColor[] { GemColor.Yellow }),
        ArcanePlains("Arcane Plains Traitstone",    Rarity.Arcane,    new GemColor[] { GemColor.Yellow, GemColor.Purple }),
        ArcaneMountain("Arcane Mountain Traitstone", Rarity.Arcane,   new GemColor[] { GemColor.Yellow, GemColor.Brown }),
        ArcaneDeath("Arcane Death Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Purple }),
        ArcaneSkull("Arcane Skull Traitstone",      Rarity.Arcane,    new GemColor[] { GemColor.Purple, GemColor.Brown }),
        ArcaneDeep("Arcane Deep Traitstone",        Rarity.Arcane,    new GemColor[] { GemColor.Brown }),
        
        Celestial("Celestial Traitstone",           Rarity.Celestial, new GemColor[] { GemColor.Blue, GemColor.Green, GemColor.Red, GemColor.Yellow, GemColor.Purple, GemColor.Brown });
        
        public final String prettyName;
        public final Rarity rarity;
        public final GemColor[] colors;
        
        private Traitstone(String name, Rarity r, GemColor[] c)
        {
            prettyName = name;
            rarity = r;
            colors = c;
        }
        
        @Override
        public String toString()
        {
            return prettyName;
        }
        
        public int getId()
        {
            return ordinal();
        }
    }
    
    public SqlTraitstoneCommand()
    {
        commandName = "traitstone";
    }
        
    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!GameData.dataLoaded)
        {
            chnl.sendMessage("Sorry, the data hasn't been loaded (yet). Please try again shortly, and if it still doesn't work, contact one of the bot devs.");
            return;
        }
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name or color to search for!");
            return;
        }
        
        ArrayList<Traitstone> traitstones = parseArguments(arguments);
        String searchTerm = String.join(" ", arguments);
        
        if (traitstones.isEmpty())
        {
            chnl.sendMessage("No traitstone `" + searchTerm + "` found.");
            return;
        }

        if (traitstones.size() > 1)
        {
            Stream<String> str = traitstones.stream().map(t -> t.toString());
            Utilities.sendDisambiguationMessage(chnl, "Search term \"" + searchTerm + "\" is ambiguous.", str::iterator);
            return;
        }

        chnl.sendMessage(getTraitstoneInfoText(traitstones.get(0), chnl));
    }

    /**
     * Parses the arguments list for a list of traitstones that match.
     * @param arguments User-supplied arguments.
     * @return The list of traitstones that match the arguments.
     */
    private ArrayList<Traitstone> parseArguments(ArrayList<String> arguments)
    {
        ArrayList<Traitstone> traitstones = new ArrayList<>();
        ArrayList<GemColor> colorArgs = new ArrayList<>();
        ArrayList<String> nameArgs = new ArrayList<>();
        Rarity rarityArg = null;

        for (String arg : arguments)
        {
            boolean found = false;
            arg = arg.toLowerCase();
            
            // If the argument exactly matches a color, track that color
            for (GemColor color : GemColor.values())
            {
                if (color.name().toLowerCase().equals(arg))
                {
                    if (!colorArgs.contains(color))
                    {
                        colorArgs.add(color);
                    }
                    found = true;
                    break;
                }
            }
            
            // If the argument exactly matches a rarity, and we don't already
            // have a rarity, track that rarity
            if (!found && (rarityArg == null))
            {
                for (Rarity rarity : Rarity.values())
                {
                    if (rarity.name().toLowerCase().equals(arg))
                    {
                        rarityArg = rarity;
                        found = true;
                        break;
                    }
                }
            }
            
            // If neither is the case, assume the string is a name match
            if (!found)
            {
                nameArgs.add(arg);
            }
        }
        
        // If no rarity was specified, assume one
        if (rarityArg == null)
        {
            rarityArg = Rarity.Arcane;
        }
        
        // Sort the user's color choices
        colorArgs.sort((c1, c2) -> c1.compareTo(c2));

        // Loop over each traitstone type looking for matches
        for (Traitstone traitstone : Traitstone.values())
        {
            // Match rarity
            if (traitstone.rarity != rarityArg)
            {
                continue;
            }
            
            boolean found = true;

            // Match text in the name
            String name = traitstone.prettyName.toLowerCase();
            for (String arg : nameArgs)
            {
                if (!name.contains(arg))
                {
                    found = false;
                    break;
                }
            }
            
            if (found)
            {
                if (!colorArgs.isEmpty())
                {
                    // There are colors; ensure an exact match
                    ArrayList<GemColor> colors = new ArrayList<>(Arrays.asList(traitstone.colors));
                    if (!colorArgs.equals(colors))
                    {
                        found = false;
                    }
                }
            }
            
            if (found)
            {
                // Passed all criteria, so add the traitstone
                traitstones.add(traitstone);
            }
        }
        return traitstones;
    }
    
    /**
     * Gets the informational text associated with this traitstone.
     * @param traitstone The traitstone to get text for.
     * @param channel The current channel.
     * @return The informational text.
     */
    private String getTraitstoneInfoText(Traitstone traitstone, IChannel channel) throws SQLException, IOException
    {
        int id = traitstone.getId();
        QueryRunner run = GemsQueryRunner.getQueryRunner();

        ResultSetHandler<List<Kingdom>> kingdomHandler = new BeanListHandler<>(Kingdom.class);
        List<Kingdom> kingdoms = run.query("SELECT Kingdoms.Name, Kingdoms.Id "
            + "FROM Kingdoms "
            + "WHERE Kingdoms.Language='en-US' AND Kingdoms.ExploreTraitstoneId=? AND Kingdoms.IsUsed "
            + "ORDER BY Kingdoms.Name", kingdomHandler,
            id
            );
        
        // Look for troops and classes that have this traitstone as a cost, and accumulate costs
        ResultSetHandler<List<Troop>> troopHandler = new BeanListHandler<>(Troop.class);
        List<Troop> troops = run.query("SELECT Troops.Name, Troops.Id, SUM(TroopTraits.TraitstonesRequired) AS TraitstonesRequired "
            + "FROM Troops "
            + "INNER JOIN TroopTraits ON TroopTraits.TroopId=Troops.Id "
            + "WHERE Troops.Language='en-US' AND TroopTraits.TraitstoneId=? AND Troops.ReleaseDate<NOW() "
            + "GROUP BY Troops.Id "
            + "ORDER BY Troops.Name", troopHandler,
            id
            );

        ResultSetHandler<List<HeroClass>> heroClassHandler = new BeanListHandler<>(HeroClass.class);
        List<HeroClass> heroClasses = run.query("SELECT Classes.Name, Classes.Id, SUM(TroopTraits.TraitstonesRequired) AS TraitstonesRequired "
            + "FROM Classes "
            + "INNER JOIN TroopTraits ON TroopTraits.TroopId=Classes.Id "
            + "WHERE Classes.Language='en-US' AND TroopTraits.TraitstoneId=? AND Classes.ReleaseDate<NOW() "
            + "GROUP BY Classes.Id "
            + "ORDER BY Classes.Name", heroClassHandler,
            id
            );

        String[] kingdomNames = kingdoms.stream().map(k -> k.getName()).toArray(String[]::new);
        ArrayList<String> troopNames = troops.stream().map(t -> t.getName() + " (" + t.getTraitstonesRequired() + ")").collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> heroClassNames = heroClasses.stream().map(c -> c.getName() + " (" + c.getTraitstonesRequired() + ")").collect(Collectors.toCollection(ArrayList::new));
        int totalNeeded = troops.stream().mapToInt(t -> t.getTraitstonesRequired()).sum() +
            heroClasses.stream().mapToInt(c -> c.getTraitstonesRequired()).sum();

        String result = "";

        result += "**" + traitstone.prettyName + "** ";

        // Transform color array into array of emoji strings, and then join
        IGuild guild = channel.getGuild();
        String[] colors = Arrays.stream(traitstone.colors).map(c -> guild.getEmojiByName(c.emoji).toString()).toArray(String[]::new);
        result += String.join(" ", colors) + "\n";

        // Add relevant output if found
        if (kingdomNames.length > 0)
        {
            result += "Found in: " + String.join(", ", kingdomNames) + "\n";
        }
        if (!troopNames.isEmpty())
        {
            final int LIMIT = 50;
            int size = troopNames.size();
            
            if (size > LIMIT)
            {
                String extraText = "... (" + (size - LIMIT) + " more)";
                troopNames.subList(LIMIT, size).clear();
                troopNames.add(extraText);
            }
            result += "\nNeeded by troops: " + String.join(", ", troopNames) + "\n";
        }
        if (!heroClassNames.isEmpty())
        {
            result += "\nNeeded by classes: " + String.join(", ", heroClassNames) + "\n";
        }
        result += "\nTotal needed: " + totalNeeded + "\n\n";

        return result;
    }

    private ArrayList<String> getTraitstoneNeededNames(HashMap<JSONObject, Integer> troopNeededMap)
    {
        ArrayList<String> troopNeededNames = new ArrayList<>();
        for (Object oTroop : troopNeededMap.keySet())
        {
            JSONObject troop = (JSONObject)oTroop;
            int amountNeeded = troopNeededMap.get(troop);
            troopNeededNames.add(troop.getString("Name") + " (" + amountNeeded + ")");
        }
        troopNeededNames.sort((s1, s2) -> s1.compareTo(s2));
        return troopNeededNames;
    }

    private int getTraitstoneNeededCount(HashMap<JSONObject, Integer> troopNeededMap)
    {
        int totalAmountNeeded = 0;
        for (int amountNeeded : troopNeededMap.values())
        {
            totalAmountNeeded += amountNeeded;
        }
        return totalAmountNeeded;
    }

    private HashMap<JSONObject, Integer> getTraitstoneNeededMap(JSONArray troops, int id) throws JSONException
    {
        HashMap<JSONObject, Integer> troopNeededMap = new HashMap<>();
        for (Object oTroop : troops)
        {
            JSONObject troop = (JSONObject)oTroop;
            JSONArray traitTable = troop.getJSONObject("TraitTable").getJSONArray("Runes");
            for (Object oTraitCosts : traitTable)
            {
                JSONArray traitCosts = (JSONArray)oTraitCosts;
                for (Object oTraitCost : traitCosts)
                {
                    JSONObject traitCost = (JSONObject)oTraitCost;
                    if (traitCost.getInt("Id") == id)
                    {
                        // Add the trait's costs to the running total
                        int needed = troopNeededMap.getOrDefault(troop, 0);
                        needed += traitCost.getInt("Required");
                        troopNeededMap.put(troop, needed);
                    }
                }
            }
        }
        return troopNeededMap;
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified traitstone.  Search can be for a name (e.g, \"rage\") or color (e.g. \"blue brown\").";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?traitstone [name|color]";
    }

    @Override
    public String getCommand()
    {
        return "traitstone";
    }
    
    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }

}
