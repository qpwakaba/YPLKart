package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.ItemEnum;

public class EditCommand extends Command {
    public EditCommand() {
        super("edit");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false)) {
            return true;
        }
        if(args.length != 3 || !(sender instanceof Player)) {
            //TODO: ここでコマンドの使い方を表示させられる。
            return false;
        }

        Player player = (Player)sender;
        String circuitName = args[2];
        ItemEnum.addCheckPointTool(player, circuitName);
        return true;
    }
}
