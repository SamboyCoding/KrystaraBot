package me.samboycoding.krystarabot.command;

import java.time.ZoneId;
import java.util.ArrayList;
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
        long lagTime = ((Long) (System.currentTimeMillis() - msg.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        if (lagTime < 0)
        {
            lagTime = (long) Math.sqrt(lagTime * lagTime); //Makes it positive.
        }
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

}
