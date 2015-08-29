package com.github.erozabesu.yplkart.cmd.circuit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public enum CircuitCommandEnum {
    LIST(new ListCommand()),
    INFO(new InfoCommand()),
    DELETE(new DeleteCommand()),
    RENAME(new RenameCommand()),
    SET_RACE_TYPE(new SetRaceTypeCommand()),
    SET_MIN_PLAYER(new SetMinPlayerCommand()),
    SET_MAX_PLAYER(new SetMaxPlayerCommand()),
    SET_MATCHING_TIME(new SetMatchingTimeCommand()),
    SET_MENU_TIME(new SetMenuTimeCommand()),
    SET_LIMIT_TIME(new SetLimitTimeCommand()),
    SET_LAP(new SetLapCommand()),
    BROADCAST_GOAL(new BroadcastGoalCommand()),
    CREATE(new CreateCommand()),
    SET_POSITION(new SetPositionCommand()),
    ACCEPT(new AcceptCommand()),
    DENY(new DenyCommand()), ;

    private final Command command;

    private CircuitCommandEnum(Command command) {
        this.command = command;
        data.commands.put(command.getName().toLowerCase(), command);
        for (String aliase : command.getAliases()) {
            data.aliases.put(aliase.toLowerCase(), command);
        }
    }

    public boolean execute(CommandSender sender, String[] args) {
        return this.command.execute(sender, this.command.getName(), args);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return this.command.tabComplete(sender, this.command.getName(), args);
    }

    public static boolean execute(CommandSender sender, String label, String[] args) {
        String commandName = args[1];
        Command command = getCommand(commandName);
        if (command != null) {
            return command.execute(sender, label, args);
        }
        return false;
    }

    public static List<String> tabComplete(CommandSender sender, String label, String[] args) {
        String commandName = args[1];
        Command command = getCommand(commandName);
        if (command != null) {
            return command.tabComplete(sender, label, args);
        }
        return null;
    }

    public static Command getCommand(String label) {
        String commandLabel = label.toLowerCase();
        if (data.commands.containsKey(commandLabel)) {
            return data.commands.get(commandLabel);
        } else if (data.aliases.containsKey(commandLabel)) {
            return data.aliases.get(commandLabel);
        }
        return null;
    }

    public static Collection<Command> getCommands() {
        return data.commands.values();
    }

    private static class data {
        private final static Map<String, Command> aliases;
        private final static Map<String, Command> commands;

        static {
            commands = new HashMap<String, Command>();
            aliases = new HashMap<String, Command>();
        }

    }

}