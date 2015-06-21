package com.github.erozabesu.yplkart.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Object.RaceManager;
import com.github.erozabesu.yplkart.Task.PlayerLookingUpdateTask;

public class PacketUtil extends ReflectionUtil{
	/*プレイヤーの見た目を選択キャラクターに偽装するタスクを起動します
	 *タスクを起動するタイミングは基本的にプレイヤーを描画し直さなければならない場合だと思われます
	 *・キャラクター選択をしたとき : RaceManager.character()
	 *・サーバーに参加したとき : EventData.onJoin
	 *・リスポーンしたとき : EventData.onRespawn
	 *・テレポートしたとき : EventData.onTeleport
	 *・乗り物に搭乗したとき : EventData.onVehicleEnter
	 * わざわざタスクを起動している理由ですが
	 * 見た目を偽装されたプレイヤーとの距離が140ブロック程度離れた後、再び140ブロック以内に近づいた際に見た目がプレイヤーに戻ってしまうためです。
	 * タスクでは140ブロック以内に新規に進入したプレイヤーを監視しパケットを再送しています。
	 */
	public static void runPlayerLookingUpdate(Player p){
		RaceManager.getRace(p).setPlayerLookingUpdateTask(
			new PlayerLookingUpdateTask(p.getUniqueId().toString(), RaceManager.getRace(p).getCharacter()).runTaskTimer(YPLKart.getInstance(), 0, 5)
		);
	}

	public static void disguise(Player p, Player target, EnumCharacter job){
		if(job == null){
			return;
		}
		if(job.getCraftClassName().equalsIgnoreCase(EnumCharacter.Human.getCraftClassName())){
			returnPlayer(p);
			return;
		}
		try {
			Object craftentity = getCraftEntityClass(p.getWorld(), job.getCraftClassName());

			Location loc = p.getLocation();

			Method getBukkitEntity = craftentity.getClass().getMethod("getBukkitEntity");
			Method setLocation = craftentity.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
			Method d = craftentity.getClass().getMethod("d", int.class);

			LivingEntity bukkitentity = (LivingEntity) getBukkitEntity.invoke(craftentity);
			bukkitentity.setCustomName(p.getName());
			bukkitentity.setCustomNameVisible(true);

			setLocation.invoke(craftentity, loc.getX(), loc.getY(), loc.getZ(), 0, 0);
			d.invoke(craftentity, p.getEntityId());

			Object entitydestroypacket = getEntityDestroyPacket(p.getEntityId());
			Object spawnentitypacket = getSpawnEntityLivingPacket(craftentity);
			Object handpacket = p.getItemInHand() == null ? null : getEquipmentPacket(p, 0, p.getItemInHand());
			Object helmetpacket = p.getEquipment().getHelmet() == null ? null : getEquipmentPacket(p, 4, p.getEquipment().getHelmet());
			Object chectpacket = p.getEquipment().getChestplate() == null ? null : getEquipmentPacket(p, 3, p.getEquipment().getChestplate());
			Object leggingspacket = p.getEquipment().getLeggings() == null ? null : getEquipmentPacket(p, 2, p.getEquipment().getLeggings());
			Object bootspacket = p.getEquipment().getBoots() == null ? null : getEquipmentPacket(p, 1, p.getEquipment().getBoots());

			if(target == null){
				for(Player other : Bukkit.getOnlinePlayers()){
					if(other.getUniqueId() == p.getUniqueId())continue;
					sendPacket(other, entitydestroypacket);
					sendPacket(other, spawnentitypacket);
					if(handpacket != null)sendPacket(other, handpacket);
					if(helmetpacket != null)sendPacket(other, helmetpacket);
					if(chectpacket != null)sendPacket(other, chectpacket);
					if(leggingspacket != null)sendPacket(other, leggingspacket);
					if(bootspacket != null)sendPacket(other, bootspacket);
				}
			}else{
				sendPacket(target, entitydestroypacket);
				sendPacket(target, spawnentitypacket);
				if(handpacket != null)sendPacket(target, handpacket);
				if(helmetpacket != null)sendPacket(target, helmetpacket);
				if(chectpacket != null)sendPacket(target, chectpacket);
				if(leggingspacket != null)sendPacket(target, leggingspacket);
				if(bootspacket != null)sendPacket(target, bootspacket);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void returnPlayer(final Player p){
		try {
			Object craftentity = getCraftEntity(p);

			Object entitydestroypacket = getEntityDestroyPacket(p.getEntityId());
			Object spawnnamedentitypacket = getSpawnNamedEntityPacket(craftentity);
			Object handpacket = p.getItemInHand() == null ? null : getEquipmentPacket(p, 0, p.getItemInHand());
			Object helmetpacket = p.getEquipment().getHelmet() == null ? null : getEquipmentPacket(p, 4, p.getEquipment().getHelmet());
			Object chectpacket = p.getEquipment().getChestplate() == null ? null : getEquipmentPacket(p, 3, p.getEquipment().getChestplate());
			Object leggingspacket = p.getEquipment().getLeggings() == null ? null : getEquipmentPacket(p, 2, p.getEquipment().getLeggings());
			Object bootspacket = p.getEquipment().getBoots() == null ? null : getEquipmentPacket(p, 1, p.getEquipment().getBoots());

			Method getBukkitEntity = craftentity.getClass().getMethod("getBukkitEntity");
			LivingEntity bukkitentity = (LivingEntity) getBukkitEntity.invoke(craftentity);
			bukkitentity.setCustomName(p.getName());
			bukkitentity.setCustomNameVisible(true);

			for(Player other : Bukkit.getOnlinePlayers()){
				if(other.getUniqueId() == p.getUniqueId())continue;
				sendPacket(other, entitydestroypacket);
				sendPacket(other, spawnnamedentitypacket);
				if(handpacket != null)sendPacket(other, handpacket);
				if(helmetpacket != null)sendPacket(other, helmetpacket);
				if(chectpacket != null)sendPacket(other, chectpacket);
				if(leggingspacket != null)sendPacket(other, leggingspacket);
				if(bootspacket != null)sendPacket(other, bootspacket);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void sendTitle(Player p, String text, int fadein, int length, int fadeout, ChatColor color, boolean issubtitle){
		try {
			Object titlesendpacket = getTitlePacket(text, color, issubtitle);
			Object titlelengthpacket = getTitleLengthPacket(fadein, length, fadeout);

			sendPacket(p, titlesendpacket);
			sendPacket(p, titlelengthpacket);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	//PlayerDeathEventで呼び出すとリスポーンウィンドウをスキップできます
	public static void skipRespawnScreen(Player p){
		try {
			Object playerConnection = getPlayerConnection(p);
			for (Method m : playerConnection.getClass().getMethods()) {
				if (m.getName().equalsIgnoreCase("a")){
					for(Class<?> c : m.getParameterTypes()){
						if(c.getName().contains("PacketPlayInClientCommand")){
							m.invoke(playerConnection, getClientCommandPacket());
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

	private static Field getPlayerConnectionField(Player p) throws Exception{
		return getField(ReflectionUtil.getCraftEntity(p), "playerConnection");
	}

	private static Object getPlayerConnection(Player p) throws Exception{
		return getPlayerConnectionField(p).get(ReflectionUtil.getCraftEntity(p));
	}

	private static void sendPacket(Player p, Object packet) throws Exception {
		Object playerConnection = getPlayerConnection(p);
		for (Method m : playerConnection.getClass().getMethods()) {
			if (m.getName().equalsIgnoreCase("sendPacket")) {
				m.invoke(playerConnection, packet);
				return;
			}
		}
	}

	// itemslot: 0-hand / 4-head / 3-chest / 2-leggings / 1-boots
	private static Object getEquipmentPacket(Player p, int itemslot, ItemStack equipment) throws Exception{
		Class<?> packetclass = getBukkitClass("PacketPlayOutEntityEquipment");
		Class<?> itemstackclass = getBukkitClass("ItemStack");
		Object packet = packetclass.getConstructor(int.class, int.class, itemstackclass).newInstance(p.getEntityId(), itemslot, getCraftItemStack(equipment));

		return packet;
	}

	private static Object getTitlePacket(String text, ChatColor color, boolean issubtitle) throws Exception{
		Class<?> chatserializer = null;
		Class<?> enumtitleaction = null;

		if(getBukkitVersion().equalsIgnoreCase("v1_8_R1")){
			chatserializer = getBukkitClass("ChatSerializer");
			enumtitleaction = getBukkitClass("EnumTitleAction");
		}else{
			chatserializer = getBukkitClass("IChatBaseComponent$ChatSerializer");
			enumtitleaction = getBukkitClass("PacketPlayOutTitle$EnumTitleAction");
		}

		Class<?> ichatbasecomponent = getBukkitClass("IChatBaseComponent");
		Object title = chatserializer.getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

		Object perform_title;
		if(issubtitle)
			perform_title = enumtitleaction.getEnumConstants()[1];
		else
			perform_title = enumtitleaction.getEnumConstants()[0];

		Class<?> packetplayouttitle = getBukkitClass("PacketPlayOutTitle");
		Object con = packetplayouttitle.getConstructor(enumtitleaction, ichatbasecomponent).newInstance(perform_title, title);

		return con;
	}

	private static Object getTitleLengthPacket(int fadein, int length, int fadeout) throws Exception{
		Class<?> packetplayouttitle = getBukkitClass("PacketPlayOutTitle");
		Object con = packetplayouttitle.getConstructor(int.class, int.class, int.class).newInstance(fadein, length, fadeout);

		return con;
	}

	private static Object getClientCommandPacket() throws Exception{
		Class<?> enumclientcommand;

		if(getBukkitVersion().equalsIgnoreCase("v1_8_R1")){
			enumclientcommand = getBukkitClass("EnumClientCommand");
		}else{
			enumclientcommand = getBukkitClass("PacketPlayInClientCommand$EnumClientCommand");
		}

		Object perform_respawn = enumclientcommand.getEnumConstants()[0];

		Class<?> clazz = getBukkitClass("PacketPlayInClientCommand");
		Object con = clazz.getConstructor(enumclientcommand).newInstance(perform_respawn);
		return con;
	}

	private static Object getEntityDestroyPacket(int id) throws Exception{
		int[] ids = new int[1];
		ids[0] = id;
		Class<?> clazz = getBukkitClass("PacketPlayOutEntityDestroy");
		Object con = clazz.getConstructor(int[].class).newInstance(ids);

		return con;
	}

	private static Object getSpawnEntityLivingPacket(Object craftentity) throws Exception{
		Class<?> clazz = getBukkitClass("PacketPlayOutSpawnEntityLiving");
		Object con = clazz.getConstructor(getBukkitClass("EntityLiving")).newInstance(craftentity);

		return con;
	}

	private static Object getAttachEntityPacket(Object vehicle, Object passenger) throws Exception{
		Class<?> clazz = getBukkitClass("PacketPlayOutAttachEntity");
		Object con = clazz.getConstructor(int.class, getBukkitClass("Entity"), getBukkitClass("Entity")).newInstance(0, passenger, vehicle);

		return con;
	}

	private static Object getSpawnNamedEntityPacket(Object craftplayer) throws Exception{
		Class<?> clazz = getBukkitClass("PacketPlayOutNamedEntitySpawn");
		Object con = clazz.getConstructor(getBukkitClass(EnumCharacter.Human.getCraftClassName())).newInstance(craftplayer);

		return con;
	}

	/*public static void attachEntity(Entity vehicle, Entity passenger, Player target){
		try {
			Object nmsPassenger = getCraftEntity(passenger);
			Object nmsVehicle = getCraftEntity(vehicle);
			Object packetPassenger = getSpawnNamedEntityPacket(nmsPassenger);
			Object packetVehicle = getAttachEntityPacket(nmsVehicle, nmsPassenger);

			if(target == null){
				for(Player other : Bukkit.getOnlinePlayers()){
					sendPacket(other, packetPassenger);
					sendPacket(other, packetVehicle);
				}
			}else{
				sendPacket(target, packetPassenger);
				sendPacket(target, packetVehicle);
			}
		} catch (Exception exception){
			exception.printStackTrace();
		}
	}

	public static void resendDisguisePacket(Player p){
		disguise(p, null, RaceManager.getRace(p).getCharacter());

		for(Player other : Bukkit.getOnlinePlayers()){
			if(!other.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString()))
				if(RaceManager.getRace(other).getCharacter() != null)
					disguise(other, p, RaceManager.getRace(other).getCharacter());
		}
	}

	public static void resendOtherPlayerPacket(Player p){
		for(Player other : Bukkit.getOnlinePlayers()){
			if(!other.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString()))
				if(RaceManager.getRace(other).getCharacter() != null)
					disguise(other, p, RaceManager.getRace(other).getCharacter());
		}
	}

	public static void disguiseUnLivingEntity(Entity e, Player target, String disguiseEntityClass, int disguiseEntityID){
		try {
			Object craftentity = getCraftEntityClass(e, disguiseEntityClass);

			Location loc = e.getLocation();

			Method setLocation = craftentity.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
			//Method d = craftentity.getClass().getMethod("d", int.class);

			setLocation.invoke(craftentity, loc.getX(), loc.getY(), loc.getZ(), 0, 0);
			//d.invoke(craftentity, e.getEntityId());

			Object entitydestroypacket = getEntityDestroyPacket(e.getEntityId());
			Object spawnentitypacket = getSpawnEntityPacket(craftentity, 45);
			Object entitymetadatapacket = getEntityMetadataPacket(craftentity, 45);

			 try {
		            Field a = spawnentitypacket.getClass().getDeclaredField("a");
		            a.setAccessible(true);
		            a.set(spawnentitypacket, 45);
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }

			if(target == null){
				for(Player other : Bukkit.getOnlinePlayers()){
					sendPacket(other, entitydestroypacket);
					sendPacket(other, spawnentitypacket);
					sendPacket(other, entitymetadatapacket);
				}
			}else{
				sendPacket(target, entitydestroypacket);
				sendPacket(target, spawnentitypacket);
				sendPacket(target, entitymetadatapacket);
			}
		} catch (Exception exception){
			exception.printStackTrace();
		}
	}

		public static void disguiseItemEntity(Entity e, Player target, ItemStack itemstack){
		try {
			Object nmsItemStack = getEntityItem(e.getLocation(), itemstack);
			Object packetItemSpawn = getSpawnItemEntityPacket(nmsItemStack);
			//Object entitydestroypacket = getEntityDestroyPacket(e.getEntityId());


			if(target == null){
				for(Player other : Bukkit.getOnlinePlayers()){
					//sendPacket(other, entitydestroypacket);
					sendPacket(other, packetItemSpawn);
				}
			}else{
				//sendPacket(target, entitydestroypacket);
				sendPacket(target, packetItemSpawn);
			}
		} catch (Exception exception){
			exception.printStackTrace();
		}
	}

		public static Object getEntityItem(Location l, ItemStack itemstack) throws Exception{
		Object craftWorld = getCraftWorld(l.getWorld());

		Class<?> entityItemClass = getBukkitClass("EntityItem");
		Object entityItem = entityItemClass.getConstructor(getBukkitClass("World")).newInstance(craftWorld);

		Class<?> nmsItemStackClass = getCraftClass("inventory.CraftItemStack");
		Object nmsItemStack = nmsItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(null, itemstack);

		entityItemClass.getMethod("setItemStack", getBukkitClass("ItemStack")).invoke(entityItem, nmsItemStack);
		entityItemClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityItem, l.getX(), l.getY(), l.getZ(), 0, 0);

		return entityItem;
	}

		private static Object getSpawnItemEntityPacket(Object craftentity) throws Exception{
		Class<?> clazz = getBukkitClass("PacketPlayOutSpawnEntity");
		Object con = clazz.getConstructor(getBukkitClass("Entity"), int.class, int.class).newInstance(craftentity, 2, 100);

		return con;
	}

		private static Object getSpawnEntityPacket(Object craftentity, int id) throws Exception{
		Class<?> clazz = getBukkitClass("PacketPlayOutSpawnEntity");
		Object con = clazz.getConstructor(getBukkitClass("Entity"), int.class).newInstance(craftentity, id);

		return con;
	}

	private static Object getEntityMetadataPacket(Object craftentity, int id) throws Exception{
		Class<?> clazz = getBukkitClass("PacketPlayOutEntityMetadata");
		Object con = clazz.getConstructor(int.class ,getBukkitClass("DataWatcher"), boolean.class).newInstance(id, craftentity.getClass().getMethod("getDataWatcher").invoke(craftentity), true);

		return con;
	}
	 */
}
