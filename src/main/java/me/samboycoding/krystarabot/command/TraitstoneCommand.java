package me.samboycoding.krystarabot.command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.samboycoding.krystarabot.Language;
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
 * Represents the ?traitstone command
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
        Minor(Language.LocString.TRAITSTONE_RARITY_MINOR),
        Major(Language.LocString.TRAITSTONE_RARITY_MAJOR),
        Runic(Language.LocString.TRAITSTONE_RARITY_RUNIC),
        Arcane(Language.LocString.TRAITSTONE_RARITY_ARCANE),
        Celestial(Language.LocString.TRAITSTONE_RARITY_CELESTIAL);
        
        Language.LocString locName;
        
        Rarity(Language.LocString name)
        {
            locName = name;
        }
        
        public String getName(Language lang)
        {
            return lang.localize(locName);
        }
    }

    /**
     * Represents a traitstone type. The order is important; do not modify.
     */
    private static enum Traitstone
    {
        MinorWater(Language.LocString.TRAITSTONE_0, Rarity.Minor, new GemColor[]
        {
            GemColor.Blue
        }),
        MinorNature(Language.LocString.TRAITSTONE_1, Rarity.Minor, new GemColor[]
        {
            GemColor.Green
        }),
        MinorFire(Language.LocString.TRAITSTONE_2, Rarity.Minor, new GemColor[]
        {
            GemColor.Red
        }),
        MinorWind(Language.LocString.TRAITSTONE_3, Rarity.Minor, new GemColor[]
        {
            GemColor.Yellow
        }),
        MinorMagic(Language.LocString.TRAITSTONE_4, Rarity.Minor, new GemColor[]
        {
            GemColor.Purple
        }),
        MinorEarth(Language.LocString.TRAITSTONE_5, Rarity.Minor, new GemColor[]
        {
            GemColor.Brown
        }),
        MajorWater(Language.LocString.TRAITSTONE_6, Rarity.Major, new GemColor[]
        {
            GemColor.Blue
        }),
        MajorNature(Language.LocString.TRAITSTONE_7, Rarity.Major, new GemColor[]
        {
            GemColor.Green
        }),
        MajorFire(Language.LocString.TRAITSTONE_8, Rarity.Major, new GemColor[]
        {
            GemColor.Red
        }),
        MajorWind(Language.LocString.TRAITSTONE_9, Rarity.Major, new GemColor[]
        {
            GemColor.Yellow
        }),
        MajorMagic(Language.LocString.TRAITSTONE_10, Rarity.Major, new GemColor[]
        {
            GemColor.Purple
        }),
        MajorEarth(Language.LocString.TRAITSTONE_11, Rarity.Major, new GemColor[]
        {
            GemColor.Brown
        }),
        RunicWater(Language.LocString.TRAITSTONE_12, Rarity.Runic, new GemColor[]
        {
            GemColor.Blue
        }),
        RunicNature(Language.LocString.TRAITSTONE_13, Rarity.Runic, new GemColor[]
        {
            GemColor.Green
        }),
        RunicFire(Language.LocString.TRAITSTONE_14, Rarity.Runic, new GemColor[]
        {
            GemColor.Red
        }),
        RunicWind(Language.LocString.TRAITSTONE_15, Rarity.Runic, new GemColor[]
        {
            GemColor.Yellow
        }),
        RunicMagic(Language.LocString.TRAITSTONE_16, Rarity.Runic, new GemColor[]
        {
            GemColor.Purple
        }),
        RunicEarth(Language.LocString.TRAITSTONE_17, Rarity.Runic, new GemColor[]
        {
            GemColor.Brown
        }),
        ArcaneStoic(Language.LocString.TRAITSTONE_18, Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue
        }),
        ArcaneSwamp(Language.LocString.TRAITSTONE_19, Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Green
        }),
        ArcaneBlood(Language.LocString.TRAITSTONE_20, Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Red
        }),
        ArcaneBlade(Language.LocString.TRAITSTONE_21, Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Yellow
        }),
        ArcaneSpirit(Language.LocString.TRAITSTONE_22, Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Purple
        }),
        ArcaneShield(Language.LocString.TRAITSTONE_23, Rarity.Arcane, new GemColor[]
        {
            GemColor.Blue, GemColor.Brown
        }),
        ArcaneStealth(Language.LocString.TRAITSTONE_24, Rarity.Arcane, new GemColor[]
        {
            GemColor.Green
        }),
        ArcaneBeast(Language.LocString.TRAITSTONE_25, Rarity.Arcane, new GemColor[]
        {
            GemColor.Green, GemColor.Red
        }),
        ArcaneLight(Language.LocString.TRAITSTONE_26, Rarity.Arcane, new GemColor[]
        {
            GemColor.Green, GemColor.Yellow
        }),
        ArcaneVenom(Language.LocString.TRAITSTONE_27, Rarity.Arcane, new GemColor[]
        {
            GemColor.Green, GemColor.Purple
        }),
        ArcaneForest(Language.LocString.TRAITSTONE_28, Rarity.Arcane, new GemColor[]
        {
            GemColor.Green, GemColor.Brown
        }),
        ArcaneRage(Language.LocString.TRAITSTONE_29, Rarity.Arcane, new GemColor[]
        {
            GemColor.Red
        }),
        ArcaneStorm(Language.LocString.TRAITSTONE_30, Rarity.Arcane, new GemColor[]
        {
            GemColor.Red, GemColor.Yellow
        }),
        ArcaneDark(Language.LocString.TRAITSTONE_31, Rarity.Arcane, new GemColor[]
        {
            GemColor.Red, GemColor.Purple
        }),
        ArcaneLava(Language.LocString.TRAITSTONE_32, Rarity.Arcane, new GemColor[]
        {
            GemColor.Red, GemColor.Brown
        }),
        ArcaneSummer(Language.LocString.TRAITSTONE_33, Rarity.Arcane, new GemColor[]
        {
            GemColor.Yellow
        }),
        ArcanePlains(Language.LocString.TRAITSTONE_34, Rarity.Arcane, new GemColor[]
        {
            GemColor.Yellow, GemColor.Purple
        }),
        ArcaneMountain(Language.LocString.TRAITSTONE_35, Rarity.Arcane, new GemColor[]
        {
            GemColor.Yellow, GemColor.Brown
        }),
        ArcaneDeath(Language.LocString.TRAITSTONE_36, Rarity.Arcane, new GemColor[]
        {
            GemColor.Purple
        }),
        ArcaneSkull(Language.LocString.TRAITSTONE_37, Rarity.Arcane, new GemColor[]
        {
            GemColor.Purple, GemColor.Brown
        }),
        ArcaneDeep(Language.LocString.TRAITSTONE_38, Rarity.Arcane, new GemColor[]
        {
            GemColor.Brown
        }),
        Celestial(Language.LocString.TRAITSTONE_39, Rarity.Celestial, new GemColor[]
        {
            GemColor.Blue, GemColor.Green, GemColor.Red, GemColor.Yellow, GemColor.Purple, GemColor.Brown
        });

        public final Language.LocString locName;
        public final Rarity rarity;
        public final GemColor[] colors;

        private Traitstone(Language.LocString name, Rarity r, GemColor[] c)
        {
            locName = name;
            rarity = r;
            colors = c;
        }
        
        public String getName(Language lang)
        {
            return lang.localize(locName);
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
        if (arguments.size() < 1)
        {
            chnl.sendMessage(lang.localize(Language.LocString.PLEASE_SPECIFY_TERM_OR_COLOR_TO_SEARCH));
            return;
        }

        ArrayList<Traitstone> traitstones = parseArguments(arguments, lang);
        String searchTerm = String.join(" ", arguments);

        if (traitstones.isEmpty())
        {
            chnl.sendMessage(lang.localizeFormat(Language.LocString.NO_THING_FOUND_FORMAT, searchTerm));
            return;
        }

        if (traitstones.size() > 1)
        {
            Stream<String> str = traitstones.stream().map(t -> t.getName(lang));
            Utilities.sendDisambiguationMessage(chnl, lang.localizeFormat(Language.LocString.TERM_IS_AMBIGUOUS_FORMAT, searchTerm), str::iterator, lang);
            return;
        }

        Traitstone ts = traitstones.get(0);
        me.samboycoding.krystarabot.gemdb.Traitstone traitstone
                = AshClient.query("traitstones/" + ts.getId() + "/details",
                        me.samboycoding.krystarabot.gemdb.Traitstone.class, lang);

        String info = getTraitstoneInfoText(traitstone, chnl, lang);

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
    private ArrayList<Traitstone> parseArguments(ArrayList<String> arguments, Language lang)
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
                if (color.getName(lang).toLowerCase().equals(arg))
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
                    if (rarity.getName(lang).toLowerCase().equals(arg))
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
            String name = traitstone.getName(lang).toLowerCase();
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
    private String getTraitstoneInfoText(me.samboycoding.krystarabot.gemdb.Traitstone traitstone, IChannel channel, Language lang) throws SQLException, IOException
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
            result += lang.localize(Language.LocString.FOUND_IN) + " " + String.join(", ", kingdomNames) + "\n";
        }
        if (!troopNames.isEmpty())
        {
            final int LIMIT = 50;
            int size = troopNames.size();

            if (size > LIMIT)
            {
                String extraText = "... (" + lang.localizeFormat(Language.LocString.N_MORE_FORMAT, size - LIMIT) + ")";
                troopNames.subList(LIMIT, size).clear();
                troopNames.add(extraText);
            }
            result += "\n" + lang.localize(Language.LocString.USED_BY_TROOPS) + " " + String.join(", ", troopNames) + "\n";
        }
        if (!heroClassNames.isEmpty())
        {
            result += "\n" + lang.localize(Language.LocString.USED_BY_CLASSES) + " " + String.join(", ", heroClassNames) + "\n";
        }
        result += "\n" + lang.localize(Language.LocString.TOTAL_NEEDED) + " " + totalNeeded + "\n\n";

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
