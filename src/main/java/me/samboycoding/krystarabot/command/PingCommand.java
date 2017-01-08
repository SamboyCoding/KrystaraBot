package me.samboycoding.krystarabot.command;

import java.time.ZoneId;
import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.SERVER;
import me.samboycoding.krystarabot.main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?ping command
 *
 * @author Sam
 */
public class PingCommand extends KrystaraCommand
{

    public PingCommand()
    {
        commandName = "ping";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        msg.delete();
        long lagTime = ((Long) (msg.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()));
        /*if (lagTime < 0)
        {
            main.logToBoth("Negative ping time?! Flipping.... Was: " + lagTime);
            lagTime = (long) Math.sqrt(lagTime * lagTime); //Makes it positive.
            main.logToBoth("Is: " + lagTime);
        }*/
        main.logToBoth("Pong!");
        chnl.sendMessage("Pong! `" + lagTime + "ms lag`.");
    }

    @Override
    public String getHelpText()
    {
        return "Check if the bot is able to respond to commands.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?ping";
    }

    @Override
    public String getCommand()
    {
        return "ping";
    }

    @Override
    public CommandType getCommandType()
    {
        return SERVER;
    }
}
