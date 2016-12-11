package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Random;
import me.samboycoding.krystarabot.quiz.LyyaQuestion;
import me.samboycoding.krystarabot.quiz.LyyaQuestionFactory;
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
        
        int reps = 1;
        LyyaQuestion.Difficulty difficulty = null;
        LyyaQuestionFactory.QuestionType type = null;
        
        if (arguments.size() > 0)
        {
            switch (arguments.get(0))
            {
                // Try to get the question as a difficulty group
                case "any":
                case "all":
                    break;
                    
                case "easy":
                   difficulty =  LyyaQuestion.Difficulty.Easy;
                   break;
                   
                case "moderate":
                   difficulty =  LyyaQuestion.Difficulty.Moderate;
                   break;
                   
                case "hard":
                   difficulty =  LyyaQuestion.Difficulty.Hard;
                   break;
                   
                default:
                    try
                    {
                        // Try to get the question as a string from the enum values
                        type = LyyaQuestionFactory.QuestionType.fromString(arguments.get(0));
                    }
                    catch (Exception e)
                    {
                        try
                        {
                            // Try to get the question as an integer as a last resort
                            int iType = Integer.parseInt(arguments.get(0));
                            type = LyyaQuestionFactory.QuestionType.fromInteger(iType);
                        }
                        catch (Exception e2)
                        {
                            chnl.sendMessage("Type argument must be in range (0-" + (LyyaQuestionFactory.QuestionType.Count-1) + ")");
                            return;
                        }
                    }
                    break;
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

            if (difficulty != null)
            {
                q = LyyaQuestionFactory.getQuestion(r, difficulty);
            }
            else if (type != null)
            {
                q = LyyaQuestionFactory.getQuestion(r, type);
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
        return "?question [difficulty/type (optional)] [amount (optional)] [seed (optional)]";
    }

    @Override
    public String getCommand()
    {
        return "question";
    }

}
