/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.gemdb;

/**
 *
 * @author julians
 */
public class Bonus implements java.io.Serializable
{
    private int troopCount = 0;
    private String typeCode = null;
    private int color = 0;
    private String language = null;
    private String name = null;
    private String description = null;
    private int armor = 0;
    private int life = 0;
    private int attack = 0;
    private int magic = 0;
    
    public Bonus()
    {
    }
    
    public void setTroopCount(int value) { this.troopCount = value; }
    public int getTroopCount() { return this.troopCount; }
    
    public void setTypeCode(String value) { this.typeCode = value; }
    public String getTypeCode() { return this.typeCode; }
    
    public void setColor(int value) { this.color = value; }
    public int getColor() { return this.color; }
    
    public void setLanguage(String value) { this.language = value; }
    public String getLanguage() { return this.language; }
    
    public void setName(String value) { this.name = value; }
    public String getName() { return this.name; }

    public void setDescription(String value) { this.description = value; }
    public String getDescription() { return this.description; }
    
    public void setArmor(int value) { this.armor = value; }
    public int getArmor() { return this.armor; }
    
    public void setLife(int value) { this.life = value; }
    public int getLife() { return this.life; }
    
    public void setAttack(int value) { this.attack = value; }
    public int getAttack() { return this.attack; }
    
    public void setMagic(int value) { this.magic = value; }
    public int getMagic() { return this.magic; }
}
