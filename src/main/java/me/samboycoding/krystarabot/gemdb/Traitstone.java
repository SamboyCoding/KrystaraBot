package me.samboycoding.krystarabot.gemdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author julians
 */
public class Traitstone implements Nameable, java.io.Serializable
{

    public static class Troop implements Nameable
    {

        private int id;
        private String name;
        private int count;

        public int getId()
        {
            return this.id;
        }

        public String getName()
        {
            return this.name;
        }

        public int getCount()
        {
            return this.count;
        }
    }

    public static class HeroClass implements Nameable
    {

        private int id;
        private String name;
        private int count;

        public int getId()
        {
            return this.id;
        }

        public String getName()
        {
            return this.name;
        }

        public int getCount()
        {
            return this.count;
        }
    }

    private static final long serialVersionUID = 1L;

    private int id = 0;
    private String name = null;
    private int colors = 0;
    private String imageUrl = null;
    private ArrayList<Kingdom> kingdoms = new ArrayList<>();
    private ArrayList<Troop> troops = new ArrayList<>();
    private ArrayList<HeroClass> classes = new ArrayList<>();

    public Traitstone()
    {
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public int getColors()
    {
        return this.colors;
    }

    public String getImageUrl()
    {
        return this.imageUrl;
    }

    public List<Kingdom> getKingdoms()
    {
        return Collections.unmodifiableList(this.kingdoms);
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
