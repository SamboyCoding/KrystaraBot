package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.samboycoding.krystarabot.Language;

public class Search implements java.io.Serializable
{
    public static Search fromQuery(String query, Language lang) throws IOException
    {
        return AshClient.query("searches/" + query, Search.class, lang);
    }

    private Search()
    {
    }

    public static class SpellSummary extends Spell.Summary
    {

        private ArrayList<Troop.Summary> troops = new ArrayList<>();
        private ArrayList<Weapon.Summary> weapons = new ArrayList<>();

        public List<Troop.Summary> getTroops()
        {
            return Collections.unmodifiableList(this.troops);
        }

        public List<Weapon.Summary> getWeapons()
        {
            return Collections.unmodifiableList(this.weapons);
        }
    }

    public static class TraitSummary extends Trait.Summary
    {

        private ArrayList<Troop.Summary> troops = new ArrayList<>();
        private ArrayList<HeroClass.Summary> classes = new ArrayList<>();

        public List<Troop.Summary> getTroops()
        {
            return Collections.unmodifiableList(this.troops);
        }

        public List<HeroClass.Summary> getHeroClasses()
        {
            return Collections.unmodifiableList(this.classes);
        }
    }

    private ArrayList<Troop.Summary> troops = new ArrayList<>();
    private ArrayList<TraitSummary> traits = new ArrayList<>();
    private ArrayList<SpellSummary> spells = new ArrayList<>();
    private ArrayList<Kingdom.Summary> kingdoms = new ArrayList<>();
    private ArrayList<HeroClass.Summary> classes = new ArrayList<>();
    private ArrayList<Weapon.Summary> weapons = new ArrayList<>();

    public List<Troop.Summary> getTroops()
    {
        return Collections.unmodifiableList(this.troops);
    }

    public List<TraitSummary> getTraits()
    {
        return Collections.unmodifiableList(this.traits);
    }

    public List<SpellSummary> getSpells()
    {
        return Collections.unmodifiableList(this.spells);
    }

    public List<Kingdom.Summary> getKingdoms()
    {
        return Collections.unmodifiableList(this.kingdoms);
    }

    public List<HeroClass.Summary> getHeroClasses()
    {
        return Collections.unmodifiableList(this.classes);
    }

    public List<Weapon.Summary> getWeapons()
    {
        return Collections.unmodifiableList(this.weapons);
    }
}
