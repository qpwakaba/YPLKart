package com.github.erozabesu.yplkart.Cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Data.Message;
import com.github.erozabesu.yplkart.Data.RaceData;
import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Enum.EnumKarts;
import com.github.erozabesu.yplkart.Enum.Permission;
import com.github.erozabesu.yplkart.Utils.Util;

public class CMDAbstractConsole extends CMDAbstract{
	String[] args;
	int length;

	public CMDAbstractConsole(String[] args){
		this.args = args;
		this.length = args.length;
	}

	@Override
	void ka(){
		Message.reference.sendMessage(null);
	}

	@Override
	void circuit(){
		if(this.length == 2){
			if(args[1].equalsIgnoreCase("list")){
				RaceData.listCricuit(null);
				return;
			}
		}else if(this.length == 3){
			if(args[1].equalsIgnoreCase("info")){
				RaceData.sendCircuitInformation(null, args[2]);
				return;
			}else if(args[1].equalsIgnoreCase("delete")){
				RaceData.deleteCircuit(null, args[2]);
				return;
			}
		}else if(this.length == 4){
			if(args[1].equalsIgnoreCase("rename")){
				RaceData.renameCircuit(null, args[2], args[3]);
				return;
			}else if(args[1].equalsIgnoreCase("setminplayer")){
				if(!Util.isNumber(args[3])){
					Message.invalidNumber.sendMessage(null);
					return;
				}
				RaceData.setMinPlayer(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("setmaxplayer")){
				if(!Util.isNumber(args[3])){
					Message.invalidNumber.sendMessage(null);
					return;
				}
				RaceData.setMaxPlayer(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("setmatchingtime")){
				if(!Util.isNumber(args[3])){
					Message.invalidNumber.sendMessage(null);
					return;
				}
				RaceData.setMatchingTime(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("setmenutime")){
				if(!Util.isNumber(args[3])){
					Message.invalidNumber.sendMessage(null);
					return;
				}
				RaceData.setMenuTime(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("setlimittime")){
				if(!Util.isNumber(args[3])){
					Message.invalidNumber.sendMessage(null);
					return;
				}
				RaceData.setLimitTime(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("setlap")){
				if(!Util.isNumber(args[3])){
					Message.invalidNumber.sendMessage(null);
					return;
				}
				RaceData.setNumberOfLaps(null, args[2], Integer.valueOf(args[3]));
				return;
			}else if(args[1].equalsIgnoreCase("broadcastgoal")){
				if(!Util.isBoolean(args[3])){
					Message.invalidBoolean.sendMessage(null);
					return;
				}
				RaceData.setBroadcastGoalMessage(null, args[2], Boolean.valueOf(args[3]));
				return;
			}
		}else if(this.length == 9){
			if(Bukkit.getWorld(args[3]) == null){
				Message.invalidWorld.sendMessage(null);
				return;
			}
			if(!Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6]) || !Util.isNumber(args[7]) || !Util.isNumber(args[8])){
				Message.invalidNumber.sendMessage(null);
				return;
			}
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
		Message.referenceCircuitOutgame.sendMessage(null);
	}

	//ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch}
	//ka display random {worldname} {x} {y} {z}  {yaw} {pitch}
	@Override
	void display(){
		if (length == 8){
			if(Bukkit.getWorld(args[2]) == null){
				Message.invalidWorld.sendMessage(null);
				return;
			}
			if(!Util.isNumber(args[3]) || !Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6]) || !Util.isNumber(args[7])){
				Message.invalidNumber.sendMessage(null);
				return;
			}
			EnumKarts kart = null;
			if(args[1].equalsIgnoreCase("random"))
				kart = EnumKarts.getRandomKart();
			else
				kart = EnumKarts.getKartfromString(args[1]);
			if(kart == null){
				Message.invalidKart.sendMessage(null);
				return;
			}

			RaceManager.createDisplayMinecart(new Location(Bukkit.getWorld(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Double.valueOf(args[5]), Float.valueOf(args[6]), Float.valueOf(args[7])), kart, null);
			Message.cmdDisplayCreate.sendMessage(null, new Object[]{kart});
		}else{
			Message.referenceDisplayOutgame.sendMessage(null);
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
				Message.cmdMenuAll.sendMessage(null);
			}else{
				if(!Util.isOnline(args[1])){
					Message.invalidPlayer.sendMessage(null);
					return;
				}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.showCharacterSelectMenu(other);
				Message.cmdMenuOther.sendMessage(null, new Object[]{other});
			}
		}else{
			Message.referenceMenuOther.sendMessage(null);
		}
	}

	//ka entry {player name} {circuit name}
	//ka entry all {circuit name}
	@Override
	void entry() {
		if(this.length == 3){
			if(!RaceData.isCircuit(args[2])){
				Message.invalidCircuit.sendMessage(null, new Object[]{args[2]});
				return;
			}
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.setEntryRaceData(other.getUniqueId(), args[2]);
				}
				Message.cmdEntryAll.sendMessage(null, args[2]);
			}else{
				Player other = Bukkit.getPlayer(args[1]);
				if(other == null){
					Message.invalidPlayer.sendMessage(null);
					return;
				}

				Message.cmdEntryOther.sendMessage(null, new Object[]{other, args[2]});
				RaceManager.setEntryRaceData(other.getUniqueId(), args[2]);
			}
		}else{
			Message.referenceEntryOther.sendMessage(null);
		}
	}

	//ka exit {player}
	//ka exit all
	@Override
	void exit() {
		if (length == 2) {
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.clearEntryRaceData(other.getUniqueId());
				}
				Message.cmdExitAll.sendMessage(null);
			}else{
				if(!Util.isOnline(args[1])){
					Message.invalidPlayer.sendMessage(null);
					return;
				}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.clearEntryRaceData(other.getUniqueId());
				Message.cmdExitOther.sendMessage(null, other);
			}
		}else{
			Message.referenceExitOther.sendMessage(null);
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
						RaceManager.setCharacterRaceData(other.getUniqueId(), EnumCharacter.getRandomCharacter());
					}
					Message.cmdCharacterRandomAll.sendMessage(null);
				}else{
					if(!Util.isOnline(args[1])){
						Message.invalidPlayer.sendMessage(null);
						return;
					}
					EnumCharacter character = EnumCharacter.getRandomCharacter();
					Player other = Bukkit.getPlayer(args[1]);
					RaceManager.setCharacterRaceData(other.getUniqueId(), character);
					Message.cmdCharacterOther.sendMessage(null, new Object[]{other, character});
				}
			}else{
				EnumCharacter character = EnumCharacter.getClassfromString(args[2]);
				if(character == null){
					Message.invalidCharacter.sendMessage(null);
					return;
				}
				if(args[1].equalsIgnoreCase("all")){
					for(Player other : Bukkit.getOnlinePlayers()){
						RaceManager.setCharacterRaceData(other.getUniqueId(), character);
					}
					Message.cmdCharacterAll.sendMessage(null, character);
				}else{
					if(!Util.isOnline(args[1])){
						Message.invalidPlayer.sendMessage(null);
						return;
					}
					Player other = Bukkit.getPlayer(args[1]);
					RaceManager.setCharacterRaceData(other.getUniqueId(), character);
					Message.cmdCharacterOther.sendMessage(null, new Object[]{other, character});
				}
			}
		}else{
			Message.referenceCharacterOther.sendMessage(null);
		}
	}

	//ka characterreset {player}
	//ka characterreset all
	@Override
	void characterreset() {
		if (length == 2){
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.clearCharacterRaceData(other.getUniqueId());
				}
				Message.cmdCharacterResetAll.sendMessage(null);
			}else{
				if(!Util.isOnline(args[1])){
					Message.invalidPlayer.sendMessage(null);
					return;
				}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.clearCharacterRaceData(other.getUniqueId());
				Message.cmdCharacterResetOther.sendMessage(null, other);
			}
		}else{
			Message.referenceCharacterResetOther.sendMessage(null);
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
			if(kart == null){
				Message.invalidKart.sendMessage(null);
				return;
			}

			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.setKartRaceData(other.getUniqueId(), kart);
				}
				if(args[2].equalsIgnoreCase("random"))
					Message.cmdRideRandomAll.sendMessage(null);
				else
					Message.cmdRideAll.sendMessage(null, kart);
			}else{
				if(!Util.isOnline(args[1])){
					Message.invalidPlayer.sendMessage(null);
					return;
				}
				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.setKartRaceData(other.getUniqueId(), kart);
				Message.cmdRideOther.sendMessage(null, new Object[]{other, kart});
			}
		}else{
			Message.referenceRideOther.sendMessage(null);
		}
	}

	//ka leave {player}
	//ka leave all
	@Override
	void leave() {
		if (length == 2) {
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.leaveRacingKart(other);
					RaceManager.clearKartRaceData(other.getUniqueId());
				}
				Message.cmdLeaveAll.sendMessage(null);
			}else{
				if(!Util.isOnline(args[1])){
					Message.invalidPlayer.sendMessage(null);
					return;
				}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.leaveRacingKart(other);
				RaceManager.clearKartRaceData(other.getUniqueId());
				Message.cmdLeaveOther.sendMessage(null, other);
			}
		}else{
			Message.referenceLeaveOther.sendMessage(null);
		}
	}

	@Override
	void ranking() {
		//ka ranking {circuit name}
		//ka ranking list
		if (this.length == 2){
			if(args[1].equalsIgnoreCase("list")){
				RaceData.listCricuit(null);
			}else{
				if(!RaceData.isCircuit(args[1])){
					Message.invalidCircuit.sendMessage(null, args[1]);
					return;
				}

				RaceData.sendRanking(null, args[1]);
			}
		//ka ranking {player name} 	{circuit name}
		//ka ranking all 			{circuit name}
		}else if(this.length == 3){
			if(!RaceData.isCircuit(args[2])){
				Message.invalidCircuit.sendMessage(null, args[2]);
				return;
			}
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceData.sendRanking(other.getUniqueId(), args[2]);
				}
				Message.cmdRankingAll.sendMessage(null, args[2]);
			}else{
				if(!Util.isOnline(args[1])){
					Message.invalidPlayer.sendMessage(null);
					return;
				}
				Player other = Bukkit.getPlayer(args[1]);
				RaceData.sendRanking(other.getUniqueId(), args[2]);
				Message.cmdRankingOther.sendMessage(null, new Object[]{other, args[2]});
			}
		}else{
			Message.referenceRankingOther.sendMessage(null);
		}
	}

	@Override
	void reload() {
		for(Player other : Bukkit.getOnlinePlayers()){
			RaceManager.clearEntryRaceData(other.getUniqueId());
		}
		RaceManager.endAllCircuit();

		Settings.reloadConfig();
		Message.reloadConfig();
		Message.cmdReload.sendMessage(null);
	}

	@Override
	void additem(ItemStack item, Permission permission){
		if(item == null && permission == null){
			Message.referenceAddItemOther.sendMessage(null);
		}else if (length == 2){
			//ka {item} all
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					other.getInventory().addItem(item);
					Message.cmdItem.sendMessage(other, item);
				}
				Message.cmdItemAll.sendMessage(null, item);
			//ka {item} {player}
			}else{
				if(!Util.isOnline(args[1])){
					Message.invalidPlayer.sendMessage(null);
					return;
				}

				Player other = Bukkit.getPlayer(args[1]);
				other.getInventory().addItem(item);
				Message.cmdItem.sendMessage(other, item);
				Message.cmdItemOther.sendMessage(null, new Object[]{other, item});
			}
		}else if(length == 3){
			if(!Util.isNumber(args[2])){
				Message.referenceAddItemOther.sendMessage(null);
				return;
			}
			item.setAmount(Integer.valueOf(args[2]));

			//ka {item} all 64
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					other.getInventory().addItem(item);
					Message.cmdItem.sendMessage(other, item);
				}
				Message.cmdItemAll.sendMessage(null, item);
			//ka {item} {player} 64
			}else{
				if(!Util.isOnline(args[1])){
					Message.invalidPlayer.sendMessage(null);
					return;
				}

				Player other = Bukkit.getPlayer(args[1]);
				other.getInventory().addItem(item);
				Message.cmdItem.sendMessage(other, item);
				Message.cmdItemOther.sendMessage(null, new Object[]{other, item});
			}
		}else{
			Message.referenceAddItemOther.sendMessage(null);
		}
	}
}
