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
import com.github.erozabesu.yplkart.enumdata.KartType;
import com.github.erozabesu.yplkart.listener.NettyListener;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.KartUtil;
import com.github.erozabesu.yplutillibrary.reflection.Fields;
import com.github.erozabesu.yplutillibrary.reflection.Methods;
import com.github.erozabesu.yplutillibrary.util.PacketUtil;
import com.github.erozabesu.yplutillibrary.util.ReflectionUtil;

public class PlayerChannelHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String packetName = msg.getClass().getSimpleName();
        /*
         * プレイヤーがエンティティから降りるパケットを受信した場合、仮想スニークフラグをtrueに変更する
         *
         * 水中でカートエンティティに搭乗した場合、強制的に搭乗を解除されてしまうため、
         * VehicleExitEventを常にsetCancelled(true)する必要がある。
         * その影響で、自発的にプレイヤーが搭乗を解除したい場合もキャンセルされてしまうため、
         * 仮想スニークフラグを利用し、フラグがtrueの場合、イベントのキャンセルを行わないようにする。
         */
        if(packetName.equalsIgnoreCase("PacketPlayInSteerVehicle")){

            //エンティティから降りるかどうか
            boolean unmount = (Boolean) ReflectionUtil.getFieldValue(
                    Fields.nmsPacketPlayInSteerVehicle_isUnmount, msg);

            //NetworkManagerからプレイヤーを取得
            Object networkManager = ctx.pipeline().toMap().get("packet_handler");
            Player player = PacketUtil.getPlayerByNetworkManager(networkManager);

            //擬似スニークフラグをfalseにする
            RaceManager.getRacer(player).setSneaking(unmount);
        }

        super.channelRead(ctx, msg);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityMetadata")) {
                int entityId = (Integer) ReflectionUtil.getFieldValue(
                        Fields.nmsPacketPlayOutEntityMetadata_EntityId, msg);

                Player player = NettyListener.getPlayerByEntityId(entityId);

                /*
                 * プレイヤーがLivingEntityに姿を変えていた場合、LivingEntityのMetadataパケットに
                 * EntityHumanのNBTデータが送信されクライアントがクラッシュしてしまうため該当データを削除する。
                 * 該当データは、メンバ変数bに格納されているindexが10、16、17、18のデータ。
                 * DataWatcher.WatchableObject.a()メソッドを実行するとindexが取得できるので
                 * 該当indexのデータを削除する。
                 * 各データが何を指すかはhttp://wiki.vg/Entities#Entity_Metadata_FormatのHumanの項目を参照。
                 */
                if (player != null) {
                    Racer r = RaceManager.getRacer(player);

                    //選択キャラクターのエンティティタイプがEntityHuman以外
                    if (r.getCharacter() != null && !r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                        //NBTが格納されているリストを取得
                        List<Object> watchableObjectList = (List<Object>) ReflectionUtil.getFieldValue(
                                Fields.nmsPacketPlayOutEntityMetadata_WatchableObject, msg);
                        Iterator iterator = watchableObjectList.iterator();

                        Object watchableObject = null;

                        //該当indexのNBTを破棄
                        while (iterator.hasNext()) {
                            watchableObject = iterator.next();
                            int index = (Integer) Methods.nmsWatchableObject_getIndex.invoke(watchableObject);
                            if (index == 10 || index == 16 || index == 17 || index == 18) {
                                iterator.remove();
                            }
                        }
                    }
                }

            //Human以外のキャラクターを選択している場合、選択キャラクターのエンティティタイプに外見を偽装
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutNamedEntitySpawn")) {
                UUID uuid = (UUID) ReflectionUtil.getFieldValue(
                        Fields.nmsPacketPlayOutNamedEntitySpawn_UUID, msg);
                Player player = Bukkit.getPlayer(uuid);
                Racer r = RaceManager.getRacer(player);

                //キャラクターを選択しており、かつ選択キャラクターのエンティティタイプがHuman以外の場合
                if (r.getCharacter() != null) {
                    if (!r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                        Object packet = PacketUtil
                                .getDisguiseLivingEntityPacket(player, r.getCharacter().getNmsClass());
                        super.write(ctx, packet, promise);
                        return;
                    }
                }

            //Human以外のキャラクターを選択している場合、装備の情報を全て破棄する
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityEquipment")) {
                int entityId = (Integer) ReflectionUtil.getFieldValue(Fields.nmsPacketPlayOutEntityEquipment_EntityId, msg);
                Player player = NettyListener.getPlayerByEntityId(entityId);

                if (player != null) {

                    Racer r = RaceManager.getRacer(player);

                    //キャラクターを選択しており、かつ選択キャラクターのエンティティタイプがHuman以外の場合
                    if (r.getCharacter() != null) {
                        if (!r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                            //returnしパケットを破棄
                            return;
                        }
                    }
                }

            /*
             * 移動中のY座標をずらし地中に半分埋めることで、
             * クライアント描画時の搭乗位置を地面の高さまで下げる
             */
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityTeleport")) {
                int entityId = (Integer) ReflectionUtil.getFieldValue(Fields.nmsPacketPlayOutEntityTeleport_EntityId, msg);
                Entity kartEntity = KartUtil.getKartEntityByEntityId(entityId);

                if (kartEntity != null) {
                    if (!KartUtil.isSpecificKartType(kartEntity, KartType.DisplayKart)) {
                        Location location = CustomArmorStandDelegator.getMountPositionAdjustedLocation(kartEntity);

                        ReflectionUtil.setFieldValue(
                                Fields.nmsPacketPlayOutEntityTeleport_LocationY, msg, (int) (location.getY() * 32.0D));
                        ReflectionUtil.setFieldValue(
                                Fields.nmsPacketPlayOutEntityTeleport_LocationYaw, msg, (byte) (location.getYaw() * 256 / 360.0F));
                    }
                }

            /*
             * スポーン座標のY座標をずらし地中に半分埋めることで、
             * クライアント描画時の搭乗位置を地面の高さまで下げる
             */
            } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutSpawnEntity")) {
                int id = (Integer) ReflectionUtil.getFieldValue(Fields.nmsPacketPlayOutSpawnEntity_EntityId, msg);
                Entity kartEntity = KartUtil.getKartEntityByEntityId(id);

                if (kartEntity != null) {
                    if (!KartUtil.isSpecificKartType(kartEntity, KartType.DisplayKart)) {
                        Location location = CustomArmorStandDelegator.getMountPositionAdjustedLocation(kartEntity);

                        ReflectionUtil.setFieldValue(
                                Fields.nmsPacketPlayOutSpawnEntity_LocationY, msg, (int) (location.getY() * 32.0D));
                        ReflectionUtil.setFieldValue(
                                Fields.nmsPacketPlayOutSpawnEntity_LocationYaw, msg, (byte) (location.getYaw() * 256 / 360.0F));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        super.write(ctx, msg, promise);
    }
}
