package me.samboycoding.krystarabot.utilities;

import me.samboycoding.krystarabot.main;

/**
 *
 * @author r3byass
 */
public class Command {

    private final String name;
    private final String desc;
    private final Boolean adm;
    private final String[] alias;
    private Boolean reg = false;

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
        alias = new String[]{};
    }

    /**
     * Creates a command with aliases. You need to register is with
     * {@link me.samboycoding.krystarabot.utilities.Command#_register()} to make
     * it work.
     *
     * @param commandName The name of the command (eg 'help')
     * @param description The description of the command to show in the help
     * menu
     * @param requiresAdmin Whether or not the command requires admin. Only
     * shown in the help menu, DOES NOT check for admin anyway.
     * @param aliases A comma-separated list of strings to serve as aliases to
     * the command.
     */
    public Command(String commandName, String description, Boolean requiresAdmin, String... aliases) {
        name = commandName;
        desc = description;
        adm = requiresAdmin;
        alias = aliases;
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

    public String[] getAliases() {
        return alias;
    }

    public void _register() {
        if (reg) {
            throw new IllegalStateException("Cannot register an already registered command!");
        }
        main.registerCommand(this);
        main.log("Registed command " + name);
    }
}