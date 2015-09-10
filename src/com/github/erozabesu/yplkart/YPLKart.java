package com.github.erozabesu.yplkart;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.erozabesu.yplkart.cmd.KaCommand;
import com.github.erozabesu.yplkart.connector.VaultConnector;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.listener.DataListener;
import com.github.erozabesu.yplkart.listener.ItemListener;
import com.github.erozabesu.yplkart.listener.KartListener;
import com.github.erozabesu.yplkart.listener.NettyListener;
import com.github.erozabesu.yplkart.listener.RaceListener;

public class YPLKart extends JavaPlugin {
    private static YPLKart PLUGIN;
    public static String PLUGIN_NAME;
    public static String PLUGIN_VERSION;

    private ConfigManager configManager;
    private VaultConnector vaultConnection;

    @Override
    public void onEnable() {
        PLUGIN = this;
        PLUGIN_NAME = this.getDescription().getName();
        PLUGIN_VERSION = this.getDescription().getVersion();

        KaCommand executor = new KaCommand();
        getCommand("ka").setExecutor(executor);

        //全コンフィグの読み込み、格納
        ConfigManager.reloadAllConfig();

        new DataListener();
        new RaceListener();
        new KartListener();
        new ItemListener();
        new NettyListener();

        //全DisplayKartオブジェクトのEntityを再生成する
        DisplayKartConfig.respawnAllKart();

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.vaultConnection = VaultConnector.loadPlugin(getServer().getPluginManager().getPlugin("Vault"));
        }
    }

    @Override
    public void onDisable() {
        Scoreboards.clearBoard();
        RaceManager.endAllCircuit(true);

        Bukkit.getPluginManager().disablePlugin(this);
    }

    public static YPLKart getInstance() {
        return PLUGIN;
    }

    public VaultConnector getVaultConnector() {
        return this.vaultConnection;
    }

    public static ConfigManager getConfigManager(){
        return getInstance().configManager;
    }

    public static File getPluginFile() {
        return getInstance().getFile();
    }

    public static boolean isPluginEnabled(World world) {
        if (Boolean.valueOf(String.valueOf(ConfigEnum.ENABLE_THIS_PLUGIN.getValue()))) {
            List<Object> disabledWorldList = Arrays.asList(ConfigEnum.DISABLED_WORLDS.getValue());
            if (!disabledWorldList.contains(world.getName())) {
                return true;
            }
        }
        return false;
    }
}