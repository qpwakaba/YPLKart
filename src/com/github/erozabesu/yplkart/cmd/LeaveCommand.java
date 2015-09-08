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

public class LeaveCommand extends Command {
    public LeaveCommand() {
        super("leave");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        // ka leave
        if (args.length == 1) {
            if (!Permission.hasPermission(sender, Permission.CMD_LEAVE, false))
                return true;
            Player player = (Player) sender;
            RaceManager.leaveRacingKart(player);
            RaceManager.racerSetter_DeselectKart(player.getUniqueId());
            return true;
            // ka leave {player}
            // ka leave all
        } else if (args.length == 2) {
            if (!Permission.hasPermission(sender, Permission.CMDOTHER_LEAVE, false))
                return true;

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.leaveRacingKart(other);
                    RaceManager.racerSetter_DeselectKart(other.getUniqueId());
                }
                MessageEnum.cmdLeaveAll.sendConvertedMessage(sender);
                return true;
            } else {
                String playerName = args[1];
                if (!Util.isOnline(playerName)) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }

                @SuppressWarnings("deprecation")
                Player other = Bukkit.getPlayer(playerName);
                RaceManager.leaveRacingKart(other);
                RaceManager.racerSetter_DeselectKart(other.getUniqueId());
                MessageEnum.cmdLeaveOther.sendConvertedMessage(sender, MessageParts.getMessageParts(other));
                return true;
            }
        } else {
            SystemMessageEnum.referenceLeave.sendConvertedMessage(sender);
            SystemMessageEnum.referenceLeaveOther.sendConvertedMessage(sender);
            return true;
        }
    }
}