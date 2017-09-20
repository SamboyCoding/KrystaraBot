package me.samboycoding.krystarabot.command;

import me.samboycoding.krystarabot.Main;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

import static me.samboycoding.krystarabot.command.CommandType.GOW;

/**
 * Represents the ?quiz command
 *
 * @author Sam
 */
public class QuizCommand extends QuestionCommand
{

    public QuizCommand()
    {
        commandName = "quiz";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        synchronized (this)
        {
            Arguments args;

            if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
            {
                // Assume default quiz arguments
                args = new Arguments(10, -1, null, null);
            } else
            {
                // Allow admins to configure arguments
                args = parseArguments(arguments, chnl, 10);
            }

            if (args == null)
            {
                return;
            }

            Main.quizH.initializeQuiz(chnl.getGuild(), sdr, chnl, args.questionCount, args.difficulty, args.questionType, args.randomSeed);
        }
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
