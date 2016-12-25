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
public class Kingdom implements java.io.Serializable
{
    private int id = 0;
    private String language = null;
    private String name = null;
    private String byLine = null;
    private String description = null;
    private String fileBase = null;
    private String bannerName = null;
    private String bannerDescription = null;
    private int tributeGold = 0;
    private int tributeGlory = 0;
    private int tributeSouls = 0;
    private int levelManaColor = 0;
    private String levelStat = null;
    private int exploreTraitstoneColors = 0;
    private String exploreTraitstoneName = null;
    private boolean isUsed = false;
    private boolean isFullKingdom = false;
    
    public Kingdom()
    {
    }
    
    public void setId(int value) { this.id = value; }
    public int getId() { return this.id; }
    
    public void setLanguage(String value) { this.language = value; }
    public String getLanguage() { return this.language; }
    
    public void setName(String value) { this.name = value; }
    public String getName() { return this.name; }
    
    public void setByLine(String value) { this.byLine = value; }
    public String getByLine() { return this.byLine; }
    
    public void setDescription(String value) { this.description = value; }
    public String getDescription() { return this.description; }
    
    public void setFileBase(String value) { this.fileBase = value; }
    public String getFileBase() { return this.fileBase; }
    
    public void setBannerName(String value) { this.bannerName = value; }
    public String getBannerName() { return this.bannerName; }
    
    public void setBannerDescription(String value) { this.bannerDescription = value; }
    public String getBannerDescription() { return this.bannerDescription; }
    
    public void setTributeGold(int value) { this.tributeGold = value; }
    public int getTributeGold() { return this.tributeGold; }
    
    public void setTributeGlory(int value) { this.tributeGlory = value; }
    public int getTributeGlory() { return this.tributeGlory; }
    
    public void setTributeSouls(int value) { this.tributeSouls = value; }
    public int getTributeSouls() { return this.tributeSouls; }
    
    public void setLevelManaColor(int value) { this.levelManaColor = value; }
    public int getLevelManaColor() { return this.levelManaColor; }

    public void setLevelStat(String value) { this.levelStat = value; }
    public String getLevelStat() { return this.levelStat; }

    public void setExploreTraitstoneName(String value) { this.exploreTraitstoneName = value; }
    public String getExploreTraitstoneName() { return this.exploreTraitstoneName; }
    
    public void setExploreTraitstoneColors(int value) { this.exploreTraitstoneColors = value; }
    public int getExploreTraitstoneColors() { return this.exploreTraitstoneColors; }

    public void setIsUsed(boolean value) { this.isUsed = value; }
    public boolean getIsUsed() { return this.isUsed; }
    
    public void setIsFullKingdom(boolean value) { this.isFullKingdom = value; }
    public boolean getIsFullKingdom() { return this.isFullKingdom; }
}