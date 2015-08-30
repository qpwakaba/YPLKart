package com.github.erozabesu.yplkart.object;

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
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.Scoreboards;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.CharacterConfig;
import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.data.KartConfig;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.task.SendExpandedTitleTask;
import com.github.erozabesu.yplkart.utils.PacketUtil;
import com.github.erozabesu.yplkart.utils.Util;

/**
 * レースを開催する仮想サーキットオブジェクトクラス
 * @author erozabesu
 */
public class Circuit {

    /** サーキット名 */
    private String circuitName;

    /** サーキットに設置された妨害アイテムエンティティリスト */
    private List<Entity> jammerEntityList;

    /** エントリーしているプレイヤーのUUIDリスト */
    private List<UUID> entryPlayerList;

    /**
     * リザーブエントリーしているプレイヤーのUUIDリスト<br>
     * リザーブエントリーは、現在のエントリー人数が最大人数を上回っている、<br>
     * もしくは既にレースがスタートしている場合に利用する、次回開催するレースに繰り越すエントリーリスト
     */
    private List<UUID> reserveEntryPlayerList;

    /** レース参加の招待を承認したプレイヤーのUUIDリスト */
    private List<UUID> matchingAcceptPlayerList;

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

    /** レースの経過時間に応じ自動終了させるタスク */
    private BukkitTask limitTimeTask;

    /** レースが開始されてからの経過時間 */
    private int currentTime;

    /** 現在のエントリー人数、ゴール人数からレース終了を検知するタスク */
    private BukkitTask detectEndTask;

    /** レースが開始されているかどうか */
    private boolean isStarted;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public Circuit() {
    }

    public Circuit(String circuitName) {
        this.setCircuitName(circuitName);
        this.initialize();

        this.runDetectReadyTask();
    }

    public void initialize() {
        this.setCurrentTime(0);
        this.setMatchingCountDownTime(0);
        this.setStandbyCountDownTime(0);
        this.setStarted(false);
        this.setMatching(false);
        this.setEntryPlayerList(new ArrayList<UUID>());
        this.setReserveEntryPlayerList(new ArrayList<UUID>());
        this.setMatchingAcceptPlayerList(new ArrayList<UUID>());
        this.setJammerEntityList(new ArrayList<Entity>());

        if (this.getDetectEndTask() != null)
            getDetectEndTask().cancel();
        this.setDetectEndTask(null);

        if (this.getLimitTimeTask() != null)
            this.getLimitTimeTask().cancel();
        this.setLimitTimeTask(null);

        if (this.getDetectReadyTask() != null)
            this.getDetectReadyTask().cancel();
        this.setDetectReadyTask(null);

        if (this.getMatchingTask() != null)
            this.getMatchingTask().cancel();
        this.setMatchingTask(null);

        if (this.getStandbyTask() != null)
            this.getStandbyTask().cancel();
        this.setStandbyTask(null);
    }

    //〓 Race Management 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * レースを開始する<br>
     * 各タスクを起動し、演出を再生する
     */
    public void startRace() {
        setStarted(true);
        runLimitTimeCountDownTask();
        runDetectEndTask();
        for (Player p : getEntryPlayer()) {
            Util.createSignalFireworks(p.getLocation());
            Util.createFlowerShower(p, 5);
            new SendExpandedTitleTask(p, 1, "START!!!" + ChatColor.GOLD, "A", 2, false).runTaskTimer(
                    YPLKart.getInstance(), 0, 1);
        }
    }

    /**
     * 開催されているレースを終了する<br>
     * 参加しているプレイヤーの情報はレース開始前の情報に復元される<br>
     * リザーブエントリーがある場合、リザーブエントリーを通常のエントリーに昇格し、<br>
     * 新たにマッチングを開始する
     */
    public void endRace() {
        sendMessageEntryPlayer(MessageEnum.raceEnd, new Object[] { getInstance() });
        Iterator<UUID> i = getEntryPlayerList().iterator();
        UUID id;
        while (i.hasNext()) {
            id = i.next();
            i.remove();
            RaceManager.clearEntryRaceData(id);
        }

        //リザーブエントリーがあれば終了処理後に改めてサーキットを新規作成する
        //ただしYPLKart.onDisable()から呼び出されている場合は何もしない
        if (YPLKart.getInstance().isEnabled()) {
            final List<UUID> nextentry = new ArrayList<UUID>(getReserveEntryPlayerList());
            if (0 < nextentry.size()) {
                Bukkit.getScheduler().runTaskLater(YPLKart.getInstance(), new Runnable() {
                    public void run() {
                        Circuit c = RaceManager.setupCircuit(getCircuitName());
                        for (UUID id : nextentry) {
                            if (Bukkit.getPlayer(id) != null) {
                                RaceManager.setEntryRaceData(id, getCircuitName(), false);
                                c.entryPlayer(id);
                            }
                        }
                    }
                }, 10);
            }
        }

        //初期化
        removeAllJammerEntity();
        initialize();
        RaceManager.clearCircuitData(getCircuitName());
        Scoreboards.endCircuit(getCircuitName());
    }

    /** エントリープレイヤーのパラメータをレース用に初期化し、レース開始地点にテレポートする */
    private void setupRacer() {
        List<Location> position = CircuitConfig.getCircuitData(getCircuitName()).getStartLocationList();
        int count = 0;

        for (UUID uuid : getEntryPlayerList()) {
            if (Bukkit.getPlayer(uuid) != null) {
                Player player = Bukkit.getPlayer(uuid);
                Racer racer = RaceManager.getRacer(player);

                //メッセージ送信
                MessageEnum.raceStart.sendConvertedMessage(player, new Object[] {getInstance()});

                //レース用スコアボードを表示
                Scoreboards.entryCircuit(uuid);

                //VehicleExitEventがフックされるため、スタンバイフラグをtrueに変更する前に搭乗を解除する
                racer.leaveVehicle();

                //Racerオブジェクトの諸々
                racer.applyRaceParameter();
                racer.setCircuitName(getCircuitName());
                racer.setStandby(true);
                racer.setKartEntityLocation(Util.adjustBlockLocation(position.get(count)));
                RaceManager.clearCharacterRaceData(uuid);
                RaceManager.clearKartRaceData(uuid);
                RaceManager.leaveRacingKart(player);

                //開始地点にテレポート、メニュー表示
                player.teleport(position.get(count));
                racer.setRaceStartLocation(position.get(count));
                RaceManager.showSelectMenu(player, true);
                ItemEnum.addItem(player, ItemEnum.MENU.getItem());

                //ToggleSneakMOD対策 : issue #150
                player.setSneaking(false);

                count++;
                continue;
            }

            //オンラインではないプレイヤーは辞退
            exitPlayer(uuid);
        }
    }

    /**
     * プレイヤーをエントリーリストに追加する
     * @param uuid 追加するプレイヤーのUUID
     */
    public void entryPlayer(UUID uuid) {
        if (!this.getEntryPlayerList().contains(uuid)) {
            this.getEntryPlayerList().add(uuid);
        }

        if (this.getReserveEntryPlayerList().contains(uuid)) {
            this.getReserveEntryPlayerList().remove(uuid);
        }
    }

    /**
     * プレイヤーをリザーブエントリーリストに追加する
     * @param uuid 追加するプレイヤーのUUID
     */
    public void entryReservePlayer(UUID uuid) {
        if (!this.getReserveEntryPlayerList().contains(uuid)) {
            this.getReserveEntryPlayerList().add(uuid);
        }

        if (this.getEntryPlayerList().contains(uuid)) {
            this.getEntryPlayerList().remove(uuid);
        }
    }

    /**
     * プレイヤーをエントリーリストから削除する
     * エントリーを削除した影響でプレイしている人数が0人になった場合はレースを終了する
     * @param uuid 追加するプレイヤーのUUID
     */
    public void exitPlayer(UUID uuid) {
        if (this.getEntryPlayerList().contains(uuid)) {
            this.getEntryPlayerList().remove(uuid);
        }

        if (this.getReserveEntryPlayerList().contains(uuid)) {
            this.getReserveEntryPlayerList().remove(uuid);
        }

        denyMatching(uuid);

        if (this.isRaceEnd()) {
            this.endRace();
        }
    }

    /**
     * レースへの招待TellRawメッセージを承認したプレイヤーリストに追加する
     * @param uuid 追加するプレイヤーのUUID
     */
    public void acceptMatching(UUID uuid) {
        if (!this.getMatchingAcceptPlayerList().contains(uuid)) {
            this.getMatchingAcceptPlayerList().add(uuid);
        }
    }

    /**
     * レースへの招待TellRawメッセージを承認したプレイヤーリストから削除する
     * @param uuid 追加するプレイヤーのUUID
     */
    public void denyMatching(UUID uuid) {
        if (this.getMatchingAcceptPlayerList().contains(uuid)) {
            this.getMatchingAcceptPlayerList().remove(uuid);
        }
    }

    /**
     * 引数entityをサーキットに設置された妨害エンティティリストに追加する
     * @param entity 追加するエンティティ
     */
    public void addJammerEntity(Entity entity) {
        this.getJammerEntityList().add(entity);
    }

    /**
     * 引数entityをサーキットに設置された妨害エンティティリストから削除する
     * @param entity 追加するエンティティ
     */
    public void removeJammerEntity(Entity entity) {
        if (this.getJammerEntityList().contains(entity)) {
            this.getJammerEntityList().remove(entity);
        }
    }

    /** サーキットに設置された全妨害エンティティをデスポーンする */
    public void removeAllJammerEntity() {
        if (this.getJammerEntityList().size() != 0) {
            for (Entity entity : this.getJammerEntityList()) {
                if (!entity.isDead()) {
                    entity.remove();
                }
            }
            this.getJammerEntityList().clear();
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
                if (getEntryPlayerList().size() < CircuitConfig.getCircuitData(getCircuitName()).getMinPlayer()) {
                    return;
                }
                //オンラインのプレイヤー人数が規定人数以上
                if (getEntryPlayer().size() < CircuitConfig.getCircuitData(getCircuitName()).getMinPlayer()) {
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

        //ローカルファイルのサーキット設定データ
        final CircuitData circuitData = CircuitConfig.getCircuitData(this.getCircuitName());

        //タスクの制限時間を初期化
        this.setMatchingCountDownTime(circuitData.getMatchingTime());

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
                    Player entryPlayer = null;
                    for (UUID id : getEntryPlayerList()) {
                        if ((entryPlayer = Bukkit.getPlayer(id)) != null) {
                            PacketUtil.sendTitle(entryPlayer, MessageEnum.titleRacePrepared.getMessage(), 0, 25, 0, false);
                            PacketUtil.sendTitle(entryPlayer
                                    , MessageEnum.titleCountDown.getConvertedMessage(getMatchingCountDownTime())
                                    , 0, 25, 0, true);

                            //カウントダウン効果音の再生
                            playCountDownSound(entryPlayer, getMatchingCountDownTime());
                        }
                    }

                //タイムアップ
                } else {

                    //参加拒否したプレイヤーをエントリー取り消し
                    Iterator<UUID> denyPlayerList = getEntryPlayerList().iterator();
                    UUID denyPlayerUUID;
                    while (denyPlayerList.hasNext()) {
                        denyPlayerUUID = denyPlayerList.next();
                        if (!Bukkit.getPlayer(denyPlayerUUID).isOnline() || !getMatchingAcceptPlayerList().contains(denyPlayerUUID)) {
                            denyPlayerList.remove();
                            denyMatching(denyPlayerUUID);
                            RaceManager.clearEntryRaceData(denyPlayerUUID);
                        }
                    }

                    //参加承認したプレイヤーがサーキットの規定人数を満たしていればレースを開始する
                    if (circuitData.getMinPlayer() <= getMatchingAcceptPlayerList().size()) {

                        //プレイヤーの状態をレース用に初期化
                        setupRacer();

                        //スコアボードのタイトルを 参加申請中→ランキング に書き換え
                        Scoreboards.startCircuit(getCircuitName());

                        //スタンバイタスクへ進む
                        runStandbyTask();

                    //参加承認したプレイヤーがサーキットの規定人数を下回る場合runDetectReadyTaskを再起動する
                    } else {

                        //規定人数の参加承認に失敗したことを参加者に通知
                        sendMessageEntryPlayer(MessageEnum.raceMatchingFailed, new Object[] { getInstance() });
                        setMatching(false);

                        //リザーブエントリーがあればサーキットの最大人数以内でエントリーに昇格する
                        Iterator<UUID> reservePlayerList = getReserveEntryPlayerList().iterator();
                        UUID reservePlayer = null;
                        while (reservePlayerList.hasNext()) {
                            reservePlayer = reservePlayerList.next();

                            //エントリーがサーキットの最大人数を下回る場合追加エントリー
                            if (!isFillPlayer()) {
                                reservePlayerList.remove();
                                entryPlayer(reservePlayer);

                            //最大人数を満たしている場合引き続きリザーブエントリーとして保留
                            } else {
                                break;
                            }
                        }

                        //1段階前のタスクに戻る
                        runDetectReadyTask();
                    }

                    //初期化
                    getMatchingAcceptPlayerList().clear();
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

        //ローカルファイルのサーキット設定データ
        final CircuitData circuitData = CircuitConfig.getCircuitData(this.getCircuitName());

        this.setStandbyCountDownTime(circuitData.getMenuTime() + 12);

        this.setStandbyTask(Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
            public void run() {
                setStandbyCountDownTime(getStandbyCountDownTime() - 1);

                //選択猶予時間がまだ残っている
                if (12 < getStandbyCountDownTime()) {
                    for (Player p : getEntryPlayer()) {
                        int count = getStandbyCountDownTime() - 12;
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceMenu.getMessage(), 0, 25, 0, false);
                        PacketUtil.sendTitle(p, MessageEnum.titleCountDown.getConvertedMessage(count), 0, 25, 0, true);
                        playCountDownSound(p, count);
                    }

                //選択猶予時間がタイムアップ
                } else if (getStandbyCountDownTime() == 12) {
                    for (Player p : getEntryPlayer()) {
                        if (RaceManager.getRacer(p).getCharacter() == null) {
                            RaceManager.setCharacterRaceData(p.getUniqueId(), CharacterConfig.getRandomCharacter());
                        }

                        if (RaceManager.getRacer(p).getKart() == null
                                && circuitData.getRaceType().equals(RaceType.KART)) {
                            RaceManager.setKartRaceData(p.getUniqueId(), KartConfig.getRandomKart());
                        }
                        p.closeInventory();
                        ItemEnum.removeAllKeyItems(p);
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceStandby.getMessage(), 10, 40, 10, false);
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceStandbySub.getMessage(), 10, 40, 10, true);
                    }
                } else if (getStandbyCountDownTime() == 10) {
                    for (Player p : getEntryPlayer()) {
                        PacketUtil.sendTitle(
                                p, MessageEnum.titleRaceLaps.getConvertedMessage(
                                        CircuitConfig.getCircuitData(getCircuitName()).getNumberOfLaps())
                                        , 10, 40, 10, false);
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceLapsSub.getMessage(), 10, 40, 10, true);
                    }
                } else if (getStandbyCountDownTime() == 8) {
                    for (Player p : getEntryPlayer()) {
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceTimeLimit.getConvertedMessage(
                                CircuitConfig.getCircuitData(getCircuitName()).getLimitTime()), 10,
                                40, 10, false);
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceTimeLimitSub.getMessage(), 10, 40, 10, true);
                    }
                } else if (getStandbyCountDownTime() == 6) {
                    for (Player p : getEntryPlayer()) {
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceReady.getMessage(), 10, 20, 10, false);
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceReadySub.getMessage(), 10, 20, 10, true);
                    }
                } else if (0 < getStandbyCountDownTime() && getStandbyCountDownTime() < 4) {
                    for (Player p : getEntryPlayer()) {
                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 4.0F, 2.0F);
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceStartCountDown.getConvertedMessage(getStandbyCountDownTime()), 0, 20,
                                0, false);
                        PacketUtil.sendTitle(p, MessageEnum.titleRaceStartCountDownSub.getMessage(), 0, 20, 0, true);
                    }
                } else if (getStandbyCountDownTime() == 0) {
                    startRace();
                } else if (getStandbyCountDownTime() < 0) {
                    setStandbyCountDownTime(0);
                    getStandbyTask().cancel();
                    setStandbyTask(null);
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

        final int limitTime = CircuitConfig.getCircuitData(getCircuitName()).getLimitTime();

        this.setLimitTimeTask(Bukkit.getScheduler().runTaskTimer(YPLKart.getInstance(), new Runnable() {
            public void run() {
                setCurrentTime(getCurrentTime() + 1);

                if (getCurrentTime() % 20 == 0) {
                    int remainTime = limitTime - getCurrentTime() / 20;
                    if (remainTime == 60) {
                        for (Player p : getEntryPlayer()) {
                            playCountDownSound(p, remainTime);
                            MessageEnum.raceTimeLimitAlert.sendConvertedMessage(p, new Object[] { getInstance(), (int) 60 });
                        }
                    } else if (remainTime == 30) {
                        for (Player p : getEntryPlayer()) {
                            playCountDownSound(p, remainTime);
                            MessageEnum.raceTimeLimitAlert.sendConvertedMessage(p, new Object[] { getInstance(), (int) 30 });
                        }
                    } else if (remainTime == 10) {
                        for (Player p : getEntryPlayer()) {
                            playCountDownSound(p, remainTime);
                            MessageEnum.raceTimeLimitAlert.sendConvertedMessage(p, new Object[] { getInstance(), (int) 10 });
                        }
                    } else if (0 < remainTime && remainTime < 10) {
                        for (Player p : getEntryPlayer()) {
                            playCountDownSound(p, remainTime);
                            MessageEnum.raceTimeLimitCountDown.sendConvertedMessage(p, new Object[] { getInstance(), remainTime });
                        }
                    } else if (remainTime == 0) {
                        for (Player p : getEntryPlayer()) {
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 2.0F, 1.0F);
                            MessageEnum.raceTimeUp.sendConvertedMessage(p, new Object[] { getInstance() });
                        }
                    } else if (remainTime < 0) {
                        endRace();
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
                    endRace();
                }
            }
        }, 10, 100));
    }

    //〓 Get/Set 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return レーススタートからの経過時間を、チック→ミリ秒に変換し返す */
    public int getLapMilliSecond() {
        return getCurrentTime() * 50;
    }

    public List<Player> getEntryPlayer() {
        List<Player> entry = new ArrayList<Player>();
        for (UUID id : this.getEntryPlayerList()) {
            if (Bukkit.getPlayer(id) != null)
                entry.add(Bukkit.getPlayer(id));
        }
        return entry;
    }

    public List<UUID> getEntryPlayerID() {
        List<UUID> entry = new ArrayList<UUID>();
        for (UUID id : this.getEntryPlayerList()) {
            if (Bukkit.getPlayer(id) != null)
                entry.add(id);
        }
        return entry;
    }

    public boolean isFillPlayer() {
        if (CircuitConfig.getCircuitData(this.getCircuitName()).getMaxPlayer() <= this.getEntryPlayerList().size()) {
            return true;
        }
        return false;
    }

    /** @return 開催されているレースがあるかどうか */
    public boolean isRaceEnd() {
        Iterator<UUID> i = getEntryPlayerID().iterator();
        UUID id;
        if (isStarted()) {
            while (i.hasNext()) {
                id = i.next();
                if (RaceManager.isEntry(id) && !RaceManager.getRace(id).isGoal())
                    return false;
            }
        } else {
            while (i.hasNext()) {
                id = i.next();
                if (RaceManager.isEntry(id))
                    return false;
            }
        }

        return true;
    }

    public boolean isJammerEntity(Entity entity) {
        return getJammerEntityList().contains(entity);
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * レース参加者にメッセージを送信する
     * @param message MessageEnum
     * @param object タグ変換する変数
     */
    public void sendMessageEntryPlayer(MessageEnum message, Object... object) {
        for (Player p : getEntryPlayer()) {
            message.sendConvertedMessage(p, object);
        }
    }

    /** レース参加者にレースへ招待するメッセージを送信する */
    public void sendRaceInviteMessage() {
        String tellraw = " [\"\",{\"text\":\"========\",\"color\":\"gray\",\"bold\":\"true\"},{\"text\":\"[参加する]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ka circuit accept\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"レースへの参加を承認します\n\",\"color\":\"yellow\"},{\"text\":\"承認した参加者が規定人数を満たせばレースが開始されます\",\"color\":\"yellow\"}]}},\"bold\":\"false\"},{\"text\":\"====\",\"color\":\"gray\",\"bold\":\"true\"},{\"text\":\"[辞退する]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ka circuit deny\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"レースの参加を辞退し、エントリーを取り消します\",\"color\":\"yellow\"}]}},\"bold\":\"false\"},{\"text\":\"========\",\"color\":\"gray\",\"bold\":\"true\"}]";

        for (UUID uuid : getEntryPlayerList()) {
            Player player = Bukkit.getPlayer(uuid);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            MessageEnum.raceReady.sendConvertedMessage(player, getInstance());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + tellraw);
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

    /** @return サーキット名 */
    public String getCircuitName() {
        return this.circuitName;
    }

    /** @return サーキットに設置された妨害アイテムエンティティリスト */
    public List<Entity> getJammerEntityList() {
        return this.jammerEntityList;
    }

    /** @return エントリーしているプレイヤーのUUIDリスト */
    public List<UUID> getEntryPlayerList() {
        return this.entryPlayerList;
    }

    /** @return リザーブエントリーしているプレイヤーのUUIDリスト */
    public List<UUID> getReserveEntryPlayerList() {
        return this.reserveEntryPlayerList;
    }

    /** @return レース参加の招待を承認したプレイヤーのUUIDリスト */
    public List<UUID> getMatchingAcceptPlayerList() {
        return this.matchingAcceptPlayerList;
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

    /** @return マッチングタスクが起動されているかどうか */
    public boolean isMatching() {
        return this.isMatching;
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

    /** @return レースが開始されているかどうか */
    public boolean isStarted() {
        return this.isStarted;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param circuitName サーキット名 */
    public void setCircuitName(String circuitName) {
        this.circuitName = circuitName;
    }

    /** @param jammerEntityList サーキットに設置された妨害アイテムエンティティリスト */
    public void setJammerEntityList(List<Entity> jammerEntityList) {
        this.jammerEntityList = jammerEntityList;
    }

    /** @param entryPlayerList エントリーしているプレイヤーのUUIDリスト */
    public void setEntryPlayerList(List<UUID> entryPlayerList) {
        this.entryPlayerList = entryPlayerList;
    }

    /** @param reserveEntryPlayerList リザーブエントリーしているプレイヤーのUUIDリスト */
    public void setReserveEntryPlayerList(List<UUID> reserveEntryPlayerList) {
        this.reserveEntryPlayerList = reserveEntryPlayerList;
    }

    /** @param matchingAcceptPlayerList レース参加の招待を承認したプレイヤーのUUIDリスト */
    public void setMatchingAcceptPlayerList(List<UUID> matchingAcceptPlayerList) {
        this.matchingAcceptPlayerList = matchingAcceptPlayerList;
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

    /** @param isMatching マッチングタスクが起動されているかどうか */
    public void setMatching(boolean isMatching) {
        this.isMatching = isMatching;
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

    /** @param value レースが開始されているかどうか */
    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }
}
