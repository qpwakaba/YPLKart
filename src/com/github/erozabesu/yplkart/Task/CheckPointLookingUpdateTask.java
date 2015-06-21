package com.github.erozabesu.yplkart.Task;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.erozabesu.yplkart.Utils.Particle;
import com.github.erozabesu.yplkart.Utils.Util;

public class CheckPointLookingUpdateTask extends BukkitRunnable{
	public Entity e;
	public ArrayList<UUID> sendPlayer = new ArrayList<UUID>();

	public CheckPointLookingUpdateTask(Entity e){
		this.e = e;
		/*for(Player other : Bukkit.getOnlinePlayers()){
			if(e.getLocation().distance(other.getLocation())>140)continue;

			PacketUtil.vanishUnLivingEntity(e, null);
			sendPlayer.add(other.getUniqueId());
		}*/
	}

	@Override
	public void run(){
		if(this.e.isDead()){
			this.cancel();
			return;
		}


		Particle.sendToLocation("REDSTONE", e.getLocation(), Util.getRandom(7), Util.getRandom(5) - 7, Util.getRandom(7), 1, 5);
		/*ArrayList<UUID> currentPlayer = new ArrayList<UUID>();

		for(Player other : Bukkit.getOnlinePlayers()){
			if(e.getLocation().distance(other.getLocation())>61)continue;
			//範囲内の送信した事がないプレイヤーにパケットを送る
			currentPlayer.add(other.getUniqueId());
			if(!sendPlayer.contains(other.getUniqueId())){
				PacketUtil.vanishUnLivingEntity(e, other);
				sendPlayer.add(other.getUniqueId());
			}
		}

		//さっきまで居たけど居なくなっているプレイヤーを配列からけす
		ArrayList<UUID> remove = new ArrayList<UUID>();
		for(UUID id : sendPlayer){
			if(!currentPlayer.contains(id))
				remove.add(id);
		}
		for(UUID id : remove){
			sendPlayer.remove(id);
		}*/
	}
}
