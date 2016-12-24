package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.SERVER;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?platform command
 * @author r3byass
 */
public class LanguageCommand extends KrystaraCommand
{
    public LanguageCommand()
    {
        commandName = "language";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if(!chnl.getID().equals(IDReference.BOTCOMMANDSCHANNEL) && !Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            sdr.getOrCreatePMChannel().sendMessage("To reduce spam, 'language' can only be used in the #bot-commands channel. Thanks!");
            return;
        }
        
        String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
        
        if (arguments.size() < 1)
        {
            chnl.sendMessage("Please specify a language. [french|german|spanish]");
            return;
        }
        String lang = arguments.get(0).toLowerCase();
        switch(lang){
            case "french":
                sdr.addRole(chnl.getGuild().getRoleByID(IDReference.FRENCHROLE));
                chnl.sendMessage(sdr.mention() + ", you joined **French**");
                Utilities.logEvent(LogType.PLATFORMASSIGN, "**" + nameOfSender + "** assigned themselves to **French**");
                break;
            case "german":
                sdr.addRole(chnl.getGuild().getRoleByID(IDReference.GERMANROLE));
                chnl.sendMessage(sdr.mention() + ", you joined **German**");
                Utilities.logEvent(LogType.PLATFORMASSIGN, "**" + nameOfSender + "** assigned themselves to **German**");
                break;
            case "spanish":
                sdr.addRole(chnl.getGuild().getRoleByID(IDReference.SPANISHROLE));
                chnl.sendMessage(sdr.mention() + ", you joined **Spanish**");
                Utilities.logEvent(LogType.PLATFORMASSIGN, "**" + nameOfSender + "** assigned themselves to **Spanish**");
                break;
            default:
                chnl.sendMessage("Please enter a valid language. Valid languages are: \"French\", \"German\" or \"Spanish\".");
        }
    }

    @Override
    public String getHelpText()
    {
        return "Assigns you to a language and gives you acces to a language specific chat channel.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public String getUsage()
    {
        return "?language [french|german|spanish]";
    }

    @Override
    public String getCommand()
    {
        return "language";
    }

    @Override
    public CommandType getCommandType()
    {
        return SERVER;
    }
}
