package main.java.com.github.erozabesu.YPLKart.Task;

import main.java.com.github.erozabesu.YPLKart.Utils.Particle;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FlowerShower extends BukkitRunnable{
	int life = 0;
	int maxlife = 0;
	Player p;

	public FlowerShower(Player p, int maxlife){
		this.p = p;
		this.maxlife = maxlife*20;
	}

	@Override
	public void run(){
		life++;

		if(maxlife < life){
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
