package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.utils.Util;

public class BroadcastGoalCommand extends Command {
    public BroadcastGoalCommand() {
        super("broadcastgoal");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false))
            return true;
        if (args.length != 4)
            // TODO: ここでコマンドの使い方を表示させられる。
            return false;

        String circuitName = args[2];
        boolean isBroadcastGoalMessage;
        if (!Util.isBoolean(args[3])) {
            MessageEnum.invalidBoolean.sendConvertedMessage(null);
            return true;
        } else {
            isBroadcastGoalMessage = Boolean.parseBoolean(args[3]);
        }
        CircuitConfig.setBroadcastGoalMessage(sender, circuitName, isBroadcastGoalMessage);
        return true;
    }
}
