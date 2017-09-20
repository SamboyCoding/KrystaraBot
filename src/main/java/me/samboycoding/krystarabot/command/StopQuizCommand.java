package me.samboycoding.krystarabot.command;

import me.samboycoding.krystarabot.Main;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

import static me.samboycoding.krystarabot.command.CommandType.MOD;

/**
 * Represents the ?reload-data command
 *
 * @author Sam
 */
public class StopQuizCommand extends KrystaraCommand
{

    public StopQuizCommand()
    {
        commandName = "stopquiz";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            chnl.sendMessage("You cannot do that!");
            return;
        }

        if (Main.quizH.isQuizRunning())
        {
            Main.quizH.abort();
            chnl.sendMessage("Stopped the quiz.");
        } else
        {
            chnl.sendMessage("No quiz is in progress.");
        }
    }

    @Override
    public String getHelpText()
    {
        return "Stops any in-progress quiz.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?stopquiz";
    }

    @Override
    public String getCommand()
    {
        return "stopquiz";
    }

    @Override
    public CommandType getCommandType()
    {
        return MOD;
    }
}
