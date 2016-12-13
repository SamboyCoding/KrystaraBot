package me.samboycoding.krystarabot.quiz;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import me.samboycoding.krystarabot.main;
import static me.samboycoding.krystarabot.quiz.QuizQuestion.Difficulty.Easy;
import static me.samboycoding.krystarabot.quiz.QuizQuestion.Difficulty.Hard;
import static me.samboycoding.krystarabot.quiz.QuizQuestion.Difficulty.Moderate;
import me.samboycoding.krystarabot.utilities.IDReference;
import me.samboycoding.krystarabot.utilities.Utilities;
import org.apache.commons.io.FilenameUtils;
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
    
    private void sendLargeMessage(Iterable<String> messageParts) 
            throws MissingPermissionsException, RateLimitException, DiscordException, InterruptedException
    {
        final int CHUNK_MAX_LENGTH = 2000;

        String chunk = "";
        Iterator<String> messageIterator = messageParts.iterator();
        
        while (messageIterator.hasNext())
        {
            String nextMsg = messageIterator.next();
            if ((chunk.length() + nextMsg.length()) > CHUNK_MAX_LENGTH)
            {
                sleepFor(1000);
                chnl.sendMessage(chunk);
                chunk = "";
            }

            chunk += nextMsg + "\n";
        }

        if (!chunk.isEmpty())
        {
            sleepFor(1000);
            chnl.sendMessage(chunk);
        }
    }
    
    @Override
    public void run()
    {
        try
        {
            sendIntro();
            
            ArrayList<QuizQuestion.Difficulty> questionDifficulties = getQuestionDifficulties();
            ArrayList<QuestionLogEntry> questionLog = new ArrayList<>();
            ArrayList<String> quizLog = new ArrayList();
            
            IMessage questionMessage = null;
            IMessage imageMessage = null;
            IMessage choicesMessage = null;
            IMessage answerMessage = null;
            Random questionSeed = new Random();
            for (int iQuestion = 0; iQuestion < questionDifficulties.size(); iQuestion++)
            {
                QuizQuestion.Difficulty difficulty = questionDifficulties.get(iQuestion);

                sendCountdownMessage("Next question: %1$d seconds", 10).delete();

                if (answerMessage != null)
                {
                    answerMessage.delete();
                    answerMessage = null;
                }
                if (choicesMessage != null)
                {
                    choicesMessage.delete();
                    choicesMessage = null;
                }
                if (imageMessage != null)
                {
                    imageMessage.delete();
                    imageMessage = null;
                }
                if (questionMessage != null)
                {
                    questionMessage.delete();
                    questionMessage = null;
                }
                
                QuizQuestion question;
                long seed = Utilities.getSeed(questionSeed);
                question = QuizQuestionFactory.getQuestion(questionSeed, difficulty);

                synchronized (this)
                {
                    q = question;
                    qh.setQuestion(q, difficulty);
                    submissions.clear();
                }

                questionLog.add(new QuestionLogEntry(difficulty, seed));

                String pointString = getPointString(difficulty, false);
                String questionText = "**Question #" + (iQuestion+1) + ":**\n\n" + question.getQuestionText() +
                        " (" + pointString + ")\n";
                
                String questionSecondaryText = question.getQuestionSecondaryText();
                if (!questionSecondaryText.isEmpty())
                {
                    questionText += questionSecondaryText + "\n";
                }
                
                URL imageUrl = question.getQuestionImageUrl();

                quizLog.add(questionText);
                questionMessage = chnl.sendMessage(questionText);
                if (imageUrl != null)
                {
                    try
                    {
                        InputStream imageStream = imageUrl.openStream();
                        String imageName = "image." + FilenameUtils.getExtension(imageUrl.getPath());
                        imageMessage = chnl.sendFile("", false, imageStream, imageName);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                choicesMessage = sendChoices(question, quizLog);

                synchronized (this)
                {
                    phase = QuizPhase.WaitingForAnswers;
                }

                sendCountdownMessage("Time remaining: %1$d seconds", qTimeSeconds).delete();

                synchronized (this)
                {
                    phase = QuizPhase.Pausing;
                }

                choicesMessage.delete();
                choicesMessage = null;
                
                answerMessage = sendAnswer(question, difficulty, quizLog);
            }

            synchronized (this)
            {
                phase = QuizPhase.Completed;
            }
            
            sendCountdownMessage("Results: %1$d seconds", 5).delete();
            if (answerMessage != null)
            {
                answerMessage.delete();
            }
            if (choicesMessage != null)
            {
                choicesMessage.delete();
            }
            if (imageMessage != null)
            {
                imageMessage.delete();
            }
            if (questionMessage != null)
            {
                questionMessage.delete();
            }

            sendResults(quizLog, questionLog);
            
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
                chnl.sendMessage("Quiz was stopped.");
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

    private IMessage sendAnswer(QuizQuestion question, QuizQuestion.Difficulty difficulty, ArrayList<String> quizLog)
            throws RateLimitException, MissingPermissionsException, DiscordException
    {
        IMessage msg;
        int pos = question.getCorrectAnswerIndex();
        String number = Integer.toString(pos + 1);
        String answerBody = "\nThe correct answer was: "
                + "\n\n" + number + ") **" + question.getAnswerText(question.getCorrectAnswerIndex())
                + "**\n\n" + getCorrectUserText(difficulty)
                + "\n" + Utilities.repeatString("-", 40);
        quizLog.add(answerBody + "\n");
        msg = chnl.sendMessage(answerBody);
        return msg;
    }

    private IMessage sendChoices(QuizQuestion question, ArrayList<String> quizLog) 
            throws MissingPermissionsException, DiscordException, InterruptedException, RateLimitException
    {
        IMessage msg;
        String questionBody = "\n";
        for (int i = 0; i < QuizQuestion.AnswerCount; i++)
        {
            questionBody += (i+1) + ") " + question.getAnswerText(i) + "\n";
        }
        quizLog.add(questionBody);
        msg = chnl.sendMessage(questionBody);
        return msg;
    }

    private ArrayList<QuizQuestion.Difficulty> getQuestionDifficulties()
    {
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
        return questionDifficulties;
    }

    private void sendResults(ArrayList<String> quizLog, ArrayList<QuestionLogEntry> questionLog) 
            throws IOException, MissingPermissionsException, RateLimitException, InterruptedException, DiscordException
    {
        quizLog.add("\nThe quiz is over! And the winner is...");
        
        sendLargeMessage(quizLog);
        sleepFor(1000);
        
        main.quizH.ordered.putAll(main.quizH.unordered); //Sort.
        
        String scores = "```\nName" + Utilities.repeatString(" ", 46) + "Score"
                + "\n" + Utilities.repeatString("-", 80);
        int numDone = 0;
        for (IUser u : main.quizH.ordered.descendingKeySet())
        {
            Integer score = main.quizH.unordered.get(u);
            String nameOfUser = (u.getNicknameForGuild(chnl.getGuild()).isPresent() ? u.getNicknameForGuild(chnl.getGuild()).get() : u.getName()).replaceAll("[^A-Za-z0-9 ]", "").trim();;
            
            main.databaseHandler.increaseUserQuizScore(u, chnl.getGuild(), score);
            if (score > 0)
            {
                u.getOrCreatePMChannel().sendMessage("You scored " + score + " points on the quiz! You now have a total of " +
                        main.databaseHandler.getQuizScore(u, chnl.getGuild()) + " points.");
                sleepFor(500);
            }
            if (numDone <= 10)
            {
                scores += "\n" + nameOfUser + Utilities.repeatString(" ", 50 - nameOfUser.length()) + score;
                numDone++;
            }
        }
        
        scores += "\n```";
        
        sleepFor(1500);
        
        chnl.sendMessage(scores);
        
        sleepFor(1000);
        chnl.sendMessage("Thanks for playing! To play again, use the command `?quiz` in any channel.");
        
        if (IDReference.Environment != IDReference.RuntimeEnvironment.Live)
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
    }

    private void sendIntro() throws InterruptedException, MissingPermissionsException, RateLimitException, DiscordException
    {
        sendCountdownMessage("Quiz will start in %1$d seconds...", 10).delete();

        chnl.sendMessage("Welcome to the GoW Discord quiz!");
        sleepFor(2500);
        chnl.sendMessage("You will be asked " + qCount + " questions, and will have " + qTimeSeconds +
                " seconds to answer each question.");
        sleepFor(2500);
        chnl.sendMessage("Questions are worth 1-3 points according to difficulty, and the" +
                " first correct answer is worth 1 extra point.");
        sleepFor(2500);
        chnl.sendMessage("Submit only the _number_ of the answer you think is correct (1-" +
                QuizQuestion.AnswerCount + "). All other answers will not be counted.");
        sleepFor(2500);
        chnl.sendMessage("The person with the most points after " + qCount + " questions wins!\n\n" +
                Utilities.repeatString("-", 40));
        sleepFor(2000);
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
