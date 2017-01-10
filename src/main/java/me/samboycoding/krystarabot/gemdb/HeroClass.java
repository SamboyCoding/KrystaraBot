package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeroClass implements Nameable, java.io.Serializable
{
    private HeroClass()
    {}
    
    public static HeroClass fromId(int id) throws IOException
    {
        return AshClient.query("classes/" + id + "/details", HeroClass.class);
    }
    
    public static class Summary extends SummaryBase
    {
        public HeroClass getDetails() throws IOException
        {
            return HeroClass.fromId(getId());
        }
    }

    public static class Perk implements Nameable
    {
        private String name;
        private String perkType;

        public String getName()
        {
            return this.name;
        }

        public String getPerkType()
        {
            return this.perkType;
        }
    }

    private static final long serialVersionUID = 1L;

    private int id = 0;
    private String name = null;
    private String type = null;
    private String kingdomName = null;
    private String weaponName = null;
    private String pageUrl = null;
    private String imageUrl = null;
    private ArrayList<Trait> traits = new ArrayList<>();
    private ArrayList<Perk> perks = new ArrayList<>();
    private Weapon weapon = null;

    public int getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public String getType()
    {
        return this.type;
    }

    public String getKingdomName()
    {
        return this.kingdomName;
    }

    public String getPageUrl()
    {
        return this.pageUrl;
    }

    public String getImageUrl()
    {
        return this.imageUrl;
    }

    public List<Trait> getTraits()
    {
        return Collections.unmodifiableList(this.traits);
    }

    public List<Perk> getPerks()
    {
        return Collections.unmodifiableList(this.perks);
    }

    public Weapon getWeapon()
    {
        return this.weapon;
    }
}
