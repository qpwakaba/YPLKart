package main.java.com.github.erozabesu.YPLKart;

import java.io.File;
import java.util.logging.Logger;

import main.java.com.github.erozabesu.YPLKart.Cmd.CMD;
import main.java.com.github.erozabesu.YPLKart.Data.DisplayKartData;
import main.java.com.github.erozabesu.YPLKart.Data.RaceData;
import main.java.com.github.erozabesu.YPLKart.Data.Settings;
import main.java.com.github.erozabesu.YPLKart.Listener.EventData;
import main.java.com.github.erozabesu.YPLKart.Listener.EventItem;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;
import main.java.com.github.erozabesu.YPLKart.Utils.PacketUtil;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class YPLKart extends JavaPlugin{
	private static YPLKart pl;
	public static String plname;
	public static final Logger log = Logger.getLogger("Minecraft");

	public static FileConfiguration cConfig;
	public static File cConfigFile;
	public static FileConfiguration Config;
	public static File ConfigFile;

	@Override
	public void onEnable(){
		pl = this;
		plname = this.getDescription().getName();
		CMD CMDExecutor = new CMD();
		getCommand("ka").setExecutor(CMDExecutor);
		new EventData(this);
		new EventItem(this);
		new Settings(this);
		new RaceData(this);
		new DisplayKartData(this);

		RaceManager.runRankingUpdateTask();
		RaceManager.runDetectRaceEndTask();
		RaceManager.runLapTimeUpdateTask();

		Util.sendMessage(null, "v." + getDescription().getVersion() + " Loaded Config");
		Util.sendMessage(null, "v." + getDescription().getVersion() + " Plugin has been Enabled");
	}

	@Override
	public void onDisable(){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(Settings.EnableScoreboard){
				Scoreboards.clearBoard(p);
			}
			if(RaceManager.isEntry(p)){
				p.setWalkSpeed(0.2F);
				p.setMaxHealth(20D);
				PacketUtil.returnPlayer(p);
			}
		}
		RaceManager.removeAllJammerEntity();
	}

	public static YPLKart getInstance(){
		return pl;
	}
}
