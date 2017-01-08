package me.samboycoding.krystarabot.gemdb;

/**
 *
 * @author julians
 */
public class Trait implements Nameable, java.io.Serializable
{

    private static final long serialVersionUID = 1L;

    private String code = null;
    private String language = null;
    private String name = null;
    private String description = null;

    public Trait()
    {
    }

    public void setCode(String value)
    {
        this.code = value;
    }

    public String getCode()
    {
        return this.code;
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
}
