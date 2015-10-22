package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.SystemMessageEnum;
import com.github.erozabesu.yplkart.enumdata.TagType;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;

/**
 * /ka entryコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class EntryCommand extends Command {
    public EntryCommand() {
        super("entry");
    }

    /*
     * ka entry [サーキット]
     * ka entry [プレイヤー] [サーキット]
     * ka entry all/@a [サーキット]
     * ka entry [サーキット] -f
     * ka entry [プレイヤー] [サーキット] -f
     * ka entry all/@a [サーキット] -f
     */
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        // ka entry [サーキット]
        if (args.length == 2) {
            if (!Permission.hasPermission(sender, Permission.CMD_ENTRY, false)) {
                return true;
            }

            if (CommonUtil.isPlayer(sender)) {
                // 存在しないサーキットを指定した場合はreturn
                String circuitName = args[1];
                if (CircuitConfig.get(circuitName) == null) {
                    MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);
                    MessageEnum.invalidCircuit.sendConvertedMessage(sender, circuitParts);
                    return true;
                }

                Player player = (Player) sender;
                RaceManager.racerSetter_Entry(RaceManager.getRacer(player), circuitName, false);

                return true;
            } else {
                SystemMessageEnum.referenceEntryOther.sendConvertedMessage(sender);

                return true;
            }

        /*
         * ka entry [プレイヤー] [サーキット]
         * ka entry all/@a [サーキット]
         * ka entry [サーキット] -f
         */
        } else if (args.length == 3) {
            // 存在しないサーキットを指定した場合はreturn
            String circuitName = args[2].equalsIgnoreCase("-f") ? args[1] : args[2];
            if (CircuitConfig.get(circuitName) == null) {
                MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);
                MessageEnum.invalidCircuit.sendConvertedMessage(sender, circuitParts);
                return true;
            }

            if (args[2].equalsIgnoreCase("-f")) {
                if (!Permission.hasPermission(sender, Permission.CMD_ENTRY, false)) {
                    return true;
                }

                if (CommonUtil.isPlayer(sender)) {
                    Player player = (Player) sender;
                    RaceManager.racerSetter_Entry(RaceManager.getRacer(player), circuitName, true);

                    return true;
                } else {
                    SystemMessageEnum.referenceEntryOther.sendConvertedMessage(sender);

                    return true;
                }
            } else {
                if (!Permission.hasPermission(sender, Permission.CMDOTHER_ENTRY, false)) {
                    return true;
                }

                // ka entry all/@a [サーキット]
                if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("@a")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.racerSetter_Entry(RaceManager.getRacer(other), args[2], false);
                    }

                    MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, args[2]);
                    MessageEnum.cmdEntryAll.sendConvertedMessage(sender, circuitParts);

                    return true;

                // ka entry [プレイヤー] [サーキット]
                } else {
                    String playerName = args[1];
                    if (!CommonUtil.isOnline(playerName)) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                        return true;
                    }

                    @SuppressWarnings("deprecation")
                    Player other = Bukkit.getPlayer(playerName);
                    MessageParts playerParts = MessageParts.getMessageParts(other);
                    MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);
                    MessageEnum.cmdEntryOther.sendConvertedMessage(sender, circuitParts, playerParts);
                    RaceManager.racerSetter_Entry(RaceManager.getRacer(other), circuitName, false);

                    return true;
                }
            }

       /*
        * ka entry [プレイヤー] [サーキット] -f
        * ka entry all/@a [サーキット] -f
        */
        } else if (args.length == 4) {
            if (args[3].equalsIgnoreCase("-f")) {
                if (!Permission.hasPermission(sender, Permission.CMDOTHER_ENTRY, false)) {
                    return true;
                }

                // 存在しないサーキットを指定した場合はreturn
                String circuitName = args[2];
                if (CircuitConfig.get(circuitName) == null) {
                    MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);
                    MessageEnum.invalidCircuit.sendConvertedMessage(sender, circuitParts);
                    return true;
                }

                // ka entry all/@a [サーキット] -f
                if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("@a")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.racerSetter_Entry(RaceManager.getRacer(other), args[2], true);
                    }

                    MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, args[2]);
                    MessageEnum.cmdEntryForceAll.sendConvertedMessage(sender, circuitParts);

                    return true;

                // ka entry [プレイヤー] [サーキット] -f
                } else {
                    String playerName = args[1];
                    if (!CommonUtil.isOnline(playerName)) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                        return true;
                    }

                    @SuppressWarnings("deprecation")
                    Player other = Bukkit.getPlayer(playerName);
                    MessageParts playerParts = MessageParts.getMessageParts(other);
                    MessageParts circuitParts = new MessageParts(TagType.CIRCUIT, circuitName);
                    MessageEnum.cmdEntryForceOther.sendConvertedMessage(sender, circuitParts, playerParts);
                    RaceManager.racerSetter_Entry(RaceManager.getRacer(other), circuitName, true);

                    return true;
                }
            }
        }

        SystemMessageEnum.referenceEntry.sendConvertedMessage(sender);
        SystemMessageEnum.referenceEntryOther.sendConvertedMessage(sender);

        return true;
    }
}