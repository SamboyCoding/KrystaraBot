package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import me.samboycoding.krystarabot.Language;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.GemColor;
import me.samboycoding.krystarabot.gemdb.HeroClass;
import me.samboycoding.krystarabot.gemdb.Search;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class ClassCommand extends QuestionCommand
{

    public ClassCommand()
    {
        commandName = "class";
    }

    @Override
    public Boolean isLocalized()
    {
        return true;
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        handleCommand(sdr, chnl, msg, arguments, argsFull, Language.ENGLISH);
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull, Language lang) throws Exception
    {
        if (arguments.size() < 1)
        {
            chnl.sendMessage(lang.localize(Language.LocString.PLEASE_SPECIFY_TERM_TO_SEARCH));
            return;
        }

        String heroClassName = String.join(" ", arguments);
        Search search = Search.fromQuery("classes?term=" + URLEncoder.encode(heroClassName, "UTF-8"), lang);
        HeroClass.Summary heroClassSummary = AshClient.getSingleResult(chnl, search.getHeroClasses(), heroClassName, lang);
        if (heroClassSummary == null)
        {
            return;
        }

        HeroClass heroClass = heroClassSummary.getDetails(lang);
        String spellDesc = heroClass.getWeapon().getSpellDescription();
        String spellMagicScalingText = heroClass.getWeapon().getSpellMagicScalingText();
        if (spellMagicScalingText != null)
        {
            if (!spellDesc.contains("{2}"))
            {
                spellDesc = spellDesc.replace("{1}", spellMagicScalingText);
            } else
            {
                spellDesc = spellDesc.replace("{1}", "(" + lang.localize(Language.LocString.MAGIC_SCALING_HALF) + ")");
                spellDesc = spellDesc.replace("{2}", spellMagicScalingText);
            }
        }
        String spellBoostRatioText = heroClass.getWeapon().getSpellBoostRatioText();
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

        GemColor[] gemColors = GemColor.fromInteger(heroClass.getWeapon().getColors());
        String[] gemColorEmojis = Arrays.stream(gemColors).map(c -> g.getEmojiByName(c.emoji).toString()).toArray(String[]::new);

        String[] traitNames = heroClass.getTraits().stream().map(t -> t.getName()).toArray(String[]::new);
        String[] perkNames = heroClass.getPerks().stream().map(p -> p.getName() + " (" + p.getPerkType() + ")").toArray(String[]::new);

        String info = "";
        info += "_" + heroClass.getKingdomName() + "_ " + heroClass.getType() + "\n";
        info += "(" + String.join(", ", traitNames) + ")\n";
        info += lang.localize(Language.LocString.ONE_OF) + " " + String.join(", ", perkNames) + "\n\n";
        info += "**" + lang.localize(Language.LocString.CLASS_WEAPON) + "**";
        info += "\n" + heroClass.getWeapon().getName() + " (" + heroClass.getWeapon().getSpellCost() + " " + String.join(" ", gemColorEmojis) + ")\n" + spellDesc;
        info += "\n";

        EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(heroClass.getName())
                .withUrl(heroClass.getPageUrl())
                .withThumbnail(heroClass.getImageUrl())
                .build();

        chnl.sendMessage("", o, false);
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
        return GOW;
    }
}
