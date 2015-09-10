package com.github.erozabesu.yplkart.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Permission.hasPermission(sender, Permission.OP_CMD_RELOAD, false)) {
            return true;
        }

        // 全プレイヤーのエントリーを解除
        for (Player other : Bukkit.getOnlinePlayers()) {
            RaceManager.racerSetter_UnEntry(RaceManager.getRacer(other));
        }

        // 全サーキットのレースをリザーブエントリーを破棄して終了
        RaceManager.endAllCircuit(false);

        // コンフィグのリロード
        ConfigManager.reloadAllConfig();

        // 全DisplayKartオブジェクトのEntityを再生成する
        DisplayKartConfig.respawnAllKart();

        MessageEnum.cmdReload.sendConvertedMessage(sender);
        return true;
    }
}