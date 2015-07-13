package com.github.erozabesu.yplkart;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.erozabesu.yplkart.Cmd.CMD;
import com.github.erozabesu.yplkart.Data.DisplayKartData;
import com.github.erozabesu.yplkart.Data.Message;
import com.github.erozabesu.yplkart.Data.RaceData;
import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Listener.DataListener;
import com.github.erozabesu.yplkart.Listener.ItemListener;
import com.github.erozabesu.yplkart.Listener.NettyListener;
import com.github.erozabesu.yplkart.Utils.PacketUtil;
import com.github.erozabesu.yplkart.Utils.ReflectionUtil;
import com.github.erozabesu.yplkart.Utils.Util;

public class YPLKart extends JavaPlugin{
	private static YPLKart pl;
	public static String plname;

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

		new DataListener(this);
		new ItemListener(this);
		new NettyListener(this);
		new Settings(this);
		new RaceData(this);
		new DisplayKartData(this);
		new ReflectionUtil();
		new Util();
		new PacketUtil();

		Message.reloadConfig();

		for(World w : Bukkit.getWorlds()){
			DisplayKartData.respawnKart(w);
		}

		Message.sendAbsolute(null, "[" + plname + "] v." + YPLKart.getInstance().getDescription().getVersion() + " Plugin has been Enabled");
	}

	@Override
	public void onDisable(){
		Scoreboards.clearBoard();
		RaceManager.endAllCircuit();
	}

	public static YPLKart getInstance(){
		return pl;
	}

	public static File getPluginFile(){
		return getInstance().getFile();
	}
}