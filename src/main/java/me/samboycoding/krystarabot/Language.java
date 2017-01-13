package me.samboycoding.krystarabot;

import java.security.InvalidParameterException;
import java.util.HashMap;
import me.samboycoding.krystarabot.command.TraitstoneCommand;
import me.samboycoding.krystarabot.gemdb.GemColor;

public enum Language
{
    ENGLISH("en", "en-US", null),
    GERMAN("de", "de-DE", getDeLookup()),
    FRENCH("fr", "fr-FR", getFrLookup()),
    ITALIAN("it", "it-IT", getItLookup()),
    SPANISH("es", "es-ES", getEsLookup());
    
    public enum LocString
    {
        ALSO_POSTED_IN_FORMAT("Also posted in %1$s"),
        ASSUMING_THING_FORMAT("Assuming \"%1$s\"..."),
        CATEGORY_BONUSES("Bonuses"),
        CATEGORY_HERO_CLASSES("Hero Classes"),
        CATEGORY_KINGDOMS("Kingdoms"),
        CATEGORY_SPELLS("Spells"),
        CATEGORY_TRAITS("Traits"),
        CATEGORY_TRIBUTE("Tribute"),
        CATEGORY_TROOPS("Troops"),
        CATEGORY_WEAPONS("Weapons"),
        CLASS_WEAPON("Class Weapon"),
        COLOR_BLUE("Blue"),
        COLOR_BROWN("Brown"),
        COLOR_GREEN("Green"),
        COLOR_PURPLE("Purple"),
        COLOR_RED("Red"),
        COLOR_YELLOW("Yellow"),
        EXPLORATION_TRAITSTONE("Exploration Traitstone:"),
        FOUND_IN("Found in:"),
        KINGDOM_HAS_NO_BANNER_FORMAT("The kingdom \"%1$s\" has no banner"),
        KINGDOM_LEVEL_GRANTS_BONUS_FORMAT("Kingdom level %1$d grants +1 %2$s to all troops."),
        MAGIC_SCALING_HALF("half"),
        N_MORE_FORMAT("%1$d more"),
        NO_THING_FOUND_FORMAT("\"%1$s\" not found."),
        ONE_OF("One of:"),
        ONLY_ONE_WEAPON_PER_TEAM("A team may only have one weapon."),
        PLEASE_SPECIFY_TERM_TO_SEARCH("Please specify a name to search for."),
        PLEASE_SPECIFY_TERM_OR_COLOR_TO_SEARCH("Please specify a name or color to search for."),
        SEARCH_RESULTS_FOR_TERM_FORMAT("Search results for \"%1$s\":"),
        TEAM_USES_THESE_COLORS("This team uses the following colors:"),
        TERM_IS_TOO_SHORT_FORMAT("Search term must be at least %1$d characters long."),
        TERM_IS_AMBIGUOUS_FORMAT("Search term \"%1$s\" is ambiguous."),
        TOTAL_NEEDED("Total needed:"),
        TRAITSTONE_0("Minor Water Traitstone"),
        TRAITSTONE_1("Minor Nature Traitstone"),
        TRAITSTONE_2("Minor Fire Traitstone"),
        TRAITSTONE_3("Minor Wind Traitstone"),
        TRAITSTONE_4("Minor Magic Traitstone"),
        TRAITSTONE_5("Minor Earth Traitstone"),
        TRAITSTONE_6("Major Water Traitstone"),
        TRAITSTONE_7("Major Nature Traitstone"),
        TRAITSTONE_8("Major Fire Traitstone"),
        TRAITSTONE_9("Major Wind Traitstone"),
        TRAITSTONE_10("Major Magic Traitstone"),
        TRAITSTONE_11("Major Earth Traitstone"),
        TRAITSTONE_12("Runic Water Traitstone"),
        TRAITSTONE_13("Runic Nature Traitstone"),
        TRAITSTONE_14("Runic Fire Traitstone"),
        TRAITSTONE_15("Runic Wind Traitstone"),
        TRAITSTONE_16("Runic Magic Traitstone"),
        TRAITSTONE_17("Runic Earth Traitstone"),
        TRAITSTONE_18("Arcane Stoic Traitstone"),
        TRAITSTONE_19("Arcane Swamp Traitstone"),
        TRAITSTONE_20("Arcane Blood Traitstone"),
        TRAITSTONE_21("Arcane Blade Traitstone"),
        TRAITSTONE_22("Arcane Spirit Traitstone"),
        TRAITSTONE_23("Arcane Shield Traitstone"),
        TRAITSTONE_24("Arcane Stealth Traitstone"),
        TRAITSTONE_25("Arcane Beast Traitstone"),
        TRAITSTONE_26("Arcane Light Traitstone"),
        TRAITSTONE_27("Arcane Venom Traitstone"),
        TRAITSTONE_28("Arcane Forest Traitstone"),
        TRAITSTONE_29("Arcane Rage Traitstone"),
        TRAITSTONE_30("Arcane Storm Traitstone"),
        TRAITSTONE_31("Arcane Dark Traitstone"),
        TRAITSTONE_32("Arcane Lava Traitstone"),
        TRAITSTONE_33("Arcane Summer Traitstone"),
        TRAITSTONE_34("Arcane Plains Traitstone"),
        TRAITSTONE_35("Arcane Mountain Traitstone"),
        TRAITSTONE_36("Arcane Death Traitstone"),
        TRAITSTONE_37("Arcane Skull Traitstone"),
        TRAITSTONE_38("Arcane Deep Traitstone"),
        TRAITSTONE_39("Celestial Traitstone"),
        TRAITSTONE_RARITY_ARCANE("Arcane"),
        TRAITSTONE_RARITY_CELESTIAL("Celestial"),
        TRAITSTONE_RARITY_MAJOR("Major"),
        TRAITSTONE_RARITY_MINOR("Minor"),
        TRAITSTONE_RARITY_RUNIC("Runic"),
        USING_FOR_TEAM_NAME_INSTEAD("Using for team name instead."),
        USED_BY_CLASSES("Used by classes:"),
        USED_BY_TROOPS("Used by troops:"),
        USED_BY_WEAPONS("Used by weapons:"),
        USER_CREATED_TEAM_FORMAT("%1$s created team:")
        ;
        
        private final String baseVal;
        
        LocString(String baseVal)
        {
            this.baseVal = baseVal;
        }
        
        public String getBaseVal()
        {
            return this.baseVal;
        }
    }
    
    private final String shortCode;
    private final String code;
    private final HashMap<LocString, String> locStringLookup;
    
    Language(String shortCode, String code, HashMap<LocString, String> locStringLookup)
    {
        this.shortCode = shortCode;
        this.code = code;
        this.locStringLookup = locStringLookup;
    }
    
    public String getShortCode()
    {
        return this.shortCode;
    }

    public String getCode()
    {
        return this.code;
    }
    
    public String localize(LocString locStr)
    {
        if (this.locStringLookup != null && this.locStringLookup.containsKey(locStr))
        {
            return this.locStringLookup.get(locStr);
        }
        
        return locStr.getBaseVal();
    }
    
    public String localizeFormat(LocString locStr, Object... args)
    {
        return String.format(localize(locStr), args);
    }
    
    public static Language fromShortCode(String shortCode)
    {
        for (Language lang : values())
        {
            if (lang.getShortCode().equals(shortCode))
            {
                return lang;
            }
        }
        
        throw new InvalidParameterException("Unrecognized language \"" + shortCode + "\".");
    }
    
    private static HashMap<LocString, String> getDeLookup()
    {
        HashMap<LocString, String> res = new HashMap<>();
        res.put(LocString.COLOR_BLUE, "Blau");
        res.put(LocString.COLOR_BROWN, "Braun");
        res.put(LocString.COLOR_GREEN, "Grün");
        res.put(LocString.COLOR_PURPLE, "Lila");
        res.put(LocString.COLOR_RED, "Rot");
        res.put(LocString.COLOR_YELLOW, "Gelb");
        res.put(LocString.FOUND_IN, "Gefunden in:");
        res.put(LocString.TOTAL_NEEDED, "Insgesamt:");
        res.put(LocString.TRAITSTONE_0, "Kleiner Wasserstein");
        res.put(LocString.TRAITSTONE_1, "Kleiner Naturstein");
        res.put(LocString.TRAITSTONE_2, "Kleiner Feuerstein");
        res.put(LocString.TRAITSTONE_3, "Kleiner Windstein");
        res.put(LocString.TRAITSTONE_4, "Kleiner Magiestein");
        res.put(LocString.TRAITSTONE_5, "Kleiner Erdstein");
        res.put(LocString.TRAITSTONE_6, "Großer Wasserstein");
        res.put(LocString.TRAITSTONE_7, "Großer Naturstein");
        res.put(LocString.TRAITSTONE_8, "Großer Feuerstein");
        res.put(LocString.TRAITSTONE_9, "Großer Windstein");
        res.put(LocString.TRAITSTONE_10, "Großer Magiestein");
        res.put(LocString.TRAITSTONE_11, "Großer Erdstein");
        res.put(LocString.TRAITSTONE_12, "Runischer Wasserstein");
        res.put(LocString.TRAITSTONE_13, "Runischer Naturstein");
        res.put(LocString.TRAITSTONE_14, "Runischer Feuerstein");
        res.put(LocString.TRAITSTONE_15, "Runischer Windstein");
        res.put(LocString.TRAITSTONE_16, "Runischer Magiestein");
        res.put(LocString.TRAITSTONE_17, "Runischer Erdstein");
        res.put(LocString.TRAITSTONE_18, "Geheimer Stoischstein");
        res.put(LocString.TRAITSTONE_19, "Geheimer Sumpfstein");
        res.put(LocString.TRAITSTONE_20, "Geheimer Blutstein");
        res.put(LocString.TRAITSTONE_21, "Geheimer Klingenstein");
        res.put(LocString.TRAITSTONE_22, "Geheimer Geisterstein");
        res.put(LocString.TRAITSTONE_23, "Geheimer Schildstein");
        res.put(LocString.TRAITSTONE_24, "Geheimer Heimlichkeitsstein");
        res.put(LocString.TRAITSTONE_25, "Geheimer Bestienstein");
        res.put(LocString.TRAITSTONE_26, "Geheimer Lichtstein");
        res.put(LocString.TRAITSTONE_27, "Geheimer Giftstein");
        res.put(LocString.TRAITSTONE_28, "Geheimer Waldstein");
        res.put(LocString.TRAITSTONE_29, "Geheimer Wutstein");
        res.put(LocString.TRAITSTONE_30, "Geheimer Sturmstein");
        res.put(LocString.TRAITSTONE_31, "Geheimer Dunkelstein");
        res.put(LocString.TRAITSTONE_32, "Geheimer Lavastein");
        res.put(LocString.TRAITSTONE_33, "Geheimer Sommerstein");
        res.put(LocString.TRAITSTONE_34, "Geheimer Ebenenstein");
        res.put(LocString.TRAITSTONE_35, "Geheimer Bergstein");
        res.put(LocString.TRAITSTONE_36, "Geheimer Totenstein");
        res.put(LocString.TRAITSTONE_37, "Geheimer Schädelstein");
        res.put(LocString.TRAITSTONE_38, "Geheimer Tiefenstein");
        res.put(LocString.TRAITSTONE_39, "Himmlischer Stein");
        res.put(LocString.TRAITSTONE_RARITY_ARCANE, "Geheimer");
        res.put(LocString.TRAITSTONE_RARITY_CELESTIAL, "Himmlischer");
        res.put(LocString.TRAITSTONE_RARITY_MAJOR, "Großer");
        res.put(LocString.TRAITSTONE_RARITY_MINOR, "Kleiner");
        res.put(LocString.TRAITSTONE_RARITY_RUNIC, "Runischer");
        res.put(LocString.USED_BY_CLASSES, "Klassen:");
        res.put(LocString.USED_BY_TROOPS, "Truppen:");
        return res;
    }

    private static HashMap<LocString, String> getEsLookup()
    {
        return new HashMap<>();
    }

    private static HashMap<LocString, String> getFrLookup()
    {
        return new HashMap<>();
    }

    private static HashMap<LocString, String> getItLookup()
    {
        return new HashMap<>();
    }
}
