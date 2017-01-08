package me.samboycoding.krystarabot.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.BOTDEV;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.ImageUtils;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?buildcache command
 *
 * @author Sam
 */
public class BuildcacheCommand extends KrystaraCommand
{

    public BuildcacheCommand()
    {
        commandName = "buildcache";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            msg.delete(); //Delete silently.
            return;
        }
        File kingdomsDir = new File("images/kingdoms/");
        if (!kingdomsDir.isDirectory())
        {
            chnl.sendMessage("!!!!Kingdoms Directory is NOT a directory?!?!!!!");
            return;
        }
        File classesDir = new File("images/classes/");
        if (!classesDir.isDirectory())
        {
            chnl.sendMessage("!!!!Classes Directory is NOT a directory?!?!!!!");
            return;
        }

        int genCount = 0;
        long start = System.currentTimeMillis();

        JSONArray kingdoms = GameData.arrayKingdoms;
        JSONArray classes = GameData.arrayClasses;

        for (Iterator<Object> it = kingdoms.iterator(); it.hasNext();)
        {
            JSONObject kingdom = (JSONObject) it.next();

            String thisId = kingdom.getString("FileBase");

            if (kingdom.getString("BannerName").equals("Unnamed Banner"))
            {
                //No banner, just generate scaled.
                File raw = new File("images/kingdoms/" + thisId + ".png");
                File scaled = new File("images/kingdoms/" + thisId + "_scaled.png");
                if (scaled.exists())
                {
                    continue;
                }
                BufferedImage kingdomIcon = ImageUtils.scaleImage(0.5f, 0.5f, ImageIO.read(raw));
                ImageUtils.writeImageToFile(kingdomIcon, "png", scaled);

                genCount++;
            } else
            {
                File stitched = new File("images/kingdoms/" + thisId + "_stitched.png");
                if (stitched.exists())
                {
                    continue;
                }
                File left = new File("images/kingdoms/" + thisId + ".png");
                File right = new File("images/banner/" + thisId + ".png");

                if (!left.exists())
                {
                    main.logToBoth("Unable to generate image for kingdom " + kingdom.getString("Name") + " because the file: " + left.getName() + " doesn't exist!");
                    continue;
                }
                if (!right.exists())
                {
                    main.logToBoth("Unable to generate image for kingdom " + kingdom.getString("Name") + " because the file: " + left.getName() + " doesn't exist!");
                    continue;
                }
                ImageUtils.writeImageToFile(ImageUtils.scaleImage(0.5f, 0.5f, ImageUtils.joinHorizontal(left, right)), "png", stitched);

                genCount++;
            }
        }

        for (Iterator<Object> it2 = classes.iterator(); it2.hasNext();)
        {
            JSONObject hClass = (JSONObject) it2.next();
            String thisId = hClass.getString("Name").toLowerCase();

            File original = new File("images/classes/" + thisId + ".png");
            if (!original.exists())
            {
                main.logToBoth("Unable to generate image for class " + thisId + ", becuase the file: " + original.getName() + " doesn't exist!");
                continue;
            }

            File scaled = new File("images/classes/" + thisId + "_scaled.png");
            ImageUtils.writeImageToFile(ImageUtils.scaleImage(0.5f, 0.5f, ImageIO.read(original)), "png", scaled);

            genCount++;
        }

        if (genCount == 0)
        {
            chnl.sendMessage("Cache already populated.");
        } else
        {
            long time = System.currentTimeMillis() - start;
            float timeSeconds = (float) time / 1000f;
            float rate = (float) genCount / timeSeconds;

            chnl.sendMessage("Populated image cache. Generated " + genCount + " files, in " + time + " milliseconds, at a rate of " + rate + " image(s)/second.");
        }
        msg.delete();
    }

    @Override
    public String getHelpText()
    {
        return "Builds a cache of scaled/stitched images.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?buildcache";
    }

    @Override
    public String getCommand()
    {
        return "buildcache";
    }

    @Override
    public CommandType getCommandType()
    {
        return BOTDEV;
    }
}
