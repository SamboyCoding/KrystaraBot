package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?codes command
 *
 * @author r3byass
 */
public class CodesCommand extends KrystaraCommand
{
    
    public CodesCommand()
    {
        commandName = "codes";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        /*if(!chnl.getID().equals(IDReference.BOTCOMMANDSCHANNEL) && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            sdr.getOrCreatePMChannel().sendMessage("To reduce spam, platform can only be used in the #bot-commands channel. Thanks!");
            return;
        }
        
        ArrayList<String> codes = main.codes.getLiveCodes();
        if (codes.isEmpty())
        {
            sdr.getOrCreatePMChannel().sendMessage("No codes are currently \"Alive\".");
            return;
        }
        msg.delete();
        sdr.getOrCreatePMChannel().sendMessage("Currently \"Alive\" codes: `" + codes.toString().replace("[", "").replace("]", "").replace("\"", "") + "`.");
        */
        
        sdr.getOrCreatePMChannel().sendMessage("As part of a recent update, `?codes` and `?dead` have been removed. This was MrSnake's decision as admin of the server, and you must now register your interest in receiving codes by doing `?coderegister yes`. Dead code alerts have been removed - they felt too cheaty. Thanks!");
        msg.delete();
    }

    @Override
    public String getHelpText()
    {
        return "Lists all the currently \"Alive\" codes.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?codes";
    }

    @Override
    public String getCommand()
    {
        return "codes";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
