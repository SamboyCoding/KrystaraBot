package me.samboycoding.krystarabot.command;

import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.ImageUtils;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?class command.
 *
 * @author Sam
 */
public class ClassCommand extends KrystaraCommand
{

    public ClassCommand()
    {
        commandName = "class";
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
            chnl.sendMessage("You need to specify a name to search for!");
            return;
        }
        
        String className = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
        ArrayList<String> results = main.data.searchForClass(className);
        
        if(results.isEmpty())
        {
            chnl.sendMessage("No hero class `" + className + "` found, " + sdr.mention());
            return;
        }
        if(results.size() > 5)
        {
            chnl.sendMessage("Search term is far too broad (" + results.size() + " results) - please refine it.");
            return;
        }
        if(results.size() > 1)
        {
            chnl.sendMessage("Search term \"" + className + "\" is too ambiguous. Possible results:\n\n\t\t-" + results.toString().replace("[", "").replace("]", "").replace(", ", "\n\t\t-") + "\n\nPlease refine the search term.");
            return;
        }
        
        JSONObject classInfo = main.data.getClassInfo(results.get(0));

        className = classInfo.getString("Name");
        String classKingdom = classInfo.getString("Kingdom");
        String classTrait1 = classInfo.getJSONArray("ParsedTraits").getJSONObject(0).getString("Name");
        String classTrait2 = classInfo.getJSONArray("ParsedTraits").getJSONObject(1).getString("Name");
        String classTrait3 = classInfo.getJSONArray("ParsedTraits").getJSONObject(2).getString("Name");
        String classAugment1 = classInfo.getJSONArray("Augment").getString(0);
        String classAugment2 = classInfo.getJSONArray("Augment").getString(1);
        String classAugment3 = classInfo.getJSONArray("Augment").getString(2);

        File classIcon = new File("images/classes/" + className.toLowerCase() + ".png");
        if (classIcon.exists())
        {
            File classIconShrunk = new File("images/classes/" + className.toLowerCase() + "_scaled.png");
            if (!classIconShrunk.exists())
            {
                ImageUtils.writeImageToFile(ImageUtils.scaleImage(0.5f, 0.5f, ImageIO.read(classIcon)), "png", classIconShrunk);
            }
            chnl.sendFile(classIconShrunk);
        }
        chnl.sendMessage("**" + className + "** (" + classKingdom + ")\nTraits: " + classTrait1 + ", " + classTrait2 + ", " + classTrait3 + "\nAugments: " + classAugment1 + ", " + classAugment2 + ", " + classAugment3);
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified hero class.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
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
