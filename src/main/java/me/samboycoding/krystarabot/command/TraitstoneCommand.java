package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.utilities.Utilities;
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
public class TraitstoneCommand extends KrystaraCommand
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
     * Represents the color of a given traitstone.
     */
    private static enum Color
    {
        Blue("mana_blue"),
        Green("mana_green"),
        Red("mana_red"),
        Yellow("mana_yellow"),
        Purple("mana_purple"),
        Brown("mana_brown");
        
        /**
         * Gets the emoji associated with the traitstone color.
         */
        public final String emoji;
        
        private Color(String e)
        {
            emoji = e;
        }
    }
    
    /**
     * Represents a traitstone type.
     * The order is important; do not modify.
     */
    private static enum Traitstone
    {
        MinorWater("Minor Water Traitstone",        Rarity.Minor,     new Color[] { Color.Blue }),
        MinorNature("Minor Nature Traitstone",      Rarity.Minor,     new Color[] { Color.Green }),
        MinorFire("Minor Fire Traitstone",          Rarity.Minor,     new Color[] { Color.Red }),
        MinorWind("Minor Wind Traitstone",          Rarity.Minor,     new Color[] { Color.Yellow }),
        MinorMagic("Minor Magic Traitstone",        Rarity.Minor,     new Color[] { Color.Purple }),
        MinorEarth("Minor Earth Traitstone",        Rarity.Minor,     new Color[] { Color.Brown }),
        
        MajorWater("Major Water Traitstone",        Rarity.Major,     new Color[] { Color.Blue }),
        MajorNature("Major Nature Traitstone",      Rarity.Major,     new Color[] { Color.Green }),
        MajorFire("Major Fire Traitstone",          Rarity.Major,     new Color[] { Color.Red }),
        MajorWind("Major Wind Traitstone",          Rarity.Major,     new Color[] { Color.Yellow }),
        MajorMagic("Major Magic Traitstone",        Rarity.Major,     new Color[] { Color.Purple }),
        MajorEarth("Major Earth Traitstone",        Rarity.Major,     new Color[] { Color.Brown }),
        
        RunicWater("Runic Water Traitstone",        Rarity.Runic,     new Color[] { Color.Blue }),
        RunicNature("Runic Nature Traitstone",      Rarity.Runic,     new Color[] { Color.Green }),
        RunicFire("Runic Fire Traitstone",          Rarity.Runic,     new Color[] { Color.Red }),
        RunicWind("Runic Wind Traitstone",          Rarity.Runic,     new Color[] { Color.Yellow }),
        RunicMagic("Runic Magic Traitstone",        Rarity.Runic,     new Color[] { Color.Purple }),
        RunicEarth("Runic Earth Traitstone",        Rarity.Runic,     new Color[] { Color.Brown }),
        
        ArcaneStoic("Arcane Stoic Traitstone",      Rarity.Arcane,    new Color[] { Color.Blue }),
        ArcaneSwamp("Arcane Swamp Traitstone",      Rarity.Arcane,    new Color[] { Color.Blue, Color.Green }),
        ArcaneBlood("Arcane Blood Traitstone",      Rarity.Arcane,    new Color[] { Color.Blue, Color.Red }),
        ArcaneBlade("Arcane Blade Traitstone",      Rarity.Arcane,    new Color[] { Color.Blue, Color.Yellow }),
        ArcaneSpirit("Arcane Spirit Traitstone",    Rarity.Arcane,    new Color[] { Color.Blue, Color.Purple }),
        ArcaneShield("Arcane Shield Traitstone",    Rarity.Arcane,    new Color[] { Color.Blue, Color.Brown }),
        ArcaneStealth("Arcane Stealth Traitstone",  Rarity.Arcane,    new Color[] { Color.Green }),
        ArcaneBeast("Arcane Beast Traitstone",      Rarity.Arcane,    new Color[] { Color.Green, Color.Red }),
        ArcaneLight("Arcane Light Traitstone",      Rarity.Arcane,    new Color[] { Color.Green, Color.Yellow }),
        ArcaneVenom("Arcane Venom Traitstone",      Rarity.Arcane,    new Color[] { Color.Green, Color.Purple }),
        ArcaneForest("Arcane Forest Traitstone",    Rarity.Arcane,    new Color[] { Color.Green, Color.Brown }),
        ArcaneRage("Arcane Rage Traitstone",        Rarity.Arcane,    new Color[] { Color.Red }),
        ArcaneStorm("Arcane Storm Traitstone",      Rarity.Arcane,    new Color[] { Color.Red, Color.Yellow }),
        ArcaneDark("Arcane Dark Traitstone",        Rarity.Arcane,    new Color[] { Color.Red, Color.Purple }),
        ArcaneLava("Arcane Lava Traitstone",        Rarity.Arcane,    new Color[] { Color.Red, Color.Brown }),
        ArcaneSummer("Arcane Summer Traitstone",    Rarity.Arcane,    new Color[] { Color.Yellow }),
        ArcanePlains("Arcane Plains Traitstone",    Rarity.Arcane,    new Color[] { Color.Yellow, Color.Purple }),
        ArcaneMountain("Arcane Mountain Traitstone", Rarity.Arcane,   new Color[] { Color.Yellow, Color.Brown }),
        ArcaneDeath("Arcane Death Traitstone",      Rarity.Arcane,    new Color[] { Color.Purple }),
        ArcaneSkull("Arcane Skull Traitstone",      Rarity.Arcane,    new Color[] { Color.Purple, Color.Brown }),
        ArcaneDeep("Arcane Deep Traitstone",        Rarity.Arcane,    new Color[] { Color.Brown }),
        
        Celestial("Celestial Traitstone",           Rarity.Celestial, new Color[] { Color.Blue, Color.Green, Color.Red, Color.Yellow, Color.Purple, Color.Brown });
        
        public final String prettyName;
        public final Rarity rarity;
        public final Color[] colors;
        
        private Traitstone(String name, Rarity r, Color[] c)
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
    
    public TraitstoneCommand()
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
            chnl.sendMessage("No traitstone `" + searchTerm + "` found, " + sdr.mention());
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
        ArrayList<Color> colorArgs = new ArrayList<>();
        ArrayList<String> nameArgs = new ArrayList<>();
        Rarity rarityArg = null;

        for (String arg : arguments)
        {
            boolean found = false;
            arg = arg.toLowerCase();
            
            // If the argument exactly matches a color, track that color
            for (Color color : Color.values())
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
                    ArrayList<Color> colors = new ArrayList<>(Arrays.asList(traitstone.colors));
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
    private String getTraitstoneInfoText(Traitstone traitstone, IChannel channel)
    {
        int id = traitstone.getId();
        ArrayList<String> kingdomNames = new ArrayList<>();
        
        // Look for kingdoms that have this traitstone set as their ExploreRune
        for (Object oKingdom : GameData.arrayKingdoms)
        {
            JSONObject kingdom = (JSONObject)oKingdom;
            if (kingdom.has("ExploreRune") && !kingdom.isNull("ExploreRune") && kingdom.getInt("ExploreRune") == id)
            {
                // Found one, add the name to the list
                kingdomNames.add(kingdom.getString("Name"));
            }
        }
        
        // Look for troops and classes that have this traitstone as a cost, and accumulate costs
        HashMap<JSONObject, Integer> troopNeededMap = getTraitstoneNeededMap(GameData.arrayTroops, id);
        int totalTroopAmountNeeded = getTraitstoneNeededCount(troopNeededMap);
        ArrayList<String> troopNeededNames = getTraitstoneNeededNames(troopNeededMap);

        HashMap<JSONObject, Integer> classNeededMap = getTraitstoneNeededMap(GameData.arrayClasses, id);
        int totalClassAmountNeeded = getTraitstoneNeededCount(classNeededMap);
        ArrayList<String> classNeededNames = getTraitstoneNeededNames(classNeededMap);
        
        String result = "";

        result += "**" + traitstone.prettyName + "** ";

        // Transform color array into array of emoji strings, and then join
        IGuild guild = channel.getGuild();
        String[] colors = Arrays.stream(traitstone.colors).map(c -> guild.getEmojiByName(c.emoji).toString()).toArray(String[]::new);
        result += String.join(" ", colors) + "\n";

        
        // Add relevant output if found
        if (!kingdomNames.isEmpty())
        {
            result += "Found in: " + String.join(", ", kingdomNames) + "\n";
        }
        if (!troopNeededNames.isEmpty())
        {
            final int LIMIT = 50;
            int size = troopNeededNames.size();
            
            if (size > LIMIT)
            {
                String extraText = "... (" + (size - LIMIT) + " more)";
                troopNeededNames.subList(LIMIT, size).clear();
                troopNeededNames.add(extraText);
            }
            result += "\nNeeded by troops: " + String.join(", ", troopNeededNames) + "\n";
        }
        if (!classNeededNames.isEmpty())
        {
            result += "\nNeeded by classes: " + String.join(", ", classNeededNames) + "\n";
        }
        result += "\nTotal needed: " + (totalTroopAmountNeeded + totalClassAmountNeeded) + "\n\n";

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
