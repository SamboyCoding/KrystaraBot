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
public class Weapon implements TeamMember, java.io.Serializable
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
    
    @Override
    public void setId(int value) { this.id = value; }
    @Override
    public int getId() { return this.id; }
    
    @Override
    public void setLanguage(String value) { this.language = value; }
    @Override
    public String getLanguage() { return this.language; }
    
    @Override
    public void setName(String value) { this.name = value; }
    @Override
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

    @Override
    public void setLastModified(Date value) { this.lastModified = value; }
    @Override
    public Date getLastModified() { return this.lastModified; }
    
    public void setSpellId(int value) { this.spellId = value; }
    public int getSpellId() { return this.spellId; }
    
    @Override
    public void setSpellName(String value) { this.spellName = value; }
    @Override
    public String getSpellName() { return this.spellName; }
    
    @Override
    public void setSpellDescription(String value) { this.spellDescription = value; }
    @Override
    public String getSpellDescription() { return this.spellDescription; }
    
    @Override
    public void setSpellCost(int value) { this.spellCost = value; }
    @Override
    public int getSpellCost() { return this.spellCost; }
    
    @Override
    public void setSpellBoostRatioText(String value) { this.spellBoostRatioText = value; }
    @Override
    public String getSpellBoostRatioText() { return this.spellBoostRatioText; }
    
    @Override
    public void setSpellMagicScalingText(String value) { this.spellMagicScalingText = value; }
    @Override
    public String getSpellMagicScalingText() { return this.spellMagicScalingText; }
    
    public void setOwnerName(String value) { this.ownerName = value; }
    public String getOwnerName() { return this.ownerName; }
}
