package com.github.erozabesu.yplkart.override;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.listener.NettyListener;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class PlayerChannelHandler extends ChannelDuplexHandler {

    private static double locationYOffset = 1.4D;

    @SuppressWarnings("unchecked")
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            /*
             * プレイヤーがLivingEntityに姿を変えていた場合、LivingEntityのMetadataパケットに
             * EntityHuman特有のデータが送信されクライアントがクラッシュしてしまうため該当データを削除する
             * EntityHuman特有のデータとは、PacketPlayOutEntityMetadata.bに格納されている
             * List<DataWatcher.WatchableObject>の中の特定の4つのデータ
             * DataWatcher.WatchableObject.a()メソッドを実行するとWatchableObjectのindexが取得できるので
             * indexが10、16、17、18に一致したものを削除する
             * 各データが何を指すかはhttp://wiki.vg/Entities#Entity_Metadata_FormatのHumanの項目を参照。
             */
            if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityMetadata")) {
                int entityId = (Integer) ReflectionUtil.getFieldValue(
                        ReflectionUtil.field_PacketPlayOutEntityMetadata_EntityId, msg);

                Player player = NettyListener.getPlayerByEntityId(entityId);

                if (player == null) {
                    super.write(ctx, msg, promise);
                    return;
                } else {
                    Racer r = RaceManager.getRace(player);

                    if (r.getCharacter() == null) {
                        super.write(ctx, msg, promise);
                    } else if (r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                        super.write(ctx, msg, promise);
                    } else {
                        List<Object> watchableObjectList = (List<Object>) ReflectionUtil.getFieldValue(
                                ReflectionUtil.field_PacketPlayOutEntityMetadata_WatchableObject, msg);
                        Iterator iterator = watchableObjectList.iterator();
                        while (iterator.hasNext()) {
                            Object watchableObject = iterator.next();
                            int index = (Integer) watchableObject.getClass().getMethod("a").invoke(watchableObject);
                            if (index == 10 || index == 16 || index == 17 || index == 18) {
                                iterator.remove();
                            }
                        }
                    }
                }

            //Human以外のキャラクターを選択している場合パケットを偽装
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutNamedEntitySpawn")) {
                UUID uuid = (UUID) ReflectionUtil.getFieldValue(
                        ReflectionUtil.field_PacketPlayOutNamedEntitySpawn_UUID, msg);
                Player player = Bukkit.getPlayer(uuid);
                Racer r = RaceManager.getRace(player);

                if (r.getCharacter() == null) {
                    super.write(ctx, msg, promise);
                } else if (r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                    super.write(ctx, msg, promise);
                } else {
                    super.write(ctx, PacketUtil.getDisguiseLivingEntityPacket(player
                            , r.getCharacter().getNmsClass(), 0, 0, 0), promise);
                }
                return;

            //Human以外のキャラクターを選択している場合、装備の情報を全て破棄する
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityEquipment")) {
                int entityId = (Integer) ReflectionUtil.getFieldValue(
                        ReflectionUtil.field_PacketPlayOutEntityEquipment_EntityId, msg);
                Player player = NettyListener.getPlayerByEntityId(entityId);

                if (player == null) {
                    super.write(ctx, msg, promise);
                    return;
                } else {
                    Racer r = RaceManager.getRace(player);

                    if (r.getCharacter() == null) {
                        super.write(ctx, msg, promise);
                    } else if (r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                        super.write(ctx, msg, promise);
                    } else {
                        // Do nothing
                    }
                }

            //カートエンティティが移動中のパケットのY座標をずらし、地中に半分埋める
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityTeleport")) {
                int entityId = (Integer) ReflectionUtil.getFieldValue(
                        ReflectionUtil.field_PacketPlayOutEntityTeleport_EntityId, msg);
                Entity kartEntity = RaceManager.getKartEntityFromEntityId(entityId);

                if (kartEntity != null) {
                    Location location = kartEntity.getLocation();
                    int locationY = (int) ((location.getY() - locationYOffset) * 32.0D);
                    byte yaw = (byte) (location.getYaw() * 256 / 360.0F);

                    ReflectionUtil.setFieldValue(
                            ReflectionUtil.field_PacketPlayOutEntityTeleport_LocationY, msg, locationY);
                    ReflectionUtil.setFieldValue(
                            ReflectionUtil.field_PacketPlayOutEntityTeleport_LocationYaw, msg, yaw);

                    super.write(ctx, msg, promise);
                    return;
                } else {
                    super.write(ctx, msg, promise);
                }

            //カートエンティティがスポーン時のパケットのY座標をずらし、地中に半分埋める
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutSpawnEntity")) {
                int id = (Integer) ReflectionUtil.getFieldValue(
                        ReflectionUtil.field_PacketPlayOutSpawnEntity_EntityId, msg);
                Entity kartEntity = RaceManager.getKartEntityFromEntityId(id);

                if (kartEntity != null) {
                    Location location = kartEntity.getLocation();
                    int locationY = (int) ((location.getY() - locationYOffset) * 32.0D);
                    byte yaw = (byte) (location.getYaw() * 256 / 360.0F);

                    ReflectionUtil.setFieldValue(
                            ReflectionUtil.field_PacketPlayOutSpawnEntity_LocationY, msg, locationY);
                    ReflectionUtil.setFieldValue(
                            ReflectionUtil.field_PacketPlayOutSpawnEntity_LocationYaw, msg, yaw);

                    super.write(ctx, msg, promise);
                    return;
                } else {
                    super.write(ctx, msg, promise);
                }
            } else {
                super.write(ctx, msg, promise);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
