package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.MessageEnum;

/**
 * /ka circuit listコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class ListCommand extends Command {
    public ListCommand() {
        super("list");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false))
            return true;
        MessageEnum.cmdCircuitList.sendConvertedMessage(sender);
        return true;
    }
}