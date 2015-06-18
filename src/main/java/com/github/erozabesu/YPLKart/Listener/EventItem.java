package main.java.com.github.erozabesu.YPLKart.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import main.java.com.github.erozabesu.YPLKart.YPLKart;
import main.java.com.github.erozabesu.YPLKart.Data.Settings;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumCharacter;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumItem;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumKarts;
import main.java.com.github.erozabesu.YPLKart.Enum.Permission;
import main.java.com.github.erozabesu.YPLKart.Object.Race;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;
import main.java.com.github.erozabesu.YPLKart.Task.ItemBanana;
import main.java.com.github.erozabesu.YPLKart.Task.ItemDyedTurtle;
import main.java.com.github.erozabesu.YPLKart.Task.ItemStar;
import main.java.com.github.erozabesu.YPLKart.Task.ItemTurtle;
import main.java.com.github.erozabesu.YPLKart.Task.SendCountDownTitle;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventItem extends RaceManager implements Listener{

	private static YPLKart pl;
	public EventItem(YPLKart plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		pl = plugin;
	}

	private static String ItemBoxName = "ItemBox";
	private static String ItemBoxNameTier2 = ChatColor.GOLD + ItemBoxName + "Tier2";
	private static String ItemBoxNameFake = ChatColor.GOLD + ItemBoxName + "！！";
	private static String FakeItemBoxName = ChatColor.GOLD + ItemBoxName + "！";
	private static HashMap<Player, Boolean> boostRailCool = new HashMap<Player, Boolean>();
	private static HashMap<Player, Boolean> vectorRailCool = new HashMap<Player, Boolean>();
	private static HashMap<Player, Boolean> itemboxCool = new HashMap<Player, Boolean>();

	@EventHandler
	public void useItem(PlayerInteractEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)return;

		final Player p = e.getPlayer();
		if(EnumItem.CheckPoint.isSimilar(p.getItemInHand())){
			e.setCancelled(true);
			if(Permission.hasPermission(p, Permission.op_cmd_circuit, false)){
				try {
					createCustomWitherSkull(p.getLocation(), p.getItemInHand().getItemMeta().getLore().get(0));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				/*Snowball projectile = p.launchProjectile(Snowball.class);
				Vector v = projectile.getVelocity();
				if(p.isOnGround()){
					v.setX(0);
					v.setY(-10);
					v.setZ(0);
					projectile.setVelocity(v);
				}
				projectile.setCustomName(ChatColor.GOLD + p.getItemInHand().getItemMeta().getLore().get(0));
				projectile.setCustomNameVisible(true);*/
			}
		}else if(EnumItem.ItemBox.isSimilar(p.getItemInHand())){
			e.setCancelled(true);
			if(Permission.hasPermission(p, Permission.op_cmd_itemboxtool, false)){
				if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
					Block b = e.getClickedBlock().getRelative(e.getBlockFace());
					EnderCrystal endercrystal = b.getWorld().spawn(b.getLocation().add(0.5,0,0.5), EnderCrystal.class);
					endercrystal.setCustomName(ItemBoxName);
					endercrystal.setCustomNameVisible(false);
					b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
				}
			}
		}else if(EnumItem.ItemBoxTier2.isSimilar(p.getItemInHand())){
			e.setCancelled(true);
			if(Permission.hasPermission(p, Permission.op_cmd_itemboxtool, false)){
				if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
					Block b = e.getClickedBlock().getRelative(e.getBlockFace());
					EnderCrystal endercrystal = b.getWorld().spawn(b.getLocation().add(0.5,0,0.5), EnderCrystal.class);
					endercrystal.setCustomName(ItemBoxNameTier2);
					endercrystal.setCustomNameVisible(true);
					b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
				}
			}
		}else if(EnumItem.ItemBoxFake.isSimilar(p.getItemInHand())){
			e.setCancelled(true);
			if(Permission.hasPermission(p, Permission.op_cmd_itemboxtool, false)){
				if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
					Block b = e.getClickedBlock().getRelative(e.getBlockFace());
					EnderCrystal endercrystal = b.getWorld().spawn(b.getLocation().add(0.5,0,0.5), EnderCrystal.class);
					endercrystal.setCustomName(ItemBoxNameFake);
					endercrystal.setCustomNameVisible(true);
					b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
				}
			}
		}
		if(isEntry(p)){
			if(EnumItem.Mushroom.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_mushroom, false)){
					Util.setItemDecrease(p);
					setPositiveItemSpeed(p, Settings.MushroomEffectSecond, Settings.MushroomEffectLevel, Sound.EXPLODE);
				}
			}else if(EnumItem.PowerfullMushroom.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_powerfullmushroom, false)){
					Util.setItemDecrease(p);
					setPositiveItemSpeed(p, Settings.PowerfullMushroomEffectSecond, Settings.PowerfullMushroomEffectLevel, Sound.EXPLODE);
					p.getWorld().playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
				}
			}else if(EnumItem.Banana.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_banana, false)){
					Util.setItemDecrease(p);
					Location l = Util.getLocationfromYaw(p.getLocation().add(0,0.5,0), -5).getBlock().getLocation().add(0.5,0,0.5);

					FallingBlock b = p.getWorld().spawnFallingBlock(l, Material.HUGE_MUSHROOM_1, (byte)8);
					b.setCustomName(EnumItem.Banana.getName());
					b.setCustomNameVisible(false);
					b.setDropItem(false);

					new ItemBanana(b, l).runTaskTimer(pl, 0, 1);
					p.getWorld().playSound(p.getLocation(), Sound.SLIME_WALK, 1.0F, 1.0F);
				}
			}else if(EnumItem.FakeItembox.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_fakeitembox, false)){
					Util.setItemDecrease(p);
					EnderCrystal endercrystal = p.getWorld().spawn(Util.getLocationfromYaw(p.getLocation().add(0,0.5,0), -5).getBlock().getLocation().add(0.5,0,0.5), EnderCrystal.class);
					endercrystal.setCustomName(FakeItemBoxName);
					endercrystal.setCustomNameVisible(true);
					addJammerEntity(endercrystal);
					p.getWorld().playEffect(p.getLocation(), Effect.CLICK1, 0);
				}
			}else if(EnumItem.Thunder.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_thunder, false))
					itemThunder(p);
			}else if(EnumItem.Star.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_star, false))
					new ItemStar(p).runTaskTimer(pl, 0, 1);
			}else if(EnumItem.Teresa.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_teresa, false))
					itemTeresa(p);
			}else if(EnumItem.Turtle.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_turtle, false))
					itemTurtle(p);
			}else if(EnumItem.RedTurtle.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_redturtle, false))
					itemRedturtle(p);
			}else if(EnumItem.ThornedTurtle.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_thornedturtle, false))
					itemThornedturtle(p);
			}else if(EnumItem.Gesso.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_gesso, false))
					itemGesso(p);
			}else if(EnumItem.Killer.isSimilar(p.getItemInHand())){
				e.setCancelled(true);
				if(Permission.hasPermission(p, Permission.use_killer, false)){
					itemKiller(p);
				}
			}
		}
	}

	@EventHandler
	public void onInteractObjectEntity(PlayerMoveEvent e){
		if(!Settings.isEnable(e.getFrom().getWorld()))return;
		final Player p = e.getPlayer();
		if(!isEntry(p))return;

		List<Entity> entities = p.getNearbyEntities(0.7, 2, 0.7);

		for(final Entity entity : entities){
			if(entity.getCustomName() != null){
				if(entity instanceof FallingBlock){
					if(entity.getCustomName().equalsIgnoreCase(EnumItem.Banana.getName())){
						if(Permission.hasPermission(p, Permission.interact_banana, false)){
							RaceManager.removeJammerEntity(entity);
							entity.remove();

							if(p.getNoDamageTicks()==0){
								Util.sendMessage(p, "バナナに引っ掛かった！");
								setNegativeItemSpeed(p, Settings.BananaEffectSecond, Settings.BananaEffectLevel, Sound.SLIME_WALK);
							}
						}
					}
				}else if(entity instanceof EnderCrystal){
					//偽アイテムボックス
					if(entity.getCustomName().equalsIgnoreCase(FakeItemBoxName)){
						if(Permission.hasPermission(p, Permission.interact_fakeitembox, false)){
							entity.remove();

							if(p.getNoDamageTicks()==0){
								Util.sendMessage(p, "偽アイテムブロックだ！");
								Util.createSafeExplosion(null, p.getLocation(), Settings.FakeItemBoxHitDamage, 1);
							}
						}
					//偽アイテムボックス復活するタイプ
					}else if(entity.getCustomName().equalsIgnoreCase(ItemBoxNameFake)){
						if(Permission.hasPermission(p, Permission.interact_fakeitembox, false)){
							entity.remove();

							pl.getServer().getScheduler().runTaskLater(pl,new Runnable() {
								public void run() {
									EnderCrystal endercrystal = entity.getWorld().spawn(entity.getLocation(), EnderCrystal.class);
									endercrystal.setCustomName(ItemBoxNameFake);
									endercrystal.setCustomNameVisible(true);
								}
							}, 2 * 20L + 10L);

							if(p.getNoDamageTicks()==0){
								Util.sendMessage(p, "偽アイテムブロックだ！");
								Util.createSafeExplosion(null, p.getLocation(), Settings.FakeItemBoxHitDamage, 1);
							}
						}
					}else if(entity.getCustomName().contains(ItemBoxName)){
						if(Permission.hasPermission(p, Permission.interact_itembox, false)){
							if(itemboxCool.get(p) == null)itemboxCool.put(p, false);
							if(!itemboxCool.get(p)){
								entity.remove();
								Bukkit.getScheduler().runTaskLater(pl,new Runnable() {
									public void run() {
										EnderCrystal endercrystal = entity.getWorld().spawn(entity.getLocation(), EnderCrystal.class);
										if(entity.getCustomName().equalsIgnoreCase(ItemBoxNameTier2)){
											endercrystal.setCustomName(ItemBoxNameTier2);
											endercrystal.setCustomNameVisible(true);
										}else{
											endercrystal.setCustomName(ItemBoxName);
											endercrystal.setCustomNameVisible(false);
										}
									}
								}, 2 * 20L + 10L);

								int denominator = getEntryPlayer().size();
								denominator = denominator + getGoalPlayer().size();
								if(denominator == 0)denominator = 1;
								int rank = getRank(p);
								if(rank == 0)rank = 1;

								int percent = 100/(denominator/rank);
								int tier = 0;

								if(percent < Settings.Tier1)
									tier = 1;
								else if(Settings.Tier1 <= percent && percent < Settings.Tier2)
									tier = 2;
								else if(Settings.Tier2 <= percent && percent < Settings.Tier3)
									tier = 3;
								else
									tier = 4;

								if(entity.getCustomName().equalsIgnoreCase(ItemBoxNameTier2))
									if(tier < 4)
										tier++;

								Inventory i = p.getInventory();
								EnumItem.addItem(p, EnumItem.getRandomItemfromTier(tier));
								Util.sendMessage(p, "アイテムゲット！");
								p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1.0F, 2.0F);
								itemboxCool.put(p, true);
								pl.getServer().getScheduler().runTaskLater(pl,new Runnable() {
									public void run() {
										itemboxCool.put(p, false);
									}
								}, 30L);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onStepBoostRail(PlayerMoveEvent e){
		if(!Settings.isEnable(e.getFrom().getWorld()))return;
		if(!isEntry(e.getPlayer()))return;

		final Player p = e.getPlayer();

		if (Util.getStepMaterial(p.getLocation()) == Material.PISTON_BASE || Util.getStepMaterial(p.getLocation()) == Material.PISTON_STICKY_BASE){
			if(Permission.hasPermission(p, Permission.interact_boostrail, false)){
				if(boostRailCool.get(p) != null)
					if(boostRailCool.get(p))return;
				Bukkit.getServer().getScheduler().runTaskLater(YPLKart.getInstance(),new Runnable() {
					public void run() {
						boostRailCool.put(p, false);
					}
				}, 10L);

				if(p.getVehicle() != null){
					if(RaceManager.isCustomMinecart(p.getVehicle())){
						RaceManager.getRace(p).setStepDashBoard();
						p.playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5F, 1.0F);
						return;
					}
				}

				setPositiveItemSpeed(p, Settings.BoostRailEffectSecond, Settings.BoostRailEffectLevel, Sound.EXPLODE);
				p.playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5F, 1.0F);
				boostRailCool.put(p, true);
			}
		}
	}

	@EventHandler
	public void onItemBoxDamaged(EntityDamageByEntityEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(!(e.getEntity() instanceof EnderCrystal))return;
		if(e.getEntity().getCustomName() == null)return;
		if(!(e.getDamager() instanceof Player))return;
		Player p = (Player)e.getDamager();
		if(p.getItemInHand() == null)return;
		if(EnumItem.ItemBox.isSimilar(p.getItemInHand())){
			if(e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxName)){
				e.getEntity().remove();
				Util.sendMessage(p, e.getEntity().getCustomName() + "を削除しました");
				e.setCancelled(true);
			}
		}else if(EnumItem.ItemBoxTier2.isSimilar(p.getItemInHand())){
			if(e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxNameTier2)){
				e.getEntity().remove();
				Util.sendMessage(p, e.getEntity().getCustomName() + "を削除しました");
				e.setCancelled(true);
			}
		}else if(EnumItem.ItemBoxFake.isSimilar(p.getItemInHand())){
			if(e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxNameFake) || e.getEntity().getCustomName().equalsIgnoreCase(FakeItemBoxName)){
				e.getEntity().remove();
				Util.sendMessage(p, e.getEntity().getCustomName() + "を削除しました");
				e.setCancelled(true);
			}
		}
	}

//〓

	@EventHandler
	public void onInteractwithDye(PlayerInteractEntityEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		if(!(e.getRightClicked() instanceof Sheep))return;
		if(EnumItem.isKeyItem(e.getPlayer().getItemInHand())){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJammerObjectDamaged(EntityInteractEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(e.getEntity().getCustomName() == null)return;

		Entity entity = e.getEntity();
		if(entity instanceof EnderCrystal){
			if(entity.getCustomName().contains(ItemBoxName))
				e.setCancelled(true);
		}else if(e.getEntity() instanceof FallingBlock){
			if(entity.getCustomName().contains(EnumItem.Banana.getName()))
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJammerObjectDamaged(EntityDamageEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(e.getEntity().getCustomName() == null)return;

		Entity entity = e.getEntity();
		if(entity instanceof EnderCrystal){
			if(entity.getCustomName().contains(ItemBoxName))
				e.setCancelled(true);
		}else if(e.getEntity() instanceof FallingBlock){
			if(entity.getCustomName().contains(EnumItem.Banana.getName()))
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJammerObjectExplode(EntityExplodeEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(e.getEntity().getCustomName() == null)return;

		Entity entity = e.getEntity();
		if(entity instanceof EnderCrystal){
			if(entity.getCustomName().contains(ItemBoxName))
				e.setCancelled(true);
		}else if(e.getEntity() instanceof FallingBlock){
			if(entity.getCustomName().contains(EnumItem.Banana.getName()))
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJammerObjectExplosionPrime(ExplosionPrimeEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(e.getEntity().getCustomName() == null)return;

		Entity entity = e.getEntity();
		if(entity instanceof EnderCrystal){
			if(entity.getCustomName().contains(ItemBoxName))
				e.setCancelled(true);
		}else if(e.getEntity() instanceof FallingBlock){
			if(entity.getCustomName().contains(EnumItem.Banana.getName()))
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFall(EntityChangeBlockEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(!(e.getEntity() instanceof FallingBlock))return;
		if(e.getEntity().getCustomName() == null)return;
		if(e.getEntity().getCustomName().equalsIgnoreCase(EnumItem.RedTurtle.getName()) || e.getEntity().getCustomName().equalsIgnoreCase(EnumItem.ThornedTurtle.getName()) || e.getEntity().getCustomName().equalsIgnoreCase(EnumItem.Turtle.getName()) || e.getEntity().getCustomName().equalsIgnoreCase(EnumItem.Banana.getName()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		if(!isEntry(e.getPlayer()))return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent e){
		if(!Settings.isEnable(e.getPlayer().getWorld()))return;
		if(!isEntry(e.getPlayer()))return;
		e.getItemDrop().remove();
	}

	@EventHandler
	public void onSuffocated(EntityDamageEvent e){
		if(!Settings.isEnable(e.getEntity().getWorld()))return;
		if(!(e.getEntity() instanceof Player))return;
		Player p = (Player) e.getEntity();
		if(!isEntry(p))return;

		if(getRace(p).getUsingKiller())
			e.setCancelled(true);
	}

	public void itemTeresa(Player user){
		ArrayList<Player> entry = getEntryPlayer();
		if(entry.size() <= 1){
			Util.sendMessage(user, "レース参加者が居ないため使用できません");
			return;
		}
		Util.setItemDecrease(user);
		entry.remove(user);
		Player target = entry.get(new Random().nextInt(entry.size()));

		Inventory targetinv = target.getInventory();

		ArrayList<ItemStack> targetitem = new ArrayList<ItemStack>();
		for(int i = 0;i < Settings.ItemSlot + getRace(target).getCharacter().getItemAdjustMaxSlotSize();i++){
			if(targetinv.getItem(i) != null)
				if(EnumItem.isKeyItem(targetinv.getItem(i)))
					targetitem.add(targetinv.getItem(i));
		}

		if(targetitem.isEmpty() || 0 < target.getNoDamageTicks()){
			Util.sendMessage(user, "アイテムを持っていなかった！");
			user.playSound(user.getLocation(), Sound.BURP, 1.0F, 0.5F);
		}else{
			ItemStack levy = targetitem.get(new Random().nextInt(targetitem.size()));
			targetinv.remove(levy);
			Util.sendMessage(user, "ライバルから" + levy.getItemMeta().getDisplayName() + "を盗んだ！");
			user.playSound(user.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
			Util.sendMessage(target, levy.getItemMeta().getDisplayName() + "を盗まれた！");
			target.playSound(user.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
			EnumItem.addItem(user, levy);
			user.updateInventory();
			target.updateInventory();
		}

		int effectsecond = (Settings.TeresaEffectSecond + getRace(user).getCharacter().getItemAdjustPositiveEffectSecond()) * 20;
		user.setNoDamageTicks(effectsecond);
		user.removePotionEffect(PotionEffectType.INVISIBILITY);
		user.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, effectsecond, 1));
	}

	public void itemThunder(Player user){
		if(getEntryPlayer().size() <= 1){
			Util.sendMessage(user, "レース参加者が居ないため使用できません");
			return;
		}
		Util.setItemDecrease(user);
		final World w = user.getWorld();

		int launchdamage = Settings.ThunderHitDamage + getRace(user).getCharacter().getItemAdjustAttackDamage();
		for (final Player p : getEntryPlayer()){
			if(p.getUniqueId() == user.getUniqueId())continue;
			if(p.getNoDamageTicks() != 0)continue;

			p.removePotionEffect(PotionEffectType.SLOW);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (Settings.ThunderEffectSecond + getRace(p).getCharacter().getItemAdjustNegativeEffectSecond()) * 20, Settings.ThunderEffectLevel + getRace(p).getCharacter().getItemAdjustNegativeEffectLevel()));

			final Location loc = p.getLocation();
			w.strikeLightningEffect(loc);
			Util.createSafeExplosion(user, loc, launchdamage, 5);
			p.playSound(loc, Sound.SUCCESSFUL_HIT, 0.5F, 1.0F);
			p.playSound(loc, Sound.LEVEL_UP, 0.5F, -1.0F);
		}
	}

	public void itemGesso(Player user){
		if(getEntryPlayer().size() <= 1){
			Util.sendMessage(user, "レース参加者が居ないため使用できません");
			return;
		}
		ArrayList<Player> target = new ArrayList<Player>();
		int rank = getRank(user);

		for(Player entry : getEntryPlayer()){
			if(getRank(entry) < rank)
				target.add(entry);
		}
		if(target.isEmpty()){
			Util.sendMessage(user, "自分より上位のプレイヤーが居ないため使用できません");
			return;
		}

		Util.setItemDecrease(user);
		final World w = user.getWorld();

		for (Player p : target){
			if(p.getUniqueId() == user.getUniqueId())continue;
			if(p.getNoDamageTicks() != 0)continue;

			p.removePotionEffect(PotionEffectType.BLINDNESS);
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (Settings.GessoEffectSecond + getRace(p).getCharacter().getItemAdjustNegativeEffectSecond()) * 20, Settings.GessoEffectLevel + getRace(p).getCharacter().getItemAdjustNegativeEffectLevel()));

			p.playSound(p.getLocation(), Sound.SLIME_WALK, 2.0F, 1.0F);
		}
	}

	public void itemTurtle(Player p){
		if(!isEntry(p))return;
		if(getRank(p) == 0)return;

		Util.setItemDecrease(p);
		FallingBlock b = p.getWorld().spawnFallingBlock(p.getLocation(), Material.HUGE_MUSHROOM_1, (byte) 7);
		b.setCustomName(EnumItem.Turtle.getName());
		b.setCustomNameVisible(false);
		b.setDropItem(false);

		new ItemTurtle(p, b, Util.getLocationfromYaw(p.getLocation(), 3), 60).runTaskTimer(pl, 0, 1);
	}

	public void itemRedturtle(Player p){
		if(!isEntry(p))return;
		if(getEntryPlayer().size() < 2){
			Util.sendMessage(p, "レース参加者が居ないため使用できません");
			return;
		}
		if(getRank(p) == 0)return;
		if(!hasNearbyUnpassedCheckpoint(p)){
			Util.sendMessage(p, "#Red周囲に未通過のチェックポイントがないため使用できません");
			return;
		}

		Util.setItemDecrease(p);

		FallingBlock turtle = p.getWorld().spawnFallingBlock(p.getEyeLocation(), Material.HUGE_MUSHROOM_1, (byte) 5);
		turtle.setCustomName(EnumItem.RedTurtle.getName());
		turtle.setCustomNameVisible(false);
		turtle.setDropItem(false);

		int rank = RaceManager.getRank(p);
		Player target = null;
		if(rank == 1)
			new ItemDyedTurtle(p, getPlayerfromRank(rank + 1), turtle, false, true).runTaskTimer(YPLKart.getInstance(), 0, 1);
		else
			new ItemDyedTurtle(p, getPlayerfromRank(rank - 1), turtle, false, false).runTaskTimer(YPLKart.getInstance(), 0, 1);
	}

	public void itemThornedturtle(Player p){
		if(!isEntry(p))return;
		if(getEntryPlayer().size() < 2){
			Util.sendMessage(p, "レース参加者が居ないため使用できません");
			return;
		}
		int rank = getRank(p);
		if(rank == 0)return;
		else if(rank == 1){
			Util.sendMessage(p, "1位のプレイヤーはトゲゾーこうらを使えません");
			return;
		}
		if(!hasNearbyUnpassedCheckpoint(p)){
			Util.sendMessage(p, "#Red周囲に未通過のチェックポイントがないため使用できません");
			return;
		}

		Util.setItemDecrease(p);

		FallingBlock turtle = p.getWorld().spawnFallingBlock(p.getEyeLocation(), Material.HUGE_MUSHROOM_1, (byte) 6);
		turtle.setCustomName(EnumItem.ThornedTurtle.getName());
		turtle.setCustomNameVisible(false);
		turtle.setDropItem(false);

		new ItemDyedTurtle(p, getPlayerfromRank(1), turtle, true, false).runTaskTimer(pl, 0, 1);
	}

	public void itemKiller(final Player p){
		if(!isEntry(p))return;
		if(getRank(p) == 0)return;

		if(!hasNearbyUnpassedCheckpoint(p)){
			Util.sendMessage(p, "#Red周囲に未通過のチェックポイントがないため使用できません");
			return;
		}

		final Race r = getRace(p);

		if(r.getUsingKiller()){
			Util.sendMessage(p, "#Red既に使用中です");
			return;
		}

		Util.setItemDecrease(p);

		EnumCharacter job = r.getCharacter();

		int life = Settings.KillerEffectSecond + job.getItemAdjustPositiveEffectSecond();
		new SendCountDownTitle(p, life, "⚠ AUTO CONTROL ⚠", ChatColor.RED, ChatColor.YELLOW).runTaskTimer(YPLKart.getInstance(), 0, 1);
		r.setUsingKiller(life, true);

		if(r.getKart() == null){
			EnumKarts kart = EnumKarts.Kart1;

			try {
				Minecart minecart = RaceManager.createCustomMinecart(p.getLocation(), kart);
				minecart.setDisplayBlock(new MaterialData(Material.HUGE_MUSHROOM_1, (byte) 9));
				minecart.setPassenger(p);
				Bukkit.getServer().getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
					public void run(){
						try {
							if(p.getVehicle() != null){
								RaceManager.removeCustomMinecart(p);
							}
						}catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}, life*20);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}else{
			if(p.getVehicle() != null)
				if(p.getVehicle() instanceof Minecart){
					((Minecart)p.getVehicle()).setDisplayBlock(new MaterialData(Material.HUGE_MUSHROOM_1, (byte) 9));
					Bukkit.getServer().getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
						public void run(){
							try {
								if(p.getVehicle() != null)
									if(p.getVehicle() instanceof Minecart)
									((Minecart)p.getVehicle()).setDisplayBlock(new MaterialData(r.getKart().getDisplayBlock(), r.getKart().getDisplayData()));

							}catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}, life*20);
				}
		}
		/*if(r.getKart() == null){
			Minecart cart = p.getWorld().spawn(p.getLocation(), Minecart.class);
			cart.setDisplayBlock(new MaterialData(Material.TNT));
			cart.setCustomName(EnumItem.Killer.getName());
			cart.setCustomNameVisible(false);
			cart.setPassenger(p);
			Util.removeEntityCollision(cart);

			new ItemAutoMoveProjectile(p, cart, life, Settings.KillerMovingDamage).runTaskTimer(pl, 0, 1);
		}*/
	}

	public static void setNegativeItemSpeed(final Player p, int second, int level, Sound sound){
		p.playSound(p.getLocation(), sound, 0.5F, -1.0F);
		second = (second + getRace(p).getCharacter().getItemAdjustNegativeEffectSecond())*20;

		Util.setPotionEffect(p, PotionEffectType.SLOW, second, level + getRace(p).getCharacter().getItemAdjustNegativeEffectLevel());

		getRace(p).setItemNegativeSpeedTask(
			Bukkit.getScheduler().runTaskLater(pl,new Runnable() {
				public void run() {
					p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
					p.removePotionEffect(PotionEffectType.SLOW);
					getRace(p).setItemNegativeSpeedTask(null);
				}
			}, second)
		);
	}

	public static void setPositiveItemSpeed(final Player p, int second, int level, Sound sound){
		p.playSound(p.getLocation(), sound, 0.5F, -1.0F);
		final Race race = getRace(p);

		second = (second + race.getCharacter().getItemAdjustPositiveEffectSecond())*20;
		Util.setPotionEffect(p,PotionEffectType.SPEED,second,level + race.getCharacter().getItemAdjustPositiveEffectLevel());
		if(race.getDeathPenaltyTask() != null){
			race.setDeathPenaltyTask(null);
			p.setWalkSpeed(race.getCharacter().getWalkSpeed());
		}
		race.setItemPositiveSpeedTask(
			Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
				public void run() {
					p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
					p.removePotionEffect(PotionEffectType.SPEED);
					race.setItemPositiveSpeedTask(null);
				}
			}, second)
		);
	}
}
