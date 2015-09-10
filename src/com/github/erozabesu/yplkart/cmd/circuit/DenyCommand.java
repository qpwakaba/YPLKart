package com.github.erozabesu.yplkart.cmd.circuit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.RaceManager;

public class DenyCommand extends Command {
    public DenyCommand() {
        super("deny");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length != 2 || !(sender instanceof Player)) {
            //TODO: ここでコマンドの使い方を表示させられる。
            return false;
        }

        Player player = (Player)sender;
        RaceManager.circuitSetter_DenyMatching(RaceManager.getRacer(player));
        return true;
    }
}
