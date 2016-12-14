package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import me.samboycoding.krystarabot.GameData;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?quiz command
 * @author Sam
 */
public class QuizCommand extends KrystaraCommand {

    public QuizCommand()
    {
        commandName = "quiz";
    }
    
    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if(!GameData.dataLoaded)
        {
            chnl.sendMessage("Unfortunately, the data is loaded yet. Please try again later, and if the problem persists, ask a bot dev to do `?reload-data`. Thanks!");
            return;
        }
        main.quizH.initializeQuiz(chnl.getGuild(), sdr, chnl);
    }

    @Override
    public String getHelpText()
    {
        return "Starts a quiz.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return false;
    }

    @Override
    public CommandType getCommandType()
    {
        return GOW;
    }

    @Override
    public String getUsage()
    {
        return "?quiz";
    }

    @Override
    public String getCommand()
    {
        return "quiz";
    }

}
