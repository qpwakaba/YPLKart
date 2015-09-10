package com.github.erozabesu.yplkart.object;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.task.SendExpandedTitleTask;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.RaceEntityUtil;
import com.github.erozabesu.yplkart.utils.Util;

/**
 * レースを開催する仮想サーキットオブジェクトクラス
 * @author erozabesu
 */
public class Circuit extends CircuitData {

    /** サーキットに設置された妨害アイテムエンティティリスト */
    private Set<Entity> jammerEntityList;

    /** エントリーしているプレイヤーのRacerセット */
    private HashSet<Racer> entryRacerSet;

    /** リザーブエントリーしているプレイヤーのRacerセット */
    private HashSet<Racer> reserveEntryRacerSet;

    /**
     * 参加人数が最小人数を満たしているかを検知するタスク<br>
     * 検知した場合タスクを停止し、matchingTaskを起動する
     */
    private BukkitTask detectReadyTask;

    /** 参加者にレース参加の確認を行うタスク */
    private BukkitTask matchingTask;

    /** マッチングタスクの制限時間 */
    private int matchingCountDownTime;

    /** マッチングタスクが起動されているかどうか */
    private boolean isMatching;

    /** 参加者にキャラクター選択、カート選択をさせるタスク */
    private BukkitTask standbyTask;

    /** スタンバイタスクの制限時間 */
    private int standbyCountDownTime;

    /** スタンバイタスクが起動されているかどうか */
    private boolean isStandby;

    /** レースの経過時間に応じ自動終了させるタスク */
    private BukkitTask limitTimeTask;

    /** レースが開始されてからの経過時間 */
    private int currentTime;

    /** レースが開始されているかどうか */
    private boolean isStarted;

    /** 現在のエントリー人数、ゴール人数からレース終了を検知するタスク */
    private BukkitTask detectEndTask;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public Circuit(String circuitName) {
        super(circuitName);
        this.initializeCircuit();
        this.runDetectReadyTask();
    }

    public Circuit(String circuitName, Location location) {
        super(circuitName, location);
        this.initializeCircuit();
        this.runDetectReadyTask();
    }

    private void initializeCircuit() {
        this.setCurrentTime(0);
        this.setMatchingCountDownTime(0);
        this.setStandbyCountDownTime(0);
        this.setEntryRacerSet(new HashSet<Racer>());
        this.setReserveEntryRacerSet(new HashSet<Racer>());
        this.setJammerEntitySet(new HashSet<Entity>());

        if (this.getDetectEndTask() != null) {
            getDetectEndTask().cancel();
        }
        this.setDetectEndTask(null);

        if (this.getLimitTimeTask() != null) {
            this.getLimitTimeTask().cancel();
        }
        this.setLimitTimeTask(null);

        if (this.getDetectReadyTask() != null) {
            this.getDetectReadyTask().cancel();
        }
        this.setDetectReadyTask(null);

        if (this.getMatchingTask() != null) {
            this.getMatchingTask().cancel();
        }
        this.setMatchingTask(null);
        this.setMatching(false);

        if (this.getStandbyTask() != null) {
            this.getStandbyTask().cancel();
        }
        this.setStandbyTask(null);
        this.setStandby(false);

        this.setStarted(false);
    }

    //〓 Race Management 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * レースを開始する<br>
     * 各タスクを起動し、演出を再生する
     */
    public void startRace() {
        this.setStarted(true);
        this.runLimitTimeCountDownTask();
        this.runDetectEndTask();
        for (Player player : getOnlineEntryPlayerList()) {
            Util.createSignalFireworks(player.getLocation());
            Util.createFlowerShower(player, 5);
            new SendExpandedTitleTask(player, 1, "START!!!" + ChatColor.GOLD, "A", 2, false).runTaskTimer(
                    YPLKart.getInstance(), 0, 1);
        }
    }

    /**
     * 開催されているレースを終了する。<br>
     * 参加しているプレイヤーの情報はレース開始前の情報に復元される。<br>
     * 引数clearAllがtrueの場合、全データを初期化した上で、全タスクを停止し、このインスタンスを廃棄する。<br>
     * falseの場合はDetectReadyタスクを再起動し、また、リザーブエントリーを通常のエントリーに昇格した上で新たにマッチングを開始する。
     * @param clearAll リザーブエントリーを利用するかどうか
     */
    public void endRace(boolean clearAll) {
        this.sendMessageEntryPlayer(MessageEnum.raceEnd, MessageParts.getMessageParts(this));
        Iterator<Racer> entryRacerListIterator = getEntryRacerSet().iterator();
        Racer racer;
        while (entryRacerListIterator.hasNext()) {
            racer = entryRacerListIterator.next();
            entryRacerListIterator.remove();
            RaceManager.racerSetter_UnEntry(racer);
        }

        // clearAllがfalseの場合、リザーブエントリーがあれば終了処理後に改めてエントリーさせる
        if (!clearAll) {
            final HashSet<Racer> nextEntryRacerList = this.getReserveEntryRacerSet();
            if (0 < nextEntryRacerList.size()) {
                Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        for (Racer nextRacer : nextEntryRacerList) {
                            if (nextRacer.getPlayer() != null) {
                                entryPlayer(nextRacer);
                            }
                        }
                    }
                }, 10);
            }
        }

        //初期化
        this.removeAllJammerEntity();
        this.initializeCircuit();
        Scoreboards.endCircuit(getCircuitName());

        // clearAllがfalseの場合、DetectReadyタスクを再起動する
        if (!clearAll) {
            this.runDetectReadyTask();
        }
    }

    /**
     * エントリーしている全プレイヤーパラメータをレース用に初期化し、レース開始地点にテレポートする。<br>
     * プレイヤーがオフラインの場合はエントリーを取り消す。
     */
    private void setupAllRacer() {
        int listSize = this.getOnlineEntryRacerList().size();
        List<Location> position = this.getStartLocationList(listSize);
        int count = 0;

        for (Racer racer : this.getEntryRacerSet()) {
            this.setupRacer(racer, position.get(count));
            count++;
        }
    }

    /**
     * 引数uuidのプレイヤーパラメータをレース用に初期化し、引数locationテレポートする。<br>
     * プレイヤーがオフラインの場合はエントリーを取り消す。
     * @param uuid プレイヤーUUID
     * @param location レース開始地点の座標
     */
    public void setupRacer(Racer racer, Location location) {

        //オンラインではないプレイヤーは辞退
        if (racer.getPlayer() == null) {
            this.exitPlayer(racer);

        } else {
            Player player = racer.getPlayer();
            UUID uuid = racer.getUUID();

            //メッセージ送信
            MessageEnum.raceStart.sendConvertedMessage(racer.getPlayer(), MessageParts.getMessageParts(getInstance()));

            //レース用スコアボードを表示
            Scoreboards.entryCircuit(racer.getUUID());

            //VehicleExitEventがフックされるため、スタンバイフラグをtrueに変更する前に搭乗を解除する
            racer.leaveVehicle();

            //Racerオブジェクトの諸々
            racer.applyRaceParameter();
            racer.setCircuit(this);
            racer.setStandby(true);
            racer.setKartEntityLocation(Util.adjustLocationToBlockCenter(location));
            RaceManager.racerSetter_DeselectCharacter(uuid);
            RaceManager.racerSetter_DeselectKart(uuid);
            RaceManager.leaveRacingKart(player);

            //開始地点にテレポート、メニュー表示
            player.teleport(location);
            racer.setRaceStartLocation(location);
            RaceManager.showSelectMenu(player, true);
            ItemEnum.addItem(player, ItemEnum.MENU.getItem());

            //ToggleSneakMOD対策 : issue #150
            player.setSneaking(false);
        }
    }

    /**
     * レーサーをエントリーリストに追加する
     * @param racer 追加するプレイヤーのRacerインスタンス
     */
    public void entryPlayer(Racer racer) {
        this.getEntryRacerSet().add(racer);
        this.getReserveEntryRacerSet().remove(racer);
    }

    /**
     * レーサーをリザーブエントリーリストに追加する
     * @param racer 追加するプレイヤーのRacerインスタンス
     */
    public void entryReservePlayer(Racer racer) {
        this.getReserveEntryRacerSet().add(racer);
        this.getEntryRacerSet().remove(racer);
    }

    /**
     * レーサーをエントリーリストから削除する
     * @param uuid 追加するプレイヤーのRacerインスタンス
     */
    public void exitPlayer(Racer racer) {
        this.getEntryRacerSet().remove(racer);
        this.getReserveEntryRacerSet().remove(racer);
    }

    /**
     * 引数entityをサーキットに設置された妨害エンティティリストに追加する
     * @param entity 追加するエンティティ
     */
    public void addJammerEntity(Entity entity) {
        this.getJammerEntitySet().add(entity);
    }

    /**
     * 引数entityをサーキットに設置された妨害エンティティリストから削除する
     * @param entity 追加するエンティティ
     */
    public void removeJammerEntity(Entity entity) {
        this.getJammerEntitySet().remove(entity);
    }

    /** サーキットに設置された全妨害エンティティをデスポーンする */
    public void removeAllJammerEntity() {
        if (this.getJammerEntitySet().size() != 0) {
            for (Entity entity : this.getJammerEntitySet()) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).setHealth(0.0D);
                } else {
                    entity.remove();

                    // チャンクに妨害エンティティが残っていない場合は配列から削除
                    if (!RaceEntityUtil.containsJammerEntity(entity.getLocation().getChunk())) {
                        RaceEntityUtil.removeJammerEntityExistChunkArray(entity.getLocation().getChunk());
                    }
                }
            }
            this.getJammerEntitySet().clear();
        }
    }

    //〓 Race Management Task 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** エントリープレイヤーが規定人数を満たしたことを検知するタスクを起動する */
    public void runDetectReadyTask() {
        if (this.getDetectReadyTask() != null) {
            this.getDetectReadyTask().cancel();
        }

        this.setDetectReadyTask(Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
            public void run() {

                //エントリーしたプレイヤーが規定人数以上
                if (getEntryRacerSet().size() < getMinPlayer()) {
                    return;
                }

                //オンラインのプレイヤー人数が規定人数以上
                if (getOnlineEntryPlayerList().size() < getMinPlayer()) {
                    return;
                }

                runMatchingTask();
                getDetectReadyTask().cancel();
                setDetectReadyTask(null);
            }
        }, 0, 100));
    }

    /** 参加者にレース参加の確認を行うタスクを起動する */
    public void runMatchingTask() {
        if (this.getMatchingTask() != null) {
            this.getMatchingTask().cancel();
        }

        //タスクの制限時間を初期化
        this.setMatchingCountDownTime(this.getMatchingTime());

        //フラグON
        this.setMatching(true);

        //参加者に承認・拒否を促すTellRawコマンドメッセージを送信
        this.sendRaceInviteMessage();

        //エントリーしたプレイヤーからレース参加の意思決定を募るタスクを起動する
        this.setMatchingTask(Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
            public void run() {

                //制限時間1秒経過
                setMatchingCountDownTime(getMatchingCountDownTime() - 1);

                //制限時間が残っている場合、参加者にカウントダウンタイトルメッセージを送信
                if (0 < getMatchingCountDownTime()) {

                    // エントリー者が全員意思決定を終えている場合は制限時間をタイムアップさせる
                    if (getEntryRacerSet().size() == getMatchingAcceptedRacerList().size()) {
                        setMatchingCountDownTime(0);
                        return;
                    }

                    for (Player player : getOnlineEntryPlayerList()) {
                        PacketUtil.sendTitle(player, MessageEnum.titleRacePrepared.getMessage(), 0, 25, 0, false);
                        PacketUtil.sendTitle(player
                                , MessageEnum.titleCountDown.getConvertedMessage(MessageParts.getMessageParts(getMatchingCountDownTime()))
                                , 0, 25, 0, true);

                        //カウントダウン効果音の再生
                        playCountDownSound(player, getMatchingCountDownTime());
                    }

                //タイムアップ
                } else {

                    List<Racer> matchingAcceptedRacerList = getMatchingAcceptedRacerList();

                    //参加拒否したプレイヤーをエントリー取り消し
                    Iterator<Racer> denyRacerList = getEntryRacerSet().iterator();
                    Racer denyRacer;
                    while (denyRacerList.hasNext()) {
                        denyRacer = denyRacerList.next();
                        if (denyRacer.getPlayer() == null || !matchingAcceptedRacerList.contains(denyRacer)) {
                            denyRacerList.remove();
                            RaceManager.racerSetter_UnEntry(denyRacer);
                        }
                    }

                    //参加承認したプレイヤーがサーキットの規定人数を満たしていればレースを開始する
                    if (getMinPlayer() <= matchingAcceptedRacerList.size()) {

                        //プレイヤーの状態をレース用に初期化
                        setupAllRacer();

                        //スコアボードのタイトルを 参加申請中→ランキング に書き換え
                        Scoreboards.startCircuit(getCircuitName());

                        //スタンバイタスクへ進む
                        runStandbyTask();

                    //参加承認したプレイヤーがサーキットの規定人数を下回る場合runDetectReadyTaskを再起動する
                    } else {

                        //規定人数の参加承認に失敗したことを参加者に通知
                        sendMessageEntryPlayer(MessageEnum.raceMatchingFailed, MessageParts.getMessageParts(getInstance()));
                        setMatching(false);

                        //リザーブエントリーがあればサーキットの最大人数以内でエントリーに昇格する
                        Iterator<Racer> reserveRacerList = getReserveEntryRacerSet().iterator();
                        Racer reserveRacer = null;
                        while (reserveRacerList.hasNext()) {
                            reserveRacer = reserveRacerList.next();

                            //エントリーがサーキットの最大人数を下回る場合追加エントリー
                            if (!isFillPlayer()) {
                                reserveRacerList.remove();
                                entryPlayer(reserveRacer);

                            //最大人数を満たしている場合引き続きリザーブエントリーとして保留
                            } else {
                                break;
                            }
                        }

                        //1段階前のタスクに戻る
                        runDetectReadyTask();
                    }

                    //初期化
                    setMatching(false);
                    setMatchingCountDownTime(0);

                    if (getMatchingTask() != null) {
                        getMatchingTask().cancel();
                    }
                    setMatchingTask(null);
                }
            }
        }, 0, 20));
    }

    /**
     * レース開始地点に参加者をテレポート後に起動する<br>
     * メニューを表示し、制限時間内にキャラクター・カートを選択させる
     * 制限時間経過後キャラクター・カートがnullだった場合はランダム選択させ、
     * レース開始のカウントダウンをスタートする
     * この間にログアウトしたプレイヤーがメニューアイテムを所持し続ける問題を回避するため、
     * 所持品のキーアイテム削除はプレイヤーがスタートブロックを踏んだ際に行う
     * カウントダウン終了と同時にstartフラグをtrueに切り替える
     */
    public void runStandbyTask() {
        if (this.getStandbyTask() != null) {
            this.getStandbyTask().cancel();
        }

        this.setStandbyCountDownTime(this.getMenuTime() + 12);
        this.setStandby(true);

        this.setStandbyTask(Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
            public void run() {
                setStandbyCountDownTime(getStandbyCountDownTime() - 1);

                //選択猶予時間がまだ残っている
                if (12 < getStandbyCountDownTime()) {
                    for (Player player : getOnlineEntryPlayerList()) {
                        int count = getStandbyCountDownTime() - 12;
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceMenu.getMessage(), 0, 25, 0, false);
                        PacketUtil.sendTitle(player, MessageEnum.titleCountDown.getConvertedMessage(MessageParts.getMessageParts(count)), 0, 25, 0, true);
                        playCountDownSound(player, count);
                    }

                //選択猶予時間がタイムアップ
                } else if (getStandbyCountDownTime() == 12) {
                    for (Racer racer : getOnlineEntryRacerList()) {
                        if (racer.getCharacter() == null) {
                            RaceManager.racerSetter_Character(racer.getUUID(), CharacterConfig.getRandomCharacter());
                        }

                        if (racer.getKart() == null && getRaceType().equals(RaceType.KART)) {
                            RaceManager.racerSetter_Kart(racer.getUUID(), KartConfig.getRandomKart());
                        }

                        Player player = racer.getPlayer();
                        player.closeInventory();
                        ItemEnum.removeAllKeyItems(player);
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceStandby.getMessage(), 10, 40, 10, false);
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceStandbySub.getMessage(), 10, 40, 10, true);
                    }
                } else if (getStandbyCountDownTime() == 10) {
                    for (Player player : getOnlineEntryPlayerList()) {
                        MessageParts numberParts = MessageParts.getMessageParts(getInstance().getNumberOfLaps());
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceLaps.getConvertedMessage(numberParts), 10, 40, 10, false);
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceLapsSub.getMessage(), 10, 40, 10, true);
                    }
                } else if (getStandbyCountDownTime() == 8) {
                    for (Player player : getOnlineEntryPlayerList()) {
                        MessageParts numberParts = MessageParts.getMessageParts(getInstance().getLimitTime());
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceTimeLimit.getConvertedMessage(numberParts), 10, 40, 10, false);
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceTimeLimitSub.getMessage(), 10, 40, 10, true);
                    }
                } else if (getStandbyCountDownTime() == 6) {
                    for (Player player : getOnlineEntryPlayerList()) {
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceReady.getMessage(), 10, 20, 10, false);
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceReadySub.getMessage(), 10, 20, 10, true);
                    }
                } else if (0 < getStandbyCountDownTime() && getStandbyCountDownTime() < 4) {
                    for (Player player : getOnlineEntryPlayerList()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 4.0F, 2.0F);
                        MessageParts numberParts = MessageParts.getMessageParts(getStandbyCountDownTime());
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceStartCountDown.getConvertedMessage(numberParts), 0, 20, 0, false);
                        PacketUtil.sendTitle(player, MessageEnum.titleRaceStartCountDownSub.getMessage(), 0, 20, 0, true);
                    }
                } else if (getStandbyCountDownTime() <= 0) {
                    setStandbyCountDownTime(0);
                    setStandby(false);
                    getStandbyTask().cancel();
                    setStandbyTask(null);

                    startRace();

                    return;
                }
            }
        }, 0, 20));
    }

    /** レースの経過時間に応じ自動終了させるタスクを起動する */
    public void runLimitTimeCountDownTask() {
        if (this.getLimitTimeTask() != null) {
            this.getLimitTimeTask().cancel();
        }

        final int limitTime = this.getLimitTime();

        this.setLimitTimeTask(Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
            public void run() {
                setCurrentTime(getCurrentTime() + 1);

                if (getCurrentTime() % 20 == 0) {
                    MessageParts circuitParts = MessageParts.getMessageParts(getInstance());

                    int remainTime = limitTime - getCurrentTime() / 20;
                    if (remainTime == 60) {
                        for (Player p : getOnlineEntryPlayerList()) {
                            playCountDownSound(p, remainTime);
                            MessageEnum.raceTimeLimitAlert.sendConvertedMessage(p, circuitParts, MessageParts.getMessageParts(60));
                        }
                    } else if (remainTime == 30) {
                        for (Player p : getOnlineEntryPlayerList()) {
                            playCountDownSound(p, remainTime);
                            MessageEnum.raceTimeLimitAlert.sendConvertedMessage(p, circuitParts, MessageParts.getMessageParts(30));
                        }
                    } else if (remainTime == 10) {
                        for (Player p : getOnlineEntryPlayerList()) {
                            playCountDownSound(p, remainTime);
                            MessageEnum.raceTimeLimitAlert.sendConvertedMessage(p, circuitParts, MessageParts.getMessageParts(10));
                        }
                    } else if (0 < remainTime && remainTime < 10) {
                        for (Player p : getOnlineEntryPlayerList()) {
                            playCountDownSound(p, remainTime);
                            MessageEnum.raceTimeLimitCountDown.sendConvertedMessage(p, circuitParts, MessageParts.getMessageParts(remainTime));
                        }
                    } else if (remainTime == 0) {
                        for (Player p : getOnlineEntryPlayerList()) {
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 2.0F, 1.0F);
                            MessageEnum.raceTimeUp.sendConvertedMessage(p, circuitParts);
                        }
                    } else if (remainTime < 0) {
                        endRace(false);
                    }
                }
            }
        }, 0, 1));
    }

    /** 現在のレース参加者の状態からレースの終了を検知し、終了処理を行うタスクを起動する */
    public void runDetectEndTask() {
        if (this.getDetectEndTask() != null) {
            this.getDetectEndTask().cancel();
        }

        this.setDetectEndTask(Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
            public void run() {
                if (isRaceEnd()) {
                    endRace(false);
                }
            }
        }, 10, 100));
    }

    //〓 Util - Get 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return レーススタートからの経過時間を、チック→ミリ秒に変換し返す */
    public int getLapMilliSecond() {
        return this.getCurrentTime() * 50;
    }

    /**
     * エントリーしているレーサーのうち、オンラインの全レーサーをListで返す。
     * @return オンラインのエントリレーサーList
     */
    public List<Racer> getOnlineEntryRacerList() {
        List<Racer> entryList = new ArrayList<Racer>();

        for (Racer racer : this.getEntryRacerSet()) {
            if (racer.getPlayer() != null) {
                entryList.add(racer);
            }
        }

        return entryList;
    }

    /**
     * エントリーしているプレイヤーのうち、オンラインの全プレイヤーをListで返す。
     * @return オンラインのエントリプレイヤーList
     */
    public List<Player> getOnlineEntryPlayerList() {
        List<Player> playerList = new ArrayList<Player>();

        for (Racer racer : this.getEntryRacerSet()) {
            Player player = racer.getPlayer();
            if (player != null) {
                playerList.add(player);
            }
        }

        return playerList;
    }

    /**
     * オンラインのレーサーのうち、ゴールフラグがfalseのレーサーをListで返す。
     * @return 走行中のオンラインレーサーList
     */
    public List<Racer> getOnlineRacingRacerList() {
        List<Racer> racerList = new ArrayList<Racer>();

        for (Racer racer : this.getOnlineEntryRacerList()) {
            if (!racer.isGoal()) {
                racerList.add(racer);
            }
        }

        return racerList;
    }

    /**
     * オンラインのプレイヤーのうち、ゴールフラグがfalseのプレイヤーをListで返す。
     * @return 走行中のオンラインプレイヤーList
     */
    public List<Player> getOnlineRacingPlayerList() {
        List<Player> playerList = new ArrayList<Player>();

        for (Racer racer : this.getOnlineEntryRacerList()) {
            if (!racer.isGoal()) {
                playerList.add(racer.getPlayer());
            }
        }

        return playerList;
    }

    /**
     * エントリーレーサーのうちゴールしているレーサーをListで返す。<br>
     * オンラインかどうかは考慮しない。
     * @return ゴールしているレーサーList
     */
    public List<Racer> getGoalRacerList() {
        List<Racer> racerList = new ArrayList<Racer>();

        for (Racer racer : this.getEntryRacerSet()) {
            if (racer.isGoal()) {
                racerList.add(racer);
            }
        }

        return racerList;
    }

    /**
     * エントリーレーサーのうちマッチングに同意しているレーサーをListで返す。<br>
     * オンラインかどうかは考慮しない。
     * @return マッチングに同意しているレーサーList
     */
    public List<Racer> getMatchingAcceptedRacerList() {
        List<Racer> racerList = new ArrayList<Racer>();

        for (Racer racer : this.getEntryRacerSet()) {
            if (racer.isMatchingAccepted()) {
                racerList.add(racer);
            }
        }

        return racerList;
    }

    /**
     * エントリーレーサーのうちマッチングに同意していないプレイヤーをListで返す。<br>
     * オンラインかどうかは考慮しない。
     * @return マッチングに同意していないレーサーList
     */
    public List<Racer> getMatchingNotAcceptedRacerList() {
        List<Racer> racerList = new ArrayList<Racer>();

        for (Racer racer : this.getEntryRacerSet()) {
            if (!racer.isMatchingAccepted()) {
                racerList.add(racer);
            }
        }

        return racerList;
    }

    //〓 Util - Is 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * レースが最少プレイ人数を満たすまで待機している状態かどうかを返す。
     * @return ウェイティングフェーズかどうか
     */
    public boolean isWaitingRacerPhase() {
        return !this.isMatching() && !this.isStandby() && !this.isStarted();
    }

    /**
     * レースが最小プレイ人数を満たし、参加者のレース参加同意を待機している状態かどうかを返す。
     * @return マッチングフェーズかどうか
     */
    public boolean isMatchingPhase() {
        return this.isMatching() && !this.isStandby() && !this.isStarted();
    }

    /**
     * 参加者をレース開始地点にテレポート後、キャラクター・カートを選択させている状態かどうかを返す。
     * @return スタンバイフェーズかどうか
     */
    public boolean isStandbyPhase() {
        return !this.isMatching() && this.isStandby() && !this.isStarted();
    }

    /**
     * レースが出走した状態かどうかを返す。
     * @return レーシングフェーズかどうか
     */
    public boolean isRacingPhase() {
        return !this.isMatching() && !this.isStandby() && this.isStarted();
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

    public boolean isFillPlayer() {
        if (this.getMaxPlayer() <= this.getEntryRacerSet().size()) {
            return true;
        }
        return false;
    }

    /**
     * 現在進行形で開催されているレースがなく、レースが終了状態かどうかを返す。<br>
     * レースがスタートしており、かつ走行中のプレイヤーが残っている場合はfalseを返す。<br>
     * また、レースがまだスタートしていない状態、かつスタンバイ状態のプレイヤーがいる場合もfalseを返す。<br>
     * 上記の状態以外の場合はtrueを返す。
     * @return 開催されているレースがあるかどうか
     */
    public boolean isRaceEnd() {
        if (this.isRacingPhase()) {
            for (Racer racer : this.getOnlineEntryRacerList()) {
                if (!racer.isGoal()) {
                    return false;
                }
            }
        } else if (this.isStandbyPhase()) {
            return this.getOnlineEntryRacerList().size() == 0;
        }

        return true;
    }

    public boolean isJammerEntity(Entity entity) {
        return this.getJammerEntitySet().contains(entity);
    }

    //〓 Util - Do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * レース参加者にメッセージを送信する
     * @param message MessageEnum
     * @param object タグ変換する変数
     */
    public void sendMessageEntryPlayer(MessageEnum message, MessageParts... messageParts) {
        for (Player player : getOnlineEntryPlayerList()) {
            message.sendConvertedMessage(player, messageParts);
        }
    }

    /** レース参加者にレースへ招待するメッセージを送信する */
    public void sendRaceInviteMessage() {
        String tellraw = " [\"\",{\"text\":\"========\",\"color\":\"gray\",\"bold\":\"true\"},{\"text\":\"[参加する]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ka circuit accept\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"レースへの参加を承認します\n\",\"color\":\"yellow\"},{\"text\":\"承認した参加者が規定人数を満たせばレースが開始されます\",\"color\":\"yellow\"}]}},\"bold\":\"false\"},{\"text\":\"====\",\"color\":\"gray\",\"bold\":\"true\"},{\"text\":\"[辞退する]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ka circuit deny\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"レースの参加を辞退し、エントリーを取り消します\",\"color\":\"yellow\"}]}},\"bold\":\"false\"},{\"text\":\"========\",\"color\":\"gray\",\"bold\":\"true\"}]";

        for (Racer racer : this.getMatchingNotAcceptedRacerList()) {
            Player player = racer.getPlayer();
            if (player != null) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                MessageEnum.raceReady.sendConvertedMessage(player, MessageParts.getMessageParts(getInstance()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + tellraw);
            }
        }
    }

    /**
     * 引数countDownTimeの数値に応じてカウントダウンの音声を再生する
     * @param player 音声を再生するプレイヤー
     * @param countDownTime 秒数
     */
    public void playCountDownSound(Player player, int countDownTime) {
        Location location = player.getLocation();

        if (countDownTime % 10 == 0) {
            player.playSound(location, Sound.DOOR_OPEN, 0.3F, 4.0F);
        } else if (30 < countDownTime) {
            player.playSound(location, Sound.CLICK, 0.3F, 4.0F);
        } else if (20 < countDownTime) {
            player.playSound(location, Sound.CLICK, 0.3F, 4.0F);
        } else if (10 < countDownTime) {
            player.playSound(location, Sound.CLICK, 0.3F, 4.0F);
        } else if (countDownTime <= 10) {
            player.playSound(location, Sound.ITEM_BREAK, 0.2F, 4.0F);
        }
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return このクラスのインスタンス */
    public Circuit getInstance() {
        return this;
    }

    /** @return サーキットに設置された妨害アイテムエンティティリスト */
    public Set<Entity> getJammerEntitySet() {
        return this.jammerEntityList;
    }

    /** @return エントリーしているプレイヤーのRacerセット */
    public HashSet<Racer> getEntryRacerSet() {
        return entryRacerSet;
    }

    /** @return リザーブエントリーしているプレイヤーのRacerセット */
    public HashSet<Racer> getReserveEntryRacerSet() {
        return reserveEntryRacerSet;
    }

    /** @return 参加人数が最小人数を満たしているかを検知するタスク */
    public BukkitTask getDetectReadyTask() {
        return this.detectReadyTask;
    }

    /** @return 参加者にレース参加の確認を行うタスク */
    public BukkitTask getMatchingTask() {
        return this.matchingTask;
    }

    /** @return マッチングタスクの制限時間 */
    public int getMatchingCountDownTime() {
        return this.matchingCountDownTime;
    }

    /** @return 参加者にキャラクター選択、カート選択をさせるタスク */
    public BukkitTask getStandbyTask() {
        return this.standbyTask;
    }

    /** @return スタンバイタスクの制限時間 */
    public int getStandbyCountDownTime() {
        return this.standbyCountDownTime;
    }

    /** @return レースの経過時間に応じ自動終了させるタスク */
    public BukkitTask getLimitTimeTask() {
        return this.limitTimeTask;
    }

    /** @return レースが開始されてからの経過時間 */
    public int getCurrentTime() {
        return this.currentTime;
    }

    /** @return 現在のエントリー人数、ゴール人数からレース終了を検知するタスク */
    public BukkitTask getDetectEndTask() {
        return this.detectEndTask;
    }

    /** @return マッチングタスクが起動されているかどうか */
    public boolean isMatching() {
        return isMatching;
    }

    /** @return スタンバイタスクが起動されているかどうか */
    public boolean isStandby() {
        return isStandby;
    }

    /** @return レースが開始されているかどうか */
    public boolean isStarted() {
        return isStarted;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param jammerEntityList サーキットに設置された妨害アイテムエンティティリスト */
    public void setJammerEntitySet(Set<Entity> jammerEntityList) {
        this.jammerEntityList = jammerEntityList;
    }

    /** @param entryRacerSet エントリーしているプレイヤーのRacerセット */
    public void setEntryRacerSet(HashSet<Racer> entryRacerSet) {
        this.entryRacerSet = entryRacerSet;
    }

    /** @param reserveEntryRacerSet リザーブエントリーしているプレイヤーのRacerセット */
    public void setReserveEntryRacerSet(HashSet<Racer> reserveEntryRacerSet) {
        this.reserveEntryRacerSet = reserveEntryRacerSet;
    }

    /** @param detectReadyTask 参加人数が最小人数を満たしているかを検知するタスク */
    public void setDetectReadyTask(BukkitTask detectReadyTask) {
        this.detectReadyTask = detectReadyTask;
    }

    /** @param matchingTask 参加者にレース参加の確認を行うタスク */
    public void setMatchingTask(BukkitTask matchingTask) {
        this.matchingTask = matchingTask;
    }

    /** @param matchingCountDownTime マッチングタスクの制限時間 */
    public void setMatchingCountDownTime(int matchingCountDownTime) {
        this.matchingCountDownTime = matchingCountDownTime;
    }

    /** @param standbyTask 参加者にキャラクター選択、カート選択をさせるタスク */
    public void setStandbyTask(BukkitTask standbyTask) {
        this.standbyTask = standbyTask;
    }

    /** @param standbyCountDownTime スタンバイタスクの制限時間 */
    public void setStandbyCountDownTime(int standbyCountDownTime) {
        this.standbyCountDownTime = standbyCountDownTime;
    }

    /** @param limitTimeTask レースの経過時間に応じ自動終了させるタスク */
    public void setLimitTimeTask(BukkitTask limitTimeTask) {
        this.limitTimeTask = limitTimeTask;
    }

    /** @param currentTime レースが開始されてからの経過時間 */
    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    /** @param detectEndTask 現在のエントリー人数、ゴール人数からレース終了を検知するタスク */
    public void setDetectEndTask(BukkitTask detectEndTask) {
        this.detectEndTask = detectEndTask;
    }

    /** @param isMatching マッチングタスクが起動されているかどうか */
    public void setMatching(boolean isMatching) {
        this.isMatching = isMatching;
    }

    /** @param isStandby スタンバイタスクが起動されているかどうか */
    public void setStandby(boolean isStandby) {
        this.isStandby = isStandby;
    }

    /** @param isStarted レースが開始されているかどうか */
    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }
}
