package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;

/**
 * /ka circuit setmenutimeコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class SetMenuTimeCommand extends Command {
    public SetMenuTimeCommand() {
        super("setmenutime");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false))
            return true;
        if (args.length != 4)
            // TODO: ここでコマンドの使い方を表示させられる。
            return false;

        String circuitName = args[2];
        int menuTime;
        try {
            menuTime = Integer.parseInt(args[3]);
        } catch (NumberFormatException ex) {
            MessageEnum.invalidNumber.sendConvertedMessage(null);
            return true;
        }
        CircuitConfig.setMenuTime(sender, circuitName, menuTime);
        return true;
    }
}
