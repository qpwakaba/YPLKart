package com.github.erozabesu.yplkart.reflection;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.bukkit.Location;

import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class Constructors extends ReflectionUtil{

    //〓 Nms 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Constructor<?> nmsBlockPosition = getConstructor(Classes.nmsBlockPosition, double.class, double.class, double.class);
    public static Constructor<?> nmsVector3f = getConstructor(Classes.nmsVector3f, float.class, float.class, float.class);
    public static Constructor<?> nmsPacketPlayOutAttachEntity = getConstructor(Classes.nmsPacketPlayOutAttachEntity, int.class, Classes.nmsEntity, Classes.nmsEntity);
    public static Constructor<?> nmsPacketPlayOutEntityDestroy = getConstructor(Classes.nmsPacketPlayOutEntityDestroy, int[].class);
    public static Constructor<?> nmsPacketPlayOutEntityEquipment = getConstructor(Classes.nmsPacketPlayOutEntityEquipment, int.class, int.class, Classes.nmsItemStack);
    public static Constructor<?> nmsPacketPlayOutEntityLook = getConstructor(Classes.nmsPacketPlayOutEntityLook, int.class, byte.class, byte.class, boolean.class);
    public static Constructor<?> nmsPacketPlayOutEntityTeleport = getConstructor(Classes.nmsPacketPlayOutEntityTeleport, int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);
    public static Constructor<?> nmsPacketPlayOutRelEntityMoveLook = getConstructor(Classes.nmsPacketPlayOutRelEntityMoveLook, int.class, byte.class, byte.class, byte.class, byte.class, byte.class, boolean.class);
    public static Constructor<?> nmsPacketPlayOutNamedEntitySpawn = getConstructor(Classes.nmsPacketPlayOutNamedEntitySpawn, Classes.nmsEntityHuman);
    public static Constructor<?> nmsPacketPlayOutSpawnEntity = getConstructor(Classes.nmsPacketPlayOutSpawnEntity, Classes.nmsEntity, int.class, int.class);
    public static Constructor<?> nmsPacketPlayOutSpawnEntityLiving = getConstructor(Classes.nmsPacketPlayOutSpawnEntityLiving, Classes.nmsEntityLiving);
    public static Constructor<?> nmsPacketPlayOutTitle = getConstructor(Classes.nmsPacketPlayOutTitle, Classes.nmsEnumTitleAction, Classes.nmsIChatBaseComponent);
    public static Constructor<?> nmsPacketPlayOutTitle_Length = getConstructor(Classes.nmsPacketPlayOutTitle, int.class, int.class, int.class);
    public static Constructor<?> nmsPacketPlayInClientCommand = getConstructor(Classes.nmsPacketPlayInClientCommand, Classes.nmsEnumClientCommand);

    //〓 Nms List 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static HashMap<String, Constructor<?>> nmsEntity_Constructor = new HashMap<String, Constructor<?>>();

    //〓 Ypl 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Constructor<?> constructor_yplCustomKart = ReflectionUtil.getConstructor(Classes.yplCustomKart, Classes.nmsWorld, Kart.class, KartType.class, Location.class);
    /** EntityXxxxxクラス毎のコンストラクタ */
}
