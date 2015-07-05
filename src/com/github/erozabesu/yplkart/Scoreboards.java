package com.github.erozabesu.yplkart;

import java.util.HashMap;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Utils.Util;

//各サーキット毎にスコアボードを一枚作成しておいて、参加者に表示するだけ
public class Scoreboards{
	private static HashMap<String, Scoreboard> scoreboard = new HashMap<String, Scoreboard>();

	private static String getPlayerRegisterName(UUID id){
		String name = Bukkit.getPlayer(id) != null ? Bukkit.getPlayer(id).getName() : Bukkit.getOfflinePlayer(id).getName();
		return ChatColor.GREEN + name;
	}

	/* Server().getScoreboardManager().getMainScoreboard()
	 * で取得されるスコアボードは初期状態で全プレイヤーに表示されているスコアボード？
	 * 登録チームもスコアも存在しないため何も表示されないが、そこに新たなチーム、スコアを登録すると
	 * p.setScoreboard(scoreboard)をしなくても全プレイヤーにそのスコアが表示される。
	 *
	 * Server().getScoreboardManager().getNewScoreboard()
	 * で作成されるスコアボードは、作成、チーム登録、スコアセットをするだけでは誰にも表示されない。
	 * 新規に作成したスコアボードはp.setScoreboard(scoreboard)して初めてそのプレイヤーに表示される。
	 *
	 * Scoreboard?
	 *  ∟Objective
	 *     ∟Sidebar/BellowName/TabList
	 *        ∟DisplayName
	 *     ∟ScoreList
	 *        ∟PlayerStringName
	 *           ∟Score
	 *  ∟Team
	 *     ∟TeamName
	 *       TeamSetting
	 *       PlayerList
	 */

	public static void createScoreboard(String name){
		if(!Settings.EnableScoreboard)return;
		if(scoreboard.containsKey(name))return;

		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

		Objective obj = sb.registerNewObjective(YPLKart.plname, "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(Util.convertInitialUpperString(name) + " 参加申請中");

		Team team = sb.registerNewTeam(YPLKart.plname);
		team.setCanSeeFriendlyInvisibles(false);
		team.setAllowFriendlyFire(false);

		scoreboard.put(name, sb);
	}

	public static void startCircuit(String name){
		if(scoreboard.get(name) == null)return;

		Scoreboard sb = scoreboard.get(name);
		Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
		if(obj.getDisplayName().equalsIgnoreCase(Util.convertInitialUpperString(name) + " 参加申請中"))
			obj.setDisplayName("ランキング");
	}

	public static void endCircuit(String name){
		if(scoreboard.get(name) == null)return;

		Scoreboard sb = scoreboard.get(name);
		Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
		if(obj.getDisplayName().equalsIgnoreCase("ランキング"))
			obj.setDisplayName(Util.convertInitialUpperString(name) + " 参加申請中");
	}

	public static void entryCircuit(UUID id){
		String entry = RaceManager.getRace(id).getEntry();
		if(entry.equalsIgnoreCase(""))return;

		createScoreboard(entry);
		Scoreboard sb = scoreboard.get(entry);
		if(sb == null)return;


		Team team = sb.getTeam(YPLKart.plname);
		team.addPlayer(Bukkit.getPlayer(id) == null ? Bukkit.getOfflinePlayer(id) : Bukkit.getPlayer(id));
		setPoint(id);

		if(Bukkit.getPlayer(id) != null)
			Bukkit.getPlayer(id).setScoreboard(sb);
	}

	public static void exitCircuit(UUID id){
		String entry = RaceManager.getRace(id).getEntry();
		if(entry.equalsIgnoreCase(""))return;

		Scoreboard sb = scoreboard.get(entry);
		if(sb == null)return;

		sb.getTeam(YPLKart.plname).removeEntry(getPlayerRegisterName(id));
		sb.resetScores(getPlayerRegisterName(id));
		hideBoard(id);
	}

	public static void setPoint(UUID id){
		String entry = RaceManager.getRace(id).getEntry();
		if(entry.equalsIgnoreCase(""))return;

		scoreboard.get(entry).getObjective(YPLKart.plname).getScore(getPlayerRegisterName(id)).setScore(RaceManager.getRace(id).getPoint());
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void clearBoard(){
		for(String key : scoreboard.keySet()){
			scoreboard.get(key).clearSlot(DisplaySlot.SIDEBAR);
		}
	}

	public static void hideBoard(UUID id){
		if(Bukkit.getPlayer(id) != null)
			Bukkit.getPlayer(id).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public static void showBoard(UUID id){
		String entry = RaceManager.getRace(id).getEntry();
		if(entry.equalsIgnoreCase(""))return;

		Scoreboard sb = scoreboard.get(entry);
		if(sb == null)return;

		if(Bukkit.getPlayer(id) != null)
			Bukkit.getPlayer(id).setScoreboard(sb);
	}
}
