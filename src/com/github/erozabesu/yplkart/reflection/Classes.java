package com.github.erozabesu.yplkart.reflection;

import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class Classes extends ReflectionUtil{

    //〓 Nms 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Class<?> nmsAxisAlignedBB = getNMSClass("AxisAlignedBB");
    public static Class<?> nmsBlockPosition = getNMSClass("BlockPosition");
    public static Class<?> nmsDamageSource = getNMSClass("DamageSource");
    public static Class<?> nmsMathHelper = getNMSClass("MathHelper");
    public static Class<?> nmsVector3f = getNMSClass("Vector3f");
    public static Class<?> nmsWorld = getNMSClass("World");
    public static Class<?> nmsBlock = getNMSClass("Block");
    public static Class<?> nmsMaterial = getNMSClass("Material");
    public static Class<?> nmsEntity = getNMSClass("Entity");
    public static Class<?> nmsEntityHuman = getNMSClass("EntityHuman");
    public static Class<?> nmsEntityPlayer = getNMSClass("EntityPlayer");
    public static Class<?> nmsEntityLiving = getNMSClass("EntityLiving");
    public static Class<?> nmsEntityArmorStand = getNMSClass("EntityArmorStand");
    public static Class<?> nmsItemStack = getNMSClass("ItemStack");
    public static Class<?> nmsPacket = getNMSClass("Packet");
    public static Class<?> nmsWatchableObject = getNMSClass("DataWatcher$WatchableObject");
    public static Class<?> nmsPlayerConnection = getNMSClass("PlayerConnection");
    public static Class<?> nmsNetworkManager = getNMSClass("NetworkManager");
    public static Class<?> nmsIChatBaseComponent = getNMSClass("IChatBaseComponent");
    public static Class<?> nmsChatSerializer = null;
    public static Class<?> nmsEnumClientCommand = null;
    public static Class<?> nmsEnumParticle = getNMSClass("EnumParticle");
    public static Class<?> nmsEnumTitleAction = null;
    public static Class<?> nmsPacketPlayInSteerVehicle = getNMSClass("PacketPlayInSteerVehicle");
    public static Class<?> nmsPacketPlayInUseEntity = getNMSClass("PacketPlayInUseEntity");
    public static Class<?> nmsPacketPlayOutAttachEntity = getNMSClass("PacketPlayOutAttachEntity");
    public static Class<?> nmsPacketPlayOutEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy");
    public static Class<?> nmsPacketPlayOutEntityEquipment = getNMSClass("PacketPlayOutEntityEquipment");
    public static Class<?> nmsPacketPlayOutEntityLook = null;
    public static Class<?> nmsPacketPlayOutEntityMetadata = getNMSClass("PacketPlayOutEntityMetadata");
    public static Class<?> nmsPacketPlayOutEntityTeleport = getNMSClass("PacketPlayOutEntityTeleport");
    public static Class<?> nmsPacketPlayOutNamedEntitySpawn = getNMSClass("PacketPlayOutNamedEntitySpawn");
    public static Class<?> nmsPacketPlayOutRelEntityMoveLook = null;
    public static Class<?> nmsPacketPlayOutSpawnEntity = getNMSClass("PacketPlayOutSpawnEntity");
    public static Class<?> nmsPacketPlayOutSpawnEntityLiving = getNMSClass("PacketPlayOutSpawnEntityLiving");
    public static Class<?> nmsPacketPlayOutTitle = getNMSClass("PacketPlayOutTitle");
    public static Class<?> nmsPacketPlayOutWorldParticles = getNMSClass("PacketPlayOutWorldParticles");
    public static Class<?> nmsPacketPlayInClientCommand = getNMSClass("PacketPlayInClientCommand");

    //XXX: CraftBukkit Unstable
    static {
        if (getBukkitVersion().equalsIgnoreCase("v1_8_R1")) {
            Classes.nmsChatSerializer = getNMSClass("ChatSerializer");
            Classes.nmsEnumTitleAction = getNMSClass("EnumTitleAction");
            Classes.nmsEnumClientCommand = getNMSClass("EnumClientCommand");
            Classes.nmsPacketPlayOutEntityLook = getNMSClass("PacketPlayOutEntityLook");
            Classes.nmsPacketPlayOutRelEntityMoveLook = getNMSClass("PacketPlayOutRelEntityMoveLook");
        } else {
            Classes.nmsChatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
            Classes.nmsEnumTitleAction = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            Classes.nmsEnumClientCommand = getNMSClass("PacketPlayInClientCommand$EnumClientCommand");
            Classes.nmsPacketPlayOutEntityLook = getNMSClass("PacketPlayOutEntity$PacketPlayOutEntityLook");
            Classes.nmsPacketPlayOutRelEntityMoveLook = getNMSClass("PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook");
        }
    }

    //〓 Craft 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Class<?> craftWorld = getCraftClass("CraftWorld");
    public static Class<?> craftBlock = getCraftClass("block.CraftBlock");
    public static Class<?> craftEntity = getCraftClass("entity.CraftEntity");
    public static Class<?> craftItemStack = getCraftClass("inventory.CraftItemStack");

    //〓 Ypl 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Class<?> yplCustomKart = getYPLKartClass("CustomArmorStand");
}
