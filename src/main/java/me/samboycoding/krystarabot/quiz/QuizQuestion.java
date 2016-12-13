package me.samboycoding.krystarabot.quiz;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;
import org.json.JSONObject;

/**
 * Base class of questions for the quiz.
 *
 * @author Emily Ash
 */
public abstract class QuizQuestion
{
    public static enum Difficulty
    {
        Easy,
        Moderate,
        Hard,
        Unused;
        
        private static final Difficulty[] Difficulties =
        {
            Easy,
            Moderate,
            Hard,
            Unused
        };

        public static final int Count = Difficulties.length;
        
        public static Difficulty fromInteger(int x)
        {
            return Difficulties[x];
        }
        
        public static Difficulty fromString(String s)
        {
            for (Difficulty d : Difficulties)
            {
                if (d.name().equalsIgnoreCase(s))
                {
                    return d;
                }
            }
            throw new InvalidParameterException();
        }

        public int getPoints()
        {
            return (this == Hard) ? 3 : (this == Moderate) ? 2 : 1;
        }
    }

    protected ArrayList<JSONObject> answers;
    protected JSONObject correctAnswer;
    protected Random random;
    
    public final static int AnswerCount = 4;
    
    public QuizQuestion(Random r)
    {
        answers = new ArrayList<>();
        random = r;
    }
    
    public String getQuestionText()
    {
        return "";
    }
    
    public String getQuestionSecondaryText()
    {
        return "";
    }
    
    public String getAnswerText(int index)
    {
        return "";
    }
    
    public int getCorrectAnswerIndex()
    {
        return answers.indexOf(correctAnswer);
    }
    
    public Difficulty getDifficulty()
    {
        return Difficulty.Easy;
    }
}
