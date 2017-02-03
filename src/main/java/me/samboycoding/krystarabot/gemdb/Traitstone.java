package me.samboycoding.krystarabot.gemdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.samboycoding.krystarabot.Language;

public class Traitstone implements Nameable, java.io.Serializable
{

    private Traitstone()
    {
    }

    public static Traitstone fromId(int id, Language lang) throws IOException
    {
        return AshClient.query("traitstones/" + id + "/details", Traitstone.class, lang);
    }

    public static class Summary extends SummaryBase
    {

        public Traitstone getDetails(Language lang) throws IOException
        {
            return Traitstone.fromId(getId(), lang);
        }
    }

    public static class TroopSummaryWithCount extends Troop.Summary
    {

        private int count;

        public int getCount()
        {
            return this.count;
        }
    }

    public static class HeroClassSummaryWithCount extends HeroClass.Summary
    {

        private int count;

        public int getCount()
        {
            return this.count;
        }
    }

    private static final long serialVersionUID = 1L;

    private int id = 0;
    private String name = null;
    private int colors = 0;
    private String imageUrl = null;
    private ArrayList<Kingdom.Summary> kingdoms = new ArrayList<>();
    private ArrayList<TroopSummaryWithCount> troops = new ArrayList<>();
    private ArrayList<HeroClassSummaryWithCount> classes = new ArrayList<>();

    public int getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public int getColors()
    {
        return this.colors;
    }

    public String getImageUrl()
    {
        return this.imageUrl;
    }

    public List<Kingdom.Summary> getKingdoms()
    {
        return Collections.unmodifiableList(this.kingdoms);
    }

    public List<TroopSummaryWithCount> getTroops()
    {
        return Collections.unmodifiableList(this.troops);
    }

    public List<HeroClassSummaryWithCount> getHeroClasses()
    {
        return Collections.unmodifiableList(this.classes);
    }
}
