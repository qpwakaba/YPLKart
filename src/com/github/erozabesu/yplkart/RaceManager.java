package com.github.erozabesu.yplkart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.Data.DisplayKartData;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Enum.EnumKarts;
import com.github.erozabesu.yplkart.Enum.EnumSelectMenu;
import com.github.erozabesu.yplkart.Enum.Permission;
import com.github.erozabesu.yplkart.Object.Circuit;
import com.github.erozabesu.yplkart.Object.Race;
import com.github.erozabesu.yplkart.Utils.PacketUtil;
import com.github.erozabesu.yplkart.Utils.ReflectionUtil;
import com.github.erozabesu.yplkart.Utils.Util;

public class RaceManager {
	public static int checkPointHeight = 8;
	public static int checkPointDetectRadius = 20;
	private static HashMap<UUID, Race> racedata = new HashMap<UUID, Race>();
	private static HashMap<String, Circuit> circuit = new HashMap<String, Circuit>();

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static Circuit setupCircuit(String circuitname){
		if(circuit.get(circuitname) == null)
			circuit.put(circuitname, new Circuit(circuitname));

		return circuit.get(circuitname);
	}

	public static void endCircuit(String circuitname){
		if(circuit.get(circuitname) != null){
			circuit.get(circuitname).init();
			circuit.remove(circuitname);
		}
	}

	public static void endAllCircuit(){
		for(Circuit c : circuit.values()){
			c.init();
		}
		circuit.clear();
	}

	public static void setMatchingCircuitData(UUID id){
		Circuit c = getCircuit(id);
		if(c == null){
			Util.sendMessage(id, "#Redレースにエントリーしていません");
			return;
		}else if(!c.isMatching()){
			Util.sendMessage(id, "#Red現在そのレースには参加できません");
			return;
		}else if(isStandBy(id)){
			return;
		}else{
			c.acceptMatching(id);
			Util.sendMessageNoHeader(id, "#Aquaレース参加を承認しました。準備が整うまでお待ち下さい");
		}
	}

	public static void clearMatchingCircuitData(UUID id){
		Circuit c = getCircuit(id);
		if(c == null){
			Util.sendMessage(id, "#Redレースにエントリーしていません");
			return;
		}else if(!c.isMatching()){
			Util.sendMessage(id, "#Red現在そのレースには参加できません");
			return;
		}else if(isStandBy(id)){
			return;
		}else{
			c.denyMatching(id);
			clearEntryRaceData(id);
		}
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void setEntryRaceData(UUID id, String circuitname){
		Circuit c = setupCircuit(circuitname);
		getRace(id).setEntry(circuitname);

		if(c.isStarted()){
			c.entryReservePlayer(id);
			Util.sendMessage(id, "#Gold" + circuitname + "#Greenのレースにエントリーしました。既にレースが開始されているため、次回開催されるレースにエントリーされました");
		}else{
			c.entryPlayer(id);
			Scoreboards.entryCircuit(id);

			Util.sendMessage(id, "#Gold" + circuitname + "#Greenのレースにエントリーしました");

			if(c.isMatching())
				setMatchingCircuitData(id);
		}
	}

	public static void setCharacterRaceData(UUID id, EnumCharacter character){
		if(!isStandBy(id)){
			Util.sendMessage(id, "#Redレースが開始されるまでキャラクター選択はできません");
			return;
		}
		if(Bukkit.getPlayer(id) == null){
			Util.sendMessage(null, "#Redオフラインプレイヤーへのキャラクター選択はできません");
			return;
		}

		final Player p = Bukkit.getPlayer(id);
		Race r = getRace(id);

		r.setCharacter(character);
		r.recoveryCharacterPhysical();
		//TODO : issue #46
		p.getInventory().setHelmet(EnumItem.MarioHat.getItem());

		PacketUtil.disguise(p, null, character);
		EnumCharacter.playCharacterVoice(Bukkit.getPlayer(id), character);
		Util.sendMessage(id, "キャラクター" + "#Gold" + character.getName() + "#Greenを選択しました");
	}

	public static void setKartRaceData(UUID id, EnumKarts kart){
		if(!isStandBy(id)){
			Util.sendMessage(id, "#Redレースが開始されるまでカート選択はできません");
			return;
		}
		if(Bukkit.getPlayer(id) == null){
			Util.sendMessage(null, "#Redオフラインプレイヤーへのカート選択はできません");
			return;
		}

		Player p = Bukkit.getPlayer(id);
		Race r = getRace(id);
		r.setKart(kart);
		r.recoveryKart();

		Util.sendMessage(id, "#White" + kart.getName() + "カート#Greenに搭乗しました");
	}

	public static void clearEntryRaceData(UUID id){
		Scoreboards.exitCircuit(id);
		getCircuit(id).exitPlayer(id);
		Race r = getRace(id);

		clearCharacterRaceData(id);
		clearKartRaceData(id);

		Player p = Bukkit.getPlayer(id);
		if(p != null){
			leaveRacingKart(p);
			if(isStandBy(id)){
				r.recoveryInventory();
				r.recoveryPhysical();
				p.teleport(r.getGoalPosition());
			}
		}

		r.init();

		Util.sendMessage(id, "エントリーを取り消しました");
	}

	public static void clearCharacterRaceData(UUID id){
		if(getRace(id).getCharacter() == null)return;

		getRace(id).setCharacter(null);
		Player p = Bukkit.getPlayer(id);
		if(p != null){
			getRace(id).recoveryPhysical();
			PacketUtil.returnPlayer(p);
			Util.sendMessage(id, "キャラクター選択を取り消しました");
		}
	}

	public static void clearKartRaceData(UUID id){
		if(getRace(id).getKart() == null)return;

		Util.sendMessage(id, "搭乗を解除しました");
		getRace(id).setKart(null);
	}

	public static void leaveRacingKart(Player p){
		if(p.getVehicle() != null)
			if(isRacingKart(p.getVehicle())){
				getRace(p).setCMDForceLeave(true);
				Entity vehicle = p.getVehicle();
				p.leaveVehicle();
				vehicle.remove();
				getRace(p).setCMDForceLeave(false);
			}
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static Circuit getCircuit(UUID id){
		try{
			return circuit.get(getRace(id).getEntry());
		}catch(NullPointerException ex){
			return null;
		}
	}

	public static Circuit getCircuit(String circuitname){
		try{
			return circuit.get(circuitname);
		}catch(NullPointerException ex){
			return null;
		}
	}

	public static Race getRace(Player p){
		if (racedata.get(p.getUniqueId()) == null){
			racedata.put(p.getUniqueId(), new Race(p.getUniqueId().toString()));
		}
		return racedata.get(p.getUniqueId());
	}

	public static Race getRace(UUID id){
		if (racedata.get(id) == null){
			racedata.put(id, new Race(id.toString()));
		}
		return racedata.get(id);
	}

	public static List<Player> getEntryPlayer(String circuitname) {
		if(circuit.get(circuitname) == null)return null;

		List<Player> entryplayer = new ArrayList<Player>();
		return circuit.get(circuitname).getEntryPlayer();
	}

	public static List<Player> getGoalPlayer(String circuitname) {
		ArrayList<Player> goalplayer = new ArrayList<Player>();
		for (Player p : getEntryPlayer(circuitname)) {
			if (getRace(p).getGoal())
				goalplayer.add(p);
		}
		return goalplayer;
	}

	public static Player getPlayerfromRank(String circuitname, int rank) {
		for (Player p : getEntryPlayer(circuitname)) {
			if (getRank(p) == rank)
				return p;
		}
		return null;
	}

	// レース走行中(CPポイントカウント中)の順位
	public static Integer getRank(Player p) {
		HashMap<UUID, Integer> count = new HashMap<UUID, Integer>();

		for(Player entryplayer : getEntryPlayer(getRace(p).getEntry())){
			//if(!entryplayer.getUniqueId().equals(p.getUniqueId()))
				count.put(entryplayer.getUniqueId(), getRace(entryplayer).getPassedCheckPoint().size());
		}

		List<Map.Entry<UUID, Integer>> entry = new ArrayList<Map.Entry<UUID, Integer>>(
				count.entrySet());
		Collections.sort(entry, new Comparator<Map.Entry<UUID, Integer>>() {
			@Override
			public int compare(Entry<UUID, Integer> entry1,
					Entry<UUID, Integer> entry2) {
				return entry2.getValue().compareTo(entry1
						.getValue());
			}
		});

		int rank = 1;
		for (Entry<UUID, Integer> ranking : entry) {
			if (ranking.getKey().equals(p.getUniqueId()))
				return rank;

			rank++;
		}

		return 0;
	}

	public static ArrayList<Entity> getNearbyCheckpoint(Location l, double radius, String circuitname){
		List<Entity> entityList = Util.getNearbyEntities(l.clone().add(0, checkPointHeight, 0), radius);

		ArrayList<Entity> nearbycheckpoint = new ArrayList<Entity>();
		for (Entity e : entityList) {
			//プレイヤーとの高低差が一定以上のチェックポイントはスルー
			if(Math.abs(e.getLocation().getY()-l.getY()) < checkPointHeight+5)
				if(isCustomWitherSkull(e, circuitname))
					if(ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(circuitname))
						nearbycheckpoint.add(e);
		}

		if(nearbycheckpoint.isEmpty())return null;
		return nearbycheckpoint;
	}

	public static List<Entity> getNearbyUnpassedCheckpoint(Location l, double radius, Race r){
		String lap = r.getLapCount() <= 0 ? "" : String.valueOf(r.getLapCount());
		List<Entity> entityList = Util.getNearbyEntities(l.clone().add(0, checkPointHeight, 0), radius);

		List<Entity> nearbycheckpoint = new ArrayList<Entity>();
		for (Entity e : entityList) {
			//プレイヤーとの高低差が一定以上のチェックポイントはスルー
			if(Math.abs(e.getLocation().getY()-l.getY()) < checkPointHeight+5)
				if(isCustomWitherSkull(e, r.getEntry()))
					if(ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(r.getEntry()))
						if(!r.getPassedCheckPoint().contains(lap + e.getUniqueId().toString()))
							nearbycheckpoint.add(e);
		}

		if(nearbycheckpoint.isEmpty())return null;
		return nearbycheckpoint;
	}

	public static Entity getNearestUnpassedCheckpoint(Location l, double radius, Race r){
		List<Entity> checkpoint = getNearbyUnpassedCheckpoint(l, radius, r);
		if(checkpoint == null)return null;

		return Util.getNearestEntity(checkpoint, l);
	}

	public static ArrayList<String> getNearbyCheckpointID(Location l, double radius, String circuitname){
		List<Entity> entityList = Util.getNearbyEntities(l.clone().add(0, checkPointHeight, 0), radius);

		ArrayList<String> nearbycheckpoint = new ArrayList<String>();
		for (Entity e : entityList) {
			//プレイヤーとの高低差が一定以上のチェックポイントはスルー
			if(Math.abs(e.getLocation().getY()-l.getY()) < checkPointHeight+5)
				if(isCustomWitherSkull(e, circuitname))
					if(ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(circuitname))
						nearbycheckpoint.add(e.getUniqueId().toString());
		}

		if(nearbycheckpoint.isEmpty())return null;

		return nearbycheckpoint;
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	/*
	 * レースに参加申請し、開始されるまで待機している状態です
	 * 行動の制限等は掛かりません
	 */
	public static Boolean isEntry(UUID id) {
		if (getRace(id).getEntry() != "")
			return true;
		return false;
	}

	/*
	 * 申請していたレースが規定人数を満たし参加者が召集された状態です
	 * まだレースは開始されていません
	 * レースの終了までインベントリの操作等が出来ない代わりに
	 * 専用のアイテムが利用でき、キャラクター選択やレース専用カートへの搭乗が可能となります
	 */
	public static Boolean isStandBy(UUID id){
		if (isEntry(id))
			if (getRace(id).getStandBy())
				return true;
		return false;
	}

	/*
	 * 申請していたレースが開始された状態です
	 */
	public static Boolean isRacing(UUID id){
		if(isEntry(id))
			if(isStandBy(id))
				if(getRace(id).getStart())
					return true;
		return false;
	}

	public static boolean isRacingKart(Entity e){
		if(e instanceof Minecart)
			if(e.getCustomName() != null)
				if(EnumKarts.getKartArrayList().contains(ChatColor.stripColor(e.getCustomName()).toString()))
					if(e.getMetadata(YPLKart.plname).get(0) != null)
						return true;
		return false;
	}

	public static boolean isDisplayKart(Entity e){
		if(e instanceof Minecart)
			if(e.getCustomName() != null)
				if(DisplayKartData.getList().contains(ChatColor.stripColor(e.getCustomName()).toString()))
					return true;
		return false;
	}

	public static boolean isCustomWitherSkull(Entity e, String circuitname){
		if(!(e instanceof WitherSkull))return false;
		if(e.getCustomName() == null)return false;
		if(!ChatColor.stripColor(e.getCustomName()).equalsIgnoreCase(circuitname))return false;
		return true;
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void removeAllJammerEntity(){
		for(Circuit cir : circuit.values()){
			cir.removeAllJammerEntity();
		}
	}

	public static Minecart createCustomMinecart(Location l, EnumKarts kart){
		try {
			Object craftWorld = ReflectionUtil.getCraftWorld(l.getWorld());
			Class<?> customClass = ReflectionUtil.getYPLKartClass("CustomMinecart");
			Object customCart = customClass.getConstructor(ReflectionUtil.getBukkitClass("World"), EnumKarts.class, Location.class, boolean.class).newInstance(craftWorld, kart, l, false);
			Minecart cart = (Minecart) customCart.getClass().getMethod("getBukkitEntity").invoke(customCart);

			customClass.getMethod("setPosition", double.class, double.class, double.class).invoke(customCart, l.getX(), l.getY()+1, l.getZ());
			craftWorld.getClass().getMethod("addEntity", ReflectionUtil.getBukkitClass("Entity")).invoke(craftWorld, customCart);

			cart.setDisplayBlock(new MaterialData(kart.getDisplayBlock(), kart.getDisplayData()));
			cart.setCustomName(kart.getName());
			cart.setCustomNameVisible(false);

			cart.setMetadata(YPLKart.plname, new FixedMetadataValue(YPLKart.getInstance(), customCart));

			return cart;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Minecart createDisplayMinecart(Location l, EnumKarts kart, String id){
		try {
			Object craftWorld = ReflectionUtil.getCraftWorld(l.getWorld());
			Class<?> customClass = ReflectionUtil.getYPLKartClass("CustomMinecart");
			Object customCart = customClass.getConstructor(ReflectionUtil.getBukkitClass("World"), EnumKarts.class, Location.class, boolean.class).newInstance(craftWorld, kart, l, true);
			final Minecart cart = (Minecart) customCart.getClass().getMethod("getBukkitEntity").invoke(customCart);

			customClass.getMethod("setPosition", double.class, double.class, double.class).invoke(customCart, l.getX(), l.getY()+1, l.getZ());
			craftWorld.getClass().getMethod("addEntity", ReflectionUtil.getBukkitClass("Entity")).invoke(craftWorld, customCart);

			cart.setDisplayBlock(new MaterialData(kart.getDisplayBlock(), kart.getDisplayData()));

			if(id == null){
				cart.setCustomName(cart.getUniqueId().toString());
				DisplayKartData.createData(cart.getUniqueId().toString(), kart, l);
			}else{
				cart.setCustomName(id);
			}
			cart.setCustomNameVisible(false);

			return cart;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Entity createCustomWitherSkull(Location l, String circuitname) throws Exception{
		WitherSkull skull = l.getWorld().spawn(l.add(0,checkPointHeight,0), WitherSkull.class);
		skull.setDirection(new Vector(0, 0, 0));
		skull.setVelocity(new Vector(0, 0, 0));
		skull.getLocation().setYaw(0);
		skull.getLocation().setPitch(0);
		skull.setCustomName(ChatColor.GREEN + circuitname);
		skull.setCustomNameVisible(true);

		return skull;
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void showCharacterSelectMenu(Player p){
		Inventory inv = Bukkit.createInventory(null, 36, "Character Select Menu");
		//inv.setItem(8, EnumSelectMenu.CharacterCancel.getMenuItem());
		inv.setItem(11, EnumCharacter.Human.getMenuItem());
		inv.setItem(12, EnumCharacter.Zombie.getMenuItem());
		inv.setItem(13, EnumCharacter.Creeper.getMenuItem());
		inv.setItem(14, EnumCharacter.Skeleton.getMenuItem());
		inv.setItem(15, EnumCharacter.Spider.getMenuItem());
		inv.setItem(20, EnumCharacter.Enderman.getMenuItem());
		inv.setItem(21, EnumCharacter.Witch.getMenuItem());
		inv.setItem(22, EnumCharacter.Pig.getMenuItem());
		inv.setItem(23, EnumCharacter.Squid.getMenuItem());
		inv.setItem(24, EnumCharacter.Villager.getMenuItem());
		inv.setItem(31, EnumSelectMenu.CharacterRandom.getMenuItem());

		if(Permission.hasPermission(p, Permission.kart_ride, true)){
			inv.setItem(30, EnumSelectMenu.CharacterPrev.getMenuItem());
			inv.setItem(32, EnumSelectMenu.CharacterNext.getMenuItem());
		}
		p.openInventory(inv);
	}

	public static void showKartSelectMenu(Player p){
		Inventory inv = Bukkit.createInventory(null, 36, "Kart Select Menu");
		//inv.setItem(8, EnumSelectMenu.KartCancel.getMenuItem());
		inv.setItem(9, EnumKarts.Kart1.getMenuItem());
		inv.setItem(11, EnumKarts.Kart2.getMenuItem());
		inv.setItem(13, EnumKarts.Kart3.getMenuItem());
		inv.setItem(15, EnumKarts.Kart4.getMenuItem());
		inv.setItem(19, EnumKarts.Kart5.getMenuItem());
		inv.setItem(21, EnumKarts.Kart6.getMenuItem());
		inv.setItem(23, EnumKarts.Kart7.getMenuItem());
		inv.setItem(25, EnumKarts.Kart8.getMenuItem());
		inv.setItem(31, EnumSelectMenu.KartRandom.getMenuItem());
		inv.setItem(30, EnumSelectMenu.KartPrev.getMenuItem());
		inv.setItem(32, EnumSelectMenu.KartNext.getMenuItem());
		p.openInventory(inv);
	}
}