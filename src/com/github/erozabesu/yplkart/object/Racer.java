package com.github.erozabesu.yplkart.object;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.task.SendExpandedTitleTask;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class Racer extends PlayerObject{

    /** レース開始前のプレイヤー情報 */
    private PlayerObject beforePlayerObject;

    /** レース中ログアウトしたプレイヤーのプレイヤー情報 */
    private PlayerObject racingPlayerObject;

    /** エントリーしているサーキット名 */
    private String circuitName;

    /** 選択キャラクター */
    private Character character;

    /** 選択カート */
    private Kart kart;

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
        init();
    }

    /**
     * メンバ変数を初期化する。<br>
     * Racerオブジェクトはサーバーがリロードされるまで同一のプレイヤーに対し使い回されるため、<br>
     * メンバ変数を初期化するメソッドが別途用意されている。
     */
    public void init() {
        setBeforePlayerObject(null);
        setRacingPlayerObject(null);

        setCircuitName("");

        setStandby(false);
        setStart(false);
        setGoal(false);

        setCurrentLaps(0);

        setPassedCheckPointList(new ArrayList<String>());
        setLastPassedCheckPointEntity(null);

        setDeathPenaltyTask(null);
        setDeathPenaltyTitleSendTask(null);
        setItemPositiveSpeedTask(null);
        setItemNegativeSpeedTask(null);

        setKillerFirstPassedCheckPointEntity(null);

        setStepDashBoard(false);
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return レース開始前のプレイヤー情報 */
    public PlayerObject getBeforePlayerObject() {
        return beforePlayerObject;
    }

    /** @return レース中ログアウトしたプレイヤーのプレイヤー情報 */
    public PlayerObject getRacingPlayerObject() {
        return racingPlayerObject;
    }

    /** @return エントリーしているサーキット名 */
    public String getCircuitName() {
        return circuitName;
    }

    /** @return 選択キャラクター */
    public Character getCharacter() {
        return character;
    }

    /** @return 選択カート */
    public Kart getKart() {
        return kart;
    }

    /** @return マッチングが終了し、レース開始地点にテレポートされた状態かどうか */
    public boolean isStandby() {
        return isStandby;
    }

    /** @return レースをゴールしているかどうか */
    public boolean isGoal() {
        return isGoal;
    }

    /** @return レースをスタートしているかどうか */
    public boolean isStart() {
        return isStart;
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

    /** @return デスペナルティタスク */
    public BukkitTask getDeathPenaltyTask() {
        return deathPenaltyTask;
    }

    /** @return デスペナルティ用点滅タイトル表示タスク */
    public BukkitTask getDeathPenaltySendTitleTask() {
        return deathPenaltyTitleSendTask;
    }

    /** @return 移動速度上昇タスク */
    public BukkitTask getItemPositiveSpeed() {
        return itemPositiveSpeedTask;
    }

    /** @return 移動速度低下タスク */
    public BukkitTask getItemNegativeSpeed() {
        return itemNegativeSpeedTask;
    }

    /** @return キラーを使用した際に、周囲にある最寄の未通過のチェックポイントを格納する */
    public Entity getUsingKiller() {
        return killerFirstPassedCheckPointEntity;
    }

    /** @return ダッシュボードを踏んで加速している状態かどうか */
    public boolean isStepDashBoard() {
        return isStepDashBoard;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param beforePlayerObject レース開始前のプレイヤー情報 */
    public void setBeforePlayerObject(PlayerObject beforePlayerObject) {
        this.beforePlayerObject = beforePlayerObject;
    }

    /** @param racingPlayerObject レース中ログアウトしたプレイヤーのプレイヤー情報 */
    public void setRacingPlayerObject(PlayerObject racingPlayerObject) {
        this.racingPlayerObject = racingPlayerObject;
    }

    /** @param circuitName エントリーしているサーキット名 */
    public void setCircuitName(String circuitName) {
        this.circuitName = circuitName;
    }

    /** @param character 選択キャラクター */
    public void setCharacter(Character character) {
        this.character = character;
    }

    /** @param kart 選択カート */
    public void setKart(Kart kart) {
        this.kart = kart;
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

    //〓 Get/Set 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 通過済みのチェックポイントリストに値を追加し、スコアボードを更新する。<br>
     * 追加する値は、this.currentLaps + チェックポイントエンティティのUUID<br>
     * のフォーマットのStringを与えること。
     * @param value 追加するチェックポイント
     */
    public void addPassedCheckPoint(String value) {
        if (getPassedCheckPointList().contains(value)) {
            return;
        }
        getPassedCheckPointList().add(value);
        Scoreboards.setPoint(this.getUUID());
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** レースの終了処理を行う */
    public void runRaceEndProcess() {
        setGoal(true);

        final String entry = getCircuitName();

        Util.createSignalFireworks(getPlayer().getLocation());
        Util.createFlowerShower(getPlayer(), 20);

        double currentmillisecond = RaceManager.getCircuit(entry).getLapMilliSeconds();

        new SendExpandedTitleTask(getPlayer(), 5, "GOAL!!!" + ChatColor.GOLD, "O", 1, false).runTaskTimer(
                YPLKart.getInstance(), 0, 1);
        String message = MessageEnum.titleGoalRank.getConvertedMessage(new Object[] { new Number[] {
                RaceManager.getGoalPlayer(entry).size(), (double) (currentmillisecond / 1000) } });
        PacketUtil.sendTitle(getPlayer(), message, 10, 100, 10, true);

        Circuit circuit = RaceManager.getCircuit(entry);
        if (CircuitConfig.getCircuitData(entry).getBroadcastGoalMessage()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                MessageEnum.raceGoal.sendConvertedMessage(p,
                        new Object[] {
                                getPlayer(),
                                circuit,
                                new Number[] { RaceManager.getGoalPlayer(entry).size(),
                                        (double) (currentmillisecond / 1000) } });
            }
        } else {
            circuit.sendMessageEntryPlayer(MessageEnum.raceGoal, new Object[] { getPlayer(), circuit,
                    new Number[] { RaceManager.getGoalPlayer(entry).size(), (double) (currentmillisecond / 1000) } });
        }

        if (getKart() == null) {
            CircuitConfig.addRaceLapTime(getPlayer(), entry, currentmillisecond / 1000, false);
        } else {
            CircuitConfig.addRaceLapTime(getPlayer(), entry, currentmillisecond / 1000, true);
        }

        //終了処理 順序に注意
        setStart(false);
        RaceManager.clearCharacterRaceData(this.getUUID());
        RaceManager.clearKartRaceData(this.getUUID());
        RaceManager.leaveRacingKart(getPlayer());
        ItemEnum.removeAllKeyItems(getPlayer());
        //TODO
        getPlayer().getInventory().clear(-1, -1);
        getBeforePlayerObject().recoveryInventory();
        getBeforePlayerObject().recoveryExp();
        getBeforePlayerObject().recoveryLocation();
    }

    /**
     * キラー使用者のカートエンティティをキラー用に初期化し、<br>
     * キラーの効果時間が切れた際に元の状態に初期化するタスクを起動する
     * @param life キラーの効果時間
     * @param nearestUnpassedCheckPoint 最寄の未通過のチェックポイントエンティティ
     */
    public void runKillerInitializeTask(int life, Entity nearestUnpassedCheckPoint) {
        final Player player = getPlayer();

        setKillerFirstPassedCheckPointEntity(nearestUnpassedCheckPoint);

        //ランニングレース中にキラーを使用した場合、新規にキラー用カートエンティティを生成し搭乗する
        if (getKart() == null) {
            Entity kartEntity = RaceManager.createRacingKart(player.getLocation(), KartConfig.getKillerKart());
            kartEntity.setPassenger(player);
        }

        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
            public void run() {
                setKillerFirstPassedCheckPointEntity(null);

                //ランニングレース中にキラーを使用した場合、登場中のキラー用カートエンティティを降りる
                if (getKart() == null) {
                    RaceManager.leaveRacingKart(player);
                }
            }
        }, life * 20);
    }

    /**
     * ダッシュボードを連続して踏めないよう、効果時間中はフラグをtrueにし、<br>
     * 効果時間が切れた場合はフラグをfalseに戻すタスクを起動する。
     */
    public void runStepDashBoardInitializeTask() {
        int effectSecond = ((Integer) ConfigEnum.ITEM_DASH_BOARD_EFFECT_SECOND.getValue()
                + RaceManager.getRacer(getPlayer()).getCharacter().getAdjustPositiveEffectSecond()) * 20;

        setStepDashBoard(true);

        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(),new Runnable() {
            public void run() {
                setStepDashBoard(false);
            }
        }, effectSecond);
    }

    /** レース開始前のプレイヤーの情報を格納する */
    public void savePlayerData() {
        if (getPlayer() != null) {
            setBeforePlayerObject(new PlayerObject(getUUID()));
        }
    }

    /** レース中ログアウトしたプレイヤーの情報を格納する */
    public void savePlayerDataOnQuit() {
        if (getPlayer() != null) {
            setRacingPlayerObject(new PlayerObject(getUUID()));
        }
    }

    /**
     * カートエンティティをRacerオブジェクトに格納されている選択カート情報を元にリスポーンさせ、<br>
     * プレイヤーを搭乗させる。
     */
    public void recoveryKart() {
        if (getPlayer() == null) {
            return;
        }
        if (getKart() == null) {
            return;
        }

        Player player = getPlayer();
        Entity vehicle = player.getVehicle();

        //既に何かのエンティティに搭乗している
        if (vehicle != null) {

            //カートエンティティに搭乗している場合、カートエンティティを削除
            if (RaceManager.isKartEntity(vehicle)) {
                RaceManager.removeKartEntity(vehicle);

            //カートエンティティ以外に搭乗している場合は降ろす
            } else {
                player.leaveVehicle();
            }
        }

        //新規に生成したカートエンティティに搭乗させる
        Entity kartEntity = RaceManager.createRacingKart(player.getLocation(), kart);
        kartEntity.setPassenger(player);

        //クライアントで搭乗が解除されている状態で描画されるのを回避するため
        //issue#109
        PacketUtil.sendOwnAttachEntityPacket(player);
    }

    public void recoveryCharacterPhysical() {
        final Player p = getPlayer();
        if (p == null)
            return;

        p.setMaxHealth(this.character.getMaxHealth());
        p.setWalkSpeed(this.character.getWalkSpeed());
        Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
            public void run() {
                if (getPlayer() != null)
                    p.setHealth(character.getMaxHealth());
            }
        }, 5L);
    }
}
