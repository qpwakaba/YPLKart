package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.CircuitConfig;

public class RenameCommand extends Command {
    public RenameCommand() {
        super("rename");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false))
            return true;
        if(args.length != 4)
            //TODO: ここでコマンドの使い方を表示させられる。
            return false;

        String oldName = args[2];
        String newName = args[3];
        CircuitConfig.renameCircuit(sender, oldName, newName);
        return true;
    }
}
