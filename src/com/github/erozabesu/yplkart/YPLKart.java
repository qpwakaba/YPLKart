package com.github.erozabesu.yplkart;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.erozabesu.yplkart.cmd.KaCommand;
import com.github.erozabesu.yplkart.connector.VaultConnector;
import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.data.PermissionEnum;
import com.github.erozabesu.yplkart.listener.DataListener;
import com.github.erozabesu.yplkart.listener.ItemListener;
import com.github.erozabesu.yplkart.listener.KartListener;
import com.github.erozabesu.yplkart.listener.NettyListener;
import com.github.erozabesu.yplkart.listener.RaceListener;
import com.github.erozabesu.yplutillibrary.YPLUtilityLibrary;

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

        // ユーティリティライブラリにインスタンスを渡す
        YPLUtilityLibrary.registerLibrary(this);

        KaCommand executor = new KaCommand();
        getCommand("ka").setExecutor(executor);

        reloadAllConfig();

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
        if (ConfigEnum.enable_this_plugin) {
            if (!ConfigEnum.disabled_worlds.contains(world.getName())) {
                return true;
            }
        }
        return false;
    }

    public static void reloadAllConfig() {

        // 以下順序を変更しないこと

        // 全コンフィグファイルの読み込み
        ConfigManager.reloadAllFile();

        // 各コンフィグの値を格納
        ConfigEnum.reload();
        PermissionEnum.reload();
        ItemEnum.reload();
        CharacterConfig.reload();
        KartConfig.reload();
        CircuitConfig.reload();
        DisplayKartConfig.reload();

        // 全メッセージファイルの読み込み
        LanguageManager.reloadAllFile();

        MessageEnum.reload();
    }
}