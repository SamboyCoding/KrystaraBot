package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Represents a command for the krystara bot
 *
 * @author Sam
 */
public abstract class KrystaraCommand implements Comparable<KrystaraCommand>
{

    protected String commandName;

    /**
     * Handles this command.
     *
     * @param sdr The user who sent the message
     * @param chnl The channel the message was sent in
     * @param msg The message that was sent.
     * @param arguments The arguments passed to the command, in an arraylist for
     * convenience
     * @param argsFull A string representing the raw arguments
     * @throws java.lang.Exception if something goes wrong.
     */
    public abstract void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception;

    /**
     * Gets the help text for this command
     *
     * @return The help text
     */
    public abstract String getHelpText();

    /**
     * Gets whether or not this command requires admin
     *
     * @return True/false, representing yes/no
     */
    public abstract Boolean requiresAdmin();

    /**
     * Shows what type of command this is
     *
     * @return A command type
     */
    public abstract CommandType getCommandType();

    /**
     * Gets the usage string for the bot (eg "?troop [name]")
     *
     * @return The usage string
     */
    public abstract String getUsage();

    /**
     * Gets the raw command used (eg for ?troop it would be "troop" to allow
     * handling in the listener)
     *
     * @return The raw command text
     */
    public abstract String getCommand();

    @Override
    public int compareTo(KrystaraCommand o)
    {
        String otherName = o.commandName;
        return commandName.compareTo(otherName);
    }
}
