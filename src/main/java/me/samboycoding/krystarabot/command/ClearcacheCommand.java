package me.samboycoding.krystarabot.command;

import java.io.File;
import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.BOTDEV;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?clearcache command
 *
 * @author Sam
 */
public class ClearcacheCommand extends KrystaraCommand
{

    public ClearcacheCommand()
    {
        commandName = "clearcache";
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

        int count = 0;
        long start = System.currentTimeMillis();
        for (File f : kingdomsDir.listFiles())
        {
            if (f.getName().contains("stitched") || f.getName().contains("scaled"))
            {
                f.delete();
                count++;
            }
        }
        for (File f : classesDir.listFiles())
        {
            if (f.getName().contains("scaled"))
            {
                f.delete();
                count++;
            }
        }
        if (count == 0)
        {
            chnl.sendMessage("Cache already empty.");
        } else
        {
            long time = System.currentTimeMillis() - start;
            System.out.println(time);
            float rate = (float) count / (float) time;

            chnl.sendMessage("Cleared image cache. Removed " + count + " files that were stitched or scaled, leaving only the raw files, in " + time + " milliseconds, at a rate of " + rate + " image(s)/millisecond.");
        }
        msg.delete();
    }

    @Override
    public String getHelpText()
    {
        return "Clears cached scales/stitched images. NOT FOR USE BY NON-DEVS!";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?clearcache";
    }

    @Override
    public String getCommand()
    {
        return "clearcache";
    }

    @Override
    public CommandType getCommandType()
    {
        return BOTDEV;
    }
}
