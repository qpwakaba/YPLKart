package com.github.erozabesu.yplkart.Cmd;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Enum.EnumItem;

public class CMD implements CommandExecutor{
	CMDAbstract cmd;
	protected String itemlist = "mushroom, powerfullmushroom, banana, fakeitembox, turtle, redturtle, thornedturtle, gesso, thunder, star, teresa, killer, itemboxtool, itemboxtooltier2, fakeitemboxtool";

	protected String referenceCircuitIngame = "/ka circuit info {circuit name} :\n"
											+ "/ka circuit create {circuit name} :\n"
											+ "/ka circuit create {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											+ "/ka circuit delete {circuit name} :\n"
											+ "/ka circuit edit {circuit name} :\n"
											+ "/ka circuit setlap {circuit name} {number of laps} :\n"
											+ "/ka circuit setminplayer {circuit name} {number of player} :\n"
											+ "/ka circuit setmaxplayer {circuit name} {number of player} :\n"
											+ "/ka circuit setmatchingtime {circuit name} {number of second} :\n"
											+ "/ka circuit setmenutime {circuit name} {number of second} :\n"
											+ "/ka circuit setlimittime {circuit name} {number of second} :\n"
											+ "/ka circuit setposition {circuit name} :\n"
											+ "/ka circuit setposition {circuit name} {worldname} {x} {y} {z} {yaw} {pitch} :\n"
											+ "/ka circuit broadcastgoal {circuit name} {true or false} :\n"
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

	protected String referenceCharacterReset = 		"/ka characterreset :";
	protected String referenceCharacterResetOther = "/ka characterreset {player name} :\n"
												  + "/ka characterreset all :";

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
							   + referenceCharacterReset + "\n"
							   + referenceCharacterResetOther + "\n"
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
			else if(args[0].equalsIgnoreCase("characterreset"))
				this.cmd.characterreset();
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
			else{
				EnumItem item = null;
				if((item = EnumItem.getEnumItem(args[0])) != null)
					this.cmd.additem(item.getItem(), item.getPermission());
			}
			return true;
		}
		return false;
	}
}