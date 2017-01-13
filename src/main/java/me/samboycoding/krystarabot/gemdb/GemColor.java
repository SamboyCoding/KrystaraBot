package me.samboycoding.krystarabot.gemdb;

import java.util.ArrayList;
import me.samboycoding.krystarabot.Language;

/**
 * Represents the color of a given traitstone.
 */
public enum GemColor
{
    Blue(Language.LocString.COLOR_BLUE, "mana_blue", 0x400),
    Green(Language.LocString.COLOR_GREEN, "mana_green", 0x100),
    Red(Language.LocString.COLOR_RED, "mana_red", 0x040),
    Yellow(Language.LocString.COLOR_YELLOW, "mana_yellow", 0x010),
    Purple(Language.LocString.COLOR_PURPLE, "mana_purple", 0x004),
    Brown(Language.LocString.COLOR_BROWN, "mana_brown", 0x001);

    /**
     * Gets the emoji associated with the traitstone color.
     */
    public final Language.LocString locName;
    public final String emoji;
    public final int flag;

    private GemColor(Language.LocString name, String e, int f)
    {
        locName = name;
        emoji = e;
        flag = f;
    }
    
    public String getName(Language lang)
    {
        return lang.localize(locName);
    }

    public static GemColor[] fromInteger(int i)
    {
        ArrayList<GemColor> colors = new ArrayList<>();
        for (GemColor c : values())
        {
            if ((c.flag & i) > 0)
            {
                colors.add(c);
            }
        }
        return colors.toArray(new GemColor[0]);
    }

    public static int toInteger(Iterable<GemColor> colors)
    {
        int i = 0;
        for (GemColor c : colors)
        {
            i |= c.flag;
        }
        return i;
    }
}
