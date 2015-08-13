package com.github.erozabesu.yplkart.task;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.utils.PacketUtil;

public class FlowerShowerTask extends BukkitRunnable {
    int life = 0;
    int maxlife = 0;
    Player player;

    public FlowerShowerTask(Player p, int maxlife) {
        this.player = p;
        this.maxlife = maxlife * 20;
    }

    @Override
    public void run() {
        life++;

        if (maxlife < life || !player.isOnline()) {
            this.cancel();
            return;
        }

        Location location = player.getLocation();
        if (life % 4 == 0) {
            PacketUtil.sendParticlePacket(null, Particle.REDSTONE, location, 7.0F, 7.0F, 7.0F, 1, 100, new int[]{});
        }
    }
}
