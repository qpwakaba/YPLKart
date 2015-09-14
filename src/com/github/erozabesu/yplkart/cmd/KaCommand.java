package com.github.erozabesu.yplkart.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;

/**
 * /kaコマンドクラス。
 * @author King
 * @author erozabesu
 */
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
            } else {
                if (!CommandEnum.execute(sender, label, args))
                    SystemMessageEnum.reference.sendConvertedMessage(sender);
                return true;
            }
        }
        return false;
    }
}