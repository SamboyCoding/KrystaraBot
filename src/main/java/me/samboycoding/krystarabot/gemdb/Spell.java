package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.samboycoding.krystarabot.Language;

public class Spell implements Nameable, java.io.Serializable
{

    private Spell()
    {
    }

    public static Spell fromId(int id, Language lang) throws IOException
    {
        return AshClient.query("spells/" + id + "/details", Spell.class, lang);
    }

    public static class Summary extends SummaryBase
    {

        public Spell getDetails(Language lang) throws IOException
        {
            return Spell.fromId(getId(), lang);
        }
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
    private ArrayList<Troop.Summary> troops = new ArrayList<>();
    private ArrayList<Weapon.Summary> weapons = new ArrayList<>();

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

    public List<Weapon.Summary> getWeapons()
    {
        return Collections.unmodifiableList(this.weapons);
    }

    public List<Troop.Summary> getTroops()
    {
        return Collections.unmodifiableList(this.troops);
    }
}
