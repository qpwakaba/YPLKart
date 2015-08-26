package com.github.erozabesu.yplkart.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.utils.Util;

/**
 * Circuitの各設定、レース記録を格納するオブジェクトクラス
 * @author erozabesu
 */
public class CircuitData {

    /** サーキット名 */
    private String circuitDataName;

    /** ワールド名 */
    private String worldName;

    /** x座標 */
    private double locationX;

    /** y座標 */
    private double locationY;

    /** z座標 */
    private double locationZ;

    /** Yaw */
    private float locationYaw;

    /** Pitch */
    private float locationPitch;

    /** 周回数 */
    private int numberOfLaps;

    /** 最小プレイ人数 */
    private int minPlayer;

    /** 最大プレイ人数 */
    private int maxPlayer;

    /** マッチング猶予時間 */
    private int matchingTime;

    /** メニュー選択猶予時間 */
    private int menuTime;

    /** レース終了までのタイムリミット */
    private int limitTime;

    /** ゴールメッセージをサーバー全体に送信するかどうか */
    private boolean broadcastGoalMessage;

    /** 開催するレースタイプ */
    private RaceType raceType;

    /** 参加者のランニングレースラップ記録 */
    private List<LapTime> runLapTime = new ArrayList<LapTime>();

    /** 参加者のカートレースラップ記録 */
    private List<LapTime> kartLapTime = new ArrayList<LapTime>();

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * 設定をメンバ変数へ格納する
     * @param circuitDataName サーキット名
     */
    public CircuitData(String circuitDataName) {
        this.setCircuitDataName(circuitDataName);

        ConfigManager configManager = ConfigManager.RACEDATA_CONFIG;

        this.setWorldName(configManager.getString(circuitDataName + ".world"));
        this.setLocationX(configManager.getDouble(circuitDataName + ".x"));
        this.setLocationY(configManager.getDouble(circuitDataName + ".y"));
        this.setLocationZ(configManager.getDouble(circuitDataName + ".z"));
        this.setLocationPitch(configManager.getFloat(circuitDataName + ".pitch"));
        this.setLocationYaw(configManager.getFloat(circuitDataName + ".yaw"));

        this.setNumberOfLaps(configManager.getInteger(circuitDataName + ".numberoflaps"));
        this.setMinPlayer(configManager.getInteger(circuitDataName + ".minplayer"));
        this.setMaxPlayer(configManager.getInteger(circuitDataName + ".maxplayer"));
        this.setMatchingTime(configManager.getInteger(circuitDataName + ".matchingtime"));
        this.setMenuTime(configManager.getInteger(circuitDataName + ".menutime"));
        this.setLimitTime(configManager.getInteger(circuitDataName + ".limittime"));
        this.setBroadcastGoalMessage(configManager.getBoolean(circuitDataName + ".broadcastgoalmessage"));
        this.setRaceType(RaceType.getRaceTypeByString(configManager.getString(circuitDataName + ".race_type")));
        this.setRunLapTimeList(this.getLapTimeFromConfiguration(true));
        this.setKartLapTimeList(this.getLapTimeFromConfiguration(false));
    }

    /** メンバ変数がnullの場合初期値を格納する */
    public void init(Location location) {
        this.setWorldName(location.getWorld().getName());
        this.setLocationX(location.getX());
        this.setLocationY(location.getY());
        this.setLocationZ(location.getZ());
        this.setLocationPitch(location.getPitch());
        this.setLocationYaw(location.getYaw());

        if (this.getNumberOfLaps() == 0) {
            this.setNumberOfLaps(3);
        }
        if (this.getMinPlayer() == 0) {
            this.setMinPlayer(3);
        }
        if (this.getMaxPlayer() == 0) {
            this.setMaxPlayer(10);
        }
        if (this.getMatchingTime() == 0) {
            this.setMatchingTime(30);
        }
        if (this.getMenuTime() == 0) {
            this.setMenuTime(30);
        }
        if (this.getLimitTime() == 0) {
            this.setLimitTime(300);
        }
        if (this.getBroadcastGoalMessage() == false) {
            this.setBroadcastGoalMessage(false);
        }
        if (this.getRaceType() == null) {
            this.setRaceType(RaceType.KART);
        }
    }

    //〓 List/Map 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数uuidと一致するUUIDキーを持つLapTimeオブジェクトを返す
     * @param uuid LapTimeオブジェクトを取得するプレイヤーのUUID
     * @return LapTimeオブジェクト
     */
    public LapTime getRunLapTime(UUID uuid) {
        LapTime lapTimeObject = null;
        for (LapTime lapTime : this.getRunLapTimeList()) {
            if (lapTime.getUuid().equals(uuid)) {
                lapTimeObject = lapTime;
            }
        }
        return lapTimeObject;
    }

    /**
     * 引数uuidと一致するUUIDキーを持つLapTimeオブジェクトを返す
     * @param uuid LapTimeオブジェクトを取得するプレイヤーのUUID
     * @return LapTimeオブジェクト
     */
    public LapTime getKartLapTime(UUID uuid) {
        LapTime lapTimeObject = null;
        for (LapTime lapTime : this.getKartLapTimeList()) {
            if (lapTime.getUuid().equals(uuid)) {
                lapTimeObject = lapTime;
            }
        }
        return lapTimeObject;
    }

    /** @param runLapTime 追加する参加者のランニングレースラップ記録 */
    public void addRunLapTimeList(LapTime runLapTime) {
        this.getRunLapTimeList().add(runLapTime);
    }

    /** @param kartLapTime 追加する参加者のカートレースラップ記録 */
    public void addKartLapTimeList(LapTime kartLapTime) {
        this.getKartLapTimeList().add(kartLapTime);
    }

    /**
     * ランニングレースラップ記録からプレイヤーの記録を削除する
     * @param uuid 記録を削除するプレイヤーのUUID
     */
    public void removeRunLapTime(UUID uuid) {
        Iterator<LapTime> iterator = this.getRunLapTimeList().iterator();
        LapTime lapTimeObject = null;
        while (iterator.hasNext()) {
            lapTimeObject = iterator.next();
            if (lapTimeObject.getUuid().equals(uuid)) {
                iterator.remove();
                return;
            }
        }
    }

    /**
     * カートレースラップ記録からプレイヤーの記録を削除する
     * @param uuid 記録を削除するプレイヤーのUUID
     */
    public void removeKartLapTime(UUID uuid) {
        Iterator<LapTime> iterator = this.getKartLapTimeList().iterator();
        LapTime lapTimeObject = null;
        while (iterator.hasNext()) {
            lapTimeObject = iterator.next();
            if (lapTimeObject.getUuid().equals(uuid)) {
                iterator.remove();
                return;
            }
        }
    }

    //〓 File 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * メンバ変数の設定データをローカルコンフィグに新しい値として上書きし
     * ローカルファイルに保存する
     * ゲーム内から動的に変更される設定データを扱うため用意されている
     * コマンド等で設定データが変更された場合逐一上書きすること
     */
    public void saveConfiguration() {
        String configKey = getCircuitDataName();
        ConfigManager config = ConfigManager.RACEDATA_CONFIG;

        //設定データに値を上書き
        config.setValue(configKey + ".world", getWorldName());
        config.setValue(configKey + ".x", getLocationX());
        config.setValue(configKey + ".y", getLocationY());
        config.setValue(configKey + ".z", getLocationZ());
        config.setValue(configKey + ".pitch", getLocationPitch());
        config.setValue(configKey + ".yaw", getLocationYaw());

        config.setValue(configKey + ".numberoflaps", getNumberOfLaps());
        config.setValue(configKey + ".minplayer", getMinPlayer());
        config.setValue(configKey + ".maxplayer", getMaxPlayer());
        config.setValue(configKey + ".matchingtime", getMatchingTime());
        config.setValue(configKey + ".menutime", getMenuTime());
        config.setValue(configKey + ".limittime", getLimitTime());
        config.setValue(configKey + ".broadcastgoalmessage", getBroadcastGoalMessage());
        config.setValue(configKey + ".race_type", this.getRaceType().name());

        //ランニングレースラップタイムを設定データに上書き
        for (LapTime lapTimeObject : getRunLapTimeList()) {
            int numberOfLaps = lapTimeObject.getNumberOfLaps();
            String uuid = lapTimeObject.getUuid().toString();
            double lapTime = lapTimeObject.getLapTime();

            config.setValue(configKey + ".laptime." + numberOfLaps + "." + uuid, lapTime);
        }

        //カートレースラップタイムを設定データに上書き
        for (LapTime lapTimeObject : this.getKartLapTimeList()) {
            int numberOfLaps = lapTimeObject.getNumberOfLaps();
            String uuid = lapTimeObject.getUuid().toString();
            double lapTime = lapTimeObject.getLapTime();

            config.setValue(configKey + ".kartlaptime." + numberOfLaps + "." + uuid, lapTime);
        }

        //設定データをローカルファイルに保存
        config.saveConfiguration();
    }

    /** ローカルコンフィグファイルから全データを削除する */
    public void deleteConfiguration() {
        ConfigManager config = ConfigManager.RACEDATA_CONFIG;

        config.setValue(getCircuitDataName(), null);

        //設定データをローカルファイルに保存
        config.saveConfiguration();
    }

    /** 全く同様のメンバ変数を持つオブジェクトを新規に生成し返す */
    public CircuitData cloneCircuitData() {

        //ローカルファイルへ未保存のメンバ変数がある可能性があるため保存しておく
        this.saveConfiguration();

        //新たに生成したオブジェクトを返す
        //コンストラクタから、サーキット名がキーのローカル設定データを読み込むため
        //メンバ変数の代入等は不要
        return new CircuitData(this.getCircuitDataName());
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param location セットするレースの開始座標 */
    public void setStartLocation(Location location) {
        this.setWorldName(location.getWorld().getName());
        this.setLocationX(location.getX());
        this.setLocationY(location.getY());
        this.setLocationZ(location.getZ());
        this.setLocationPitch(location.getPitch());
        this.setLocationYaw(location.getYaw());
    }

    /** @return レースの開始座標 */
    public Location getStartLocation() {
        World world = Bukkit.getWorld(getWorldName());

        if (world == null) {
            return null;
        }

        return new Location(world
                , this.getLocationX(), this.getLocationY(), this.getLocationZ()
                , this.getLocationYaw(), this.getLocationPitch());
    }

    /**
     * レース開始座標を基準に、参加者を整列させる座標Listを返す
     * @return レース開始座標List
     */
    public List<Location> getStartLocationList() {
        Location location = this.getStartLocation();
        if (location == null) {
            return null;
        }

        List<Location> list = new ArrayList<Location>();
        while (list.size() < getMaxPlayer()) {
            if (!Util.isSolidBlock(location)) {
                list.add(location);
            }
            if (!Util.isSolidBlock(Util.getSideLocationFromYaw(location, 4))) {
                list.add(Util.getSideLocationFromYaw(location, 4));
            }
            if (!Util.isSolidBlock(Util.getSideLocationFromYaw(location, -4))) {
                list.add(Util.getSideLocationFromYaw(location, -4));
            }

            location = Util.getForwardLocationFromYaw(location, -4);
        }

        return list;
    }

    /**
     * メンバ変数の設定データ一覧をログ出力する
     * @param address ログの送信先
     */
    public void sendInformation(CommandSender address) {
        Location l = this.getStartLocation();
        Number[] numberdata = {
                this.getNumberOfLaps(), this.getMinPlayer(), this.getMaxPlayer()
                , this.getLimitTime(), this.getMenuTime(), this.getMatchingTime()
                , l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch() };

        Circuit circuit = new Circuit();
        circuit.setCircuitName(this.getCircuitDataName());
        MessageEnum.tableCircuitInformation
                .sendConvertedMessage(address, circuit, this.getRaceType(), numberdata, this.getBroadcastGoalMessage());
    }

    public void sendRanking(CommandSender address) {

        //ランキングデータがない
        if (this.getRunLapTimeList().size() == 0 && this.getKartLapTimeList().size() == 0) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(this.getCircuitDataName());
            MessageEnum.cmdCircuitRankingNoScoreData.sendConvertedMessage(address, circuit);
            return;
        }

        //ランニングレース
        if (getRunLapTimeList().size() == 0) {

            // Do nothing

        } else {
            //LapTimeオブジェクトを周回数毎にハッシュマップに格納
            HashMap<Integer, List<LapTime>> lapTimeKeySet = new HashMap<Integer, List<LapTime>>();

            for (LapTime lapTimeObject : this.getRunLapTimeList()) {
                int numberOfLaps = lapTimeObject.getNumberOfLaps();

                if (lapTimeKeySet.get(numberOfLaps) == null) {
                    lapTimeKeySet.put(numberOfLaps, new ArrayList<LapTime>());
                }

                lapTimeKeySet.get(numberOfLaps).add(lapTimeObject);
            }

            //LapTimeオブジェクトの配列をラップタイムに関して昇順ソート
            for (int numberOfLaps : lapTimeKeySet.keySet()) {
                Collections.sort(lapTimeKeySet.get(numberOfLaps), new Comparator<LapTime>() {
                    @Override
                    public int compare(LapTime lapTime1, LapTime lapTime2) {
                        return lapTime1.compareTo(lapTime2);
                    }
                });
            }

            //配列上位10位までのLapTimeオブジェクトのメンバ変数を文字列に書き出し
            for (int numberOfLaps : lapTimeKeySet.keySet()) {

                //ランキング参照者の記録
                int ownRank = 0;
                double ownLapSecond = 0;

                List<LapTime> lapTimeObjectList = lapTimeKeySet.get(numberOfLaps);
                String ranking = "";

                //ヘッダー
                ranking += "<darkaqua>====== " + "<aqua>" + getCircuitDataName().toUpperCase()
                        + "<darkaqua> Running Race Ranking" + " - <aqua>" + numberOfLaps
                        + " <darkaqua>Laps" + " <darkaqua>======<br>";

                for (int rank = 1; rank <= lapTimeObjectList.size(); rank++) {
                    LapTime lapTimeObject = lapTimeObjectList.get(rank - 1);
                    String playerName = Bukkit.getOfflinePlayer(lapTimeObject.getUuid()).getName();

                    //配列上位10位以内
                    if (rank <= 10) {
                        ranking += "   <yellow>" + rank + ". <white>" + playerName + " : "
                                + "<yellow>" + lapTimeObject.getLapTime() + " sec<br>";
                    }

                    //ランキング参照者がプレイヤーの場合、参照者自身のデータを格納する
                    if (address instanceof Player) {
                        if (lapTimeObject.getUuid().equals(((Player) address).getUniqueId())) {
                            ownRank = rank;
                            ownLapSecond = lapTimeObject.getLapTime();
                        }
                    }
                }

                //ランキング参照者のデータを別枠で書き出す
                if (address instanceof Player) {
                    String playerName = ((Player) address).getName();

                    if (ownRank != 0 && ownLapSecond != 0) {
                        ranking += "<white>" + playerName + "<green>さんの順位は<yellow>" + ownRank
                                + "位 : " + ownLapSecond + " sec" + "<green>です<br>";
                    } else {
                        ranking += "<white>" + playerName + "<green>さんのデータは存在しません<br>";
                    }
                }

                MessageEnum.sendAbsolute(address, MessageEnum.replaceChatColor(ranking));
            }
        }

        //カートレース
        if (getKartLapTimeList().size() == 0) {

            // Do nothing

        } else {
            //LapTimeオブジェクトを周回数毎にハッシュマップに格納
            HashMap<Integer, List<LapTime>> lapTimeKeySet = new HashMap<Integer, List<LapTime>>();

            for (LapTime lapTimeObject : this.getKartLapTimeList()) {
                int numberOfLaps = lapTimeObject.getNumberOfLaps();

                if (lapTimeKeySet.get(numberOfLaps) == null) {
                    lapTimeKeySet.put(numberOfLaps, new ArrayList<LapTime>());
                }

                lapTimeKeySet.get(numberOfLaps).add(lapTimeObject);
            }

            //LapTimeオブジェクトの配列をラップタイムに関して昇順ソート
            for (int numberOfLaps : lapTimeKeySet.keySet()) {
                Collections.sort(lapTimeKeySet.get(numberOfLaps), new Comparator<LapTime>() {
                    @Override
                    public int compare(LapTime lapTime1, LapTime lapTime2) {
                        return lapTime1.compareTo(lapTime2);
                    }
                });
            }

            //配列上位10位までのLapTimeオブジェクトのメンバ変数を文字列に書き出し
            for (int numberOfLaps : lapTimeKeySet.keySet()) {

                //ランキング参照者の記録
                int ownRank = 0;
                double ownLapSecond = 0;

                List<LapTime> lapTimeObjectList = lapTimeKeySet.get(numberOfLaps);
                String ranking = "";

                //ヘッダー
                ranking += "<darkaqua>====== " + "<aqua>" + getCircuitDataName().toUpperCase()
                        + "<darkaqua> Kart Race Ranking" + " - <aqua>" + numberOfLaps
                        + " <darkaqua>Laps" + " <darkaqua>======<br>";

                for (int rank = 1; rank <= lapTimeObjectList.size(); rank++) {
                    LapTime lapTimeObject = lapTimeObjectList.get(rank - 1);
                    String playerName = Bukkit.getOfflinePlayer(lapTimeObject.getUuid()).getName();

                    //配列上位10位以内
                    if (rank <= 10) {
                        ranking += "   <yellow>" + rank + ". <white>" + playerName + " : "
                                + "<yellow>" + lapTimeObject.getLapTime() + " sec<br>";
                    }

                    //ランキング参照者がプレイヤーの場合、参照者自身のデータを格納する
                    if (address instanceof Player) {
                        if (lapTimeObject.getUuid().equals(((Player) address).getUniqueId())) {
                            ownRank = rank;
                            ownLapSecond = lapTimeObject.getLapTime();
                        }
                    }
                }
                //ランキング参照者のデータを別枠で書き出す
                if (address instanceof Player) {
                    String playerName = ((Player) address).getName();

                    if (ownRank != 0 && ownLapSecond != 0) {
                        ranking += "<white>" + playerName + "<green>さんの順位は<yellow>" + ownRank
                                + "位 : " + ownLapSecond + " sec" + "<green>です<br>";
                    } else {
                        ranking += "<white>" + playerName + "<green>さんのデータは存在しません<br>";
                    }
                }

                MessageEnum.sendAbsolute(address, MessageEnum.replaceChatColor(ranking));
            }
        }
    }

    /**
     * ローカルコンフィグのランニングラップタイムデータを取得する
     * 取得が冗長になるため専用のメソッドを用意している
     * データのフォーマットは下記のようなネスト構造になっている
     * <Circuit name>:
     *   laptime:
     *     <Number of Laps>:
     *       <Player UUID> : 123.45
     *       ...
     *     <Number of Laps>:
     *       <Player UUID> : 123.21
     *       ...
     *   kartlaptime:
     *     <Number of Laps>:
     *       <Player UUID> : 123.45
     *       ...
     *     <Number of Laps>:
     *       <Player UUID> : 123.21
     *       ...
     * @param isRunningRace ランニングレースかどうか
     * @return LapTimeオブジェクトList
     */
    private List<LapTime> getLapTimeFromConfiguration(boolean isRunningRace) {
        List<LapTime> lapTimeMap = new ArrayList<LapTime>();

        //レースタイプ
        String raceTypeKey = isRunningRace ? "laptime" : "kartlaptime";

        //設定データを取得
        ConfigManager manager = ConfigManager.RACEDATA_CONFIG;
        FileConfiguration config = manager.getLocalConfig();

        //<Circuit name>.<Race Type>.<Number of Laps>のセクションを取得
        String lapKey = this.getCircuitDataName() + "." + raceTypeKey;
        ConfigurationSection lapSection = config.getConfigurationSection(lapKey);

        //存在しないセクションであれば空のListを返す
        if (lapSection == null) {
            return lapTimeMap;
        }

        //セクションのキーからLapTimeオブジェクトを生成
        for (String lapValue : lapSection.getKeys(false)) {
            //<Player UUID>
            String uuidKey = lapKey + "." + lapValue;
            ConfigurationSection uuidSection = config.getConfigurationSection(uuidKey);
            for (String uuidValue : uuidSection.getKeys(false)) {

                double lapTime = manager.getDouble(uuidKey + "." + uuidValue);
                LapTime lapTimeObject =
                        new LapTime(Integer.valueOf(lapValue), UUID.fromString(uuidValue), lapTime);
                lapTimeMap.add(lapTimeObject);
            }
        }

        return lapTimeMap;
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return configKey サーキット名 */
    public String getCircuitDataName() {
        return this.circuitDataName;
    }

    /** @return ワールド名 */
    public String getWorldName() {
        return this.worldName;
    }

    /** @return locationX x座標 */
    public double getLocationX() {
        return this.locationX;
    }

    /** @return locationY y座標 */
    public double getLocationY() {
        return this.locationY;
    }

    /** @return locationZ z座標 */
    public double getLocationZ() {
        return this.locationZ;
    }

    /** @return locationYaw Yaw */
    public float getLocationYaw() {
        return this.locationYaw;
    }

    /** @return locationPitch Pitch */
    public float getLocationPitch() {
        return this.locationPitch;
    }

    /** @return numberOfLaps 周回数 */
    public int getNumberOfLaps() {
        return this.numberOfLaps;
    }

    /** @return minPlayer 最小プレイ人数 */
    public int getMinPlayer() {
        return this.minPlayer;
    }

    /** @return maxPlayer 最大プレイ人数 */
    public int getMaxPlayer() {
        return this.maxPlayer;
    }

    /** @return matchingTime マッチング猶予時間 */
    public int getMatchingTime() {
        return this.matchingTime;
    }

    /** @return menuTime メニュー選択猶予時間 */
    public int getMenuTime() {
        return this.menuTime;
    }

    /** @return limitTime レース終了までのタイムリミット */
    public int getLimitTime() {
        return this.limitTime;
    }

    /** @return broadcastGoalMessage ゴールメッセージをサーバー全体に送信するかどうか */
    public boolean getBroadcastGoalMessage() {
        return this.broadcastGoalMessage;
    }

    /** @return 開催するレースタイプ */
    public RaceType getRaceType() {
        return raceType;
    }

    /** @return runLapTime 参加者のランニングレースラップ記録 */
    public List<LapTime> getRunLapTimeList() {
        return this.runLapTime;
    }

    /** @return kartLapTime 参加者のカートレースラップ記録 */
    public List<LapTime> getKartLapTimeList() {
        return this.kartLapTime;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param configKey サーキット名 */
    public void setCircuitDataName(String circuitDataName) {
        this.circuitDataName = circuitDataName;
    }

    /** @param worldName ワールド名 */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /** @param locationX x座標 */
    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    /** @param locationY y座標 */
    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    /** @param locationZ z座標 */
    public void setLocationZ(double locationZ) {
        this.locationZ = locationZ;
    }

    /** @param locationYaw Yaw */
    public void setLocationYaw(float locationYaw) {
        this.locationYaw = locationYaw;
    }

    /** @param locationPitch Pitch */
    public void setLocationPitch(float locationPitch) {
        this.locationPitch = locationPitch;
    }

    /** @param numberOfLaps 周回数 */
    public void setNumberOfLaps(int numberOfLaps) {
        this.numberOfLaps = numberOfLaps;
    }

    /** @param minPlayer 最小プレイ人数 */
    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
    }

    /** @param maxPlayer 最大プレイ人数 */
    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    /** @param matchingTime マッチング猶予時間 */
    public void setMatchingTime(int matchingTime) {
        this.matchingTime = matchingTime;
    }

    /** @param menuTime メニュー選択猶予時間 */
    public void setMenuTime(int menuTime) {
        this.menuTime = menuTime;
    }

    /** @param limitTime レース終了までのタイムリミット */
    public void setLimitTime(int limitTime) {
        this.limitTime = limitTime;
    }

    /** @param broadcastGoalMessage ゴールメッセージをサーバー全体に送信するかどうか */
    public void setBroadcastGoalMessage(boolean broadcastGoalMessage) {
        this.broadcastGoalMessage = broadcastGoalMessage;
    }

    /** @param raceType 開催するレースタイプ */
    public void setRaceType(RaceType raceType) {
        this.raceType = raceType;
    }

    /** @param runLapTimeList 参加者のランニングレースラップ記録 */
    public void setRunLapTimeList(List<LapTime> runLapTimeList) {
        this.runLapTime = runLapTimeList;
    }

    /** @param kartLapTimeList 参加者のカートレースラップ記録 */
    public void setKartLapTimeList(List<LapTime> kartLapTimeList) {
        this.kartLapTime = kartLapTimeList;
    }
}
