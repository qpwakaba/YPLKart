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
    private static String nmsPackage = "net.minecraft.server." + getBukkitVersion();
    private static String craftPackage = "org.bukkit.craftbukkit." + getBukkitVersion();
    private static String yplkartPackage =
            "com.github.erozabesu.yplkart.override." + getBukkitVersion();

    private static Class<?> nmsWorld = getNMSClass("World");
    private static Class<?> craftWorld = getCraftClass("CraftWorld");
    private static Class<?> craftItemStack = getCraftClass("inventory.CraftItemStack");

    private static HashMap<String, Constructor<?>> bukkitEntity_Constructor =
            new HashMap<String, Constructor<?>>();
    private static HashMap<String, Method> craftEntity_getHandle = new HashMap<String, Method>();
    public static Method craftWorld_getHandle;
    private static Method static_craftItemStack_asNMSCopy;

    public ReflectionUtil() {
        try {
            craftWorld_getHandle = craftWorld.getMethod("getHandle");
            static_craftItemStack_asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            Method getHandle = craftEntity_getHandle.get(entity.getClass().getSimpleName());
            if (getHandle == null) {
                getHandle = entity.getClass().getMethod("getHandle");
                craftEntity_getHandle.put(entity.getClass().getSimpleName(), getHandle);
            }

            return getHandle.invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftEntityFromClassName(World w, String classname) {
        try {
            Constructor<?> con = bukkitEntity_Constructor.get(classname);
            if (con == null) {
                con = getNMSClass(classname).getConstructor(nmsWorld);
                bukkitEntity_Constructor.put(classname, con);
            }

            return con.newInstance(getCraftWorld(w));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftWorld(World w) {
        try {
            return craftWorld_getHandle.invoke(w);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getCraftItemStack(ItemStack item) {
        try {
            return static_craftItemStack_asNMSCopy.invoke(null, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
