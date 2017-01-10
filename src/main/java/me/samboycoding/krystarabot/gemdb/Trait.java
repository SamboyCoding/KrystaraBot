package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Trait implements Nameable, java.io.Serializable
{

    private Trait()
    {
    }

    public static Trait fromCode(String code) throws IOException
    {
        return AshClient.query("traits/" + code + "/details", Trait.class);
    }

    public static class Summary implements Nameable
    {

        private String code;
        private String name;

        public String getCode()
        {
            return code;
        }

        public String getName()
        {
            return name;
        }

        public Trait getDetails() throws IOException
        {
            return Trait.fromCode(getCode());
        }
    }

    private static final long serialVersionUID = 1L;

    private String code = null;
    private String name = null;
    private String description = null;
    private ArrayList<Troop.Summary> troops = new ArrayList<>();
    private ArrayList<HeroClass.Summary> classes = new ArrayList<>();

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

    public List<Troop.Summary> getTroops()
    {
        return Collections.unmodifiableList(this.troops);
    }

    public List<HeroClass.Summary> getHeroClasses()
    {
        return Collections.unmodifiableList(this.classes);
    }
}
