package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;
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

    int numEasyAsked = 0;
    int numNormAsked = 0;
    int numHardAsked = 0;
    Question q;
    
    public QuizQuestionTimer(IChannel c)
    {
        chnl = c;
    }

    @Override
    public void run()
    {
        IMessage msg;
        Random r = new Random();
        try
        {
            int numQuestions = 0;
            while (numQuestions < 10)
            {
                msg = chnl.sendMessage("Question will be revealed in 10 seconds...");
                Thread.sleep(10000);
                
                numQuestions++;
                String toSend = "**Question #" + numQuestions + ":**\n\n";

                int difficulty = -1;
                while (difficulty == -1)
                {
                    int num = r.nextInt(3); //0, 1, or 2
                    num++; //Increase to 1, 2, or 3
                    if(num == 1 && numEasyAsked < 3)
                    {
                        difficulty = 1;
                        numEasyAsked++;
                    } else if(num == 2 && numNormAsked < 4)
                    {
                        difficulty = 2;
                        numNormAsked++;
                    } else if(num == 3 && numHardAsked < 3)
                    {
                        difficulty = 3;
                        numHardAsked++;
                    } else
                    {
                        //Just continue
                    }
                }
                
                String difficultyString = (difficulty == 1 ? "easy" : (difficulty == 2 ? "normal" : "hard"));
                
                toSend += "(This is a " + difficultyString + " question)\n\n";
                q = main.quizH.generateQuestion(difficulty);

                toSend += q.question + "\n";
                
                ArrayList<Integer> order = new ArrayList<>();
                
                while(order.size() < 4)
                {
                    int num = r.nextInt(4); //0, 1, 2, or 3
                    if(!order.contains(num))
                    {
                        order.add(num);
                    }
                }

                toSend += "1) " + q.answers.get(order.get(0)) + "\n";
                toSend += "2) " + q.answers.get(order.get(1)) + "\n";
                toSend += "3) " + q.answers.get(order.get(2)) + "\n";
                toSend += "4) " + q.answers.get(order.get(3)) + "\n\n";

                msg.delete();
                
                chnl.sendMessage(toSend);
                
                msg = chnl.sendMessage("Answer will be revealed in 10 seconds...");

                Thread.sleep(10000);

                msg.delete();
                int pos = order.indexOf(0);
                String letter = (pos == 0 ? "1" : (pos == 1 ? "2" : (pos == 2 ? "3" : "4")));
                msg = chnl.sendMessage("The correct answer was: \n" + letter + ") " + q.answers.get(q.correctAnswer) + "\n" + Utilities.repeatString("-", 50));
            }
            
            chnl.sendMessage("The quiz is over! Thanks for playing! The scores were:");
            
            QuizHandler.quizThread = null;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
