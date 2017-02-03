package me.samboycoding.krystarabot.command;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import me.samboycoding.krystarabot.Language;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.SummaryBase;
import me.samboycoding.krystarabot.gemdb.Search;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author r3byass
 */
public class SearchCommand extends KrystaraCommand
{

    public SearchCommand()
    {
        commandName = "search";
    }

    @SafeVarargs
    final String getSearchListsAsString(List<? extends SummaryBase>... lists)
    {
        ArrayList<SummaryBase> allResults = new ArrayList<>();
        for (List<? extends SummaryBase> list : lists)
        {
            allResults.addAll(list);
        }
        String[] allNames = allResults.stream().map(t -> t.getName()).toArray(String[]::new);
        return "(" + String.join(", ", allNames) + ")";
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

        String searchTerm = String.join(" ", arguments);

        if (searchTerm.length() < 4)
        {
            chnl.sendMessage(lang.localizeFormat(Language.LocString.TERM_IS_TOO_SHORT_FORMAT, 4));
            return;
        }

        chnl.setTypingStatus(true);

        String searchOutput = lang.localizeFormat(Language.LocString.SEARCH_RESULTS_FOR_TERM_FORMAT, searchTerm) + "\n\n";

        Search result = Search.fromQuery("all?term=" + URLEncoder.encode(searchTerm, "UTF-8"), lang);

        String[] troopNames = result.getTroops().stream().map(t -> "    - " + t.getName()).toArray(String[]::new);
        String[] traitNames = result.getTraits().stream().map(t -> "    - " + t.getName() + " " + getSearchListsAsString(t.getTroops(), t.getHeroClasses())).toArray(String[]::new);
        String[] spellNames = result.getSpells().stream().map(t -> "    - " + t.getName() + " " + getSearchListsAsString(t.getTroops(), t.getWeapons())).toArray(String[]::new);
        String[] kingdomNames = result.getKingdoms().stream().map(t -> "    - " + t.getName()).toArray(String[]::new);
        String[] heroClassNames = result.getHeroClasses().stream().map(t -> "    - " + t.getName()).toArray(String[]::new);
        String[] weaponNames = result.getWeapons().stream().map(t -> "    - " + t.getName()).toArray(String[]::new);
        if (troopNames.length > 0)
        {
            searchOutput += "**" + lang.localize(Language.LocString.CATEGORY_TROOPS) + "**:\n" + String.join("\n", troopNames) + "\n\n";
        }
        if (weaponNames.length > 0)
        {
            searchOutput += "**" + lang.localize(Language.LocString.CATEGORY_WEAPONS) + "**:\n" + String.join("\n", weaponNames) + "\n\n";
        }
        if (heroClassNames.length > 0)
        {
            searchOutput += "**" + lang.localize(Language.LocString.CATEGORY_HERO_CLASSES) + "**:\n" + String.join("\n", heroClassNames) + "\n\n";
        }
        if (kingdomNames.length > 0)
        {
            searchOutput += "**" + lang.localize(Language.LocString.CATEGORY_KINGDOMS) + "**:\n" + String.join("\n", kingdomNames) + "\n\n";
        }
        if (spellNames.length > 0)
        {
            searchOutput += "**" + lang.localize(Language.LocString.CATEGORY_SPELLS) + "**:\n" + String.join("\n", spellNames) + "\n\n";
        }
        if (traitNames.length > 0)
        {
            searchOutput += "**" + lang.localize(Language.LocString.CATEGORY_TRAITS) + "**:\n" + String.join("\n", traitNames) + "\n\n";
        }
        chnl.sendMessage(searchOutput);
        chnl.setTypingStatus(false);
    }

    @Override
    public String getHelpText()
    {
        return "Search for troops, traits, spells, hero classes or kingdoms containing the specified text.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?search [text]";
    }

    @Override
    public String getCommand()
    {
        return "search";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
