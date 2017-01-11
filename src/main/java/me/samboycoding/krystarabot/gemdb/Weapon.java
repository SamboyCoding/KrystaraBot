package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import me.samboycoding.krystarabot.Language;

public class Weapon extends TeamMember implements java.io.Serializable
{

    private Weapon()
    {
    }

    public static Weapon fromId(int id, Language lang) throws IOException
    {
        return AshClient.query("weapons/" + id + "/details", Weapon.class, lang);
    }

    public static class Summary extends SummaryBase
    {

        public Weapon getDetails(Language lang) throws IOException
        {
            return Weapon.fromId(getId(), lang);
        }
    }

    private static final long serialVersionUID = 1L;
    private String rarity = null;
    private int rarityId = 0;
    private HeroClass.Summary owner = null;
    private String pageUrl = null;
    private String imageUrl = null;

    public String getRarity()
    {
        return this.rarity;
    }

    public int getRarityId()
    {
        return this.rarityId;
    }

    public HeroClass.Summary getOwner()
    {
        return this.owner;
    }

    public String getPageUrl()
    {
        return this.pageUrl;
    }

    public String getImageUrl()
    {
        return this.imageUrl;
    }
}
