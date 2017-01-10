package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Troop extends TeamMember implements java.io.Serializable
{

    private Troop()
    {
    }

    public static Troop fromId(int id) throws IOException
    {
        return AshClient.query("troops/" + id + "/details", Troop.class);
    }

    public static class Summary extends SummaryBase
    {

        public Troop getDetails() throws IOException
        {
            return Troop.fromId(getId());
        }
    }

    private static final long serialVersionUID = 1L;
    private String rarity = null;
    private String type = null;
    private String description = null;
    private int rarityId = 0;
    private int maxArmor = 0;
    private int maxLife = 0;
    private int maxAttack = 0;
    private int maxMagic = 0;
    private String kingdomName = null;
    private String pageUrl = null;
    private String imageUrl = null;
    private ArrayList<Trait.Summary> traits = new ArrayList<>();

    public String getRarity()
    {
        return this.rarity;
    }

    public String getType()
    {
        return this.type;
    }

    public String getDescription()
    {
        return this.description;
    }

    public int getRarityId()
    {
        return this.rarityId;
    }

    public int getMaxArmor()
    {
        return this.maxArmor;
    }

    public int getMaxLife()
    {
        return this.maxLife;
    }

    public int getMaxAttack()
    {
        return this.maxAttack;
    }

    public int getMaxMagic()
    {
        return this.maxMagic;
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

    public List<Trait.Summary> getTraits()
    {
        return Collections.unmodifiableList(this.traits);
    }
}
