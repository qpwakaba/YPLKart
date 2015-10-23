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
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.KartType;
import com.github.erozabesu.yplkart.enumdata.RaceType;
import com.github.erozabesu.yplkart.enumdata.SelectMenu;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.MessageParts;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.CheckPointUtil;
import com.github.erozabesu.yplkart.utils.KartUtil;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;
import com.github.erozabesu.yplutillibrary.util.PacketUtil;

public class RaceListener implements Listener {

    public RaceListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, YPLKart.getInstance());
    }

    /**
     * スタンバイフェーズ以降のレース中のエンティティへの接触を禁止する。
     * @param event PlayerInteractEntityEvent
     */
    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Racer racer = RaceManager.getRacer(player);

        if (racer.isStillInRace()) {
            event.setCancelled(true);
        }
    }

    /**
     * スタンバイフェーズ～レーシングフェーズの間の水平方向への移動を禁止する
     * @param event PlayerMoveEvent
     */
    @EventHandler
    public void cancelMove(PlayerMoveEvent event) {
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld())) {
            return;
        }

        Player player = event.getPlayer();
        Racer racer = RaceManager.getRacer(player);
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        if (circuit.isStandbyPhase()) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (!from.equals(to)) {
                if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
                    return;
                }
                player.teleport(CommonUtil.adjustLocationToBlockCenter(from).add(0, 1, 0));
                event.setCancelled(true);
            }
        }
    }

    /**
     * スタートブロック・ゴールブロックを跨いだ際に、プレイヤーの周回数を加算、もしくは減算する。
     * @param event PlayerMoveEvent
     */
    @EventHandler
    public void saveLapcount(PlayerMoveEvent event) {
        if (!YPLKart.isPluginEnabled(event.getFrom().getWorld()))
            return;

        Player player = event.getPlayer();
        Racer racer = RaceManager.getRacer(player);

        if (!racer.isStillInRace()) {
            return;
        }

        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        String fromBlockId = CommonUtil.getGroundBlockID(event.getFrom(), 10);
        String toBlockId = CommonUtil.getGroundBlockID(event.getTo(), 10);

        //現在の週回数
        int currentLaps = racer.getCurrentLaps();

        //正常に進行している場合
        if (fromBlockId.equalsIgnoreCase(ConfigEnum.settings$start_block_id)) {
            if (toBlockId.equalsIgnoreCase(ConfigEnum.settings$goal_block_id)) {

                //サーキットの周回数を達成した場合ゴールする
                if (currentLaps == circuit.getNumberOfLaps()) {
                    racer.endRace();
                } else {

                    //周回数が0週の場合スタートフラグをtrueにする
                    if (currentLaps == 0) {
                        racer.setStart(true);

                        //レース前から所持していたレース用アイテムを削除
                        ItemEnum.removeAllKeyItems(player);

                    //2周目以降。周回数が変動したことをプレイヤーに通知する
                    } else {
                        MessageEnum.raceUpdateLap.sendConvertedMessage(player, MessageParts.getMessageParts(currentLaps + 1), circuitParts);
                    }

                    //周回数を加算
                    racer.setCurrentLaps(currentLaps + 1);
                }
            }

        //逆走している場合
        } else if (fromBlockId.equalsIgnoreCase(ConfigEnum.settings$goal_block_id)) {
            if (toBlockId.equalsIgnoreCase(ConfigEnum.settings$start_block_id)) {

                //逆走していることをプレイヤーに通知
                MessageEnum.raceReverseRun.sendConvertedMessage(player, circuitParts);

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
        Racer racer = RaceManager.getRacer(player);
        if (!racer.isStillRacing()) {
            return;
        }

        if (racer.getCurrentLaps() < 1) {
            return;
        }

        Location location = player.getLocation();

        // 周囲のチェックポイントのうち、視認が可能な未通過のチェックポイントを取得
        Entity nearestCheckPoint = CheckPointUtil.getInSightAndDetectableNearestUnpassedCheckpoint(racer, location, 270.0F);

        // 前回通過したチェックポイント
        Entity lastPassedCheckPoint = racer.getLastPassedCheckPointEntity();

        // 初回通過の場合は取得したチェックポイントを格納してreturn
        if (lastPassedCheckPoint == null) {
            if (nearestCheckPoint != null) {
                racer.setLastPassedCheckPointEntity(nearestCheckPoint);
                racer.addPassedCheckPoint(racer.getCurrentLaps() + nearestCheckPoint.getUniqueId().toString());
                return;

            // チェックポイントが検出できていない場合は何もせずreturn
            } else {
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
    public void onRegainHealth(EntityRegainHealthEvent event) {
        if (!YPLKart.isPluginEnabled(event.getEntity().getWorld())) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Racer racer = RaceManager.getRacer((Player) event.getEntity());
        if (!racer.isStillRacing()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!YPLKart.isPluginEnabled(event.getPlayer().getWorld())) {
            return;
        }

        final Player player = event.getPlayer();
        final Racer racer = RaceManager.getRacer(player);
        final Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        if (racer.isStillInRace()) {

            // issue #197
            Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                public void run() {
                    //レース中のパラメータを復元する
                    racer.getRacingPlayerObject().recoveryAll();

                    //キャラクターが未選択の場合強制的にランダム選択する : issue #121
                    if (racer.getCharacter() == null) {
                        RaceManager.racerSetter_Character(player.getUniqueId(), CharacterConfig.getRandomCharacter());
                        ItemEnum.removeAllKeyItems(player);
                    }

                    // レースタイプがカートレースの場合、かつ、カートが未選択の場合強制的にランダム選択する : issue #121
                    if (circuit.getRaceType().equals(RaceType.KART)) {
                        if (racer.getKart() == null) {
                            RaceManager.racerSetter_Kart(player.getUniqueId(), KartConfig.getRandomKart());
                            ItemEnum.removeAllKeyItems(player);
                        }
                    }

                    //カートエンティティを再生成し搭乗する
                    racer.recoveryKart();

                    Scoreboards.showBoard(player.getUniqueId());
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

        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        //ログアウト中にレースが終了してしまった場合、レース前の情報が全て消えてしまうため、
        //レース中ログアウトした場合、現在のプレイヤー情報を保存し、体力等をレース前の状態に戻す
        //再度レース中にログインした場合は、DataListener.onJoin()で、ログアウト時に保存したプレイヤーデータを復元しレースに復帰させる

        // レースがスタンバイフェーズ以降
        if (circuit.isAfterStandbyPhase()) {
            Scoreboards.hideBoard(player.getUniqueId());

            racer.savePlayerDataOnQuit();
            racer.saveKartEntityLocation();
            RaceManager.leaveRacingKart(player);

            //レース前のパラメータを復元する
            racer.recoveryAll();

        // スタンバイフェーズ以降前
        } else {
            RaceManager.racerSetter_UnEntry(racer);
        }
    }

    /**
     * スタンバイフェーズ以降にリスポーンしたプレイヤーのリスポーン座標の変更、デスペナルティの適用、及びカートエンティティの再生成を行う。
     * @param event PlayerRespawnEvent
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!YPLKart.isPluginEnabled(event.getPlayer().getWorld())) {
            return;
        }

        final Player player = event.getPlayer();
        final Racer racer = RaceManager.getRacer(player);
        if (!racer.isStillInRace()) {
            return;
        }

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
            Location respawnLocation = racer.getLastPassedCheckPointEntity().getLocation().clone().add(0, -CheckPointUtil.checkPointHeight, 0);
            respawnLocation.setPitch(0.0F);
            event.setRespawnLocation(respawnLocation);

        //チェックポイントを通過していない場合はレース開始地点にリスポーンする
        } else {
            Location respawnLocation = racer.getRaceStartLocation().clone();
            respawnLocation.setPitch(0.0F);
            event.setRespawnLocation(respawnLocation);
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
        Racer racer = RaceManager.getRacer(player);

        if (racer.isStillRacing()) {

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

        //スタンバイフェーズのダメージを無効
        } else if (racer.isStandbyPhase()) {
            if (event.getCause() != DamageCause.VOID) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!YPLKart.isPluginEnabled(event.getEntity().getWorld())) {
            return;
        }

        final Player player = (Player) event.getEntity();
        Racer racer = RaceManager.getRacer(player);
        if (!racer.isAfterStandbyPhase()) {
            return;
        }

        RaceManager.leaveRacingKart(player);
        racer.setLastYaw(player.getLocation().getYaw());

        if (player.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("true")) {
            return;
        }
        event.getDrops().clear();
        event.setKeepInventory(true);

        /*
         * リスポーン時に再生成される際に実行されるRacer.recoveryKart()ではRacer.kartEntityLocationの座標に
         * カートが再生成されるため、予め最後に通過したチェックポイントの座標を格納しておく
         * 通過済みのチェックポイントがない場合はレース開始地点の座標を格納する
         */
        if (racer.getLastPassedCheckPointEntity() == null) {
            racer.setKartEntityLocation(racer.getRaceStartLocation());
        } else {
            racer.setKartEntityLocation(
                    racer.getLastPassedCheckPointEntity().getLocation().add(0.0D, -CheckPointUtil.checkPointHeight, 0.0D));
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(YPLKart.getInstance(), new Runnable() {
            public void run() {
                if (player.isDead())
                    PacketUtil.skipRespawnScreen(player);
            }
        });
    }

    /**
     * レースがスタンバイフェーズ以降の場合は、キャラクター・カートが未選択の状態ではメニューを閉じさせない。
     * @param event InventoryCloseEvent
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!YPLKart.isPluginEnabled(event.getPlayer().getWorld())) {
            return;
        }

        final Player player = (Player) event.getPlayer();
        Racer racer = RaceManager.getRacer(player);
        if (!racer.isAfterStandbyPhase()) {
            return;
        }

        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        if (event.getInventory().getName().equalsIgnoreCase("Character Select Menu")) {

            //まだキャラクターを選択していない場合は別の画面に遷移させない
            if (racer.getCharacter() == null) {
                MessageEnum.raceMustSelectCharacter.sendConvertedMessage(player, circuitParts);
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(player, true);
                    }
                });
                return;
            }

            /*
             * まだカートを選択しておらず、かつ参加しているサーキットのレースタイプがKARTの場合、
             * 強制的にカート選択画面に遷移する
             */
            if (racer.getKart() == null && circuit.getRaceType().equals(RaceType.KART)) {
                MessageEnum.raceMustSelectKart.sendConvertedMessage(player, circuitParts);
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(player, false);
                    }
                });
                return;
            }
        } else if (event.getInventory().getName().equalsIgnoreCase("Kart Select Menu")) {

            /*
             * まだカートを選択しておらず、かつ参加しているサーキットのレースタイプがKARTの場合、
             * 別の画面に遷移させない
             */
            if (racer.getKart() == null && circuit.getRaceType().equals(RaceType.KART)) {
                MessageEnum.raceMustSelectKart.sendConvertedMessage(player, circuitParts);
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(player, false);
                    }
                });
                return;
            }

            //まだキャラクターを選択していない場合、強制的にキャラクター選択画面に遷移する
            if (racer.getCharacter() == null) {
                MessageEnum.raceMustSelectCharacter.sendConvertedMessage(player, circuitParts);
                Bukkit.getScheduler().runTaskAsynchronously(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        RaceManager.showSelectMenu(player, true);
                    }
                });
                return;
            }
        }
    }

    /**
     * レース中のインベントリのクリックをキャンセルする。
     * @param event InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClickInRace(InventoryClickEvent event) {
        if (!YPLKart.isPluginEnabled(event.getWhoClicked().getWorld())) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Racer racer = RaceManager.getRacer(player);
        UUID uuid = player.getUniqueId();

        //クライアントを×ボタン等で強制終了した場合、プレイヤーはオフラインになるためreturn
        if (!player.isOnline()) {
            return;
        }

        //既にゴールしている場合はreturn
        if (racer.isGoal()) {
            return;
        }

        //スタンバイ状態以降はインベントリの操作をさせない
        if (racer.isAfterStandbyPhase()) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }


    /**
     * キャラクター、カートセレクトメニューをクリックした際の処理を行う。
     * @param event InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!YPLKart.isPluginEnabled(event.getWhoClicked().getWorld())) {
            return;
        }

        String inventoryName = event.getInventory().getName();
        if (!inventoryName.equalsIgnoreCase("Character Select Menu") && !inventoryName.equalsIgnoreCase("Kart Select Menu")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        player.updateInventory();

        Racer racer = RaceManager.getRacer(player);
        UUID uuid = player.getUniqueId();

        //クライアントを×ボタン等で強制終了した場合、プレイヤーはオフラインになるためreturn
        if (!player.isOnline()) {
            return;
        }

        //既にゴールしている場合はreturn
        if (racer.isGoal()) {
            return;
        }

        //スタンバイフェーズ以降でない場合はreturn
        if (!racer.isAfterStandbyPhase()) {
            return;
        }

        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return;
        }

        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        if (inventoryName.equalsIgnoreCase("Character Select Menu")) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) {
                return;
            }

            String clickedItemName = itemStack.hasItemMeta()
                    ? ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()) : null;
            if (clickedItemName == null) {
                return;
            }

            //キャンセルボタン
            if (SelectMenu.CHARACTER_CANCEL.equalsIgnoreCase(clickedItemName)) {
                player.closeInventory();

            //ランダムボタン
            } else if (SelectMenu.CHARACTER_RANDOM.equalsIgnoreCase(clickedItemName)) {
                RaceManager.racerSetter_Character(uuid, CharacterConfig.getRandomCharacter());

            //ネクストプレビューボタン
            } else if (SelectMenu.CHARACTER_NEXT.equalsIgnoreCase(clickedItemName) || SelectMenu.CHARACTER_PREVIOUS.equalsIgnoreCase(clickedItemName)) {
                if (racer.isAfterStandbyPhase()) {
                    if (racer.getCharacter() == null) {
                        MessageEnum.raceMustSelectCharacter.sendConvertedMessage(player, circuitParts);
                    } else {
                        player.closeInventory();

                        //kart == nullの場合はonInventoryCloseで強制的にメニューが表示される
                        if (racer.getKart() != null) {
                            RaceManager.showSelectMenu(player, false);
                        }
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
        } else if (inventoryName.equalsIgnoreCase("Kart Select Menu")) {
            ItemStack item = event.getCurrentItem();
            if (item == null) {
                return;
            }

            String clicked = item.hasItemMeta() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : null;
            if (clicked == null) {
                return;
            }

            //キャンセルボタン
            if (SelectMenu.KART_CANCEL.equalsIgnoreCase(clicked)) {
                player.closeInventory();

            //ランダムボタン
            } else if (SelectMenu.KART_RANDOM.equalsIgnoreCase(clicked)) {
                RaceManager.racerSetter_Kart(uuid, KartConfig.getRandomKart());

            //ネクストプレビューボタン
            } else if (SelectMenu.KART_NEXT.equalsIgnoreCase(clicked) || SelectMenu.KART_PREVIOUS.equalsIgnoreCase(clicked)) {
                if (racer.isAfterStandbyPhase()) {
                    if (racer.getKart() == null) {
                        MessageEnum.raceMustSelectKart.sendConvertedMessage(player, circuitParts);
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
