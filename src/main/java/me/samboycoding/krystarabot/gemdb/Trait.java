package me.samboycoding.krystarabot.gemdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private ArrayList<Troop> troops = new ArrayList<>();
    private ArrayList<HeroClass> classes = new ArrayList<>();

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

    public List<Troop> getTroops()
    {
        return Collections.unmodifiableList(this.troops);
    }

    public List<HeroClass> getHeroClasses()
    {
        return Collections.unmodifiableList(this.classes);
    }
}
