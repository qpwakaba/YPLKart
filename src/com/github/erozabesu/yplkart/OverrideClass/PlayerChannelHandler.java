package com.github.erozabesu.yplkart.OverrideClass;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Listener.NettyListener;
import com.github.erozabesu.yplkart.Object.Race;
import com.github.erozabesu.yplkart.Object.RaceManager;
import com.github.erozabesu.yplkart.Utils.PacketUtil;
import com.github.erozabesu.yplkart.Utils.ReflectionUtil;
import com.github.erozabesu.yplkart.Utils.Util;

public class PlayerChannelHandler extends ChannelDuplexHandler{
	@SuppressWarnings("unchecked")
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		try{
			/*
			 * プレイヤーがLivingEntityに姿を変えていた場合、LivingEntityのMetadataパケットに
			 * EntityHuman特有のデータも上乗せ送信されてしまいクライアントがクラッシュするため
			 * 該当データを削除する。
			 * EntityHuman特有のデータとは、
			 * PacketPlayOutEntityMetadata.bに格納されているList<DataWatcher.WatchableObject>の中の
			 * 特定の4つのデータ。
			 * DataWatcher.WatchableObject.a()メソッドを実行するとWatchableObjectのindexが取得できるので
			 * indexが10、16、17、18に一致したものを削除する。
			 * 各データが何を指すかはhttp://wiki.vg/Entities#Entity_Metadata_FormatのHumanの項目を参照。
			 */
			if(msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityMetadata")){
				int id = (int) ReflectionUtil.getFieldValue(msg, "a");
				if(NettyListener.playerEntityId.get(id) == null){
					super.write(ctx, msg, promise);
					return;
				}else{
					Player p = Bukkit.getPlayer(UUID.fromString(NettyListener.playerEntityId.get(id)));
					Race r = RaceManager.getRace(p);

					if(r.getCharacter() == null){
						super.write(ctx, msg, promise);
					}else if(r.getCharacter().equals(EnumCharacter.Human)){
						super.write(ctx, msg, promise);
					}else{
						List<Object> watchableobject = (List<Object>) ReflectionUtil.getFieldValue(msg, "b");
						Iterator i = watchableobject.iterator();
						while(i.hasNext()){
							Object w = i.next();
							int index = (int) w.getClass().getMethod("a").invoke(w);
							if(index == 10 || index == 16 || index == 17 || index == 18){
								i.remove();
							}
						}
					}
				}
			}else if(msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutNamedEntitySpawn")){
				Player p = Bukkit.getPlayer((UUID) Util.getFieldValue(msg, "b"));
				Race r = RaceManager.getRace(p);

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
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
