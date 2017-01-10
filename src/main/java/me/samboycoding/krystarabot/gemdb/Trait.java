package me.samboycoding.krystarabot.gemdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Trait implements Nameable, java.io.Serializable
{
    public static class Troop extends IdNamePair
    {
    }
    
    public static class HeroClass extends IdNamePair
    {
    }

    private static final long serialVersionUID = 1L;

    private String code = null;
    private String name = null;
    private String description = null;
    private ArrayList<Troop> troops = new ArrayList<>();
    private ArrayList<HeroClass> classes = new ArrayList<>();

    public String getCode()
    {
        return this.code;
    }

    public String getName()
    {
        return this.name;
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
