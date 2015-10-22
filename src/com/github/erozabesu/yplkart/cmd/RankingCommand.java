package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.enumdata.TagType;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

/**
 * /ka rankingコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class RankingCommand extends Command {
    public RankingCommand() {
        super("ranking");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        // ka ranking {circuit name}
        // ka ranking list
        if (args.length == 2) {
            if (!Permission.hasPermission(sender, Permission.CMD_RANKING, false))
                return true;
            if (args[1].equalsIgnoreCase("list")) {
                MessageEnum.cmdCircuitList.sendConvertedMessage(sender);
                return true;
            } else {
                String circuitName = args[1];
                if (CircuitConfig.get(circuitName) == null) {
                    MessageEnum.invalidCircuit.sendConvertedMessage(sender, new MessageParts(TagType.CIRCUIT, circuitName));
                    return true;
                }

                CircuitConfig.sendRanking(sender, args[1]);
                return true;
            }

        // ka ranking {player name} {circuit name}
        // ka ranking all {circuit name}
        } else if (args.length == 3) {
            if (!Permission.hasPermission(sender, Permission.CMDOTHER_RANKING, false)) {
                return true;
            }

            String circuitName = args[2];
            Circuit circuit = CircuitConfig.get(circuitName);
            if (circuit == null) {
                MessageEnum.invalidCircuit.sendConvertedMessage(sender, MessageParts.getMessageParts(circuit));
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    CircuitConfig.sendRanking(other, circuitName);
                }
                MessageEnum.cmdRankingAll.sendConvertedMessage(sender, MessageParts.getMessageParts(circuit));

                return true;
            } else {
                String playerName = args[1];
                if (!CommonUtil.isOnline(playerName)) {
                    MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                    return true;
                }
                @SuppressWarnings("deprecation")
                Player other = Bukkit.getPlayer(playerName);
                CircuitConfig.sendRanking(other, circuitName);

                MessageParts circuitParts = MessageParts.getMessageParts(circuit);
                MessageParts playerParts = MessageParts.getMessageParts(other);
                MessageEnum.cmdRankingOther.sendConvertedMessage(sender, circuitParts, playerParts);
                return true;
            }
        } else {
            SystemMessageEnum.referenceRanking.sendConvertedMessage(sender);
            SystemMessageEnum.referenceRankingOther.sendConvertedMessage(sender);
            return true;
        }
    }
}