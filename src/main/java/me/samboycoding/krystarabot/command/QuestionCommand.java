package me.samboycoding.krystarabot.command;

import me.samboycoding.krystarabot.Main;
import me.samboycoding.krystarabot.quiz.QuizQuestion;
import me.samboycoding.krystarabot.quiz.QuizQuestionType;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.io.FilenameUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

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

    protected class Arguments
    {

        public final int questionCount;
        public final long randomSeed;
        public final QuizQuestion.Difficulty difficulty;
        public final QuizQuestionType questionType;

        public Arguments(int qCount, long rSeed, QuizQuestion.Difficulty d, QuizQuestionType type)
        {
            questionCount = qCount;
            randomSeed = rSeed;
            difficulty = d;
            questionType = type;
        }
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        if (!Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
        {
            msg.delete();
            return;
        }

        Arguments args = parseArguments(arguments, chnl, 1);
        if (args == null)
        {
            return;
        }

        long seed = args.randomSeed;
        Random r = new Random(seed);

        ArrayList<String> messageStrings = new ArrayList<>();
        URL lastImageUrl = null;

        QuizQuestion[] qs = null;
        if (args.difficulty != null)
        {
            qs = Main.quizQuestionFactory.getQuestions(args.questionCount, r, args.difficulty);
        } else
        {
            if (args.questionType != null)
            {
                qs = Main.quizQuestionFactory.getQuestions(args.questionCount, r, args.questionType);
            } else
            {
                qs = Main.quizQuestionFactory.getQuestions(args.questionCount, r);
            }
        }

        for (QuizQuestion q : qs)
        {
            URL imageUrl = null;

            imageUrl = q.getQuestionImageUrl();
            if (imageUrl != null)
            {
                lastImageUrl = imageUrl;
            }
            String answerString = "";
            for (int i = 0; i < QuizQuestion.ANSWER_COUNT; i++)
            {
                String boldStr = (q.getCorrectAnswerIndex() == i) ? "**" : "";
                answerString += Integer.toString(i + 1) + ") " + boldStr + q.getAnswerText(i) + boldStr + "\n";
            }

            String secondaryText = q.getQuestionSecondaryText();
            if (!secondaryText.isEmpty())
            {
                secondaryText += "\n";
            }
            messageStrings.add("Question: " + q.getQuestionText() + " (" + seed + ")\n" + secondaryText + answerString + "\n");

            seed = q.getRandomSeed();
        }

        Utilities.sendLargeMessage(chnl, messageStrings);

        if (lastImageUrl != null)
        {
            InputStream stream = lastImageUrl.openStream();
            String imageName = "image." + FilenameUtils.getExtension(lastImageUrl.getPath());
            chnl.sendFile("", false, stream, imageName);
        }
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

    protected Arguments parseArguments(ArrayList<String> arguments, IChannel chnl, int defaultCount) throws Exception
    {
        int questionCount = defaultCount;
        long seed = -1;
        QuizQuestion.Difficulty difficulty = null;
        QuizQuestionType type = null;

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
                    } catch (Exception e1)
                    {
                    }

                    if (difficulty == null)
                    {
                        try
                        {
                            // Try to get the question as a string from the enum values
                            type = QuizQuestionType.fromString(arguments.get(0));
                        } catch (Exception e2)
                        {
                        }
                    }

                    if ((difficulty == null) && (type == null))
                    {
                        try
                        {
                            // Try to get the question as an integer as a last resort
                            int iType = Integer.parseInt(arguments.get(0));
                            type = QuizQuestionType.fromInteger(iType);
                        } catch (Exception e3)
                        {
                        }
                    }

                    if ((difficulty == null) && (type == null))
                    {
                        ArrayList<String> difficultyStrings = new ArrayList<>();
                        ArrayList<String> typeStrings = new ArrayList<>();

                        for (int i = 0; i < QuizQuestion.Difficulty.Count; i++)
                        {
                            difficultyStrings.add(QuizQuestion.Difficulty.fromInteger(i).name());
                        }
                        for (int i = 0; i < QuizQuestionType.Count; i++)
                        {
                            typeStrings.add(QuizQuestionType.fromInteger(i).name());
                        }
                        chnl.sendMessage("Invalid question type.  Valid values are:"
                                + "\n**any, all:** Generate a question of any type"
                                + "\n**" + String.join(", ", difficultyStrings) + ":** Generate a question of the specified difficulty"
                                + "\n**0, 1, ..., " + (QuizQuestionType.Count - 1) + ":** Generate a question of the specified index"
                                + "\n**" + String.join(", ", typeStrings) + ":** Generate a question of the specified type");
                        return null;
                    }
                    break;
            }
        }

        if (arguments.size() > 1)
        {
            try
            {
                questionCount = Math.min(30, Math.max(1, Integer.parseInt(arguments.get(1))));
            } catch (NumberFormatException e)
            {
                chnl.sendMessage("Repeat count argument must be a whole number!");
                return null;
            }
        }

        if (arguments.size() > 2)
        {
            try
            {
                seed = Long.parseLong(arguments.get(2));
            } catch (NumberFormatException e)
            {
                chnl.sendMessage("Seed argument must be a whole number!");
                return null;
            }
        }

        if (seed < 0)
        {
            seed = System.currentTimeMillis() % 10000;
        }

        return new Arguments(questionCount, seed, difficulty, type);
    }
}
