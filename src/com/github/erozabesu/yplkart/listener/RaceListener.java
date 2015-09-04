package com.github.erozabesu.yplkart.listener;

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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.EnumSelectMenu;
import com.github.erozabesu.yplkart.object.CircuitData;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.object.RaceType;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.CheckPointUtil;
import com.github.erozabesu.yplkart.utils.KartUtil;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class RaceListener implements Listener {

    public RaceListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, YPLKart.getInstance());
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Racer racer = RaceManager.getRacer(player);
        if (RaceManager.isStandby(player.getUniqueId()) && !racer.isGoal()) {
            event.setCancelled(true);
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

        if (RaceManager.isStandby(uuid) && !RaceManager.isStarted(uuid)) {
            if (!event.getFrom().equals(event.getTo())) {
                Location from = event.getFrom();
                Location to = event.getTo();

                if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
                    return;
                }
                player.teleport(Util.adjustLocationToBlockCenter(from).add(0, 1, 0));
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
        if (!RaceManager.isStandby(player.getUniqueId())) {
            return;
        }

        //ゴールしているプレイヤーは除外
        Racer racer = RaceManager.getRacer(player);
        if (racer.isGoal()) {
            return;
        }

        String fromBlockId = Util.getGroundBlockID(event.getFrom(), 10);
        String toBlockId = Util.getGroundBlockID(event.getTo(), 10);

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

        final Racer racer = RaceManager.getRacer(player);
        if (racer.getCurrentLaps() < 1) {
            return;
        }

        String circuitName = racer.getCircuitName();
        Location location = player.getLocation();

        // 周囲のチェックポイントのうち、視認が可能な未通過のチェックポイントを取得
        Entity nearestCheckPoint = CheckPointUtil.getInSightAndVisibleNearestUnpassedCheckpoint(racer, location, 270.0F);

        // 前回通過したチェックポイント
        Entity lastPassedCheckPoint = racer.getLastPassedCheckPointEntity();

        // 初回通過の場合は取得したチェックポイントを格納してreturn
        if (lastPassedCheckPoint == null) {
            if (nearestCheckPoint != null) {
                racer.setLastPassedCheckPointEntity(nearestCheckPoint);
                racer.addPassedCheckPoint(racer.getCurrentLaps() + nearestCheckPoint.getUniqueId().toString());
                return;
            }
        }

        // 前回通過したチェックポイントの検出範囲
        Integer lastPassedCheckPointDetectRadius = CheckPointUtil.getDetectCheckPointRadiusByCheckPointEntity(lastPassedCheckPoint);

        // 新しい未通過かつ視認可能なチェックポイントが取得できず、
        // かつ前回通過したチェックポイントとの距離が検出範囲を超えている場合コースアウトと判定する
        if (nearestCheckPoint == null) {
            if (lastPassedCheckPoint != null && lastPassedCheckPointDetectRadius != null) {
                if (lastPassedCheckPointDetectRadius < location.distance(lastPassedCheckPoint.getLocation())) {
                    racer.applyCourseOut();
                }
            }
        } else {
            Location checkPointLocation = nearestCheckPoint.getLocation();

            // まだ前回通過したチェックポイントの検出範囲内に居る場合、
            // かつ最寄の視認可能な未通過のチェックポイントとの距離が前回通過したチェックポイントとの距離よりも遠い場合return
            if (lastPassedCheckPoint != null && lastPassedCheckPointDetectRadius != null) {
                double lastCheckPointDistance = location.distance(lastPassedCheckPoint.getLocation());
                double nearestCheckPointDistance = location.distance(checkPointLocation);
                if (lastCheckPointDistance < lastPassedCheckPointDetectRadius) {
                    if (lastCheckPointDistance < nearestCheckPointDistance) {
                        return;
                    }
                }
            }

            racer.setLastPassedCheckPointEntity(nearestCheckPoint);
            racer.addPassedCheckPoint(racer.getCurrentLaps() + nearestCheckPoint.getUniqueId().toString());
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
        final Racer racer = RaceManager.getRacer(p);
        final CircuitData circuitData = CircuitConfig.getCircuitData(racer.getCircuitName());
        if (circuitData == null) {
            return;
        }

        if (RaceManager.isStandby(p.getUniqueId()) && !racer.isGoal()) {

            // issue #197
            Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                public void run() {
                    //レース中のパラメータを復元する
                    racer.getRacingPlayerObject().recoveryAll();

                    //カートエンティティを再生成し搭乗する
                    racer.recoveryKart();

                    //カート、もしくはキャラクターが未選択の場合強制的にランダム選択する : issue #121
                    if (racer.getCharacter() == null) {
                        RaceManager.racerSetter_Character(p.getUniqueId(), CharacterConfig.getRandomCharacter());
                        ItemEnum.removeAllKeyItems(p);
                    }

                    if (circuitData.getRaceType().equals(RaceType.KART)) {
                        RaceManager.racerSetter_Kart(p.getUniqueId(), KartConfig.getRandomKart());
                        ItemEnum.removeAllKeyItems(p);
                    }

                    Scoreboards.showBoard(p.getUniqueId());
                }
            }, 10);
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
            if (KartUtil.isSpecificKartType(player.getVehicle(), KartType.DisplayKart)) {
                racer.leaveVehicle();
            }
        }

        //ログアウト中にレースが終了してしまった場合、レース前の情報が全て消えてしまうため、
        //レース中ログアウトした場合、現在のプレイヤー情報を保存し、体力等をレース前の状態に戻す
        //再度レース中にログインした場合は、DataListener.onJoin()で、ログアウト時に保存したプレイヤーデータを復元しレースに復帰させる
        if (RaceManager.isStandby(player.getUniqueId())) {
            Scoreboards.hideBoard(player.getUniqueId());

            racer.savePlayerDataOnQuit();
            racer.saveKartEntityLocation();
            RaceManager.leaveRacingKart(player);

            //レース前のパラメータを復元する
            racer.recoveryAll();
        } else if (RaceManager.isEntry(player.getUniqueId())
                && !RaceManager.isStandby(player.getUniqueId())) {
            RaceManager.racerSetter_UnEntry(player.getUniqueId());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!YPLKart.isPluginEnabled(event.getPlayer().getWorld())) {
            return;
        }
        if (!RaceManager.isStandby(event.getPlayer().getUniqueId())) {
            return;
        }

        final Player player = event.getPlayer();
        final Racer racer = RaceManager.getRacer(player);

        //リスポーン直後はプレイヤーに関する操作は通らないため利用可能になってから実行する
        Bukkit.getScheduler().scheduleSyncDelayedTask(YPLKart.getInstance(), new Runnable() {
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
                    .add(0, -CheckPointUtil.checkPointHeight, 0);
            event.setRespawnLocation(
                    new Location(respawn.getWorld()
                            , respawn.getX(), respawn.getY(), respawn.getZ(), racer.getLastYaw(), 0));
        //チェックポイントを通過していない場合はレース開始地点にリスポーンする
        } else {
            Location respawn = racer.getRaceStartLocation();
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
                    if (KartUtil.isSpecificKartType(player.getVehicle(), KartType.RacingKart)) {
                        event.setCancelled(true);
                    }
                }
            }

        //スタンバイ状態～レース開始までのダメージを無効
        } else if (RaceManager.isStandby(uuid) && !RaceManager.isStarted(uuid)) {
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

        if (!RaceManager.isStandby(p.getUniqueId())) {
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
         * カートが再生成されるため、予め最後に通過したチェックポイントの座標を格納しておく
         * 通過済みのチェックポイントがない場合はレース開始地点の座標を格納する
         */
        if (r.getLastPassedCheckPointEntity() == null) {
            r.setKartEntityLocation(r.getRaceStartLocation());
        } else {
            r.setKartEntityLocation(
                    r.getLastPassedCheckPointEntity().getLocation().add(0.0D, -CheckPointUtil.checkPointHeight, 0.0D));
        }

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
        if (!RaceManager.isStandby(((Player) e.getPlayer()).getUniqueId())) {
            return;
        }

        final Player p = (Player) e.getPlayer();
        Racer r = RaceManager.getRacer(p);
        if (e.getInventory().getName().equalsIgnoreCase("Character Select Menu")) {

            //まだキャラクターを選択していない場合は別の画面に遷移させない
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

            /*
             * まだカートを選択しておらず、かつ参加しているサーキットのレースタイプがKARTの場合、
             * 強制的にカート選択画面に遷移する
             */
            CircuitData circuitData = CircuitConfig.getCircuitData(r.getCircuitName());
            if (circuitData != null) {
                if (r.getKart() == null && circuitData.getRaceType().equals(RaceType.KART)) {
                    MessageEnum.raceMustSelectKart.sendConvertedMessage(
                            p, RaceManager.getCircuit(r.getCircuitName()));
                    Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                        public void run() {
                            RaceManager.showSelectMenu(p, false);
                        }
                    });
                    return;
                }
            }
        } else if (e.getInventory().getName().equalsIgnoreCase("Kart Select Menu")) {

            /*
             * まだカートを選択しておらず、かつ参加しているサーキットのレースタイプがKARTの場合、
             * 別の画面に遷移させない
             */
            CircuitData circuitData = CircuitConfig.getCircuitData(r.getCircuitName());
            if (circuitData != null) {
                if (r.getKart() == null && circuitData.getRaceType().equals(RaceType.KART)) {
                    MessageEnum.raceMustSelectKart.sendConvertedMessage(
                            p, RaceManager.getCircuit(r.getCircuitName()));
                    Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                        public void run() {
                            RaceManager.showSelectMenu(p, false);
                        }
                    });
                    return;
                }
            }

            //まだキャラクターを選択していない場合、強制的にキャラクター選択画面に遷移する
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
        if (RaceManager.isStandby(player.getUniqueId())) {
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
                RaceManager.racerSetter_Character(uuid, CharacterConfig.getRandomCharacter());
                //ネクストプレビューボタン
            } else if (EnumSelectMenu.CHARACTER_NEXT.equalsIgnoreCase(clickedItemName)
                    || EnumSelectMenu.CHARACTER_PREVIOUS.equalsIgnoreCase(clickedItemName)) {
                if (RaceManager.isStandby(uuid)) {
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
                RaceManager.racerSetter_Character(uuid, CharacterConfig.getCharacter(clickedItemName));
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
                RaceManager.racerSetter_Kart(uuid, KartConfig.getRandomKart());
            } else if (EnumSelectMenu.KART_NEXT.equalsIgnoreCase(clicked)
                    || EnumSelectMenu.KART_PREVIOUS.equalsIgnoreCase(clicked)) {
                //ネクストプレビューボタン
                if (RaceManager.isStandby(uuid)) {
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
                RaceManager.racerSetter_Kart(uuid, KartConfig.getKart(clicked));
            }
            player.playSound(player.getLocation(), Sound.CLICK, 0.5F, 1.0F);
        }
    }
}
