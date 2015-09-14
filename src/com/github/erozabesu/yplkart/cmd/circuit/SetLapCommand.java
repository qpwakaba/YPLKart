package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;

/**
 * /ka circuit setlapコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class SetLapCommand extends Command {
    public SetLapCommand() {
        super("setlap");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false))
            return true;
        if(args.length != 4)
            //TODO: ここでコマンドの使い方を表示させられる。
            return false;

        String circuitName = args[2];
        int lap;
        try {
            lap = Integer.parseInt(args[3]);
        } catch (NumberFormatException ex) {
            MessageEnum.invalidNumber.sendConvertedMessage(null);
            return true;
        }
        CircuitConfig.setNumberOfLaps(sender, circuitName, lap);
        return true;
    }
}
