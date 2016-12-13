package me.samboycoding.krystarabot.quiz;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import me.samboycoding.krystarabot.command.Top10Command;
import me.samboycoding.krystarabot.utilities.IDReference;
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
    public void initializeQuiz(IGuild srv, IUser sdr, IChannel source,
            int questionCount, QuizQuestion.Difficulty difficulty, QuizQuestionFactory.QuestionType questionType, long randomSeed) throws Exception
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
            quizQuestionTimer = new QuizQuestionTimer(this, quizChannel, questionCount, 10,
                difficulty, questionType, randomSeed);
            quizThread = new Thread(quizQuestionTimer, "Quiz question timer");
            quizThread.start();
        }

        IChannel globalChannel = srv.getChannelByID(IDReference.GLOBALCHANNEL);
        String quizAnnounceText = "A new quiz is starting in " + quizChannel.mention() + "!  Enter the channel to join in.";
        
        globalChannel.sendMessage(quizAnnounceText);
        if (source != globalChannel)
        {
            source.sendMessage(quizAnnounceText);
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
        String text = msg.getContent().trim();

        int answer = -1;
        if (text.length() == 1)
        {
            char answerChar = text.charAt(0);
            answer = (answerChar - '0') - 1;
        }

        if (answer < 0 || answer >= QuizQuestion.AnswerCount)
        {
            timer.addChatterMessage(msg);
            return;
        }
        
        msg.delete();

        int currentScore = 0;
        if (!unordered.containsKey(usr))
        {
            unordered.put(usr, currentScore);
        } else
        {
            currentScore = unordered.get(usr);
        }

        int scoreDelta = 0;

        switch (timer.submitAnswer(theQ, usr, answer))
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
