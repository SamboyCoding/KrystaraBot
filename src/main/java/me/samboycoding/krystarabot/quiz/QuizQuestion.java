package me.samboycoding.krystarabot.quiz;

import java.io.InputStream;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;

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
        
        public static final int Count = values().length;
        
        public static Difficulty fromInteger(int x)
        {
            return values()[x];
        }
        
        public static Difficulty fromString(String s)
        {
            for (Difficulty d : values())
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

    public final static int ANSWER_COUNT = 4;
    
    public abstract String getQuestionText();
    public abstract String getAnswerText(int index);
    public abstract int getCorrectAnswerIndex();
    public abstract Difficulty getDifficulty();
    public abstract long getRandomSeed();

    public String getQuestionSecondaryText()
    {
        return "";
    }
    
    public URL getQuestionImageUrl()
    {
        return null;
    }
}
