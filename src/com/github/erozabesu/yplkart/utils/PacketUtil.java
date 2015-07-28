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
import com.github.erozabesu.yplkart.object.Character;

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
    public static void disguise(Player player, Player target, Character character) {
        if (character == null) {
            return;
        }
        if (character.getNmsClass().getSimpleName().equalsIgnoreCase("EntityHuman")) {
            returnPlayer(player);
            return;
        }
        try {
            Object craftEntity = getCraftEntityFromClassName(player.getWorld(), character.getNmsClass().getSimpleName());

            Object entitydestroypacket = getEntityDestroyPacket(player.getEntityId());
            Object spawnentitypacket = getDisguisePacket(player, character);
            Object attachentitypacket = null;
            if (player.getVehicle() != null) {
                attachentitypacket = getAttachEntityPacket(craftEntity, getCraftEntity(player.getVehicle()));
            }

            Object handpacket = getEquipmentPacket(player, 0, new ItemStack(Material.AIR));
            Object helmetpacket = getEquipmentPacket(player, 4, new ItemStack(Material.AIR));
            Object chectpacket = getEquipmentPacket(player, 3, new ItemStack(Material.AIR));
            Object leggingspacket = getEquipmentPacket(player, 2, new ItemStack(Material.AIR));
            Object bootspacket = getEquipmentPacket(player, 1, new ItemStack(Material.AIR));

            if (target == null) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.getUniqueId() == player.getUniqueId())
                        continue;
                    if (!other.getWorld().getName().equalsIgnoreCase(player.getWorld().getName()))
                        continue;
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
                sendPacket(target, entitydestroypacket);
                sendPacket(target, spawnentitypacket);
                if (attachentitypacket != null) {
                    sendPacket(target, attachentitypacket);
                }
                sendPacket(target, handpacket);
                sendPacket(target, helmetpacket);
                sendPacket(target, chectpacket);
                sendPacket(target, leggingspacket);
                sendPacket(target, bootspacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void returnPlayer(final Player p) {
        try {
            Object craftentity = getCraftEntity(p);

            Object entitydestroypacket = getEntityDestroyPacket(p.getEntityId());
            Object spawnnamedentitypacket = getSpawnNamedEntityPacket(craftentity);
            Object attachentitypacket = null;
            if (p.getVehicle() != null) {
                attachentitypacket = getAttachEntityPacket(craftentity, getCraftEntity(p.getVehicle()));
            }
            Object handpacket = p.getItemInHand() == null ? null : getEquipmentPacket(p, 0, p.getItemInHand());
            Object helmetpacket = p.getEquipment().getHelmet() == null ? null : getEquipmentPacket(p, 4, p
                    .getEquipment().getHelmet());
            Object chectpacket = p.getEquipment().getChestplate() == null ? null : getEquipmentPacket(p, 3, p
                    .getEquipment().getChestplate());
            Object leggingspacket = p.getEquipment().getLeggings() == null ? null : getEquipmentPacket(p, 2, p
                    .getEquipment().getLeggings());
            Object bootspacket = p.getEquipment().getBoots() == null ? null : getEquipmentPacket(p, 1, p.getEquipment()
                    .getBoots());

            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.getUniqueId() == p.getUniqueId())
                    continue;
                if (!other.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))
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

    //〓 Get Packet 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Object getDisguisePacket(Player p, Character character) throws Exception {
        Object craftentity = getCraftEntityFromClassName(
                p.getWorld(), character.getNmsClass().getSimpleName());
        Location loc = p.getLocation();

        nmsEntity_setLocation.invoke(craftentity, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        nmsEntity_setEntityID.invoke(craftentity, p.getEntityId());
        nmsEntity_setCustomName.invoke(craftentity, p.getName());
        nmsEntity_setCustomNameVisible.invoke(craftentity, true);

        return getSpawnEntityLivingPacket(craftentity);
    }

    // itemslot: 0-hand / 4-head / 3-chest / 2-leggings / 1-boots
    private static Object getEquipmentPacket(Player p, int itemslot, ItemStack equipment) throws Exception {
        return constructor_nmsPacketPlayOutEntityEquipment.newInstance(p.getEntityId(), itemslot,
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

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    private static void sendPacket(Player p, Object packet) {
        try {
            nmsPlayerConnection_sendPacket.invoke(getPlayerConnection(p), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
