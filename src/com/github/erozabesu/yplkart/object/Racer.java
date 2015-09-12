package com.github.erozabesu.yplkart.object;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.enumdata.KartType;
import com.github.erozabesu.yplkart.enumdata.RaceType;
import com.github.erozabesu.yplkart.override.CustomArmorStandDelegator;
import com.github.erozabesu.yplkart.reflection.Methods;
import com.github.erozabesu.yplkart.task.SendBlinkingTitleTask;
import com.github.erozabesu.yplkart.task.SendExpandedTitleTask;
import com.github.erozabesu.yplkart.utils.CheckPointUtil;
import com.github.erozabesu.yplkart.utils.KartUtil;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class Racer extends PlayerObject{

    /** レース中ログアウトしたプレイヤーのプレイヤー情報 */
    private PlayerObject racingPlayerObject;

    /** エントリーしているサーキット */
    private Circuit circuit;

    /** 選択キャラクター */
    private Character character;

    /** 姿を偽装するために仮想スポーンさせたNmsEntity */
    private Object disguisedNmsEntity;

    /** 選択カート */
    private Kart kart;

    /** レース開始時にテレポートする座標 */
    private Location raceStartLocation;

    /**
     * 搭乗しているカートエンティティの座標
     * カートエンティティを再生成した際のYawを固定するため、
     * レースがスタンバイ状態に移行し、レース開始地点にテレポートする際の座標、<br>
     * もしくは、ログアウト時のカートエンティティの座標を格納する
     */
    private Location kartEntityLocation;

    /** アイテムボックスに接触して間もない状態かどうか */
    private boolean isItemBoxCooling;

    /**
     * issue #112<br>
     * アイテムを使用して間もない状態かどうか<br>
     * アイテムを右クリック時PlayerInteractEventが2回フックされ実行してしまう場合があるため、<br>
     * 1度のクリックで連続してアイテムを消耗してしまわないようにするため、このフラグを用いる
     */
    private boolean isItemUseCooling;

    /** マッチングに同意しているかどうか */
    private boolean isMatchingAccepted;

    /** マッチングが終了し、レース開始地点にテレポートされた状態かどうか */
    private boolean isStandby;

    /** レースをゴールしているかどうか */
    private boolean isGoal;

    /** レースをスタートしているかどうか */
    private boolean isStart;

    /** レースの周回数 */
    private int currentLaps;

    /**
     * 通過済みのチェックポイントリスト<br>
     * this.currentLaps + チェックポイントエンティティのUUID がStringとして格納される
     */
    private ArrayList<String> passedCheckPointList;

    /** 最後に通過したチェックポイントエンティティ */
    private Entity lastPassedCheckPointEntity;

    /** アイテムボックスに接触して間もない状態かどうかをセットするタスク */
    private BukkitTask itemBoxCoolingTask;

    /**
     * issue #112<br>
     * アイテムを使用して間もない状態かどうかをセットするタスク
     */
    private BukkitTask itemUseCoolingTask;

    /** デスペナルティタスク */
    private BukkitTask deathPenaltyTask;

    /** デスペナルティ用点滅タイトル表示タスク */
    private BukkitTask deathPenaltyTitleSendTask;

    /** 移動速度上昇タスク */
    private BukkitTask itemPositiveSpeedTask;

    /** 移動速度低下タスク */
    private BukkitTask itemNegativeSpeedTask;

    /** キラーを使用した際の、周囲にある最寄の未通過のチェックポイントを格納する */
    private Entity killerFirstPassedCheckPointEntity;

    /** ダッシュボードを踏んで加速している状態かどうか */
    private boolean isStepDashBoard;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * @param uuid プレイヤーUUID
     */
    public Racer(UUID uuid) {
        super(uuid);
        this.initializeRacer();
    }

    /**
     * メンバ変数を初期化する。<br>
     * Racerオブジェクトはサーバーがリロードされるまで同一のプレイヤーに対し使い回されるため、<br>
     * メンバ変数を初期化するメソッドが別途用意されている。
     */
    public void initializeRacer() {
        this.setRacingPlayerObject(null);

        this.setCircuit(null);

        this.setKartEntityLocation(null);

        this.setMatchingAccepted(false);
        this.setItemBoxCooling(false);
        this.setItemUseCooling(false);
        this.setStandby(false);
        this.setStart(false);
        this.setGoal(false);

        this.setCurrentLaps(0);

        this.setPassedCheckPointList(new ArrayList<String>());
        this.setLastPassedCheckPointEntity(null);

        this.setItemBoxCoolingTask(null);
        this.setItemUseCoolingTask(null);
        this.setDeathPenaltyTask(null);
        this.setDeathPenaltyTitleSendTask(null);
        this.setItemPositiveSpeedTask(null);
        this.setItemNegativeSpeedTask(null);

        this.setKillerFirstPassedCheckPointEntity(null);

        this.setStepDashBoard(false);
    }

    //〓 Race Edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** レースの終了処理を行う */
    public void endRace() {
        this.setGoal(true);

        //演出パーティクル
        Util.createSignalFireworks(getPlayer().getLocation());
        Util.createFlowerShower(getPlayer(), 20);

        //偽装するために仮想スポーンさせているNmsEntityをデスポーンさせる
        if (this.getDisguisedNmsEntity() != null) {
            Entity disguisedEntity =
                    (Entity) ReflectionUtil.invoke(Methods.nmsEntity_getBukkitEntity, this.getDisguisedNmsEntity());
            PacketUtil.sendEntityDestroyPacket(null, disguisedEntity);
        }

        //リザルトメッセージの送信
        this.sendResult();

        //リザルトをローカルファイルへ保存
        this.saveResult();

        //初期化 順序に注意
        this.setStart(false);
        RaceManager.racerSetter_DeselectKart(this.getUUID());
        RaceManager.leaveRacingKart(getPlayer());
        this.recoveryAll();
        RaceManager.racerSetter_DeselectCharacter(this.getUUID());
    }

    /**
     * レースのリザルトをゴールしたプレイヤーに送信する。<br>
     * また、全体通知機能がtrueであればサーバーの全プレイヤーに、<br>
     * falseであればレースの参加者のみに併せて送信する。
     */
    private void sendResult() {
        Circuit circuit = this.getCircuit();
        if (circuit == null) {
            return;
        }

        String circuitName = circuit.getCircuitName();
        double lapTime = circuit.getLapMilliSecond() / 1000.0D;
        int currentRank = circuit.getGoalRacerList().size();

        MessageParts playerParts = MessageParts.getMessageParts(this.getPlayer());
        MessageParts circuitParts = MessageParts.getMessageParts(circuit);
        MessageParts numberParts = MessageParts.getMessageParts(currentRank, lapTime);

        //〓ゴールしたプレイヤー向けメッセージ送信

        //演出のGOALタイトルを送信
        new SendExpandedTitleTask(getPlayer(), 5, "GOAL!!!" + ChatColor.GOLD, "O", 1, false).runTaskTimer(
                YPLKart.getInstance(), 0, 1);

        //リザルトのサブタイトルを送信
        String messageResultTitle = MessageEnum.titleGoalRank.getConvertedMessage(numberParts);
        PacketUtil.sendTitle(getPlayer(), messageResultTitle, 10, 100, 10, true);

        //〓全体メッセージ送信

        //全体通知機能がtrueであれば、ゴールしたプレイヤーのリザルトをサーバーの全プレイヤーに送信する
        if (circuit.getBroadcastGoalMessage()) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                MessageEnum.raceGoal.sendConvertedMessage(other, playerParts, circuitParts, numberParts);
            }

        //全体通知機能がfalseであれば、ゴールしたプレイヤーのリザルトをレースの参加者のみに送信する
        } else {
            circuit.sendMessageEntryPlayer(MessageEnum.raceGoal, playerParts, circuitParts, numberParts);
        }
    }

    /** レースのラップタイムをローカルファイルへ保存する */
    public void saveResult() {
        Circuit circuit = this.getCircuit();
        if (circuit == null) {
            return;
        }

        String circuitName = circuit.getCircuitName();
        double lapTime = circuit.getLapMilliSecond() / 1000.0D;

        //ローカルファイルへリザルトの保存
        if (this.getKart() == null) {
            CircuitConfig.addRaceLapTime(getPlayer(), circuitName, lapTime, false);
        } else {
            CircuitConfig.addRaceLapTime(getPlayer(), circuitName, lapTime, true);
        }
    }

    /**
     * 通過済みのチェックポイントリストに値を追加し、スコアボードを更新する。<br>
     * 追加する値は、this.currentLaps + チェックポイントエンティティのUUID<br>
     * のフォーマットのStringを与えること。
     * @param value 追加するチェックポイント
     */
    public void addPassedCheckPoint(String value) {
        if (this.getPassedCheckPointList().contains(value)) {
            return;
        }
        this.getPassedCheckPointList().add(value);
        Scoreboards.setPoint(getUUID());
    }

    /** レース中ログアウトしたプレイヤーの情報を格納する */
    public void savePlayerDataOnQuit() {
        if (this.getPlayer() != null) {
            this.setRacingPlayerObject(new PlayerObject(getUUID()));
        }
    }


    //〓 Task 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * キラー使用者のカートエンティティをキラー用に初期化し、<br>
     * キラーの効果時間が切れた際に元の状態に初期化するタスクを起動する
     * @param life キラーの効果時間
     * @param nearestUnpassedCheckPoint 最寄の未通過のチェックポイントエンティティ
     */
    public void runKillerInitializeTask(int life, Entity nearestUnpassedCheckPoint) {
        final Player player = getPlayer();

        this.setKillerFirstPassedCheckPointEntity(nearestUnpassedCheckPoint);

        //ランニングレース中にキラーを使用した場合、新規にキラー用カートエンティティを生成し搭乗する
        if (this.getCircuit().getRaceType().equals(RaceType.RUNNING)) {
            Entity kartEntity = KartUtil.createRacingKart(player.getLocation(), KartConfig.getKillerKart());
            kartEntity.setPassenger(player);
        }

        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
            public void run() {
                setKillerFirstPassedCheckPointEntity(null);

                //ランニングレース中にキラーを使用した場合、登場中のキラー用カートエンティティを降りる
                if (getCircuit().getRaceType().equals(RaceType.RUNNING)) {
                    RaceManager.leaveRacingKart(player);
                }
            }
        }, life * 20);
    }

    /**
     * ダッシュボードを踏んだ際のポーションエフェクトを付与する。<br>
     * また、ダッシュボードを連続して踏めないよう、効果時間中はフラグをtrueにし、<br>
     * 効果時間が切れた場合はフラグをfalseに戻すタスクを起動する。
     */
    public void runStepDashBoardTask() {
        if (this.isStepDashBoard()) {
            return;
        }

        this.setStepDashBoard(true);
        int effectSecond = ((Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_SECOND.getValue()
                + this.getCharacter().getAdjustPositiveEffectSecond()) * 20;

        this.getPlayer().playSound(this.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5F, 1.0F);
        this.runPositiveItemSpeedTask(
                (Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_SECOND.getValue()
                , (Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_LEVEL.getValue()
                , Sound.EXPLODE);

        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(),new Runnable() {
            public void run() {
                setStepDashBoard(false);
            }
        }, effectSecond);
    }

    public void runNegativeItemSpeedTask(int effectSecond, int effectLevel, Sound sound) {
        final Player player = this.getPlayer();
        Character character = this.getCharacter();
        effectSecond = (effectSecond + character.getAdjustNegativeEffectSecond()) * 20;

        player.playSound(player.getLocation(), sound, 0.5F, -1.0F);
        Util.setPotionEffect(player, PotionEffectType.SLOW, effectSecond, effectLevel + character.getAdjustNegativeEffectLevel());

        this.setItemNegativeSpeedTask(
            Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                public void run() {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                    player.removePotionEffect(PotionEffectType.SLOW);
                    setItemNegativeSpeedTask(null);
                }
            }, effectSecond)
        );
    }

    public void runPositiveItemSpeedTask(int effectSecond, int level, Sound sound) {
        final Player player = this.getPlayer();
        Character character = this.getCharacter();
        effectSecond = (effectSecond + character.getAdjustPositiveEffectSecond()) * 20;

        player.playSound(player.getLocation(), sound, 0.5F, -1.0F);
        Util.setPotionEffect(player, PotionEffectType.SPEED, effectSecond, level + character.getAdjustPositiveEffectLevel());

        if (this.getDeathPenaltyTask() != null) {
            this.removeDeathPenalty();
        }

        this.setItemPositiveSpeedTask(
            Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                public void run() {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                    player.removePotionEffect(PotionEffectType.SPEED);
                    setItemPositiveSpeedTask(null);
                }
            }, effectSecond)
        );
    }
    /**
     * プレイヤーにデスペナルティのフィジカルパラメータを適用し、諸々の演出を再生する。<br>
     * 同時に一定時間後にフィジカルを本来の数値に戻すタスクを起動する。
     */
    public void applyDeathPenalty() {
        // キラー使用中は除外
        if (this.getUsingKiller() != null) {
            return;
        }

        // 無敵時間中は除外
        if (0 < this.getPlayer().getNoDamageTicks()) {
            return;
        }

        final Player player = this.getPlayer();
        final Character character = this.getCharacter();

        //プレイヤーのフィジカルにデスペナルティを適用
        player.setWalkSpeed(character.getPenaltyWalkSpeed());
        player.setNoDamageTicks(character.getPenaltyAntiReskillSecond() * 20);

        //演出
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 0.5F);

        //FOVの初期化用
        player.setSprinting(true);

        //デスペナルティの効果を初期化するタスクを起動
        this.setDeathPenaltyTask(
                Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        removeDeathPenalty();
                    }
                }, character.getPenaltySecond() * 20)
                );

        //デスペナルティ用タイトルメッセージを点滅表示
        this.setDeathPenaltyTitleSendTask(
                new SendBlinkingTitleTask(player, character.getPenaltySecond(),
                        MessageEnum.titleDeathPanalty.getMessage()).runTaskTimer(YPLKart.getInstance(), 0, 1)
                );
    }

    /**
     * 最後に通過したチェックポイントに強制的にテレポートし、デスペナルティを付与する。<br>
     * 最後に通過したチェックポイントが格納されていない場合は除外される。
     */
    public void applyCourseOut() {
        Entity lastPassedCheckPoint = this.getLastPassedCheckPointEntity();
        if (lastPassedCheckPoint != null) {
            final Location teleportLocation = lastPassedCheckPoint.getLocation().add(0, -CheckPointUtil.checkPointHeight, 0);
            Entity kartEntity = this.getPlayer().getVehicle();
            if (kartEntity != null) {
                this.getPlayer().leaveVehicle();
                kartEntity.remove();
            }
            this.getPlayer().teleport(teleportLocation);

            //遅延させないとカートが生成されない
            Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                public void run() {
                    setKartEntityLocation(teleportLocation);
                    recoveryKart();
                }
            }, 2);

            this.applyDeathPenalty();
        }
    }

    /** アイテムを使用して間もない状態かどうかをセットするタスクを起動する */
    public void runItemUseCoolingTask() {
        this.setItemUseCooling(true);
        BukkitTask itemUseCoolingTask = Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
            public void run() {
                setItemUseCooling(false);
            }
        }, 1L);

        this.setItemUseCoolingTask(itemUseCoolingTask);
    }

    /** アイテムボックスに接触して間もない状態かどうかをセットするタスクを起動する */
    public void runItemBoxCoolingTask() {
        this.setItemBoxCooling(true);
        this.setItemBoxCoolingTask(Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable(){
            public void run() {
                setItemBoxCooling(false);
            }
        }, 30L));
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * レースにエントリーしているかどうかを返す。
     * @return レースにエントリーしているかどうか
     */
    public boolean isEntry() {
        return this.getCircuit() != null;
    }

    /**
     * レースが最少プレイ人数を満たすまで待機している状態かどうかを返す。
     * @return ウェイティングフェーズかどうか
     */
    public boolean isWaitingRacerPhase() {
        return this.getCircuit() != null && this.getCircuit().isWaitingRacerPhase();
    }

    /**
     * レースが最小プレイ人数を満たし、参加者のレース参加同意を待機している状態かどうかを返す。
     * @return マッチングフェーズかどうか
     */
    public boolean isMatchingPhase() {
        return this.getCircuit() != null && this.getCircuit().isMatchingPhase();
    }

    /**
     * 参加者をレース開始地点にテレポート後、キャラクター・カートを選択させている状態かどうかを返す。
     * @return スタンバイフェーズかどうか
     */
    public boolean isStandbyPhase() {
        return this.getCircuit() != null && this.getCircuit().isStandbyPhase();
    }

    /**
     * レースが出走した状態かどうかを返す。
     * @return レーシングフェーズかどうか
     */
    public boolean isRacingPhase() {
        return this.getCircuit() != null && this.getCircuit().isRacingPhase();
    }

    /**
     * レースの状態がマッチングフェーズ、スタンバイフェーズ、レーシングフェーズのいずれかの状態かどうかを返す。
     * @return マッチングフェーズ以降のフェーズかどうか
     */
    public boolean isAfterMatchingPhase() {
        return this.isMatchingPhase() || this.isStandbyPhase() || this.isRacingPhase();
    }

    /**
     * レースの状態がスタンバイフェーズ、レーシングフェーズのいずれかの状態かどうかを返す。
     * @return スタンバイフェーズ以降のフェーズかどうか
     */
    public boolean isAfterStandbyPhase() {
        return this.isStandbyPhase() || this.isRacingPhase();
    }

    /**
     * 参加中のレースがスタンバイフェーズ以降であり、かつ、まだゴールしていないかどうかを返す。
     * @return プレイヤーがまだレース中かどうか
     */
    public boolean isStillInRace() {
        return this.isAfterStandbyPhase() && !this.isGoal;
    }

    /**
     * 参加中のレースがレーシングフェーズであり、かつ、まだゴールしていないかどうかを返す。
     * @return プレイヤーがまだ走行中かどうか
     */
    public boolean isStillRacing() {
        return this.isRacingPhase() && !this.isGoal();
    }

    public void removeDeathPenalty() {
        Player player = this.getPlayer();

        this.setDeathPenaltyTask(null);
        this.setDeathPenaltyTitleSendTask(null);

        //フィジカルを本来の数値に戻す
        player.setWalkSpeed(character.getWalkSpeed());

        //演出
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);

        //FOVの初期化用
        player.setSprinting(true);
    }

    /** 搭乗しているカートエンティティの座標を格納する */
    public void saveKartEntityLocation() {
        if (this.getPlayer() == null) {
            return;
        }
        if (this.getKart() == null) {
            return;
        }

        Entity vehicle = this.getPlayer().getVehicle();
        if (vehicle == null) {
            return;
        }
        if (!KartUtil.isSpecificKartType(vehicle, KartType.RacingKart)) {
            return;
        }

        this.setKartEntityLocation(vehicle.getLocation());
    }

    /**
     * カートエンティティをRacerオブジェクトに格納されている選択カート情報を元にリスポーンさせ、<br>
     * プレイヤーを搭乗させる。
     */
    public void recoveryKart() {
        if (this.getPlayer() == null) {
            return;
        }
        if (this.getKart() == null) {
            return;
        }

        Player player = this.getPlayer();
        Entity vehicle = player.getVehicle();

        //既に何かのエンティティに搭乗している
        if (vehicle != null) {

            //カートエンティティに搭乗している場合パラメータの変更のみ行いreturn
            if (KartUtil.isKartEntity(vehicle)) {
                Object kartEntity = KartUtil.getCustomMinecartObjectByEntityMetaData(vehicle);
                CustomArmorStandDelegator.setParameter(kartEntity, this.getKart());
                return;

            //カートエンティティ以外に搭乗している場合は降ろす
            } else {
                player.leaveVehicle();
            }
        }

        //新規に生成したカートエンティティに搭乗させる
        Entity kartEntity = KartUtil.createRacingKart(this.getKartEntityLocation(), kart);
        kartEntity.setPassenger(player);

        //クライアントで搭乗が解除されている状態で描画されるのを回避するため
        //issue#109
        PacketUtil.sendOwnAttachEntityPacket(player);
    }

    public void recoveryCharacter() {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }

        player.setMaxHealth(this.getCharacter().getMaxHealth());
        player.setWalkSpeed(this.getCharacter().getWalkSpeed());
        player.setHealth(this.getCharacter().getMaxHealth());
        player.getInventory().setHelmet(this.getCharacter().getMenuItem());
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return レース中ログアウトしたプレイヤーのプレイヤー情報 */
    public PlayerObject getRacingPlayerObject() {
        return this.racingPlayerObject;
    }

    /** @return エントリーしているサーキット */
    public Circuit getCircuit() {
        return circuit;
    }

    /** @return 選択キャラクター */
    public Character getCharacter() {
        return this.character;
    }

    /** @return 姿を偽装するために仮想スポーンさせたNmsEntity */
    public Object getDisguisedNmsEntity() {
        return disguisedNmsEntity;
    }

    /** @return 選択カート */
    public Kart getKart() {
        return this.kart;
    }

    /** @return レース開始時にテレポートする座標 */
    public Location getRaceStartLocation() {
        return raceStartLocation;
    }

    /** @return 搭乗しているカートエンティティの座標 */
    public Location getKartEntityLocation() {
        return kartEntityLocation;
    }

    /** @return マッチングに同意しているかどうか */
    public boolean isMatchingAccepted() {
        return isMatchingAccepted;
    }

    /** @return アイテムボックスに接触して間もない状態かどうか */
    public boolean isItemBoxCooling() {
        return isItemBoxCooling;
    }

    /** @return アイテムを使用して間もない状態かどうか */
    public boolean isItemUseCooling() {
        return isItemUseCooling;
    }

    /** @return マッチングが終了し、レース開始地点にテレポートされた状態かどうか */
    public boolean isStandby() {
        return this.isStandby;
    }

    /** @return レースをゴールしているかどうか */
    public boolean isGoal() {
        return this.isGoal;
    }

    /** @return レースをスタートしているかどうか */
    public boolean isStart() {
        return this.isStart;
    }

    /** @return レースの周回数 */
    public int getCurrentLaps() {
        return this.currentLaps;
    }

    /** @return 通過済みのチェックポイントリスト */
    public ArrayList<String> getPassedCheckPointList() {
        return this.passedCheckPointList;
    }

    /** @return 最後に通過したチェックポイントエンティティ */
    public Entity getLastPassedCheckPointEntity() {
        return this.lastPassedCheckPointEntity;
    }

    /** @return アイテムボックスに接触して間もない状態かどうかをセットするタスク */
    public BukkitTask getItemBoxCoolingTask() {
        return itemBoxCoolingTask;
    }

    /** @return アイテムを使用して間もない状態かどうかをセットするタスク */
    public BukkitTask getItemUseCoolingTask() {
        return this.itemUseCoolingTask;
    }

    /** @return デスペナルティタスク */
    public BukkitTask getDeathPenaltyTask() {
        return this.deathPenaltyTask;
    }

    /** @return デスペナルティ用点滅タイトル表示タスク */
    public BukkitTask getDeathPenaltySendTitleTask() {
        return this.deathPenaltyTitleSendTask;
    }

    /** @return 移動速度上昇タスク */
    public BukkitTask getItemPositiveSpeed() {
        return this.itemPositiveSpeedTask;
    }

    /** @return 移動速度低下タスク */
    public BukkitTask getItemNegativeSpeed() {
        return this.itemNegativeSpeedTask;
    }

    /** @return キラーを使用した際に、周囲にある最寄の未通過のチェックポイントを格納する */
    public Entity getUsingKiller() {
        return this.killerFirstPassedCheckPointEntity;
    }

    /** @return ダッシュボードを踏んで加速している状態かどうか */
    public boolean isStepDashBoard() {
        return this.isStepDashBoard;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param racingPlayerObject レース中ログアウトしたプレイヤーのプレイヤー情報 */
    public void setRacingPlayerObject(PlayerObject racingPlayerObject) {
        this.racingPlayerObject = racingPlayerObject;
    }

    /** @param circuit エントリーしているサーキット */
    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }

    /** @param character 選択キャラクター */
    public void setCharacter(Character character) {
        this.character = character;
    }

    /** @param disguisedNmsEntity 姿を偽装するために仮想スポーンさせたNmsEntity */
    public void setDisguisedNmsEntity(Object disguisedNmsEntity) {
        this.disguisedNmsEntity = disguisedNmsEntity;
    }

    /** @param kart 選択カート */
    public void setKart(Kart kart) {
        this.kart = kart;
    }

    /** @param raceStartLocation レース開始時にテレポートする座標 */
    public void setRaceStartLocation(Location raceStartLocation) {
        this.raceStartLocation = raceStartLocation;
    }

    /** @param kartEntityLocation 搭乗しているカートエンティティの座標 */
    public void setKartEntityLocation(Location kartEntityLocation) {
        this.kartEntityLocation = kartEntityLocation;
    }

    /** @param isMatchingAccepted マッチングに同意しているかどうか */
    public void setMatchingAccepted(boolean isMatchingAccepted) {
        this.isMatchingAccepted = isMatchingAccepted;
    }

    /** @param isItemBoxCooling アイテムボックスに接触して間もない状態かどうか */
    public void setItemBoxCooling(boolean isItemBoxCooling) {
        this.isItemBoxCooling = isItemBoxCooling;
    }

    /** @param isItemUseCooling アイテムを使用して間もない状態かどうか */
    public void setItemUseCooling(boolean isItemUseCooling) {
        this.isItemUseCooling = isItemUseCooling;
    }

    /** @param value マッチングが終了し、レース開始地点にテレポートされた状態かどうか */
    public void setStandby(boolean value) {
        this.isStandby = value;
    }

    /** @param value レースをゴールしているかどうか */
    public void setGoal(boolean value) {
        this.isGoal = value;
    }

    /** @param value レースをスタートしているかどうか */
    public void setStart(boolean value) {
        this.isStart = value;
    }

    /** @param value レースの周回数 */
    public void setCurrentLaps(int value) {
        this.currentLaps = value;
    }

    /** @param passedCheckPointList 通過済みのチェックポイントリスト */
    public void setPassedCheckPointList(ArrayList<String> passedCheckPointList) {
        this.passedCheckPointList = passedCheckPointList;
    }

    /** @param lastPassedCheckPointEntity 最後に通過したチェックポイントエンティティ */
    public void setLastPassedCheckPointEntity(Entity lastPassedCheckPointEntity) {
        this.lastPassedCheckPointEntity = lastPassedCheckPointEntity;
    }

    /** @param itemBoxCoolingTask アイテムボックスに接触して間もない状態かどうかをセットするタスク */
    public void setItemBoxCoolingTask(BukkitTask itemBoxCoolingTask) {
        if (this.itemBoxCoolingTask != null) {
            this.itemBoxCoolingTask.cancel();
        }
        this.itemBoxCoolingTask = itemBoxCoolingTask;
    }

    /** @param itemUseCoolingTask アイテムを使用して間もない状態かどうかをセットするタスク */
    public void setItemUseCoolingTask(BukkitTask itemUseCoolingTask) {
        if (this.itemUseCoolingTask != null) {
            this.itemUseCoolingTask.cancel();
        }
        this.itemUseCoolingTask = itemUseCoolingTask;
    }

    /** @param newtask デスペナルティタスク */
    public void setDeathPenaltyTask(BukkitTask newtask) {
        //重複しないよう既に起動中のタスクがあればキャンセルする
        if (this.deathPenaltyTask != null) {
            this.deathPenaltyTask.cancel();
        }

        this.deathPenaltyTask = newtask;
    }

    /** @param newtask デスペナルティ用点滅タイトル表示タスク */
    public void setDeathPenaltyTitleSendTask(BukkitTask newtask) {
        //重複しないよう既に起動中のタスクがあればキャンセルする
        if (this.deathPenaltyTitleSendTask != null) {
            this.deathPenaltyTitleSendTask.cancel();
        }

        this.deathPenaltyTitleSendTask = newtask;
    }

    /** @param newtask 移動速度上昇タスク */
    public void setItemPositiveSpeedTask(BukkitTask newtask) {
        //重複しないよう既に起動中のタスクがあればキャンセルする
        if (this.itemPositiveSpeedTask != null) {
            this.itemPositiveSpeedTask.cancel();
        }

        this.itemPositiveSpeedTask = newtask;
    }

    /** @param newtask 移動速度低下タスク */
    public void setItemNegativeSpeedTask(BukkitTask newtask) {
        //重複しないよう既に起動中のタスクがあればキャンセルする
        if (this.itemNegativeSpeedTask != null) {
            this.itemNegativeSpeedTask.cancel();
        }

        this.itemNegativeSpeedTask = newtask;
    }

    /** @param entity キラーを使用した際に、周囲にある最寄の未通過のチェックポイントを格納する */
    public void setKillerFirstPassedCheckPointEntity(Entity killerFirstPassedCheckPointEntity) {
        this.killerFirstPassedCheckPointEntity = killerFirstPassedCheckPointEntity;
    }

    /** @param isStepDashBoard ダッシュボードを踏んで加速している状態かどうか */
    public void setStepDashBoard(boolean isStepDashBoard) {
        this.isStepDashBoard = isStepDashBoard;
    }
}
