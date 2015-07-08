package com.github.erozabesu.yplkart.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Utils.Util;

public final class RaceData{
	public static YPLKart pl;

	private static String filename = "racedata.yml";
	private static File datafolder;
	private static File configFile;
	private static FileConfiguration config;

	private static int numberoflaps = 3;
	private static int defaultminplayer = 3;
	private static int defaultmaxplayer = 10;
	private static int matchingtime = 30;
	private static int menutime = 30;
	private static int limittime = 300;
	private static boolean broadcastboalmessage = false;

	public RaceData(YPLKart plugin){
		pl = plugin;
		datafolder = pl.getDataFolder();

		configFile = new File(datafolder, filename);
		config = YamlConfiguration.loadConfiguration(configFile);

		CreateConfig();
	}

	/* <circuit name>:
	 *   world: world
	 *   x: double
	 *   y: double
	 *   z: double
	 *   yaw: double
	 *   pitch: double
	 *   laptime:
	 *     <lapcount>:
	 *       <player name>: 123.456
	 *       <player name>: 123.456
	 *     <lapcount>:
	 *       <player name>: 123.456
	 *       <player name>: 123.456
	 * */

	public static void createCircuit(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname)){
			Location l = p.getLocation();
			config.set(circuitname + ".world", l.getWorld().getName());
			config.set(circuitname + ".x", l.getX());
			config.set(circuitname + ".y", l.getY());
			config.set(circuitname + ".z", l.getZ());
			config.set(circuitname + ".yaw", l.getYaw());
			config.set(circuitname + ".pitch", l.getPitch());
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenを作成しました");
		}else{
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは既に作成済みです");
		}
		saveConfigFile();
	}

	public static void createCircuit(Player p, String circuitname, String worldname, double x, double y, double z, float yaw, float pitch){
		if(!getCircuitSet().contains(circuitname)){
			config.set(circuitname + ".world", worldname);
			config.set(circuitname + ".x", x);
			config.set(circuitname + ".y", y);
			config.set(circuitname + ".z", z);
			config.set(circuitname + ".yaw", yaw);
			config.set(circuitname + ".pitch", pitch);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenを作成しました");
		}else{
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは既に作成済みです");
		}
		saveConfigFile();
	}

	public static void deleteCircuit(Player p, String circuitname){
		for(String key : config.getKeys(false)){
			if(key.equalsIgnoreCase(circuitname)){
				config.set(key, null);
				for(World w : Bukkit.getWorlds()){
					for(Entity e : w.getEntities()){
						if(RaceManager.isCustomWitherSkull(e, circuitname))
							e.remove();
					}
				}
				saveConfigFile();
				Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenを削除しました");
				return;
			}
		}
		Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
	}

	public static void listCricuit(Player p){
		Util.sendMessage(p, "[header]#GoldCircuit List：\n" + getCircuitList());
	}

	public static void editCircuit(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			p.getInventory().addItem(EnumItem.getCheckPointTool(EnumItem.CheckPoint, circuitname));
			Util.sendMessage(p, "[header]サーキット：" + "#Gold" + circuitname + "#Greenのチェックポイントツールを配布しました");
		}
	}

	public static void renameCircuit(Player p, String name, String newname){
		if(!getCircuitSet().contains(name)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + name + "#Redは存在しません");
		}else if(getCircuitSet().contains(newname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + newname + "#Redは既に作成済みです");
		}else{
			for(World w : Bukkit.getWorlds()){
				for(Entity e : w.getEntities()){
					if(RaceManager.isCustomWitherSkull(e, name))
						e.setCustomName(e.getCustomName().replace(name, newname));
				}
			}

			config.set(newname, config.get(name));
			config.set(name, null);
			saveConfigFile();

			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + name + "#Greenの名称を#Gold" + newname + "#Greenに変更しました");
		}
	}

	public static void addRunningRaceLapTime(Player p, String circuitname, double laptime){
		if(getCircuitSet().contains(circuitname)){

			String path = circuitname + ".laptime." + getNumberOfLaps(circuitname) + "." + p.getUniqueId().toString();
			if(!config.contains(path)){
				config.set(path, laptime);
				saveConfigFile();
				return;
			}

			if(laptime < config.getDouble(path, 0)){
				Util.sendMessage(p, "[header]記録更新！#Yellow" + config.getDouble(path) + "#Green秒 --> #Yellow" + laptime + "#Green秒");
				config.set(path, laptime);
				saveConfigFile();
				return;
			}
		}
	}

	public static void addKartRaceLapTime(Player p, String circuitname, double laptime){
		if(getCircuitSet().contains(circuitname)){

			String path = circuitname + ".kartlaptime." + getNumberOfLaps(circuitname) + "." + p.getUniqueId().toString();
			if(!config.contains(path)){
				config.set(path, laptime);
				saveConfigFile();
				return;
			}

			if(laptime < config.getDouble(path, 0)){
				Util.sendMessage(p, "[header]記録更新！#Yellow" + config.getDouble(path) + "#Green秒 --> #Yellow" + laptime + "#Green秒");
				config.set(path, laptime);
				saveConfigFile();
				return;
			}
		}
	}

	public static void sendRanking(UUID id, String circuitname){
		String ranking = RaceData.getRanking(id, circuitname);
		String kartranking = RaceData.getKartRanking(id, circuitname);
		if(ranking == null && kartranking == null)
			Util.sendMessage(id, "[header]#Redサーキット : " + "#Yellow" + circuitname + " #Redのレースデータがありません");
		else{
			if(kartranking != null)
				Util.sendMessage(id, kartranking);
			if(ranking != null)
				Util.sendMessage(id, ranking);
		}
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static int getNumberOfLaps(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return numberoflaps;
		if(config.getInt(circuitname + ".numberoflaps") == 0)
			return numberoflaps;

		return config.getInt(circuitname + ".numberoflaps");
	}

	public static int getMinPlayer(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return defaultminplayer;
		if(config.getInt(circuitname + ".minplayer") == 0)
			return defaultminplayer;

		return config.getInt(circuitname + ".minplayer");
	}

	public static int getMaxPlayer(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return defaultmaxplayer;
		if(config.getInt(circuitname + ".maxplayer") == 0)
			return defaultmaxplayer;

		return config.getInt(circuitname + ".maxplayer");
	}

	public static int getMatchingTime(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return matchingtime;
		if(config.getInt(circuitname + ".matchingtime") == 0)
			return matchingtime;

		return config.getInt(circuitname + ".matchingtime");
	}

	public static int getMenuTime(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return menutime;
		if(config.getInt(circuitname + ".menutime") == 0)
			return menutime;

		return config.getInt(circuitname + ".menutime");
	}

	public static boolean getBroadcastGoalMessage(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return false;

		return config.getBoolean(circuitname + ".broadcastgoalmessage", broadcastboalmessage);
	}

	public static int getLimitTime(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return limittime;
		if(config.getInt(circuitname + ".limittime") == 0)
			return limittime;

		return config.getInt(circuitname + ".limittime");
	}

	public static Location getPosition(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return null;
		if(Bukkit.getWorld(config.getString(circuitname + ".world")) == null)
			return null;

		return new Location(Bukkit.getWorld(config.getString(circuitname + ".world")),config.getDouble(circuitname + ".x"),config.getDouble(circuitname + ".y"),config.getDouble(circuitname + ".z"),(float)config.getDouble(circuitname + ".yaw"),(float)config.getDouble(circuitname + ".pitch"));
	}

	public static List<Location> getPositionList(String circuitname){
		Location position = getPosition(circuitname);
		if(position == null)
			return null;

		List<Location> list = new ArrayList<Location>();
		while(list.size() < defaultmaxplayer){
			if(!Util.isSolidBlock(position))
				list.add(position);
			if(!Util.isSolidBlock(Util.getSideLocationfromYaw(position, 4)))
				list.add(Util.getSideLocationfromYaw(position, 4));
			if(!Util.isSolidBlock(Util.getSideLocationfromYaw(position, -4)))
				list.add(Util.getSideLocationfromYaw(position, -4));

			position = Util.getLocationfromYaw(position, 4);
		}

		return list;
	}

	public static String getRanking(UUID id, String circuitname){
		if(!getCircuitSet().contains(circuitname))return null;
		try{
			//記憶されている周回数
			ArrayList<String> numberoflaps = new ArrayList<String>();
			for(String lap : config.getConfigurationSection(circuitname + ".laptime").getKeys(false)){
				numberoflaps.add(lap);
			}

			if(numberoflaps.isEmpty()){
				return null;
			}

			String ranking = "";

			//各週回数のデータ取得
			for(String lap : numberoflaps){
				HashMap<UUID, Double> count = new HashMap<UUID, Double>();
				for(String path : config.getConfigurationSection(circuitname + ".laptime." + lap).getKeys(false)){
					count.put(UUID.fromString(path), config.getDouble(circuitname + ".laptime." + lap + "." + path));
				}

				if(count.size() < 1)continue;

				//データを並び替える
				List<Map.Entry<UUID, Double>> entry = new ArrayList<Map.Entry<UUID, Double>>(count.entrySet());
				Collections.sort(entry, new Comparator<Map.Entry<UUID, Double>>() {
					@Override
					public int compare(Entry<UUID, Double> entry1, Entry<UUID, Double> entry2) {
						return ((Double) entry2.getValue()).compareTo((Double) entry1.getValue());
					}
				});

				ranking += "#DarkAqua====== " + "#Aqua" + circuitname.toUpperCase() + "#DarkAqua Running Race Ranking" + " - #Aqua" + lap + " #DarkAquaLaps" + " #DarkAqua======\n";
				int ownrank = 0;
				double ownsec = 0;
				for(int rank = 1; rank <= entry.size(); rank++){
					if(rank <= 10){
						entry.get(entry.size()-rank);
						ranking += "   #Yellow" + rank + ". #White" + Bukkit.getOfflinePlayer(entry.get(entry.size()-rank).getKey()).getName() + " : " + "#Yellow" + entry.get(entry.size()-rank).getValue() + " sec\n";
					}
					if(id.toString().equalsIgnoreCase(entry.get(entry.size()-rank).getKey().toString())){
						ownrank = rank;
						ownsec = entry.get(entry.size()-rank).getValue();
					}
				}

				if(ownrank != 0 && ownsec != 0)
					ranking += "#White" + Bukkit.getOfflinePlayer(id).getName() + "#Greenさんの順位は#Yellow" + ownrank + "位 : " + ownsec + " sec" + "#Greenです\n";
				else
					ranking += "#White" + Bukkit.getOfflinePlayer(id).getName() + "#Greenさんのデータは存在しません";
			}

			return ranking;
		}catch(NullPointerException ex){
		}
		return null;
	}

	public static String getKartRanking(UUID id, String circuitname){
		if(!getCircuitSet().contains(circuitname))return null;
		try{
			//記憶されている周回数
			ArrayList<String> numberoflaps = new ArrayList<String>();
			for(String lap : config.getConfigurationSection(circuitname + ".kartlaptime").getKeys(false)){
				numberoflaps.add(lap);
			}

			if(numberoflaps.isEmpty()){
				return null;
			}

			String ranking = "";

			//各週回数のデータ取得
			for(String lap : numberoflaps){
				HashMap<UUID, Double> count = new HashMap<UUID, Double>();
				for(String path : config.getConfigurationSection(circuitname + ".kartlaptime." + lap).getKeys(false)){
					count.put(UUID.fromString(path), config.getDouble(circuitname + ".kartlaptime." + lap + "." + path));
				}

				if(count.size() < 1)continue;

				//データを並び替える
				List<Map.Entry<UUID, Double>> entry = new ArrayList<Map.Entry<UUID, Double>>(count.entrySet());
				Collections.sort(entry, new Comparator<Map.Entry<UUID, Double>>() {
					@Override
					public int compare(Entry<UUID, Double> entry1, Entry<UUID, Double> entry2) {
						return ((Double) entry2.getValue()).compareTo((Double) entry1.getValue());
					}
				});

				ranking += "#DarkAqua====== " + "#Aqua" + circuitname.toUpperCase() + "#DarkAqua Kart Race Ranking" + " - #Aqua" + lap + " #DarkAquaLaps" + " #DarkAqua======\n";
				int ownrank = 0;
				double ownsec = 0;
				for(int rank = 1; rank <= entry.size(); rank++){
					if(rank <= 10){
						entry.get(entry.size()-rank);
						ranking += "   #Yellow" + rank + ". #White" + Bukkit.getOfflinePlayer(entry.get(entry.size()-rank).getKey()).getName() + " : " + "#Yellow" + entry.get(entry.size()-rank).getValue() + " sec\n";
					}
					if(id.toString().equalsIgnoreCase(entry.get(entry.size()-rank).getKey().toString())){
						ownrank = rank;
						ownsec = entry.get(entry.size()-rank).getValue();
					}
				}
				if(ownrank != 0 && ownsec != 0)
					ranking += "#White" + Bukkit.getOfflinePlayer(id).getName() + "#Greenさんの順位は#Yellow" + ownrank + "位 : " + ownsec + " sec" + "#Greenです\n";
				else
					ranking += "#White" + Bukkit.getOfflinePlayer(id).getName() + "#Greenさんのデータは存在しません";
			}

			return ranking;
		}catch(NullPointerException ex){
		}
		return null;
	}

	public static String getCircuitInformation(String circuitname){
		if(!getCircuitSet().contains(circuitname))return "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません";

		Location l = getPosition(circuitname);
		String info = "#Aqua==========-[ #GoldCircuit Information #Aqua]-==========" + "\n" +
						"    " + "#Greenサーキット名 ： #White" + Util.convertInitialUpperString(circuitname) + "\n" +
						"    " + "#Green周回数 ： #White" + getNumberOfLaps(circuitname) + "\n" +
						"    " + "#Green最小プレイ人数 ： #White" + getMinPlayer(circuitname) + "\n" +
						"    " + "#Green最大プレイ人数 ： #White" + getMaxPlayer(circuitname) + "\n" +
						"    " + "#Greenレースが自動終了するまでの時間 ： #White" + getLimitTime(circuitname) + "#Green (秒)\n" +
						"    " + "#Greenキャラクター・カートを選択できる猶予時間 ： #White" + getMenuTime(circuitname) + "#Green (秒)\n" +
						"    " + "#Greenレースへの参加・辞退を決定できる猶予時間 ： #White" + getMatchingTime(circuitname) + "#Green (秒)\n" +
						"    " + "#Green順位・ラップタイムのサーバー全体通知 ： #White" + getBroadcastGoalMessage(circuitname) + "\n" +
						"    " + "#Greenレース開始座標 ： " + "\n" +
						"#Green" + "            " + "x #White" + l.getBlockX() + "#Green / y #White" + l.getBlockY() + "#Green / z #White" + l.getBlockZ() + "\n" +
						"#Green" + "            " + "yaw #White" + l.getYaw() + "#Green / pitch #White" + l.getPitch() + "\n";

		return info;
	}

	public static Set<String> getCircuitSet(){
		return config.getKeys(false);
	}

	public static String getCircuitList(){
		String names = null;
		for(String circuitname : getCircuitSet()){
			if(names == null)
				names = "#White" + circuitname + "#Green / ";
			else
				names += "#White" + circuitname + "#Green / ";
		}
		return names;
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void setPosition(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".world", l.getWorld().getName());
			config.set(circuitname + ".x", l.getX());
			config.set(circuitname + ".y", l.getY());
			config.set(circuitname + ".z", l.getZ());
			config.set(circuitname + ".yaw", l.getYaw());
			config.set(circuitname + ".pitch", l.getPitch());
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenの開始座標を再設定しました");
			saveConfigFile();
		}
	}

	public static void setNumberOfLaps(Player p, String circuitname, int amount){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".numberoflaps", amount);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenの周回数を#White" + amount + "周#Greenに設定しました");
			saveConfigFile();
		}
	}

	public static void setMinPlayer(Player p, String circuitname, int amount){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else if(getMaxPlayer(circuitname) < amount){
			Util.sendMessage(p, "[header]#Red最大プレイ人数を上回る数値は設定できません。現在最大プレイ人数は#White" + getMaxPlayer(circuitname) + "人#Redに設定されています");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".minplayer", amount);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenの最小プレイ人数を#White" + amount + "人#Greenに設定しました");
			saveConfigFile();
		}
	}

	public static void setMaxPlayer(Player p, String circuitname, int amount){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else if(amount < getMinPlayer(circuitname)){
			Util.sendMessage(p, "[header]#Red最小プレイ人数を下回る数値は設定できません。現在最小プレイ人数は#White" + getMinPlayer(circuitname) + "人#Redに設定されています");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".maxplayer", amount);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenの最大プレイ人数を#White" + amount + "人#Greenに設定しました");
			saveConfigFile();
		}
	}

	public static void setMatchingTime(Player p, String circuitname, int second){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".matchingtime", second);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenのマッチング時間を#White" + second + "秒#Greenに設定しました");
			saveConfigFile();
		}
	}

	public static void setMenuTime(Player p, String circuitname, int second){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".menutime", second);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenのメニュー選択時間を#White" + second + "秒#Greenに設定しました");
			saveConfigFile();
		}
	}

	public static void setLimitTime(Player p, String circuitname, int second){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".limittime", second);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenのレース終了までの制限時間を#White" + second + "秒#Greenに設定しました");
			saveConfigFile();
		}
	}

	public static void setBroadcastGoalMessage(Player p, String circuitname, boolean flag){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			config.set(circuitname + ".broadcastgoalmessage", flag);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Green順位・ラップタイムのサーバー全体通知を#White" + flag + "#Greenに設定しました");
			saveConfigFile();
		}
	}

	public static void setPosition(Player p, String circuitname, String worldname, double x, double y, double z, float yaw, float pitch){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "[header]#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			config.set(circuitname + ".world", worldname);
			config.set(circuitname + ".x", x);
			config.set(circuitname + ".y", y);
			config.set(circuitname + ".z", z);
			config.set(circuitname + ".yaw", yaw);
			config.set(circuitname + ".pitch", pitch);
			Util.sendMessage(p, "[header]#Greenサーキット：" + "#Gold" + circuitname + "#Greenの開始座標を再設定しました");
			saveConfigFile();
		}
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static boolean isCircuit(String value){
		for(String circuit : getCircuitSet()){
			if(circuit.equalsIgnoreCase(value))
				return true;
		}
		return false;
	}

	// 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void CreateConfig() {
		if(!(configFile.exists())){
			pl.saveResource(filename, true);
			configFile = new File(datafolder, filename);
			config = YamlConfiguration.loadConfiguration(configFile);
			Util.sendMessage(null, "[header]" + filename + ".ymlを生成しました");
		}
	}

	public static File getConfigFile(){
		return configFile;
	}

	public static FileConfiguration getConfig(){
		return config;
	}

	public static void saveConfigFile(){
		saveFile(configFile, config);
	}

	public static void saveFile(File file, FileConfiguration config){
		try{
			config.save(file);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
