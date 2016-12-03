package me.samboycoding.krystarabot.quiz;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Handles the creation/destruction of a quiz, questions + answers, and scoring.
 * @author Sam
 */
public class QuizHandler 
{
    public void initializeQuiz(IGuild srv, IUser sdr, IChannel source) throws DiscordException, RateLimitException, MissingPermissionsException
    {
        if(srv.getChannelsByName("Quiz").isEmpty())
        {
            
        } else
        {
            //Already running
        }
    }
}
