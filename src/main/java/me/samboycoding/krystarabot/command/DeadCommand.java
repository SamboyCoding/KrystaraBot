package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?dead command
 *
 * @author r3byass
 */
public class DeadCommand extends KrystaraCommand
{

    public DeadCommand()
    {
        commandName = "dead";
    }
    
    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        /*String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
        
        String skullEmoji = chnl.getGuild().getEmojiByName("gow_skull").toString();
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You have to enter a code first!");
            return;
        }
        String code2 = arguments.get(0);
        if (code2.length() == 10)
        {
            if (!main.codes.isCodePresent(code2))
            {
                chnl.sendMessage("No code `" + code2 + "` found!");
                return;
            }
            if (main.codes.isCodeDead(code2))
            {
                chnl.sendMessage("That code is already marked as dead!");
                return;
            }
            main.codes.makeCodeDead(code2);
            chnl.getGuild().getChannelByID(IDReference.CODESCHANNEL).sendMessage(skullEmoji + " Code `" + arguments.get(0).toUpperCase() + "` is dead! " + skullEmoji);
            chnl.getGuild().getChannelByID(IDReference.LOGSCHANNEL).sendMessage("**[DEAD CODE]** - **" + nameOfSender + "** reported '" + code2 + "' as dead.");
        } else
        {
            chnl.sendMessage("Please check your code - it has to be 10 characters long, and yours is " + code2.length() + "!");
        }
        */
        
        sdr.getOrCreatePMChannel().sendMessage("As part of a recent update, `?codes` and `?dead` have been removed. This was MrSnake's decision as admin of the server, and you must now register your interest in receiving codes by doing `?coderegister yes`. Dead code alerts have been removed - they felt too cheaty. Thanks!");
        msg.delete();
    }

    @Override
    public String getHelpText()
    {
        return "Report a code as dead in the #codes channel.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?dead [code]";
    }

    @Override
    public String getCommand()
    {
        return "dead";
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }
}
