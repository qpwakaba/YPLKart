package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

/**
 * /ka kartコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class KartCommand extends Command {
    public KartCommand() {
        super("kart");
    }
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        // ka ride {kart name}
        // ka ride random
        if (args.length == 2 && sender instanceof Player) {
            if (!Permission.hasPermission(sender, Permission.CMD_KART, false))
                return true;
            Player player = (Player) sender;
            Kart kart;
            if (args[1].equalsIgnoreCase("random")) {
                kart = KartConfig.getRandomKart();
            } else {
                kart = KartConfig.getKart(args[1]);
                if (kart == null) {
                    MessageEnum.invalidKart.sendConvertedMessage(sender);
                    return true;
                }
            }
            RaceManager.racerSetter_Kart(player.getUniqueId(), kart);
            return true;
            // ka ride all {kart name}
            // ka ride {player name} {kart name}
            // ka ride all random
            // ka ride {player name} random
        } else if (args.length == 3) {
            if (!Permission.hasPermission(sender, Permission.CMDOTHER_KART, false))
                return true;
            Kart kart = null;
            boolean isRandomKart = args[2].equalsIgnoreCase("random");
            if (isRandomKart) {
                kart = KartConfig.getRandomKart();
            } else {
                kart = KartConfig.getKart(args[2]);
                if (kart == null) {
                    MessageEnum.invalidKart.sendConvertedMessage(sender);
                    return true;
                }
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.racerSetter_Kart(other.getUniqueId(), kart);
                }
                if (isRandomKart)
                    MessageEnum.cmdRideRandomAll.sendConvertedMessage(sender);
                else
                    MessageEnum.cmdRideAll.sendConvertedMessage(sender, MessageParts.getMessageParts(kart));

                return true;
            } else {
                String playerName = args[1];
                if (!CommonUtil.isOnline(playerName)) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }
                @SuppressWarnings("deprecation")
                Player other = Bukkit.getPlayer(playerName);
                RaceManager.racerSetter_Kart(other.getUniqueId(), kart);
                MessageEnum.cmdRideOther.sendConvertedMessage(sender, MessageParts.getMessageParts(other), MessageParts.getMessageParts(kart));
                return true;
            }
        } else {
            SystemMessageEnum.referenceRide.sendConvertedMessage(sender);
            SystemMessageEnum.referenceRideOther.sendConvertedMessage(sender);
            return true;
        }
    }
}