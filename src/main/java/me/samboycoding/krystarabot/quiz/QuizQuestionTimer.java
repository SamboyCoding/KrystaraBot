package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import me.samboycoding.krystarabot.main;
import static me.samboycoding.krystarabot.quiz.QuizQuestion.Difficulty.Easy;
import static me.samboycoding.krystarabot.quiz.QuizQuestion.Difficulty.Hard;
import static me.samboycoding.krystarabot.quiz.QuizQuestion.Difficulty.Moderate;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * The thread that handles the asking/answering of questions
 *
 * @author Sam
 */
public class QuizQuestionTimer implements Runnable
{

    IChannel chnl;

    private QuizQuestion q;
    private QuizPhase phase;

    private String quizLog = "";
    private boolean isAborted = false;
    private final Object abortSignal = new Object();
    private final int qCount;
    private final int qTimeSeconds;
    
    public enum QuizPhase
    {
        Introduction,
        Pausing,
        WaitingForAnswers,
        Completed
    }

    public enum QuizSubmitResult
    {
        Incorrect,
        Correct,
        FirstCorrect,
        AlreadyAnswered,
        TooEarly,
        TooLate
    }

    public static class QuizSubmitEntry
    {

        IUser user;
        QuizSubmitResult result;

        public QuizSubmitEntry(IUser u, QuizSubmitResult r)
        {
            user = u;
            result = r;
        }
    }

    private ArrayList<QuizSubmitEntry> submissions = new ArrayList<>();

    private IMessage msg;
    private QuizHandler qh;

    public QuizQuestionTimer(QuizHandler handler, IChannel c, int questionCount, int questionTimeInSeconds)
    {
        chnl = c;
        phase = QuizPhase.Introduction;
        qh = handler;
        qCount = questionCount;
        qTimeSeconds = questionTimeInSeconds;
    }
    
    private static class QuestionLogEntry
    {
        public final QuizQuestion.Difficulty difficulty;
        public final long seed;
        
        public QuestionLogEntry(QuizQuestion.Difficulty d, long s)
        {
            difficulty = d;
            seed = s;
        }
    }
    
    private IMessage sendCountdownMessage(String formatString, int seconds) 
            throws MissingPermissionsException, RateLimitException, DiscordException, InterruptedException
    {
        String messageString = String.format(formatString, seconds);
        IMessage msg = chnl.sendMessage(messageString);
        while (seconds > 0)
        {
            sleepFor(1000);
            seconds--;
            
            try
            {
                messageString = String.format(formatString, seconds);
                msg.edit(messageString);
            } catch(RateLimitException e)
            {
                sleepFor(500);
                //Ignore and move on, with an extra 1/2 second wait.
            }
        }
        return msg;
    }
    
    @Override
    public void run()
    {
        try
        {
            IMessage msg = sendCountdownMessage("Quiz will start in %1$d seconds...", 10);
            msg.delete();

            chnl.sendMessage("Welcome to the GoW Discord quiz!");
            sleepFor(2500);

            chnl.sendMessage("You will be asked " + qCount + " questions, and will have " + qTimeSeconds + 
                    " seconds to answer each question.");
            sleepFor(2500);

            chnl.sendMessage("Submit only the _number_ of the answer you think is correct (1-" + 
                    QuizQuestion.AnswerCount + "). All other answers will not be counted.");
            sleepFor(2500);

            chnl.sendMessage("The person with the most points after " + qCount + " questions wins!\n\n" + 
                    Utilities.repeatString("-", 40));
            sleepFor(2000);
            
            int curQuestionIndex = 0;

            ArrayList<QuizQuestion.Difficulty> questionDifficulties = new ArrayList<>();
            
            for (int i = 0; i < qCount / 3; i++)
            {
                questionDifficulties.add(Easy);
            }
            for (int i = 0; i < qCount / 3; i++)
            {
                questionDifficulties.add(Hard);
            }
            while (questionDifficulties.size() < qCount)
            {
                questionDifficulties.add(Moderate);
            }
            java.util.Collections.shuffle(questionDifficulties);

            ArrayList<QuestionLogEntry> questionLog = new ArrayList<>();
            
            while (questionDifficulties.size() > 0)
            {
                String formatString = "Next question: %1$d seconds";
                IMessage timer = sendCountdownMessage(formatString, 10);
                if (msg != null)
                {
                    msg.delete();
                }

                curQuestionIndex++;
                synchronized (this)
                {
                    submissions = new ArrayList<>();
                }
                String toSend = "**Question #" + curQuestionIndex + ":**\n\n";

                QuizQuestion.Difficulty difficulty = questionDifficulties.remove(0);

                QuizQuestion question;

                Random questionSeed = new Random();
                long seed = Utilities.getSeed(questionSeed);

                synchronized (this)
                {
                    q = QuizQuestionFactory.getQuestion(questionSeed, difficulty);
                    question = q;
                    qh.setQuestion(q, difficulty);
                }

                String pointString = getPointString(difficulty, false);

                questionLog.add(new QuestionLogEntry(difficulty, seed));

                toSend += question.getQuestionText() + " (" + pointString + ")\n";
                toSend += "1) " + question.getAnswerText(0) + "\n";
                toSend += "2) " + question.getAnswerText(1) + "\n";
                toSend += "3) " + question.getAnswerText(2) + "\n";
                toSend += "4) " + question.getAnswerText(3) + "\n\n";

                quizLog += toSend;
                timer.delete();

                msg = chnl.sendMessage(toSend);

                synchronized (this)
                {
                    phase = QuizPhase.WaitingForAnswers;
                }

                formatString = "Time remaining: %1$d seconds";
                timer = sendCountdownMessage(formatString, qTimeSeconds);

                synchronized (this)
                {
                    phase = QuizPhase.Pausing;
                }

                timer.delete();

                int pos = question.getCorrectAnswerIndex();
                String number = Integer.toString(pos + 1);

                toSend = "**Question #" + curQuestionIndex + ":**\n\nThe correct answer was: "
                        + "\n\n" + number + ") **" + question.getAnswerText(question.getCorrectAnswerIndex())
                        + "**\n\n" + getCorrectUserText(difficulty)
                        + "\n" + Utilities.repeatString("-", 40);

                quizLog += toSend + "\n";

                msg.delete();

                msg = chnl.sendMessage(toSend);
            }

            synchronized (this)
            {
                phase = QuizPhase.Completed;
            }

            String formatString = "Results: %1$d seconds";
            IMessage timer = sendCountdownMessage(formatString, 10);
            msg.delete();
            timer.delete();

            chnl.sendMessage("...\n\n\n\nQuiz Log: ");

            String txt = (quizLog + "\n\nThe quiz is over! Thanks for playing! And the winner is...").trim();

            final int CHUNK_SIZE = 2000;
            
            while (txt.length() > CHUNK_SIZE)
            {
                String chunk = txt.substring(0, CHUNK_SIZE);
                int lastQuestionBreak = chunk.lastIndexOf("--");
                if (lastQuestionBreak < 0)
                {
                    break;
                }
                
                String toSend = txt.substring(0, lastQuestionBreak + 1).trim();
                if (toSend.length() > 0)
                {
                    sleepFor(1000);
                    chnl.sendMessage(toSend);
                }

                txt = txt.substring(lastQuestionBreak + 2).trim();
            }
            
            if (txt.length() > 0)
            {
                sleepFor(1000);
                chnl.sendMessage(txt);
            }

            sleepFor(2500);

            main.quizH.ordered.putAll(main.quizH.unordered); //Sort.

            String scores = "```\nName" + Utilities.repeatString(" ", 46) + "Score"
                    + "\n" + Utilities.repeatString("-", 80);
            int numDone = 0;
            for (IUser u : main.quizH.ordered.descendingKeySet())
            {
                Integer score = main.quizH.unordered.get(u);
                String nameOfUser = (u.getNicknameForGuild(chnl.getGuild()).isPresent() ? u.getNicknameForGuild(chnl.getGuild()).get() : u.getName()).replaceAll("[^A-Za-z0-9 ]", "").trim();;

                main.databaseHandler.increaseUserQuizScore(u, chnl.getGuild(), score);
                u.getOrCreatePMChannel().sendMessage("You got " + score + " points on the quiz! You are now on " + main.databaseHandler.getQuizScore(u, chnl.getGuild()));
                if (numDone <= 10)
                {
                    scores += "\n" + nameOfUser + Utilities.repeatString(" ", 50 - nameOfUser.length()) + score;
                    numDone++;
                }

                sleepFor(500);
            }

            scores += "\n```";

            quizLog += scores;

            sleepFor(1500);

            chnl.sendMessage(scores);

            if (!IDReference.LIVE)
            {
                String questionLogText = "Debug info (dev-server only):\n";
                int i = 0;
                for (QuestionLogEntry entry : questionLog)
                {
                    i++;
                    questionLogText += "  " + i + ":  ?question " + entry.difficulty.toString() + " 1 " + entry.seed + "\n";
                }
                sleepFor(1000);
                chnl.sendMessage(questionLogText);
            }
        } catch (RateLimitException rle)
        {
            //Attempt to provide a meaningful error.
            
            StackTraceElement[] st = rle.getStackTrace();

            boolean success = false;
            
            for (StackTraceElement el : st)
            {
                if (el.getClassName().equals(QuizQuestionTimer.class.getName()))
                {
                    main.logToBoth("[Error] [Quiz] Rate limited! Line " + el.getLineNumber() + " limited for " + rle.getRetryDelay() + " ms.");
                    success = true;
                }
            }
            
            
            if(!success)
            {
                //Failed to provide meaningful error.
                main.logToBoth("Rate limited, but not us, but happening in us?! For: " + rle.getRetryDelay() + ". Stacktrace: ");
                rle.printStackTrace();
            }
            
        } catch (InterruptedException ie)
        {
            try
            {
                chnl.sendMessage("Quiz was aborted.");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            synchronized (this)
            {
                phase = QuizPhase.Completed;
            }
            
            if (!isAborted)
            {
                qh.handleQuestionTimerComplete();
            }
        }
    }
    
    private void sleepFor(int timeout) throws InterruptedException
    {
        synchronized (abortSignal)
        {
            abortSignal.wait(timeout);
            if (isAborted)
            {
                throw new InterruptedException();
            }
        }
    }
    
    public void abort()
    {
        synchronized (abortSignal)
        {
            isAborted = true;
            abortSignal.notify();
        }
    }
    
    public QuizSubmitResult submitAnswer(QuizQuestion question, IUser user, int answer)
    {
        synchronized (this)
        {
            if (phase == QuizQuestionTimer.QuizPhase.Introduction)
            {
                return QuizQuestionTimer.QuizSubmitResult.TooEarly;
            }
            else
            {
                if ((phase == QuizQuestionTimer.QuizPhase.Pausing) || (phase == QuizQuestionTimer.QuizPhase.Completed)
                        || (q != question))
                {
                    return QuizQuestionTimer.QuizSubmitResult.TooLate;
                }
            }

            boolean isFirst = true;
            boolean isCorrect = (question.getCorrectAnswerIndex() == answer);

            for (QuizQuestionTimer.QuizSubmitEntry entry : submissions)
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

            submissions.add(new QuizQuestionTimer.QuizSubmitEntry(user, result));

            return result;
        }
    }
    
    private String getPointString(QuizQuestion.Difficulty difficulty, boolean wasFirst)
    {
        int points = difficulty.getPoints() + (wasFirst ? 1 : 0);
        String plural = (points > 1) ? "s" : "";
        return points + " pt" + plural;
    }
    
    private String getCorrectUserText(QuizQuestion.Difficulty difficulty)
    {
        ArrayList<String> correctUserNames = new ArrayList<>();
        String firstCorrectUserName = null;

        String firstPointText = getPointString(difficulty, true);
        String pointText = getPointString(difficulty, false);
        String result;
        
        synchronized (this)
        {
            for (QuizSubmitEntry entry : submissions)
            {
                if (entry.result == QuizSubmitResult.Correct
                        || entry.result == QuizSubmitResult.FirstCorrect)
                {
                    String name = entry.user.getNicknameForGuild(chnl.getGuild()).isPresent()
                            ? entry.user.getNicknameForGuild(chnl.getGuild()).get() : entry.user.getName();

                    if (entry.result == QuizSubmitResult.FirstCorrect)
                    {
                        firstCorrectUserName = name;
                    }
                    else
                    {
                        correctUserNames.add(name);
                    }
                }
            }
        }
        
        if (firstCorrectUserName == null)
        {
            // Nobody got it correct
            result = "Correct answers: **Nobody!**";
        }
        else
        {
            result = "Correct answers: **" + firstCorrectUserName + "** (" + firstPointText + ")";
            if (correctUserNames.size() > 0)
            {
                result += ", **" + correctUserNames.toString().replace("[", "").replace("]", "") + "** (" + pointText + ")";
            }
        }
        return result;
    }
}
