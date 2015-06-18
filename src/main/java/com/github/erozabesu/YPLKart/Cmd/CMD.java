package main.java.com.github.erozabesu.YPLKart.Cmd;

import main.java.com.github.erozabesu.YPLKart.YPLKart;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumCharacter;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumItem;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumKarts;
import main.java.com.github.erozabesu.YPLKart.Enum.Permission;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMD implements CommandExecutor{
	CMDAbstract cmd;
	protected String itemlist = "mushroom, powerfullmushroom, banana, fakeitembox, turtle, redturtle, thornedturtle, gesso, thunder, star, teresa, killer, itemboxtool, itemboxtooltier2, fakeitemboxtool";

	protected String referenceCircuitIngame = "/ka circuit create {circuit name} :\n"
											+ "/ka circuit create {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											+ "/ka circuit delete {circuit name} :\n"
											+ "/ka circuit edit {circuit name} :\n"
											+ "/ka circuit setposition {circuit name} :\n"
											+ "/ka circuit setposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											+ "/ka circuit setgoalposition {circuit name} :\n"
											+ "/ka circuit setgoalposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											+ "/ka circuit rename {circuit name} {new circuitname} :\n"
											+ "/ka circuit list :";

	protected String referenceCircuitOutgame = "/ka circuit create {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											 + "/ka circuit delete {circuit name} :\n"
											 + "/ka circuit setposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											 + "/ka circuit setgoalposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											 + "/ka circuit rename {circuit name} {new circuitname} :\n"
											 + "/ka circuit list :";

	protected String referenceDisplayIngame = 	"/ka display {kart name} :\n"
											  + "/ka display random :\n"
											  + "/ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											  + "/ka display random {worldname} {x} {y} {z}  {yaw} {pitch} :";

	protected String referenceDisplayOutgame = 	"/ka display {kart name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											  + "/ka display random {worldname} {x} {y} {z}  {yaw} {pitch} :";

	protected String referenceEntry = 		"/ka entry {circuit name} :";
	protected String referenceEntryOther = 	"/ka entry {player name} {circuit name} :\n"
										  + "/ka entry all {circuit name} :";

	protected String referenceExit = 		"/ka exit :";
	protected String referenceExitOther = 	"/ka exit {player name} :\n"
										  + "/ka exit all";

	protected String referenceMenu = 		"/ka menu :";
	protected String referenceMenuOther = 	"/ka menu {player name} :\n"
										  + "/ka menu all :";

	protected String referenceCharacter = 		"/ka character {character name} :\n"
											  + "/ka character random :";
	protected String referenceCharacterOther = 	"/ka character {player name} {character name} :\n"
											  + "/ka character all {character name} :\n"
											  + "/ka character {player name} random :\n"
											  + "/ka character all random :";

	/*protected String referenceCharacterReset = 		"/ka characterreset :";
	protected String referenceCharacterResetOther = "/ka characterreset {player name} :\n"
												  + "/ka characterreset all :";*/

	protected String referenceRide = 		"/ka kart {kart name} :\n"
										  + "/ka kart random :";
	protected String referenceRideOther = 	"/ka kart {player name} {kart name} :\n"
										  + "/ka kart all {kart name} :\n"
										  + "/ka kart {player name} random :\n"
										  + "/ka kart all random :";

	protected String referenceLeave = 		"/ka leave :";
	protected String referenceLeaveOther = 	"/ka leave {player name} :\n"
										  + "/ka leave all :";

	protected String referenceRanking = 		"/ka ranking {circuit name} :\n"
											  + "/ka ranking list :";
	protected String referenceRankingOther = 	"/ka ranking {player name} {circuit name} :\n"
											  + "/ka ranking all {circuit name} :";

	protected String referenceAddItem = 		"/ka {item name} :\n"
											  + "/ka {item name} {amount} :";
	protected String referenceAddItemOther = 	"/ka {item name} {player name} :\n"
											  + "/ka {item name} {player name} {amount} :\n"
											  + "/ka {item name} all :\n"
											  + "/ka {item name} all {amount} :";

	protected String reference = "#Green===========-[ #Gold" + YPLKart.plname + "Command List#Green ]-===========\n"
							   + "/ka circuit :\n"
							   + "/ka display :\n"
							   + "/ka entry :\n"
							   + referenceExit + "\n"
							   + referenceExitOther + "\n"
							   + referenceMenu + "\n"
							   + referenceMenuOther + "\n"
							   + "/ka character :\n"
							//   + referenceCharacterReset + "\n"
							//   + referenceCharacterResetOther + "\n"
							   + "/ka ride :\n"
							   + referenceLeave + "\n"
							   + referenceLeaveOther + "\n"
							   + "/ka ranking :\n"
							   + "/ka item :\n"
							   + "/ka reload :\n"
							   + "===========================================\n";
	public CMD(){
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(label.equalsIgnoreCase("ka")){
			for(int i = 0; i < args.length; i++){
				args[i] = args[i].toLowerCase();
			}

			if(sender instanceof Player)
				this.cmd = new CMDAbstractPlayer((Player)sender, args);
			else if(sender instanceof BlockCommandSender)
				this.cmd = new CMDAbstractBlock(args);
			else
				this.cmd = new CMDAbstractConsole(args);

			if(args.length == 0)
				this.cmd.ka();
			else if(args[0].equalsIgnoreCase("circuit"))
				this.cmd.circuit();
			else if(args[0].equalsIgnoreCase("display"))
				this.cmd.display();
			else if(args[0].equalsIgnoreCase("menu"))
				this.cmd.menu();
			else if(args[0].equalsIgnoreCase("entry"))
				this.cmd.entry();
			else if(args[0].equalsIgnoreCase("exit"))
				this.cmd.exit();
			else if(args[0].equalsIgnoreCase("character"))
				this.cmd.character();
			//else if(args[0].equalsIgnoreCase("characterreset"))
			//	this.cmd.characterreset();
			else if(args[0].equalsIgnoreCase("kart"))
				this.cmd.ride();
			else if(args[0].equalsIgnoreCase("leave"))
				this.cmd.leave();
			else if(args[0].equalsIgnoreCase("ranking"))
				this.cmd.ranking();
			else if(args[0].equalsIgnoreCase("reload"))
				this.cmd.reload();
			else if(args[0].equalsIgnoreCase("item"))
				this.cmd.additem(null, null);
			else if(args[0].equalsIgnoreCase("mushroom"))
				this.cmd.additem(EnumItem.Mushroom.getItem(), Permission.itemcmd_mushroom);
			else if(args[0].equalsIgnoreCase("powerfullmushroom"))
				this.cmd.additem(EnumItem.PowerfullMushroom.getItem(), Permission.itemcmd_powerfullmushroom);
			else if(args[0].equalsIgnoreCase("turtle"))
				this.cmd.additem(EnumItem.Turtle.getItem(), Permission.itemcmd_turtle);
			else if(args[0].equalsIgnoreCase("redturtle"))
				this.cmd.additem(EnumItem.RedTurtle.getItem(), Permission.itemcmd_redturtle);
			else if(args[0].equalsIgnoreCase("thornedturtle"))
				this.cmd.additem(EnumItem.ThornedTurtle.getItem(), Permission.itemcmd_thornedturtle);
			else if(args[0].equalsIgnoreCase("banana"))
				this.cmd.additem(EnumItem.Banana.getItem(), Permission.itemcmd_banana);
			else if(args[0].equalsIgnoreCase("fakeitembox"))
				this.cmd.additem(EnumItem.FakeItembox.getItem(), Permission.itemcmd_fakeitembox);
			else if(args[0].equalsIgnoreCase("thunder"))
				this.cmd.additem(EnumItem.Thunder.getItem(), Permission.itemcmd_thunder);
			else if(args[0].equalsIgnoreCase("star"))
				this.cmd.additem(EnumItem.Star.getItem(), Permission.itemcmd_star);
			else if(args[0].equalsIgnoreCase("teresa"))
				this.cmd.additem(EnumItem.Teresa.getItem(), Permission.itemcmd_teresa);
			else if(args[0].equalsIgnoreCase("gesso"))
				this.cmd.additem(EnumItem.Gesso.getItem(), Permission.itemcmd_gesso);
			else if(args[0].equalsIgnoreCase("killer"))
				this.cmd.additem(EnumItem.Killer.getItem(), Permission.itemcmd_killer);
			else if(args[0].equalsIgnoreCase("itemboxtool"))
				this.cmd.additem(EnumItem.ItemBox.getItem(), Permission.op_cmd_itemboxtool);
			else if(args[0].equalsIgnoreCase("itemboxtooltier2"))
				this.cmd.additem(EnumItem.ItemBoxTier2.getItem(), Permission.op_cmd_itemboxtool);
			else if(args[0].equalsIgnoreCase("fakeitemboxtool"))
				this.cmd.additem(EnumItem.ItemBoxFake.getItem(), Permission.op_cmd_itemboxtool);
			return true;
		}
		return false;
	}

	protected void messageNoPlayer(Player p){
		Util.sendMessage(p, "#Red指定したプレイヤーはログインしていません");
	}

	protected void messageInvalidWorld(Player p){
		Util.sendMessage(p, "#Red指定したワールドは存在しません");
	}

	protected void messageInvalidNumber(Player p){
		Util.sendMessage(p, "#Red数値を入力して下さい");
	}

	protected void messageInvalidCircuit(Player p, String circuitname){
		Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
	}

	protected void messageInvalidCharacter(Player p){
		Util.sendMessage(p, "#Redキャラクター名が正しくありません\n#White" + EnumCharacter.getCharacterList());
	}

	protected void messageInvalidKart(Player p){
		Util.sendMessage(p, "#Redカート名が正しくありません\n#White" + EnumKarts.getKartList());
	}

	protected void messageDisplay(Player p, EnumKarts kart){
		Util.sendMessage(p, "ディスプレイ専用の#White" + kart.getName() + "カート#Greenを設置しました");
	}

	protected void messageMenuAll(Player p){
		Util.sendMessage(p, "全プレイヤーにメニューを表示しました");
	}

	protected void messageMenuOther(Player p, Player other){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんにメニューを表示しました");
	}

	protected void messageEntryOther(Player p, Player other, String circuitname){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんをサーキット：#Gold" + circuitname + "#Greenにエントリーしました");
	}

	protected void messageEntryAll(Player p, String circuitname){
		Util.sendMessage(p, "全プレイヤーをサーキット：#Gold" + circuitname + "#Greenにエントリーしました");
	}

	protected void messageExitAll(Player p){
		Util.sendMessage(p, "全プレイヤーのエントリーを取り消しました");
	}

	protected void messageExitOther(Player p, Player other){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんのエントリーを取り消しました");
	}

	protected void messageCharacterAll(Player p, EnumCharacter character){
		Util.sendMessage(p, "全プレイヤーのキャラクターを#White" + character.getName() + "#Greenにセットしました");
	}

	protected void messageCharacterRandomAll(Player p){
		Util.sendMessage(p, "全プレイヤーのキャラクターを#Whiteランダム#Greenにセットしました");
	}

	protected void messageCharacterOther(Player p, Player other, EnumCharacter character){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんのキャラクターを#White" + character.getName() + "#Greenにセットしました");
	}

	protected void messageCharacterResetAll(Player p){
		Util.sendMessage(p, "全プレイヤーのキャラクター選択を取り消しました");
	}

	protected void messageCharacterResetOther(Player p, Player other){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんのキャラクター選択を取り消しました");
	}

	protected void messageRideAll(Player p, EnumKarts kart){
		Util.sendMessage(p, "全プレイヤーを#White" + kart.getName() + "カート#Greenに搭乗させました");
	}

	protected void messageRideRandomAll(Player p){
		Util.sendMessage(p, "全プレイヤーを#Whiteランダムカート#Greenに搭乗させました");
	}

	protected void messageRideOther(Player p, Player other, EnumKarts kart){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんを#White" + kart.getName() + "カート#Greenに搭乗させました");
	}

	protected void messageLeaveAll(Player p){
		Util.sendMessage(p, "全プレイヤーの搭乗を解除しました");
	}

	protected void messageLeaveOther(Player p, Player other){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんの搭乗を解除しました");
	}

	protected void messageRankingOther(Player p, Player other, String circuitname){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんにサーキット：#Gold" + circuitname + "#Greenのランキングを表示しました");
	}

	protected void messageRankingAll(Player p, String circuitname){
		Util.sendMessage(p, "全プレイヤーにサーキット：#Gold" + circuitname + "#Greenのランキングを表示しました");
	}

	protected void messageAddItem(Player p, ItemStack item){
		Util.sendMessage(p, "#White" + item.getItemMeta().getDisplayName() + "#Greenを配布しました");
	}

	protected void messageAddItemOther(Player p, Player other, ItemStack item){
		Util.sendMessage(p, "#White" + other.getName() + "#Greenさんに#White" +  item.getItemMeta().getDisplayName() + "#Greenを配布しました");
	}

	protected void messageAddItemAll(Player p, ItemStack item){
		Util.sendMessage(p, "全プレイヤーに#White" + item.getItemMeta().getDisplayName() + "#Greenを配布しました");
	}
}