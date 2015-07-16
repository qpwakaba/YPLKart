package com.github.erozabesu.yplkart.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ReflectionUtil {
    private static String bukkitVersion =
            Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    private static String bukkitPackage = "net.minecraft.server." + getBukkitVersion();
    private static String craftPackage = "org.bukkit.craftbukkit." + getBukkitVersion();
    private static String yplkartPackage =
            "com.github.erozabesu.yplkart.override." + getBukkitVersion();

    private static Class<?> NMSWorld = getBukkitClass("World");
    private static Class<?> CraftWorld = getCraftClass("CraftWorld");
    private static Class<?> CraftItemStack = getCraftClass("inventory.CraftItemStack");

    private static HashMap<String, Constructor<?>> BukkitEntity_Constructor =
            new HashMap<String, Constructor<?>>();
    private static HashMap<String, Method> CraftEntity_getHandle = new HashMap<String, Method>();
    public static Method CraftWorld_getHandle;
    private static Method static_CraftItemStack_asNMSCopy;

    public ReflectionUtil() {
        try {
            CraftWorld_getHandle = CraftWorld.getMethod("getHandle");
            static_CraftItemStack_asNMSCopy = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBukkitVersion() {
        return bukkitVersion;
    }

    public static String getBukkitPackageName() {
        return bukkitPackage;
    }

    public static String getCraftPackageName() {
        return craftPackage;
    }

    public static String getYPLKartPackageName() {
        return yplkartPackage;
    }

    public static Class<?> getBukkitClass(String s) {
        try {
            return Class.forName(getBukkitPackageName() + "." + s);
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return field;
    }

    public static Field getField(Class<?> clazz, String name) {
        for (Field field : clazz.getFields()) {
            if (!field.getName().equalsIgnoreCase(name))
                continue;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getCraftEntity(Entity entity) {
        try {
            Method getHandle = CraftEntity_getHandle.get(entity.getClass().getSimpleName());
            if (getHandle == null) {
                getHandle = entity.getClass().getMethod("getHandle");
                CraftEntity_getHandle.put(entity.getClass().getSimpleName(), getHandle);
            }

            return getHandle.invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftEntityFromClassName(World w, String classname) {
        try {
            Constructor<?> con = BukkitEntity_Constructor.get(classname);
            if (con == null) {
                con = getBukkitClass(classname).getConstructor(NMSWorld);
                BukkitEntity_Constructor.put(classname, con);
            }

            return con.newInstance(getCraftWorld(w));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftWorld(World w) {
        try {
            return CraftWorld_getHandle.invoke(w);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftItemStack(ItemStack item) {
        try {
            return static_CraftItemStack_asNMSCopy.invoke(null, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
