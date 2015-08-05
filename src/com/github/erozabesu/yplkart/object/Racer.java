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
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.task.SendExpandedTitleTask;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Util;

public class Racer extends PlayerObject{

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
        initializeRacer();
    }

    /**
     * メンバ変数を初期化する。<br>
     * Racerオブジェクトはサーバーがリロードされるまで同一のプレイヤーに対し使い回されるため、<br>
     * メンバ変数を初期化するメソッドが別途用意されている。
     */
    public void initializeRacer() {
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

    /** @return レース中ログアウトしたプレイヤーのプレイヤー情報 */
    public PlayerObject getRacingPlayerObject() {
        return this.racingPlayerObject;
    }

    /** @return エントリーしているサーキット名 */
    public String getCircuitName() {
        return this.circuitName;
    }

    /** @return 選択キャラクター */
    public Character getCharacter() {
        return this.character;
    }

    /** @return 選択カート */
    public Kart getKart() {
        return this.kart;
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
        Scoreboards.setPoint(getUUID());
    }

    /** レース中ログアウトしたプレイヤーの情報を格納する */
    public void savePlayerDataOnQuit() {
        if (getPlayer() != null) {
            setRacingPlayerObject(new PlayerObject(getUUID()));
        }
    }

    //〓 Race Edit 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** レースの終了処理を行う */
    public void endRace() {
        setGoal(true);

        //演出パーティクル
        Util.createSignalFireworks(getPlayer().getLocation());
        Util.createFlowerShower(getPlayer(), 20);

        //リザルトメッセージの送信
        sendResult();

        //リザルトをローカルファイルへ保存
        saveResult();

        //初期化 順序に注意
        setStart(false);
        RaceManager.clearCharacterRaceData(this.getUUID());
        RaceManager.clearKartRaceData(this.getUUID());
        RaceManager.leaveRacingKart(getPlayer());
        recoveryAll();
    }

    /**
     * レースのリザルトをゴールしたプレイヤーに送信する。<br>
     * また、全体通知機能がtrueであればサーバーの全プレイヤーに、<br>
     * falseであればレースの参加者のみに併せて送信する。
     */
    private void sendResult() {
        String circuitName = getCircuitName();
        Circuit circuit = RaceManager.getCircuit(circuitName);
        double lapTime = RaceManager.getCircuit(circuitName).getLapMilliSecond() / 1000.0D;
        int currentRank = RaceManager.getGoalPlayer(circuitName).size();
        Number[] messagePartsRaceResult = new Number[]{currentRank, lapTime};

        //〓ゴールしたプレイヤー向けメッセージ送信

        //演出のGOALタイトルを送信
        new SendExpandedTitleTask(getPlayer(), 5, "GOAL!!!" + ChatColor.GOLD, "O", 1, false).runTaskTimer(
                YPLKart.getInstance(), 0, 1);

        //リザルトのサブタイトルを送信
        String messageResultTitle = MessageEnum.titleGoalRank.getConvertedMessage(new Object[]{messagePartsRaceResult});
        PacketUtil.sendTitle(getPlayer(), messageResultTitle, 10, 100, 10, true);

        //〓全体メッセージ送信

        Object[] messageBroadcastParts = new Object[]{getPlayer(), circuit, messagePartsRaceResult};

        //全体通知機能がtrueであれば、ゴールしたプレイヤーのリザルトをサーバーの全プレイヤーに送信する
        if (CircuitConfig.getCircuitData(circuitName).getBroadcastGoalMessage()) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                MessageEnum.raceGoal.sendConvertedMessage(other, messageBroadcastParts);
            }

        //全体通知機能がfalseであれば、ゴールしたプレイヤーのリザルトをレースの参加者のみに送信する
        } else {
            circuit.sendMessageEntryPlayer(MessageEnum.raceGoal, messageBroadcastParts);
        }
    }

    /** レースのラップタイムをローカルファイルへ保存する */
    public void saveResult() {
        String circuitName = getCircuitName();
        double lapTime = RaceManager.getCircuit(circuitName).getLapMilliSecond() / 1000.0D;

        //ローカルファイルへリザルトの保存
        if (getKart() == null) {
            CircuitConfig.addRaceLapTime(getPlayer(), circuitName, lapTime, false);
        } else {
            CircuitConfig.addRaceLapTime(getPlayer(), circuitName, lapTime, true);
        }
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

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
