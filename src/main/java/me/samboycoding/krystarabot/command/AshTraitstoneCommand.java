package me.samboycoding.krystarabot.command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Represents the ?trait command
 *
 * @author Emily Ash
 */
public class AshTraitstoneCommand extends KrystaraCommand
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
     * Represents a traitstone type. The order is important; do not modify.
     */
    private static enum Traitstone
    {
        MinorWater("Minor Water Traitstone", Rarity.Minor, new GemColor[]
        {
            GemColor.Blue
        }),
        MinorNature("Minor Nature Traitstone", Rarity.Minor, new GemColor[]
        {
            GemColor.Green
        }),
        MinorFire("Minor Fire Traitstone", Rarity.Minor, new GemColor[]
        {
            GemColor.Red
        }),
        MinorWind("Minor Wind Traitstone", Rarity.Minor, new GemColor[]
        {
            GemColor.Yellow
        }),
        MinorMagic("Minor Magic Traitstone", Rarity.Minor, new GemColor[]
        {
            GemColor.Purple
        }),
        MinorEarth("Minor Earth Traitstone", Rarity.Minor, new GemColor[]
        {
            GemColor.Brown
        }),
        MajorWater("Major Water Traitstone", Rarity.Major, new GemColor[]
        {
            GemColor.Blue
        }),
        MajorNature("Major Nature Traitstone", Rarity.Major, new GemColor[]
        {
            GemColor.Green
        }),
        MajorFire("Major Fire Traitstone", Rarity.Major, new GemColor[]
        {
            GemColor.Red
        }),
        MajorWind("Major Wind Traitstone", Rarity.Major, new GemColor[]
        {
            GemColor.Yellow
        }),
        MajorMagic("Major Magic Traitstone", Rarity.Major, new GemColor[]
        {
            GemColor.Purple
        }),
        MajorEarth("Major Earth Traitstone", Rarity.Major, new GemColor[]
        {
            GemColor.Brown
        }),
        RunicWater("Runic Water Traitstone", Rarity.Runic, new GemColor[]
        {
            GemColor.Blue
        }),
        RunicNature("Runic Nature Traitstone", Rarity.Runic, new GemColor[]
        {
            GemColor.Green
        }),
        RunicFire("Runic Fire Traitstone", Rarity.Runic, new GemColor[]
        {
            GemColor.Red
        }),
        RunicWind("Runic Wind Traitstone", Rarity.Runic, new GemColor[]
        {
            GemColor.Yellow
        }),
        RunicMagic("Runic Magic Traitstone", Rarity.Runic, new GemColor[]
        {
            GemColor.Purple
        }),
        RunicEarth("Runic Earth Traitstone", Rarity.Runic, new GemColor[]
        {
            GemColor.Brown
        }),
        ArcaneStoic("Arcane Stoic Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue
        }),
        ArcaneSwamp("Arcane Swamp Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Green
        }),
        ArcaneBlood("Arcane Blood Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Red
        }),
        ArcaneBlade("Arcane Blade Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Yellow
        }),
        ArcaneSpirit("Arcane Spirit Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Purple
        }),
        ArcaneShield("Arcane Shield Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Brown
        }),
        ArcaneStealth("Arcane Stealth Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Green
        }),
        ArcaneBeast("Arcane Beast Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Green, GemColor.Red
        }),
        ArcaneLight("Arcane Light Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Green, GemColor.Yellow
        }),
        ArcaneVenom("Arcane Venom Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Green, GemColor.Purple
        }),
        ArcaneForest("Arcane Forest Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Green, GemColor.Brown
        }),
        ArcaneRage("Arcane Rage Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Red
        }),
        ArcaneStorm("Arcane Storm Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Red, GemColor.Yellow
        }),
        ArcaneDark("Arcane Dark Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Red, GemColor.Purple
        }),
        ArcaneLava("Arcane Lava Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Red, GemColor.Brown
        }),
        ArcaneSummer("Arcane Summer Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Yellow
        }),
        ArcanePlains("Arcane Plains Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Yellow, GemColor.Purple
        }),
        ArcaneMountain("Arcane Mountain Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Yellow, GemColor.Brown
        }),
        ArcaneDeath("Arcane Death Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Purple
        }),
        ArcaneSkull("Arcane Skull Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Purple, GemColor.Brown
        }),
        ArcaneDeep("Arcane Deep Traitstone", Rarity.Arcane, new GemColor[]
        {
            GemColor.Brown
        }),
        Celestial("Celestial Traitstone", Rarity.Celestial, new GemColor[]
        {
            GemColor.Blue, GemColor.Green, GemColor.Red, GemColor.Yellow, GemColor.Purple, GemColor.Brown
        });

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

    public AshTraitstoneCommand()
    {
        commandName = "traitstone";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name or color to search for!");
            return;
        }

        ArrayList<Traitstone> traitstones = parseArguments(arguments);
        String searchTerm = String.join(" ", arguments);

        if (traitstones.isEmpty())
        {
            chnl.sendMessage("No traitstone \"" + searchTerm + "\" found.");
            return;
        }

        if (traitstones.size() > 1)
        {
            Stream<String> str = traitstones.stream().map(t -> t.toString());
            Utilities.sendDisambiguationMessage(chnl, "Search term \"" + searchTerm + "\" is ambiguous.", str::iterator);
            return;
        }

        Traitstone ts = traitstones.get(0);
        me.samboycoding.krystarabot.gemdb.Traitstone traitstone
                = AshClient.query("traitstones/" + ts.getId() + "/details",
                        me.samboycoding.krystarabot.gemdb.Traitstone.class);

        String info = getTraitstoneInfoText(traitstone, chnl);

        EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(traitstone.getName())
                .withThumbnail(traitstone.getImageUrl())
                .build();
        chnl.sendMessage("", o, false);
    }

    /**
     * Parses the arguments list for a list of traitstones that match.
     *
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
     *
     * @param traitstone The traitstone to get text for.
     * @param channel The current channel.
     * @return The informational text.
     */
    private String getTraitstoneInfoText(me.samboycoding.krystarabot.gemdb.Traitstone traitstone, IChannel channel) throws SQLException, IOException
    {
        String[] kingdomNames = traitstone.getKingdoms().stream().map(k -> k.getName()).toArray(String[]::new);
        ArrayList<String> troopNames = traitstone.getTroops().stream().map(t -> t.getName() + " (" + t.getCount() + ")").collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> heroClassNames = traitstone.getHeroClasses().stream().map(c -> c.getName() + " (" + c.getCount() + ")").collect(Collectors.toCollection(ArrayList::new));
        int totalNeeded = traitstone.getTroops().stream().mapToInt(t -> t.getCount()).sum()
                + traitstone.getHeroClasses().stream().mapToInt(c -> c.getCount()).sum();

        String result = "";

        // Transform color array into array of emoji strings, and then join
        IGuild guild = channel.getGuild();
        GemColor[] gemColors = GemColor.fromInteger(traitstone.getColors());
        String[] colors = Arrays.stream(gemColors).map(c -> guild.getEmojiByName(c.emoji).toString()).toArray(String[]::new);
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
