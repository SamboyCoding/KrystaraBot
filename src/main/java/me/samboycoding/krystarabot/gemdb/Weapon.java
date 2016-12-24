/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.gemdb;

import java.sql.Date;

/**
 *
 * @author Emily Ash
 */
public class Weapon implements java.io.Serializable
{
    private int id = 0;
    private String language = null;
    private String name = null;
    private String rarity = null;
    private int colors = 0;
    private Date releaseDate;
    private String fileBase = null;
    private int rarityId = 0;
    private Date lastModified;
    private int spellId = 0;
    private String spellName = null;
    private String spellDescription = null;
    private String spellBoostRatioText = null;
    private String spellMagicScalingText = null;
    private int spellCost = 0;
    private String ownerName = null;
    
    public Weapon()
    {
    }
    
    public void setId(int value) { this.id = value; }
    public int getId() { return this.id; }
    
    public void setLanguage(String value) { this.language = value; }
    public String getLanguage() { return this.language; }
    
    public void setName(String value) { this.name = value; }
    public String getName() { return this.name; }
    
    public void setRarity(String value) { this.rarity = value; }
    public String getRarity() { return this.rarity; }
    
    public void setColors(int value) { this.colors = value; }
    public int getColors() { return this.colors; }
    
    public void setReleaseDate(Date value) { this.releaseDate = value; }
    public Date getReleaseDate() { return this.releaseDate; }

    public void setFileBase(String value) { this.fileBase = value; }
    public String getFileBase() { return this.fileBase; }
    
    public void setRarityId(int value) { this.rarityId = value; }
    public int getRarityId() { return this.rarityId; }

    public void setLastModified(Date value) { this.lastModified = value; }
    public Date getLastModified() { return this.lastModified; }
    
    public void setSpellId(int value) { this.spellId = value; }
    public int getSpellId() { return this.spellId; }
    
    public void setSpellName(String value) { this.spellName = value; }
    public String getSpellName() { return this.spellName; }
    
    public void setSpellDescription(String value) { this.spellDescription = value; }
    public String getSpellDescription() { return this.spellDescription; }
    
    public void setSpellCost(int value) { this.spellCost = value; }
    public int getSpellCost() { return this.spellCost; }
    
    public void setSpellBoostRatioText(String value) { this.spellBoostRatioText = value; }
    public String getSpellBoostRatioText() { return this.spellBoostRatioText; }
    
    public void setSpellMagicScalingText(String value) { this.spellMagicScalingText = value; }
    public String getSpellMagicScalingText() { return this.spellMagicScalingText; }
    
    public void setOwnerName(String value) { this.ownerName = value; }
    public String getOwnerName() { return this.ownerName; }
}
