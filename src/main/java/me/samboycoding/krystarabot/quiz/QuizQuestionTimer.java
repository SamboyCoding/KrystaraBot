package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import me.samboycoding.krystarabot.main;
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

    Question q;
    QuizPhase phase;
    
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
    
    public class QuizSubmitEntry
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
            ArrayList<Integer> questionDifficulties = 
                new ArrayList<>(Arrays.asList(1,1,1,2,2,2,2,3,3,3));
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

                int difficulty = questionDifficulties.remove(0);

                Question question;
                synchronized (this)
                {
                    q = main.quizH.generateQuestion(difficulty);
                    question = q;
                }

                String plural = (difficulty > 1) ? "s" : "";
                toSend += question.question + " (" + difficulty + " pt" + plural + ".)\n";
                
                toSend += "1) " + question.answers.get(0) + "\n";
                toSend += "2) " + question.answers.get(1) + "\n";
                toSend += "3) " + question.answers.get(2) + "\n";
                toSend += "4) " + question.answers.get(3) + "\n\n";

                msg.delete();
                
                chnl.sendMessage(toSend);
                
                msg = chnl.sendMessage("Answer will be revealed in 10 seconds...");

                synchronized(this)
                {
                    phase = QuizPhase.WaitingForAnswers;
                }

                Thread.sleep(10000);
                
                synchronized(this)
                {
                    phase = QuizPhase.Pausing;
                }

                msg.delete();
                
                int pos = question.correctAnswer;
                String number = Integer.toString(pos + 1);
                chnl.sendMessage("The correct answer was: "
                        + "\n\n" + number + ") **" + question.answers.get(question.correctAnswer)
                        + "**\n\n" + getCorrectUserText()
                        + "\n" + Utilities.repeatString("-", 50));
            }
            
            synchronized(this)
            {
                phase = QuizPhase.Completed;
            }

            chnl.sendMessage("The quiz is over! Thanks for playing! The scores were:");
            
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
        
        synchronized(this)
        {
            for(QuizSubmitEntry entry : submissions)
            {
                if (entry.result == QuizSubmitResult.Correct ||
                    entry.result == QuizSubmitResult.FirstCorrect)
                {
                    String name = entry.user.getNicknameForGuild(chnl.getGuild()).isPresent() ? 
                        entry.user.getNicknameForGuild(chnl.getGuild()).get() : entry.user.getName();
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
            result += "\nThe first correct answer was from: " + firstCorrectUserName + "! (+2 pts.)";
        }
        return result;
    }
    
    public QuizSubmitResult submitAnswer(Question question, IUser user, int answer)
    {
        synchronized(this)
        {
            if(phase == QuizPhase.Introduction)
            {
                return QuizSubmitResult.TooEarly;
            }
            else if((phase == QuizPhase.Pausing) || (phase == QuizPhase.Completed) ||
                    (q != question))
            {
                return QuizSubmitResult.TooLate;
            }
            
            boolean isFirst = true;
            boolean isCorrect = (question.correctAnswer == answer);
            
            for(QuizSubmitEntry entry : submissions)
            {
                if (entry.user == user)
                {
                    return QuizSubmitResult.AlreadyAnswered;
                }
                if (entry.result == QuizSubmitResult.FirstCorrect)
                {
                    isFirst = false;
                }
            }
            
            QuizSubmitResult result = QuizSubmitResult.Incorrect;
            if (isCorrect)
            {
                result = isFirst ? QuizSubmitResult.FirstCorrect : QuizSubmitResult.Correct;
            }
            
            submissions.add(new QuizSubmitEntry(user, result));
            
            return result;
        }
    }
}
