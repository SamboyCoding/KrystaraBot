package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import me.samboycoding.krystarabot.main;
import static me.samboycoding.krystarabot.quiz.LyyaQuestion.Difficulty.Easy;
import static me.samboycoding.krystarabot.quiz.LyyaQuestion.Difficulty.Hard;
import static me.samboycoding.krystarabot.quiz.LyyaQuestion.Difficulty.Moderate;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * The thread that handles the asking/answering of questions
 *
 * @author Sam
 */
public class QuizQuestionTimer implements Runnable
{

    IChannel chnl;

    public LyyaQuestion q;
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

    @Override
    public void run()
    {
        Random r = new Random();
        try
        {
            int numQuestions = 0;

            ArrayList<LyyaQuestion.Difficulty> questionDifficulties
                    = new ArrayList<>(Arrays.asList(Easy, Easy, Easy, Moderate, Moderate, Moderate, Moderate, Hard, Hard, Hard));
                    //= new ArrayList<>(Arrays.asList(Easy, Moderate, Hard));
            java.util.Collections.shuffle(questionDifficulties);

            while (questionDifficulties.size() > 0)
            {
                IMessage timer = chnl.sendMessage("Question will be revealed in 10 seconds...");
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

                LyyaQuestion.Difficulty difficulty = questionDifficulties.remove(0);

                main.quizH.lastDifficulty = difficulty;

                LyyaQuestion question;

                Random questionSeed = new Random();
                long seed = Utilities.getSeed(questionSeed);

                synchronized (this)
                {
                    q = LyyaQuestionFactory.getQuestion(questionSeed, difficulty);
                    question = q;
                    main.quizH.currentQ = q;
                }

                String plural = (difficulty == Moderate || difficulty == Hard) ? "s" : "";
                toSend += question.getQuestionText() + " (" + difficulty.getPoints() + " pt" + plural + ") [Question ID: **" + difficulty.name() + "-" + seed + "**]\n";
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

                toSend = "The correct answer was: "
                        + "\n\n" + number + ") **" + question.getAnswerText(question.getCorrectAnswerIndex())
                        + "**\n\n" + getCorrectUserText()
                        + "\n" + Utilities.repeatString("-", 50);

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

            String txt = quizLog;

            while (txt.trim().length() > 0)
            {
                String TwoThousandChars = txt.substring(0, (txt.length() > 2000 ? 2000 : txt.length()));
                int lastQuestionBreak = TwoThousandChars.lastIndexOf("--");
                String toSend = txt.substring(0, lastQuestionBreak + 1);

                if (toSend.trim().length() > 0)
                {
                    chnl.sendMessage(toSend);
                }
                
                if(txt.trim().length() < 1)
                {
                    break;
                }

                txt = txt.substring(lastQuestionBreak + 2);
                Thread.sleep(1000);
            }

            if (!txt.trim().isEmpty())
            {
                chnl.sendMessage(txt);
            }
            
            Thread.sleep(1250);

            chnl.sendMessage("The quiz is over! Thanks for playing! The top 10 scores were:");

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

            QuizHandler.qt = null;
            QuizHandler.quizThread = null;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getCorrectUserText()
    {
        ArrayList<String> correctUserNames = new ArrayList<>();
        String firstCorrectUserName = null;

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

        String result = "The following people answered correctly: " + (correctUserNames.isEmpty() ? "Nobody!" : correctUserNames.toString().replace("[", "").replace("]", ""));
        if (firstCorrectUserName != null)
        {
            result += "\nThe first correct answer was from " + firstCorrectUserName + "! (+2 pts.)";
        }
        return result;
    }
}
