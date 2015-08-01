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
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class PlayerChannelHandler extends ChannelDuplexHandler {

    private static double locationYOffset = 1.4D;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String packetName = msg.getClass().getSimpleName();
        /*
         * プレイヤーがエンティティから降りるパケットを受信した場合、レース中であれば値を変更する
         *
         * カートエンティティとして利用しているアーマースタンドは、Vehicleクラスを継承していないため、
         * プレイヤーの操作をVehicleEventではフックできない。
         * また、PlayerToggleSneakEventは搭乗中のプレイヤーからはスローされない。
         * そのため、プレイヤーのShiftキー押下をキャンセルできず、レース中であってもカートから降りてしまう。
         * また、エンティティから降りるメソッドは、EntityHumanクラス内で定義されているため、
         * EntityArmorStand内のどのメソッドをOverrideしてもこの操作をキャンセルすることはできない。
         * そこで、クライアントから受信したShiftキー押下のパケットを変更し、
         * レース中はカートから降りられないよう常にfalseに設定している。
         * ただし、この影響でPlayer.isSneaking()が常にfalseを返し、ドリフト機能が動作しないため、
         * Racerオブジェクトの擬似スニークフラグを利用する。
         */
        if(packetName.equalsIgnoreCase("PacketPlayInSteerVehicle")){

            //エンティティから降りるかどうか
            boolean unmount = (Boolean) ReflectionUtil.getFieldValue(
                    ReflectionUtil.field_PacketPlayInSteerVehicle_isUnmount, msg);

            //NetworkManagerからプレイヤーを取得
            Object networkManager = ctx.pipeline().toMap().get("packet_handler");
            Player player = PacketUtil.getPlayerByNetworkManager(networkManager);

            if (player != null) {
                //レース中であれば値をfalseに変更する
                if (unmount) {
                    if (RaceManager.isStandBy(player.getUniqueId())) {
                        ReflectionUtil.setFieldValue(
                                ReflectionUtil.field_PacketPlayInSteerVehicle_isUnmount, msg, false);

                        //擬似スニークフラグをtrueにする
                        RaceManager.getRacer(player).setSneaking(true);

                        super.channelRead(ctx, msg);
                        return;
                    }
                }

                //擬似スニークフラグをfalseにする
                RaceManager.getRacer(player).setSneaking(false);
            }

            super.channelRead(ctx, msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityMetadata")) {
            int entityId = (Integer) ReflectionUtil.getFieldValue(
                    ReflectionUtil.field_PacketPlayOutEntityMetadata_EntityId, msg);

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
                            ReflectionUtil.field_PacketPlayOutEntityMetadata_WatchableObject, msg);
                    Iterator iterator = watchableObjectList.iterator();

                    Object watchableObject = null;

                    //該当indexのNBTを破棄
                    while (iterator.hasNext()) {
                        watchableObject = iterator.next();
                        int index = (Integer) ReflectionUtil.nmsWatchableObject_getIndex.invoke(watchableObject);
                        if (index == 10 || index == 16 || index == 17 || index == 18) {
                            iterator.remove();
                        }
                    }
                }
            }

            super.write(ctx, msg, promise);

        //Human以外のキャラクターを選択している場合、選択キャラクターのエンティティタイプに外見を偽装
        } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutNamedEntitySpawn")) {
            UUID uuid = (UUID) ReflectionUtil.getFieldValue(
                    ReflectionUtil.field_PacketPlayOutNamedEntitySpawn_UUID, msg);
            Player player = Bukkit.getPlayer(uuid);
            Racer r = RaceManager.getRacer(player);

            //キャラクターを選択しており、かつ選択キャラクターのエンティティタイプがHuman以外の場合
            if (r.getCharacter() != null) {
                if (!r.getCharacter().getNmsClass().getSimpleName().contains("Human")) {
                    Object packet = PacketUtil
                            .getDisguiseLivingEntityPacket(player, r.getCharacter().getNmsClass(), 0, 0, 0);
                    super.write(ctx, packet, promise);
                    return;
                }
            }

            super.write(ctx, msg, promise);

        //Human以外のキャラクターを選択している場合、装備の情報を全て破棄する
        } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityEquipment")) {
            int entityId = (Integer) ReflectionUtil.getFieldValue(
                    ReflectionUtil.field_PacketPlayOutEntityEquipment_EntityId, msg);
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

            super.write(ctx, msg, promise);

        /*
         * 移動中のY座標をずらし地中に半分埋めることで、
         * クライアント描画時の搭乗位置を地面の高さまで下げる
         */
        } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityTeleport")) {
            int entityId = (Integer) ReflectionUtil.getFieldValue(
                    ReflectionUtil.field_PacketPlayOutEntityTeleport_EntityId, msg);
            Entity kartEntity = RaceManager.getKartEntityByEntityId(entityId);

            if (kartEntity != null) {
                if (!RaceManager.isSpecificKartType(kartEntity, KartType.DisplayKart)) {
                    Location location = kartEntity.getLocation();
                    int locationY = (int) ((location.getY() - locationYOffset) * 32.0D);
                    byte yaw = (byte) (location.getYaw() * 256 / 360.0F);

                    ReflectionUtil.setFieldValue(
                            ReflectionUtil.field_PacketPlayOutEntityTeleport_LocationY, msg, locationY);
                    ReflectionUtil.setFieldValue(
                            ReflectionUtil.field_PacketPlayOutEntityTeleport_LocationYaw, msg, yaw);
                }
            }

            super.write(ctx, msg, promise);

        /*
         * スポーン座標のY座標をずらし地中に半分埋めることで、
         * クライアント描画時の搭乗位置を地面の高さまで下げる
         */
        } else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutSpawnEntity")) {
            int id = (Integer) ReflectionUtil.getFieldValue(
                    ReflectionUtil.field_PacketPlayOutSpawnEntity_EntityId, msg);
            Entity kartEntity = RaceManager.getKartEntityByEntityId(id);

            if (kartEntity != null) {
                if (!RaceManager.isSpecificKartType(kartEntity, KartType.DisplayKart)) {
                    Location location = kartEntity.getLocation();
                    int locationY = (int) ((location.getY() - locationYOffset) * 32.0D);
                    byte yaw = (byte) (location.getYaw() * 256 / 360.0F);

                    ReflectionUtil.setFieldValue(
                            ReflectionUtil.field_PacketPlayOutSpawnEntity_LocationY, msg, locationY);
                    ReflectionUtil.setFieldValue(
                            ReflectionUtil.field_PacketPlayOutSpawnEntity_LocationYaw, msg, yaw);
                }
            }

            super.write(ctx, msg, promise);

        } /*else if (msg.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityVelocity")) {
            System.out.println("PacketPlayOutEntityVelocity");
            super.write(ctx, msg, promise);
        }*/ else {
            super.write(ctx, msg, promise);
        }
    }
}
