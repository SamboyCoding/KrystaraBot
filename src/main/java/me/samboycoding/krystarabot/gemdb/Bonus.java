package me.samboycoding.krystarabot.gemdb;

public class Bonus implements java.io.Serializable
{

    private static final long serialVersionUID = 1L;
    private int troopCount = 0;
    private String typeCode = null;
    private int color = 0;
    private String name = null;
    private String description = null;
    private int armor = 0;
    private int life = 0;
    private int attack = 0;
    private int magic = 0;

    public Bonus()
    {
    }

    public int getTroopCount()
    {
        return this.troopCount;
    }

    public String getTypeCode()
    {
        return this.typeCode;
    }

    public int getColor()
    {
        return this.color;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public int getArmor()
    {
        return this.armor;
    }

    public int getLife()
    {
        return this.life;
    }

    public int getAttack()
    {
        return this.attack;
    }

    public int getMagic()
    {
        return this.magic;
    }
}
