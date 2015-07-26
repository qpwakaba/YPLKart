package com.github.erozabesu.yplkart.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ReflectionUtil {

    //〓 Package Name 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    protected static String bukkitVersion =
            Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    protected static String nmsPackage = "net.minecraft.server." + getBukkitVersion();
    protected static String craftPackage = "org.bukkit.craftbukkit." + getBukkitVersion();
    protected static String yplkartPackage =
            "com.github.erozabesu.yplkart.override." + getBukkitVersion();

    //〓 Nms Class 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    protected static Class<?> nmsWorld = getNMSClass("World");
    protected static Class<?> nmsBlock = getNMSClass("Block");
    protected static Class<?> nmsMaterial = getNMSClass("Material");
    protected static Class<?> nmsEntity = getNMSClass("Entity");
    protected static Class<?> nmsEntityHuman = getNMSClass("EntityHuman");
    protected static Class<?> nmsEntityLiving = getNMSClass("EntityLiving");
    protected static Class<?> nmsItemStack = getNMSClass("ItemStack");
    protected static Class<?> nmsPacket = getNMSClass("Packet");
    protected static Class<?> nmsPlayerConnection = getNMSClass("PlayerConnection");
    protected static Class<?> nmsIChatBaseComponent = getNMSClass("IChatBaseComponent");
    protected static Class<?> nmsChatSerializer = null;
    protected static Class<?> nmsEnumTitleAction = null;
    protected static Class<?> nmsEnumClientCommand = null;
    protected static Class<?> nmsPacketPlayOutAttachEntity = getNMSClass("PacketPlayOutAttachEntity");
    protected static Class<?> nmsPacketPlayOutEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy");
    protected static Class<?> nmsPacketPlayOutEntityEquipment = getNMSClass("PacketPlayOutEntityEquipment");
    protected static Class<?> nmsPacketPlayOutNamedEntitySpawn = getNMSClass("PacketPlayOutNamedEntitySpawn");
    protected static Class<?> nmsPacketPlayOutSpawnEntityLiving = getNMSClass("PacketPlayOutSpawnEntityLiving");
    protected static Class<?> nmsPacketPlayOutTitle = getNMSClass("PacketPlayOutTitle");
    protected static Class<?> nmsPacketPlayInClientCommand = getNMSClass("PacketPlayInClientCommand");

    static {
        if (getBukkitVersion().equalsIgnoreCase("v1_8_R1")) {
            nmsChatSerializer = getNMSClass("ChatSerializer");
            nmsEnumTitleAction = getNMSClass("EnumTitleAction");
            nmsEnumClientCommand = getNMSClass("EnumClientCommand");
        } else {
            nmsChatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
            nmsEnumTitleAction = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            nmsEnumClientCommand = getNMSClass("PacketPlayInClientCommand$EnumClientCommand");
        }
    }

    //〓 Nms Constructor 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    protected static Constructor<?> constructor_nmsPacketPlayOutAttachEntity;
    protected static Constructor<?> constructor_nmsPacketPlayOutEntityDestroy;
    protected static Constructor<?> constructor_nmsPacketPlayOutEntityEquipment;
    protected static Constructor<?> constructor_nmsPacketPlayOutNamedEntitySpawn;
    protected static Constructor<?> constructor_nmsPacketPlayOutSpawnEntityLiving;
    protected static Constructor<?> constructor_nmsPacketPlayOutTitle;
    protected static Constructor<?> constructor_nmsPacketPlayOutTitle_Length;
    protected static Constructor<?> constructor_nmsPacketPlayInClientCommand;

    static {
        try {
            constructor_nmsPacketPlayOutAttachEntity = nmsPacketPlayOutAttachEntity.getConstructor(int.class, nmsEntity, nmsEntity);
            constructor_nmsPacketPlayOutEntityDestroy = nmsPacketPlayOutEntityDestroy.getConstructor(int[].class);
            constructor_nmsPacketPlayOutEntityEquipment = nmsPacketPlayOutEntityEquipment.getConstructor(int.class, int.class, nmsItemStack);
            constructor_nmsPacketPlayOutNamedEntitySpawn = nmsPacketPlayOutNamedEntitySpawn.getConstructor(nmsEntityHuman);
            constructor_nmsPacketPlayOutSpawnEntityLiving = nmsPacketPlayOutSpawnEntityLiving.getConstructor(nmsEntityLiving);
            constructor_nmsPacketPlayOutTitle = nmsPacketPlayOutTitle.getConstructor(nmsEnumTitleAction, nmsIChatBaseComponent);
            constructor_nmsPacketPlayOutTitle_Length = nmsPacketPlayOutTitle.getConstructor(int.class, int.class, int.class);
            constructor_nmsPacketPlayInClientCommand = nmsPacketPlayInClientCommand.getConstructor(nmsEnumClientCommand);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //〓 Nms Method 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    protected static Method nmsBlock_getMaterial;
    protected static Method nmsMaterial_isSolid;
    protected static Method nmsPlayerConnection_sendPacket;
    protected static Method nmsEntity_setEntityID;
    protected static Method nmsEntity_setLocation;
    protected static Method nmsEntity_setCustomName;
    protected static Method nmsEntity_setCustomNameVisible;

    protected static Method static_nmsChatSerializer_buildTitle;
    protected static Method static_nmsBlock_getById;

    static {
        try {
            nmsBlock_getMaterial = nmsBlock.getMethod("getMaterial");
            nmsMaterial_isSolid = nmsMaterial.getMethod("isSolid");
            nmsPlayerConnection_sendPacket = nmsPlayerConnection.getMethod("sendPacket", nmsPacket);
            nmsEntity_setEntityID = nmsEntity.getMethod("d", int.class);
            nmsEntity_setLocation = nmsEntity.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            nmsEntity_setCustomName = nmsEntity.getMethod("setCustomName", String.class);
            nmsEntity_setCustomNameVisible = nmsEntity.getMethod("setCustomNameVisible", boolean.class);

            static_nmsChatSerializer_buildTitle = nmsChatSerializer.getMethod("a", String.class);
            static_nmsBlock_getById = nmsBlock.getMethod("getById", int.class);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //〓 Nms Method List 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** EntityXxxxxクラス毎のコンストラクタ */
    protected static HashMap<String, Constructor<?>> nmsEntity_Constructor =
            new HashMap<String, Constructor<?>>();

    //〓 Craft Class 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    protected static Class<?> craftWorld = getCraftClass("CraftWorld");
    protected static Class<?> craftItemStack = getCraftClass("inventory.CraftItemStack");

    //〓 Craft Method 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    protected static Method craftWorld_getHandle;
    protected static Method static_craftItemStack_asNMSCopy;

    static {
        try {
            craftWorld_getHandle = craftWorld.getMethod("getHandle");
            static_craftItemStack_asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //〓 Craft Method List 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** EntityXxxxxクラス毎のgetHandleメソッド */
    protected static HashMap<String, Method> craftEntity_getHandle = new HashMap<String, Method>();

    public static String getBukkitVersion() {
        return bukkitVersion;
    }

    public static String getNMSPackageName() {
        return nmsPackage;
    }

    public static String getCraftPackageName() {
        return craftPackage;
    }

    public static String getYPLKartPackageName() {
        return yplkartPackage;
    }

    public static Class<?> getNMSClass(String s) {
        try {
            return Class.forName(getNMSPackageName() + "." + s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getCraftClass(String s) {
        try {
            return Class.forName(getCraftPackageName() + "." + s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getYPLKartClass(String s) {
        try {
            return Class.forName(getYPLKartPackageName() + "." + s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Object instance, String name) {
        Field field = null;
        try {
            field = instance.getClass().getField(name);
            if (field != null) {
                field.setAccessible(true);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    public static Field getField(Class<?> clazz, String name) {
        for (Field field : clazz.getFields()) {
            if (!field.getName().equalsIgnoreCase(name)) {
                continue;
            }
            field.setAccessible(true);
            return field;
        }
        return null;
    }

    public static Object getFieldValue(Object instance, String name) throws Exception {
        Field field = instance.getClass().getDeclaredField(name);
        field.setAccessible(true);

        return field.get(instance);
    }

    public static void setFieldValue(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getCraftEntity(Entity entity) {
        try {
            Method getHandle = craftEntity_getHandle.get(entity.getClass().getSimpleName());
            if (getHandle == null) {
                getHandle = entity.getClass().getMethod("getHandle");
                craftEntity_getHandle.put(entity.getClass().getSimpleName(), getHandle);
            }

            return getHandle.invoke(entity);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getCraftEntityFromClassName(World w, String classname) {
        try {
            Constructor<?> con = nmsEntity_Constructor.get(classname);
            if (con == null) {
                con = getNMSClass(classname).getConstructor(nmsWorld);
                nmsEntity_Constructor.put(classname, con);
            }

            return con.newInstance(getCraftWorld(w));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getCraftWorld(World w) {
        try {
            return craftWorld_getHandle.invoke(w);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftItemStack(ItemStack item) {
        try {
            return static_craftItemStack_asNMSCopy.invoke(null, item);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
