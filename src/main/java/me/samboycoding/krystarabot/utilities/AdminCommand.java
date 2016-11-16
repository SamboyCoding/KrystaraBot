package me.samboycoding.krystarabot.utilities;

import me.samboycoding.krystarabot.main;

/**
 * TODO: Javadocs
 *
 * @author Sam
 */
public class AdminCommand extends Command
{

    /**
     * {@inheritDoc}
     */
    public AdminCommand(String commandName, String description, Boolean requiresAdmin)
    {
        super(commandName, description, requiresAdmin);
    }

    @Override
    public void _register()
    {
        if (reg)
        {
            throw new IllegalStateException("Cannot register an already registered command!");
        }
        main.registerAdminCommand(this);
        main.log("Registed ADMIN command " + name);
    }
}
