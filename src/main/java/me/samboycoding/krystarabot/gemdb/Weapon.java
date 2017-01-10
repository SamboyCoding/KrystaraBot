package me.samboycoding.krystarabot.gemdb;

public class Weapon extends TeamMember implements java.io.Serializable
{
    private static class HeroClass extends IdNamePair
    {
    }
    
    private static final long serialVersionUID = 1L;
    private String rarity = null;
    private int rarityId = 0;
    private HeroClass owner = null;
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

    public HeroClass getOwner()
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
