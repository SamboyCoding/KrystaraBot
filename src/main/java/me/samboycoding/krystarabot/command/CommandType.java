package me.samboycoding.krystarabot.command;

/**
 * Represents the different type of commands
 *
 * @author Sam
 */
public enum CommandType
{
    SERVER("Server Commands"), GOW("GoW Commands"), MOD("Moderator Commands (Will be logged)"), BOTDEV("Bot Developer Commands (Will be logged)");

    private final String headerText;

    private CommandType(String h)
    {
        headerText = h;
    }

    @Override
    public String toString()
    {
        return this.headerText;
    }
}
