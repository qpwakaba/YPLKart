package com.github.erozabesu.yplkart.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class Methods extends ReflectionUtil {

    //〓 Nms 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Method nmsAxisAlignedBB_grow = getMethod(Classes.nmsAxisAlignedBB, "grow", double.class, double.class, double.class);

    public static Method nmsWorld_getEntities = getMethod(Classes.nmsWorld, "getEntities", Classes.nmsEntity, Classes.nmsAxisAlignedBB);
    public static Method nmsWorld_addEntity = getMethod(Classes.nmsWorld, "addEntity", Classes.nmsEntity);

    public static Method nmsBlock_getMaterial = getMethod(Classes.nmsBlock, "getMaterial");

    public static Method nmsDamageSource_getEntity = getMethod(Classes.nmsDamageSource, "getEntity");

    public static Method nmsMaterial_isSolid = getMethod(Classes.nmsMaterial, "isSolid");

    public static Method nmsEntity_getBoundingBox = getMethod(Classes.nmsEntity, "getBoundingBox");
    public static Method nmsEntity_getBukkitEntity = getMethod(Classes.nmsEntity, "getBukkitEntity");
    public static Method nmsEntity_getCustomName = getMethod(Classes.nmsEntity, "getCustomName");
    public static Method nmsEntity_getId = getMethod(Classes.nmsEntity, "getId");
    public static Method nmsEntity_getWorld = getMethod(Classes.nmsEntity, "getWorld");
    public static Method nmsEntity_setEntityID = getMethod(Classes.nmsEntity, "d", int.class);
    public static Method nmsEntity_setLocation = getMethod(Classes.nmsEntity, "setLocation", double.class, double.class, double.class, float.class, float.class);
    public static Method nmsEntity_setCustomName = getMethod(Classes.nmsEntity, "setCustomName", String.class);
    public static Method nmsEntity_setCustomNameVisible = getMethod(Classes.nmsEntity, "setCustomNameVisible", boolean.class);
    public static Method nmsEntity_setSize;
    public static Method nmsEntity_setYawPitch = getMethod(Classes.nmsEntity, "setYawPitch", float.class, float.class);
    public static Method nmsEntity_checkBlockCollisions = getMethod(Classes.nmsEntity, "checkBlockCollisions");
    public static Method nmsEntity_collide = getMethod(Classes.nmsEntity, "collide", Classes.nmsEntity);
    public static Method nmsEntity_die = getMethod(Classes.nmsEntity, "die");
    public static Method nmsEntity_mount = getMethod(Classes.nmsEntity, "mount", Classes.nmsEntity);
    public static Method nmsEntity_move = getMethod(Classes.nmsEntity, "move", double.class, double.class, double.class);
    public static Method nmsEntity_moveAbsolute = getMethod(Classes.nmsEntity, "g", double.class, double.class, double.class);

    public static Method nmsEntityHuman_getAttributesMovementSpeed;

    public static Method nmsEntityArmorStand_setRightArmPose = getMethod(Classes.nmsEntityArmorStand, "setRightArmPose", Classes.nmsVector3f);

    public static Method nmsWatchableObject_getIndex = getMethod(Classes.nmsWatchableObject, "a");

    public static Method nmsPlayerConnection_skipRespawnWindow = getMethod(Classes.nmsPlayerConnection, "a", Classes.nmsPacketPlayInClientCommand);
    public static Method nmsPlayerConnection_sendPacket = getMethod(Classes.nmsPlayerConnection, "sendPacket", Classes.nmsPacket);

    /** 引数1と引数2の絶対値を比較し、高い方の数値をDouble型で返す */
    public static Method static_nmsMathHelper_a = getMethod(Classes.nmsMathHelper, "a", double.class, double.class);
    public static Method static_nmsMathHelper_a2 = getMethod(Classes.nmsMathHelper, "a", double.class, double.class, double.class);
    public static Method static_nmsMathHelper_floor = getMethod(Classes.nmsMathHelper, "floor", double.class);
    public static Method static_nmsMathHelper_cos = getMethod(Classes.nmsMathHelper, "cos", float.class);
    public static Method static_nmsMathHelper_sin = getMethod(Classes.nmsMathHelper, "sin", float.class);

    public static Method static_nmsChatSerializer_buildTitle = getMethod(Classes.nmsChatSerializer, "a", String.class);

    public static Method static_nmsBlock_getById = getMethod(Classes.nmsBlock, "getById", int.class);

    static {
        //XXX: CraftBukkit Unstable
        if (getBukkitVersion().equalsIgnoreCase("v1_8_R1")) {
            Methods.nmsEntity_setSize = getMethod(Classes.nmsEntity, "a", float.class, float.class);
            Methods.nmsEntityHuman_getAttributesMovementSpeed = getMethod(Classes.nmsEntityHuman, "bH");
        } else {
            Methods.nmsEntity_setSize = getMethod(Classes.nmsEntity, "setSize", float.class, float.class);
            Methods.nmsEntityHuman_getAttributesMovementSpeed = getMethod(Classes.nmsEntityHuman, "bI");
        }
    }

    //〓 Craft 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * @param Instance:World
     * @return NmsWorldServer
     */
    public static Method craftWorld_getHandle =  getMethod(Classes.craftWorld, "getHandle");

    /**
     * @param Instance:Block
     * @return NmsBlock
     */
    public static Method craftBlock_getNMSBlock =  getMethod(Classes.craftBlock, "getNMSBlock");

    /**
     * ItemStackからCraftItemStackを取得し返す
     * @param Instance:ItemStack
     * @return CraftItemStack
     */
    public static Method static_craftItemStack_asNMSCopy =  getMethod(Classes.craftItemStack, "asNMSCopy", ItemStack.class);

    //〓 Craft List 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** EntityXxxxxクラス毎のgetHandleメソッド */
    public static HashMap<String, Method> craftEntity_getHandle = new HashMap<String, Method>();

    //〓 Ypl 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Method Ypl_getGroundFrictionX = getMethod(Classes.yplCustomKart, "getGroundFrictionX");
    public static Method Ypl_getGroundFrictionY = getMethod(Classes.yplCustomKart, "getGroundFrictionY");
    public static Method Ypl_getGroundFrictionZ = getMethod(Classes.yplCustomKart, "getGroundFrictionZ");
    public static Method Ypl_getFlyFrictionX = getMethod(Classes.yplCustomKart, "getFlyFrictionX");
    public static Method Ypl_getFlyFrictionY = getMethod(Classes.yplCustomKart, "getFlyFrictionY");
    public static Method Ypl_getFlyFrictionZ = getMethod(Classes.yplCustomKart, "getFlyFrictionZ");
    public static Method Ypl_getKart = getMethod(Classes.yplCustomKart, "getKart");
    public static Method Ypl_getKartType = getMethod(Classes.yplCustomKart, "getKartType");
    public static Method Ypl_getSpeedStack = getMethod(Classes.yplCustomKart, "getSpeedStack");
    public static Method Ypl_getLastMotionSpeed = getMethod(Classes.yplCustomKart, "getLastMotionSpeed");
    public static Method Ypl_isKillerInitialized = getMethod(Classes.yplCustomKart, "isKillerInitialized");
    public static Method Ypl_getKillerX = getMethod(Classes.yplCustomKart, "getKillerX");
    public static Method Ypl_getKillerY = getMethod(Classes.yplCustomKart, "getKillerY");
    public static Method Ypl_getKillerZ = getMethod(Classes.yplCustomKart, "getKillerZ");
    public static Method Ypl_getKillerPassedCheckPointList = getMethod(Classes.yplCustomKart, "getKillerPassedCheckPointList");
    public static Method Ypl_getKillerLastPassedCheckPoint = getMethod(Classes.yplCustomKart, "getKillerLastPassedCheckPoint");
    public static Method Ypl_getLivingCheckTask = getMethod(Classes.yplCustomKart, "getLivingCheckTask");
    public static Method Ypl_getLastTicksLived = getMethod(Classes.yplCustomKart, "getLastTicksLived");

    public static Method Ypl_setGroundFrictionX = getMethod(Classes.yplCustomKart, "setGroundFrictionX", double.class);
    public static Method Ypl_setGroundFrictionY = getMethod(Classes.yplCustomKart, "setGroundFrictionY", double.class);
    public static Method Ypl_setGroundFrictionZ = getMethod(Classes.yplCustomKart, "setGroundFrictionZ", double.class);
    public static Method Ypl_setFlyFrictionX = getMethod(Classes.yplCustomKart, "setFlyFrictionX", double.class);
    public static Method Ypl_setFlyFrictionY = getMethod(Classes.yplCustomKart, "setFlyFrictionY", double.class);
    public static Method Ypl_setFlyFrictionZ = getMethod(Classes.yplCustomKart, "setFlyFrictionZ", double.class);
    public static Method Ypl_setKart = getMethod(Classes.yplCustomKart, "setKart", Kart.class);
    public static Method Ypl_setKartType = getMethod(Classes.yplCustomKart, "setKartType", KartType.class);
    public static Method Ypl_setSpeedStack = getMethod(Classes.yplCustomKart, "setSpeedStack", double.class);
    public static Method Ypl_setLastMotionSpeed = getMethod(Classes.yplCustomKart, "setLastMotionSpeed", double.class);
    public static Method Ypl_setKillerInitialized = getMethod(Classes.yplCustomKart, "setKillerInitialized", boolean.class);
    public static Method Ypl_setKillerX = getMethod(Classes.yplCustomKart, "setKillerX", double.class);
    public static Method Ypl_setKillerY = getMethod(Classes.yplCustomKart, "setKillerY", double.class);
    public static Method Ypl_setKillerZ = getMethod(Classes.yplCustomKart, "setKillerZ", double.class);
    public static Method Ypl_setKillerPassedCheckPointList = getMethod(Classes.yplCustomKart, "setKillerPassedCheckPointList", List.class);
    public static Method Ypl_setKillerLastPassedCheckPoint = getMethod(Classes.yplCustomKart, "setKillerLastPassedCheckPoint", Entity.class);
    public static Method Ypl_setLivingCheckTask = getMethod(Classes.yplCustomKart, "setLivingCheckTask", BukkitTask.class);
    public static Method Ypl_setLastTicksLived = getMethod(Classes.yplCustomKart, "setLastTicksLived", int.class);
}
