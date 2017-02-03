/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import me.samboycoding.krystarabot.Language;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.HeroClass;
import me.samboycoding.krystarabot.gemdb.Search;
import me.samboycoding.krystarabot.gemdb.SummaryBase;
import me.samboycoding.krystarabot.gemdb.Traitable;
import me.samboycoding.krystarabot.gemdb.Troop;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class TraitsCommand extends QuestionCommand
{

    public TraitsCommand()
    {
        commandName = "traits";

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

        String troopName = String.join(" ", arguments);
        Search search = Search.fromQuery("all?term=" + URLEncoder.encode(troopName, "UTF-8"), lang);
        ArrayList<SummaryBase> searchResults = new ArrayList<>();
        searchResults.addAll(search.getTroops());
        searchResults.addAll(search.getHeroClasses());
        SummaryBase traitableSummary = AshClient.getSingleResult(chnl, searchResults, troopName, lang);
        if (traitableSummary == null)
        {
            return;
        }

        Traitable traitable;
        if (traitableSummary instanceof HeroClass.Summary)
        {
            traitable = ((HeroClass.Summary) traitableSummary).getDetails(lang);
        } else
        {
            traitable = ((Troop.Summary) traitableSummary).getDetails(lang);
        }

        String info = "";
        for (Traitable.TraitSummary trait : traitable.getTraits())
        {
            info += getTraitText(trait) + "\n\n";
        }

        EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(traitable.getName())
                .withUrl(traitable.getPageUrl())
                .withThumbnail(traitable.getImageUrl())
                .build();
        chnl.sendMessage("", o, false);
    }

    private String getTraitText(Traitable.TraitSummary trait)
    {
        String text = "**" + trait.getName() + ":** " + trait.getDescription() + "\n";
        String[] costs = trait.getCosts().stream().map(c -> "    - " + c.getName() + ": " + c.getCount()).toArray(String[]::new);
        text += String.join("\n", costs);
        return text;
    }

    @Override
    public String getHelpText()
    {
        return "Shows detailed trait information for the specified troop.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?traits [name]";
    }

    @Override
    public String getCommand()
    {
        return "traits";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
