package com.github.erozabesu.yplkart.utils;

import io.netty.channel.Channel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.YPLKart;

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

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
    public static void disguiseLivingEntity(Player adress, Entity disguiseEntity, Class<?> nmsEntityClass, double offsetX, double offsetY, double offsetZ) {
        if (nmsEntityClass == null) {
            return;
        }

        String nmsEntityClassName = nmsEntityClass.getSimpleName();
        if (nmsEntityClassName.equalsIgnoreCase("EntityHuman")) {
            if (disguiseEntity instanceof Player) {
                returnOriginalPlayer((Player) disguiseEntity);
            }
            return;
        }

        try {
            Object craftEntity = getNewCraftEntityFromClass(disguiseEntity.getWorld(), nmsEntityClass);

            Object entitydestroypacket = getEntityDestroyPacket(disguiseEntity.getEntityId());
            Object spawnentitypacket = getDisguiseLivingEntityPacket(disguiseEntity, nmsEntityClass
                    , offsetX, offsetY, offsetZ);
            Object attachentitypacket = null;
            if (disguiseEntity.getVehicle() != null) {
                attachentitypacket = getAttachEntityPacket(craftEntity, getCraftEntity(disguiseEntity.getVehicle()));
            }

            Object handpacket = getEquipmentPacket(disguiseEntity, 0, new ItemStack(Material.AIR));
            Object helmetpacket = getEquipmentPacket(disguiseEntity, 4, new ItemStack(Material.AIR));
            Object chectpacket = getEquipmentPacket(disguiseEntity, 3, new ItemStack(Material.AIR));
            Object leggingspacket = getEquipmentPacket(disguiseEntity, 2, new ItemStack(Material.AIR));
            Object bootspacket = getEquipmentPacket(disguiseEntity, 1, new ItemStack(Material.AIR));

            if (adress == null) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.getUniqueId() == disguiseEntity.getUniqueId()) {
                        continue;
                    }
                    if (!other.getWorld().getName().equalsIgnoreCase(disguiseEntity.getWorld().getName())) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void returnOriginalPlayer(Player player) {
        try {
            Object craftentity = getCraftEntity(player);

            Object entitydestroypacket = getEntityDestroyPacket(player.getEntityId());
            Object spawnnamedentitypacket = getSpawnNamedEntityPacket(craftentity);
            Object attachentitypacket = null;
            if (player.getVehicle() != null) {
                attachentitypacket = getAttachEntityPacket(craftentity, getCraftEntity(player.getVehicle()));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendTitle(Player p, String text, int fadein, int length, int fadeout, boolean issubtitle) {
        try {
            Object titlesendpacket = getTitlePacket(text, issubtitle);
            Object titlelengthpacket = getTitleLengthPacket(fadein, length, fadeout);

            sendPacket(p, titlesendpacket);
            sendPacket(p, titlelengthpacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //PlayerDeathEventで呼び出すとリスポーンウィンドウをスキップできます
    public static void skipRespawnScreen(Player p) {
        try {
            Object playerConnection = getPlayerConnection(p);
            for (Method m : playerConnection.getClass().getMethods()) {
                if (m.getName().equalsIgnoreCase("a")) {
                    for (Class<?> c : m.getParameterTypes()) {
                        if (c.getName().contains("PacketPlayInClientCommand")) {
                            m.invoke(playerConnection, getClientCommandPacket());
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 引数entityがデスポーンするパケットを引数targetに送信する
     * @param target パケットを送信するプレイヤー
     * @param entity デスポーンさせるエンティティ
     */
    public static void sendEntityDestroyPacket(Player target, Entity entity) {
        Object packet = null;

        packet = getEntityDestroyPacket(entity.getEntityId());

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
    public static void sendOwnAttachEntityPacket(final Player p) {
        if (p.getVehicle() == null)
            return;
        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
            public void run() {
                try {
                    Object attachentitypacket = getAttachEntityPacket(getCraftEntity(p), getCraftEntity(p.getVehicle()));
                    sendPacket(p, attachentitypacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        Object packet = null;

        packet = getEntityTeleportPacket(entity.getEntityId(), location);

        if (target == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendPacket(player, packet);
            }
        } else {
            sendPacket(target, packet);
        }
    }

    public static void sendSpawnEntityPacket(Player target, Object craftEntity, int objectID, int objectData) {
        Object packet = null;

        packet = getSpawnEntityPacket(craftEntity, objectID, objectData);

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
        Object packet = null;

        packet = getEntityEquipmentPacket(entityId, itemSlot, equipItemStack);

        if (target == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendPacket(player, packet);
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
    public static Object getDisguiseLivingEntityPacket(Entity entity, Class<?> nmsEntityClass, double offsetX, double offsetY, double offsetZ){
        try {
            Object craftEntity = getNewCraftEntityFromClass(entity.getWorld(), nmsEntityClass);
            Location location = entity.getLocation();

            nmsEntity_setLocation.invoke(craftEntity, location.getX() + offsetX, location.getY() + offsetY
                    , location.getZ() + offsetZ, location.getYaw(), location.getPitch());
            nmsEntity_setEntityID.invoke(craftEntity, entity.getEntityId());
            nmsEntity_setCustomName.invoke(craftEntity, entity.getName());
            nmsEntity_setCustomNameVisible.invoke(craftEntity, true);

            return getSpawnEntityLivingPacket(craftEntity);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    // itemslot: 0-hand / 4-head / 3-chest / 2-leggings / 1-boots
    private static Object getEquipmentPacket(Entity entity, int itemslot, ItemStack equipment) throws Exception {
        return constructor_nmsPacketPlayOutEntityEquipment.newInstance(entity.getEntityId(), itemslot,
                getCraftItemStack(equipment));
    }

    private static Object getTitlePacket(String text, boolean issubtitle) throws Exception {
        ChatColor color = Util.getChatColorFromText(text);
        text = ChatColor.stripColor(text);
        Object title = static_nmsChatSerializer_buildTitle.invoke(
                null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

        return constructor_nmsPacketPlayOutTitle.newInstance(issubtitle ?
                enumTitleAction_PerformSubTitle : enumTitleAction_PerformTitle, title);
    }

    private static Object getTitleLengthPacket(int fadein, int length, int fadeout) throws Exception {
        return constructor_nmsPacketPlayOutTitle_Length.newInstance(fadein, length, fadeout);
    }

    private static Object getClientCommandPacket() throws Exception {
        return constructor_nmsPacketPlayInClientCommand.newInstance(enumClientCommand_PerformRespawn);
    }

    /**
     * @param entityId デスポーンさせるEntityのEntityID
     * @return 引数entityIdをEntityIDとして持つEntityをデスポーンさせるパケット
     */
    public static Object getEntityDestroyPacket(int entityId){
        Object packet = null;
        try {
            packet = constructor_nmsPacketPlayOutEntityDestroy.newInstance(new int[] { entityId });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return packet;
    }

    /**
     * 引数craftPlayerをスポーンさせるパケットを返す<br>
     * HumanEntityのみ利用可能
     * @param craftPlayer スポーンさせるHumanEntity
     * @return 引数craftPlayerをスポーンさせるパケット
     */
    private static Object getSpawnNamedEntityPacket(Object craftPlayer) {
        Object packet = null;
        try {
            packet = constructor_nmsPacketPlayOutNamedEntitySpawn.newInstance(craftPlayer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return packet;
    }

    /**
     * 引数craftLivingEntityエンティティをスポーンさせるパケットを返す<br>
     * LivingEntityのみ利用可能
     * @param craftLivingEntity スポーンさせるCraftEntity
     * @return 引数craftLivingEntityをスポーンさせるパケット
     */
    private static Object getSpawnEntityLivingPacket(Object craftLivingEntity) {
        Object packet = null;
        try {
            packet = constructor_nmsPacketPlayOutSpawnEntityLiving.newInstance(craftLivingEntity);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return packet;
    }

    /**
     * @param passenger 搭乗させるCraftEntity
     * @param vehicle 乗り物となるCraftEntity
     * @return 引数passengerを引数vehicleに搭乗させるパケット
     */
    private static Object getAttachEntityPacket(Object passenger, Object vehicle) {
        Object packet = null;
        try {
            packet = constructor_nmsPacketPlayOutAttachEntity.newInstance(0, passenger, vehicle);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return packet;
    }

    /**
     * @param entityId テレポートさせるEntityのEntityID
     * @param location テレポートする座標
     * @return 引数entityIdをEntityIDとして持つEntityを引数locationにテレポートするパケット
     */
    public static Object getEntityTeleportPacket(int entityId, Location location){
        Object packet = null;
        try {
            //パケットではdouble型を扱えない
            //座標は、座標値を32倍した数値をint型に変換し利用する
            int x = (int) (location.getX() * 32.0D);
            int y = (int) (location.getY() * 32.0D);
            int z = (int) (location.getZ() * 32.0D);

            //パケットではfloat型を扱えない
            //yaw・pitchはbyte型に変換し利用する
            //変換は、byteの最大値256をyaw・pitchの最大値360.0Fで割り、基になったyaw・pitchに掛け合わせる
            byte yaw = (byte) (location.getYaw() * (255.0F / 360.0F));
            byte pitch = (byte) (location.getPitch() * (255.0F / 360.0F));

            packet = constructor_nmsPacketPlayOutEntityTeleport
                    .newInstance(entityId, x, y, z, yaw, pitch, false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return packet;
    }

    public static Object getSpawnEntityPacket(Object craftEntity, int objectID, int objectData) {
        Object packet = null;

        try {
            packet = constructor_nmsPacketPlayOutSpawnEntity.newInstance(craftEntity, objectID, objectData);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return packet;
    }

    /**
     * エンティティがアイテムを装備するパケットを返す
     * @param entityId アイテムを装備するエンティティのエンティティID
     * @param itemSlot アイテムを装備するスロット
     * @param equipItemStack 装備するアイテムのNmsItemStackオブジェクト
     * @return エンティティがアイテムを装備するパケット
     */
    public static Object getEntityEquipmentPacket(int entityId, int itemSlot, Object equipItemStack) {
        Object packet = null;

        try {
            packet = constructor_nmsPacketPlayOutEntityEquipment.newInstance(entityId, itemSlot, equipItemStack);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return packet;
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    private static void sendPacket(Player p, Object packet) {
        try {
            nmsPlayerConnection_sendPacket.invoke(getPlayerConnection(p), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static Object getPlayerConnection(Player player) throws Exception {
        Object connection = getPlayerConnectionMap().get(player);

        if (connection == null) {
            Object craftPlayer = ReflectionUtil.getCraftEntity(player);
            Field connectionField = getField(craftPlayer, "playerConnection");

            connection = connectionField.get(craftPlayer);
            putPlayerConnection(player, connection);
        }

        return connection;
    }

    private static Object getNetworkManager(Player player) throws Exception {
        Object playerconnection = getPlayerConnection(player);
        Object network = getNetworkManagerMap().get(player);

        if (network == null) {
            Field networkField = getField(playerconnection, "networkManager");
            network = networkField.get(playerconnection);
            putNetworkManager(player, network);
        }

        return network;
    }

    public static Channel getChannel(Player player) throws Exception {
        Object network = getNetworkManager(player);
        Channel channel = getChannelMap().get(player);

        if (channel == null) {
            String version = getBukkitVersion();
            Field channelField = null;

            if (version.equalsIgnoreCase("v1_8_R1")) {
                channelField = ReflectionUtil.getField(network, "i");
            } else if (version.equalsIgnoreCase("v1_8_R2")) {
                channelField = ReflectionUtil.getField(network, "k");
            } else if (version.equalsIgnoreCase("v1_8_R3")) {
                channelField = ReflectionUtil.getField(network, "channel");
            }

            channel = (Channel) channelField.get(network);
            putChannel(player, channel);
        }

        return channel;
    }
}
