package com.github.erozabesu.yplkart.listener;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.object.Character;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.task.ItemBananaTask;
import com.github.erozabesu.yplkart.task.ItemDyedTurtleTask;
import com.github.erozabesu.yplkart.task.ItemStarTask;
import com.github.erozabesu.yplkart.task.ItemTurtleTask;
import com.github.erozabesu.yplkart.task.SendCountDownTitleTask;
import com.github.erozabesu.yplkart.utils.Util;

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

        // issue #112
        if (e.isCancelled()) {
            return;
        }

        Player player = e.getPlayer();
        //プラグインが有効でない場合return
        if (!YPLKart.isPluginEnabled(player.getWorld())) {
            return;
        }

        Action clickAction = e.getAction();

        //アクションが右クリック以外の場合return
        if (clickAction != Action.RIGHT_CLICK_BLOCK && clickAction != Action.RIGHT_CLICK_AIR) {
            return;
        }

        //手にアイテムを持っていない場合return
        if (player.getItemInHand() == null) {
            return;
        }

        ItemEnum itemEnum = ItemEnum.getItemByItemStack(player.getItemInHand());

        //手に持っているアイテムがキーアイテムでない場合return
        if (itemEnum == null) {
            return;
        }

        //キーアイテムの使用パーミッションを所有していない場合return
        if (!Permission.hasPermission(player, itemEnum.getUsePermission(), false)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        e.setCancelled(true);

        //チェックポイントツール
        if (ItemEnum.CHECKPOINT_TOOL.isSimilar(player.getItemInHand())) {
            try {
                createCustomWitherSkull(player.getLocation(), player.getItemInHand().getItemMeta().getLore().get(0));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        //アイテムボックスツール
        } else if (ItemEnum.ITEMBOX_TOOL.isSimilar(player.getItemInHand())) {
            //ブロックを右クリックした場合のみ動作
            if (clickAction == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock().getRelative(e.getBlockFace());
                EnderCrystal endercrystal = b.getWorld()
                        .spawn(b.getLocation().add(0.5, 0, 0.5), EnderCrystal.class);
                endercrystal.setCustomName(ItemBoxName);
                endercrystal.setCustomNameVisible(false);
                b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
            }

        //アイテムボックスツールティアー2
        } else if (ItemEnum.ITEMBOX_TOOL_TIER2.isSimilar(player.getItemInHand())) {
            //ブロックを右クリックした場合のみ動作
            if (clickAction == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock().getRelative(e.getBlockFace());
                EnderCrystal endercrystal = b.getWorld()
                        .spawn(b.getLocation().add(0.5, 0, 0.5), EnderCrystal.class);
                endercrystal.setCustomName(ItemBoxNameTier2);
                endercrystal.setCustomNameVisible(true);
                b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
            }

        //フェイクアイテムボックスツール
        } else if (ItemEnum.FAKE_ITEMBOX_TOOL.isSimilar(player.getItemInHand())) {
            //ブロックを右クリックした場合のみ動作
            if (clickAction == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock().getRelative(e.getBlockFace());
                EnderCrystal endercrystal = b.getWorld()
                        .spawn(b.getLocation().add(0.5, 0, 0.5), EnderCrystal.class);
                endercrystal.setCustomName(ItemBoxNameFake);
                endercrystal.setCustomNameVisible(true);
                b.getWorld().playSound(b.getLocation(), Sound.CLICK, 1.0F, 1.0F);
            }

        //メニュー
        } else if (ItemEnum.MENU.isSimilar(player.getItemInHand())) {
            showSelectMenu(player, true);
        }

        //レース中、かつゴールしていない状態のみ利用できるアイテム群
        if (isStillRacing(uuid)) {

            //ダッシュきのこ
            if (ItemEnum.MUSHROOM.isSimilar(player.getItemInHand())) {
                Util.setItemDecrease(player);
                setPositiveItemSpeed(player, ItemEnum.MUSHROOM.getEffectSecond(), ItemEnum.MUSHROOM.getEffectLevel(), Sound.EXPLODE);

            //パワフルダッシュキノコ
            } else if (ItemEnum.POWERFULL_MUSHROOM.isSimilar(player.getItemInHand())) {
            Util.setItemDecrease(player);
                setPositiveItemSpeed(player, ItemEnum.POWERFULL_MUSHROOM.getEffectSecond(),
                        ItemEnum.POWERFULL_MUSHROOM.getEffectLevel(), Sound.EXPLODE);
                player.getWorld().playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);

            //バナナ
            } else if (ItemEnum.BANANA.isSimilar(player.getItemInHand())) {
                Util.setItemDecrease(player);
                Location l = Util.getForwardLocationFromYaw(player.getLocation().add(0, 0.5, 0), -5).getBlock().getLocation()
                        .add(0.5, 0, 0.5);

                FallingBlock b = player.getWorld().spawnFallingBlock(l, Material.HUGE_MUSHROOM_1, (byte) 8);
                b.setCustomName(ItemEnum.BANANA.getDisplayName());
                b.setCustomNameVisible(false);
                b.setDropItem(false);
                Util.removeEntityCollision(b);

                new ItemBananaTask(RaceManager.getCircuit(uuid), b, l).runTaskTimer(pl, 0, 1);
                player.getWorld().playSound(player.getLocation(), Sound.SLIME_WALK, 1.0F, 1.0F);

            //にせアイテムボックス
            } else if (ItemEnum.FAKE_ITEMBOX.isSimilar(player.getItemInHand())) {
                Util.setItemDecrease(player);
                EnderCrystal endercrystal = player.getWorld().spawn(
                        Util.getForwardLocationFromYaw(player.getLocation().add(0, 0.5, 0), -5).getBlock().getLocation()
                                .add(0.5, 0, 0.5), EnderCrystal.class);
                endercrystal.setCustomName(FakeItemBoxName);
                endercrystal.setCustomNameVisible(true);
                RaceManager.getCircuit(uuid).addJammerEntity(endercrystal);
                player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 0);

            //サンダー
            } else if (ItemEnum.THUNDER.isSimilar(player.getItemInHand())) {
                itemThunder(player);

            //スーパースター
            } else if (ItemEnum.STAR.isSimilar(player.getItemInHand())) {
                new ItemStarTask(player).runTaskTimer(pl, 0, 1);

            //テレサ
            } else if (ItemEnum.TERESA.isSimilar(player.getItemInHand())) {
                itemTeresa(player);

            //ミドリこうら
            } else if (ItemEnum.TURTLE.isSimilar(player.getItemInHand())) {
                itemTurtle(player);
            //アカこうら
            } else if (ItemEnum.RED_TURTLE.isSimilar(player.getItemInHand())) {
                itemRedturtle(player);

            //トゲゾーこうら
            } else if (ItemEnum.THORNED_TURTLE.isSimilar(player.getItemInHand())) {
                itemThornedturtle(player);

            //ゲッソー
            } else if (ItemEnum.GESSO.isSimilar(player.getItemInHand())) {
                itemGesso(player);

            //キラー
            } else if (ItemEnum.KILLER.isSimilar(player.getItemInHand())) {
                itemKiller(player);
            }
        }
    }

    //TODO 冗長
    @EventHandler
    public void onInteractObjectEntity(PlayerMoveEvent event) {
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld())) {
            return;
        }
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (!isStillRacing(uuid)) {
            return;
        }

        List<Entity> entities = player.getNearbyEntities(0.7, 2, 0.7);

        for (final Entity entity : entities) {
            if (entity.getCustomName() != null) {
                if (entity instanceof FallingBlock) {
                    if (entity.getCustomName().equalsIgnoreCase(ItemEnum.BANANA.getDisplayName())) {
                        if (Permission.hasPermission(player, Permission.INTERACT_BANANA, false)) {
                            RaceManager.getCircuit(uuid).removeJammerEntity(entity);
                            entity.remove();

                            if (player.getNoDamageTicks() == 0) {
                                MessageEnum.raceInteractBanana.sendConvertedMessage(player,
                                        RaceManager.getCircuit(uuid));
                                setNegativeItemSpeed(player, ItemEnum.BANANA.getEffectSecond(),
                                        ItemEnum.BANANA.getEffectLevel(), Sound.SLIME_WALK);
                            }
                        }
                    }
                } else if (entity instanceof EnderCrystal) {
                    //偽アイテムボックス
                    if (entity.getCustomName().equalsIgnoreCase(FakeItemBoxName)) {
                        if (Permission.hasPermission(player, Permission.INTERACT_FAKEITEMBOX, false)) {
                            entity.remove();

                            if (player.getNoDamageTicks() == 0) {
                                MessageEnum.raceInteractFakeItemBox.sendConvertedMessage(player,
                                        RaceManager.getCircuit(uuid));
                                Util.createSafeExplosion(null, player.getLocation(),
                                        ItemEnum.FAKE_ITEMBOX.getHitDamage(), 1, 2.0F, false, Particle.EXPLOSION_LARGE);
                            }
                        }
                    //偽アイテムボックス復活するタイプ
                    } else if (entity.getCustomName().equalsIgnoreCase(ItemBoxNameFake)) {
                        if (Permission.hasPermission(player, Permission.INTERACT_FAKEITEMBOX, false)) {
                            entity.remove();

                            pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
                                public void run() {
                                    EnderCrystal endercrystal = entity.getWorld().spawn(entity.getLocation(),
                                            EnderCrystal.class);
                                    endercrystal.setCustomName(ItemBoxNameFake);
                                    endercrystal.setCustomNameVisible(true);
                                }
                            }, 2 * 20L + 10L);

                            if (player.getNoDamageTicks() == 0) {
                                MessageEnum.raceInteractBanana.sendConvertedMessage(player, RaceManager.getCircuit(uuid));
                                Util.createSafeExplosion(null, player.getLocation(),
                                        ItemEnum.FAKE_ITEMBOX.getHitDamage(), 1, 2.0F, false, Particle.EXPLOSION_LARGE);
                            }
                        }
                    } else if (entity.getCustomName().contains(ItemBoxName)) {
                        if (Permission.hasPermission(player, Permission.INTERACT_ITEMBOX, false)) {
                            if (itemboxCool.get(player) == null)
                                itemboxCool.put(player, false);
                            if (!itemboxCool.get(player)) {
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

                                int denominator = getRacingPlayer(getRacer(player).getCircuitName()).size();
                                //denominator = denominator + getGoalPlayer(getRace(p).getEntry()).size();
                                if (denominator == 0)
                                    denominator = 1;
                                int rank = getRank(player);
                                if (rank == 0)
                                    rank = 1;

                                int percent = 100 / (denominator / rank);
                                int tier = 0;
                                int settingsTier1 = (Integer) ConfigEnum.ITEM_TIER1.getValue();
                                int settingsTier2 = (Integer) ConfigEnum.ITEM_TIER2.getValue();
                                int settingsTier3 = (Integer) ConfigEnum.ITEM_TIER3.getValue();

                                if (percent < settingsTier1)
                                    tier = 1;
                                else if (settingsTier1 <= percent && percent < settingsTier2)
                                    tier = 2;
                                else if (settingsTier2 <= percent && percent < settingsTier3)
                                    tier = 3;
                                else
                                    tier = 4;

                                if (entity.getCustomName().equalsIgnoreCase(ItemBoxNameTier2))
                                    if (tier < 4)
                                        tier++;

                                Inventory i = player.getInventory();
                                ItemEnum.addRandomItemFromTier(player, tier);
                                itemboxCool.put(player, true);
                                pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
                                    public void run() {
                                        itemboxCool.put(player, false);
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
    public void onStepSpeedBlock(PlayerMoveEvent event) {
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld())) {
            return;
        }
        if (!isStillRacing(event.getPlayer().getUniqueId())) {
            return;
        }

        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if (Util.getGroundBlockMaterial(player.getLocation()) == Material.PISTON_BASE
                || Util.getGroundBlockMaterial(player.getLocation()) == Material.PISTON_STICKY_BASE) {
            if (Permission.hasPermission(player, Permission.INTERACT_DASHBOARD, false)) {
                if (boostRailCool.get(player) != null)
                    if (boostRailCool.get(player))
                        return;
                Bukkit.getServer().getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        boostRailCool.put(player, false);
                    }
                }, 10L);

                if (player.getVehicle() != null) {
                    if (isSpecificKartType(player.getVehicle(), KartType.RacingKart)) {
                        getRacer(player).runStepDashBoardInitializeTask();
                        player.playSound(event.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5F, 1.0F);
                        return;
                    }
                }

                setPositiveItemSpeed(player
                        , (Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_SECOND.getValue()
                        , (Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_LEVEL.getValue()
                        , Sound.EXPLODE);
                //p.playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5F, 1.0F);
                boostRailCool.put(player, true);
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
        if (!YPLKart.isPluginEnabled(e.getEntity().getWorld()))
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
        if (ItemEnum.ITEMBOX_TOOL.isSimilar(p.getItemInHand())) {
            if (e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxName)) {
                e.getEntity().remove();
                MessageEnum.itemRemoveItemBox.sendConvertedMessage(p);
                e.setCancelled(true);
            }
        } else if (ItemEnum.ITEMBOX_TOOL_TIER2.isSimilar(p.getItemInHand())) {
            if (e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxNameTier2)) {
                e.getEntity().remove();
                MessageEnum.itemRemoveItemBox.sendConvertedMessage(p);
                e.setCancelled(true);
            }
        } else if (ItemEnum.FAKE_ITEMBOX_TOOL.isSimilar(p.getItemInHand())) {
            if (e.getEntity().getCustomName().equalsIgnoreCase(ItemBoxNameFake)
                    || e.getEntity().getCustomName().equalsIgnoreCase(FakeItemBoxName)) {
                e.getEntity().remove();
                MessageEnum.itemRemoveItemBox.sendConvertedMessage(p);
                e.setCancelled(true);
            }
        }
    }

    //〓

    @EventHandler
    public void onInteractwithDye(PlayerInteractEntityEvent e) {
        if (!YPLKart.isPluginEnabled(e.getPlayer().getWorld()))
            return;
        if (!(e.getRightClicked() instanceof Sheep))
            return;
        if (ItemEnum.isKeyItem(e.getPlayer().getItemInHand())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectDamaged(EntityInteractEvent e) {
        if (!YPLKart.isPluginEnabled(e.getEntity().getWorld()))
            return;
        if (e.getEntity().getCustomName() == null)
            return;

        Entity entity = e.getEntity();
        if (entity instanceof EnderCrystal) {
            if (entity.getCustomName().contains(ItemBoxName))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof FallingBlock) {
            if (entity.getCustomName().contains(ItemEnum.BANANA.getDisplayName()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectDamaged(EntityDamageEvent e) {
        if (!YPLKart.isPluginEnabled(e.getEntity().getWorld()))
            return;
        if (e.getEntity().getCustomName() == null)
            return;

        Entity entity = e.getEntity();
        if (entity instanceof EnderCrystal) {
            if (entity.getCustomName().contains(ItemBoxName))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof FallingBlock) {
            if (entity.getCustomName().contains(ItemEnum.BANANA.getDisplayName()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectExplode(EntityExplodeEvent e) {
        if (!YPLKart.isPluginEnabled(e.getEntity().getWorld()))
            return;
        if (e.getEntity().getCustomName() == null)
            return;

        Entity entity = e.getEntity();
        if (entity instanceof EnderCrystal) {
            if (entity.getCustomName().contains(ItemBoxName))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof FallingBlock) {
            if (entity.getCustomName().contains(ItemEnum.BANANA.getDisplayName()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectExplosionPrime(ExplosionPrimeEvent e) {
        if (!YPLKart.isPluginEnabled(e.getEntity().getWorld()))
            return;
        if (e.getEntity().getCustomName() == null)
            return;

        Entity entity = e.getEntity();
        if (entity instanceof EnderCrystal) {
            if (entity.getCustomName().contains(ItemBoxName))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof FallingBlock) {
            if (entity.getCustomName().contains(ItemEnum.BANANA.getDisplayName()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFallingBlockFalled(EntityChangeBlockEvent e) {
        Entity entity = e.getEntity();
        String customName = entity.getCustomName();

        if (!YPLKart.isPluginEnabled(entity.getWorld())) {
            return;
        }
        if (!(entity instanceof FallingBlock)) {
            return;
        }
        if (customName == null) {
            return;
        }
        if (customName.equalsIgnoreCase(ItemEnum.RED_TURTLE.getDisplayName())
                || customName.equalsIgnoreCase(ItemEnum.THORNED_TURTLE.getDisplayName())
                || customName.equalsIgnoreCase(ItemEnum.TURTLE.getDisplayName())
                || customName.equalsIgnoreCase(ItemEnum.BANANA.getDisplayName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!YPLKart.isPluginEnabled(player.getWorld())) {
            return;
        }
        if (!isStandBy(player.getUniqueId())) {
            return;
        }
        if (getRace(player.getUniqueId()).isGoal()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!YPLKart.isPluginEnabled(player.getWorld())) {
            return;
        }
        if (!isStandBy(player.getUniqueId())) {
            return;
        }
        if (getRace(player.getUniqueId()).isGoal()) {
            return;
        }

        if (ItemEnum.MENU.isSimilar(event.getItemDrop().getItemStack()))
            event.setCancelled(true);
        else
            event.getItemDrop().remove();
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public void itemTeresa(Player user) {
        List<Player> entry = getRacingPlayer(getRacer(user).getCircuitName());
        if (entry.size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        Util.setItemDecrease(user);
        entry.remove(user);
        Player target = entry.get(new Random().nextInt(entry.size()));

        Inventory targetinv = target.getInventory();

        ArrayList<ItemStack> targetitem = new ArrayList<ItemStack>();
        int itemSlotSize = (Integer) ConfigEnum.ITEM_SLOT.getValue()
                + getRacer(target).getCharacter().getAdjustMaxSlotSize();
        for (int i = 0; i < itemSlotSize; i++) {
            if (targetinv.getItem(i) != null)
                if (ItemEnum.isKeyItem(targetinv.getItem(i)))
                    targetitem.add(targetinv.getItem(i));
        }

        if (targetitem.isEmpty() || 0 < target.getNoDamageTicks()) {
            MessageEnum.itemTeresaNoItem.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            user.playSound(user.getLocation(), Sound.BURP, 1.0F, 0.5F);
        } else {
            ItemStack rob = targetitem.get(new Random().nextInt(targetitem.size()));
            targetinv.remove(rob);
            MessageEnum.itemTeresaRob.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()), rob });
            user.playSound(user.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            MessageEnum.itemTeresaRobbed.sendConvertedMessage(target, new Object[] { getCircuit(user.getUniqueId()), rob });
            target.playSound(user.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            ItemEnum.addItem(user, rob);
            user.updateInventory();
            target.updateInventory();
        }

        int effectsecond = (ItemEnum.TERESA.getEffectSecond() + getRacer(user).getCharacter()
                .getAdjustPositiveEffectSecond()) * 20;
        user.setNoDamageTicks(effectsecond);
        user.removePotionEffect(PotionEffectType.INVISIBILITY);
        user.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, effectsecond, 1));
    }

    public void itemThunder(Player user) {
        List<Player> list = getRacingPlayer(getRacer(user).getCircuitName());
        if (list.size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        Util.setItemDecrease(user);
        final World w = user.getWorld();

        int launchdamage = ItemEnum.THUNDER.getHitDamage()
                + getRacer(user).getCharacter().getAdjustAttackDamage();
        for (final Player p : list) {
            if (p.getUniqueId() == user.getUniqueId())
                continue;
            if (p.getNoDamageTicks() != 0)
                continue;

            int effectSecond = ItemEnum.THUNDER.getEffectSecond()
                    + getRacer(p).getCharacter().getAdjustNegativeEffectSecond();
            int effectLevel = ItemEnum.THUNDER.getEffectLevel()
                    + getRacer(p).getCharacter().getAdjustNegativeEffectLevel();

            p.removePotionEffect(PotionEffectType.SLOW);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW
                    , effectSecond * 20, effectLevel));

            final Location loc = p.getLocation();
            w.strikeLightningEffect(loc);
            Util.createSafeExplosion(user, loc, launchdamage, 5, 1.0F, false, Particle.CLOUD);
            p.playSound(loc, Sound.SUCCESSFUL_HIT, 0.5F, 1.0F);
            p.playSound(loc, Sound.LEVEL_UP, 0.5F, -1.0F);
        }
    }

    public void itemGesso(Player user) {
        List<Player> list = getRacingPlayer(getRacer(user).getCircuitName());
        if (list.size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        ArrayList<Player> target = new ArrayList<Player>();
        int rank = getRank(user);

        for (Player entry : list) {
            if (getRank(entry) < rank)
                target.add(entry);
        }
        if (target.isEmpty()) {
            MessageEnum.itemNoHigherPlayer.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        Util.setItemDecrease(user);
        final World w = user.getWorld();

        for (Player p : target) {
            if (p.getUniqueId() == user.getUniqueId())
                continue;
            if (p.getNoDamageTicks() != 0)
                continue;

            int effectSecond = ItemEnum.GESSO.getEffectSecond()
                    + getRacer(p).getCharacter().getAdjustNegativeEffectSecond();
            int effectLevel = ItemEnum.GESSO.getEffectLevel()
                    + getRacer(p).getCharacter().getAdjustNegativeEffectLevel();

            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS
                    , effectSecond * 20, effectLevel));

            p.playSound(p.getLocation(), Sound.SLIME_WALK, 2.0F, 1.0F);
        }
    }

    public void itemTurtle(Player p) {
        if (getRank(p) == 0)
            return;

        Util.setItemDecrease(p);
        FallingBlock b = p.getWorld().spawnFallingBlock(p.getLocation(), Material.HUGE_MUSHROOM_1, (byte) 7);
        b.setCustomName(ItemEnum.TURTLE.getDisplayName());
        b.setCustomNameVisible(false);
        b.setDropItem(false);

        new ItemTurtleTask(p, b, Util.getForwardLocationFromYaw(p.getLocation(), 3), 60).runTaskTimer(pl, 0, 1);
    }

    public void itemRedturtle(Player user) {
        if (getRacingPlayer(getRacer(user).getCircuitName()).size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        if (getRank(user) == 0)
            return;
        if (getNearestUnpassedCheckpoint(user.getLocation(), checkPointDetectRadius + 20, getRacer(user)) == null) {
            MessageEnum.itemNoCheckpoint.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        Util.setItemDecrease(user);

        FallingBlock turtle = user.getWorld().spawnFallingBlock(user.getEyeLocation(), Material.HUGE_MUSHROOM_1,
                (byte) 5);
        turtle.setCustomName(ItemEnum.RED_TURTLE.getDisplayName());
        turtle.setCustomNameVisible(false);
        turtle.setDropItem(false);

        int rank = getRank(user);
        Player target = null;
        if (rank == 1)
            new ItemDyedTurtleTask(user, getPlayerfromRank(getRacer(user).getCircuitName(), rank + 1), turtle, false, true)
                    .runTaskTimer(YPLKart.getInstance(), 0, 1);
        else
            new ItemDyedTurtleTask(user, getPlayerfromRank(getRacer(user).getCircuitName(), rank - 1), turtle, false, false)
                    .runTaskTimer(YPLKart.getInstance(), 0, 1);
    }

    public void itemThornedturtle(Player user) {
        if (getRacingPlayer(getRacer(user).getCircuitName()).size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }
        int rank = getRank(user);
        if (rank == 0)
            return;
        else if (rank == 1) {
            MessageEnum.itemHighestPlayer.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()),
                    ItemEnum.THORNED_TURTLE.getItem() });
            return;
        }
        if (getNearestUnpassedCheckpoint(user.getLocation(), checkPointDetectRadius + 20, getRacer(user)) == null) {
            MessageEnum.itemNoCheckpoint.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        Util.setItemDecrease(user);

        FallingBlock turtle = user.getWorld().spawnFallingBlock(user.getEyeLocation(), Material.HUGE_MUSHROOM_1,
                (byte) 6);
        turtle.setCustomName(ItemEnum.THORNED_TURTLE.getDisplayName());
        turtle.setCustomNameVisible(false);
        turtle.setDropItem(false);

        new ItemDyedTurtleTask(user, getPlayerfromRank(getRacer(user).getCircuitName(), 1), turtle, true, false).runTaskTimer(
                pl, 0, 1);
    }

    public void itemKiller(final Player user) {
        if (getRank(user) == 0)
            return;

        Entity unpassedcheckpoint = getNearestUnpassedCheckpoint(user.getLocation(), checkPointDetectRadius + 20,
                getRacer(user));

        if (unpassedcheckpoint == null) {
            MessageEnum.itemNoCheckpoint.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        final Racer r = getRacer(user);

        if (r.getUsingKiller() != null) {
            MessageEnum.itemAlreadyUsing.sendConvertedMessage(user, new Object[] { getCircuit(user.getUniqueId()) });
            return;
        }

        Util.setItemDecrease(user);

        Character character = r.getCharacter();

        int life = ItemEnum.KILLER.getEffectSecond() + character.getAdjustPositiveEffectSecond();
        new SendCountDownTitleTask(user, life, MessageEnum.titleUsingKiller.getMessage()).runTaskTimer(
                YPLKart.getInstance(), 0, 1);

        r.runKillerInitializeTask(life, unpassedcheckpoint);
    }

    public static void setNegativeItemSpeed(final Player p, int second, int level, Sound sound) {
        p.playSound(p.getLocation(), sound, 0.5F, -1.0F);
        second = (second + getRacer(p).getCharacter().getAdjustNegativeEffectSecond()) * 20;

        Util.setPotionEffect(p, PotionEffectType.SLOW, second, level
                + getRacer(p).getCharacter().getAdjustNegativeEffectLevel());

        getRacer(p).setItemNegativeSpeedTask(
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                        p.removePotionEffect(PotionEffectType.SLOW);
                        getRacer(p).setItemNegativeSpeedTask(null);
                    }
                }, second)
                );
    }

    public static void setPositiveItemSpeed(final Player p, int second, int level, Sound sound) {
        p.playSound(p.getLocation(), sound, 0.5F, -1.0F);
        final Racer race = getRacer(p);

        second = (second + race.getCharacter().getAdjustPositiveEffectSecond()) * 20;
        Util.setPotionEffect(p, PotionEffectType.SPEED, second, level
                + race.getCharacter().getAdjustPositiveEffectLevel());
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
