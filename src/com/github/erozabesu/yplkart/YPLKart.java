package com.github.erozabesu.yplkart;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.erozabesu.yplkart.Cmd.CMD;
import com.github.erozabesu.yplkart.Connection.VaultConnection;
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

public class YPLKart extends JavaPlugin {
    public static String plname;

    private VaultConnection vaultConnection;

    @Override
    public void onEnable() {
        plname = this.getDescription().getName();
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

        Message.reloadConfig();

        for (World w : Bukkit.getWorlds()) {
            DisplayKartData.respawnKart(w);
        }

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.vaultConnection = VaultConnection.setupConnection(getServer().getPluginManager().getPlugin("Vault"));
        }

        Message.sendAbsolute(null, "[" + plname + "] v." + YPLKart.getInstance().getDescription().getVersion()
                + " Plugin has been Enabled");
    }

    @Override
    public void onDisable() {
        Scoreboards.clearBoard();
        RaceManager.endAllCircuit();
    }

    public static YPLKart getInstance() {
        return (YPLKart) Bukkit.getPluginManager().getPlugin("YPLKart");
    }

    public VaultConnection getVaultConnection() {
        return this.vaultConnection;
    }

    public static File getPluginFile() {
        return getInstance().getFile();
    }
}