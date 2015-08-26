package com.github.erozabesu.yplkart.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.cmd.circuit.AcceptCommand;
import com.github.erozabesu.yplkart.cmd.circuit.BroadcastGoalCommand;
import com.github.erozabesu.yplkart.cmd.circuit.CreateCommand;
import com.github.erozabesu.yplkart.cmd.circuit.DeleteCommand;
import com.github.erozabesu.yplkart.cmd.circuit.DenyCommand;
import com.github.erozabesu.yplkart.cmd.circuit.EditCommand;
import com.github.erozabesu.yplkart.cmd.circuit.InfoCommand;
import com.github.erozabesu.yplkart.cmd.circuit.ListCommand;
import com.github.erozabesu.yplkart.cmd.circuit.RenameCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetLapCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetMatchingTimeCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetMaxPlayerCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetMenuTimeCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetMinPlayerCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetPositionCommand;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;

public class KaCommand implements CommandExecutor {

    public KaCommand() {
        // Do nothing
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("ka")) {
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].toLowerCase();
            }

            if (args.length == 0) {
                if (!Permission.hasPermission(sender, Permission.CMD_KA, false))
                    return true;

                SystemMessageEnum.reference.sendConvertedMessage(sender);
                return true;
            } else if (args[0].equalsIgnoreCase("circuit")) {
                if (args.length == 1) {

                } else {
                    Command subCommand;
                    String subCommandName = args[1];
                    if (subCommandName.equalsIgnoreCase("info")) {
                        subCommand = new InfoCommand();
                    } else if (subCommandName.equalsIgnoreCase("create")) {
                        subCommand = new CreateCommand();
                    } else if (subCommandName.equalsIgnoreCase("delete")) {
                        subCommand = new DeleteCommand();
                    } else if (subCommandName.equalsIgnoreCase("edit")) {
                        subCommand = new EditCommand();
                    } else if (subCommandName.equalsIgnoreCase("broadcastgoal")) {
                        subCommand = new BroadcastGoalCommand();
                    } else if (subCommandName.equalsIgnoreCase("setlap")) {
                        subCommand = new SetLapCommand();
                    } else if (subCommandName.equalsIgnoreCase("setminplayer")) {
                        subCommand = new SetMinPlayerCommand();
                    } else if (subCommandName.equalsIgnoreCase("setmaxplayer")) {
                        subCommand = new SetMaxPlayerCommand();
                    } else if (subCommandName.equalsIgnoreCase("setmatchingtime")) {
                        subCommand = new SetMatchingTimeCommand();
                    } else if (subCommandName.equalsIgnoreCase("setmenutime")) {
                        subCommand = new SetMenuTimeCommand();
                    } else if (subCommandName.equalsIgnoreCase("setposition")) {
                        subCommand = new SetPositionCommand();
                    } else if (subCommandName.equalsIgnoreCase("rename")) {
                        subCommand = new RenameCommand();
                    } else if (subCommandName.equalsIgnoreCase("list")) {
                        subCommand = new ListCommand();
                    } else if (subCommandName.equalsIgnoreCase("accept")) {
                        subCommand = new AcceptCommand();
                    } else if (subCommandName.equalsIgnoreCase("deny")) {
                        subCommand = new DenyCommand();
                    } else {
                        SystemMessageEnum.referenceCircuitIngame.sendConvertedMessage(sender);
                        return true;
                    }
                    return subCommand.execute(sender, subCommandName, args);
                }
            }
        }
        return false;
    }
}