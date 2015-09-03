package com.github.erozabesu.yplkart.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;

public class DataListener implements Listener {

    public DataListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, YPLKart.getInstance());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!YPLKart.isPluginEnabled(e.getWorld()))
            return;
        DisplayKartConfig.respawnKart(e.getChunk());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (!YPLKart.isPluginEnabled(e.getWorld()))
            return;
        DisplayKartConfig.respawnKart(e.getWorld());
    }
}
