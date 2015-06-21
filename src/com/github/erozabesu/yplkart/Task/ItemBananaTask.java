package com.github.erozabesu.yplkart.Task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Object.RaceManager;

public class ItemBananaTask extends BukkitRunnable{
	Entity banana;
	Location l;

	public ItemBananaTask(Entity banana, Location l){
		this.banana = banana;
		this.l = l;
		RaceManager.addJammerEntity(banana);
	}

	@Override
	public void run(){
			if(!RaceManager.isRaceEnd()){
				if(banana.isDead()){
					if(RaceManager.isJammerEntity(this.banana)){
						RaceManager.removeJammerEntity(this.banana);

						FallingBlock b = l.getWorld().spawnFallingBlock(l, Material.HUGE_MUSHROOM_1, (byte) 8);
						b.setCustomName(EnumItem.Banana.getName());
						b.setCustomNameVisible(false);
						b.setDropItem(false);

						this.banana = b;
						RaceManager.addJammerEntity(this.banana);
					//誰かがバナナに接触してバナナが消滅していた場合
					}else{
						this.cancel();
						this.banana.remove();
						return;
					}
				}
			}else{
				this.cancel();
				this.banana.remove();
				return;
			}

		banana.setVelocity(new Vector(0,0.04,0));
	}
}
