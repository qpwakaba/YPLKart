package com.github.erozabesu.yplkart.listener;

import io.netty.channel.Channel;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.override.PlayerChannelHandler;
import com.github.erozabesu.yplkart.utils.PacketUtil;

public class NettyListener implements Listener {
    private static HashMap<Integer, Player> playerEntityId = new HashMap<Integer, Player>();

    public NettyListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, YPLKart.getInstance());
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Player getPlayerByEntityId(int entityId) {
        if (playerEntityId.containsKey(entityId)) {
            return playerEntityId.get(entityId);
        }

        return null;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void putPlayerEntityId(Player player) {
        playerEntityId.put(player.getEntityId(), player);
    }

    //〓 Do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void inject(Player player) throws Exception {
        Channel channel = PacketUtil.getChannel(player);
        PlayerChannelHandler pch = new PlayerChannelHandler();
        if (channel.pipeline().get(PlayerChannelHandler.class) == null) {
            channel.pipeline().addBefore("packet_handler", YPLKart.PLUGIN_NAME, pch);
        }
    }

    public static void remove(Player player) throws Exception {
        final Channel channel = PacketUtil.getChannel(player);
        if (channel.pipeline().get(PlayerChannelHandler.class) != null) {
            channel.pipeline().remove(PlayerChannelHandler.class);
        }
    }

    //〓 Event 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) throws Exception {
        if (event.getPlugin().equals(YPLKart.getInstance())) {
            playerEntityId.clear();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerEntityId.put(player.getEntityId(), player);
                inject(player);
            }
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) throws Exception {
        if (event.getPlugin().equals(YPLKart.getInstance())) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws Exception {
        Player player = event.getPlayer();

        playerEntityId.put(player.getEntityId(), player);
        inject(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws Exception {
        Player player = event.getPlayer();

        playerEntityId.remove(player.getEntityId());
        remove(player);
        PacketUtil.removeAllData(player);
    }
}
