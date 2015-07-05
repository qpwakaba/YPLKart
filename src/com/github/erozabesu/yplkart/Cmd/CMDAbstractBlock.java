package com.github.erozabesu.yplkart.Cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Data.RaceData;
import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Enum.EnumKarts;
import com.github.erozabesu.yplkart.Enum.Permission;
import com.github.erozabesu.yplkart.Utils.Util;

public class CMDAbstractBlock extends CMDAbstract{
	String[] args;
	int length;

	public CMDAbstractBlock(String[] args){
		this.args = args;
		this.length = args.length;
	}

	@Override
	void ka(){
		if(Bukkit.getPlayer(args[0]).isOnline())
			Util.sendMessage(Bukkit.getPlayer(args[0]), reference);
	}

	@Override
	void circuit(){
		if(this.length == 2){
			if(args[1].equalsIgnoreCase("list")){
				RaceData.listCricuit(null);
				return;
			}
		}else if(this.length == 3){
			if(args[1].equalsIgnoreCase("delete")){
				RaceData.deleteCircuit(null, args[2]);
				return;
			}
		}else if(this.length == 4){
			if(args[1].equalsIgnoreCase("rename")){
				RaceData.renameCircuit(null, args[2], args[3]);
				return;
			}else if(args[1].equalsIgnoreCase("setminplayer")){
				if(!Util.isNumber(args[3])){
					messageInvalidNumber(null);
					return;
				}
				RaceData.setMinPlayer(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("setmatchingtime")){
				if(!Util.isNumber(args[3])){
					messageInvalidNumber(null);
					return;
				}
				RaceData.setMatchingTime(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("setlimittime")){
				if(!Util.isNumber(args[3])){
					messageInvalidNumber(null);
					return;
				}
				RaceData.setLimitTime(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("setlap")){
				if(!Util.isNumber(args[3])){
					messageInvalidNumber(null);
					return;
				}
				RaceData.setNumberOfLaps(null, args[2], Integer.valueOf(args[3]));
				return;
			}
		}else if(this.length == 9){
			if(Bukkit.getWorld(args[3]) == null)
				return;
			if(!Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6]) || !Util.isNumber(args[7]) || !Util.isNumber(args[8]))
				return;
			//0:circuit 1:create 2:circuitname 3:worldname 4:x 5:y 6:z
			if(args[1].equalsIgnoreCase("create")){
				RaceData.createCircuit(null, args[2], args[3], Double.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]), Float.valueOf(args[7]), Float.valueOf(args[8]));
				return;
			//0:circuit 1:setposition 2:circuitname 3:worldname 4:x 5:y 6:z
			}else if(args[1].equalsIgnoreCase("setposition")){
				RaceData.setPosition(null, args[2], args[3], Double.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]), Float.valueOf(args[7]), Float.valueOf(args[8]));
				return;
			}
		}
	}

	//ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch}
	//ka display random {worldname} {x} {y} {z}  {yaw} {pitch}
	@Override
	void display(){
		if (length == 8){
			if(Bukkit.getWorld(args[2]) == null)
				return;
			if(!Util.isNumber(args[3]) || !Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6]) || !Util.isNumber(args[7]))
				return;
			EnumKarts kart = null;
			if(args[1].equalsIgnoreCase("random"))
				kart = EnumKarts.getRandomKart();
			else
				kart = EnumKarts.getKartfromString(args[1]);
			if(kart == null)
				return;

			RaceManager.createDisplayMinecart(new Location(Bukkit.getWorld(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Double.valueOf(args[5]), Float.valueOf(args[6]), Float.valueOf(args[7])), kart, null);
		}else{
		}
	}

	//ka menu {player}
	//ka menu all
	@Override
	void menu() {
		if (length == 2) {
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.showCharacterSelectMenu(other);
				}
			}else{
				if(!Util.isOnline(args[1]))return;

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.showCharacterSelectMenu(other);
			}
		}else{
		}
	}

	//ka entry {player name} {circuit name}
	//ka entry all {circuit name}
	@Override
	void entry() {
		if(this.length == 3){
			if(!RaceData.isCircuit(args[2])){
				return;
			}
			if(args[1].equalsIgnoreCase("all")){
				for(Player p : Bukkit.getOnlinePlayers()){
					RaceManager.entry(p.getUniqueId(), args[2]);
				}
			}else{
				if(!Util.isOnline(args[1])){
					return;
				}
				RaceManager.entry(Bukkit.getPlayer(args[1]).getUniqueId(), args[2]);
			}
		}else{
		}
	}

	//ka exit {player}
	//ka exit all
	@Override
	void exit() {
		if (length == 2) {
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.exit(other.getUniqueId());
				}
			}else{
				if(!Util.isOnline(args[1])){messageNoPlayer(null);return;}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.exit(other.getUniqueId());
			}
		}else{
		}
	}

	//ka character {player} {character name}
	//ka character all {character name}
	//ka character {player} random
	//ka character all random
	@Override
	void character(){
		if(this.length == 3){
			if(args[2].equalsIgnoreCase("random")){
				if(args[1].equalsIgnoreCase("all")){
					for(Player other : Bukkit.getOnlinePlayers()){
						RaceManager.character(other.getUniqueId(), EnumCharacter.getRandomCharacter());
					}
				}else{
					if(!Util.isOnline(args[1]))
						return;
					EnumCharacter character = EnumCharacter.getRandomCharacter();
					RaceManager.character(Bukkit.getPlayer(args[1]).getUniqueId(), character);
				}
			}else{
				EnumCharacter character = EnumCharacter.getClassfromString(args[2]);
				if(character == null)
					return;
				if(args[1].equalsIgnoreCase("all")){
					for(Player other : Bukkit.getOnlinePlayers()){
						RaceManager.character(other.getUniqueId(), character);
					}
				}else{
					if(!Util.isOnline(args[1]))
						return;
					RaceManager.character(Bukkit.getPlayer(args[1]).getUniqueId(), character);
				}
			}
		}else{
		}
	}

	//ka characterreset {player}
	//ka characterreset all
	@Override
	void characterreset() {
		if (length == 2){
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.characterReset(other.getUniqueId());
				}
			}else{
				if(!Util.isOnline(args[1])){messageNoPlayer(null);return;}

				RaceManager.characterReset(Bukkit.getPlayer(args[1]).getUniqueId());
			}
		}else{
		}
	}

	//ka ride all {kart name}
	//ka ride {player name} {kart name}
	//ka ride all random
	//ka ride {player name} random
	@Override
	void ride(){
		if(this.length == 3){
			EnumKarts kart = null;
			if(args[2].equalsIgnoreCase("random"))
				kart = EnumKarts.getRandomKart();
			else
				kart = EnumKarts.getKartfromString(args[2]);
			if(kart == null)
				return;

			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.setPassengerCustomMinecart(other, kart);
				}
			}else{
				if(!Util.isOnline(args[1]))
					return;

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.setPassengerCustomMinecart(other, kart);
			}
		}else{
		}
	}

	//ka leave {player}
	//ka leave all
	@Override
	void leave() {
		if (length == 2) {
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.removeCustomMinecart(other);
					RaceManager.leave(other.getUniqueId());
				}
			}else{
				if(!Util.isOnline(args[1]))return;

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.removeCustomMinecart(other);
				RaceManager.leave(other.getUniqueId());
			}
		}else{
		}
	}

	@Override
	void ranking() {
		//ka ranking {player name} 	{circuit name}
		//ka ranking all 			{circuit name}
		if(!Util.isOnline(args[1]))
			return;
		Player other = Bukkit.getPlayer(args[1]);

		if(this.length == 3){
			if(!RaceData.isCircuit(args[2])){
				messageInvalidCircuit(other, args[2]);
				return;
			}

			RaceManager.ranking(other.getUniqueId(), args[2]);
		}else{
			Util.sendMessage(null, "===========================================\n" + referenceRankingOther + "\n" + "#GoldCircuit List :\n" + "#White" + RaceData.getCircuitList());
		}
	}

	@Override
	void reload() {
		for(Player p : Bukkit.getOnlinePlayers()){
			RaceManager.exit(p.getUniqueId());
		}
		RaceManager.endAllCircuit();

		Settings.reloadConfig();
		Util.sendMessage(null, "コンフィグをリロードしました");
	}

	@Override
	void additem(ItemStack item, Permission permission){
		if (length == 2){
			//ka {item} all
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					other.getInventory().addItem(item);
					messageAddItem(other, item);
				}
				Util.broadcastMessage("全プレイヤーに#White" + item.getItemMeta().getDisplayName() + "#Greenを配布しました");
			//ka {item} @?
			}else{
				Player other = Bukkit.getPlayer(args[1]);
				other.getInventory().addItem(item);
				messageAddItem(other, item);
			}
		}else if(length == 3){
			if(!Util.isNumber(args[2]))return;
			item.setAmount(Integer.valueOf(args[2]));

			//ka {item} all 64
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					other.getInventory().addItem(item);
					messageAddItem(other, item);
				}
				Util.broadcastMessage("全プレイヤーに#White" + item.getItemMeta().getDisplayName() + "#Greenを配布しました");
			//ka {item} @? 64
			}else{
				Player other = Bukkit.getPlayer(args[1]);
				other.getInventory().addItem(item);
				messageAddItem(other, item);
			}
		}else{
		}
	}
}
