package me.samboycoding.krystarabot.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.ImageUtils;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?kingdom command
 *
 * @author r3byass
 */
public class KingdomCommand extends KrystaraCommand
{
    
    public KingdomCommand()
    {
        commandName = "kingdom";
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
        String kingdomName = arguments.toString().replace("[", "").replace("]", "").replace(",", "");
        ArrayList<String> results = main.data.searchForKingdom(kingdomName);
        
        if(results.isEmpty())
        {
            chnl.sendMessage("No kingdom `" + kingdomName + "` found, " + sdr.mention());
            return;
        }
        if(results.size() > 5)
        {
            chnl.sendMessage("Search term is far too broad (" + results.size() + " results) - please refine it.");
            return;
        }
        if(results.size() > 1)
        {
            chnl.sendMessage("Search term \"" + kingdomName + "\" is too ambiguous. Possible results:\n\n\t\t-" + results.toString().replace("[", "").replace("]", "").replace(", ", "\n\t\t-") + "\n\nPlease refine the search term.");
            return;
        }
        
        JSONObject kingdomInfo = main.data.getKingdomInfo(results.get(0));

        kingdomName = kingdomInfo.getString("Name");
        int numTroops = kingdomInfo.getJSONArray("Troops").length();
        String bannerName = kingdomInfo.getString("BannerName");
        String bannerDesc = kingdomInfo.getString("BannerManaDescription");
        String bonus2 = kingdomInfo.getJSONObject("Bonus2").getString("Name");
        String bonus3 = kingdomInfo.getJSONObject("Bonus3").getString("Name");
        String bonus4 = kingdomInfo.getJSONObject("Bonus4").getString("Name");
        String bonus2Desc = kingdomInfo.getJSONObject("Bonus2").getString("Description");
        String bonus3Desc = kingdomInfo.getJSONObject("Bonus3").getString("Description");
        String bonus4Desc = kingdomInfo.getJSONObject("Bonus4").getString("Description");
        String level10Emoji = null;
        String traitstone = null;

        if (!kingdomInfo.isNull("LevelData"))
        {
            level10Emoji = chnl.getGuild().getEmojiByName("gow_" + kingdomInfo.getJSONObject("LevelData").getString("Stat")).toString();
            traitstone = kingdomInfo.getString("ExploreRuneName").replace("Traitstone", "");
        }

        String tributeText = "";

        if (!kingdomInfo.isNull("TributeData"))
        {
            int gloryAmount = kingdomInfo.getJSONObject("TributeData").getInt("Glory");
            int goldAmount = kingdomInfo.getJSONObject("TributeData").getInt("Gold");
            int soulsAmount = kingdomInfo.getJSONObject("TributeData").getInt("Souls");

            String goldEmoji = chnl.getGuild().getEmojiByName("gow_gold").toString();
            String soulsEmoji = chnl.getGuild().getEmojiByName("gow_soul").toString();
            String gloryEmoji = chnl.getGuild().getEmojiByName("gow_glory").toString();

            tributeText = "**Tribute**\n" + goldEmoji + " " + goldAmount + " " + soulsEmoji + " " + soulsAmount + " " + gloryEmoji + " " + gloryAmount;
        }

        String kingdomId = kingdomInfo.getString("FileBase");
        String troops = kingdomInfo.getJSONArray("Troops").toString().replace("[", "").replace("]", "").replace(",", ", ").replace("\"", "");

        if (bannerName.equals("Unnamed Banner"))
        {
            //No banner, do not stitch.
            File logo = new File("images/kingdoms/" + kingdomId + ".png");
            File scaled = new File("images/kingdoms/" + kingdomId + "_scaled.png");
            BufferedImage kingdomIcon = ImageUtils.scaleImage(0.5f, 0.5f, ImageIO.read(logo));
            ImageUtils.writeImageToFile(kingdomIcon, "png", scaled);
            chnl.sendFile(scaled);
            chnl.sendMessage("**" + kingdomName + "**\nTroops (" + numTroops + "): " + troops + "\n\n**Bonuses:**\n**x2:** " + bonus2 + " - " + bonus2Desc + "\n**x3:** " + bonus3 + " - " + bonus3Desc + "\n**x4:** " + bonus4 + " - " + bonus4Desc + "\n\n" + tributeText + (level10Emoji != null ? "\n\nKingdom level 10 grants +1 " + level10Emoji + " to all troops.\nExploration Traitstone: " + traitstone : ""));
        } else
        {
            File stitched = new File("images/kingdoms/" + kingdomId + "_stitched.png");
            File left = new File("images/kingdoms/" + kingdomId + ".png");
            File right = new File("images/banner/" + kingdomId + ".png");
            if (!stitched.exists())
            {
                ImageUtils.writeImageToFile(ImageUtils.scaleImage(0.5f, 0.5f, ImageUtils.joinHorizontal(left, right)), "png", stitched);
            }
            chnl.sendFile(stitched);
            String toSend = "**" + kingdomName + "**\nTroops (" + numTroops + "): " + troops + "\n" + bannerName + " - " + bannerDesc + "\n\n**Bonuses**\n**x2:** " + bonus2 + " - " + bonus2Desc + "\n**x3:** " + bonus3 + " - " + bonus3Desc + "\n**x4:** " + bonus4 + " - " + bonus4Desc + "\n\n" + tributeText + (level10Emoji != null ? "\n\nKingdom level 10 grants +1 " + level10Emoji + " to all troops.\nExploration Traitstone: " + traitstone : "");

            chnl.sendMessage(toSend);
        }
    }

    @Override
    public String getHelpText()
    {
        return "Shows information for the specified kingdom.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?kingdom [name]";
    }

    @Override
    public String getCommand()
    {
        return "kingdom";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
