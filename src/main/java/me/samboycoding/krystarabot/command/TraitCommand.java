package me.samboycoding.krystarabot.command;

import java.net.URLEncoder;
import java.util.ArrayList;
import me.samboycoding.krystarabot.Language;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.gemdb.AshClient;
import me.samboycoding.krystarabot.gemdb.Search;
import me.samboycoding.krystarabot.gemdb.Trait;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Represents the ?trait command
 *
 * @author Sam
 */
public class TraitCommand extends KrystaraCommand
{

    public TraitCommand()
    {
        commandName = "trait";
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

        String traitName = String.join(" ", arguments);
        Search search = Search.fromQuery("traits?term=" + URLEncoder.encode(traitName, "UTF-8"), lang);
        Search.TraitSummary traitSummary = AshClient.getSingleResult(chnl, search.getTraits(), traitName, lang);
        if (traitSummary == null)
        {
            return;
        }

        Trait trait = traitSummary.getDetails(lang);
        String info = trait.getDescription() + "\n";
        if (!trait.getTroops().isEmpty())
        {
            String[] troopNames = trait.getTroops().stream().map(t -> t.getName()).toArray(String[]::new);
            info += lang.localize(Language.LocString.USED_BY_TROOPS) + " " + String.join(", ", troopNames) + "\n";
        }
        if (!trait.getHeroClasses().isEmpty())
        {
            String[] heroClassNames = trait.getHeroClasses().stream().map(c -> c.getName()).toArray(String[]::new);
            info += lang.localize(Language.LocString.USED_BY_CLASSES) + " " + String.join(", ", heroClassNames) + "\n";
        }

        EmbedObject o = new EmbedBuilder()
                .withDesc(info)
                .withTitle(trait.getName())
                .build();
        chnl.sendMessage("", o, false);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified trait.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?trait [name]";
    }

    @Override
    public String getCommand()
    {
        return "trait";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }

}
