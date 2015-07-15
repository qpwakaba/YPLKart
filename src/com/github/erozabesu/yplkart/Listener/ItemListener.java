package com.github.erozabesu.yplkart.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.Data.Message;
import com.github.erozabesu.yplkart.Data.Settings;
import com.github.erozabesu.yplkart.Enum.EnumCharacter;
import com.github.erozabesu.yplkart.Enum.EnumItem;
import com.github.erozabesu.yplkart.Enum.EnumKarts;
import com.github.erozabesu.yplkart.Enum.Permission;
import com.github.erozabesu.yplkart.Object.Race;
import com.github.erozabesu.yplkart.Task.ItemBananaTask;
import com.github.erozabesu.yplkart.Task.ItemDyedTurtleTask;
import com.github.erozabesu.yplkart.Task.ItemStarTask;
import com.github.erozabesu.yplkart.Task.ItemTurtleTask;
import com.github.erozabesu.yplkart.Task.SendCountDownTitleTask;
import com.github.erozabesu.yplkart.Utils.Util;

public class ItemListener extends RaceManager implements Listener {
    private static YPLKart pl;

    public ItemListener(YPLKart plugin) {
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
    public void useItem(PlayerInteractEvent e) {
        if (!Settings.isEnable(e.getPlayer().getWorld()))
            return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        final Player p = e.getPlayer();
        final UUID id = p.getUniqueId();
        if (EnumItem.CHECKPOINT_TOOL.isSimilar(p.getItemInHand())) {
            e.setCancelled(true);
            if (Permission.hasPermission(p, EnumItem.CHECKPOINT_TOOL.getPermission(), false)) {
                try {
                    createCustomWitherSkull(p.getLocation(), p.getItemInHand().getItemMeta().getLore().get(0));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (EnumItem.ITEMBOX_TOOL.isSimilar(p.getItemInHand())) {
            e.setCancelled(true);
            if (Permission.hasPermission(p, EnumItem.ITEMBOX_TOOL.getPermission(), false)) {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block b = e.getClickedBlock().getRelative(e.getBlockFace());
                    EnderCrystal endercrystal = b.getWorld()
                            .spawn(b.getLocation().add(0.5, 0, 0.5), EnderCrystal.class);
                    endercrystal.setCustomName(ItemBoxName);
                    endercrystal.setCustomNameVisible(false);
                    b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                }
            }
        } else if (EnumItem.ITEMBOX_TOOL_TIER2.isSimilar(p.getItemInHand())) {
            e.setCancelled(true);
            if (Permission.hasPermission(p, EnumItem.ITEMBOX_TOOL_TIER2.getPermission(), false)) {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block b = e.getClickedBlock().getRelative(e.getBlockFace());
                    EnderCrystal endercrystal = b.getWorld()
                            .spawn(b.getLocation().add(0.5, 0, 0.5), EnderCrystal.class);
                    endercrystal.setCustomName(ItemBoxNameTier2);
                    endercrystal.setCustomNameVisible(true);
                    b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                }
            }
        } else if (EnumItem.FAKE_ITEMBOX_TOOL.isSimilar(p.getItemInHand())) {
            e.setCancelled(true);
            if (Permission.hasPermission(p, EnumItem.FAKE_ITEMBOX_TOOL.getPermission(), false)) {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block b = e.getClickedBlock().getRelative(e.getBlockFace());
                    EnderCrystal endercrystal = b.getWorld()
                            .spawn(b.getLocation().add(0.5, 0, 0.5), EnderCrystal.class);
                    endercrystal.setCustomName(ItemBoxNameFake);
                    endercrystal.setCustomNameVisible(true);
                    b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                }
            }
        } else if (EnumItem.MENU.isSimilar(p.getItemInHand())) {
            e.setCancelled(true);
            showCharacterSelectMenu(p);
        }
        if (isRacing(id)) {
            if (EnumItem.MUSHROOM.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.MUSHROOM.getPermission(), false)) {
                    Util.setItemDecrease(p);
                    setPositiveItemSpeed(p, Settings.MushroomEffectSecond, Settings.MushroomEffectLevel, Sound.EXPLODE);
                }
            } else if (EnumItem.POWERFULL_MUSHROOM.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.POWERFULL_MUSHROOM.getPermission(), false)) {
                    Util.setItemDecrease(p);
                    setPositiveItemSpeed(p, Settings.PowerfullMushroomEffectSecond,
                            Settings.PowerfullMushroomEffectLevel, Sound.EXPLODE);
                    p.getWorld().playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                }
            } else if (EnumItem.BANANA.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.BANANA.getPermission(), false)) {
                    Util.setItemDecrease(p);
                    Location l = Util.getFrontBackLocationFromYaw(p.getLocation().add(0, 0.5, 0), -5).getBlock().getLocation()
                            .add(0.5, 0, 0.5);

                    FallingBlock b = p.getWorld().spawnFallingBlock(l, Material.HUGE_MUSHROOM_1, (byte) 8);
                    b.setCustomName(EnumItem.BANANA.getName());
                    b.setCustomNameVisible(false);
                    b.setDropItem(false);
                    Util.removeEntityCollision(b);

                    new ItemBananaTask(RaceManager.getCircuit(id), b, l).runTaskTimer(pl, 0, 1);
                    p.getWorld().playSound(p.getLocation(), Sound.SLIME_WALK, 1.0F, 1.0F);
                }
            } else if (EnumItem.FAKE_ITEMBOX.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.FAKE_ITEMBOX.getPermission(), false)) {
                    Util.setItemDecrease(p);
                    EnderCrystal endercrystal = p.getWorld().spawn(
                            Util.getFrontBackLocationFromYaw(p.getLocation().add(0, 0.5, 0), -5).getBlock().getLocation()
                                    .add(0.5, 0, 0.5), EnderCrystal.class);
                    endercrystal.setCustomName(FakeItemBoxName);
                    endercrystal.setCustomNameVisible(true);
                    RaceManager.getCircuit(id).addJammerEntity(endercrystal);
                    p.getWorld().playEffect(p.getLocation(), Effect.CLICK1, 0);
                }
            } else if (EnumItem.THUNDER.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.THUNDER.getPermission(), false))
                    itemThunder(p);
            } else if (EnumItem.STAR.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.STAR.getPermission(), false))
                    new ItemStarTask(p).runTaskTimer(pl, 0, 1);
            } else if (EnumItem.TERESA.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.TERESA.getPermission(), false))
                    itemTeresa(p);
            } else if (EnumItem.TURTLE.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.TURTLE.getPermission(), false))
                    itemTurtle(p);
            } else if (EnumItem.RED_TURTLE.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.RED_TURTLE.getPermission(), false))
                    itemRedturtle(p);
            } else if (EnumItem.THORNED_TURTLE.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.THORNED_TURTLE.getPermission(), false))
                    itemThornedturtle(p);
            } else if (EnumItem.GESSO.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.GESSO.getPermission(), false))
                    itemGesso(p);
            } else if (EnumItem.KILLER.isSimilar(p.getItemInHand())) {
                e.setCancelled(true);
                if (Permission.hasPermission(p, EnumItem.KILLER.getPermission(), false)) {
                    itemKiller(p);
                }
            }
        }
    }

    @EventHandler
    public void onInteractObjectEntity(PlayerMoveEvent e) {
        if (!Settings.isEnable(e.getFrom().getWorld()))
            return;
        final Player p = e.getPlayer();
        final UUID id = p.getUniqueId();
        if (!isRacing(id))
            return;

        List<Entity> entities = p.getNearbyEntities(0.7, 2, 0.7);

        for (final Entity entity : entities) {
            if (entity.getCustomName() != null) {
                if (entity instanceof FallingBlock) {
                    if (entity.getCustomName().equalsIgnoreCase(EnumItem.BANANA.getName())) {
                        if (Permission.hasPermission(p, Permission.INTERACT_BANANA, false)) {
                            RaceManager.getCircuit(id).removeJammerEntity(entity);
                            entity.remove();

                            if (p.getNoDamageTicks() == 0) {
                                Message.raceInteractBanana.sendMessage(p, RaceManager.getCircuit(id));
                                setNegativeItemSpeed(p, Settings.BananaEffectSecond, Settings.BananaEffectLevel,
                                        Sound.SLIME_WALK);
                            }
                        }
                    }
                } else if (entity instanceof EnderCrystal) {
                    //偽アイテムボックス
                    if (entity.getCustomName().equalsIgnoreCase(FakeItemBoxName)) {
                        if (Permission.hasPermission(p, Permission.INTERACT_FAKEITEMBOX, false)) {
                            entity.remove();

                            if (p.getNoDamageTicks() == 0) {
                                Message.raceInteractFakeItemBox.sendMessage(p, RaceManager.getCircuit(id));
                                Util.createSafeExplosion(null, p.getLocation(), Settings.FakeItemBoxHitDamage, 1);
                            }
                        }
                        //偽アイテムボックス復活するタイプ
                    } else if (entity.getCustomName().equalsIgnoreCase(ItemBoxNameFake)) {
                        if (Permission.hasPermission(p, Permission.INTERACT_FAKEITEMBOX, false)) {
                            entity.remove();

                            pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
                                public void run() {
                                    EnderCrystal endercrystal = entity.getWorld().spawn(entity.getLocation(),
                                            EnderCrystal.class);
                                    endercrystal.setCustomName(ItemBoxNameFake);
                                    endercrystal.setCustomNameVisible(true);
                                }
                            }, 2 * 20L + 10L);

                            if (p.getNoDamageTicks() == 0) {
                                Message.raceInteractBanana.sendMessage(p, RaceManager.getCircuit(id));
                                Util.createSafeExplosion(null, p.getLocation(), Settings.FakeItemBoxHitDamage, 1);
                            }
                        }
                    } else if (entity.getCustomName().contains(ItemBoxName)) {
                        if (Permission.hasPermission(p, Permission.INTERACT_ITEMBOX, false)) {
                            if (itemboxCool.get(p) == null)
                                itemboxCool.put(p, false);
                            if (!itemboxCool.get(p)) {
                                entity.remove();
                                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                                    public void run() {
                                        EnderCrystal endercrystal = entity.getWorld().spawn(entity.getLocation(),
                                                EnderCrystal.class);
                                        if (entity.getCustomName().equalsIgnoreCase(ItemBoxNameTier2)) {
                                            endercrystal.setCustomName(ItemBoxNameTier2);
                                            endercrystal.setCustomNameVisible(true);
                                        } else {
                                            endercrystal.setCustomName(ItemBoxName);
                                            endercrystal.setCustomNameVisible(false);
                                        }
                                    }
                                }, 2 * 20L + 10L);

                                int denominator = getRacingPlayer(getRace(p).getEntry()).size();
                                //denominator = denominator + getGoalPlayer(getRace(p).getEntry()).size();
                                if (denominator == 0)
                                    denominator = 1;
                                int rank = getRank(p);
                                if (rank == 0)
                                    rank = 1;

                                int percent = 100 / (denominator / rank);
                                int tier = 0;

                                if (percent < Settings.Tier1)
                                    tier = 1;
                                else if (Settings.Tier1 <= percent && percent < Settings.Tier2)
                                    tier = 2;
                                else if (Settings.Tier2 <= percent && percent < Settings.Tier3)
                                    tier = 3;
                                else
                                    tier = 4;

                                if (entity.getCustomName().equalsIgnoreCase(ItemBoxNameTier2))
                                    if (tier < 4)
                                        tier++;

                                Inventory i = p.getInventory();
                                ItemStack item = EnumItem.getRandomItemfromTier(p, tier);
                                if (item != null) {
                                    EnumItem.addItem(p, item);
                                    Message.raceInteractItemBox.sendMessage(p, new Object[] { getCircuit(id), item });
                                } else {
                                    Message.raceInteractItemBoxFailed.sendMessage(p, getCircuit(id));
                                }
                                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1.0F, 2.0F);
                                itemboxCool.put(p, true);
                                pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
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
    public void onStepSpeedBlock(PlayerMoveEvent e) {
        if (!Settings.isEnable(e.getFrom().getWorld()))
            return;
        if (!isRacing(e.getPlayer().getUniqueId()))
            return;

        final Player p = e.getPlayer();
        final UUID id = p.getUniqueId();

        if (Util.getGroundBlockMaterial(p.getLocation()) == Material.PISTON_BASE
                || Util.getGroundBlockMaterial(p.getLocation()) == Material.PISTON_STICKY_BASE) {
            if (Permission.hasPermission(p, Permission.INTERACT_DASHBOARD, false)) {
                if (boostRailCool.get(p) != null)
                    if (boostRailCool.get(p))
                        return;
                Bukkit.getServer().getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        boostRailCool.put(p, false);
                    }
                }, 10L);

                if (p.getVehicle() != null) {
                    if (isRacingKart(p.getVehicle())) {
                        getRace(p).setStepDashBoard();
                        p.playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5F, 1.0F);
                        return;
                    }
                }

                setPositiveItemSpeed(p, Settings.BoostRailEffectSecond, Settings.BoostRailEffectLevel, Sound.EXPLODE);
                //p.playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5F, 1.0F);
                boostRailCool.put(p, true);
            }
        }

        /*if (Util.getStepBlock(p.getLocation()).equalsIgnoreCase(Settings.DirtBlock)){
        	double x = p.getVelocity().clone().multiply(0.5).getX();
        	double z = p.getVelocity().clone().multiply(0.5).getZ();
        	double y = p.getVelocity().clone().getY();
        	p.setVelocity(new Vector(x,y,z));
        }*/
    }

    @EventHandler
    public void onItemBoxDamaged(EntityDamageByEntityEvent e) {
        if (!Settings.isEnable(e.getEntity().getWorld()))
            return;
        if (!(e.getEntity() instanceof EnderCrystal))
            return;
        if (e.getEntity().getCustomName() == null)
            return;
        if (!(e.getDamager() instanceof Player))
            return;
        Player p = (Player) e.getDamager();
        if (p.getItemInHand() == null)
            return;
        if (EnumItem.ITEMBOX_TOOL.isSimilar(p.getItemInHand())) {
            if (e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxName)) {
                e.getEntity().remove();
                Message.itemRemoveItemBox.sendMessage(p);
                e.setCancelled(true);
            }
        } else if (EnumItem.ITEMBOX_TOOL_TIER2.isSimilar(p.getItemInHand())) {
            if (e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxNameTier2)) {
                e.getEntity().remove();
                Message.itemRemoveItemBox.sendMessage(p);
                e.setCancelled(true);
            }
        } else if (EnumItem.FAKE_ITEMBOX_TOOL.isSimilar(p.getItemInHand())) {
            if (e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxNameFake)
                    || e.getEntity().getCustomName().equalsIgnoreCase(FakeItemBoxName)) {
                e.getEntity().remove();
                Message.itemRemoveItemBox.sendMessage(p);
                e.setCancelled(true);
            }
        }
    }

    //〓

    @EventHandler
    public void onInteractwithDye(PlayerInteractEntityEvent e) {
        if (!Settings.isEnable(e.getPlayer().getWorld()))
            return;
        if (!(e.getRightClicked() instanceof Sheep))
            return;
        if (EnumItem.isKeyItem(e.getPlayer().getItemInHand())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectDamaged(EntityInteractEvent e) {
        if (!Settings.isEnable(e.getEntity().getWorld()))
            return;
        if (e.getEntity().getCustomName() == null)
            return;

        Entity entity = e.getEntity();
        if (entity instanceof EnderCrystal) {
            if (entity.getCustomName().contains(ItemBoxName))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof FallingBlock) {
            if (entity.getCustomName().contains(EnumItem.BANANA.getName()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectDamaged(EntityDamageEvent e) {
        if (!Settings.isEnable(e.getEntity().getWorld()))
            return;
        if (e.getEntity().getCustomName() == null)
            return;

        Entity entity = e.getEntity();
        if (entity instanceof EnderCrystal) {
            if (entity.getCustomName().contains(ItemBoxName))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof FallingBlock) {
            if (entity.getCustomName().contains(EnumItem.BANANA.getName()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectExplode(EntityExplodeEvent e) {
        if (!Settings.isEnable(e.getEntity().getWorld()))
            return;
        if (e.getEntity().getCustomName() == null)
            return;

        Entity entity = e.getEntity();
        if (entity instanceof EnderCrystal) {
            if (entity.getCustomName().contains(ItemBoxName))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof FallingBlock) {
            if (entity.getCustomName().contains(EnumItem.BANANA.getName()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectExplosionPrime(ExplosionPrimeEvent e) {
        if (!Settings.isEnable(e.getEntity().getWorld()))
            return;
        if (e.getEntity().getCustomName() == null)
            return;

        Entity entity = e.getEntity();
        if (entity instanceof EnderCrystal) {
            if (entity.getCustomName().contains(ItemBoxName))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof FallingBlock) {
            if (entity.getCustomName().contains(EnumItem.BANANA.getName()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFallingBlockFalled(EntityChangeBlockEvent e) {
        if (!Settings.isEnable(e.getEntity().getWorld()))
            return;
        if (!(e.getEntity() instanceof FallingBlock))
            return;
        if (e.getEntity().getCustomName() == null)
            return;
        if (e.getEntity().getCustomName().equalsIgnoreCase(EnumItem.RED_TURTLE.getName())
                || e.getEntity().getCustomName().equalsIgnoreCase(EnumItem.THORNED_TURTLE.getName())
                || e.getEntity().getCustomName().equalsIgnoreCase(EnumItem.TURTLE.getName())
                || e.getEntity().getCustomName().equalsIgnoreCase(EnumItem.BANANA.getName()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent e) {
        if (!Settings.isEnable(e.getPlayer().getWorld()))
            return;
        if (!isStandBy(e.getPlayer().getUniqueId()))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (!Settings.isEnable(e.getPlayer().getWorld()))
            return;
        if (!isStandBy(e.getPlayer().getUniqueId()))
            return;

        if (EnumItem.MENU.isSimilar(e.getItemDrop().getItemStack()))
            e.setCancelled(true);
        else
            e.getItemDrop().remove();
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public void itemTeresa(Player user) {
        List<Player> entry = getRacingPlayer(getRace(user).getEntry());
        if (entry.size() <= 1) {
            Message.itemNoPlayer.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        Util.setItemDecrease(user);
        entry.remove(user);
        Player target = entry.get(new Random().nextInt(entry.size()));

        Inventory targetinv = target.getInventory();

        ArrayList<ItemStack> targetitem = new ArrayList<ItemStack>();
        for (int i = 0; i < Settings.ItemSlot + getRace(target).getCharacter().getItemAdjustMaxSlotSize(); i++) {
            if (targetinv.getItem(i) != null)
                if (EnumItem.isKeyItem(targetinv.getItem(i)))
                    targetitem.add(targetinv.getItem(i));
        }

        if (targetitem.isEmpty() || 0 < target.getNoDamageTicks()) {
            Message.itemTeresaNoItem.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            user.playSound(user.getLocation(), Sound.BURP, 1.0F, 0.5F);
        } else {
            ItemStack rob = targetitem.get(new Random().nextInt(targetitem.size()));
            targetinv.remove(rob);
            Message.itemTeresaRob.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()), rob });
            user.playSound(user.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            Message.itemTeresaRobbed.sendMessage(target, new Object[] { getCircuit(user.getUniqueId()), rob });
            target.playSound(user.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            EnumItem.addItem(user, rob);
            user.updateInventory();
            target.updateInventory();
        }

        int effectsecond = (Settings.TeresaEffectSecond + getRace(user).getCharacter()
                .getItemAdjustPositiveEffectSecond()) * 20;
        user.setNoDamageTicks(effectsecond);
        user.removePotionEffect(PotionEffectType.INVISIBILITY);
        user.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, effectsecond, 1));
    }

    public void itemThunder(Player user) {
        List<Player> list = getRacingPlayer(getRace(user).getEntry());
        if (list.size() <= 1) {
            Message.itemNoPlayer.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        Util.setItemDecrease(user);
        final World w = user.getWorld();

        int launchdamage = Settings.ThunderHitDamage + getRace(user).getCharacter().getItemAdjustAttackDamage();
        for (final Player p : list) {
            if (p.getUniqueId() == user.getUniqueId())
                continue;
            if (p.getNoDamageTicks() != 0)
                continue;

            p.removePotionEffect(PotionEffectType.SLOW);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (Settings.ThunderEffectSecond + getRace(p)
                    .getCharacter().getItemAdjustNegativeEffectSecond()) * 20, Settings.ThunderEffectLevel
                    + getRace(p).getCharacter().getItemAdjustNegativeEffectLevel()));

            final Location loc = p.getLocation();
            w.strikeLightningEffect(loc);
            Util.createSafeExplosion(user, loc, launchdamage, 5);
            p.playSound(loc, Sound.SUCCESSFUL_HIT, 0.5F, 1.0F);
            p.playSound(loc, Sound.LEVEL_UP, 0.5F, -1.0F);
        }
    }

    public void itemGesso(Player user) {
        List<Player> list = getRacingPlayer(getRace(user).getEntry());
        if (list.size() <= 1) {
            Message.itemNoPlayer.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        ArrayList<Player> target = new ArrayList<Player>();
        int rank = getRank(user);

        for (Player entry : list) {
            if (getRank(entry) < rank)
                target.add(entry);
        }
        if (target.isEmpty()) {
            Message.itemNoHigherPlayer.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        Util.setItemDecrease(user);
        final World w = user.getWorld();

        for (Player p : target) {
            if (p.getUniqueId() == user.getUniqueId())
                continue;
            if (p.getNoDamageTicks() != 0)
                continue;

            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (Settings.GessoEffectSecond + getRace(p)
                    .getCharacter().getItemAdjustNegativeEffectSecond()) * 20, Settings.GessoEffectLevel
                    + getRace(p).getCharacter().getItemAdjustNegativeEffectLevel()));

            p.playSound(p.getLocation(), Sound.SLIME_WALK, 2.0F, 1.0F);
        }
    }

    public void itemTurtle(Player p) {
        if (!isRacing(p.getUniqueId()))
            return;
        if (getRank(p) == 0)
            return;

        Util.setItemDecrease(p);
        FallingBlock b = p.getWorld().spawnFallingBlock(p.getLocation(), Material.HUGE_MUSHROOM_1, (byte) 7);
        b.setCustomName(EnumItem.TURTLE.getName());
        b.setCustomNameVisible(false);
        b.setDropItem(false);

        new ItemTurtleTask(p, b, Util.getFrontBackLocationFromYaw(p.getLocation(), 3), 60).runTaskTimer(pl, 0, 1);
    }

    public void itemRedturtle(Player user) {
        if (!isRacing(user.getUniqueId()))
            return;
        if (getRacingPlayer(getRace(user).getEntry()).size() <= 1) {
            Message.itemNoPlayer.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        if (getRank(user) == 0)
            return;
        if (getNearestUnpassedCheckpoint(user.getLocation(), checkPointDetectRadius + 20, getRace(user)) == null) {
            Message.itemNoCheckpoint.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        Util.setItemDecrease(user);

        FallingBlock turtle = user.getWorld().spawnFallingBlock(user.getEyeLocation(), Material.HUGE_MUSHROOM_1,
                (byte) 5);
        turtle.setCustomName(EnumItem.RED_TURTLE.getName());
        turtle.setCustomNameVisible(false);
        turtle.setDropItem(false);

        int rank = getRank(user);
        Player target = null;
        if (rank == 1)
            new ItemDyedTurtleTask(user, getPlayerfromRank(getRace(user).getEntry(), rank + 1), turtle, false, true)
                    .runTaskTimer(YPLKart.getInstance(), 0, 1);
        else
            new ItemDyedTurtleTask(user, getPlayerfromRank(getRace(user).getEntry(), rank - 1), turtle, false, false)
                    .runTaskTimer(YPLKart.getInstance(), 0, 1);
    }

    public void itemThornedturtle(Player user) {
        if (!isRacing(user.getUniqueId()))
            return;
        if (getRacingPlayer(getRace(user).getEntry()).size() <= 1) {
            Message.itemNoPlayer.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        int rank = getRank(user);
        if (rank == 0)
            return;
        else if (rank == 1) {
            Message.itemHighestPlayer.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()),
                    EnumItem.THORNED_TURTLE.getItem() });
            return;
        }
        if (getNearestUnpassedCheckpoint(user.getLocation(), checkPointDetectRadius + 20, getRace(user)) == null) {
            Message.itemNoCheckpoint.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        Util.setItemDecrease(user);

        FallingBlock turtle = user.getWorld().spawnFallingBlock(user.getEyeLocation(), Material.HUGE_MUSHROOM_1,
                (byte) 6);
        turtle.setCustomName(EnumItem.THORNED_TURTLE.getName());
        turtle.setCustomNameVisible(false);
        turtle.setDropItem(false);

        new ItemDyedTurtleTask(user, getPlayerfromRank(getRace(user).getEntry(), 1), turtle, true, false).runTaskTimer(
                pl, 0, 1);
    }

    public void itemKiller(final Player user) {
        if (!isRacing(user.getUniqueId()))
            return;
        if (getRank(user) == 0)
            return;

        Entity unpassedcheckpoint = getNearestUnpassedCheckpoint(user.getLocation(), checkPointDetectRadius + 20,
                getRace(user));

        if (unpassedcheckpoint == null) {
            Message.itemNoCheckpoint.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        final Race r = getRace(user);

        if (r.getUsingKiller() != null) {
            Message.itemAlreadyUsing.sendMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        Util.setItemDecrease(user);

        EnumCharacter job = r.getCharacter();

        int life = Settings.KillerEffectSecond + job.getItemAdjustPositiveEffectSecond();
        new SendCountDownTitleTask(user, life, Message.titleUsingKiller.getMessage()).runTaskTimer(
                YPLKart.getInstance(), 0, 1);
        r.setUsingKiller(life, unpassedcheckpoint);

        if (r.getKart() == null) {
            EnumKarts kart = EnumKarts.KART1;

            try {
                Minecart minecart = createCustomMinecart(user.getLocation(), kart);
                minecart.setDisplayBlock(new MaterialData(Material.HUGE_MUSHROOM_1, (byte) 9));
                minecart.setPassenger(user);
                Bukkit.getServer().getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        try {
                            if (user.getVehicle() != null) {
                                leaveRacingKart(user);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, life * 20);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            if (user.getVehicle() != null)
                if (user.getVehicle() instanceof Minecart) {
                    ((Minecart) user.getVehicle())
                            .setDisplayBlock(new MaterialData(Material.HUGE_MUSHROOM_1, (byte) 9));
                    Bukkit.getServer().getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                        public void run() {
                            try {
                                if (user.getVehicle() != null)
                                    if (user.getVehicle() instanceof Minecart)
                                        ((Minecart) user.getVehicle()).setDisplayBlock(new MaterialData(r.getKart()
                                                .getDisplayBlock(), r.getKart().getDisplayData()));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, life * 20);
                }
        }
    }

    public static void setNegativeItemSpeed(final Player p, int second, int level, Sound sound) {
        p.playSound(p.getLocation(), sound, 0.5F, -1.0F);
        second = (second + getRace(p).getCharacter().getItemAdjustNegativeEffectSecond()) * 20;

        Util.setPotionEffect(p, PotionEffectType.SLOW, second, level
                + getRace(p).getCharacter().getItemAdjustNegativeEffectLevel());

        getRace(p).setItemNegativeSpeedTask(
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                        p.removePotionEffect(PotionEffectType.SLOW);
                        getRace(p).setItemNegativeSpeedTask(null);
                    }
                }, second)
                );
    }

    public static void setPositiveItemSpeed(final Player p, int second, int level, Sound sound) {
        p.playSound(p.getLocation(), sound, 0.5F, -1.0F);
        final Race race = getRace(p);

        second = (second + race.getCharacter().getItemAdjustPositiveEffectSecond()) * 20;
        Util.setPotionEffect(p, PotionEffectType.SPEED, second, level
                + race.getCharacter().getItemAdjustPositiveEffectLevel());
        if (race.getDeathPenaltyTask() != null) {
            race.setDeathPenaltyTask(null);
            race.setDeathPenaltyTitleSendTask(null);
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
