package me.samboycoding.krystarabot;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Properties;
import me.samboycoding.krystarabot.command.TraitstoneCommand;
import me.samboycoding.krystarabot.gemdb.GemColor;

public enum Language
{
    ENGLISH("en", "en-US", ""),
    GERMAN("de", "de-DE", "_de"),
    FRENCH("fr", "fr-FR", "_fr"),
    ITALIAN("it", "it-IT", "_it"),
    SPANISH("es", "es-ES", "_es");
    
    public enum LocString
    {
        ALSO_POSTED_IN_FORMAT,
        ASSUMING_THING_FORMAT,
        CATEGORY_BONUSES,
        CATEGORY_HERO_CLASSES,
        CATEGORY_KINGDOMS,
        CATEGORY_SPELLS,
        CATEGORY_TRAITS,
        CATEGORY_TRIBUTE,
        CATEGORY_TROOPS,
        CATEGORY_WEAPONS,
        CLASS_WEAPON,
        COLOR_BLUE,
        COLOR_BROWN,
        COLOR_GREEN,
        COLOR_PURPLE,
        COLOR_RED,
        COLOR_YELLOW,
        EXPLORATION_TRAITSTONE,
        FOUND_IN,
        KINGDOM_HAS_NO_BANNER_FORMAT,
        KINGDOM_LEVEL_GRANTS_BONUS_FORMAT,
        MAGIC_SCALING_HALF,
        N_MORE_FORMAT,
        NO_THING_FOUND_FORMAT,
        ONE_OF,
        ONLY_ONE_WEAPON_PER_TEAM,
        PLEASE_SPECIFY_TERM_TO_SEARCH,
        PLEASE_SPECIFY_TERM_OR_COLOR_TO_SEARCH,
        SEARCH_RESULTS_FOR_TERM_FORMAT,
        TEAM_USES_THESE_COLORS,
        TERM_IS_TOO_SHORT_FORMAT,
        TERM_IS_AMBIGUOUS_FORMAT,
        TOTAL_NEEDED,
        TRAITSTONE_0,
        TRAITSTONE_1,
        TRAITSTONE_2,
        TRAITSTONE_3,
        TRAITSTONE_4,
        TRAITSTONE_5,
        TRAITSTONE_6,
        TRAITSTONE_7,
        TRAITSTONE_8,
        TRAITSTONE_9,
        TRAITSTONE_10,
        TRAITSTONE_11,
        TRAITSTONE_12,
        TRAITSTONE_13,
        TRAITSTONE_14,
        TRAITSTONE_15,
        TRAITSTONE_16,
        TRAITSTONE_17,
        TRAITSTONE_18,
        TRAITSTONE_19,
        TRAITSTONE_20,
        TRAITSTONE_21,
        TRAITSTONE_22,
        TRAITSTONE_23,
        TRAITSTONE_24,
        TRAITSTONE_25,
        TRAITSTONE_26,
        TRAITSTONE_27,
        TRAITSTONE_28,
        TRAITSTONE_29,
        TRAITSTONE_30,
        TRAITSTONE_31,
        TRAITSTONE_32,
        TRAITSTONE_33,
        TRAITSTONE_34,
        TRAITSTONE_35,
        TRAITSTONE_36,
        TRAITSTONE_37,
        TRAITSTONE_38,
        TRAITSTONE_39,
        TRAITSTONE_RARITY_ARCANE,
        TRAITSTONE_RARITY_CELESTIAL,
        TRAITSTONE_RARITY_MAJOR,
        TRAITSTONE_RARITY_MINOR,
        TRAITSTONE_RARITY_RUNIC,
        USING_FOR_TEAM_NAME_INSTEAD,
        USED_BY_CLASSES,
        USED_BY_TROOPS,
        USED_BY_WEAPONS,
        USER_CREATED_TEAM_FORMAT
        ;
    }
    
    private final String shortCode;
    private final String code;
    private final String propFileName;
    private Properties props;
    
    Language(String shortCode, String code, String propFileName)
    {
        this.shortCode = shortCode;
        this.code = code;
        this.propFileName = propFileName;
    }
    
    private java.util.Properties getProps() throws IOException
    {
        Properties props = new Properties();
        InputStream input = Language.class.getClassLoader().getResourceAsStream("Messages" + this.propFileName + ".properties");
        props.load(input);
        return props;
    }
    
    public String getShortCode()
    {
        return this.shortCode;
    }

    public String getCode()
    {
        return this.code;
    }
    
    public String localize(LocString locStr) throws IOException
    {
        if (props == null)
        {
            props = getProps();
        }
        
        return (String)props.get(locStr.name());
    }
    
    public String localizeFormat(LocString locStr, Object... args) throws IOException
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
}
