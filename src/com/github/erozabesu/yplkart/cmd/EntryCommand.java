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
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.utils.Util;

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

            if (Util.isPlayer(sender)) {
                // 存在しないサーキットを指定した場合はreturn
                String circuitName = args[1];
                if (CircuitConfig.getCircuitData(circuitName) == null) {
                    Circuit circuit = new Circuit();
                    circuit.setCircuitName(circuitName);
                    MessageEnum.invalidCircuit.sendConvertedMessage(sender, circuit);
                    return true;
                }

                Player player = (Player) sender;
                RaceManager.racerSetter_Entry(player.getUniqueId(), circuitName, false);

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
            if (CircuitConfig.getCircuitData(circuitName) == null) {
                Circuit circuit = new Circuit();
                circuit.setCircuitName(circuitName);
                MessageEnum.invalidCircuit.sendConvertedMessage(sender, circuit);
                return true;
            }

            if (args[2].equalsIgnoreCase("-f")) {
                if (!Permission.hasPermission(sender, Permission.CMD_ENTRY, false)) {
                    return true;
                }

                if (Util.isPlayer(sender)) {
                    Player player = (Player) sender;
                    RaceManager.racerSetter_Entry(player.getUniqueId(), circuitName, true);

                    return true;
                } else {
                    SystemMessageEnum.referenceEntryOther.sendConvertedMessage(sender);

                    return true;
                }
            } else {
                if (!Permission.hasPermission(sender, Permission.CMD_ENTRY.getTargetOtherPermission(), false)) {
                    return true;
                }

                // ka entry all/@a [サーキット]
                if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("@a")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.racerSetter_Entry(other.getUniqueId(), args[2], false);
                    }
                    MessageEnum.cmdEntryAll.sendConvertedMessage(sender, args[2]);

                    return true;

                // ka entry [プレイヤー] [サーキット]
                } else {
                    String playerName = args[1];
                    if (!Util.isOnline(playerName)) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                        return true;
                    }

                    @SuppressWarnings("deprecation")
                    Player other = Bukkit.getPlayer(playerName);
                    Circuit circuit = new Circuit();
                    circuit.setCircuitName(circuitName);
                    MessageEnum.cmdEntryOther.sendConvertedMessage(sender, circuit, other);
                    RaceManager.racerSetter_Entry(other.getUniqueId(), circuitName, false);

                    return true;
                }
            }

       /*
        * ka entry [プレイヤー] [サーキット] -f
        * ka entry all/@a [サーキット] -f
        */
        } else if (args.length == 4) {
            if (args[3].equalsIgnoreCase("-f")) {
                if (!Permission.hasPermission(sender, Permission.CMD_ENTRY.getTargetOtherPermission(), false)) {
                    return true;
                }

                // 存在しないサーキットを指定した場合はreturn
                String circuitName = args[2];
                if (CircuitConfig.getCircuitData(circuitName) == null) {
                    Circuit circuit = new Circuit();
                    circuit.setCircuitName(circuitName);
                    MessageEnum.invalidCircuit.sendConvertedMessage(sender, circuit);
                    return true;
                }

                // ka entry all/@a [サーキット] -f
                if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("@a")) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        RaceManager.racerSetter_Entry(other.getUniqueId(), args[2], true);
                    }

                    Circuit circuit = new Circuit();
                    circuit.setCircuitName(args[2]);
                    MessageEnum.cmdEntryForceAll.sendConvertedMessage(sender, circuit);

                    return true;

                // ka entry [プレイヤー] [サーキット] -f
                } else {
                    String playerName = args[1];
                    if (!Util.isOnline(playerName)) {
                        MessageEnum.invalidPlayer.sendConvertedMessage(sender);
                        return true;
                    }

                    @SuppressWarnings("deprecation")
                    Player other = Bukkit.getPlayer(playerName);
                    Circuit circuit = new Circuit();
                    circuit.setCircuitName(circuitName);
                    MessageEnum.cmdEntryForceOther.sendConvertedMessage(sender, circuit, other);
                    RaceManager.racerSetter_Entry(other.getUniqueId(), circuitName, true);

                    return true;
                }
            }
        }

        SystemMessageEnum.referenceEntry.sendConvertedMessage(sender);
        SystemMessageEnum.referenceEntryOther.sendConvertedMessage(sender);

        return true;
    }
}