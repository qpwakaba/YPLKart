package com.github.erozabesu.yplkart.Object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Utils.Util;

public class Circuit {
	private String name;
	private int laptime;
	private boolean isstarted;
	private BukkitTask updatetask;
	private BukkitTask laptimetask;
	private List<UUID> entry;
	private List<Entity> jammerentity;

	public Circuit(final String name){
		this.name = name;
		init();

		this.laptimetask = Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
			public void run(){
				if(isStarted())
					laptime++;
			}
		}, 0, 1);

		this.updatetask = Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
			public void run(){
				if (isRaceEnd()){
					//プレイヤーデータの初期化はレース終了時のみ行うようにしなければ、
					//まだレース中のプレイヤーのスコアボード・ドロップアイテムに影響してしまう
					for(UUID id : entry){
						RaceManager.exit(Bukkit.getPlayer(id));
					}
					removeAllJammerEntity();
					init();

					RaceManager.endCircuit(name);
					Util.broadcastMessage("#Blue" + name + "#Aquaのレースが終了しました。");
				}
			}
		}, 0, 100);
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public void init(){
		this.laptime = 0;
		this.isstarted = false;
		this.entry = new ArrayList<UUID>();
		this.jammerentity = new ArrayList<Entity>();

		if(this.updatetask != null)
			this.updatetask.cancel();
		this.updatetask = null;

		if(this.laptimetask != null)
			this.laptimetask.cancel();
		this.laptimetask = null;
	}

	public void entryPlayer(UUID id){
		if(!this.entry.contains(id))
			this.entry.add(id);
	}

	public void setStart(boolean value){
		this.isstarted = value;
	}

	public void addJammerEntity(Entity e) {
		this.jammerentity.add(e);
	}

	public void removeJammerEntity(Entity entity) {
		if (this.jammerentity.contains(entity))
			this.jammerentity.remove(entity);
	}

	public void removeAllJammerEntity() {
		if (this.jammerentity.size() != 0) {
			for (Entity e : this.jammerentity) {
				if (!e.isDead())
					e.remove();
			}
			this.jammerentity.clear();
		}
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public String getName(){
		return this.name;
	}

	public int getLapTime(){
		return this.laptime;
	}

	public BukkitTask getTask(){
		return this.updatetask;
	}

	public int getLapMilliSeconds(){
		return laptime * 50;
	}

	public List<Player> getEntryPlayer(){
		List<Player> entry = new ArrayList<Player>();
		for(UUID id : this.entry){
			if(Bukkit.getPlayer(id).isOnline())
				entry.add(Bukkit.getPlayer(id));
		}
		return entry;
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public boolean isStarted(){
		return this.isstarted;
	}

	public boolean isRaceEnd(){
		Iterator<UUID> entrylist = this.entry.iterator();
		UUID id;
		while(entrylist.hasNext()){
			id = entrylist.next();
			if (RaceManager.isEntry(Bukkit.getPlayer(id)) && Bukkit.getPlayer(id).isOnline())
				return false;
		}

		return true;
	}

	public boolean isJammerEntity(Entity e) {
		return jammerentity.contains(e) ? true : false;
	}
}
