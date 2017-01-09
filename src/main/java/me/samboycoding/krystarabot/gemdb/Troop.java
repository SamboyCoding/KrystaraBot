package me.samboycoding.krystarabot.gemdb;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Emily Ash
 */
public class Troop extends TeamMember implements java.io.Serializable
{

    public static class Trait implements Nameable
    {

        private String code;
        private String name;

        public String getCode()
        {
            return this.code;
        }

        public String getName()
        {
            return this.name;
        }
    }

    private static final long serialVersionUID = 1L;
    private String rarity = null;
    private String type = null;
    private Date releaseDate;
    private String description = null;
    private String fileBase = null;
    private int rarityId = 0;
    private int maxArmor = 0;
    private int maxLife = 0;
    private int maxAttack = 0;
    private int maxMagic = 0;
    private String kingdomName = null;
    private int traitstonesRequired = 0;
    private final String[] traitNames = new String[3];
    private String pageUrl = null;
    private String imageUrl = null;
    private ArrayList<Trait> traits = new ArrayList<>();

    public Troop()
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

    public void setType(String value)
    {
        this.type = value;
    }

    public String getType()
    {
        return this.type;
    }

    public void setReleaseDate(Date value)
    {
        this.releaseDate = value;
    }

    public Date getReleaseDate()
    {
        return this.releaseDate;
    }

    public void setDescription(String value)
    {
        this.description = value;
    }

    public String getDescription()
    {
        return this.description;
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

    public void setMaxArmor(int value)
    {
        this.maxArmor = value;
    }

    public int getMaxArmor()
    {
        return this.maxArmor;
    }

    public void setMaxLife(int value)
    {
        this.maxLife = value;
    }

    public int getMaxLife()
    {
        return this.maxLife;
    }

    public void setMaxAttack(int value)
    {
        this.maxAttack = value;
    }

    public int getMaxAttack()
    {
        return this.maxAttack;
    }

    public void setMaxMagic(int value)
    {
        this.maxMagic = value;
    }

    public int getMaxMagic()
    {
        return this.maxMagic;
    }

    public void setKingdomName(String value)
    {
        this.kingdomName = value;
    }

    public String getKingdomName()
    {
        return this.kingdomName;
    }

    public void setTraitstonesRequired(int value)
    {
        this.traitstonesRequired = value;
    }

    public int getTraitstonesRequired()
    {
        return this.traitstonesRequired;
    }

    public void setTraitName0(String value)
    {
        this.traitNames[0] = value;
    }

    public void setTraitName1(String value)
    {
        this.traitNames[1] = value;
    }

    public void setTraitName2(String value)
    {
        this.traitNames[2] = value;
    }

    public String getTraitName(int index)
    {
        return this.traitNames[index];
    }

    public String getPageUrl()
    {
        return this.pageUrl;
    }

    public String getImageUrl()
    {
        return this.imageUrl;
    }

    public List<Trait> getTraits()
    {
        return Collections.unmodifiableList(this.traits);
    }
}
