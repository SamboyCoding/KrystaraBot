/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author r3byass
 */
public class BanCommand extends KrystaraCommand
{
    public BanCommand()
    {
        commandName = "ban";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()).isPresent() ? sdr.getNicknameForGuild(msg.getGuild()).get() : sdr.getName();
        
        if (arguments.size() < 1)
        {
            chnl.sendMessage("You need an @mention of a user to ban!");
            return;
        }
        if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            String id = arguments.get(0).replace("<@", "").replace("!", "").replace(">", "");
            IUser usr = chnl.getGuild().getUserByID(id);
            
            if (usr == null)
            {
                chnl.sendMessage("Invaild @mention!");
                return;
            }
            
            String nameOfUser = usr.getNicknameForGuild(msg.getGuild()).isPresent() ? usr.getNicknameForGuild(msg.getGuild()).get() : usr.getName();
            
            chnl.getGuild().banUser(usr);
            
            Utilities.logEvent(LogType.BAN, "**" + nameOfSender + "** banned user **" + nameOfUser + "**");
            chnl.sendMessage("User \"" + nameOfUser + "\" banned.");
            msg.delete();
        } else
        {
            chnl.sendMessage("You cannot do that!");
        }
    }

    @Override
    public String getHelpText()
    {
        return "Bans the specified server from the server.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?ban [@user]";
    }

    @Override
    public String getCommand()
    {
        return "ban";
    }

    @Override
    public CommandType getCommandType()
    {
        return MOD;
    }

}
