package com.github.erozabesu.yplkart.Listener;

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
import com.github.erozabesu.yplkart.OverrideClass.PlayerChannelHandler;
import com.github.erozabesu.yplkart.Utils.PacketUtil;

public class NettyListener implements Listener{
	private static YPLKart pl;
	public static HashMap<Integer, String> playerEntityId = new HashMap<Integer, String>();
	public NettyListener(YPLKart plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		pl = plugin;
	}

	public static void inject(Player p) throws Exception{
        Channel channel = PacketUtil.getChannel(p);
        PlayerChannelHandler pch = new PlayerChannelHandler();
        if(channel.pipeline().get(PlayerChannelHandler.class) == null){
            channel.pipeline().addBefore("packet_handler", YPLKart.plname, pch);
        }
    }

    public static void remove(final Player p) throws Exception{
        final Channel channel = PacketUtil.getChannel(p);
        if(channel.pipeline().get(PlayerChannelHandler.class) != null){
            channel.pipeline().remove(PlayerChannelHandler.class);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) throws Exception{
    	if(e.getPlugin().equals(pl)){
    		for(Player player: Bukkit.getOnlinePlayers()) {
    			remove(player);
    		}
    	}
    }

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) throws Exception{
		playerEntityId.put(e.getPlayer().getEntityId(), e.getPlayer().getUniqueId().toString());
		inject(e.getPlayer());
	}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) throws Exception{
    	playerEntityId.remove(e.getPlayer().getEntityId());
    	remove(e.getPlayer());
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) throws Exception{
    	if(e.getPlugin().equals(pl)){
    		playerEntityId.clear();
    		for(Player p: Bukkit.getOnlinePlayers()){
    			playerEntityId.put(p.getEntityId(), p.getUniqueId().toString());
    			inject(p);
    		}
    	}
    }
}
