package me.samboycoding.krystarabot.quiz;

import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RateLimitException;

/**
 * The start quiz timer
 *
 * @author r3byass
 */
public class QuizStartTimer implements Runnable
{

    int time = 10;
    IChannel c;

    public QuizStartTimer(IChannel channel)
    {
        c = channel;
    }

    @Override
    public void run()
    {
        try
        {
            IMessage msg = c.sendMessage("Quiz will start in 10 seconds...");
            for (int i = time; i >= 0; i--)
            {
                try
                {
                    if(msg == null)
                    {
                        return;
                    }
                    msg.edit("Quiz will start in " + i + " seconds...");
                } catch(RateLimitException e)
                {
                    Thread.sleep(500);
                    //Ignore and move on, with an extra 1/2 second wait.
                }
                Thread.sleep(1000);
            }
            msg.delete();
            c.sendMessage("Welcome to the GoW Discord quiz!");
            Thread.sleep(2500);
            c.sendMessage("You will be asked 10 questions, and will have 10 seconds to answer each question.");
            Thread.sleep(2500);
            c.sendMessage("The questions come in 3 difficulties: easy (1 point), medium (2 points), and hard (3 points).");
            Thread.sleep(2500);
            c.sendMessage("You will be asked 3 easy, 4 normal and 3 hard questions.");
            Thread.sleep(2500);
            c.sendMessage("Enter the number of the answer you think is correct. DO NOT attempt to enter the full answer.");
            Thread.sleep(2500);
            c.sendMessage("The person with the most points after 10 questions wins!\n\n" + Utilities.repeatString("-", 50));
            Thread.sleep(2000);
            
            QuizHandler.qt = new QuizQuestionTimer(c);
            QuizHandler.quizThread = new Thread(QuizHandler.qt, "Quiz question timer");
            QuizHandler.quizThread.start();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
