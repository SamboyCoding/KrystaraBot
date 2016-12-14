package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.json.JSONArray;
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
    private static enum Rarity
    {
        Minor,
        Major,
        Runic,
        Arcane,
        Celestial
    }
    
    private static enum Color
    {
        Blue("mana_blue"),
        Green("mana_green"),
        Red("mana_red"),
        Yellow("mana_yellow"),
        Purple("mana_purple"),
        Brown("mana_brown");
        
        public final String emoji;
        
        private Color(String e)
        {
            emoji = e;
        }
    }
    
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
        
        if (traitstones.isEmpty())
        {
            chnl.sendMessage("No traitstone `" + String.join(" ", arguments) + "` found, " + sdr.mention());
            return;
        }

        ArrayList<String> output = new ArrayList<>();
        
        for (Traitstone traitstone : traitstones)
        {
            output.add(getTraitstoneInfoText(traitstone, chnl));
        }
        
        Utilities.sendLargeMessage(chnl, output);
    }

    private ArrayList<Traitstone> parseArguments(ArrayList<String> arguments)
    {
        // Try to find a traitstone by name
        ArrayList<Traitstone> traitstones = new ArrayList<>();
        ArrayList<Color> colorArgs = new ArrayList<>();
        ArrayList<String> nameArgs = new ArrayList<>();
        Rarity rarityArg = null;
        for (String arg : arguments)
        {
            boolean found = false;
            arg = arg.toLowerCase();
            for (Color color : Color.values())
            {
                if (color.name().toLowerCase().equals(arg))
                {
                    colorArgs.add(color);
                    found = true;
                    break;
                }
            }
            
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
            
            if (!found)
            {
                nameArgs.add(arg);
            }
        }
        if (rarityArg == null)
        {
            rarityArg = Rarity.Arcane;
        }
        colorArgs.sort((c1, c2) -> c1.compareTo(c2));
        for (Traitstone traitstone : Traitstone.values())
        {
            if (traitstone.rarity != rarityArg)
            {
                continue;
            }
            
            boolean found = true;

            for (String arg : nameArgs)
            {
                boolean foundThisArg = false;
                if (traitstone.prettyName.toLowerCase().contains(arg))
                {
                    foundThisArg = true;
                }
                
                if (!foundThisArg)
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
                traitstones.add(traitstone);
            }
        }
        return traitstones;
    }
    
    private String getTraitstoneInfoText(Traitstone traitstone, IChannel channel)
    {
        int id = traitstone.getId();
        ArrayList<String> kingdomNames = new ArrayList<>();
                
        for (Object oKingdom : GameData.arrayKingdoms)
        {
            JSONObject kingdom = (JSONObject)oKingdom;
            if (kingdom.has("ExploreRune") && !kingdom.isNull("ExploreRune") && kingdom.getInt("ExploreRune") == id)
            {
                kingdomNames.add(kingdom.getString("Name"));
            }
        }
        
        HashMap<JSONObject, Integer> troopNeededMap = new HashMap<>();
        for (Object oTroop : GameData.arrayTroops)
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
                        int needed = troopNeededMap.getOrDefault(troop, 0);
                        needed += traitCost.getInt("Required");
                        troopNeededMap.put(troop, needed);
                    }
                }
            }
        }
        
        ArrayList<String> troopNeededNames = new ArrayList<>();
        if (!troopNeededMap.isEmpty())
        {
            JSONObject[] oTroops = troopNeededMap.keySet().toArray(new JSONObject[0]);
            ArrayList<JSONObject> troops = new ArrayList<>(Arrays.asList(oTroops));
            troops.sort((t1, t2) -> t1.getString("Name").compareTo(t2.getString("Name")));
            
            for (JSONObject troop : troops)
            {
                troopNeededNames.add(troop.getString("Name") + " (" + troopNeededMap.get(troop) + ")");
            }
        }
        
        String result = "**" + traitstone.prettyName + "**\n";

        IGuild guild = channel.getGuild();
        String[] colors = Arrays.stream(traitstone.colors).map(c -> guild.getEmojiByName(c.emoji).toString()).toArray(String[]::new);
        result += String.join(" ", colors) + "\n";

        if (!kingdomNames.isEmpty())
        {
            result += "Found in: " + String.join(", ", kingdomNames) + "\n";
        }
        if (!troopNeededNames.isEmpty())
        {
            result += "Needed for: " + String.join(", ", troopNeededNames) + "\n";
        }

        return result;
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified traitstone.";
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
