package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.RaceManager;

/**
 * /ka circuit acceptコマンドクラス。
 * @author King
 * @author erozabesu
 */
public class AcceptCommand extends Command {
    public AcceptCommand() {
        super("accept");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            // TODO: ここでコマンドの使い方を表示させられる。
            return false;
        }

        Player player = (Player) sender;
        RaceManager.circuitSetter_AcceptMatching(RaceManager.getRacer(player));

        return true;
    }
}
