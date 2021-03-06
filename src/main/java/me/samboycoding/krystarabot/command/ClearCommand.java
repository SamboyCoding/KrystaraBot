package me.samboycoding.krystarabot.command;

import java.util.ArrayList;
import static me.samboycoding.krystarabot.command.CommandType.MOD;
import me.samboycoding.krystarabot.utilities.LogType;
import me.samboycoding.krystarabot.utilities.Utilities;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;

/**
 *
 * @author r3byass
 */
public class ClearCommand extends KrystaraCommand
{

    public ClearCommand()
    {
        commandName = "clear";
    }

    @Override
    public void handleCommand(IUser sdr, IChannel chnl, IMessage msg, ArrayList<String> arguments, String argsFull) throws Exception
    {
        String nameOfSender = sdr.getNicknameForGuild(msg.getGuild()) != null ? sdr.getNicknameForGuild(msg.getGuild()) : sdr.getName();

        try
        {
            if (arguments.size() < 1)
            {
                chnl.sendMessage(sdr.mention() + ", that command needs an argument (a number between 1 and 100)");
                return;
            }

            int amount = Integer.parseInt(arguments.get(0));
            if (amount < 1 || amount > 100)
            {
                chnl.sendMessage("Amount to delete must be between 1 and 100");
                return;
            }

            MessageHistory msgs = chnl.getMessageHistory(amount + 1);
            
            if (amount == 1)
            {
                //Cannot delete one with .bulkDelete()
                msgs.get(0).delete(); //Again, ignore index 0, as it's the command
                Utilities.cleanupMessage(chnl.sendMessage("1 message deleted. This message will self-destruct in 3 seconds..."), 3000);

                Utilities.logEvent(LogType.MESSAGEDELETE, "**" + nameOfSender + "** cleared 1 message from channel **" + chnl.getName() + "**");
                msg.delete();
                return;
            }

            ArrayList<IMessage> toDelete = new ArrayList<>();
            try
            {
                for (int i = 0; i < amount; i++) //Start at 1 to ignore command - it's removed later.
                {
                    toDelete.add(msgs.get(i));
                }
            } catch (ArrayIndexOutOfBoundsException ignored)
            {
                //Ignored
            }

            toDelete.remove(0);
            
            if (Utilities.canUseAdminCommand(sdr, chnl.getGuild()))
            {
                chnl.bulkDelete(toDelete);
                Utilities.cleanupMessage(chnl.sendMessage(toDelete.size() + " messages deleted (out of " + amount + " requested). This message will self-destruct in 10 seconds..."), 10000);

                Utilities.logEvent(LogType.MESSAGEDELETE, "**" + nameOfSender + "** cleared " + toDelete.size() + " messages from channel **" + chnl.getName() + "**");
            } else
            {
                chnl.sendMessage("You cannot do that!");
            }

            msg.delete();
        } catch (NumberFormatException ex)
        {
            chnl.sendMessage("Invalid number of messages to delete specified. Must be a whole number between 1 and 100");
        } catch (ArrayIndexOutOfBoundsException ex2)
        {
            Utilities.cleanupMessage(chnl.sendMessage("No messages found!"), 3000);
        }
    }

    @Override
    public String getHelpText()
    {
        return "Deletes the specified amount of messages.";
    }

    @Override
    public Boolean requiresAdmin()
    {
        return true;
    }

    @Override
    public String getUsage()
    {
        return "?clear [amount (1-100)]";
    }

    @Override
    public String getCommand()
    {
        return "clear";
    }

    @Override
    public CommandType getCommandType()
    {
        return MOD;
    }
}
