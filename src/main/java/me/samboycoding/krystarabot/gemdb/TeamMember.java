package me.samboycoding.krystarabot.gemdb;

public class TeamMember implements Nameable
{

    private int id = 0;
    private String language = null;
    private String name = null;
    private int colors = 0;
    private int spellId = 0;
    private String spellName = null;
    private String spellDescription = null;
    private String spellBoostRatioText = null;
    private String spellMagicScalingText = null;
    private int spellCost = 0;

    public int getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public int getColors()
    {
        return this.colors;
    }

    public int getSpellId()
    {
        return this.spellId;
    }

    public String getSpellName()
    {
        return this.spellName;
    }

    public String getSpellDescription()
    {
        return this.spellDescription;
    }

    public int getSpellCost()
    {
        return this.spellCost;
    }

    public String getSpellBoostRatioText()
    {
        return this.spellBoostRatioText;
    }

    public String getSpellMagicScalingText()
    {
        return this.spellMagicScalingText;
    }
}
