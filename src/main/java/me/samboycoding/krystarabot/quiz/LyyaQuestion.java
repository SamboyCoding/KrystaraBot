package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import me.samboycoding.krystarabot.GameData;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Alternative version of questions for the quiz.
 *
 * @author Emily Ash
 */
public abstract class LyyaQuestion
{
    public static enum Difficulty
    {
        Easy,
        Moderate,
        Hard;

        public int getPoints()
        {
            return (this == Hard) ? 3 : (this == Moderate) ? 2 : 1;
        }
    }

    protected ArrayList<JSONObject> answers;
    protected JSONObject correctAnswer;
    protected Random random;
    
    public final static int AnswerCount = 4;
    
    public LyyaQuestion(Random r)
    {
        answers = new ArrayList<>();
        random = r;
    }
    
    public String getQuestionText()
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
