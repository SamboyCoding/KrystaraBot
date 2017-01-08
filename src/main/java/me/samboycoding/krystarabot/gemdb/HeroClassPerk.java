package me.samboycoding.krystarabot.gemdb;

/**
 *
 * @author EmilyAsh
 */
public class HeroClassPerk implements java.io.Serializable
{

    private static final long serialVersionUID = 1L;

    private String language = null;
    private String name = null;
    private String description = null;
    private String perkType = null;

    public HeroClassPerk()
    {
    }

    public void setLanguage(String value)
    {
        this.language = value;
    }

    public String getLanguage()
    {
        return this.language;
    }

    public void setName(String value)
    {
        this.name = value;
    }

    public String getName()
    {
        return this.name;
    }

    public void setDescription(String value)
    {
        this.description = value;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setPerkType(String value)
    {
        this.perkType = value;
    }

    public String getPerkType()
    {
        return this.perkType;
    }
}
