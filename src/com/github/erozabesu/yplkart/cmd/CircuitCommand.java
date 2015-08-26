package com.github.erozabesu.yplkart.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.cmd.circuit.BroadcastGoalCommand;
import com.github.erozabesu.yplkart.cmd.circuit.CreateCommand;
import com.github.erozabesu.yplkart.cmd.circuit.DeleteCommand;
import com.github.erozabesu.yplkart.cmd.circuit.InfoCommand;
import com.github.erozabesu.yplkart.cmd.circuit.ListCommand;
import com.github.erozabesu.yplkart.cmd.circuit.RenameCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetLapCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetLimitTimeCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetMatchingTimeCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetMaxPlayerCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetMenuTimeCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetMinPlayerCommand;
import com.github.erozabesu.yplkart.cmd.circuit.SetPositionCommand;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;

public class CircuitCommand extends Command {
    public CircuitCommand() {
        super("circuit");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2)
            // TODO: ここでコマンドの使い方を表示させられる。
            return false;

        if (args[1].equalsIgnoreCase("list")) {
            Command command = new ListCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("info")) {
            Command command = new InfoCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("delete")) {
            Command command = new DeleteCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("rename")) {
            Command command = new RenameCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("setminplayer")) {
            Command command = new SetMinPlayerCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("setmaxplayer")) {
            Command command = new SetMaxPlayerCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("setmatchingtime")) {
            Command command = new SetMatchingTimeCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("setmenutime")) {
            Command command = new SetMenuTimeCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("setlimittime")) {
            Command command = new SetLimitTimeCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("setlap")) {
            Command command = new SetLapCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("broadcastgoal")) {
            Command command = new BroadcastGoalCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("create")) {
            Command command = new CreateCommand();
            return command.execute(sender, label, args);
        } else if (args[1].equalsIgnoreCase("setposition")) {
            Command command = new SetPositionCommand();
            return command.execute(sender, label, args);
        } else {
            SystemMessageEnum.referenceCircuitOutgame.sendConvertedMessage(null);
            return true;
        }
    }
}