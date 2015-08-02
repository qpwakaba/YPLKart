package com.github.erozabesu.yplkart.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.Permission;
import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.EnumSelectMenu;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.task.SendBlinkingTitleTask;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class DataListener implements Listener {
    private YPLKart pl;

    public DataListener(YPLKart plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        pl = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!YPLKart.isPluginEnabled(e.getWorld()))
            return;
        DisplayKartConfig.respawnKart(e.getChunk());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (!YPLKart.isPluginEnabled(e.getWorld()))
            return;
        DisplayKartConfig.respawnKart(e.getWorld());
    }

    /**
     * 左クリックによるディスプレイカートの搭乗、削除。<br>
     * アーマースタンドエンティティのMarkerNBTをtrueにしている影響で、<br>
     * クリックによる接触判定がフックできないため、左クリック時に周囲のカートエンティティを取得し、<br>
     * 間接的に接触している。<br>
     * 左クリックで搭乗、スニーク＋左クリックで削除を行う。
     * @param event
     */
    @EventHandler
    public void interactDisplayKart(PlayerInteractEvent event) {
        //プラグインが有効かどうか
        if (YPLKart.isPluginEnabled(event.getPlayer().getWorld())) {

            //左クリックした場合
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                Player player = event.getPlayer();

                //周囲のエンティティからカートエンティティを検出し格納
                List<Entity> kartEntitiesList = new ArrayList<Entity>();
                for (Entity nearbyEntity : player.getNearbyEntities(1.0D, 1.0D, 1.0D)) {
                    if (RaceManager.isKartEntity(nearbyEntity)) {

                        //レーシングカートは除外
                        if (!RaceManager.isSpecificKartType(nearbyEntity, KartType.RacingKart)) {
                            kartEntitiesList.add(nearbyEntity);
                        }
                    }
                }

                //周囲にカートエンティティがいるかどうか
                if (0 < kartEntitiesList.size()) {

                    //最寄のカートエンティティを取得
                    Entity kartEntity = Util.getNearestEntity(kartEntitiesList, player.getLocation());

                    //スニーク＋左クリック
                    if (player.isSneaking()) {

                        //パーミッションを所有しているかどうか
                        if (Permission.hasPermission(player, Permission.OP_KART_REMOVE, false)) {

                            //ローカルコンフィグファイルから削除
                            DisplayKartConfig.deleteDisplayKart(player, kartEntity.getCustomName());

                            //エンティティをデスポーン
                            kartEntity.remove();
                        }
                    //左クリックのみ
                    } else {

                        //搭乗者がいない場合、搭乗させる
                        if (kartEntity.getPassenger() == null) {
                            kartEntity.setPassenger(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void removeCheckPoint(PlayerMoveEvent e) {
        if (!YPLKart.isPluginEnabled(e.getFrom().getWorld()))
            return;
        Player p = e.getPlayer();
        if (!ItemEnum.CHECKPOINT_TOOL.isSimilar(p.getItemInHand()))
            return;

        List<Entity> list = p.getNearbyEntities(1, 1, 1);
        for (Entity entity : list) {
            if (RaceManager.isCustomWitherSkull(
                    entity, p.getItemInHand().getItemMeta().getLore().get(0)))
                if (entity.getLocation().distance(p.getLocation()) < 1.5) {
                    p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                    entity.remove();
                    MessageEnum.itemRemoveCheckPoint.sendConvertedMessage(p);
                    break;
                }
        }
    }

    @EventHandler
    public void saveLastStepBlock(PlayerMoveEvent e) {
        if (!YPLKart.isPluginEnabled(e.getFrom().getWorld()))
            return;
        if (!RaceManager.isStandBy(e.getPlayer().getUniqueId()))
            return;

        RaceManager.getRacer(e.getPlayer()).setLastStepBlock(Util.getGroundBlockID(e.getFrom()));
    }

    //スタンバイ状態～レースが開始されるまでの間、水平方向への移動を禁止する
    @EventHandler
    public void cancelMove(PlayerMoveEvent e) {
        if (!YPLKart.isPluginEnabled(e.getFrom().getWorld()))
            return;
        if (RaceManager.isStandBy(e.getPlayer().getUniqueId())
                && !RaceManager.isRacing(e.getPlayer().getUniqueId())) {
            if (!e.getFrom().equals(e.getTo())) {
                Location from = e.getFrom();
                Location to = e.getTo();

                if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
                    return;
                e.getPlayer().teleport(Util.adjustBlockLocation(from).add(0, 1, 0));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void saveLapcount(PlayerMoveEvent e) {
        if (!YPLKart.isPluginEnabled(e.getFrom().getWorld()))
            return;
        Player p = e.getPlayer();
        if (!RaceManager.isStandBy(p.getUniqueId()))
            return;
        Racer r = RaceManager.getRacer(p);
        if (r.getGoal())
            return;
        if (r.getLapStepCool())
            return;

        int lapcount = r.getLapCount();

        if (r.getLastStepBlock().equalsIgnoreCase((String) ConfigEnum.START_BLOCK_ID.getValue())) {
            if (Util.getGroundBlockID(e.getTo()).equalsIgnoreCase(
                    (String) ConfigEnum.GOAL_BLOCK_ID.getValue())) {
                //正常ルート
                if (lapcount == CircuitConfig.getCircuitData(r.getEntry()).getNumberOfLaps()) {
                    r.setGoal();
                } else {
                    if (lapcount == 0) {
                        r.setStart(true, e.getFrom(), e.getTo());
                    } else {
                        MessageEnum.raceUpdateLap.sendConvertedMessage(p,
                                new Object[] { (lapcount + 1), RaceManager.getCircuit(r.getEntry()) });
                    }
                    r.setLapCount(lapcount + 1);
                }
                r.setCool();
            }
        } else if (r.getLastStepBlock().equalsIgnoreCase(
                (String) ConfigEnum.GOAL_BLOCK_ID.getValue())) {
            if (Util.getGroundBlockID(e.getTo()).equalsIgnoreCase(
                    (String) ConfigEnum.START_BLOCK_ID.getValue())) {
                //逆走
                MessageEnum.raceReverseRun.sendConvertedMessage(p, RaceManager.getCircuit(r.getEntry()));
                if (lapcount <= 0)
                    r.setLapCount(0);
                else {
                    r.setLapCount(lapcount - 1);
                }
                r.setCool();
            }
        }
    }

    @EventHandler
    public void RunningRank(PlayerMoveEvent e) {//順位
        if (!YPLKart.isPluginEnabled(e.getFrom().getWorld())) {
            return;
        }
        Player p = e.getPlayer();
        if (!RaceManager.isRacing(p.getUniqueId())) {
            return;
        }
        if (RaceManager.getRacer(p).getLapCount() < 1) {
            return;
        }

        Racer r = RaceManager.getRacer(p);

        ArrayList<String> checkpoint = RaceManager.getNearbyCheckpointID(
                e.getPlayer().getLocation(), RaceManager.checkPointDetectRadius, r.getEntry());
        if (checkpoint == null) {
            return;
        }

        Iterator<String> i = checkpoint.iterator();
        String id;
        String lap = r.getLapCount() <= 0 ? "" : String.valueOf(r.getLapCount());
        while (i.hasNext()) {
            id = i.next();
            r.addPassedCheckPoint(lap + id);
            r.setLastPassedCheckPoint(id);
        }
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e) {
        if (!YPLKart.isPluginEnabled(e.getEntity().getWorld())) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!RaceManager.isRacing(((Player) e.getEntity()).getUniqueId())) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!YPLKart.isPluginEnabled(e.getPlayer().getWorld())) {
            return;
        }

        final Player p = e.getPlayer();
        if (RaceManager.isStandBy(p.getUniqueId())) {
            Racer r = RaceManager.getRacer(p);
            p.teleport(r.getGoalPositionOnQuit());
            Scoreboards.showBoard(p.getUniqueId());
            r.recoveryPhysicalOnQuit();
            r.recoveryInventoryOnQuit();
            r.recoveryKart();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!YPLKart.isPluginEnabled(e.getPlayer().getWorld())) {
            return;
        }

        Player player = e.getPlayer();
        Racer r = RaceManager.getRacer(player);

        //ディスプレイカートに搭乗中ログアウトするとディスプレイカートまで削除されてしまうため、
        //ログアウト前に降ろしておく。何故カートが削除されてしまうのかは原因不明
        if (player.getVehicle() != null) {
            if (RaceManager.isSpecificKartType(player.getVehicle(), KartType.DisplayKart)) {
                player.leaveVehicle();
            }
        }

        //ログアウト中にレースが終了してしまった場合、レース前の情報が全て消えてしまうため、
        //レース中ログアウトした場合、現在のプレイヤー情報を保存し、体力等をレース前の状態に戻す
        //再度レース中にログインした場合は、DataListener.onJoin()で、ログアウト時に保存したプレイヤーデータを復元しレースに復帰させる
        if (RaceManager.isStandBy(player.getUniqueId())) {
            Scoreboards.hideBoard(player.getUniqueId());

            r.savePlayerDataOnQuit();
            r.saveInventoryOnQuit();

            RaceManager.leaveRacingKart(player);
            r.recoveryInventory();
            r.recoveryPhysical();
            player.teleport(r.getGoalPosition());

            //ゴール直後にログアウトした場合、r.setGoalでスケジュールされたテレポートタスクが不発するため対策
            if (r.getGoal()) {
                player.teleport(r.getGoalPosition());
                r.init();
            }
        } else if (RaceManager.isEntry(player.getUniqueId())
                && !RaceManager.isStandBy(player.getUniqueId())) {
            RaceManager.clearEntryRaceData(player.getUniqueId());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!YPLKart.isPluginEnabled(e.getPlayer().getWorld())) {
            return;
        }
        if (!RaceManager.isStandBy(e.getPlayer().getUniqueId())) {
            return;
        }

        final Player p = e.getPlayer();
        final Racer r = RaceManager.getRacer(p);

        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
            public void run() {
                p.setSprinting(true);
                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 0.5F);
                p.setWalkSpeed(r.getCharacter().getPenaltyWalkSpeed());
                p.setNoDamageTicks(r.getCharacter().getPenaltyAntiReskillSecond() * 20);
                r.recoveryKart();
                r.setDeathPenaltyTitleSendTask(
                        new SendBlinkingTitleTask(p, r.getCharacter().getPenaltySecond(),
                                MessageEnum.titleDeathPanalty.getMessage()).runTaskTimer(YPLKart.getInstance(), 0, 1)
                        );
            }
        });

        r.setDeathPenaltyTask(
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        p.setWalkSpeed(r.getCharacter().getWalkSpeed());
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                        p.setSprinting(true);
                        r.setDeathPenaltyTask(null);
                    }
                }, r.getCharacter().getPenaltySecond() * 20 + 3)
                );

        if (r.getLastPassedCheckPoint() != null) {
            Location respawn = r.getLastPassedCheckPoint().getLocation()
                    .add(0, -RaceManager.checkPointHeight, 0);
            e.setRespawnLocation(
                    new Location(respawn.getWorld()
                            , respawn.getX(), respawn.getY(), respawn.getZ(), r.getLastYaw(), 0));
        }
    }

    //キラー使用中の窒素ダメージを無効
    //カート搭乗中の落下ダメージを無効
    //スタンバイ状態～レース開始までのダメージを無効
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!YPLKart.isPluginEnabled(event.getEntity().getWorld())) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() == DamageCause.VOID) {
            return;
        }
        Player p = (Player) event.getEntity();

        if (RaceManager.isRacing(p.getUniqueId())) {
            if (RaceManager.getRacer(p).getUsingKiller() != null) {
                if (event.getCause() == DamageCause.SUFFOCATION) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (event.getCause() == DamageCause.FALL) {
                if (p.getVehicle() != null) {
                    if (RaceManager.isSpecificKartType(p.getVehicle(), KartType.RacingKart)) {
                        event.setCancelled(true);
                    }
                }
            }
        } else if (RaceManager.isStandBy(p.getUniqueId())
                && !RaceManager.isRacing(p.getUniqueId())) {
            if (event.getCause() != DamageCause.VOID)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!YPLKart.isPluginEnabled(e.getEntity().getWorld())) {
            return;
        }
        final Player p = (Player) e.getEntity();

        if (!RaceManager.isStandBy(p.getUniqueId())) {
            return;
        }
        Racer r = RaceManager.getRacer(p);

        RaceManager.leaveRacingKart(p);
        r.setLastYaw(p.getLocation().getYaw());

        if (p.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("true")) {
            return;
        }
        e.getDrops().clear();
        //r.saveInventory();
        e.setKeepInventory(true);

        Bukkit.getScheduler().scheduleSyncDelayedTask(YPLKart.getInstance(), new Runnable() {
            public void run() {
                if (p.isDead())
                    PacketUtil.skipRespawnScreen(p);
            }
        });
    }

    //エントリー中の場合、キャラクター・カートが未選択の場合はメニューを閉じさせません
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!YPLKart.isPluginEnabled(e.getPlayer().getWorld())) {
            return;
        }
        if (!RaceManager.isStandBy(((Player) e.getPlayer()).getUniqueId())) {
            return;
        }

        final Player p = (Player) e.getPlayer();
        Racer r = RaceManager.getRacer(p);
        if (e.getInventory().getName().equalsIgnoreCase("Character Select Menu")) {
            if (r.getCharacter() == null) {
                MessageEnum.raceMustSelectCharacter.sendConvertedMessage(
                        p, RaceManager.getCircuit(r.getEntry()));
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(p, true);
                    }
                });
                return;
            }
            if (r.getKart() == null && Permission.hasPermission(p, Permission.KART_RIDE, true)) {
                MessageEnum.raceMustSelectKart.sendConvertedMessage(
                        p, RaceManager.getCircuit(r.getEntry()));
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(p, false);
                    }
                });
                return;
            }
        } else if (e.getInventory().getName().equalsIgnoreCase("Kart Select Menu")) {
            if (r.getKart() == null && Permission.hasPermission(p, Permission.KART_RIDE, true)) {
                MessageEnum.raceMustSelectKart.sendConvertedMessage(
                        p, RaceManager.getCircuit(r.getEntry()));
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(p, false);
                    }
                });
                return;
            }
            if (r.getCharacter() == null) {
                MessageEnum.raceMustSelectCharacter.sendConvertedMessage(
                        p, RaceManager.getCircuit(r.getEntry()));
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(p, true);
                    }
                });
                return;
            }
        }
    }

    //エントリー中：cancel
    //インベントリネーム一致：cancel
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!YPLKart.isPluginEnabled(e.getWhoClicked().getWorld())) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        //召集後はインベントリの操作をさせない
        if (RaceManager.isStandBy(((Player) e.getWhoClicked()).getUniqueId())) {
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        }

        Player p = (Player) e.getWhoClicked();
        UUID id = p.getUniqueId();
        Racer r = RaceManager.getRacer(p);
        if (e.getInventory().getName().equalsIgnoreCase("Character Select Menu")) {
            e.setCancelled(true);
            p.updateInventory();

            ItemStack item = e.getCurrentItem();
            if (item == null) {
                return;
            }

            String clickedItemName = item.hasItemMeta()
                    ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : null;
            if (clickedItemName == null) {
                return;
            }

            //キャンセルボタン
            if (EnumSelectMenu.CHARACTER_CANCEL.equalsIgnoreCase(clickedItemName)) {
                p.closeInventory();
                //ランダムボタン
            } else if (EnumSelectMenu.CHARACTER_RANDOM.equalsIgnoreCase(clickedItemName)) {
                RaceManager.setCharacterRaceData(id, CharacterConfig.getRandomCharacter());
                //ネクストプレビューボタン
            } else if (EnumSelectMenu.CHARACTER_NEXT.equalsIgnoreCase(clickedItemName)
                    || EnumSelectMenu.CHARACTER_PREVIOUS.equalsIgnoreCase(clickedItemName)) {
                if (RaceManager.isStandBy(id)) {
                    if (r.getCharacter() == null) {
                        MessageEnum.raceMustSelectCharacter.sendConvertedMessage(
                                p, RaceManager.getCircuit(r.getEntry()));
                    } else {
                        p.closeInventory();

                        //kart == nullの場合はonInventoryCloseで強制的にメニューが表示される
                        if (r.getKart() != null)
                            RaceManager.showSelectMenu(p, false);
                    }
                } else {
                    p.closeInventory();
                    RaceManager.showSelectMenu(p, false);
                }
                //キャラクター選択
            } else if (CharacterConfig.getCharacter(clickedItemName) != null) {
                RaceManager.setCharacterRaceData(id, CharacterConfig.getCharacter(clickedItemName));
            }
            p.playSound(p.getLocation(), Sound.CLICK, 0.5F, 1.0F);
        } else if (e.getInventory().getName().equalsIgnoreCase("Kart Select Menu")) {
            e.setCancelled(true);
            p.updateInventory();

            ItemStack item = e.getCurrentItem();
            if (item == null) {
                return;
            }

            String clicked = item.hasItemMeta() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : null;
            if (clicked == null) {
                return;
            }

            if (EnumSelectMenu.KART_CANCEL.equalsIgnoreCase(clicked)) {
                //キャンセルボタン
                p.closeInventory();
            } else if (EnumSelectMenu.KART_RANDOM.equalsIgnoreCase(clicked)) {
                //ランダムボタン
                RaceManager.setKartRaceData(id, KartConfig.getRandomKart());
            } else if (EnumSelectMenu.KART_NEXT.equalsIgnoreCase(clicked)
                    || EnumSelectMenu.KART_PREVIOUS.equalsIgnoreCase(clicked)) {
                //ネクストプレビューボタン
                if (RaceManager.isStandBy(id)) {
                    if (r.getKart() == null) {
                        MessageEnum.raceMustSelectKart.sendConvertedMessage(
                                p, RaceManager.getCircuit(r.getEntry()));
                    } else {
                        p.closeInventory();

                        //character == nullの場合はonInventoryCloseで強制的にメニューが表示される
                        if (r.getCharacter() != null) {
                            RaceManager.showSelectMenu(p, true);
                        }
                    }
                } else {
                    p.closeInventory();
                    RaceManager.showSelectMenu(p, true);
                }
            } else if (KartConfig.getKart(clicked) != null) {
                //カート選択
                RaceManager.setKartRaceData(id, KartConfig.getKart(clicked));
            }
            p.playSound(p.getLocation(), Sound.CLICK, 0.5F, 1.0F);
        }
    }
}
