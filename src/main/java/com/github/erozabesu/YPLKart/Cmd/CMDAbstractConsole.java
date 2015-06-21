package main.java.com.github.erozabesu.YPLKart.Cmd;

import main.java.com.github.erozabesu.YPLKart.Data.RaceData;
import main.java.com.github.erozabesu.YPLKart.Data.Settings;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumCharacter;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumKarts;
import main.java.com.github.erozabesu.YPLKart.Enum.Permission;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMDAbstractConsole extends CMDAbstract{
	String[] args;
	int length;

	public CMDAbstractConsole(String[] args){
		this.args = args;
		this.length = args.length;
	}

	@Override
	void ka(){
		Util.sendMessage(null, reference);
	}

	//ka circuit create {circuit name} {world} {x} {y} {z}
	//ka circuit delete {circuit name}
	//ka circuit setposition {circuit name} {worldname} {x} {y} {z}
	//ka circuit rename {circuit name} {new circuitname}
	//ka circuit list
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
			}
		}else if(this.length == 9){
			if(Bukkit.getWorld(args[3]) == null){
				messageInvalidWorld(null);
				return;
			}
			if(!Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6]) || !Util.isNumber(args[7]) || !Util.isNumber(args[8])){
				messageInvalidNumber(null);
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
			}else if(args[1].equalsIgnoreCase("setgoalposition")){
				RaceData.setGoalPosition(null, args[2], args[3], Double.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]), Float.valueOf(args[7]), Float.valueOf(args[8]));
				return;
			}
		}
		Util.sendMessage(null, "===========================================\n" + this.referenceCircuitOutgame + "\n" + "#GoldCircuit List :\n" + "#White" + RaceData.getCircuitList());
	}

	//ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch}
	//ka display random {worldname} {x} {y} {z}  {yaw} {pitch}
	@Override
	void display(){
		if (length == 8){
			if(Bukkit.getWorld(args[2]) == null){
				messageInvalidWorld(null);
				return;
			}
			if(!Util.isNumber(args[3]) || !Util.isNumber(args[4]) || !Util.isNumber(args[5]) || !Util.isNumber(args[6]) || !Util.isNumber(args[7])){
				messageInvalidNumber(null);
				return;
			}
			EnumKarts kart = null;
			if(args[1].equalsIgnoreCase("random"))
				kart = EnumKarts.getRandomKart();
			else
				kart = EnumKarts.getKartfromString(args[1]);
			if(kart == null){
				messageInvalidKart(null);
				return;
			}

			RaceManager.createDisplayMinecart(new Location(Bukkit.getWorld(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Double.valueOf(args[5]), Float.valueOf(args[6]), Float.valueOf(args[7])), kart, null);
			messageDisplay(null, kart);
		}else{
			Util.sendMessage(null, "===========================================\n" + referenceDisplayIngame + "\n" + "#GoldKart List :\n" + "#White" + EnumKarts.getKartList());
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
				messageMenuAll(null);
			}else{
				if(!Util.isOnline(args[1])){messageNoPlayer(null);return;}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.showCharacterSelectMenu(other);
				messageMenuOther(null, other);
			}
		}else{
			Util.sendMessage(null, referenceMenu + "\n" + referenceMenuOther);
		}
	}

	//ka entry {player name} {circuit name}
	//ka entry all {circuit name}
	@Override
	void entry() {
		if(this.length == 3){
			if(!RaceData.getCircuitList().contains(args[2])){
				messageInvalidCircuit(null, args[2]);
				return;
			}
			if(args[1].equalsIgnoreCase("all")){
				for(Player p : Bukkit.getOnlinePlayers()){
					RaceManager.entry(p, args[2]);
				}
				messageEntryAll(null, args[2]);
			}else{
				if(!Util.isOnline(args[1])){
					messageNoPlayer(null);
					return;
				}
				Player other = Bukkit.getPlayer(args[1]);
				messageEntryOther(null, other, args[2]);
				RaceManager.entry(other, args[2]);
			}
		}else{
			Util.sendMessage(null, "===========================================\n"
									+ referenceEntryOther + "\n"
									+ "#GoldCircuit List :\n"
									+ "#White" + RaceData.getCircuitList());
		}
	}

	//ka exit {player}
	//ka exit all
	@Override
	void exit() {
		if (length == 2) {
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.exit(other);
				}
				messageExitAll(null);
			}else{
				if(!Util.isOnline(args[1])){messageNoPlayer(null);return;}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.exit(other);
				messageExitOther(null, other);
			}
		}else{
			Util.sendMessage(null, referenceExitOther);
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
						RaceManager.character(other, EnumCharacter.getRandomCharacter());
					}
					messageCharacterRandomAll(null);
				}else{
					if(!Util.isOnline(args[1])){
						messageNoPlayer(null);
						return;
					}
					EnumCharacter character = EnumCharacter.getRandomCharacter();
					Player other = Bukkit.getPlayer(args[1]);
					RaceManager.character(other, character);
					messageCharacterOther(null, other, character);
				}
			}else{
				EnumCharacter character = EnumCharacter.getClassfromString(args[2]);
				if(character == null){
					messageInvalidCharacter(null);
					return;
				}
				if(args[1].equalsIgnoreCase("all")){
					for(Player other : Bukkit.getOnlinePlayers()){
						RaceManager.character(other, character);
					}
					messageCharacterAll(null, character);
				}else{
					if(!Util.isOnline(args[1])){
						messageNoPlayer(null);
						return;
					}
					Player other = Bukkit.getPlayer(args[1]);
					RaceManager.character(other, character);
					messageCharacterOther(null, other, character);
				}
			}
		}else{
			Util.sendMessage(null, "===========================================\n" + referenceCharacter + "\n" + referenceCharacterOther + "\n" + "#GoldCharacter List :\n" + "#White" + EnumCharacter.getCharacterList());
		}
	}

	//ka characterreset {player}
	//ka characterreset all
	/*@Override
	void characterreset() {
		if (length == 2){
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.characterReset(other);
				}
				messageCharacterResetAll(null);
			}else{
				if(!Util.isOnline(args[1])){messageNoPlayer(null);return;}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.characterReset(other);
				messageCharacterResetOther(null, other);
			}
		}else{
			Util.sendMessage(null, referenceCharacterResetOther);
		}
	}*/

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
				messageInvalidKart(null);
				return;
			}

			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					RaceManager.setPassengerCustomMinecart(other, kart);
				}
				if(args[2].equalsIgnoreCase("random"))
					messageRideRandomAll(null);
				else
					messageRideAll(null, kart);
			}else{
				if(!Util.isOnline(args[1])){
					messageNoPlayer(null);
					return;
				}
				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.setPassengerCustomMinecart(other, kart);
				messageRideOther(null, other, kart);
			}
		}else{
			Util.sendMessage(null, "===========================================\n" + referenceRide + "\n" + referenceRideOther + "\n" + "#GoldKart List :\n" + "#White" + EnumKarts.getKartList());
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
					RaceManager.leave(other);
				}
				messageLeaveAll(null);
			}else{
				if(!Util.isOnline(args[1])){messageNoPlayer(null);return;}

				Player other = Bukkit.getPlayer(args[1]);
				RaceManager.removeCustomMinecart(other);
				RaceManager.leave(other);
				messageLeaveOther(null, other);
			}
		}else{
			Util.sendMessage(null, referenceLeaveOther);
		}
	}

	@Override
	void reload() {
		Settings.reloadConfig();
		Util.sendMessage(null, "コンフィグをリロードしました");
	}

	@Override
	void additem(ItemStack item, Permission permission){
		if(item == null && permission == null){
			Util.sendMessage(null, "===========================================\n" + referenceAddItemOther + "\n" + "#GoldItem List :\n" + "#White" + itemlist);
		}else if (length == 2){
			//ka {item} all
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					other.getInventory().addItem(item);
					messageAddItem(other, item);
				}
				messageAddItemAll(null, item);
			//ka {item} {player}
			}else{
				if(!Util.isOnline(args[1])){messageNoPlayer(null);return;}

				Player other = Bukkit.getPlayer(args[1]);
				other.getInventory().addItem(item);
				messageAddItem(other, item);
				messageAddItemOther(null, other, item);
			}
		}else if(length == 3){
			if(!Util.isNumber(args[2])){Util.sendMessage(null, referenceAddItemOther);return;}
			item.setAmount(Integer.valueOf(args[2]));

			//ka {item} all 64
			if(args[1].equalsIgnoreCase("all")){
				for(Player other : Bukkit.getOnlinePlayers()){
					other.getInventory().addItem(item);
					messageAddItem(other, item);
				}
				messageAddItemAll(null, item);
			//ka {item} {player} 64
			}else{
				if(!Util.isOnline(args[1])){messageNoPlayer(null);return;}

				Player other = Bukkit.getPlayer(args[1]);
				other.getInventory().addItem(item);
				messageAddItem(other, item);
				messageAddItemOther(null, other, item);
			}
		}else{
			Util.sendMessage(null, "===========================================\n" + referenceAddItemOther + "\n" + "#GoldItem List :\n" + "#White" + itemlist);
		}
	}

	@Override
	void ranking() {
		// TODO 自動生成されたメソッド・スタブ

	}
}
