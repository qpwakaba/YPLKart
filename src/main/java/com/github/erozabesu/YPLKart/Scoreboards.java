package main.java.com.github.erozabesu.YPLKart;

import java.util.HashMap;

import main.java.com.github.erozabesu.YPLKart.Data.Settings;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

//各サーキット毎にスコアボードを一枚作成しておいて、参加者に表示するだけ
public class Scoreboards{
	private static HashMap<String, Scoreboard> scoreboard = new HashMap<String, Scoreboard>();

	private static String getPlayerRegisterName(Player p){
		return ChatColor.GREEN + p.getName();
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
		obj.setDisplayName("ランキング");

		Team team = sb.registerNewTeam(YPLKart.plname);
		team.setCanSeeFriendlyInvisibles(false);
		team.setAllowFriendlyFire(false);

		scoreboard.put(name, sb);
	}

	public static void entryCircuit(Player p){
		String entry = RaceManager.getRace(p).getEntry();
		if(entry.equalsIgnoreCase(""))return;

		createScoreboard(entry);
		Scoreboard sb = scoreboard.get(entry);
		if(sb == null)return;

		Team team = sb.getTeam(YPLKart.plname);
		team.addPlayer(p);
		setPoint(p);
		p.setScoreboard(sb);
	}


	public static void exitCircuit(Player p){
		String entry = RaceManager.getRace(p).getEntry();
		if(entry.equalsIgnoreCase(""))return;

		Scoreboard sb = scoreboard.get(entry);
		if(sb == null)return;

		sb.getTeam(YPLKart.plname).removeEntry(getPlayerRegisterName(p));
		sb.resetScores(getPlayerRegisterName(p));
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public static void setPoint(Player p){
		String entry = RaceManager.getRace(p).getEntry();
		if(entry.equalsIgnoreCase(""))return;

		scoreboard.get(entry).getObjective(YPLKart.plname).getScore(getPlayerRegisterName(p)).setScore(RaceManager.getRace(p).getPoint());
	}

	public static void clearBoard(){
		for(String key : scoreboard.keySet()){
			scoreboard.get(key).clearSlot(DisplaySlot.SIDEBAR);
		}
	}

	public static void clearBoard(Player p){
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public static void showBoard(Player p){
		String entry = RaceManager.getRace(p).getEntry();
		if(entry.equalsIgnoreCase(""))return;

		Scoreboard sb = scoreboard.get(entry);
		if(sb == null)return;

		p.setScoreboard(sb);
	}
}
