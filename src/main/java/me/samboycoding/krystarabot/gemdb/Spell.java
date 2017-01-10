package me.samboycoding.krystarabot.gemdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Spell implements Nameable, java.io.Serializable
{
    public static class Troop extends IdNamePair
    {
    }
    
    public static class Weapon extends IdNamePair
    {
    }

    private static final long serialVersionUID = 1L;
    private int id = 0;
    private String name = null;
    private String description = null;
    private String boostRatioText = null;
    private String magicScalingText = null;
    private int cost = 0;
    private int colors = 0;
    private String imageUrl = null;
    private ArrayList<Troop> troops = new ArrayList<>();
    private ArrayList<Weapon> weapons = new ArrayList<>();

    public int getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public int getCost()
    {
        return this.cost;
    }

    public int getColors()
    {
        return this.colors;
    }

    public String getBoostRatioText()
    {
        return this.boostRatioText;
    }

    public String getMagicScalingText()
    {
        return this.magicScalingText;
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
