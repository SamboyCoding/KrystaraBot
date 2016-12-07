package me.samboycoding.krystarabot.quiz;

import java.util.ArrayList;

/**
 * Represents a quiz question
 *
 * @author Sam
 */
public class Question
{
    public final String question;
    public final ArrayList<String>  answers;
    public final int correctAnswer;
    
    public Question(String q, ArrayList<String> a, int ca)
    {
        question = q;
        answers = a;
        correctAnswer = ca;
    }
}
