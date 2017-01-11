package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Arrays;
import static me.samboycoding.krystarabot.command.CommandType.GOW;
import me.samboycoding.krystarabot.quiz.QuizQuestion;
import me.samboycoding.krystarabot.quiz.QuizQuestionType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents the ?listquestions command
 *
 * @author Sam
 */
public class ListQuestionsCommand extends KrystaraCommand
{

    public ListQuestionsCommand()
    {
        commandName = "listquestions";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        ArrayList<QuizQuestionType> qTypes = new ArrayList<>(Arrays.asList(QuizQuestionType.values()));

        String questionTypes = "";

        int num = 0;
        for (QuizQuestionType qt : qTypes)
        {
            if (qt.difficulty != QuizQuestion.Difficulty.Unused)
            {
                num++;
                questionTypes += "\n\t" + num + ") " + qt.description + " (" + qt.difficulty + ")";
            }
        }

        questionTypes = "There are " + num + " questions defined. Full List (in no particular order): \n" + questionTypes;
        sdr.getOrCreatePMChannel().sendMessage(questionTypes);
    }

    @Override
    public String getHelpText()
    {
        return "Lists all the defined quiz questions.";
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
        return "?listquestions";
    }

    @Override
    public String getCommand()
    {
        return "listquestions";
    }

}
