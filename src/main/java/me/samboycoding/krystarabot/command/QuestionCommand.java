package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.quiz.Question;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author r3byass
 */
public class QuestionCommand extends KrystaraCommand
{
    
    public QuestionCommand()
    {
        commandName = "question";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            msg.delete();
            return;
        }

        if (arguments.size() < 2)
        {
            chnl.sendMessage("Insufficiant arguments! Check help.");
        }
        String difficulty = arguments.get(0);
        int qNum;
        try
        {
            qNum = Integer.parseInt(arguments.get(1));
        } catch (NumberFormatException e)
        {
            chnl.sendMessage("Second argument must be a whole number!");
            return;
        }

        int difNum;

        switch (difficulty.toLowerCase())
        {
            case "easy":
                difNum = 1;
                break;
            case "normal":
                difNum = 2;
                break;
            case "hard":
                difNum = 3;
                break;
            default:
                chnl.sendMessage("Invalid difficulty!");
                return;
        }

        Question q = main.quizH.getSpecificQuestion(difNum, qNum);
        
        if(q == null)
        {
            chnl.sendMessage("Invalid question number!");
            return;
        }
        
        chnl.sendMessage("Question: \"" + q.question + "\"\nPossible answers: " + q.answers.toString().replace("[", "").replace("]", "") + "\nCorrect answer position: " + q.correctAnswer);
    }

    @Override
    public String getHelpText()
    {
        return "Used to force the output of a specific question.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public CommandType getCommandType()
    {
        return CommandType.BOTDEV;
    }

    @Override
    public String getUsage()
    {
        return "?question [difficulty] [question #]";
    }

    @Override
    public String getCommand()
    {
        return "question";
    }

}
