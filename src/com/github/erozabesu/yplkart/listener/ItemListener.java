package com.github.erozabesu.yplkart.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
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
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.ItemDyedTurtle;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.task.ItemStarTask;
import com.github.erozabesu.yplkart.task.ItemTurtleTask;
import com.github.erozabesu.yplkart.task.SendCountDownTitleTask;
import com.github.erozabesu.yplkart.utils.CheckPointUtil;
import com.github.erozabesu.yplkart.utils.RaceEntityUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class ItemListener extends RaceManager implements Listener {

    public ItemListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, YPLKart.getInstance());
    }

    @EventHandler
    public void unloadJammerEntityExistChunk(ChunkUnloadEvent event) {
        if (!YPLKart.isPluginEnabled(event.getWorld())) {
            return;
        }

        Chunk chunk = event.getChunk();
        if (RaceEntityUtil.getJammerEntityExistChunkArray().contains(chunk)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void removeCheckPoint(PlayerMoveEvent event) {
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld())) {
            return;
        }

        Player player = event.getPlayer();
        if (!Permission.hasPermission(player, Permission.OP_CMD_CIRCUIT, true)) {
            return;
        }

        if (!ItemEnum.CHECKPOINT_TOOL.isSimilar(player.getItemInHand())
                && !ItemEnum.CHECKPOINT_TOOL_TIER2.isSimilar(player.getItemInHand())
                && !ItemEnum.CHECKPOINT_TOOL_TIER3.isSimilar(player.getItemInHand()))
            return;

        List<Entity> list = player.getNearbyEntities(1, 1, 1);
        for (Entity entity : list) {
            String circuitName = player.getItemInHand().getItemMeta().getLore().get(0);
            if (CheckPointUtil.isSpecificCircuitCheckPointEntity(entity, circuitName)) {
                if (entity.getLocation().distance(player.getLocation()) < 1.5) {
                    player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                    entity.remove();
                    MessageEnum.itemRemoveCheckPoint.sendConvertedMessage(player);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void useTool(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // プラグインが有効でない場合return
        if (!YPLKart.isPluginEnabled(player.getWorld())) {
            return;
        }

        // 手にアイテムを持っていない場合return
        if (player.getItemInHand() == null) {
            return;
        }

        ItemEnum itemEnum = ItemEnum.getItemByItemStack(player.getItemInHand());

        // 手に持っているアイテムがキーアイテムでない場合return
        if (itemEnum == null) {
            return;
        }

        // キーアイテムの使用パーミッションを所有していない場合return
        if (!Permission.hasPermission(player, itemEnum.getUsePermission(), false)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Racer racer = RaceManager.getRacer(uuid);
        event.setCancelled(true);

        Action clickAction = event.getAction();

        // 右クリック専用アイテム
        if (clickAction == Action.RIGHT_CLICK_BLOCK || clickAction == Action.RIGHT_CLICK_AIR) {
            // スタンバイ状態、かつスタートしていない状態のみメニューアイテムを利用
            if (ItemEnum.MENU.isSimilar(player.getItemInHand())) {
                if (racer.isStandby() && !racer.isStart()) {
                    showSelectMenu(player, true);
                    return;
                }
            }

            // 空気ブロック以外のブロックを右クリックした場合のみ動作するアイテム
            if (clickAction == Action.RIGHT_CLICK_BLOCK) {

                Block clickedBlock = event.getClickedBlock().getRelative(event.getBlockFace());

                // アイテムボックスツール
                if (ItemEnum.ITEMBOX_TOOL.isSimilar(player.getItemInHand())) {
                    RaceEntityUtil.createItemBox(clickedBlock.getLocation(), 1);
                    clickedBlock.getWorld().playSound(clickedBlock.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                    return;

                // アイテムボックスツールティアー2
                } else if (ItemEnum.ITEMBOX_TOOL_TIER2.isSimilar(player.getItemInHand())) {
                    RaceEntityUtil.createItemBox(clickedBlock.getLocation(), 2);
                    clickedBlock.getWorld().playSound(clickedBlock.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                    return;

                // フェイクアイテムボックスツール
                } else if (ItemEnum.FAKE_ITEMBOX_TOOL.isSimilar(player.getItemInHand())) {
                    RaceEntityUtil.createFakeItemBox(clickedBlock.getLocation());
                    clickedBlock.getWorld().playSound(clickedBlock.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                    return;
                }
            }
        }

        // 設置するチェックポイントが可視チェックポイントかどうか
        boolean isVisible = clickAction == Action.LEFT_CLICK_BLOCK || clickAction == Action.LEFT_CLICK_AIR;

        //チェックポイントツール
        if (ItemEnum.CHECKPOINT_TOOL.isSimilar(player.getItemInHand())) {
            CheckPointUtil.createCheckPointEntity(player.getLocation(), player.getItemInHand().getItemMeta().getLore().get(0), isVisible);
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);

        //チェックポイントツールTier2
        } else if (ItemEnum.CHECKPOINT_TOOL_TIER2.isSimilar(player.getItemInHand())) {
            CheckPointUtil.createCheckPointEntity(player.getLocation(), player.getItemInHand().getItemMeta().getLore().get(0), isVisible);
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);

        //チェックポイントツールTier3
        } else if (ItemEnum.CHECKPOINT_TOOL_TIER3.isSimilar(player.getItemInHand())) {
            CheckPointUtil.createCheckPointEntity(player.getLocation(), player.getItemInHand().getItemMeta().getLore().get(0), isVisible);
            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
        }
    }

    @EventHandler
    public void useRaceItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        //プラグインが有効でない場合return
        if (!YPLKart.isPluginEnabled(player.getWorld())) {
            return;
        }

        Action clickAction = event.getAction();

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
        Racer racer = RaceManager.getRacer(uuid);
        event.setCancelled(true);

        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        // レース中、かつゴールしていない状態以外はreturn
        if (!racer.isStillRacing()) {
            return;
        }

        //アイテムを使用して間もない状態の場合return : issue #112
        if (racer.isItemUseCooling()) {
            return;
        }

        //アイテムを使用したフラグをtrueにセットする : issue #112
        racer.runItemUseCoolingTask();

        //ダッシュきのこ
        if (ItemEnum.MUSHROOM.isSimilar(player.getItemInHand())) {
            Util.setItemDecrease(player);
            racer.runPositiveItemSpeedTask(ItemEnum.MUSHROOM.getEffectSecond(), ItemEnum.MUSHROOM.getEffectLevel(), Sound.EXPLODE);

        //パワフルダッシュキノコ
        } else if (ItemEnum.POWERFULL_MUSHROOM.isSimilar(player.getItemInHand())) {
            Util.setItemDecrease(player);
            racer.runPositiveItemSpeedTask(ItemEnum.POWERFULL_MUSHROOM.getEffectSecond(),
                    ItemEnum.POWERFULL_MUSHROOM.getEffectLevel(), Sound.EXPLODE);
            player.getWorld().playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);

        //バナナ
        } else if (ItemEnum.BANANA.isSimilar(player.getItemInHand())) {
            Util.setItemDecrease(player);
            Location location = Util.getForwardLocationFromYaw(player.getLocation().add(0, 0.5, 0), -5).getBlock().getLocation()
                    .add(0.5, 0, 0.5);
            player.getWorld().playSound(player.getLocation(), Sound.SLIME_WALK, 1.0F, 1.0F);
            RaceEntityUtil.createBanana(circuit, location);

        //にせアイテムボックス
        } else if (ItemEnum.FAKE_ITEMBOX.isSimilar(player.getItemInHand())) {
            Util.setItemDecrease(player);
            Location createLocation = Util.getForwardLocationFromYaw(player.getLocation().clone().add(0, 0.5, 0), -5);
            EnderCrystal endercrystal = RaceEntityUtil.createDesposableFakeItemBox(createLocation, circuit);
            circuit.addJammerEntity(endercrystal);
            player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 0);

        //サンダー
        } else if (ItemEnum.THUNDER.isSimilar(player.getItemInHand())) {
            itemThunder(player);

        //スーパースター
        } else if (ItemEnum.STAR.isSimilar(player.getItemInHand())) {
            new ItemStarTask(player).runTaskTimer(YPLKart.getInstance(), 0, 1);

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

    @EventHandler
    public void onInteractObjectEntity(PlayerMoveEvent event) {
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld())) {
            return;
        }

        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (!RaceManager.getRacer(player).isStillRacing()) {
            return;
        }

        List<Entity> entities = player.getNearbyEntities(0.7, 0.7, 0.7);
        for (final Entity entity : entities) {
            if (entity.getCustomName() != null && !entity.getCustomName().equalsIgnoreCase("")) {
                if (entity instanceof ArmorStand) {
                    if (Permission.hasPermission(player, Permission.INTERACT_BANANA, false)) {
                        if (RaceEntityUtil.isBananaEntity(entity)) {
                            RaceEntityUtil.collideBanana(player, entity);
                            return;
                        }
                    }
                } else if (entity instanceof EnderCrystal) {
                    if (Permission.hasPermission(player, Permission.INTERACT_FAKEITEMBOX, false)) {
                        // ツールで設置した偽アイテムボックス
                        if (RaceEntityUtil.isNormalFakeItemBox(entity)) {
                            RaceEntityUtil.collideNormalFakeItemBox(player, entity);
                            return;

                        // 復活するタイプの偽アイテムボックス
                        } else if (RaceEntityUtil.isDisposableFakeItemBox(entity)) {
                            RaceEntityUtil.collideDisposableFakeItemBox(player, entity);
                            return;
                        }
                    }

                    if (Permission.hasPermission(player, Permission.INTERACT_ITEMBOX, false)) {
                        RaceEntityUtil.collideItemBox(player, entity);
                        return;
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

        Player player = event.getPlayer();
        Racer racer = RaceManager.getRacer(player);
        if (!racer.isStillRacing()) {
            return;
        }

        if (Util.getGroundBlockMaterial(player.getLocation(), 1) == Material.PISTON_BASE
                || Util.getGroundBlockMaterial(player.getLocation(), 1) == Material.PISTON_STICKY_BASE) {
            if (Permission.hasPermission(player, Permission.INTERACT_DASHBOARD, false)) {
                racer.runStepDashBoardTask();
            }
        }
    }

    @EventHandler
    public void onItemBoxDamagedByPlayer(EntityDamageByEntityEvent event) {
        if (!YPLKart.isPluginEnabled(event.getEntity().getWorld())) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        ItemStack handItem = player.getItemInHand();
        if (handItem == null) {
            return;
        }

        Entity entity = event.getEntity();
        if (ItemEnum.ITEMBOX_TOOL.isSimilar(handItem)) {
            if (RaceEntityUtil.isNormalItemBoxEntity(entity)) {
                event.getEntity().remove();
                MessageEnum.itemRemoveItemBox.sendConvertedMessage(player);
                event.setCancelled(true);
            }
        } else if (ItemEnum.ITEMBOX_TOOL_TIER2.isSimilar(handItem)) {
            if (RaceEntityUtil.isHighGradeItemBoxEntity(entity)) {
                event.getEntity().remove();
                MessageEnum.itemRemoveItemBox.sendConvertedMessage(player);
                event.setCancelled(true);
            }
        } else if (ItemEnum.FAKE_ITEMBOX_TOOL.isSimilar(handItem)) {
            if (RaceEntityUtil.isNormalFakeItemBox(entity) || RaceEntityUtil.isDisposableFakeItemBox(entity)) {
                event.getEntity().remove();
                MessageEnum.itemRemoveItemBox.sendConvertedMessage(player);
                event.setCancelled(true);
            }
        }
    }

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
    public void onJammerObjectInteract(EntityInteractEvent event) {
        if (!YPLKart.isPluginEnabled(event.getEntity().getWorld())) {
            return;
        }
        if (event.getEntity().getCustomName() == null || event.getEntity().getCustomName().equalsIgnoreCase("")) {
            return;
        }

        Entity entity = event.getEntity();
        if (RaceEntityUtil.isBananaEntity(entity)
                || RaceEntityUtil.isRedTurtleEntity(entity)
                || RaceEntityUtil.isDisposableFakeItemBox(entity)
                || RaceEntityUtil.isHighGradeItemBoxEntity(entity)
                || RaceEntityUtil.isNormalFakeItemBox(entity)
                || RaceEntityUtil.isNormalItemBoxEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectDamaged(EntityDamageEvent event) {
        if (!YPLKart.isPluginEnabled(event.getEntity().getWorld())) {
            return;
        }
        if (!event.getCause().equals(DamageCause.VOID)) {
            return;
        }
        if (event.getEntity().getCustomName() == null || event.getEntity().getCustomName().equalsIgnoreCase("")) {
            return;
        }

        Entity entity = event.getEntity();
        if (RaceEntityUtil.isBananaEntity(entity)
                || RaceEntityUtil.isRedTurtleEntity(entity)
                || RaceEntityUtil.isDisposableFakeItemBox(entity)
                || RaceEntityUtil.isHighGradeItemBoxEntity(entity)
                || RaceEntityUtil.isNormalFakeItemBox(entity)
                || RaceEntityUtil.isNormalItemBoxEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectExplode(EntityExplodeEvent event) {
        if (!YPLKart.isPluginEnabled(event.getEntity().getWorld())) {
            return;
        }
        if (event.getEntity().getCustomName() == null || event.getEntity().getCustomName().equalsIgnoreCase("")) {
            return;
        }

        Entity entity = event.getEntity();
        if (RaceEntityUtil.isBananaEntity(entity)
                || RaceEntityUtil.isRedTurtleEntity(entity)
                || RaceEntityUtil.isDisposableFakeItemBox(entity)
                || RaceEntityUtil.isHighGradeItemBoxEntity(entity)
                || RaceEntityUtil.isNormalFakeItemBox(entity)
                || RaceEntityUtil.isNormalItemBoxEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJammerObjectExplosionPrime(ExplosionPrimeEvent event) {
        if (!YPLKart.isPluginEnabled(event.getEntity().getWorld())) {
            return;
        }
        if (event.getEntity().getCustomName() == null || event.getEntity().getCustomName().equalsIgnoreCase("")) {
            return;
        }

        Entity entity = event.getEntity();
        if (RaceEntityUtil.isBananaEntity(entity)
                || RaceEntityUtil.isRedTurtleEntity(entity)
                || RaceEntityUtil.isDisposableFakeItemBox(entity)
                || RaceEntityUtil.isHighGradeItemBoxEntity(entity)
                || RaceEntityUtil.isNormalFakeItemBox(entity)
                || RaceEntityUtil.isNormalItemBoxEntity(entity)) {
            event.setCancelled(true);
        }
    }

    /**
     * スタンバイフェーズ以降のドロップアイテムの取得を禁止する。
     * @param event PlayerPickupItemEvent
     */
    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!YPLKart.isPluginEnabled(player.getWorld())) {
            return;
        }

        Racer racer = RaceManager.getRacer(player);
        if (racer.isStillInRace()) {
            event.setCancelled(true);
        }
    }

    /**
     * スタンバイフェーズ以降のレース中に捨てたアイテムを削除する。
     * @param event PlayerDropItemEvent
     */
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!YPLKart.isPluginEnabled(player.getWorld())) {
            return;
        }

        Racer racer = getRacer(player);

        // スタンバイフェーズ以降ではない、もしくはゴールしている場合はreturn
        if (!racer.isStillInRace()) {
            return;
        }

        // メニューアイテムは捨てることができない
        if (ItemEnum.MENU.isSimilar(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        } else {
            event.getItemDrop().remove();
        }
    }

    //〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public void itemTeresa(Player user) {
        Racer racer = RaceManager.getRacer(user);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        List<Player> racingPlayerList = circuit.getOnlineRacingPlayerList();

        // オンラインの走行者が自分以外に居ない場合はreturn
        if (racingPlayerList.size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, circuitParts);
            return;
        }

        Util.setItemDecrease(user);
        racingPlayerList.remove(user);
        Player target = racingPlayerList.get(new Random().nextInt(racingPlayerList.size()));

        Inventory targetinv = target.getInventory();

        ArrayList<ItemStack> targetitem = new ArrayList<ItemStack>();
        int itemSlotSize = (Integer) ConfigEnum.ITEM_SLOT.getValue() + getRacer(target).getCharacter().getAdjustMaxSlotSize();
        for (int i = 0; i < itemSlotSize; i++) {
            if (targetinv.getItem(i) != null)
                if (ItemEnum.isKeyItem(targetinv.getItem(i)))
                    targetitem.add(targetinv.getItem(i));
        }

        if (targetitem.isEmpty() || 0 < target.getNoDamageTicks()) {
            MessageEnum.itemTeresaNoItem.sendConvertedMessage(user, circuitParts);
            user.playSound(user.getLocation(), Sound.BURP, 1.0F, 0.5F);
        } else {
            ItemStack rob = targetitem.get(new Random().nextInt(targetitem.size()));
            targetinv.remove(rob);
            MessageEnum.itemTeresaRob.sendConvertedMessage(user, circuitParts, MessageParts.getMessageParts(rob));
            user.playSound(user.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            MessageEnum.itemTeresaRobbed.sendConvertedMessage(target, circuitParts, MessageParts.getMessageParts(rob));
            target.playSound(user.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            ItemEnum.addItem(user, rob);
            user.updateInventory();
            target.updateInventory();
        }
    }

    public void itemThunder(Player user) {
        Racer racer = RaceManager.getRacer(user);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        List<Player> racingPlayerList = circuit.getOnlineRacingPlayerList();

        // オンラインの走行者が自分以外に居ない場合はreturn
        if (racingPlayerList.size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, circuitParts);
            return;
        }

        Util.setItemDecrease(user);
        World world = user.getWorld();

        int launchdamage = ItemEnum.THUNDER.getHitDamage() + racer.getCharacter().getAdjustAttackDamage();
        for (final Player player : racingPlayerList) {
            if (player.getUniqueId() == user.getUniqueId()) {
                continue;
            }
            if (player.getNoDamageTicks() != 0) {
                continue;
            }

            int effectSecond = ItemEnum.THUNDER.getEffectSecond() + getRacer(player).getCharacter().getAdjustNegativeEffectSecond();
            int effectLevel = ItemEnum.THUNDER.getEffectLevel() + getRacer(player).getCharacter().getAdjustNegativeEffectLevel();

            player.removePotionEffect(PotionEffectType.SLOW);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, effectSecond * 20, effectLevel));

            Location location = player.getLocation();
            world.strikeLightningEffect(location);
            Util.createSafeExplosion(user, location, launchdamage, 5, 0.4F, 1.0F, Particle.CLOUD);
            player.playSound(location, Sound.SUCCESSFUL_HIT, 0.5F, 1.0F);
            player.playSound(location, Sound.LEVEL_UP, 0.5F, -1.0F);
        }
    }

    public void itemGesso(Player user) {
        Racer racer = RaceManager.getRacer(user);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        List<Player> racingPlayerList = circuit.getOnlineRacingPlayerList();

        // オンラインの走行者が自分以外に居ない場合はreturn
        if (racingPlayerList.size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, circuitParts);
            return;
        }

        List<Player> targetPlayerList = new ArrayList<Player>();
        int ownRank = getRank(user);

        // 自身より上位のプレイヤーを抽出
        for (Player otherPlayer : racingPlayerList) {
            if (getRank(otherPlayer) < ownRank)
                targetPlayerList.add(otherPlayer);
        }

        // 自身より上位のプレイヤーが居ない場合return
        if (targetPlayerList.isEmpty()) {
            MessageEnum.itemNoHigherPlayer.sendConvertedMessage(user, circuitParts);
            return;
        }

        Util.setItemDecrease(user);

        for (Player targetPlayer : targetPlayerList) {
            if (targetPlayer.getUniqueId() == user.getUniqueId()) {
                continue;
            }

            if (targetPlayer.getNoDamageTicks() != 0) {
                continue;
            }

            int effectSecond = ItemEnum.GESSO.getEffectSecond()
                    + getRacer(targetPlayer).getCharacter().getAdjustNegativeEffectSecond();
            int effectLevel = ItemEnum.GESSO.getEffectLevel()
                    + getRacer(targetPlayer).getCharacter().getAdjustNegativeEffectLevel();

            targetPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS
                    , effectSecond * 20, effectLevel));

            targetPlayer.playSound(targetPlayer.getLocation(), Sound.SLIME_WALK, 2.0F, 1.0F);
        }
    }

    public void itemTurtle(Player player) {
        Racer racer = RaceManager.getRacer(player);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        if (RaceManager.getRank(player) == 0) {
            return;
        }

        Util.setItemDecrease(player);

        ArmorStand turtle = RaceEntityUtil.createTurtle(circuit, player.getLocation(), ItemEnum.TURTLE);
        new ItemTurtleTask(turtle, player).runTaskTimer(YPLKart.getInstance(), 0, 1);
    }

    public void itemRedturtle(Player user) {
        Racer racer = RaceManager.getRacer(user);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);

        // オンラインの走行者が自分以外に居ない場合はreturn
        if (circuit.getOnlineRacingRacerList().size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, circuitParts);
            return;
        }

        if (getRank(user) == 0) {
            return;
        }

        int detectCheckPointRadius = (Integer) ConfigEnum.ITEM_DETECT_CHECKPOINT_RADIUS_TIER3.getValue();
        Entity firstCheckPoint = CheckPointUtil.getNearestCheckpoint(circuit.getCircuitName(), user.getLocation(), detectCheckPointRadius);
        if (firstCheckPoint == null) {
            MessageEnum.itemNoCheckpoint.sendConvertedMessage(user, circuitParts);
            return;
        }

        Util.setItemDecrease(user);
        ArmorStand turtle = RaceEntityUtil.createTurtle(circuit, user.getEyeLocation().add(0.0D, -1.5D, 0.0D), ItemEnum.RED_TURTLE);

        int ownRank = RaceManager.getRank(user);
        int targetRank = ownRank == 1 ? ownRank + 1 : ownRank - 1;
        Player target = RaceManager.getPlayerfromRank(circuit, targetRank);

        new ItemDyedTurtle(circuit.getCircuitName(), turtle, firstCheckPoint, user, target, ItemEnum.RED_TURTLE, ownRank == 1).runTaskTimer(YPLKart.getInstance(), 0, 1);
    }

    public void itemThornedturtle(Player user) {
        Racer racer = RaceManager.getRacer(user);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);

        // オンラインの走行者が自分以外に居ない場合はreturn
        if (circuit.getOnlineRacingRacerList().size() <= 1) {
            MessageEnum.itemNoPlayer.sendConvertedMessage(user, circuitParts);
            return;
        }

        int ownRank = RaceManager.getRank(user);
        if (ownRank == 0) {
            return;
        }

        Entity firstCheckPoint = CheckPointUtil.getNearestCheckpoint(circuit.getCircuitName(), user.getLocation(), 50.0D);
        if (firstCheckPoint == null) {
            MessageEnum.itemNoCheckpoint.sendConvertedMessage(user, circuitParts);
            return;
        }

        Util.setItemDecrease(user);

        ArmorStand turtle = RaceEntityUtil.createTurtle(circuit, user.getEyeLocation().add(0.0D, -1.5D, 0.0D), ItemEnum.THORNED_TURTLE);

        int targetRank = ownRank == 1 ? 2 : 1;
        Player target = RaceManager.getPlayerfromRank(circuit, targetRank);
        new ItemDyedTurtle(circuit.getCircuitName(), turtle, firstCheckPoint, user, target, ItemEnum.THORNED_TURTLE, false).runTaskTimer(YPLKart.getInstance(), 0, 1);
    }

    public void itemKiller(final Player user) {
        if (getRank(user) == 0) {
            return;
        }

        Racer racer = RaceManager.getRacer(user);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);

        // 通過済みのチェックポイントが1つ以上ない場合はreturn
        if (racer.getPassedCheckPointList().size() < 1) {
            MessageEnum.itemNoCheckpoint.sendConvertedMessage(user, circuitParts);
            return;
        }

        Entity unpassedcheckpoint = CheckPointUtil.getInSightAndDetectableNearestUnpassedCheckpoint(racer, user.getLocation(), 180.0F);

        if (unpassedcheckpoint == null) {
            MessageEnum.itemNoCheckpoint.sendConvertedMessage(user, circuitParts);
            return;
        }

        if (racer.getUsingKiller() != null) {
            MessageEnum.itemAlreadyUsing.sendConvertedMessage(user, circuitParts);
            return;
        }

        Util.setItemDecrease(user);

        Character character = racer.getCharacter();

        int life = ItemEnum.KILLER.getEffectSecond() + character.getAdjustPositiveEffectSecond();
        new SendCountDownTitleTask(user, life, MessageEnum.titleUsingKiller.getMessage()).runTaskTimer(
                YPLKart.getInstance(), 0, 1);

        racer.runKillerInitializeTask(life, unpassedcheckpoint);
    }
}
