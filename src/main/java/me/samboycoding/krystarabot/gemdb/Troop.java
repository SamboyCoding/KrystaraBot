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
public class Troop implements java.io.Serializable
{
    private int id = 0;
    private String language = null;
    private String name = null;
    private String rarity = null;
    private String type = null;
    private int colors = 0;
    private Date releaseDate;
    private String description = null;
    private String fileBase = null;
    private int rarityId = 0;
    private Date lastModified;
    private int maxArmor = 0;
    private int maxLife = 0;
    private int maxAttack = 0;
    private int maxMagic = 0;
    private String spellName = null;
    private String spellDescription = null;
    private String spellBoostRatioText = null;
    private String spellMagicScalingText = null;
    private int spellCost = 0;
    private String kingdomName = null;
    private int traitstonesRequired = 0;
    
    public Troop()
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
    
    public void setType(String value) { this.type = value; }
    public String getType() { return this.type; }
    
    public void setColors(int value) { this.colors = value; }
    public int getColors() { return this.colors; }
    
    public void setReleaseDate(Date value) { this.releaseDate = value; }
    public Date getReleaseDate() { return this.releaseDate; }

    public void setDescription(String value) { this.description = value; }
    public String getDescription() { return this.description; }
    
    public void setFileBase(String value) { this.fileBase = value; }
    public String getFileBase() { return this.fileBase; }
    
    public void setRarityId(int value) { this.rarityId = value; }
    public int getRarityId() { return this.rarityId; }

    public void setLastModified(Date value) { this.lastModified = value; }
    public Date getLastModified() { return this.lastModified; }
    
    public void setMaxArmor(int value) { this.maxArmor = value; }
    public int getMaxArmor() { return this.maxArmor; }
    
    public void setMaxLife(int value) { this.maxLife = value; }
    public int getMaxLife() { return this.maxLife; }
    
    public void setMaxAttack(int value) { this.maxAttack = value; }
    public int getMaxAttack() { return this.maxAttack; }
    
    public void setMaxMagic(int value) { this.maxMagic = value; }
    public int getMaxMagic() { return this.maxMagic; }
    
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
    
    public void setKingdomName(String value) { this.kingdomName = value; }
    public String getKingdomName() { return this.kingdomName; }
    
    public void setTraitstonesRequired(int value) { this.traitstonesRequired = value; }
    public int getTraitstonesRequired() { return this.traitstonesRequired; }
}
