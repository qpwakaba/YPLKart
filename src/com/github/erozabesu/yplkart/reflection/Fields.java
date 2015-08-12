package com.github.erozabesu.yplkart.reflection;

import java.lang.reflect.Field;

import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class Fields extends ReflectionUtil {

    public static Field nmsPacketPlayInSteerVehicle_isUnmount = getField(Classes.nmsPacketPlayInSteerVehicle, "d");
    public static Field nmsPacketPlayOutEntityMetadata_EntityId = getField(Classes.nmsPacketPlayOutEntityMetadata, "a");
    public static Field nmsPacketPlayOutEntityMetadata_WatchableObject = getField(Classes.nmsPacketPlayOutEntityMetadata, "b");
    public static Field nmsPacketPlayOutNamedEntitySpawn_UUID = getField(Classes.nmsPacketPlayOutNamedEntitySpawn, "b");
    public static Field nmsPacketPlayOutEntityEquipment_EntityId = getField(Classes.nmsPacketPlayOutEntityEquipment, "a");
    public static Field nmsPacketPlayOutEntityEquipment_ItemSlot = getField(Classes.nmsPacketPlayOutEntityEquipment, "b");
    public static Field nmsPacketPlayOutEntityEquipment_ItemStack = getField(Classes.nmsPacketPlayOutEntityEquipment, "c");
    public static Field nmsPacketPlayOutEntityTeleport_EntityId = getField(Classes.nmsPacketPlayOutEntityTeleport, "a");
    public static Field nmsPacketPlayOutEntityTeleport_LocationY = getField(Classes.nmsPacketPlayOutEntityTeleport, "c");
    public static Field nmsPacketPlayOutEntityTeleport_LocationYaw = getField(Classes.nmsPacketPlayOutEntityTeleport, "e");
    public static Field nmsPacketPlayOutSpawnEntity_EntityId = getField(Classes.nmsPacketPlayOutSpawnEntity, "a");
    public static Field nmsPacketPlayOutSpawnEntity_LocationY = getField(Classes.nmsPacketPlayOutSpawnEntity, "c");
    public static Field nmsPacketPlayOutSpawnEntity_LocationYaw = getField(Classes.nmsPacketPlayOutSpawnEntity, "i");

    public static Field nmsAxisAlignedBB_locYBottom = getField(Classes.nmsAxisAlignedBB, "b");

    public static Field nmsWorld_isClientSide;

    /** @return double ブロックの高さ。通常ブロックなら1.0D、半ブロックなら0.5D */
    public static Field nmsBlock_maxY = getField(Classes.nmsBlock, "maxY");

    public static Field nmsEntity_dead = getField(Classes.nmsEntity, "dead");
    public static Field nmsEntity_onGround = getField(Classes.nmsEntity, "onGround");
    public static Field nmsEntity_noclip;
    public static Field nmsEntity_positionChanged = getField(Classes.nmsEntity, "positionChanged");
    public static Field nmsEntity_climbableHeight = getField(Classes.nmsEntity, "S");
    public static Field nmsEntity_fallDistance = getField(Classes.nmsEntity, "fallDistance");
    public static Field nmsEntity_ticksLived = getField(Classes.nmsEntity, "ticksLived");
    public static Field nmsEntity_locX = getField(Classes.nmsEntity, "locX");
    public static Field nmsEntity_locY = getField(Classes.nmsEntity, "locY");
    public static Field nmsEntity_locZ = getField(Classes.nmsEntity, "locZ");
    public static Field nmsEntity_yaw = getField(Classes.nmsEntity, "yaw");
    public static Field nmsEntity_pitch = getField(Classes.nmsEntity, "pitch");
    public static Field nmsEntity_finalYaw;
    public static Field nmsEntity_lastX = getField(Classes.nmsEntity, "lastX");
    public static Field nmsEntity_lastY = getField(Classes.nmsEntity, "lastY");
    public static Field nmsEntity_lastZ = getField(Classes.nmsEntity, "lastZ");
    public static Field nmsEntity_motX = getField(Classes.nmsEntity, "motX");
    public static Field nmsEntity_motY = getField(Classes.nmsEntity, "motY");
    public static Field nmsEntity_motZ = getField(Classes.nmsEntity, "motZ");

    /** @return float */
    public static Field nmsEntity_width = getField(Classes.nmsEntity, "width");

    /** @return float */
    public static Field nmsEntity_length = getField(Classes.nmsEntity, "length");
    public static Field nmsEntity_passenger = getField(Classes.nmsEntity, "passenger");
    public static Field nmsEntity_vehicle = getField(Classes.nmsEntity, "vehicle");

    public static Field nmsEntityHuman_forwardMotionInput;
    public static Field nmsEntityHuman_sideMotionInput;

    //XXX: CraftBukkit Unstable
    static {
        if (getBukkitVersion().equalsIgnoreCase("v1_8_R1")) {
            nmsWorld_isClientSide = getField(Classes.nmsWorld, "isStatic");
            nmsEntity_noclip = getField(Classes.nmsEntity, "T");
            nmsEntityHuman_forwardMotionInput = getField(Classes.nmsEntityHuman, "aY");
            nmsEntityHuman_sideMotionInput = getField(Classes.nmsEntityHuman, "aX");
        } else {
            nmsWorld_isClientSide = getField(Classes.nmsWorld, "isClientSide");
            nmsEntity_noclip = getField(Classes.nmsEntity, "noclip");
            nmsEntityHuman_forwardMotionInput = getField(Classes.nmsEntityHuman, "ba");
            nmsEntityHuman_sideMotionInput = getField(Classes.nmsEntityHuman, "aZ");
        }
    }
}
