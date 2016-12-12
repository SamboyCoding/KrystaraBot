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

    private IChannel quizChannel = null;
    private Thread quizThread = null;
    private QuizStartTimer quizStartTimer = null;
    private QuizQuestionTimer quizQuestionTimer = null;

    private QuizQuestion currentQ = null;
    private QuizQuestion.Difficulty currentDifficulty = null;

    LinkedHashMap<IUser, Integer> unordered = new LinkedHashMap<>();
    Top10Command.ValueComparator comp = new Top10Command.ValueComparator((Map<IUser, Integer>) unordered);
    TreeMap<IUser, Integer> ordered = new TreeMap<>(comp);

    public QuizHandler()
    {
    }

    public boolean isQuizRunning()
    {
        return (quizThread != null);
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

        synchronized (this)
        {
            quizStartTimer = new QuizStartTimer(this, quizChannel);
            quizThread = new Thread(quizStartTimer, "Quiz start timer");
            quizThread.start();
        }

        if (quizChannel != source)
        {
            srv.getChannelByID(IDReference.GLOBALCHANNEL).sendMessage("A new quiz is starting in " + quizChannel.mention() + "!  Enter the channel to join in.");
        }
    }
    
    public void handleStartTimerComplete()
    {
        synchronized (this)
        {
            quizQuestionTimer = new QuizQuestionTimer(this, quizChannel);
            quizThread = new Thread(quizQuestionTimer, "Quiz question timer");
            quizThread.start();
        }
    }
    
    public void handleQuestionTimerComplete()
    {
        synchronized (this)
        {
            quizThread = null;
        }
    }
    
    public void abort()
    {
        Thread runningThread = null;
        
        synchronized (this)
        {
            if (quizStartTimer != null)
            {
                quizStartTimer.abort();
            }
            if (quizQuestionTimer != null)
            {
                quizQuestionTimer.abort();
            }
            runningThread = quizThread;
        }
        
        if (runningThread != null)
        {
            try
            {
                synchronized (runningThread)
                {
                    runningThread.wait();
                }
            }
            catch (InterruptedException e)
            {}
        }
        
        synchronized (this)
        {
            quizStartTimer = null;
            quizQuestionTimer = null;
            quizThread = null;
        }
    }
    
    public void setQuestion(QuizQuestion q, QuizQuestion.Difficulty difficulty)
    {
        synchronized (this)
        {
            currentQ = q;
            currentDifficulty = difficulty;
        }
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void handleAnswer(IMessage msg) throws MissingPermissionsException, RateLimitException, DiscordException
    {
        QuizQuestion theQ;
        QuizQuestionTimer timer;

        synchronized (this)
        {
            theQ = currentQ;
            timer = quizQuestionTimer;
        }

        if (timer == null)
        {
            msg.delete();
            return;
        }

        IUser usr = msg.getAuthor();
        String text = msg.getContent();

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

        switch (timer.submitAnswer(theQ, usr, pos))
        {
            case Incorrect:
                break;
            case Correct:
                scoreDelta = currentDifficulty.getPoints();
                break;
            case FirstCorrect:
                scoreDelta = currentDifficulty.getPoints() + 1;
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
