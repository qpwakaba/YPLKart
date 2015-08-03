package com.github.erozabesu.yplkart.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.utils.PacketUtil;

public class SendCountDownTitleTask extends BukkitRunnable {
    int life = 0;
    int maxlife = 0;
    Player p;
    String text;

    public SendCountDownTitleTask(Player p, int maxlife, String text) {
        this.p = p;
        this.maxlife = maxlife * 20;
        this.text = text;

        PacketUtil.sendTitle(p, text, 0, 25, 0, false);
        PacketUtil.sendTitle(p, MessageEnum.titleCountDown.getConvertedMessage(maxlife), 0, 25, 0, true);
    }

    @Override
    public void run() {
        life++;

        if (RaceManager.getRacer(this.p).isGoal()) {
            this.cancel();
            return;
        }

        if (!RaceManager.isEntry(this.p.getUniqueId())) {
            this.cancel();
            return;
        }

        if (maxlife < life) {
            this.cancel();
            return;
        }

        if (life % 20 == 0) {
            if (((int) (maxlife - life) / 20) != 0) {
                PacketUtil.sendTitle(p, text, 0, 25, 0, false);
                PacketUtil.sendTitle(p, MessageEnum.titleCountDown.getConvertedMessage((maxlife - life) / 20), 0, 25, 0, true);
            }
        }
    }
}
