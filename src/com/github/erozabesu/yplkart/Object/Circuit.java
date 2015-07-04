package com.github.erozabesu.yplkart.Object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Data.RaceData;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Task.SendCountDownTitleTask;
import com.github.erozabesu.yplkart.Utils.Util;

public class Circuit{
	private String name;
	private int laptime;
	private int limittime;
	private int matchingcountdown;
	private boolean isstarted;
	private boolean ismatching;
	private BukkitTask countuptask;
	private BukkitTask detectend;
	private BukkitTask detectreadytask;
	private BukkitTask matchingtask;
	private List<UUID> entry;
	private List<UUID> reserveentry;
	private List<UUID> matchingaccept;
	private List<Entity> jammerentity;

	/*
	 * マッチングの仕様
	 * 規定の参加人数を満たす
	 * 		レディタスクをキャンセルし、制限時間付きのマッチングタスクを起動
	 * 			コマンドから同意を得られたプレイヤーをリストアップ
	 * 			同意を得られなかったプレイヤー、制限時間内に返答がなかったプレイヤーはエントリーを取り消す
	 * 			最終的な参加人数が規定人数を満たしていればレースを開始する
	 * 			満たしていなければ最後に残ったエントリープレイヤーを引き継ぎ新規にレディタスクを起動し最初に戻る
	 *
	 * 			制限時間内に新規にエントリーしたプレイヤーがいた場合もマッチングタスクの範疇として扱う
	 * 			この場合は既に同意を得たものとし、制限時間が0になるまで待機してもらう
	 * 			制限時間を加算し、同じく同意を得る形にした場合、いたずら目的でエントリーを繰り返す
	 * 			プレイヤーが出る可能性があるためこの仕様に落ち着いた
	 */
	public Circuit(final String name){
		this.name = name;
		this.limittime = RaceData.getLimitTime(name);
		init();

		runCountUpTask();
		runDetectEndTask();
		runDetectReadyTask();
	}

	private void setupRacer(){
		for(UUID id : entry){
			if(Bukkit.getPlayer(id) != null){
				if(Bukkit.getPlayer(id).isOnline()){
					Player p = Bukkit.getPlayer(id);
					Race r = RaceManager.getRace(p);

					Scoreboards.entryCircuit(p);
					r.init();
					r.setEntry(name);
					RaceManager.characterReset(p);
					RaceManager.leave(p);
					r.setStandBy(true);
					r.savePlayerData();

					p.getInventory().clear();
					p.setLevel(0);
					p.setExp(0);

					p.leaveVehicle();
					p.teleport(RaceData.getPosition(name));
					RaceManager.showCharacterSelectMenu(p);
					EnumItem.addItem(p, EnumItem.Menu.getItem());

					continue;
				}
			}
			exitPlayer(id);
		}
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public void init(){
		this.laptime = 0;
		this.matchingcountdown = 0;
		this.isstarted = false;
		this.ismatching = false;
		this.entry = new ArrayList<UUID>();
		this.reserveentry = new ArrayList<UUID>();
		this.matchingaccept = new ArrayList<UUID>();
		this.jammerentity = new ArrayList<Entity>();

		if(this.detectend != null)
			detectend.cancel();
		this.detectend = null;

		if(this.countuptask != null)
			this.countuptask.cancel();
		this.countuptask = null;

		if(this.detectreadytask != null)
			this.detectreadytask.cancel();
		this.detectreadytask = null;

		if(this.matchingtask != null)
			this.matchingtask.cancel();
		this.matchingtask = null;
	}

	public void endRace(){
		sendMessageEntryPlayer("#Blue" + Util.convertInitialUpperString(name) + "#Aquaのレースが終了しました");
		Iterator<UUID> i = entry.iterator();
		UUID id;
		while(i.hasNext()){
			id = i.next();
			i.remove();
			RaceManager.exit(Bukkit.getPlayer(id));
		}

		//リザーブエントリーがあれば終了処理後に改めてサーキットを新規作成する
		final List<UUID> nextentry = new ArrayList<UUID>(reserveentry);
		if(0 < nextentry.size()){
			Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
				public void run(){
					Circuit c = RaceManager.setupCircuit(name);
					for(UUID id : nextentry){
						RaceManager.entry(Bukkit.getPlayer(id), name);
						c.entryPlayer(id);
					}
				}
			}, 10);
		}

		//初期化
		removeAllJammerEntity();
		init();
		RaceManager.endCircuit(name);
		Scoreboards.endCircuit(name);
	}

	public void entryPlayer(UUID id){
		if(!this.entry.contains(id))
			this.entry.add(id);
	}

	public void entryPlayer(Player p){
		if(!this.entry.contains(p.getUniqueId()))
			this.entry.add(p.getUniqueId());
	}

	public void entryReservePlayer(UUID id){
		if(!this.reserveentry.contains(id))
			this.reserveentry.add(id);
	}

	public void entryReservePlayer(Player p){
		if(!this.reserveentry.contains(p.getUniqueId()))
			this.reserveentry.add(p.getUniqueId());
	}

	public void exitPlayer(UUID id){
		if(this.entry.contains(id))
			this.entry.remove(id);

		if(this.reserveentry.contains(id))
			this.reserveentry.remove(id);
	}

	public void exitPlayer(Player p){
		if(this.entry.contains(p.getUniqueId()))
			this.entry.remove(p.getUniqueId());

		if(this.reserveentry.contains(p.getUniqueId()))
			this.reserveentry.remove(p.getUniqueId());
	}

	public void acceptMatching(UUID id){
		if(!this.matchingaccept.contains(id))
			this.matchingaccept.add(id);
	}

	public void acceptMatching(Player p){
		if(!this.matchingaccept.contains(p.getUniqueId()))
			this.matchingaccept.add(p.getUniqueId());
	}

	public void denyMatching(UUID id){
		if(this.matchingaccept.contains(id))
			this.matchingaccept.remove(id);
	}

	public void denyMatching(Player p){
		if(this.matchingaccept.contains(p.getUniqueId()))
			this.matchingaccept.remove(p.getUniqueId());
	}

	public void setStart(boolean value){
		this.isstarted = value;
	}

	public void setMatching(boolean value){
		this.ismatching = value;
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

	public void runCountUpTask(){
		if(this.countuptask != null)
			this.countuptask.cancel();

		this.countuptask = Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
			public void run(){
				if(isStarted()){
					laptime++;

					if(laptime % 20 == 0){
						int remaintime = limittime - laptime/20;
						if(remaintime == 60){
							sendMessageEntryPlayer("#Gray======== #Aqua残り時間#White60秒#Aqua！ #Gray========");
						}else if(remaintime == 30){
							sendMessageEntryPlayer("#Gray======== #Aqua残り時間#White30秒#Aqua！ #Gray========");
						}else if(remaintime == 10){
							sendMessageEntryPlayer("#Gray======== #Red残り時間#White10秒#Aqua！ #Gray========");
						}else if(0 < remaintime && remaintime < 10){
							sendMessageEntryPlayer("#Gray========       #White" + remaintime + "#Red！        #Gray========");
						}else if(remaintime == 0){
							sendMessageEntryPlayer("#Gray======== #Redタイムアップ！  #Gray========");
							endRace();
						}
					}
				}
			}
		}, 0, 1);
	}

	public void runDetectEndTask(){
		if(this.detectend != null)
			this.detectend.cancel();

		this.detectend = Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
			public void run(){
				if (isRaceEnd()){
					endRace();
				}
			}
		}, 10, 100);
	}

	public void runDetectReadyTask(){
		if(this.detectreadytask != null)
			this.detectreadytask.cancel();

		this.detectreadytask = Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
			public void run(){
				//エントリーしたプレイヤーが規定人数以上
				if(entry.size() < RaceData.getMinPlayer(name))return;
				//オンラインのプレイヤー人数が規定人数以上
				if(getEntryPlayer().size() < RaceData.getMinPlayer(name))return;
				runMatchingTask();
				detectreadytask.cancel();
				detectreadytask = null;
			}
		}, 0, 100);
	}

	public void runMatchingTask(){
		if(this.matchingtask != null)
			this.matchingtask.cancel();

		this.matchingcountdown = RaceData.getMatchingTime(this.name);
		setMatching(true);
		String tellraw = " [\"\",{\"text\":\"========\",\"color\":\"gray\",\"bold\":\"true\"},{\"text\":\"[参加する]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ka circuit accept\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"レースへの参加を承認します\n\",\"color\":\"yellow\"},{\"text\":\"承認した参加者が規定人数を満たせばレースが開始されます\",\"color\":\"yellow\"}]}},\"bold\":\"false\"},{\"text\":\"====\",\"color\":\"gray\",\"bold\":\"true\"},{\"text\":\"[辞退する]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ka circuit deny\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"レースの参加を辞退し、エントリーを取り消します\",\"color\":\"yellow\"}]}},\"bold\":\"false\"},{\"text\":\"========\",\"color\":\"gray\",\"bold\":\"true\"}]";

		for(UUID id : entry){
			Player p = Bukkit.getPlayer(id);
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
			new SendCountDownTitleTask(p, this.matchingcountdown, "レースの準備が整いました", ChatColor.WHITE, ChatColor.YELLOW).runTaskTimer(YPLKart.getInstance(), 0, 1);
			Util.sendMessageNoHeader(p, "#Aquaレースを開始する準備が整いました");
			Util.sendMessageNoHeader(p, "#Aqua↓↓↓のチャットをクリックして参加を確定して下さい");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + tellraw);
		}

		this.matchingtask = Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
			public void run(){
				matchingcountdown--;
				if(matchingcountdown < 0){
					Iterator<UUID> exitlist = entry.iterator();
					UUID exitid;
					while(exitlist.hasNext()){
						exitid = exitlist.next();
						if(!Bukkit.getPlayer(exitid).isOnline() || !matchingaccept.contains(exitid)){
							exitlist.remove();
							denyMatching(exitid);
							RaceManager.exit(Bukkit.getPlayer(exitid));
						}
					}

					if(RaceData.getMinPlayer(name) <= matchingaccept.size()){
						matchingaccept.clear();
						matchingcountdown = 0;
						matchingtask.cancel();
						matchingtask = null;

						setupRacer();
						Scoreboards.startCircuit(name);
						sendMessageEntryPlayer("#Blue" + Util.convertInitialUpperString(name) + "#Aquaのレースを開始します");
						return;
					}

					//マッチングに失敗した場合
					sendMessageEntryPlayer("#Aqua辞退者が出たため再マッチングを行います。規定人数が揃うまでお待ち下さい");
					setMatching(false);
					matchingaccept.clear();
					matchingcountdown = 0;
					matchingtask.cancel();
					matchingtask = null;
					runDetectReadyTask();
				}
			}
		}, 0, 20);
	}

	public void sendMessageEntryPlayer(String message){
		for(Player p : getEntryPlayer()){
			Util.sendMessageNoHeader(p, message);
		}
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public String getName(){
		return this.name;
	}

	public int getLapTime(){
		return this.laptime;
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

	public boolean isMatching(){
		return this.ismatching;
	}

	//レースが終了しているかどうか判定する
	public boolean isRaceEnd(){
		Iterator<Player> i = getEntryPlayer().iterator();
		Player p;
		if(isStarted()){
			while(i.hasNext()){
				p = i.next();
				if(RaceManager.isEntry(p)
						&& RaceManager.getRace(p).getStart()
						&& !RaceManager.getRace(p).getGoal())
							return false;
			}
		}else{
			while(i.hasNext()){
				p = i.next();
				if(RaceManager.isEntry(p))
					return false;
			}
		}

		return true;
	}

	public boolean isJammerEntity(Entity e) {
		return jammerentity.contains(e) ? true : false;
	}
}
