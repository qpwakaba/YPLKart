package com.github.erozabesu.yplkart.utils;

import io.netty.channel.Channel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.object.Character;

public class PacketUtil extends ReflectionUtil {
    private static HashMap<UUID, Object> playerConnection = new HashMap<UUID, Object>();
    private static HashMap<UUID, Object> networkManager = new HashMap<UUID, Object>();
    private static HashMap<UUID, Channel> channel = new HashMap<UUID, Channel>();

    private static Object enumTitleAction_PerformTitle = nmsEnumTitleAction.getEnumConstants()[0];
    private static Object enumTitleAction_PerformSubTitle = nmsEnumTitleAction.getEnumConstants()[1];
    private static Object enumClientCommand_PerformRespawn = nmsEnumClientCommand.getEnumConstants()[0];

    /*
     * プレイヤーがログアウトした場合は登録されているデータを初期化します
     * 再ログイン後はCraftEntityが内部的に別のものに変わるため前回ログイン時のデータは流用できません
     */
    public static void removeData(UUID id) {
        playerConnection.remove(id);
        networkManager.remove(id);
        channel.remove(id);
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
            Object disguise = getCraftEntityFromClassName(player.getWorld(), character.getNmsClass().getSimpleName());
            Location loc = player.getLocation();

            nmsEntity_setLocation.invoke(disguise, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            nmsEntity_setEntityID.invoke(disguise, player.getEntityId());
            nmsEntity_setCustomName.invoke(disguise, player.getName());
            nmsEntity_setCustomNameVisible.invoke(disguise, true);

            Object entitydestroypacket = getEntityDestroyPacket(player.getEntityId());
            Object spawnentitypacket = getSpawnEntityLivingPacket(disguise);
            Object attachentitypacket = null;
            if (player.getVehicle() != null)
                attachentitypacket = getAttachEntityPacket(disguise, getCraftEntity(player.getVehicle()));
            Object handpacket = player.getItemInHand() == null ? null : getEquipmentPacket(player, 0, player.getItemInHand());
            Object helmetpacket = player.getEquipment().getHelmet() == null ? null : getEquipmentPacket(player, 4, player
                    .getEquipment().getHelmet());
            Object chectpacket = player.getEquipment().getChestplate() == null ? null : getEquipmentPacket(player, 3, player
                    .getEquipment().getChestplate());
            Object leggingspacket = player.getEquipment().getLeggings() == null ? null : getEquipmentPacket(player, 2, player
                    .getEquipment().getLeggings());
            Object bootspacket = player.getEquipment().getBoots() == null ? null : getEquipmentPacket(player, 1, player.getEquipment()
                    .getBoots());

            if (target == null) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.getUniqueId() == player.getUniqueId())
                        continue;
                    if (!other.getWorld().getName().equalsIgnoreCase(player.getWorld().getName()))
                        continue;
                    sendPacket(other, entitydestroypacket);
                    sendPacket(other, spawnentitypacket);
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
            } else {
                sendPacket(target, entitydestroypacket);
                sendPacket(target, spawnentitypacket);
                if (attachentitypacket != null)
                    sendPacket(target, attachentitypacket);
                if (handpacket != null)
                    sendPacket(target, handpacket);
                if (helmetpacket != null)
                    sendPacket(target, helmetpacket);
                if (chectpacket != null)
                    sendPacket(target, chectpacket);
                if (leggingspacket != null)
                    sendPacket(target, leggingspacket);
                if (bootspacket != null)
                    sendPacket(target, bootspacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public static void returnPlayer(final Player p) {
        try {
            Object craftentity = getCraftEntity(p);

            Object entitydestroypacket = getEntityDestroyPacket(p.getEntityId());
            Object spawnnamedentitypacket = getSpawnNamedEntityPacket(craftentity);
            Object attachentitypacket = null;
            if (p.getVehicle() != null)
                attachentitypacket = getAttachEntityPacket(craftentity, getCraftEntity(p.getVehicle()));
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

    private static void sendPacket(Player p, Object packet) {
        try {
            nmsPlayerConnection_sendPacket.invoke(getPlayerConnection(p), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    private static Object getPlayerConnection(Player p) throws Exception {
        Object connection = playerConnection.get(p.getUniqueId());

        if (connection == null) {
            Object craftPlayer = ReflectionUtil.getCraftEntity(p);
            Field connectionField = getField(craftPlayer, "playerConnection");

            connection = connectionField.get(craftPlayer);
            playerConnection.put(p.getUniqueId(), connection);
        }

        return connection;
    }

    private static Object getNetworkManager(Player p) throws Exception {
        Object playerconnection = getPlayerConnection(p);
        Object network = networkManager.get(p.getUniqueId());

        if (network == null) {
            Field networkField = getField(playerconnection, "networkManager");
            network = networkField.get(playerconnection);
            networkManager.put(p.getUniqueId(), network);
        }

        return network;
    }

    public static Channel getChannel(Player p) throws Exception {
        Object network = getNetworkManager(p);
        Channel c = channel.get(p.getUniqueId());

        if (c == null) {
            String version = getBukkitVersion();
            Field channelField = null;

            if (version.equalsIgnoreCase("v1_8_R1")) {
                channelField = ReflectionUtil.getField(network, "i");
            } else if (version.equalsIgnoreCase("v1_8_R2")) {
                channelField = ReflectionUtil.getField(network, "k");
            } else if (version.equalsIgnoreCase("v1_8_R3")) {
                channelField = ReflectionUtil.getField(network, "channel");
            }

            c = (Channel) channelField.get(network);
            channel.put(p.getUniqueId(), c);
        }

        return c;
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

    public static Object getEntityDestroyPacket(int id) throws Exception {
        return constructor_nmsPacketPlayOutEntityDestroy.newInstance(new int[] { id });
    }

    private static Object getSpawnEntityLivingPacket(Object craftentity) throws Exception {
        return constructor_nmsPacketPlayOutSpawnEntityLiving.newInstance(craftentity);
    }

    private static Object getAttachEntityPacket(Object passenger, Object vehicle) throws Exception {
        return constructor_nmsPacketPlayOutAttachEntity.newInstance(0, passenger, vehicle);
    }

    private static Object getSpawnNamedEntityPacket(Object craftplayer) throws Exception {
        return constructor_nmsPacketPlayOutNamedEntitySpawn.newInstance(craftplayer);
    }
}