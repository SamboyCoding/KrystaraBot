package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import java.util.Random;
import me.samboycoding.krystarabot.quiz.QuizQuestion;
import me.samboycoding.krystarabot.quiz.QuizQuestionFactory;
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
        long seed = -1;
        QuizQuestion.Difficulty difficulty = null;
        QuizQuestionFactory.QuestionType type = null;
        
        if (arguments.size() > 0)
        {
            switch (arguments.get(0).toLowerCase())
            {
                // Try to get the question as a difficulty group
                case "any":
                case "all":
                    break;
                    
                default:
                    try
                    {
                        // Try to get the difficulty as a string from the enum values
                        difficulty = QuizQuestion.Difficulty.fromString(arguments.get(0));
                    }
                    catch (Exception e1)
                    {}
                    
                    if (difficulty == null)
                    {
                        try
                        {
                            // Try to get the question as a string from the enum values
                            type = QuizQuestionFactory.QuestionType.fromString(arguments.get(0));
                        }
                        catch (Exception e2)
                        {}
                    }
                    
                    if ((difficulty == null) && (type == null))
                    {
                        try
                        {
                            // Try to get the question as an integer as a last resort
                            int iType = Integer.parseInt(arguments.get(0));
                            type = QuizQuestionFactory.QuestionType.fromInteger(iType);
                        }
                        catch (Exception e3)
                        {}
                    }
                    
                    if ((difficulty == null) && (type == null))
                    {
                        ArrayList<String> difficultyStrings = new ArrayList<>();
                        ArrayList<String> typeStrings = new ArrayList<>();

                        for (int i = 0; i < QuizQuestion.Difficulty.Count; i++)
                        {
                            difficultyStrings.add(QuizQuestion.Difficulty.fromInteger(i).name());
                        }
                        for (int i = 0; i < QuizQuestionFactory.QuestionType.Count; i++)
                        {
                            typeStrings.add(QuizQuestionFactory.QuestionType.fromInteger(i).name());
                        }
                        chnl.sendMessage("Invalid question type.  Valid values are:" +
                                "\n**any, all:** Generate a question of any type" +
                                "\n**" + String.join(", ", difficultyStrings) + ":** Generate a question of the specified difficulty" +
                                "\n**0, 1, ..., " + (QuizQuestionFactory.QuestionType.Count-1) + ":** Generate a question of the specified index" +
                                "\n**" + String.join(", ", typeStrings) + ":** Generate a question of the specified type");
                        return;
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
            QuizQuestion q;

            if (difficulty != null)
            {
                q = QuizQuestionFactory.getQuestion(r, difficulty);
            }
            else if (type != null)
            {
                q = QuizQuestionFactory.getQuestion(r, type);
            }
            else 
            {
                q = QuizQuestionFactory.getQuestion(r);
            }

            String answerString = "";
            for (int i = 0; i < QuizQuestion.AnswerCount; i++)
            {
                String boldStr = (q.getCorrectAnswerIndex() == i) ? "**" : "";
                answerString += Integer.toString(i+1) + ") " + boldStr + q.getAnswerText(i) + boldStr + "\n";
            }
            
            String secondaryText = q.getQuestionSecondaryText();
            if (!secondaryText.isEmpty())
            {
                secondaryText += "\n";
            }
            messageStrings.add("Question: " + q.getQuestionText() + " (" + seed + ")\n" + secondaryText + answerString);
            
            seed = Utilities.getSeed(r);
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
