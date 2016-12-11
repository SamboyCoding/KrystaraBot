package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Random;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.quiz.LyyaQuestion;
import me.samboycoding.krystarabot.quiz.LyyaQuestionFactory;
import me.samboycoding.krystarabot.quiz.Question;
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
        
        int qNum = -1;
        int reps = 1;
        
        if (arguments.size() > 0)
        {
            try
            {
                qNum = Integer.parseInt(arguments.get(0));
            } 
            catch (NumberFormatException e)
            {
                chnl.sendMessage("Type argument must be a whole number!");
                return;
            }
        }
        
        if (arguments.size() > 1)
        {
            try
            {
                reps = Math.min(10, Math.max(1, Integer.parseInt(arguments.get(1))));
            } 
            catch (NumberFormatException e)
            {
                chnl.sendMessage("Repeat count argument must be a whole number!");
                return;
            }
        }
        
        long seed = -1;
        if (arguments.size() > 2)
        {
            try
            {
                seed = Long.parseLong(arguments.get(2));
            }
            catch (NumberFormatException e)
            {
                chnl.sendMessage("Seed argument must be a whole number!");
                return;
            }
        }

        if (seed < 0)
        {
            seed = System.currentTimeMillis() % 10000;            
        }
        Random r = new Random(seed);

        ArrayList<String> messageStrings = new ArrayList<>();
        
        for (int rep = 0; rep < reps; rep++)
        {
            LyyaQuestion q;

            if (qNum >= 0)
            {
                q = LyyaQuestionFactory.getQuestion(r, LyyaQuestionFactory.QuestionType.fromInteger(qNum));
            }
            else 
            {
                q = LyyaQuestionFactory.getQuestion(r);
            }

            String answerString = "";
            for (int i = 0; i < LyyaQuestion.AnswerCount; i++)
            {
                String boldStr = (q.getCorrectAnswerIndex() == i) ? "**" : "";
                answerString += Integer.toString(i+1) + ") " + boldStr + q.getAnswerText(i) + boldStr + "\n";
            }
            
            messageStrings.add("Question: " + q.getQuestionText() + " (" + seed + ")\n" + answerString);
        }
 
        String finalString = String.join("\n\n", messageStrings);
        chnl.sendMessage(finalString);
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
