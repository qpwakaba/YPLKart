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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
    public void onRespawn(PlayerRespawnEvent event) {
        if (!YPLKart.isPluginEnabled(event.getPlayer().getWorld())) {
            return;
        }
        if (!RaceManager.isStandBy(event.getPlayer().getUniqueId())) {
            return;
        }

        final Player player = event.getPlayer();
        final Racer racer = RaceManager.getRacer(player);

        //リスポーン直後はプレイヤーに関する操作は通らないため利用可能になってから実行する
        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
            public void run() {

                //FOVの初期化用
                player.setSprinting(true);

                //演出
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 0.5F);

                //プレイヤーのフィジカルにデスペナルティを適用
                player.setWalkSpeed(racer.getCharacter().getPenaltyWalkSpeed());
                player.setNoDamageTicks(racer.getCharacter().getPenaltyAntiReskillSecond() * 20);

                //生前カートに搭乗していた場合はカートエンティティを再生成し搭乗する
                racer.recoveryKart();

                //デスペナルティ用タイトルメッセージを点滅表示
                racer.setDeathPenaltyTitleSendTask(
                        new SendBlinkingTitleTask(player, racer.getCharacter().getPenaltySecond(),
                                MessageEnum.titleDeathPanalty.getMessage()).runTaskTimer(YPLKart.getInstance(), 0, 1)
                        );
            }
        });

        //デスペナルティの効果時間が終了した場合、プレイヤーのフィジカルを本来の数値に戻す
        racer.setDeathPenaltyTask(
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        //フィジカルを本来の数値に戻す
                        player.setWalkSpeed(racer.getCharacter().getWalkSpeed());

                        //演出
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);

                        //FOVの初期化用
                        player.setSprinting(true);

                        //変数の初期化
                        racer.setDeathPenaltyTask(null);
                    }
                }, racer.getCharacter().getPenaltySecond() * 20 + 3)
                );

        //最後に通過したチェックポイントの座標にリスポーンする
        if (racer.getLastPassedCheckPoint() != null) {
            Location respawn = racer.getLastPassedCheckPoint().getLocation()
                    .add(0, -RaceManager.checkPointHeight, 0);
            event.setRespawnLocation(
                    new Location(respawn.getWorld()
                            , respawn.getX(), respawn.getY(), respawn.getZ(), racer.getLastYaw(), 0));
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
