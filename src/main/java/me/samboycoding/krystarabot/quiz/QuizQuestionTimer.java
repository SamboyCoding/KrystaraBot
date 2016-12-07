package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;
import java.util.Random;
import me.samboycoding.krystarabot.main;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;

/**
 * The thread that handles the asking/answering of questions
 *
 * @author Sam
 */
public class QuizQuestionTimer implements Runnable
{

    IChannel chnl;

    public QuizQuestionTimer(IChannel c)
    {
        chnl = c;
    }

    @Override
    public void run()
    {
        try
        {
            int numQuestions = 0;
            while (numQuestions < 10)
            {
                chnl.sendMessage("Question will be revealed in 5 seconds...");
                Thread.sleep(5000);
                
                numQuestions++;
                String toSend = "**Question #" + numQuestions + ":**\n\n";

                Question q = main.quizH.generateQuestion(1); //Easy, for now

                toSend += q.question + "\n";
                
                ArrayList<Integer> order = new ArrayList<>();
                
                Random r = new Random();
                
                while(order.size() < 4)
                {
                    int num = r.nextInt(4); //0, 1, 2, or 3
                    if(!order.contains(num))
                    {
                        order.add(num);
                    }
                }

                toSend += "a) " + q.answers.get(order.get(0)) + "\n";
                toSend += "b) " + q.answers.get(order.get(1)) + "\n";
                toSend += "c) " + q.answers.get(order.get(2)) + "\n";
                toSend += "d) " + q.answers.get(order.get(3)) + "\n\n";

                toSend += "Answer will be revealed in 10 seconds...";

                chnl.sendMessage(toSend);
                Thread.sleep(10000);

                chnl.sendMessage("Answer: \n" + q.answers.get(q.correctAnswer) + "\n" + Utilities.repeatString("-", 50));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
