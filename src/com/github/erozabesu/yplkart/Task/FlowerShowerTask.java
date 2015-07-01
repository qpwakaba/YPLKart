package com.github.erozabesu.yplkart.Task;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.Utils.Particle;
import com.github.erozabesu.yplkart.Utils.Util;

public class FlowerShowerTask extends BukkitRunnable{
	int life = 0;
	int maxlife = 0;
	Player p;

	public FlowerShowerTask(Player p, int maxlife){
		this.p = p;
		this.maxlife = maxlife*20;
	}

	@Override
	public void run(){
		life++;

		if(maxlife < life || !p.isOnline()){
			this.cancel();
			return;
		}

		Location l = p.getLocation();
		if(life % 4 == 0){
			for(int i = 0;i < 10;i++){
				Particle.sendToLocation("REDSTONE", l, Util.getRandom(20), Util.getRandom(20), Util.getRandom(20), 1, 20);
			}
		}
	}
}
