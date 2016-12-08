package me.samboycoding.krystarabot.quiz;

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

    int time = 20;
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
            IMessage msg = c.sendMessage("Quiz will start in 120 seconds...");
            for (int i = time; i >= 0; i--)
            {
                try
                {
                    msg.edit("Quiz will start in " + i + " seconds...");
                } catch(RateLimitException e)
                {
                    Thread.sleep(500);
                    //Ignore and move on, with an extra 1/2 second wait.
                }
                Thread.sleep(1000);
            }
            Thread.sleep(1500);
            msg.edit("Quiz starting!");
            new Thread(new QuizQuestionTimer(c), "Quiz question timer").start();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
