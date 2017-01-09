package me.samboycoding.krystarabot.gemdb;

import java.sql.Date;

/**
 *
 * @author Emily Ash
 */
public class Weapon extends TeamMember implements java.io.Serializable
{

    private static final long serialVersionUID = 1L;
    private String rarity = null;
    private Date releaseDate;
    private String fileBase = null;
    private int rarityId = 0;
    private String ownerName = null;
    private String pageUrl = null;
    private String imageUrl = null;

    public Weapon()
    {
    }

    public void setRarity(String value)
    {
        this.rarity = value;
    }

    public String getRarity()
    {
        return this.rarity;
    }

    public void setReleaseDate(Date value)
    {
        this.releaseDate = value;
    }

    public Date getReleaseDate()
    {
        return this.releaseDate;
    }

    public void setFileBase(String value)
    {
        this.fileBase = value;
    }

    public String getFileBase()
    {
        return this.fileBase;
    }

    public void setRarityId(int value)
    {
        this.rarityId = value;
    }

    public int getRarityId()
    {
        return this.rarityId;
    }

    public void setOwnerName(String value)
    {
        this.ownerName = value;
    }

    public String getOwnerName()
    {
        return this.ownerName;
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
