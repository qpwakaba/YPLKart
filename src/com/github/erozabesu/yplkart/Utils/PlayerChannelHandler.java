package com.github.erozabesu.yplkart.Utils;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Object.Race;
import com.github.erozabesu.yplkart.Object.RaceManager;

public class PlayerChannelHandler extends ChannelDuplexHandler{
	/*@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

	}*/

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutNamedEntitySpawn")){
			final Player p = Bukkit.getPlayer((UUID) Util.getFieldValue(msg, "b"));
			final Race r = RaceManager.getRace(p);
			//Human以外のキャラクターを選択している場合パケットを偽装

			if(r.getCharacter() == null){
				super.write(ctx, msg, promise);
			}else if(r.getCharacter().equals(EnumCharacter.Human)){
				super.write(ctx, msg, promise);
			}else{
				super.write(ctx, PacketUtil.getDisguisePacket(p, r.getCharacter()), promise);
			}
			return;
		}else{
			super.write(ctx, msg, promise);
		}
	}
}
