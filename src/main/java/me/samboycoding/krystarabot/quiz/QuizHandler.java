package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import me.samboycoding.krystarabot.GameData;
import me.samboycoding.krystarabot.command.Top10Command;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.IDReference;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Handles the creation/destruction of a quiz, questions + answers, and scoring.
 *
 * @author Sam
 */
public class QuizHandler
{

    IChannel quizChannel = null;
    public static Thread quizThread = null;
    public static QuizQuestionTimer qt = null;

    public QuizQuestion currentQ = null;
    public QuizQuestion.Difficulty lastDifficulty = null;

    LinkedHashMap<IUser, Integer> unordered = new LinkedHashMap<>();
    Top10Command.ValueComparator comp = new Top10Command.ValueComparator((Map<IUser, Integer>) unordered);
    TreeMap<IUser, Integer> ordered = new TreeMap<>(comp);

    public QuizHandler()
    {
    }

    public QuizQuestionTimer.QuizSubmitResult submitAnswer(QuizQuestion question, IUser user, int answer)
    {
        synchronized (this)
        {
            if (qt.phase == QuizQuestionTimer.QuizPhase.Introduction)
            {
                return QuizQuestionTimer.QuizSubmitResult.TooEarly;
            } else
            {
                if ((qt.phase == QuizQuestionTimer.QuizPhase.Pausing) || (qt.phase == QuizQuestionTimer.QuizPhase.Completed)
                        || (qt.q != question))
                {
                    return QuizQuestionTimer.QuizSubmitResult.TooLate;
                }
            }

            boolean isFirst = true;
            boolean isCorrect = (question.getCorrectAnswerIndex() == answer);

            for (QuizQuestionTimer.QuizSubmitEntry entry : qt.submissions)
            {
                if (entry.user == user)
                {
                    return QuizQuestionTimer.QuizSubmitResult.AlreadyAnswered;
                }
                if (entry.result == QuizQuestionTimer.QuizSubmitResult.FirstCorrect)
                {
                    isFirst = false;
                }
            }

            QuizQuestionTimer.QuizSubmitResult result = QuizQuestionTimer.QuizSubmitResult.Incorrect;
            if (isCorrect)
            {
                result = isFirst ? QuizQuestionTimer.QuizSubmitResult.FirstCorrect : QuizQuestionTimer.QuizSubmitResult.Correct;
            }

            qt.submissions.add(new QuizQuestionTimer.QuizSubmitEntry(user, result));

            return result;
        }
    }

    public boolean isQuizRunning()
    {
        return qt != null || quizThread != null;
    }

    public IChannel getQuizChannel()
    {
        return quizChannel;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void initializeQuiz(IGuild srv, IUser sdr, IChannel source) throws Exception
    {
        if (isQuizRunning())
        {
            source.sendMessage("There is a quiz in progress.  Please wait for it to finish before starting a new quiz. You can join it here: " + quizChannel.mention());
            return;
        }

        List<IChannel> quizChannels = srv.getChannelsByName("quiz");
        if (quizChannels.isEmpty())
        {
            quizChannel = srv.createChannel("quiz");
        } else
        {
            quizChannel = quizChannels.get(0);
            quizChannel.delete();

            quizChannel = srv.createChannel("quiz");
        }

        //quizChannel is now set
        unordered = new LinkedHashMap<>();
        comp = new Top10Command.ValueComparator((Map<IUser, Integer>) unordered);
        ordered = new TreeMap<>(comp);

        unordered.put(sdr, new Integer(0));

        quizThread = new Thread(new QuizStartTimer(quizChannel), "Quiz start timer");
        quizThread.start();

        if (quizChannel != source)
        {
            srv.getChannelByID(IDReference.GLOBALCHANNEL).sendMessage("A new quiz is starting in " + quizChannel.mention() + "!  Enter the channel to join in.");
        }
    }

    public void finishQuestion()
    {
        currentQ = null;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void handleAnswer(IMessage msg) throws MissingPermissionsException, RateLimitException, DiscordException
    {
        QuizQuestion theQ = currentQ;

        if (!isQuizRunning())
        {
            msg.getAuthor().getOrCreatePMChannel().sendMessage("The quiz hasn't started yet, or has finished. Sorry!");
            msg.delete();
            return;
        }

        IUser usr = msg.getAuthor();
        String text = msg.getContent();
        IChannel c = msg.getChannel();

        msg.delete();

        int currentScore = 0;
        if (!unordered.containsKey(usr))
        {
            unordered.put(usr, currentScore);
        } else
        {
            currentScore = unordered.get(usr);
        }

        int answer = Integer.parseInt(text); //We can just do this as Listener checks if it is valid

        int pos = answer - 1;

        int scoreDelta = 0;

        switch (submitAnswer(theQ, usr, pos))
        {
            case Incorrect:
                break;
            case Correct:
                scoreDelta = lastDifficulty.getPoints();
                break;
            case FirstCorrect:
                scoreDelta = lastDifficulty.getPoints() + 2;
                break;
            case AlreadyAnswered:
                break;
            case TooEarly:
                break;
            case TooLate:
                break;
            default:
                break;
        }
        if (scoreDelta > 0)
        {
            //Correct
            currentScore += scoreDelta;
            unordered.remove(usr);
            unordered.put(usr, new Integer(currentScore));
        }
    }
}
