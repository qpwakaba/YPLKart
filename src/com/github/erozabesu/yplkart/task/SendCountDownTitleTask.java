package com.github.erozabesu.yplkart.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.PacketUtil;

public class SendCountDownTitleTask extends BukkitRunnable {
    int life = 0;
    int maxlife = 0;
    Player player;
    Racer racer;
    String text;

    public SendCountDownTitleTask(Player player, int maxlife, String text) {
        this.player = player;
        this.racer = RaceManager.getRacer(player);
        this.maxlife = maxlife * 20;
        this.text = text;

        PacketUtil.sendTitle(player, text, 0, 25, 0, false);
        PacketUtil.sendTitle(player, MessageEnum.titleCountDown.getConvertedMessage(MessageParts.getMessageParts(maxlife)), 0, 25, 0, true);
    }

    @Override
    public void run() {
        life++;

        if (this.racer.isGoal()) {
            this.cancel();
            return;
        }

        if (!this.racer.isEntry()) {
            this.cancel();
            return;
        }

        if (maxlife < life) {
            this.cancel();
            return;
        }

        if (life % 20 == 0) {
            if (((int) (maxlife - life) / 20) != 0) {
                PacketUtil.sendTitle(player, text, 0, 25, 0, false);
                PacketUtil.sendTitle(player, MessageEnum.titleCountDown.getConvertedMessage(MessageParts.getMessageParts((maxlife - life) / 20)), 0, 25, 0, true);
            }
        }
    }
}
