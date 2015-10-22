package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

/**
 * /ka menuコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class MenuCommand extends Command {
    public MenuCommand() {
        super("menu");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            if (!Permission.hasPermission(sender, Permission.CMD_MENU, false))
                return true;
            if (CommonUtil.isPlayer(sender)) {
                RaceManager.showSelectMenu((Player) sender, true);
                return true;
            }
        } else if (args.length == 2) {
            if (!Permission.hasPermission(sender, Permission.CMDOTHER_MENU, false))
                return true;

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.showSelectMenu(other, true);
                }
                MessageEnum.cmdMenuAll.sendConvertedMessage(sender);
                return true;
            } else {
            	String playerName = args[1];
                if (!CommonUtil.isOnline(playerName)) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }

                @SuppressWarnings("deprecation")
                Player other = Bukkit.getPlayer(playerName);
                RaceManager.showSelectMenu(other, true);
                MessageEnum.cmdMenuOther.sendConvertedMessage(sender, MessageParts.getMessageParts(other));
                return true;
            }
        }
        SystemMessageEnum.referenceMenu.sendConvertedMessage(sender);
        SystemMessageEnum.referenceMenuOther.sendConvertedMessage(sender);
        return true;
    }
}