package com.github.erozabesu.yplkart;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.erozabesu.yplkart.cmd.CMD;
import com.github.erozabesu.yplkart.connector.VaultConnector;
import com.github.erozabesu.yplkart.data.DisplayKartData;
import com.github.erozabesu.yplkart.data.Messages;
import com.github.erozabesu.yplkart.data.RaceData;
import com.github.erozabesu.yplkart.data.Settings;
import com.github.erozabesu.yplkart.listener.DataListener;
import com.github.erozabesu.yplkart.listener.ItemListener;
import com.github.erozabesu.yplkart.listener.NettyListener;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class YPLKart extends JavaPlugin {
    public static String PLUGIN_NAME;
    public static String PLUGIN_VERSION;

    private VaultConnector vaultConnection;

    @Override
    public void onEnable() {
        PLUGIN_NAME = this.getDescription().getName();
        PLUGIN_VERSION = this.getDescription().getVersion();

        CMD CMDExecutor = new CMD();
        getCommand("ka").setExecutor(CMDExecutor);

        new ReflectionUtil();
        new Util();
        new PacketUtil();

        new DataListener(this);
        new ItemListener(this);
        new NettyListener(this);
        new Settings(this);
        new RaceData(this);
        new DisplayKartData(this);

        Messages.reloadConfig();

        for (World w : Bukkit.getWorlds()) {
            DisplayKartData.respawnKart(w);
        }

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.vaultConnection = VaultConnector.loadPlugin(getServer().getPluginManager().getPlugin("Vault"));
        }

        //Message.sendAbsolute(null,
        //        "[" + PLUGIN_NAME + "] v." + PLUGIN_VERSION + " Plugin has been Enabled");
    }

    @Override
    public void onDisable() {
        Scoreboards.clearBoard();
        RaceManager.endAllCircuit();
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public static YPLKart getInstance() {
        return (YPLKart) Bukkit.getPluginManager().getPlugin("YPLKart");
    }

    public VaultConnector getVaultConnector() {
        return this.vaultConnection;
    }

    public static File getPluginFile() {
        return getInstance().getFile();
    }
}