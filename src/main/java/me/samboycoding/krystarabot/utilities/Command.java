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

    public Command(String commandName, String description, Boolean requiresAdmin) {
        name = commandName;
        desc = description;
        adm = requiresAdmin;
        alias = new String[]{};
    }

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
