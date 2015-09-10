package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.object.CircuitData;

public class InfoCommand extends Command {
    public InfoCommand() {
        super("info");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false))
            return true;
        if(args.length != 3)
            //TODO: ここでコマンドの使い方を表示させられる。
            return false;

        String circuitName = args[2];
        CircuitData circuitData = CircuitConfig.get(circuitName);
        if (circuitData != null) {
            circuitData.sendInformation(sender);
        } else {
        	circuitData = new CircuitData(circuitName);
            MessageEnum.invalidCircuit.sendConvertedMessage(sender);
        }
        return true;
    }
}
