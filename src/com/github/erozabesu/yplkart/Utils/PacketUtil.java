package com.github.erozabesu.yplkart.Utils;

import io.netty.channel.Channel;

import java.lang.reflect.Constructor;
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
import com.github.erozabesu.yplkart.Enum.EnumCharacter;

public class PacketUtil extends ReflectionUtil {
    private static HashMap<UUID, Object> playerConnection = new HashMap<UUID, Object>();
    private static HashMap<UUID, Object> networkManager = new HashMap<UUID, Object>();
    private static HashMap<UUID, Channel> channel = new HashMap<UUID, Channel>();

    private static Class<?> Entity = getBukkitClass("Entity");
    private static Class<?> EntityHuman = getBukkitClass("EntityHuman");
    private static Class<?> EntityLiving = getBukkitClass("EntityLiving");
    private static Class<?> ItemStack = getBukkitClass("ItemStack");

    private static Class<?> Packet = getBukkitClass("Packet");
    private static Class<?> PlayerConnection = getBukkitClass("PlayerConnection");

    private static Class<?> IChatBaseComponent = getBukkitClass("IChatBaseComponent");
    private static Class<?> ChatSerializer;
    private static Class<?> EnumTitleAction;
    private static Class<?> EnumClientCommand;

    private static Class<?> PacketPlayOutAttachEntity = getBukkitClass("PacketPlayOutAttachEntity");
    private static Constructor<?> Constructor_PacketPlayOutAttachEntity;
    private static Class<?> PacketPlayOutEntityDestroy = getBukkitClass("PacketPlayOutEntityDestroy");
    private static Constructor<?> Constructor_PacketPlayOutEntityDestroy;
    private static Class<?> PacketPlayOutEntityEquipment = getBukkitClass("PacketPlayOutEntityEquipment");
    private static Constructor<?> Constructor_PacketPlayOutEntityEquipment;
    private static Class<?> PacketPlayOutNamedEntitySpawn = getBukkitClass("PacketPlayOutNamedEntitySpawn");
    private static Constructor<?> Constructor_PacketPlayOutNamedEntitySpawn;
    private static Class<?> PacketPlayOutSpawnEntityLiving = getBukkitClass("PacketPlayOutSpawnEntityLiving");
    private static Constructor<?> Constructor_PacketPlayOutSpawnEntityLiving;
    private static Class<?> PacketPlayOutTitle = getBukkitClass("PacketPlayOutTitle");
    private static Constructor<?> Constructor_PacketPlayOutTitle;
    private static Constructor<?> Constructor_PacketPlayOutTitle_Length;
    private static Class<?> PacketPlayInClientCommand = getBukkitClass("PacketPlayInClientCommand");
    private static Constructor<?> Constructor_PacketPlayInClientCommand;

    private static Object EnumTitleAction_PerformTitle;
    private static Object EnumTitleAction_PerformSubTitle;
    private static Object EnumClientCommand_PerformRespawn;

    private static Method PlayerConnection_sendPacket;
    private static Method Entity_setEntityID;
    private static Method Entity_setLocation;
    private static Method Entity_setCustomName;
    private static Method Entity_setCustomNameVisible;
    private static Method static_ChatSerializer_buildTitle;

    public PacketUtil() {
        try {
            if (getBukkitVersion().equalsIgnoreCase("v1_8_R1")) {
                ChatSerializer = getBukkitClass("ChatSerializer");
                EnumTitleAction = getBukkitClass("EnumTitleAction");
                EnumClientCommand = getBukkitClass("EnumClientCommand");
            } else {
                ChatSerializer = getBukkitClass("IChatBaseComponent$ChatSerializer");
                EnumTitleAction = getBukkitClass("PacketPlayOutTitle$EnumTitleAction");
                EnumClientCommand = getBukkitClass("PacketPlayInClientCommand$EnumClientCommand");
            }

            Constructor_PacketPlayOutAttachEntity = PacketPlayOutAttachEntity.getConstructor(int.class, Entity, Entity);
            Constructor_PacketPlayOutEntityDestroy = PacketPlayOutEntityDestroy.getConstructor(int[].class);
            Constructor_PacketPlayOutEntityEquipment = PacketPlayOutEntityEquipment.getConstructor(int.class, int.class, ItemStack);
            Constructor_PacketPlayOutNamedEntitySpawn = PacketPlayOutNamedEntitySpawn.getConstructor(EntityHuman);
            Constructor_PacketPlayOutSpawnEntityLiving = PacketPlayOutSpawnEntityLiving.getConstructor(EntityLiving);
            Constructor_PacketPlayOutTitle = PacketPlayOutTitle.getConstructor(EnumTitleAction, IChatBaseComponent);
            Constructor_PacketPlayOutTitle_Length = PacketPlayOutTitle.getConstructor(int.class, int.class, int.class);
            Constructor_PacketPlayInClientCommand = PacketPlayInClientCommand.getConstructor(EnumClientCommand);

            EnumTitleAction_PerformTitle = EnumTitleAction.getEnumConstants()[0];
            EnumTitleAction_PerformSubTitle = EnumTitleAction.getEnumConstants()[1];
            EnumClientCommand_PerformRespawn = EnumClientCommand.getEnumConstants()[0];

            PlayerConnection_sendPacket = PlayerConnection.getMethod("sendPacket", Packet);
            Entity_setEntityID = Entity.getMethod("d", int.class);
            Entity_setLocation = Entity.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            Entity_setCustomName = Entity.getMethod("setCustomName", String.class);
            Entity_setCustomNameVisible = Entity.getMethod("setCustomNameVisible", boolean.class);
            static_ChatSerializer_buildTitle = ChatSerializer.getMethod("a", String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public static void disguise(Player p, Player target, EnumCharacter job) {
        if (job == null) {
            return;
        }
        if (job.getCraftClassName().equalsIgnoreCase(EnumCharacter.Human.getCraftClassName())) {
            returnPlayer(p);
            return;
        }
        try {
            Object disguise = getCraftEntityFromClassName(p.getWorld(), job.getCraftClassName());
            Location loc = p.getLocation();

            Entity_setLocation.invoke(disguise, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            Entity_setEntityID.invoke(disguise, p.getEntityId());
            Entity_setCustomName.invoke(disguise, p.getName());
            Entity_setCustomNameVisible.invoke(disguise, true);

            Object entitydestroypacket = getEntityDestroyPacket(p.getEntityId());
            Object spawnentitypacket = getSpawnEntityLivingPacket(disguise);
            Object attachentitypacket = null;
            if (p.getVehicle() != null)
                attachentitypacket = getAttachEntityPacket(disguise, getCraftEntity(p.getVehicle()));
            Object handpacket = p.getItemInHand() == null ? null : getEquipmentPacket(p, 0, p.getItemInHand());
            Object helmetpacket = p.getEquipment().getHelmet() == null ? null : getEquipmentPacket(p, 4, p
                    .getEquipment().getHelmet());
            Object chectpacket = p.getEquipment().getChestplate() == null ? null : getEquipmentPacket(p, 3, p
                    .getEquipment().getChestplate());
            Object leggingspacket = p.getEquipment().getLeggings() == null ? null : getEquipmentPacket(p, 2, p
                    .getEquipment().getLeggings());
            Object bootspacket = p.getEquipment().getBoots() == null ? null : getEquipmentPacket(p, 1, p.getEquipment()
                    .getBoots());

            if (target == null) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.getUniqueId() == p.getUniqueId())
                        continue;
                    if (!other.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))
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

    public static Object getDisguisePacket(Player p, EnumCharacter job) throws Exception {
        Object craftentity = getCraftEntityFromClassName(p.getWorld(), job.getCraftClassName());
        Location loc = p.getLocation();

        Entity_setLocation.invoke(craftentity, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        Entity_setEntityID.invoke(craftentity, p.getEntityId());
        Entity_setCustomName.invoke(craftentity, p.getName());
        Entity_setCustomNameVisible.invoke(craftentity, true);

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
            PlayerConnection_sendPacket.invoke(getPlayerConnection(p), packet);
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
        return Constructor_PacketPlayOutEntityEquipment.newInstance(p.getEntityId(), itemslot,
                getCraftItemStack(equipment));
    }

    private static Object getTitlePacket(String text, boolean issubtitle) throws Exception {
        ChatColor color = Util.getChatColorFromText(text);
        text = ChatColor.stripColor(text);
        Object title = static_ChatSerializer_buildTitle.invoke(
                null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

        return Constructor_PacketPlayOutTitle.newInstance(issubtitle ?
                EnumTitleAction_PerformSubTitle : EnumTitleAction_PerformTitle, title);
    }

    private static Object getTitleLengthPacket(int fadein, int length, int fadeout) throws Exception {
        return Constructor_PacketPlayOutTitle_Length.newInstance(fadein, length, fadeout);
    }

    private static Object getClientCommandPacket() throws Exception {
        return Constructor_PacketPlayInClientCommand.newInstance(EnumClientCommand_PerformRespawn);
    }

    public static Object getEntityDestroyPacket(int id) throws Exception {
        return Constructor_PacketPlayOutEntityDestroy.newInstance(new int[] { id });
    }

    private static Object getSpawnEntityLivingPacket(Object craftentity) throws Exception {
        return Constructor_PacketPlayOutSpawnEntityLiving.newInstance(craftentity);
    }

    private static Object getAttachEntityPacket(Object passenger, Object vehicle) throws Exception {
        return Constructor_PacketPlayOutAttachEntity.newInstance(0, passenger, vehicle);
    }

    private static Object getSpawnNamedEntityPacket(Object craftplayer) throws Exception {
        return Constructor_PacketPlayOutNamedEntitySpawn.newInstance(craftplayer);
    }
}
