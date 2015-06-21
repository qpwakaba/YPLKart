package main.java.com.github.erozabesu.YPLKart.Task;

import java.util.ArrayList;
import java.util.UUID;

import main.java.com.github.erozabesu.YPLKart.Enum.EnumCharacter;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;
import main.java.com.github.erozabesu.YPLKart.Utils.PacketUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLookingUpdateTask extends BukkitRunnable{
	public String id;
	public EnumCharacter job;
	public ArrayList<String> sendPlayer = new ArrayList<String>();

	public PlayerLookingUpdateTask(String id, EnumCharacter job){
		this.id = id;
		this.job = job;
		Player p = Bukkit.getPlayer(UUID.fromString(this.id));
		for(Player other : Bukkit.getOnlinePlayers()){
			if(other.getUniqueId().toString().equalsIgnoreCase(id))continue;
			if(!other.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))continue;
			if(p.getLocation().distance(other.getLocation())>120)continue;

			PacketUtil.disguise(p, other, this.job);
			sendPlayer.add(other.getUniqueId().toString());
		}
	}

	@Override
	public void run(){
		Player p = Bukkit.getPlayer(UUID.fromString(this.id));
		if(p == null){
			this.cancel();
			return;
		}
		if(RaceManager.getRace(p).getCharacter() == null){
			PacketUtil.returnPlayer(p);
			this.cancel();
			return;
		}

		ArrayList<String> currentPlayer = new ArrayList<String>();

		for(Player other : Bukkit.getOnlinePlayers()){
			if(other.getUniqueId().toString().equalsIgnoreCase(this.id))continue;
			if(!other.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))continue;
			if(p.getLocation().distance(other.getLocation())>120)continue;

			//範囲内の送信した事がないプレイヤーにパケットを送る
			currentPlayer.add(other.getUniqueId().toString());
			if(!sendPlayer.contains(other.getUniqueId().toString())){
				PacketUtil.disguise(p, other, this.job);
				sendPlayer.add(other.getUniqueId().toString());
			}
		}

		//さっきまで居たけど居なくなっているプレイヤーを配列からけす
		ArrayList<String> remove = new ArrayList<String>();
		for(String id : sendPlayer){
			if(!currentPlayer.contains(id))
				remove.add(id);
		}
		for(String id : remove){
			sendPlayer.remove(id);
			//PacketUtil.death(p);
			//PacketUtil.disguise(p, Bukkit.getPlayer(UUID.fromString(id)), EnumCharacter.Human);
		}
	}
}
