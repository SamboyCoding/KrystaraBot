package me.samboycoding.krystarabot.quiz;

import java.awt.Color;
import java.util.EnumSet;
import me.samboycoding.krystarabot.utilities.IDReference;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RoleBuilder;

/**
 * Handles the creation/destruction of a quiz, questions + answers, and scoring.
 * @author Sam
 */
public class QuizHandler 
{
    IChannel quizChannel = null;
    IRole quizRole = null;
    
    public void initializeQuiz(IGuild srv, IUser sdr, IChannel source) throws Exception
    {
        EnumSet<Permissions> sendReceiveMessages = EnumSet.of(Permissions.SEND_MESSAGES, Permissions.READ_MESSAGES, Permissions.READ_MESSAGE_HISTORY);
        if(srv.getChannelsByName("Quiz").isEmpty())
        {
            quizChannel = srv.createChannel("Quiz");
            quizChannel.overrideRolePermissions(srv.getEveryoneRole(), null, sendReceiveMessages); //Disallow @everyone to use the channel
        } else
        {
            //If the quiz channel exists, assume that the quiz is running
            throw new IllegalStateException("Tried to initialize a quiz when one is already running!");
        }
        
        //quizChannel is now set
        
        if(srv.getRolesByName("Quiz").isEmpty())
        {
            quizRole = new RoleBuilder(srv)
                    .withName("Quiz")
                    .withColor(Color.blue)
                    .build();
        } else
        {
            //If the quiz role exists, assume that the quiz is running
            throw new IllegalStateException("Tried to initialize a quiz when one is already running!");
        }
        
        sdr.addRole(quizRole);
        
        quizChannel.overrideRolePermissions(quizRole, sendReceiveMessages, null); //Allow members with the "quiz" role to access the channel
        quizChannel.overrideRolePermissions(srv.getRoleByID(IDReference.MODROLE), sendReceiveMessages, null); //Allow members with the moderator role to access the channel
        
        new Thread(new QuizStartTimer(quizChannel), "Quiz start timer").start();
    }
}
