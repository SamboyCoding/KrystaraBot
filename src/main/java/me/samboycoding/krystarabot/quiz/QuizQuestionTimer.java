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

    public QuizQuestionTimer(IChannel c)
    {
        chnl = c;
        phase = QuizPhase.Introduction;
    }

    @Override
    public void run()
    {
        IMessage msg;
        Random r = new Random();
        try
        {
            int numQuestions = 0;
            
            ArrayList<LyyaQuestion.Difficulty> questionDifficulties
                    = new ArrayList<>(Arrays.asList(Easy, Easy, Easy, Moderate, Moderate, Moderate, Moderate, Hard, Hard, Hard));
            java.util.Collections.shuffle(questionDifficulties);

            while (questionDifficulties.size() > 0)
            {
                msg = chnl.sendMessage("Question will be revealed in 10 seconds...");
                Thread.sleep(10000);

                numQuestions++;
                synchronized (this)
                {
                    submissions = new ArrayList<>();
                }
                String toSend = "**Question #" + numQuestions + ":**\n\n";

                LyyaQuestion.Difficulty difficulty = questionDifficulties.remove(0);

                main.quizH.lastDifficulty = difficulty;
                
                LyyaQuestion question;
                
                long seed = System.currentTimeMillis() % 1000;
                Random questionSeed = new Random(seed);
                
                synchronized (this)
                {
                    q = LyyaQuestionFactory.getQuestion(questionSeed, difficulty);
                    question = q;
                    main.quizH.currentQ = q;
                }

                String plural = (difficulty == Moderate || difficulty == Hard) ? "s" : "";
                toSend += question.getQuestionText() + " (" + difficulty.getPoints() + " pt" + plural + ") [" + seed + "]\n";
                toSend += "1) " + question.getAnswerText(0) + "\n";
                toSend += "2) " + question.getAnswerText(1) + "\n";
                toSend += "3) " + question.getAnswerText(2) + "\n";
                toSend += "4) " + question.getAnswerText(3) + "\n\n";

                msg.delete();

                chnl.sendMessage(toSend);

                msg = chnl.sendMessage("Answer will be revealed in 10 seconds...");

                synchronized (this)
                {
                    phase = QuizPhase.WaitingForAnswers;
                }

                Thread.sleep(10000);
                
                synchronized (this)
                {
                    phase = QuizPhase.Pausing;
                }

                msg.delete();

                int pos = question.getCorrectAnswerIndex();
                String number = Integer.toString(pos + 1);
                chnl.sendMessage("The correct answer was: "
                        + "\n\n" + number + ") **" + question.getAnswerText(question.getCorrectAnswerIndex())
                        + "**\n\n" + getCorrectUserText()
                        + "\n" + Utilities.repeatString("-", 50));
            }

            synchronized (this)
            {
                phase = QuizPhase.Completed;
            }

            chnl.sendMessage("The quiz is over! Thanks for playing! The top 10 scores were:");

            main.quizH.ordered.putAll(main.quizH.unordered); //Sort.

            String scores = "```\nName" + Utilities.repeatString(" ", 46) + "Score";
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
            }

            scores += "\n```";

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
