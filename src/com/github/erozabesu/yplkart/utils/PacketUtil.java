package com.github.erozabesu.yplkart.utils;

import io.netty.channel.Channel;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.reflection.Constructors;
import com.github.erozabesu.yplkart.reflection.Fields;
import com.github.erozabesu.yplkart.reflection.Methods;
import com.github.erozabesu.yplkart.reflection.Objects;

public class PacketUtil extends ReflectionUtil {

    /**
     * PlayerとPlayerConnectionを格納するハッシュマップ
     * Playerの再ログインと共に削除されるため、UUIDではなくPlayerを利用する
     */
    private static HashMap<Player, Object> playerConnectionMap = new HashMap<Player, Object>();

    /**
     * PlayerとNetworkManagerを格納するハッシュマップ
     * Playerの再ログインと共に削除されるため、UUIDではなくPlayerを利用する
     */
    private static HashMap<Player, Object> networkManagerMap = new HashMap<Player, Object>();

    /**
     * PlayerとChannelを格納するハッシュマップ
     * Playerの再ログインと共に削除されるため、UUIDではなくPlayerを利用する
     */
    private static HashMap<Player, Channel> channelMap = new HashMap<Player, Channel>();

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static HashMap<Player, Object> getPlayerConnectionMap() {
        return playerConnectionMap;
    }

    public static HashMap<Player, Object> getNetworkManagerMap() {
        return networkManagerMap;
    }

    public static HashMap<Player, Channel> getChannelMap() {
        return channelMap;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static void putPlayerConnection(Player player, Object playerConnection) {
        getPlayerConnectionMap().put(player, playerConnection);
    }

    public static void putNetworkManager(Player player, Object networkManager) {
        getNetworkManagerMap().put(player, networkManager);
    }

    public static void putChannel(Player player, Channel channel) {
        getChannelMap().put(player, channel);
    }

    public static void removeAllData(Player player) {
        playerConnectionMap.remove(player);
        networkManagerMap.remove(player);
        channelMap.remove(player);
    }

    //〓 Send Packet 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /*
     * job.getCraftClassName()で得られるエンティティの姿にpを変身させます
     * targetプレイヤーに向けパケットを送信します
     * targetがnullの場合全プレイヤーに送信します
     */
    /**
     * 引数disguisePlayerの姿を引数nmsEntityClassに偽装するパケットを引数adressに送信する
     * @param adress パケット送信対象
     * @param disguisePlayer 外見を偽装するプレイヤー
     * @param nmsEntityClass 偽装に利用するNmsEntityクラス
     */
    public static void disguiseLivingEntity(Player adress, Player disguisePlayer, Class<?> nmsEntityClass) {
        if (nmsEntityClass == null) {
            return;
        }

        Racer racer = RaceManager.getRacer(disguisePlayer);

        //既に他のNmsEntityに外見を偽装している場合は仮想NmsEntityをデスポーンする
        if (racer.getDisguisedNmsEntity() != null) {
            sendEntityDestroyPacket(null
                    , (Entity) invoke(Methods.nmsEntity_getBukkitEntity, racer.getDisguisedNmsEntity()));
            racer.setDisguisedNmsEntity(null);
        }

        //偽装するNmsEntityがNmsEntityHumanの場合別の処理を呼び出しreturn
        String nmsEntityClassName = nmsEntityClass.getSimpleName();
        if (nmsEntityClassName.equalsIgnoreCase("EntityHuman")) {
            returnOriginalPlayer(disguisePlayer);
            return;
        }

        Object craftEntity = Util.getNewCraftEntityFromClass(disguisePlayer.getWorld(), nmsEntityClass);

        //スポーンさせた仮想NmsEntityを格納
        racer.setDisguisedNmsEntity(craftEntity);

        Object entitydestroypacket = getEntityDestroyPacket(disguisePlayer.getEntityId());
        Object spawnentitypacket = getDisguiseLivingEntityPacket(disguisePlayer, nmsEntityClass);
        Object attachentitypacket = null;
        if (disguisePlayer.getVehicle() != null) {
            attachentitypacket = getAttachEntityPacket(craftEntity, Util.getCraftEntity(disguisePlayer.getVehicle()));
        }

        Object handpacket = getEquipmentPacket(disguisePlayer, 0, new ItemStack(Material.AIR));
        Object helmetpacket = getEquipmentPacket(disguisePlayer, 4, new ItemStack(Material.AIR));
        Object chectpacket = getEquipmentPacket(disguisePlayer, 3, new ItemStack(Material.AIR));
        Object leggingspacket = getEquipmentPacket(disguisePlayer, 2, new ItemStack(Material.AIR));
        Object bootspacket = getEquipmentPacket(disguisePlayer, 1, new ItemStack(Material.AIR));

        if (adress == null) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.getUniqueId() == disguisePlayer.getUniqueId()) {
                    continue;
                }
                if (!other.getWorld().getName().equalsIgnoreCase(disguisePlayer.getWorld().getName())) {
                    continue;
                }

                sendPacket(other, entitydestroypacket);
                sendPacket(other, spawnentitypacket);
                if (attachentitypacket != null) {
                    sendPacket(other, attachentitypacket);
                }
                sendPacket(other, handpacket);
                sendPacket(other, helmetpacket);
                sendPacket(other, chectpacket);
                sendPacket(other, leggingspacket);
                sendPacket(other, bootspacket);
            }
        } else {
            sendPacket(adress, entitydestroypacket);
            sendPacket(adress, spawnentitypacket);
            if (attachentitypacket != null) {
                sendPacket(adress, attachentitypacket);
            }
            sendPacket(adress, handpacket);
            sendPacket(adress, helmetpacket);
            sendPacket(adress, chectpacket);
            sendPacket(adress, leggingspacket);
            sendPacket(adress, bootspacket);
        }
    }

    private static void returnOriginalPlayer(Player player) {
        Object craftentity = Util.getCraftEntity(player);

        Object entitydestroypacket = getEntityDestroyPacket(player.getEntityId());
        Object spawnnamedentitypacket = getSpawnNamedEntityPacket(craftentity);
        Object attachentitypacket = null;
        if (player.getVehicle() != null) {
            attachentitypacket = getAttachEntityPacket(craftentity, Util.getCraftEntity(player.getVehicle()));
        }
        Object handpacket = player.getItemInHand() == null ? null : getEquipmentPacket(player, 0, player.getItemInHand());
        Object helmetpacket = player.getEquipment().getHelmet() == null ? null : getEquipmentPacket(player, 4, player
                .getEquipment().getHelmet());
        Object chectpacket = player.getEquipment().getChestplate() == null ? null : getEquipmentPacket(player, 3, player
                .getEquipment().getChestplate());
        Object leggingspacket = player.getEquipment().getLeggings() == null ? null : getEquipmentPacket(player, 2, player
                .getEquipment().getLeggings());
        Object bootspacket = player.getEquipment().getBoots() == null ? null : getEquipmentPacket(player, 1, player.getEquipment()
                .getBoots());

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.getUniqueId() == player.getUniqueId())
                continue;
            if (!other.getWorld().getName().equalsIgnoreCase(player.getWorld().getName()))
                continue;
            sendPacket(other, entitydestroypacket);
            sendPacket(other, spawnnamedentitypacket);
            if (attachentitypacket != null)
                sendPacket(other, attachentitypacket);
            if (handpacket != null)
                sendPacket(other, handpacket);
            if (helmetpacket != null)
                sendPacket(other, helmetpacket);
            if (chectpacket != null)
                sendPacket(other, chectpacket);
            if (leggingspacket != null)
                sendPacket(other, leggingspacket);
            if (bootspacket != null)
                sendPacket(other, bootspacket);
        }
    }

    public static void sendTitle(Player p, String text, int fadein, int length, int fadeout, boolean issubtitle) {
        Object titlesendpacket = getTitlePacket(text, issubtitle);
        Object titlelengthpacket = getTitleLengthPacket(fadein, length, fadeout);

        sendPacket(p, titlesendpacket);
        sendPacket(p, titlelengthpacket);
    }

    /**
     * PlayerDeathEventで呼び出すと、引数playerのリスポーンウィンドウをスキップし、強制的にリスポーンさせる
     * @param player リスポーンウィンドウをスキップするプレイヤー
     */
    public static void skipRespawnScreen(Player player) {
        Object playerConnection = getPlayerConnection(player);
        invoke(Methods.nmsPlayerConnection_skipRespawnWindow, playerConnection);
    }

    /**
     * 引数entityがデスポーンするパケットを引数targetに送信する
     * @param target パケットを送信するプレイヤー
     * @param entity デスポーンさせるエンティティ
     */
    public static void sendEntityDestroyPacket(Player target, Entity entity) {
        Object packet = getEntityDestroyPacket(entity.getEntityId());
        if (target == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendPacket(player, packet);
            }
        } else {
            sendPacket(target, packet);
        }
    }

    /* プレイヤーをテレポート後即エンティティに搭乗させると、
     * サーバーとクライアントで情報の食い違いが発生するため、それを正すためだけの処理
     * クライアント側では搭乗された判定が行われないため、搭乗状態のはずが自由移動できてしまう
     * 移動しても、サーバーでは搭乗状態になっているため、すぐもとの場所に戻される
     * 数チック後にエンティティに搭乗するパケットを再送し食い違いを修正する
     */
    public static void sendOwnAttachEntityPacket(final Player player) {
        if (player.getVehicle() == null) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
            public void run() {
                Object attachentitypacket = getAttachEntityPacket(Util.getCraftEntity(player), Util.getCraftEntity(player.getVehicle()));
                sendPacket(player, attachentitypacket);
            }
        }, 5);
    }

    /**
     * 引数entityを引数locationへテレポートさせるパケットを引数targetへ送信する
     * @param target パケットを送信するプレイヤー
     * @param entity テレポートさせるエンティティ
     * @param location テレポートする座標
     */
    public static void sendEntityTeleportPacket(Player target, Entity entity, Location location) {
        Object packet = getEntityTeleportPacket(entity.getEntityId(), location);
        if (target == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendPacket(player, packet);
            }
        } else {
            sendPacket(target, packet);
        }
    }

    public static void sendSpawnEntityPacket(Player target, Object craftEntity, int objectID, int objectData) {
        Object packet = getSpawnEntityPacket(craftEntity, objectID, objectData);
        if (target == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendPacket(player, packet);
            }
        } else {
            sendPacket(target, packet);
        }
    }

    /**
     * エンティティがアイテムを装備するパケットを送信する<br>
     * 引数itemSlotで装備するスロットを指定する<br>
     * 0:手  1:ブーツ  2:レギンス  3:チェスト  4:ヘルメット
     * @param target パケットを送信するプレイヤー
     * @param entityId アイテムを装備するエンティティのエンティティID
     * @param itemSlot アイテムを装備するスロット
     * @param equipItemStack 装備するアイテムのNmsItemStack
     */
    public static void sendEntityEquipmentPacket(Player target, int entityId, int itemSlot, Object equipItemStack) {
        Object packet = getEntityEquipmentPacket(entityId, itemSlot, equipItemStack);
        if (target == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendPacket(player, packet);
            }
        } else {
            sendPacket(target, packet);
        }
    }

    /**
     * 引数targetにパーティクル再生パケットを送信する
     * @param target パケットを送信するプレイヤー。nullの場合同じワールドの全プレイヤーに送信する
     * @param particle Particleクラスの要素
     * @param isLongDistance パーティクルの描画距離を広いモードで再生するかどうか
     * @param location 再生座標
     * @param offsetX X方向のオフセット。与えられた数値以内の乱数を生成し再生する
     * @param offsetY Y方向のオフセット。与えられた数値以内の乱数を生成し再生する
     * @param offsetZ Z方向のオフセット。与えられた数値以内の乱数を生成し再生する
     * @param speed パーティクルの移動速度？
     * @param count 再生回数
     * @param particleData 基本的にnull。ブロックのID等が影響する場合に用いる。ITEMCRACK:配列数2:ID,Data / BLOCKCRACK:配列数1:ID+(Data<<12) / BLOCKDUST:配列数1:ID+(Data<<12)
     */
    public static void sendParticlePacket(Player target, Particle particle, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count, int[] particleData) {
        World world = location.getWorld();
        Object packet = getParticlePacket(particle, location, offsetX, offsetY, offsetZ, speed, count, particleData);
        if (target == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getLocation().getWorld().equals(world)) {
                    sendPacket(player, packet);
                }
            }
        } else {
            sendPacket(target, packet);
        }
    }

    //〓 Get Packet 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数entityと同様の情報を持った、引数nmsEntityClassクラスのエンティティがスポーンするパケットを返す
     * @param entity スポーンさせるエンティティの基となる情報をもつエンティティ
     * @param nmsEntityClass スポーンさせるエンティティのNmsEntityClass
     * @return 引数entityと同様の情報を持った、引数nmsEntityClassクラスのエンティティがスポーンするパケット
     */
    public static Object getDisguiseLivingEntityPacket(Entity entity, Class<?> nmsEntityClass) {
        Object craftEntity = Util.getNewCraftEntityFromClass(entity.getWorld(), nmsEntityClass);
        Location location = entity.getLocation();

        invoke(Methods.nmsEntity_setLocation, craftEntity
                , location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        invoke(Methods.nmsEntity_setEntityID, craftEntity, entity.getEntityId());
        invoke(Methods.nmsEntity_setCustomName, craftEntity, entity.getName());
        invoke(Methods.nmsEntity_setCustomNameVisible, craftEntity, true);

        return getSpawnEntityLivingPacket(craftEntity);
    }

    // itemslot: 0-hand / 4-head / 3-chest / 2-leggings / 1-boots
    private static Object getEquipmentPacket(Entity entity, int itemslot, ItemStack equipment) {
        return newInstance(Constructors.nmsPacketPlayOutEntityEquipment, entity.getEntityId(), itemslot,
                invoke(Methods.static_craftItemStack_asNMSCopy, null, equipment));
    }

    private static Object getTitlePacket(String text, boolean issubtitle) {
        ChatColor color = Util.getChatColorFromText(text);
        text = ChatColor.stripColor(text);

        Object title = invoke(Methods.static_nmsChatSerializer_buildTitle,
                null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

        return newInstance(Constructors.nmsPacketPlayOutTitle, issubtitle ?
                Objects.nmsEnumTitleAction_PerformSubTitle : Objects.nmsEnumTitleAction_PerformTitle, title);
    }

    private static Object getTitleLengthPacket(int fadein, int length, int fadeout) {
        return newInstance(Constructors.nmsPacketPlayOutTitle_Length, fadein, length, fadeout);
    }

    private static Object getClientCommandPacket() {
        return newInstance(Constructors.nmsPacketPlayInClientCommand, Objects.nmsEnumClientCommand_PerformRespawn);
    }

    /**
     * @param entityId デスポーンさせるEntityのEntityID
     * @return 引数entityIdをEntityIDとして持つEntityをデスポーンさせるパケット
     */
    public static Object getEntityDestroyPacket(int entityId){
        return newInstance(Constructors.nmsPacketPlayOutEntityDestroy, new int[] { entityId });
    }

    /**
     * 引数craftPlayerをスポーンさせるパケットを返す<br>
     * HumanEntityのみ利用可能
     * @param craftPlayer スポーンさせるHumanEntity
     * @return 引数craftPlayerをスポーンさせるパケット
     */
    private static Object getSpawnNamedEntityPacket(Object craftPlayer) {
        return newInstance(Constructors.nmsPacketPlayOutNamedEntitySpawn, craftPlayer);
    }

    /**
     * 引数craftLivingEntityエンティティをスポーンさせるパケットを返す<br>
     * LivingEntityのみ利用可能
     * @param craftLivingEntity スポーンさせるCraftEntity
     * @return 引数craftLivingEntityをスポーンさせるパケット
     */
    private static Object getSpawnEntityLivingPacket(Object craftLivingEntity) {
        return newInstance(Constructors.nmsPacketPlayOutSpawnEntityLiving, craftLivingEntity);
    }

    /**
     * @param passenger 搭乗させるCraftEntity
     * @param vehicle 乗り物となるCraftEntity
     * @return 引数passengerを引数vehicleに搭乗させるパケット
     */
    private static Object getAttachEntityPacket(Object passenger, Object vehicle) {
        return newInstance(Constructors.nmsPacketPlayOutAttachEntity, 0, passenger, vehicle);
    }

    /**
     * @param entityId テレポートさせるEntityのEntityID
     * @param location テレポートする座標
     * @return 引数entityIdをEntityIDとして持つEntityを引数locationにテレポートするパケット
     */
    public static Object getEntityTeleportPacket(int entityId, Location location) {
        //パケットではdouble型を扱えないため
        //座標は、座標値を32倍した数値をint型に変換し利用する
        int x = (int) (location.getX() * 32.0D);
        int y = (int) (location.getY() * 32.0D);
        int z = (int) (location.getZ() * 32.0D);

        //パケットではfloat型を扱えないため
        //yaw・pitchはbyte型に変換し利用する
        //変換は、byteの最大値256をyaw・pitchの最大値360.0Fで割り、基になったyaw・pitchに掛け合わせる
        byte yaw = (byte) (location.getYaw() * (255.0F / 360.0F));
        byte pitch = (byte) (location.getPitch() * (255.0F / 360.0F));

        return newInstance(Constructors.nmsPacketPlayOutEntityTeleport, entityId, x, y, z, yaw, pitch, false);
    }

    public static Object getSpawnEntityPacket(Object craftEntity, int objectID, int objectData) {
        return newInstance(Constructors.nmsPacketPlayOutSpawnEntity, craftEntity, objectID, objectData);
    }

    /**
     * エンティティがアイテムを装備するパケットを返す
     * @param entityId アイテムを装備するエンティティのエンティティID
     * @param itemSlot アイテムを装備するスロット
     * @param equipItemStack 装備するアイテムのNmsItemStackオブジェクト
     * @return エンティティがアイテムを装備するパケット
     */
    public static Object getEntityEquipmentPacket(int entityId, int itemSlot, Object equipItemStack) {
        return newInstance(Constructors.nmsPacketPlayOutEntityEquipment, entityId, itemSlot, equipItemStack);
    }

    /**
     * パーティクル再生パケットを返す
     * @param particle Particleクラスの要素
     * @param location 再生座標
     * @param offsetX X方向のオフセット。与えられた数値以内の乱数を生成し再生する
     * @param offsetY Y方向のオフセット。与えられた数値以内の乱数を生成し再生する
     * @param offsetZ Z方向のオフセット。与えられた数値以内の乱数を生成し再生する
     * @param speed パーティクルの移動速度？
     * @param count 再生回数
     * @param particleData 基本的にnull。ブロックのID等が影響する場合に用いる。ITEMCRACK:配列数2:ID,Data / BLOCKCRACK:配列数1:ID+(Data<<12) / BLOCKDUST:配列数1:ID+(Data<<12)
     * @return パーティクル再生パケット
     */
    public static Object getParticlePacket(Particle particle, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count, int[] particleData) {
        return newInstance(Constructors.nmsPacketPlayOutWorldParticles
                , particle.getNmsEnumParticle(), particle.isLongDistance(), (float) location.getX(), (float) location.getY()
                , (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, particleData);
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    private static void sendPacket(Player p, Object packet) {
        invoke(Methods.nmsPlayerConnection_sendPacket, getPlayerConnection(p), packet);
    }

    /**
     * 引数networkManagerと一致するNetworkManagerオブジェクトを所有するプレイヤーを返す
     * @param networkManager NetworkManagerオブジェクト
     * @return NetworkManagerオブジェクトが一致したプレイヤー
     */
    public static Player getPlayerByNetworkManager(Object networkManager) {
        for (Player key : getNetworkManagerMap().keySet()) {
            if (getNetworkManagerMap().get(key).equals(networkManager)) {
                return key;
            }
        }
        return null;
    }

    private static Object getPlayerConnection(Player player) {
        Object connection = getPlayerConnectionMap().get(player);

        if (connection == null) {
            Object craftPlayer = Util.getCraftEntity(player);

            connection = getFieldValue(Fields.nmsEntityPlayer_playerConnection, craftPlayer);
            putPlayerConnection(player, connection);
        }

        return connection;
    }

    private static Object getNetworkManager(Player player) {
        Object playerconnection = getPlayerConnection(player);
        Object network = getNetworkManagerMap().get(player);

        if (network == null) {
            network = getFieldValue(Fields.nmsPlayerConnection_networkManager, playerconnection);
            putNetworkManager(player, network);
        }

        return network;
    }

    public static Channel getChannel(Player player) {
        Object network = getNetworkManager(player);
        Channel channel = getChannelMap().get(player);

        if (channel == null) {
            channel = (Channel) getFieldValue(Fields.nmsNetworkManager_channel, network);
            putChannel(player, channel);
        }

        return channel;
    }
}
