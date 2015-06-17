package main.java.com.github.erozabesu.YPLKart;

import main.java.com.github.erozabesu.YPLKart.Data.Settings;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Scoreboards{
	private static String rank = ChatColor.GREEN + "順位" + ChatColor.WHITE;

	public static void createBoard(Player plr){
		if(Settings.EnableScoreboard){
			Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
			addContents(sb, plr);

			Team team = sb.registerNewTeam(plr.getName());
			team.setCanSeeFriendlyInvisibles(true);
			team.setAllowFriendlyFire(false);
			team.addPlayer(plr);
			plr.setScoreboard(sb);
		}else{
			clearBoard(plr);
		}
	}

	public static void reloadBoard(Player p){
		if(!Settings.EnableScoreboard)return;

		clearBoard(p);
		Scoreboard sb = p.getScoreboard();
		addContents(sb, p);
	}

	public static void addContents(Scoreboard sb, Player p){
		Objective obj;
		obj = sb.registerNewObjective(p.getName(), "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName("ランキング");
		setBoardCount(p, obj);
	}

	public static void clearBoard(Player p){
		if(p.getScoreboard() != null){
			Scoreboard sb = p.getScoreboard();
			sb.clearSlot(DisplaySlot.SIDEBAR);
			sb.clearSlot(DisplaySlot.BELOW_NAME);
			sb.clearSlot(DisplaySlot.PLAYER_LIST);
			if(sb.getObjective(p.getName()) != null){
				 sb.getObjective(p.getName()).unregister();
			}
		}else{
			Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
			p.setScoreboard(sb);
		}
	}

	public static void setBoardCount(Player p, Objective obj){
		for(Player entry : RaceManager.getEntryPlayer()){
			try{
				obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + entry.getName())).setScore(RaceManager.getRace(entry).getPoint());
			}catch(NullPointerException exception){}
		}
		for (Player entry : RaceManager.getGoalPlayer()){
			try{
				obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + entry.getName())).setScore(RaceManager.getRace(entry).getPoint());
			}catch(NullPointerException exception){}
		}
	}
}
