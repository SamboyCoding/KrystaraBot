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
import sx.blah.discord.util.RateLimitException;

/**
 * The thread that handles the asking/answering of questions
 *
 * @author Sam
 */
public class QuizQuestionTimer implements Runnable
{

    IChannel chnl;

    public QuizQuestion q;
    public QuizPhase phase;

    String quizLog = "";

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

    ArrayList<QuizSubmitEntry> submissions = new ArrayList<>();

    IMessage msg;

    public QuizQuestionTimer(IChannel c)
    {
        chnl = c;
        phase = QuizPhase.Introduction;
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

    @Override
    public void run()
    {
        Random r = new Random();
        try
        {
            int numQuestions = 0;

            ArrayList<QuizQuestion.Difficulty> questionDifficulties
                    = new ArrayList<>(Arrays.asList(Easy, Easy, Easy, Moderate, Moderate, Moderate, Moderate, Hard, Hard, Hard));
            //= new ArrayList<>(Arrays.asList(Easy, Moderate, Hard));
            java.util.Collections.shuffle(questionDifficulties);

            ArrayList<QuestionLogEntry> questionLog = new ArrayList<>();
            
            while (questionDifficulties.size() > 0)
            {
                IMessage timer = chnl.sendMessage("Question #" + (numQuestions+1) + " will be revealed in 10 seconds...");
                Thread.sleep(10000);

                if (msg != null)
                {
                    msg.delete();
                }

                numQuestions++;
                synchronized (this)
                {
                    submissions = new ArrayList<>();
                }
                String toSend = "**Question #" + numQuestions + ":**\n\n";

                QuizQuestion.Difficulty difficulty = questionDifficulties.remove(0);

                main.quizH.lastDifficulty = difficulty;

                QuizQuestion question;

                Random questionSeed = new Random();
                long seed = Utilities.getSeed(questionSeed);

                synchronized (this)
                {
                    q = QuizQuestionFactory.getQuestion(questionSeed, difficulty);
                    question = q;
                    main.quizH.currentQ = q;
                }

                questionLog.add(new QuestionLogEntry(difficulty, seed));
                String plural = (difficulty == Moderate || difficulty == Hard) ? "s" : "";
                toSend += question.getQuestionText() + " (" + difficulty.getPoints() + " pt" + plural + ")\n";
                toSend += "1) " + question.getAnswerText(0) + "\n";
                toSend += "2) " + question.getAnswerText(1) + "\n";
                toSend += "3) " + question.getAnswerText(2) + "\n";
                toSend += "4) " + question.getAnswerText(3) + "\n\n";

                quizLog += toSend;
                timer.delete();

                msg = chnl.sendMessage(toSend);

                timer = chnl.sendMessage("Answer will be revealed in 10 seconds...");

                synchronized (this)
                {
                    phase = QuizPhase.WaitingForAnswers;
                }

                Thread.sleep(10000);

                synchronized (this)
                {
                    phase = QuizPhase.Pausing;
                }

                timer.delete();

                int pos = question.getCorrectAnswerIndex();
                String number = Integer.toString(pos + 1);

                toSend = "**Question #" + numQuestions + ":**\n\nThe correct answer was: "
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

            msg.delete();

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
                    Thread.sleep(1000);
                    chnl.sendMessage(toSend);
                }

                txt = txt.substring(lastQuestionBreak + 2).trim();
            }
            
            if (txt.length() > 0)
            {
                Thread.sleep(1000);
                chnl.sendMessage(txt);
            }

            Thread.sleep(2500);

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

                Thread.sleep(500);
            }

            scores += "\n```";

            quizLog += scores;

            Thread.sleep(1500);

            chnl.sendMessage(scores);

            if (!IDReference.LIVE)
            {
                String questionLogText = "Debug info:\n";
                int i = 0;
                for (QuestionLogEntry entry : questionLog)
                {
                    i++;
                    questionLogText += "  " + i + ":  ?question " + entry.difficulty.toString() + " 1 " + entry.seed + "\n";
                }
                Thread.sleep(1000);
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
            
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            synchronized (this)
            {
                phase = QuizPhase.Completed;
            }
            QuizHandler.qt = null;
            QuizHandler.quizThread = null;
        }
    }

    private String getCorrectUserText(QuizQuestion.Difficulty difficulty)
    {
        ArrayList<String> correctUserNames = new ArrayList<>();
        String firstCorrectUserName = null;

        String plural = (difficulty == Moderate || difficulty == Hard) ? "s" : "";
        String pointText = " (" + difficulty.getPoints() + " pt" + plural + ")";

        synchronized (this)
        {
            for (QuizSubmitEntry entry : submissions)
            {
                if (entry.result == QuizSubmitResult.Correct
                        || entry.result == QuizSubmitResult.FirstCorrect)
                {
                    String name = entry.user.getNicknameForGuild(chnl.getGuild()).isPresent()
                            ? entry.user.getNicknameForGuild(chnl.getGuild()).get() : entry.user.getName();
                    correctUserNames.add(name);

                    if (entry.result == QuizSubmitResult.FirstCorrect)
                    {
                        firstCorrectUserName = name;
                    }
                }
            }
        }

        String result = "The following people answered correctly: " + (correctUserNames.isEmpty() ? "**Nobody**!" : "**" + correctUserNames.toString().replace("[", "").replace("]", "") + "**" + pointText);
        if (firstCorrectUserName != null)
        {
            result += "\nThe first correct answer was from **" + firstCorrectUserName + "**! (+1 pt)";
        }
        return result;
    }
}
