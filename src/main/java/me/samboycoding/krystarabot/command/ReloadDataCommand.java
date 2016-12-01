package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import me.samboycoding.krystarabot.GameDataLoaderThread;
import static me.samboycoding.krystarabot.command.CommandType.BOTDEV;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?reload-data command
 *
 * @author Sam
 */
public class ReloadDataCommand extends KrystaraCommand
{
    
    public ReloadDataCommand()
    {
        commandName = "reload-data";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            chnl.sendMessage("You cannot do that!");
            return;
        }

        new Thread(new GameDataLoaderThread(chnl), "Game Data Reloader").start();
    }

    @Override
    public String getHelpText()
    {
        return "Reloads the internal data source for the lookup commands. NOT FOR USE BY NON-DEVS!";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?reload-data";
    }

    @Override
    public String getCommand()
    {
        return "reload-data";
    }

    @Override
    public CommandType getCommandType()
    {
        return BOTDEV;
    }
}
