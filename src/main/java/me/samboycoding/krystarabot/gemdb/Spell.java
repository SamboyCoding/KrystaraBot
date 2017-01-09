package me.samboycoding.krystarabot.gemdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Emily Ash
 */
public class Spell implements Nameable, java.io.Serializable
{
    private static final long serialVersionUID = 1L;
    private int id = 0;
    private String name = null;
    private String description = null;
    private String boostRatioText = null;
    private String magicScalingText = null;
    private int cost = 0;
    private int colors = 0;
    private String troopName = null;
    private String weaponName = null;
    private String imageUrl = null;
    private ArrayList<Troop> troops = new ArrayList<>();
    private ArrayList<Weapon> weapons = new ArrayList<>();

    public Spell()
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

    public void setName(String value)
    {
        this.name = value;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public void setDescription(String value)
    {
        this.description = value;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setCost(int value)
    {
        this.cost = value;
    }

    public int getCost()
    {
        return this.cost;
    }

    public void setColors(int value)
    {
        this.colors = value;
    }

    public int getColors()
    {
        return this.colors;
    }

    public void setBoostRatioText(String value)
    {
        this.boostRatioText = value;
    }

    public String getBoostRatioText()
    {
        return this.boostRatioText;
    }

    public void setMagicScalingText(String value)
    {
        this.magicScalingText = value;
    }

    public String getMagicScalingText()
    {
        return this.magicScalingText;
    }

    public void setTroopName(String value)
    {
        this.troopName = value;
    }

    public String getTroopName()
    {
        return this.troopName;
    }

    public void setWeaponName(String value)
    {
        this.weaponName = value;
    }

    public String getWeaponName()
    {
        return this.weaponName;
    }

    public String getImageUrl()
    {
        return this.imageUrl;
    }

    public List<Weapon> getWeapons()
    {
        return Collections.unmodifiableList(this.weapons);
    }

    public List<Troop> getTroops()
    {
        return Collections.unmodifiableList(this.troops);
    }
}
