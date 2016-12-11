/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author julians
 */
public abstract class LyyaQuestion
{
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
}
