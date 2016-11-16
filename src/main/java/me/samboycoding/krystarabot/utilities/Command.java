package me.samboycoding.krystarabot.utilities;

import me.samboycoding.krystarabot.main;

/**
 *
 * @author r3byass
 */
public class Command {

    protected final String name;
    protected final String desc;
    protected final Boolean adm;
    protected Boolean reg = false;

    /**
     * Creates a command with no aliases. You need to register is with
     * {@link me.samboycoding.krystarabot.utilities.Command#_register()} to make
     * it work.
     *
     * @param commandName The name of the command (eg 'help')
     * @param description The description of the command to show in the help
     * menu
     * @param requiresAdmin Whether or not the command requires admin. Only
     * shown in the help menu, DOES NOT check for admin anyway.
     */
    public Command(String commandName, String description, Boolean requiresAdmin) {
        name = commandName;
        desc = description;
        adm = requiresAdmin;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }

    public Boolean requiresAdmin() {
        return adm;
    }

    public void _register() {
        if (reg) {
            throw new IllegalStateException("Cannot register an already registered command!");
        }
        main.registerCommand(this);
        main.log("Registed command " + name);
    }
}
