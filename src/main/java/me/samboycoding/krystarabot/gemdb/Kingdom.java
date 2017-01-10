package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kingdom implements Nameable, java.io.Serializable
{
    private Kingdom()
    {}
    
    public static Kingdom fromId(int id) throws IOException
    {
        return AshClient.query("kingdoms/" + id + "/details", Kingdom.class);
    }

    public static class Summary extends SummaryBase
    {
        public Kingdom getDetails() throws IOException
        {
            return Kingdom.fromId(getId());
        }
    }
    
    private static final long serialVersionUID = 1L;

    private int id = 0;
    private String name = null;
    private String byLine = null;
    private String description = null;
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
    private String pageUrl = null;
    private String imageUrl = null;
    private String bannerImageUrl = null;
    private ArrayList<Bonus> bonuses = new ArrayList<>();
    private ArrayList<Troop> troops = new ArrayList<>();

    public int getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public String getByLine()
    {
        return this.byLine;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getBannerName()
    {
        return this.bannerName;
    }

    public String getBannerDescription()
    {
        return this.bannerDescription;
    }

    public int getTributeGold()
    {
        return this.tributeGold;
    }

    public int getTributeGlory()
    {
        return this.tributeGlory;
    }

    public int getTributeSouls()
    {
        return this.tributeSouls;
    }

    public int getLevelManaColor()
    {
        return this.levelManaColor;
    }

    public String getLevelStat()
    {
        return this.levelStat;
    }

    public String getExploreTraitstoneName()
    {
        return this.exploreTraitstoneName;
    }

    public int getExploreTraitstoneColors()
    {
        return this.exploreTraitstoneColors;
    }

    public boolean isUsed()
    {
        return this.isUsed;
    }

    public boolean isFullKingdom()
    {
        return this.isFullKingdom;
    }

    public String getPageUrl()
    {
        return this.pageUrl;
    }

    public String getImageUrl()
    {
        return this.imageUrl;
    }

    public String getBannerImageUrl()
    {
        return this.bannerImageUrl;
    }

    public List<Bonus> getBonuses()
    {
        return Collections.unmodifiableList(this.bonuses);
    }

    public List<Troop> getTroops()
    {
        return Collections.unmodifiableList(this.troops);
    }
}
