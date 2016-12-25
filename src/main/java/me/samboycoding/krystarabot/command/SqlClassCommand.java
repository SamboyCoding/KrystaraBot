/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.command;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.sql.DataSource;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.GemsQueryRunner;
import me.samboycoding.krystarabot.gemdb.HeroClass;
import me.samboycoding.krystarabot.gemdb.HeroClassPerk;
import me.samboycoding.krystarabot.gemdb.Trait;
import me.samboycoding.krystarabot.gemdb.Troop;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.io.FileUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 *
 * @author julians
 */
public class SqlClassCommand extends QuestionCommand
{
    public SqlClassCommand()
    {
        commandName = "class";

    }
    
    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            chnl.sendMessage("You cannot do that!");
            return;
        }
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }
        
        try
        {
            String heroClassName = String.join(" ", arguments);
            QueryRunner run = GemsQueryRunner.getQueryRunner();
            ResultSetHandler<List<HeroClass>> classHandler = new BeanListHandler<>(HeroClass.class);
            List<HeroClass> heroClasses = run.query("SELECT Kingdoms.Name AS KingdomName, "
                + "     Spells.Name AS SpellName, Spells.Description AS SpellDescription, "
                + "     Spells.BoostRatioText AS SpellBoostRatioText, Spells.MagicScalingText AS SpellMagicScalingText, "
                + "     Spells.Cost AS SpellCost, Weapons.Name AS WeaponName, Classes.*"
                + "FROM Classes "
                + "INNER JOIN Weapons ON Weapons.OwnerId=Classes.Id AND Weapons.Language=Classes.Language "
                + "INNER JOIN Kingdoms ON Kingdoms.Id=Classes.KingdomId AND Kingdoms.Language=Classes.Language "
                + "INNER JOIN Spells ON Spells.Id=Weapons.SpellId AND Spells.Language=Weapons.Language "
                + "WHERE Classes.Language='en-US' AND Classes.Name LIKE ? AND Classes.ReleaseDate<NOW() "
                + "ORDER BY Classes.Name", classHandler,
                heroClassName + "%"
                );

            if (heroClasses.isEmpty())
            {
                chnl.sendMessage("No class `" + heroClassName + "` found.");
                return;
            }
            else if ((heroClasses.size() > 1) && (!heroClasses.get(0).getName().toLowerCase().equals(heroClassName.toLowerCase())))
            {
                Stream<String> str = heroClasses.stream().map(t -> t.getName());
                Utilities.sendDisambiguationMessage(chnl, "Search term \"" + heroClassName + "\" is ambiguous.", str::iterator);
                return;
            }
            
            HeroClass heroClass = heroClasses.get(0);
            
            ResultSetHandler<List<Trait>> traitHandler = new BeanListHandler<>(Trait.class);
            List<Trait> traits = run.query("SELECT Traits.* FROM TroopTraits "
                + "INNER JOIN Traits ON Traits.Code=TroopTraits.Code "
                + "WHERE Traits.Language='en-US' AND TroopTraits.TroopId=? AND TroopTraits.CostIndex=0 "
                + "ORDER BY TraitIndex", traitHandler,
                heroClass.getId()
                );

            ResultSetHandler<List<HeroClassPerk>> perkHandler = new BeanListHandler<>(HeroClassPerk.class);
            List<HeroClassPerk> perks = run.query("SELECT ClassPerks.* FROM ClassPerks "
                + "WHERE ClassPerks.Language='en-US' AND ClassPerks.ClassId=? "
                + "ORDER BY PerkIndex", perkHandler,
                heroClass.getId()
                );
            
            URL url = new URL("http://ashtender.com/gems/assets/classes/" + heroClass.getId() + ".png");

            String spellDesc = heroClass.getSpellDescription();
            String spellMagicScalingText = heroClass.getSpellMagicScalingText();
            if (spellMagicScalingText != null)
            {
                if (!spellDesc.contains("{2}"))
                {
                    spellDesc = spellDesc.replace("{1}", spellMagicScalingText);
                }
                else
                {
                    spellDesc = spellDesc.replace("{1}", "(half)");
                    spellDesc = spellDesc.replace("{2}", spellMagicScalingText);
                }
            }
            String spellBoostRatioText = heroClass.getSpellBoostRatioText();
            if (spellBoostRatioText != null)
            {
                spellDesc += spellBoostRatioText;
            }

            //Emojis
            IGuild g = chnl.getGuild();
            String emojiArmor = g.getEmojiByName("gow_armor").toString();
            String emojiLife = g.getEmojiByName("gow_life").toString();
            String emojiAttack = g.getEmojiByName("gow_attack").toString();
            String emojiMagic = g.getEmojiByName("gow_magic").toString();
            
            GemColor[] gemColors = GemColor.fromInteger(heroClass.getColors());
            String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);
            
            String[] traitNames = traits.stream().map(t -> t.getName()).toArray(String[]::new);
            String[] perkNames = perks.stream().map(p -> p.getName() + " (" + p.getPerkType() + ")").toArray(String[]::new);
            
            String info = "";
            info += "_" + heroClass.getKingdomName() + "_ " + heroClass.getType() + "\n";
            info += "(" + String.join(", ", traitNames) + ")\n";
            info += "One of: " + String.join(", ", perkNames) + "\n\n";
            info += "**Class Weapon**";
            info += "\n" + heroClass.getSpellName() + " (" + heroClass.getSpellCost() + " " + String.join(" ", gemColorEmojis) + ")\n" + spellDesc;
            info += "\n";

            EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(heroClass.getName())
                .withUrl("http://ashtender.com/gems/classes/" + heroClass.getId())
                .withThumbnail("http://ashtender.com/gems/assets/classes/" + heroClass.getId() + ".png")
                .build();

            chnl.sendMessage("", o, false);
        }

        catch (IOException e)
        {
            chnl.sendMessage("Credentials file could not be found.");
            throw e;
        }
        catch (SQLException e2)
        {
            chnl.sendMessage("Database query failed.");
            throw e2;
        }
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified class.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?class [name]";
    }

    @Override
    public String getCommand()
    {
        return "class";
    }

    @Override
    public CommandType getCommandType()
    {
        return MOD;
    }
}
