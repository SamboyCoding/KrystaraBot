package me.samboycoding.krystarabot.quiz;

/**
 * Represents the template for a question in the quiz
 *
 * @author Sam
 */
public class QuestionTemplate
{
    public final String templateText;
    public final String searchFor;
    public final String searchIn;
    
    public QuestionTemplate(String templ, String forr, String in)
    {
        templateText = templ;
        searchFor = forr;
        searchIn = in;
    }
}
