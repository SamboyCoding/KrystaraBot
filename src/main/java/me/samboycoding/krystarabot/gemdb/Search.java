/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.gemdb;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author julians
 */
public class Search implements java.io.Serializable
{
    public static interface SearchResult extends Nameable
    {
        public int getId();
    }
    
    public static class Troop implements SearchResult
    {
        private int id;
        private String name;
        
        public int getId() { return this.id; }
        public String getName() { return this.name; }
    }
    
    public static class Spell implements SearchResult
    {
        private int id;
        private String name;
        private ArrayList<Troop> troops = new ArrayList<>();
        
        public int getId() { return this.id; }
        public String getName() { return this.name; }
        public List<Troop> getTroops() { return Collections.unmodifiableList(this.troops); }
    }

    public static class Trait
    {
        private String code;
        private String name;
        private ArrayList<Troop> troops = new ArrayList<>();
        
        public String getCode() { return this.code; }
        public String getName() { return this.name; }
        public List<Troop> getTroops() { return Collections.unmodifiableList(this.troops); }
    }

    public static class Kingdom implements SearchResult
    {
        private int id;
        private String name;
        
        public int getId() { return this.id; }
        public String getName() { return this.name; }
    }
    
    public static class HeroClass implements SearchResult
    {
        private int id;
        private String name;
        
        public int getId() { return this.id; }
        public String getName() { return this.name; }
    }

    public static class Weapon implements SearchResult
    {
        private int id;
        private String name;
        
        public int getId() { return this.id; }
        public String getName() { return this.name; }
    }

    private ArrayList<Troop> troops = new ArrayList<>();;
    private ArrayList<Trait> traits = new ArrayList<>();;
    private ArrayList<Spell> spells = new ArrayList<>();;
    private ArrayList<Kingdom> kingdoms = new ArrayList<>();;
    private ArrayList<HeroClass> classes = new ArrayList<>();;
    private ArrayList<Weapon> weapons = new ArrayList<>();;
    
    public List<Troop> getTroops() { return Collections.unmodifiableList(this.troops); }
    public List<Trait> getTraits() { return Collections.unmodifiableList(this.traits); }
    public List<Spell> getSpells() { return Collections.unmodifiableList(this.spells); }
    public List<Kingdom> getKingdoms() { return Collections.unmodifiableList(this.kingdoms); }
    public List<HeroClass> getHeroClasses() { return Collections.unmodifiableList(this.classes); }
    public List<Weapon> getWeapons() { return Collections.unmodifiableList(this.weapons); }
}
