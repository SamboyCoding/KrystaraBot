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
public class TeamMember implements Nameable {
    private int id = 0;
    private String language = null;
    private String name = null;
    private int colors = 0;
    private Date lastModified;
    private int spellId = 0;
    private String spellName = null;
    private String spellDescription = null;
    private String spellBoostRatioText = null;
    private String spellMagicScalingText = null;
    private int spellCost = 0;
    private String kind = null;

    public void setId(int value) { this.id = value; }
    public int getId() { return this.id; }
    
    public void setLanguage(String value) { this.language = value; }
    public String getLanguage() { return this.language; }
    
    @Override
    public void setName(String value) { this.name = value; }
    @Override
    public String getName() { return this.name; }
    
    public void setColors(int value) { this.colors = value; }
    public int getColors() { return this.colors; }
    
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
    
    public void setKind(String value) { this.kind = value; }
    public String getKind() { return this.kind; }
}
