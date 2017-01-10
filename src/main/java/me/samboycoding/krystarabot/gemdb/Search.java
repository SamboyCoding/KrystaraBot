package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Search implements java.io.Serializable
{
    public static Search fromQuery(String query) throws IOException
    {
        return AshClient.query("searches/" + query, Search.class);
    }

    private Search()
    {
    }

    public static class SearchResult extends IdNamePair
    {
    }

    public static class Troop extends SearchResult
    {
    }

    public static class Spell extends SearchResult
    {
        private ArrayList<Troop> troops = new ArrayList<>();
        private ArrayList<Weapon> weapons = new ArrayList<>();

        public List<Troop> getTroops()
        {
            return Collections.unmodifiableList(this.troops);
        }

        public List<Weapon> getWeapons()
        {
            return Collections.unmodifiableList(this.weapons);
        }
    }

    public static class Trait implements Nameable
    {
        private String code;
        private String name;
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

        public List<Troop> getTroops()
        {
            return Collections.unmodifiableList(this.troops);
        }

        public List<HeroClass> getHeroClasses()
        {
            return Collections.unmodifiableList(this.classes);
        }
    }

    public static class Kingdom extends SearchResult
    {
    }

    public static class HeroClass extends SearchResult
    {
    }

    public static class Weapon extends SearchResult
    {
    }

    private ArrayList<Troop> troops = new ArrayList<>();
    private ArrayList<Trait> traits = new ArrayList<>();
    private ArrayList<Spell> spells = new ArrayList<>();
    private ArrayList<Kingdom> kingdoms = new ArrayList<>();
    private ArrayList<HeroClass> classes = new ArrayList<>();
    private ArrayList<Weapon> weapons = new ArrayList<>();
    
    public List<Troop> getTroops()
    {
        return Collections.unmodifiableList(this.troops);
    }

    public List<Trait> getTraits()
    {
        return Collections.unmodifiableList(this.traits);
    }

    public List<Spell> getSpells()
    {
        return Collections.unmodifiableList(this.spells);
    }

    public List<Kingdom> getKingdoms()
    {
        return Collections.unmodifiableList(this.kingdoms);
    }

    public List<HeroClass> getHeroClasses()
    {
        return Collections.unmodifiableList(this.classes);
    }

    public List<Weapon> getWeapons()
    {
        return Collections.unmodifiableList(this.weapons);
    }
}
