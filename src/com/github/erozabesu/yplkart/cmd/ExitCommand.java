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
import com.github.erozabesu.yplkart.utils.Util;

public class ExitCommand extends Command {
    public ExitCommand() {
        super("exit");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            if (!Permission.hasPermission(sender, Permission.CMD_EXIT, false)) {
                return true;
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;
                RaceManager.racerSetter_UnEntry(RaceManager.getRacer(player));
                return true;
            }
        } else if (args.length == 2) {
            if (!Permission.hasPermission(sender, Permission.CMDOTHER_EXIT, false)) {
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.racerSetter_UnEntry(RaceManager.getRacer(other));
                }
                MessageEnum.cmdExitAll.sendConvertedMessage(sender);
                return true;
            } else {
                String playerName = args[1];
                if (!Util.isOnline(playerName)) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }

                @SuppressWarnings("deprecation")
                Player other = Bukkit.getPlayer(playerName);
                RaceManager.racerSetter_UnEntry(RaceManager.getRacer(other));
                MessageEnum.cmdExitOther.sendConvertedMessage(sender, MessageParts.getMessageParts(other));
                return true;
            }
        }
        SystemMessageEnum.referenceExit.sendConvertedMessage(sender);
        SystemMessageEnum.referenceExitOther.sendConvertedMessage(sender);
        return true;
    }
}