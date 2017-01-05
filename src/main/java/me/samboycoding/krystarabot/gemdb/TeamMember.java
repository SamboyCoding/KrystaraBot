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
public interface TeamMember {
    public void setId(int value);
    public int getId();

    public void setLanguage(String value);
    public String getLanguage();

    public void setName(String value);
    public String getName();

    public void setLastModified(Date value);
    public Date getLastModified();

    public void setSpellName(String value);
    public String getSpellName();
    
    public void setSpellDescription(String value);
    public String getSpellDescription();
    
    public void setSpellCost(int value);
    public int getSpellCost();
    
    public void setSpellBoostRatioText(String value);
    public String getSpellBoostRatioText();
    
    public void setSpellMagicScalingText(String value);
    public String getSpellMagicScalingText();
    
    public void setColors(int value);
    public int getColors();
}
