package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.main;
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

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!GameData.dataLoaded)
        {
            chnl.sendMessage("Sorry, the data hasn't been loaded (yet). Please try again shortly, and if it still doesn't work, contact one of the bot devs.");
            return;
        }
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need to specify a search term!");
            return;
        }
        String searchTerm = arguments.toString().replace("[", "").replace("]", "").replace(",", "");

        if (searchTerm.length() < 4)
        {
            chnl.sendMessage("Search term must be at least 4 characters long.");
            return;
        }
        ArrayList<String> troopResults = new ArrayList<>();
        ArrayList<String> traitResults = new ArrayList<>();
        ArrayList<String> spellResults = new ArrayList<>();
        ArrayList<String> kingdomResults = new ArrayList<>();
        ArrayList<String> classResults = new ArrayList<>();

        troopResults.addAll(main.data.searchForTroop(searchTerm));
        traitResults.addAll(main.data.searchForTrait(searchTerm));
        spellResults.addAll(main.data.searchForSpell(searchTerm));
        kingdomResults.addAll(main.data.searchForKingdom(searchTerm));
        classResults.addAll(main.data.searchForClass(searchTerm));

        String troopRes = troopResults.isEmpty() ? "None" : troopResults.toString().replace("[", "").replace("]", "").replace("\"", "");
        String traitRes = traitResults.isEmpty() ? "None" : traitResults.toString().replace("[", "").replace("]", "").replace("\"", "");
        String spellRes = spellResults.isEmpty() ? "None" : spellResults.toString().replace("[", "").replace("]", "").replace("\"", "");
        String kingdomRes = kingdomResults.isEmpty() ? "None" : kingdomResults.toString().replace("[", "").replace("]", "").replace("\"", "");
        String classRes = classResults.isEmpty() ? "None" : classResults.toString().replace("[", "").replace("]", "").replace("\"", "");

        String searchOutput = "Search results for `" + searchTerm + "`:\n\n";
        if (!troopRes.equals("None"))
        {
            searchOutput += "**Troops**:\n" + troopRes + "\n\n";
        }
        if (!traitRes.equals("None"))
        {
            searchOutput += "**Traits**:\n" + traitRes + "\n\n";
        }
        if (!spellRes.equals("None"))
        {
            searchOutput += "**Spells**:\n" + spellRes + "\n\n";
        }
        if (!kingdomRes.equals("None"))
        {
            searchOutput += "**Kingdoms**:\n" + kingdomRes + "\n\n";
        }
        if (!classRes.equals("None"))
        {
            searchOutput += "**Hero Classes**:\n" + classRes + "\n\n";
        }

        chnl.sendMessage(searchOutput);
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
