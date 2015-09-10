package com.github.erozabesu.yplkart.reflection;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.bukkit.Location;

import com.github.erozabesu.yplkart.enumdata.KartType;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class Constructors extends ReflectionUtil{

    //〓 Nms 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * @param Double
     * @param Double
     * @param Double
     */
    public static Constructor<?> nmsBlockPosition = getConstructor(Classes.nmsBlockPosition, double.class, double.class, double.class);

    /**
     * @param Double
     * @param Double
     * @param Double
     */
    public static Constructor<?> nmsVec3D = getConstructor(Classes.nmsVec3D, double.class, double.class, double.class);

    /**
     * @param Float
     * @param Float
     * @param Float
     */
    public static Constructor<?> nmsVector3f = getConstructor(Classes.nmsVector3f, float.class, float.class, float.class);

    /**
     * @param NmsIChatBaseComponent
     * @param Byte
     */
    public static Constructor<?> nmsPacketPlayOutChat = getConstructor(Classes.nmsPacketPlayOutChat, Classes.nmsIChatBaseComponent, byte.class);

    /**
     * @param Integer
     * @param NmsEntity
     * @param NmsEntity
     */
    public static Constructor<?> nmsPacketPlayOutAttachEntity = getConstructor(Classes.nmsPacketPlayOutAttachEntity, int.class, Classes.nmsEntity, Classes.nmsEntity);

    /** @param Integer[] */
    public static Constructor<?> nmsPacketPlayOutEntityDestroy = getConstructor(Classes.nmsPacketPlayOutEntityDestroy, int[].class);

    /**
     * @param Integer
     * @param Integer
     * @param NmsItemStack
     */
    public static Constructor<?> nmsPacketPlayOutEntityEquipment = getConstructor(Classes.nmsPacketPlayOutEntityEquipment, int.class, int.class, Classes.nmsItemStack);

    /**
     * @param Integer
     * @param Byte
     * @param Byte
     * @param Boolean
     */
    public static Constructor<?> nmsPacketPlayOutEntityLook = getConstructor(Classes.nmsPacketPlayOutEntityLook, int.class, byte.class, byte.class, boolean.class);

    /**
     * @param Integer
     * @param Integer
     * @param Integer
     * @param Integer
     * @param Byte
     * @param Byte
     * @param Boolean
     */
    public static Constructor<?> nmsPacketPlayOutEntityTeleport = getConstructor(Classes.nmsPacketPlayOutEntityTeleport, int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);

    /**
     * @param Integer
     * @param Byte
     * @param Byte
     * @param Byte
     * @param Byte
     * @param Byte
     * @param Boolean
     */
    public static Constructor<?> nmsPacketPlayOutRelEntityMoveLook = getConstructor(Classes.nmsPacketPlayOutRelEntityMoveLook, int.class, byte.class, byte.class, byte.class, byte.class, byte.class, boolean.class);

    /** @param NmsEntityHuman */
    public static Constructor<?> nmsPacketPlayOutNamedEntitySpawn = getConstructor(Classes.nmsPacketPlayOutNamedEntitySpawn, Classes.nmsEntityHuman);

    /**
     * @param NmsEntity
     * @param Integer
     * @param Integer
     */
    public static Constructor<?> nmsPacketPlayOutSpawnEntity = getConstructor(Classes.nmsPacketPlayOutSpawnEntity, Classes.nmsEntity, int.class, int.class);

    /** @param NmsEntityLiving */
    public static Constructor<?> nmsPacketPlayOutSpawnEntityLiving = getConstructor(Classes.nmsPacketPlayOutSpawnEntityLiving, Classes.nmsEntityLiving);

    /** @param NmsIChatBaseComponent */
    public static Constructor<?> nmsPacketPlayOutTitle = getConstructor(Classes.nmsPacketPlayOutTitle, Classes.nmsEnumTitleAction, Classes.nmsIChatBaseComponent);

    /**
     * @param Integer
     * @param Integer
     * @param Integer
     */
    public static Constructor<?> nmsPacketPlayOutTitle_Length = getConstructor(Classes.nmsPacketPlayOutTitle, int.class, int.class, int.class);

    /**
     * @param NmsEnumParticle
     * @param Boolean
     * @param Float
     * @param Float
     * @param Float
     * @param Float
     * @param Float
     * @param Float
     * @param Float
     * @param Integer
     * @param Integer[]
     */
    public static Constructor<?> nmsPacketPlayOutWorldParticles = getConstructor(Classes.nmsPacketPlayOutWorldParticles, Classes.nmsEnumParticle, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);

    /** @param NmsEnumClientCommand */
    public static Constructor<?> nmsPacketPlayInClientCommand = getConstructor(Classes.nmsPacketPlayInClientCommand, Classes.nmsEnumClientCommand);

    //〓 Nms List 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** EntityXxxxxクラス毎のコンストラクタ */
    public static HashMap<String, Constructor<?>> nmsEntity_Constructor = new HashMap<String, Constructor<?>>();

    //〓 Ypl 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Constructor<?> constructor_yplCustomKart = ReflectionUtil.getConstructor(Classes.yplCustomKart, Classes.nmsWorld, Kart.class, KartType.class, Location.class);
}
