package com.github.erozabesu.yplkart.Utils;

import java.lang.reflect.Method;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireWorks {
	private static Method nmwWorld = null;
	private static Method broadcastEntityEffect = null;
	private static Method nmsFirework = null;

	public static void playFirework(World world, Location loc, FireworkEffect fe) throws Exception {
		Firework fw = (Firework) world.spawn(loc, Firework.class);

		Object obj_world = null;
		Object obj_firework = null;

		if(nmwWorld == null) {
			nmwWorld = getMethod(world.getClass(), "getHandle");
			nmsFirework = getMethod(fw.getClass(), "getHandle");
		}

		obj_world = nmwWorld.invoke(world, (Object[]) null);
		obj_firework = nmsFirework.invoke(fw, (Object[]) null);

		if(broadcastEntityEffect == null) {
			broadcastEntityEffect = getMethod(obj_world.getClass(), "broadcastEntityEffect");
		}

		FireworkMeta data = (FireworkMeta) fw.getFireworkMeta();
		data.clearEffects();
		data.setPower(1);
		data.addEffect(fe);
		fw.setFireworkMeta(data);

		broadcastEntityEffect.invoke(obj_world, new Object[] {obj_firework, (byte) 17});
		fw.remove();
	}

	private static Method getMethod(Class<?> cl, String method) {
		for(Method m : cl.getMethods()) {
			if(m.getName().equals(method)) {
				return m;
			}
		}
		return null;
	}
}
