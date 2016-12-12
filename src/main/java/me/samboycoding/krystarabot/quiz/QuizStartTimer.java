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
    private int time = 10;
    private IChannel c;
    private QuizHandler qh;
    private boolean isAborted = false;
    private final Object abortSignal = new Object();
        
    public QuizStartTimer(QuizHandler handler, IChannel channel)
    {
        qh = handler;
        c = channel;
    }

    @Override
    public void run()
    {
        try
        {
            IMessage msg = c.sendMessage("Quiz will start in 10 seconds...");
            for (int i = time; i > 0; i--)
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
                    sleepFor(500);
                    //Ignore and move on, with an extra 1/2 second wait.
                }
                sleepFor(1000);
            }
            msg.delete();
            c.sendMessage("Welcome to the GoW Discord quiz!");
            sleepFor(2500);
            c.sendMessage("You will be asked 10 questions, and will have 10 seconds to answer each question.");
            sleepFor(2500);
            c.sendMessage("You will be asked 3 easy (1 point), 4 normal (2 points) and 3 hard (3 points) questions.");
            sleepFor(2500);
            c.sendMessage("Enter the number of the answer you think is correct. DO NOT attempt to enter the full answer.");
            sleepFor(2500);
            c.sendMessage("The person with the most points after 10 questions wins!\n\n" + Utilities.repeatString("-", 40));
            sleepFor(2000);
            qh.handleStartTimerComplete();
        } catch (InterruptedException ie)
        {
            try
            {
                c.sendMessage("Quiz was aborted.");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
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
}
