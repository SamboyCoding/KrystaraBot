package me.samboycoding.krystarabot.gemdb;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Emily Ash
 */
public class HeroClass implements Nameable, java.io.Serializable
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

    public static class Perk implements Nameable
    {

        private String name;
        private String perkType;

        public String getName()
        {
            return this.name;
        }

        public String getPerkType()
        {
            return this.perkType;
        }
    }

    private static final long serialVersionUID = 1L;

    private int id = 0;
    private String language = null;
    private String name = null;
    private String type = null;
    private int colors = 0;
    private Date releaseDate;
    private String fileBase = null;
    private int rarityId = 0;
    private Date lastModified;
    private String spellName = null;
    private String spellDescription = null;
    private String spellBoostRatioText = null;
    private String spellMagicScalingText = null;
    private int spellCost = 0;
    private String kingdomName = null;
    private String weaponName = null;
    private int traitstonesRequired = 0;
    private String[] traitNames = new String[3];
    private String pageUrl = null;
    private String imageUrl = null;
    private ArrayList<Trait> traits = new ArrayList<>();
    private ArrayList<Perk> perks = new ArrayList<>();
    private Weapon weapon = null;

    public HeroClass()
    {
    }

    public void setId(int value)
    {
        this.id = value;
    }

    public int getId()
    {
        return this.id;
    }

    public void setLanguage(String value)
    {
        this.language = value;
    }

    public String getLanguage()
    {
        return this.language;
    }

    public void setName(String value)
    {
        this.name = value;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public void setType(String value)
    {
        this.type = value;
    }

    public String getType()
    {
        return this.type;
    }

    public void setColors(int value)
    {
        this.colors = value;
    }

    public int getColors()
    {
        return this.colors;
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

    public void setLastModified(Date value)
    {
        this.lastModified = value;
    }

    public Date getLastModified()
    {
        return this.lastModified;
    }

    public void setSpellName(String value)
    {
        this.spellName = value;
    }

    public String getSpellName()
    {
        return this.spellName;
    }

    public void setSpellDescription(String value)
    {
        this.spellDescription = value;
    }

    public String getSpellDescription()
    {
        return this.spellDescription;
    }

    public void setSpellCost(int value)
    {
        this.spellCost = value;
    }

    public int getSpellCost()
    {
        return this.spellCost;
    }

    public void setSpellBoostRatioText(String value)
    {
        this.spellBoostRatioText = value;
    }

    public String getSpellBoostRatioText()
    {
        return this.spellBoostRatioText;
    }

    public void setSpellMagicScalingText(String value)
    {
        this.spellMagicScalingText = value;
    }

    public String getSpellMagicScalingText()
    {
        return this.spellMagicScalingText;
    }

    public void setKingdomName(String value)
    {
        this.kingdomName = value;
    }

    public String getKingdomName()
    {
        return this.kingdomName;
    }

    public void setWeaponName(String value)
    {
        this.weaponName = value;
    }

    public String getWeaponName()
    {
        return this.weaponName;
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

    public List<Perk> getPerks()
    {
        return Collections.unmodifiableList(this.perks);
    }

    public Weapon getWeapon()
    {
        return this.weapon;
    }
}
