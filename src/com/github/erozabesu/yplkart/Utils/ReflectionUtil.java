package com.github.erozabesu.yplkart.Utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ReflectionUtil{
	public static String getBukkitVersion(){
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	public static String getBukkitPackageName(){
		return "net.minecraft.server." + getBukkitVersion();
	}

	public static String getCraftPackageName(){
		return "org.bukkit.craftbukkit." + getBukkitVersion();
	}

	public static String getYPLKartPackageName(){
		return "com.github.erozabesu.yplkart.OverrideClass." + getBukkitVersion();
	}

	public static Class<?> getBukkitClass(String s){
		try {
			return Class.forName(getBukkitPackageName() + "." + s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getCraftClass(String s){
		try {
			return Class.forName(getCraftPackageName() + "." + s);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getYPLKartClass(String s){
		try {
			return Class.forName(getYPLKartPackageName() + "." + s);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field getField(Object instance, String name){
		for(Field f : instance.getClass().getFields()){
			if(!f.getName().equalsIgnoreCase(name))continue;
			f.setAccessible(true);
			return f;
		}
		return null;
	}

	public static Field getField(Class<?> clazz, String name){
		for(Field f : clazz.getFields()){
			if(!f.getName().equalsIgnoreCase(name))continue;
			f.setAccessible(true);
			return f;
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

	public static Object getCraftEntity(Entity entity){
		try {
			return entity.getClass().getMethod("getHandle").invoke(entity);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getCraftEntityClass(World w, String classname){
		Class<?> entityclass = getBukkitClass(classname);
		try {
			return entityclass.getConstructor(getBukkitClass("World")).newInstance(getCraftWorld(w));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getCraftWorld(World w){
		try {
			return (getCraftClass("CraftWorld")).getMethod("getHandle").invoke(w);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getCraftItemStack(ItemStack item){
		try {
			return getCraftClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
