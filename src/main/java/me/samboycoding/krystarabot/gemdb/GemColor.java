
package me.samboycoding.krystarabot.gemdb;

import java.util.ArrayList;


/**
 * Represents the color of a given traitstone.
 */
public enum GemColor
{
    Blue("mana_blue", 0x400),
    Green("mana_green", 0x100),
    Red("mana_red", 0x040),
    Yellow("mana_yellow", 0x010),
    Purple("mana_purple", 0x004),
    Brown("mana_brown", 0x001);

    /**
     * Gets the emoji associated with the traitstone color.
     */
    public final String emoji;
    public final int flag;

    private GemColor(String e, int f)
    {
        emoji = e;
        flag = f;
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
