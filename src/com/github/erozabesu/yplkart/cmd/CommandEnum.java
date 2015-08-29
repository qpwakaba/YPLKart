package com.github.erozabesu.yplkart.cmd;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.data.ItemEnum;

public enum CommandEnum {
    CIRCUIT(new CircuitCommand()),
    DISPLAY(new DisplayCommand()),
    MENU(new MenuCommand()),
    ENTRY(new EntryCommand()),
    EXIT(new ExitCommand()),
    CHARACTER(new CharacterCommand()),
    CHARACTERRESET(new CharacterResetCommand()),
    KART(new KartCommand()),
    LEAVE(new LeaveCommand()),
    RANKING(new RankingCommand()),
    RELOAD(new ReloadCommand()),
    ITEM(new ItemCommand()),
    // DEBUG(null),
    ;

    private final Command command;

    private CommandEnum(Command command) {
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
        if(args.length == 0) {
            return false;
        }
        String commandName = args[0];
        Command command = getCommand(commandName);
        if (command != null) {
            return command.execute(sender, label, args);
        } else if (ItemEnum.getItemByCommandKey(args[0]) != null) {
            command = new ItemCommand();
            return command.execute(sender, label, args);
        }
        return false;
    }

    public static List<String> tabComplete(CommandSender sender, String label, String[] args) {
        String commandName = args[0];
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