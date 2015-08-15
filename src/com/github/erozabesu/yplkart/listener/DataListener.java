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

    /**
     * スタンバイ状態～レースが開始されるまでの間、水平方向への移動を禁止する
     * @param event
     */
    @EventHandler
    public void cancelMove(PlayerMoveEvent event) {
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld())) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (RaceManager.isStandBy(uuid) && !RaceManager.isStarted(uuid)) {
            if (!event.getFrom().equals(event.getTo())) {
                Location from = event.getFrom();
                Location to = event.getTo();

                if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
                    return;
                }
                player.teleport(Util.adjustBlockLocation(from).add(0, 1, 0));
                event.setCancelled(true);
            }
        }
    }

    /**
     * スタートブロック・ゴールブロックを跨いだ際に、プレイヤーの周回数を加算、もしくは減算する
     * @param event
     */
    @EventHandler
    public void saveLapcount(PlayerMoveEvent event) {
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld()))
            return;

        //マッチングが終了しているプレイヤー以外は除外
        Player player = event.getPlayer();
        if (!RaceManager.isStandBy(player.getUniqueId())) {
            return;
        }

        //ゴールしているプレイヤーは除外
        Racer racer = RaceManager.getRacer(player);
        if (racer.isGoal()) {
            return;
        }

        String fromBlockId = Util.getGroundBlockID(event.getFrom());
        String toBlockId = Util.getGroundBlockID(event.getTo());

        //現在の週回数
        int currentLaps = racer.getCurrentLaps();

        //正常に進行している場合
        if (fromBlockId.equalsIgnoreCase((String) ConfigEnum.START_BLOCK_ID.getValue())) {
            if (toBlockId.equalsIgnoreCase((String) ConfigEnum.GOAL_BLOCK_ID.getValue())) {

                //サーキットの周回数を達成した場合ゴールする
                if (currentLaps == CircuitConfig.getCircuitData(racer.getCircuitName()).getNumberOfLaps()) {
                    racer.endRace();
                } else {

                    //周回数が0週の場合スタートフラグをtrueにする
                    if (currentLaps == 0) {
                        racer.setStart(true);

                        //レース前から所持していたレース用アイテムを削除
                        ItemEnum.removeAllKeyItems(player);

                    //2周目以降。周回数が変動したことをプレイヤーに通知する
                    } else {
                        MessageEnum.raceUpdateLap.sendConvertedMessage(player,
                                new Object[] { (currentLaps + 1), RaceManager.getCircuit(racer.getCircuitName()) });
                    }

                    //周回数を加算
                    racer.setCurrentLaps(currentLaps + 1);
                }
            }

        //逆走している場合
        } else if (fromBlockId.equalsIgnoreCase((String) ConfigEnum.GOAL_BLOCK_ID.getValue())) {
            if (toBlockId.equalsIgnoreCase((String) ConfigEnum.START_BLOCK_ID.getValue())) {

                //逆走していることをプレイヤーに通知
                MessageEnum.raceReverseRun.sendConvertedMessage(player, RaceManager.getCircuit(racer.getCircuitName()));

                //周回数を減算
                if (currentLaps <= 0) {
                    racer.setCurrentLaps(0);
                } else {
                    racer.setCurrentLaps(currentLaps - 1);
                }
            }
        }
    }

    /**
     * 周囲のチェックポイントを取得し格納する
     * @param event
     */
    @EventHandler
    public void RunningRank(PlayerMoveEvent event) {//順位
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld())) {
            return;
        }
        Player player = event.getPlayer();
        if (!RaceManager.isStillRacing(player.getUniqueId())) {
            return;
        }

        Racer racer = RaceManager.getRacer(player);
        if (racer.getCurrentLaps() < 1) {
            return;
        }

        ArrayList<Entity> checkPointEntityList = RaceManager.getNearbyCheckpoint(
                event.getPlayer().getLocation(), RaceManager.checkPointDetectRadius, racer.getCircuitName());
        if (checkPointEntityList == null) {
            return;
        }

        Iterator<Entity> i = checkPointEntityList.iterator();
        Entity checkPointEntity;
        String currentLaps = racer.getCurrentLaps() <= 0 ? "" : String.valueOf(racer.getCurrentLaps());
        while (i.hasNext()) {
            checkPointEntity = i.next();
            racer.addPassedCheckPoint(currentLaps + checkPointEntity.getUniqueId().toString());
            racer.setLastPassedCheckPointEntity(checkPointEntity);
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
        if (!RaceManager.isStillRacing(((Player) e.getEntity()).getUniqueId())) {
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
            Racer racer = RaceManager.getRacer(p);

            //レース中のパラメータを復元する
            racer.getRacingPlayerObject().recoveryAll();

            //カートエンティティを再生成し搭乗する
            racer.recoveryKart();

            //カート、もしくはキャラクターが未選択の場合強制的にランダム選択する : issue #121
            if (racer.getCharacter() == null) {
                RaceManager.setCharacterRaceData(p.getUniqueId(), CharacterConfig.getRandomCharacter());
                ItemEnum.removeAllKeyItems(p);
            }
            //TODO: issue #79
            if (racer.getKart() == null) {
                RaceManager.setKartRaceData(p.getUniqueId(), KartConfig.getRandomKart());
                ItemEnum.removeAllKeyItems(p);
            }

            Scoreboards.showBoard(p.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!YPLKart.isPluginEnabled(event.getPlayer().getWorld())) {
            return;
        }

        Player player = event.getPlayer();
        Racer racer = RaceManager.getRacer(player);

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

            racer.savePlayerDataOnQuit();
            racer.saveKartEntityLocation();
            RaceManager.leaveRacingKart(player);

            //レース前のパラメータを復元する
            racer.recoveryAll();

            //ゴール直後にログアウトした場合、r.setGoalでスケジュールされたテレポートタスクが不発するため対策
            if (racer.isGoal()) {
                racer.recoveryLocation();
                racer.initializeRacer();
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

                //プレイヤーにデスペナルティを適用
                racer.applyDeathPenalty();

                //生前カートに搭乗していた場合はカートエンティティを再生成し搭乗する
                racer.recoveryKart();
            }
        });

        //最後に通過したチェックポイントの座標にリスポーンする
        if (racer.getLastPassedCheckPointEntity() != null) {
            Location respawn = racer.getLastPassedCheckPointEntity().getLocation()
                    .add(0, -RaceManager.checkPointHeight, 0);
            event.setRespawnLocation(
                    new Location(respawn.getWorld()
                            , respawn.getX(), respawn.getY(), respawn.getZ(), racer.getLastYaw(), 0));
        }
    }

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

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (RaceManager.isStillRacing(uuid)) {

            //キラー使用中の窒素ダメージを無効
            if (RaceManager.getRacer(player).getUsingKiller() != null) {
                if (event.getCause() == DamageCause.SUFFOCATION) {
                    event.setCancelled(true);
                    return;
                }
            }

            //カート搭乗中の落下ダメージを無効
            if (event.getCause() == DamageCause.FALL) {
                if (player.getVehicle() != null) {
                    if (RaceManager.isSpecificKartType(player.getVehicle(), KartType.RacingKart)) {
                        event.setCancelled(true);
                    }
                }
            }

        //スタンバイ状態～レース開始までのダメージを無効
        } else if (RaceManager.isStandBy(uuid) && !RaceManager.isStarted(uuid)) {
            if (event.getCause() != DamageCause.VOID) {
                event.setCancelled(true);
            }
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

        /*
         * リスポーン時に再生成される際に実行されるRacer.recoveryKart()ではRacer.kartEntityLocationの座標に
         * カートが再生成されるため、予め座標を格納しておく
         */
        r.setKartEntityLocation(
                r.getLastPassedCheckPointEntity().getLocation().add(0.0D, -RaceManager.checkPointHeight, 0.0D));

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
                        p, RaceManager.getCircuit(r.getCircuitName()));
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(p, true);
                    }
                });
                return;
            }
            if (r.getKart() == null && Permission.hasPermission(p, Permission.KART_RIDE, true)) {
                MessageEnum.raceMustSelectKart.sendConvertedMessage(
                        p, RaceManager.getCircuit(r.getCircuitName()));
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
                        p, RaceManager.getCircuit(r.getCircuitName()));
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(p, false);
                    }
                });
                return;
            }
            if (r.getCharacter() == null) {
                MessageEnum.raceMustSelectCharacter.sendConvertedMessage(
                        p, RaceManager.getCircuit(r.getCircuitName()));
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

        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        //クライアントを×ボタン等で強制終了した場合、プレイヤーはオフラインになるためreturn
        if (!player.isOnline()) {
            return;
        }

        //既にゴールしている場合はreturn
        if (RaceManager.getRace(uuid).isGoal()) {
            return;
        }

        //スタンバイ状態以降はインベントリの操作をさせない
        if (RaceManager.isStandBy(player.getUniqueId())) {
            e.setCancelled(true);
            player.updateInventory();
        }

        Racer racer = RaceManager.getRacer(player);
        if (e.getInventory().getName().equalsIgnoreCase("Character Select Menu")) {
            e.setCancelled(true);
            player.updateInventory();

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
                player.closeInventory();
                //ランダムボタン
            } else if (EnumSelectMenu.CHARACTER_RANDOM.equalsIgnoreCase(clickedItemName)) {
                RaceManager.setCharacterRaceData(uuid, CharacterConfig.getRandomCharacter());
                //ネクストプレビューボタン
            } else if (EnumSelectMenu.CHARACTER_NEXT.equalsIgnoreCase(clickedItemName)
                    || EnumSelectMenu.CHARACTER_PREVIOUS.equalsIgnoreCase(clickedItemName)) {
                if (RaceManager.isStandBy(uuid)) {
                    if (racer.getCharacter() == null) {
                        MessageEnum.raceMustSelectCharacter.sendConvertedMessage(
                                player, RaceManager.getCircuit(racer.getCircuitName()));
                    } else {
                        player.closeInventory();

                        //kart == nullの場合はonInventoryCloseで強制的にメニューが表示される
                        if (racer.getKart() != null)
                            RaceManager.showSelectMenu(player, false);
                    }
                } else {
                    player.closeInventory();
                    RaceManager.showSelectMenu(player, false);
                }
                //キャラクター選択
            } else if (CharacterConfig.getCharacter(clickedItemName) != null) {
                RaceManager.setCharacterRaceData(uuid, CharacterConfig.getCharacter(clickedItemName));
            }
            player.playSound(player.getLocation(), Sound.CLICK, 0.5F, 1.0F);
        } else if (e.getInventory().getName().equalsIgnoreCase("Kart Select Menu")) {
            e.setCancelled(true);
            player.updateInventory();

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
                player.closeInventory();
            } else if (EnumSelectMenu.KART_RANDOM.equalsIgnoreCase(clicked)) {
                //ランダムボタン
                RaceManager.setKartRaceData(uuid, KartConfig.getRandomKart());
            } else if (EnumSelectMenu.KART_NEXT.equalsIgnoreCase(clicked)
                    || EnumSelectMenu.KART_PREVIOUS.equalsIgnoreCase(clicked)) {
                //ネクストプレビューボタン
                if (RaceManager.isStandBy(uuid)) {
                    if (racer.getKart() == null) {
                        MessageEnum.raceMustSelectKart.sendConvertedMessage(
                                player, RaceManager.getCircuit(racer.getCircuitName()));
                    } else {
                        player.closeInventory();

                        //character == nullの場合はonInventoryCloseで強制的にメニューが表示される
                        if (racer.getCharacter() != null) {
                            RaceManager.showSelectMenu(player, true);
                        }
                    }
                } else {
                    player.closeInventory();
                    RaceManager.showSelectMenu(player, true);
                }
            } else if (KartConfig.getKart(clicked) != null) {
                //カート選択
                RaceManager.setKartRaceData(uuid, KartConfig.getKart(clicked));
            }
            player.playSound(player.getLocation(), Sound.CLICK, 0.5F, 1.0F);
        }
    }
}
