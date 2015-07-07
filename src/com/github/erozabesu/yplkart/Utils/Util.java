package com.github.erozabesu.yplkart.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Object.Race;
import com.github.erozabesu.yplkart.Task.FlowerShowerTask;
import com.github.erozabesu.yplkart.Task.SendBlinkingTitleTask;

public class Util extends ReflectionUtil{
	public static Class<?> CraftBlock;
	public static Class<?> CraftMaterial;

	public static Method Block_getById;
	public static Method Block_getMaterial;
	public static Method Material_isSolid;

	public Util(){
		try {
			CraftBlock = getBukkitClass("Block");
			CraftMaterial = getBukkitClass("Material");
			Block_getById = CraftBlock.getMethod("getById", int.class);
			Block_getMaterial = CraftBlock.getMethod("getMaterial");
			Material_isSolid = CraftMaterial.getMethod("isSolid");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getRandom(int value){
		int num;
		Random random = new Random();
		num = random.nextInt(10) < 5 ? random.nextInt(value) : -random.nextInt(value);
		return num;
	}

	public static Vector getVectorLocationToLocation(Location second_location,Location first_location){
		Vector vector = second_location.toVector().subtract(first_location.toVector());
		return vector.normalize();
	}

	public static float getYawfromVector(Vector v) {
		double dx = v.getX();
		double dz = v.getZ();
		double yaw = 0;
		// Set yaw
		if (dx != 0) {
			// Set yaw start value based on dx
			if (dx < 0) {
				yaw = 1.5 * Math.PI;
			} else {
				yaw = 0.5 * Math.PI;
			}
			yaw -= Math.atan(dz / dx);
		} else if (dz < 0) {
			yaw = Math.PI;
		}
		return (float) (-yaw * 180 / Math.PI - 90);
	}

	public static String getStepBlock(Location l){
		Block b = l.getBlock();
		if(b.getType() == Material.AIR){
			b = b.getLocation().add(0,-1,0).getBlock();
			if(b.getType() == Material.AIR){
				b = b.getLocation().add(0,-1,0).getBlock();
			}
		}

		return String.valueOf(b.getTypeId()) + ":" + String.valueOf(b.getData());
	}

	public static Material getStepMaterial(Location l){
		Block b = l.add(0,1,0).getBlock();
		if(b.getType() == Material.AIR){
			b = b.getLocation().add(0,-1,0).getBlock();
			if(b.getType() == Material.AIR){
				b = b.getLocation().add(0,-1,0).getBlock();
			}
		}

		return b.getType();
	}

	public static Location getLocationfromYaw(Location from, double offset){
		Location fromadjust = adjustBlockLocation(from);
		float yaw = fromadjust.getYaw();
		double x = -Math.sin(Math.toRadians(yaw < 0 ? yaw+360 : yaw));
		double z = Math.cos(Math.toRadians(yaw < 0 ? yaw+360 : yaw));

		Location to = new Location(fromadjust.getWorld(), fromadjust.getX()+x*offset, fromadjust.getY(), fromadjust.getZ()+z*offset);
		to.setYaw(yaw);
		return to;
	}

	public static Location getSideLocationfromYaw(Location from, double offset){
		Location fromadjust = adjustBlockLocation(from);
		float yaw = fromadjust.getYaw();
		double x = Math.cos(Math.toRadians(yaw));
		double z = Math.sin(Math.toRadians(yaw));

		Location to = new Location(fromadjust.getWorld(), fromadjust.getX()+x*offset, fromadjust.getY(), fromadjust.getZ()+z*offset);
		to.setYaw(yaw);
		return to;
	}

	//指定半径内の高さ１ブロック内の座標を直方体で取得
	public static ArrayList<Location> getSquareLocation(Location loc, double r){
		ArrayList<Location> square = new ArrayList<Location>();
		double cx = loc.getX();
		double cz = loc.getZ();
		for (double x = cx - r; x <= cx + r; x++) {
			for (double z = cz - r; z <= cz + r; z++) {
				Location cylLocation = new Location(loc.getWorld(), x, loc.getBlockY(), z);
				square.add(cylLocation);
			}
		}
		return square;
	}

	public static ArrayList<Entity> getNearbyEntities(Location l, double radius) {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for (Entity entity : l.getWorld().getEntities()) {
			if (l.distanceSquared(entity.getLocation()) <= radius * radius) {
				entities.add(entity);
			}
		}
		return entities;
	}

	public static ArrayList<LivingEntity> getNearbyLivingEntities(Location l, double radius){
		ArrayList<Entity> entity = getNearbyEntities(l,radius);
		ArrayList<LivingEntity> livingentity = new ArrayList<LivingEntity>();
		for(Entity e : entity){
			if(e instanceof LivingEntity)
				livingentity.add((LivingEntity) e);
		}
		return livingentity;
	}

	public static ArrayList<Player> getNearbyPlayers(Location l, double radius){
		ArrayList<Entity> entity = getNearbyEntities(l,radius);
		ArrayList<Player> livingentity = new ArrayList<Player>();
		for(Entity e : entity){
			if(e instanceof Player)
				livingentity.add((Player) e);
		}
		return livingentity;
	}

	public static Entity getNearestEntity(List<Entity> entities, Location l){
		Iterator<Entity> i = entities.iterator();
		Entity e = null;
		Entity temp;
		while(i.hasNext()){
			temp = i.next();
			if(e != null)
				if(e.getWorld().getName().equalsIgnoreCase(temp.getWorld().getName()))
					if(e.getLocation().distance(l) < temp.getLocation().distance(l))continue;
			e = temp;
		}

		return e;
	}

	public static Player getNearestPlayer(ArrayList<Player> players, Location l){
		if(players == null)return null;

		Iterator<Player> i = players.iterator();
		Player p = null;
		Player temp;
		while(i.hasNext()){
			temp = i.next();
			if(p != null)
				if(p.getWorld().getName().equalsIgnoreCase(temp.getWorld().getName()))
					if(p.getLocation().distance(l) < temp.getLocation().distance(l))continue;
			p = temp;
		}

		return p;
	}

	public static ArrayList<Entity> getAllVehicle(Entity e){
		ArrayList<Entity> entity = new ArrayList<Entity>();

		Entity vehicle = e.getVehicle() != null ? e.getVehicle() : null;
		while(vehicle != null){
			entity.add(vehicle);
			vehicle = vehicle.getVehicle() != null ? vehicle.getVehicle() : null;
		}

		return entity;
	}

	public static ArrayList<Entity> getAllPassenger(Entity e){
		ArrayList<Entity> entity = new ArrayList<Entity>();

		Entity passenger = e.getPassenger() != null ? e.getPassenger() : null;
		while(passenger != null){
			entity.add(passenger);
			passenger = passenger.getPassenger() != null ? passenger.getPassenger() : null;
		}
		return entity;
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void setPotionEffect(Player p, PotionEffectType effect, int second, int level){
		p.removePotionEffect(effect);
		p.addPotionEffect(new PotionEffect(effect, second*20, level));
	}

	public static void setItemDecrease(Player p){
		int i = p.getItemInHand().getAmount();
		if (i == 1) {
			p.setItemInHand(null);
		}else if (i > 1){
			p.getItemInHand().setAmount(i - 1);
		}
	}

	/*public static void setBlock(final Location l, Material m, byte data){
		Block b = l.getBlock();
		if(b.getType().equals(Material.AIR)){
			b.setType(m);
			b.setData(data);
			RaceData.addJammerBlock(b);
		}else{
			l.setY(l.getY() + 1);
			if(b.getType().equals(Material.AIR)){
				b.setType(m);
				b.setData(data);
				RaceData.addJammerBlock(b);
			}
		}
	}*/

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static Boolean isOnline(String name){
		for(Player online : Bukkit.getOnlinePlayers()){
			if(name.equalsIgnoreCase(online.getName())){
				return true;
			}
		}
		return false;
	}

	public static boolean isNumber(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (NumberFormatException e) {
			try {
				Float.parseFloat(number);
				return true;
			} catch (NumberFormatException ee) {
			    try {
			        Double.parseDouble(number);
			        return true;
			    } catch (NumberFormatException eee) {
			        return false;
			    }
			}
		}
	}

	public static boolean isLoadedChunk(Location l){
		Player player = getNearestPlayer(getNearbyPlayers(l, 100), l);
		if(player == null)return false;

		return true;
	}

	public static Boolean isSolidBlock(Location l){
		try {
			return (Boolean) Material_isSolid.invoke(Block_getMaterial.invoke(Block_getById.invoke(CraftBlock, l.getBlock().getTypeId())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Boolean isSlabBlock(Location l){
		int id = l.getBlock().getTypeId();
		if(id == 44 || id == 126 || id == 182)return true;
		return false;
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void addDamage(Entity damaged, Entity executor, int damage){
		if(!(damaged instanceof LivingEntity))return;
		if(damaged instanceof Player){
			final Player p = (Player)damaged;
			if(0 < p.getNoDamageTicks())return;
			if(!RaceManager.isRacing(p.getUniqueId()))return;

			p.playEffect(EntityEffect.HURT);

			if(1 <= p.getHealth() - damage){
				p.setHealth(p.getHealth()-damage);
			}else{
				p.setHealth(p.getMaxHealth());
				if(executor != null)
					broadcastMessage(damaged.getName() + " killed by " + executor.getName());
				else
					broadcastMessage(damaged.getName() + " is dead");

				final Race r = RaceManager.getRace(p);
				new SendBlinkingTitleTask((Player) damaged, r.getCharacter().getDeathPenaltySecond(), "DEATH PENALTY", ChatColor.RED).runTaskTimer(YPLKart.getInstance(), 0, 1);

				p.setWalkSpeed(r.getCharacter().getDeathPenaltyWalkSpeed());
				p.setNoDamageTicks(r.getCharacter().getDeathPenaltyAntiReskillSecond() * 20);
				p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 0.5F);
				Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
					public void run(){
						p.setWalkSpeed(r.getCharacter().getWalkSpeed());
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
						p.setSprinting(true);
					}
				}, r.getCharacter().getDeathPenaltySecond() * 20);
			}
		}else{
			((LivingEntity)damaged).damage(damage, executor);
		}
	}

	public static void removeEntityCollision(Entity e){
		try{
			Object craftentity = getCraftEntity(e);
			Field nmsField;
			for(Field f : craftentity.getClass().getFields()){
				if(!f.getName().equalsIgnoreCase("noclip"))continue;
				nmsField = f;
				nmsField.setAccessible(true);
				nmsField.setBoolean(craftentity, true);
				return;
			}
		}catch(Exception exception){
		}
	}

	public static void removeEntityVerticalMotion(Entity e){
		try{
			Object craftentity = getCraftEntity(e);
			Field nmsField;
			for(Field f : craftentity.getClass().getFields()){
				if(!f.getName().equalsIgnoreCase("motY"))continue;
				nmsField = f;
				nmsField.setAccessible(true);
				nmsField.setDouble(craftentity, f.getDouble(craftentity) + 0.03999999910593033D);
				return;
			}
		}catch(Exception exception){
		}
	}

	public static Location adjustBlockLocation(Location old){
		Location value = old.getBlock().getLocation();
		return new Location(old.getWorld(), value.getX()+0.5, value.getY(), value.getZ()+0.5, old.getYaw(), old.getPitch());
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	public static void sendMessage(Object adress, String message){
		Player p = null;
		if(adress instanceof Player)
			p = (Player) adress;
		else if(adress instanceof UUID)
			p = Bukkit.getPlayer((UUID) adress);

		for (String line : replaceLine(message)) {
			line = replacePatch("#Green" + line);
			line = replaceChatColor(line);

			if (p != null)
				p.sendMessage(line);
			else
				YPLKart.log.log(Level.INFO, ChatColor.stripColor(line));
		}
	}

	public static void broadcastMessage(String message){
		for(Player p : Bukkit.getOnlinePlayers()){
			sendMessage(p, message);
		}
	}

	private static String replacePatch(String text){
		text = text.replace("[header]", "#Yellow[" + YPLKart.plname + "] #Green");
		text = text.replace("{race type}", "{#Whiterace type#Green}");
		text = text.replace("{circuit name}", "{#Whitecircuit name#Green}");
		text = text.replace("{worldname}", "{#Whiteworldname#Green}");
		text = text.replace("{x}", "{#Whitex#Green}");
		text = text.replace("{y}", "{#Whitey#Green}");
		text = text.replace("{z}", "{#Whitez#Green}");
		text = text.replace("{yaw}", "{#Whiteyaw#Green}");
		text = text.replace("{pitch}", "{#Whitepitch#Green}");
		text = text.replace("{new circuitname}", "{#Whitenew circuitname#Green}");
		text = text.replace("{player name}", "{#Whiteplayer name#Green}");
		text = text.replace("{character name}", "{#Whitecharacter name#Green}");
		text = text.replace("{kart name}", "{#Whitekart name#Green}");
		text = text.replace("{item name}", "{#Whiteitem name#Green}");
		text = text.replace("{amount}", "{#Whiteamount#Green}");
		text = text.replace("{number of player}", "{#Whitenumber of player#Green}");
		text = text.replace("{number of second}", "{#Whitenumber of second#Green}");

		return text;
	}

	public static String replaceChatColor(String text){
		text = text.replace("#Black", ChatColor.BLACK.toString());
		text = text.replace("#White", ChatColor.WHITE.toString());
		text = text.replace("#Gray", ChatColor.GRAY.toString());
		text = text.replace("#DarkGray", ChatColor.DARK_GRAY.toString());
		text = text.replace("#Red", ChatColor.RED.toString());
		text = text.replace("#DarkRed", ChatColor.DARK_RED.toString());
		text = text.replace("#Green", ChatColor.GREEN.toString());
		text = text.replace("#DarkGreen", ChatColor.DARK_GREEN.toString());
		text = text.replace("#Blue", ChatColor.BLUE.toString());
		text = text.replace("#DarkBlue", ChatColor.DARK_BLUE.toString());
		text = text.replace("#Yellow", ChatColor.YELLOW.toString());
		text = text.replace("#Gold", ChatColor.GOLD.toString());
		text = text.replace("#LightPurple", ChatColor.LIGHT_PURPLE.toString());
		text = text.replace("#DarkPurple", ChatColor.DARK_PURPLE.toString());
		text = text.replace("#Aqua", ChatColor.AQUA.toString());
		text = text.replace("#DarkAqua", ChatColor.DARK_AQUA.toString());
		text = text.replace("#Magic", ChatColor.MAGIC.toString());
		return text;
	}

	public static List<String> replaceLine(String message){
		List<String> newtext = new ArrayList<String>();
		for (String line : message.split("\n")){
			newtext.add(line);
		}
		return newtext;
	}

	public static String convertSignNumber(int number){
		return 0 <= number ? "#Gold+" + String.valueOf(number) : "#Blue" + String.valueOf(number);
	}

	public static String convertSignNumber(float number){
		return 0 <= number ? "#Gold+" + String.valueOf(number) : "#Blue" + String.valueOf(number);
	}

	public static String convertSignNumber(double number){
		return 0 <= number ? "#Gold+" + String.valueOf(number) : "#Blue" + String.valueOf(number);
	}

	public static String convertSignNumberR(int number){
		String text = "";
		if(number == 0)
			text = "#Gold+" + String.valueOf(number);
		else
			text = 0 < number ? "#Blue+" + String.valueOf(number) : "#Gold" + String.valueOf(number);
		return text;
	}

	public static String convertSignNumberR(float number){
		String text = "";
		if(number == 0)
			text = "#Gold+" + String.valueOf(number);
		else
			text = 0 < number ? "#Blue+" + String.valueOf(number) : "#Gold" + String.valueOf(number);
		return text;
	}

	public static String convertSignNumberR(double number){
		String text = "";
		if(number == 0)
			text = "#Gold+" + String.valueOf(number);
		else
			text = 0 < number ? "#Blue+" + String.valueOf(number) : "#Gold" + String.valueOf(number);
		return text;
	}


	public static String convertInitialUpperString(String string){
		String initial = string.substring(0, 1).toUpperCase();
		String other = string.substring(1, string.length()).toLowerCase();

		return initial + other;
	}

	//ブロック、非生物エンティティに影響しない爆発を発生
	public static void createSafeExplosion(Player executor, Location l, int damage, int range){
		Particle.sendToLocation("CLOUD", l, 0, 0, 0, 1, 10);
		Particle.sendToLocation("SMOKE_NORMAL", l, 0, 0, 0, 1, 10);
		l.getWorld().playSound(l, Sound.EXPLODE, 0.2F, 1.0F);
		ArrayList<LivingEntity> entities = Util.getNearbyLivingEntities(l, range);
		for(LivingEntity damaged : entities){
			if(executor != null)
				if(damaged.getUniqueId() == executor.getUniqueId())continue;
			if(0 < damaged.getNoDamageTicks())continue;
			if(damaged.isDead())continue;
			if(!(damaged instanceof Player))continue;
			if(!RaceManager.isRacing(((Player)damaged).getUniqueId()))continue;

			Vector v = Util.getVectorLocationToLocation(l, damaged.getLocation());
			v.setX(v.clone().multiply(-1).getX());
			v.setY(0);
			v.setZ(v.clone().multiply(-1).getZ());
			damaged.setVelocity(v);

			addDamage(damaged, executor, damage);
		}
	}

	public static void createFlowerShower(Player p, int maxlife){
		new FlowerShowerTask(p,maxlife).runTaskTimer(YPLKart.getInstance(), 0, 1);
	}

	public static void createSignalFireworks(Location l){
		World w = l.getWorld();
		FireworkEffect effect = FireworkEffect.builder().withColor(Color.GREEN).withFade(Color.LIME).with(Type.BALL_LARGE).build();
		FireworkEffect effect2 = FireworkEffect.builder().withColor(Color.YELLOW).withFade(Color.ORANGE).with(Type.STAR).build();
		FireworkEffect effect3 = FireworkEffect.builder().withColor(Color.RED).withFade(Color.PURPLE).with(Type.CREEPER).build();
		FireworkEffect effect4 = FireworkEffect.builder().withColor(Color.AQUA).withFade(Color.BLUE).with(Type.BURST).build();

		for(int i = 0; i <= 2; i++){
			try {
				FireWorks.playFirework(w, l.clone().add(Util.getRandom(20), 5, Util.getRandom(20)), effect);
				FireWorks.playFirework(w, l.clone().add(Util.getRandom(20), 5, Util.getRandom(20)), effect2);
				FireWorks.playFirework(w, l.clone().add(Util.getRandom(20), 5, Util.getRandom(20)), effect3);
				FireWorks.playFirework(w, l.clone().add(Util.getRandom(20), 5, Util.getRandom(20)), effect4);
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
}