package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.RaceType;

public class SetRaceTypeCommand extends Command {
    public SetRaceTypeCommand() {
        super("setracetype");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_CIRCUIT, false)) {
            return true;
        }

        if (args.length != 4) {
            // TODO: ここでコマンドの説明を表示させられる。
            return false;
        }

        RaceType raceType = RaceType.getRaceTypeByString(args[3]);
        if (raceType == null) {
            MessageEnum.invalidRaceType.sendConvertedMessage(sender);
            return true;
        }

        String circuitName = args[2];
        CircuitConfig.setRaceType(sender, circuitName, raceType);
        return true;
    }
}
