package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?newcode command
 *
 * @author r3byass
 */
public class NewcodeCommand extends KrystaraCommand
{
    public NewcodeCommand()
    {
        commandName = "newcode";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if(!chnl.getID().equals(IDReference.BOTCOMMANDSCHANNEL) && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            sdr.getOrCreatePMChannel().sendMessage("To reduce spam, newcode can only be used in the #bot-commands channel. Thanks!");
            return;
        }
        
        String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You have to enter a code first!");
            return;
        }
        String code = arguments.get(0);
        if (code.length() == 10)
        {
            if (main.codes.isCodePresent(code))
            {
                chnl.sendMessage("That code is already present!");
                return;
            }
            main.codes.addCode(code);
            Utilities.logEvent(LogType.NEWCODE, "**" + nameOfSender + "** posted '" + code + "' as new code.");
            
            for(IUser usr : chnl.getGuild().getUsers())
            {
                if(main.databaseHandler.getReceivesCodes(chnl.getGuild(), usr))
                {
                    usr.getOrCreatePMChannel().sendMessage("New code: `" + code + "`");
                }
            }
        } else
        {
            chnl.sendMessage("Please check your code - it has to be 10 characters long, and yours is " + code.length() + "!");
        }
    }

    @Override
    public String getHelpText()
    {
        return "Post a new code into the #codes channel.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?newcode [code]";
    }

    @Override
    public String getCommand()
    {
        return "newcode";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
