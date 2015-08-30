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

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        // ka entry {circuit name}
        if (args.length == 2) {
            if (!Permission.hasPermission(sender, Permission.CMD_ENTRY, false))
                return true;
            if (Util.isPlayer(sender)) {
                Player player = (Player) sender;
                String circuitName = args[1];
                if (CircuitConfig.getCircuitData(circuitName) == null) {
                    Circuit circuit = new Circuit();
                    circuit.setCircuitName(circuitName);
                    MessageEnum.invalidCircuit.sendConvertedMessage(sender, circuit);
                    return true;
                }

                RaceManager.setEntryRaceData(player.getUniqueId(), circuitName, false);
                return true;
            }
            // ka entry {player name} {circuit name}
            // ka entry all {circuit name}
        } else if (args.length == 3) {
            if (!Permission.hasPermission(sender, Permission.CMD_ENTRY.getTargetOtherPermission(), false))
                return true;
            String circuitName = args[2];
            if (CircuitConfig.getCircuitData(circuitName) == null) {
                Circuit circuit = new Circuit();
                circuit.setCircuitName(circuitName);
                MessageEnum.invalidCircuit.sendConvertedMessage(sender, circuit);
                return true;
            }
            if (args[1].equalsIgnoreCase("all")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    RaceManager.setEntryRaceData(other.getUniqueId(), args[2], false);
                }
                MessageEnum.cmdEntryAll.sendConvertedMessage(sender, args[2]);
                return true;
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
                RaceManager.setEntryRaceData(other.getUniqueId(), circuitName, false);
                return true;
            }
        }
        SystemMessageEnum.referenceEntry.sendConvertedMessage(sender);
        SystemMessageEnum.referenceEntryOther.sendConvertedMessage(sender);
        return true;
    }
}