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
        setCircuitDataName(circuitDataName);

        ConfigManager configManager = ConfigManager.RACEDATA_CONFIG;

        setWorldName((String) configManager.getString(circuitDataName + ".world"));
        setLocationX((Double) configManager.getDouble(circuitDataName + ".x"));
        setLocationY((Double) configManager.getDouble(circuitDataName + ".y"));
        setLocationZ((Double) configManager.getDouble(circuitDataName + ".z"));
        setLocationPitch((Float) configManager.getFloat(circuitDataName + ".pitch"));
        setLocationYaw((Float) configManager.getFloat(circuitDataName + ".yaw"));

        setNumberOfLaps((Integer) configManager.getInteger(circuitDataName + ".numberoflaps"));
        setMinPlayer((Integer) configManager.getInteger(circuitDataName + ".minplayer"));
        setMaxPlayer((Integer) configManager.getInteger(circuitDataName + ".maxplayer"));
        setMatchingTime((Integer) configManager.getInteger(circuitDataName + ".matchingtime"));
        setMenuTime((Integer) configManager.getInteger(circuitDataName + ".menutime"));
        setLimitTime((Integer) configManager.getInteger(circuitDataName + ".limittime"));
        setBroadcastGoalMessage(configManager.getBoolean(circuitDataName + ".broadcastgoalmessage"));
        setRunLapTimeList(getLapTimeFromConfiguration(true));
        setKartLapTimeList(getLapTimeFromConfiguration(false));
    }

    /** メンバ変数がnullの場合初期値を格納する */
    public void init(Location location) {
        if (getWorldName() == null) {
            setWorldName(location.getWorld().getName());
        }
        if (getLocationX() == 0) {
            setLocationX(location.getX());
        }
        if (getLocationY() == 0) {
            setLocationY(location.getY());
        }
        if (getLocationZ() == 0) {
            setLocationZ(location.getZ());
        }
        if (getLocationPitch() == 0) {
            setLocationPitch(location.getPitch());
        }
        if (getLocationYaw() == 0) {
            setLocationYaw(location.getYaw());
        }

        if (getNumberOfLaps() == 0) {
            setNumberOfLaps(3);
        }
        if (getMinPlayer() == 0) {
            setMinPlayer(3);
        }
        if (getMaxPlayer() == 0) {
            setMaxPlayer(10);
        }
        if (getMatchingTime() == 0) {
            setMatchingTime(30);
        }
        if (getMenuTime() == 0) {
            setMenuTime(30);
        }
        if (getLimitTime() == 0) {
            setLimitTime(300);
        }
        if (getBroadcastGoalMessage() == false) {
            setBroadcastGoalMessage(false);
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
        for (LapTime lapTime : getRunLapTimeList()) {
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
        for (LapTime lapTime : getKartLapTimeList()) {
            if (lapTime.getUuid().equals(uuid)) {
                lapTimeObject = lapTime;
            }
        }
        return lapTimeObject;
    }

    /** @param runLapTime 追加する参加者のランニングレースラップ記録 */
    public void addRunLapTimeList(LapTime runLapTime) {
        getRunLapTimeList().add(runLapTime);
    }

    /** @param kartLapTime 追加する参加者のカートレースラップ記録 */
    public void addKartLapTimeList(LapTime kartLapTime) {
        getKartLapTimeList().add(kartLapTime);
    }

    /**
     * ランニングレースラップ記録からプレイヤーの記録を削除する
     * @param uuid 記録を削除するプレイヤーのUUID
     */
    public void removeRunLapTime(UUID uuid) {
        Iterator<LapTime> iterator = getRunLapTimeList().iterator();
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
        Iterator<LapTime> iterator = getKartLapTimeList().iterator();
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

        //ランニングレースラップタイムを設定データに上書き
        for(LapTime lapTimeObject : getRunLapTimeList()) {
            int numberOfLaps = lapTimeObject.getNumberOfLaps();
            String uuid = lapTimeObject.getUuid().toString();
            double lapTime = lapTimeObject.getLapTime();

            config.setValue(configKey + ".laptime." + numberOfLaps + "." + uuid, lapTime);
        }

        //カートレースラップタイムを設定データに上書き
        for(LapTime lapTimeObject : getKartLapTimeList()) {
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
    public CircuitData clone() {

        //ローカルファイルへ未保存のメンバ変数がある可能性があるため保存しておく
        saveConfiguration();

        //新たに生成したオブジェクトを返す
        //コンストラクタから、サーキット名がキーのローカル設定データを読み込むため
        //メンバ変数の代入等は不要
        return new CircuitData(getCircuitDataName());
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param location セットするレースの開始座標 */
    public void setStartLocation(Location location) {
        setWorldName(location.getWorld().getName());
        setLocationX(location.getX());
        setLocationY(location.getY());
        setLocationZ(location.getZ());
        setLocationPitch(location.getPitch());
        setLocationYaw(location.getYaw());
    }

    /** @return レースの開始座標 */
    public Location getStartLocation() {
        World world = Bukkit.getWorld(getWorldName());

        if (world == null) {
            return null;
        }

        return new Location(world
                , getLocationX(), getLocationY(), getLocationZ()
                , getLocationYaw(), getLocationPitch());
    }

    /**
     * レース開始座標を基準に、参加者を整列させる座標Listを返す
     * @return レース開始座標List
     */
    public List<Location> getStartLocationList() {
        Location location = getStartLocation();
        if (location == null)
            return null;

        List<Location> list = new ArrayList<Location>();
        while (list.size() < getMaxPlayer()) {
            if (!Util.isSolidBlock(location))
                list.add(location);
            if (!Util.isSolidBlock(Util.getSideLocationFromYaw(location, 4)))
                list.add(Util.getSideLocationFromYaw(location, 4));
            if (!Util.isSolidBlock(Util.getSideLocationFromYaw(location, -4)))
                list.add(Util.getSideLocationFromYaw(location, -4));

            location = Util.getForwardLocationFromYaw(location, -4);
        }

        return list;
    }

    /**
     * メンバ変数の設定データ一覧をログ出力する
     * @param adress ログの送信先
     */
    public void sendInformation(Object adress) {
        Location l = getStartLocation();
        boolean flag = getBroadcastGoalMessage();
        Number[] numberdata = {
                getNumberOfLaps(), getMinPlayer(), getMaxPlayer()
                , getLimitTime(), getMenuTime(), getMatchingTime()
                , l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch() };

        MessageEnum.tableCircuitInformation
            .sendConvertedMessage(adress, new Object[] { getCircuitDataName(), flag, numberdata });
    }

    public void sendRanking(Object adress) {

        //ランキングデータがない
        if (getRunLapTimeList().size() == 0 && getKartLapTimeList().size() == 0) {
            Circuit circuit = new Circuit();
            circuit.setCircuitName(this.getCircuitDataName());
            MessageEnum.cmdCircuitRankingNoScoreData.sendConvertedMessage(adress, circuit);
            return;
        }

        //ランニングレース
        if (getRunLapTimeList().size() == 0) {
            // Do nothing
        } else {
            //LapTimeオブジェクトを周回数毎にハッシュマップに格納
            HashMap<Integer, List<LapTime>> lapTimeKeySet = new HashMap<Integer, List<LapTime>>();

            for (LapTime lapTimeObject : getRunLapTimeList()) {
                int numberOfLaps = lapTimeObject.getNumberOfLaps();

                if (lapTimeKeySet.get(numberOfLaps) == null) {
                    lapTimeKeySet.put(numberOfLaps, new ArrayList<LapTime>());
                }

                lapTimeKeySet.get(numberOfLaps).add(lapTimeObject);
            }

            //LapTimeオブジェクトの配列をラップタイムに関して昇順ソート
            for (int numberOfLaps : lapTimeKeySet.keySet()) {
                Collections.sort(lapTimeKeySet.get(numberOfLaps), new Comparator<LapTime>(){
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
                    LapTime lapTimeObject = lapTimeObjectList.get(rank-1);
                    String playerName = Bukkit.getOfflinePlayer(lapTimeObject.getUuid()).getName();

                    //配列上位10位以内
                    if (rank <= 10) {
                        ranking += "   <yellow>" + rank + ". <white>" + playerName + " : "
                                + "<yellow>" + lapTimeObject.getLapTime() + " sec<br>";
                    }

                    //ランキング参照者がプレイヤーの場合、参照者自身のデータを格納する
                    if (adress instanceof Player) {
                        if (lapTimeObject.getUuid().equals(((Player) adress).getUniqueId())) {
                            ownRank = rank;
                            ownLapSecond = lapTimeObject.getLapTime();
                        }
                    } else if (adress instanceof UUID) {
                        if (lapTimeObject.getUuid().equals(((UUID) adress))) {
                            ownRank = rank;
                            ownLapSecond = lapTimeObject.getLapTime();
                        }
                    }
                }

                //ランキング参照者のデータを別枠で書き出す
                if (adress instanceof Player || adress instanceof UUID) {
                    String playerName = adress instanceof Player
                            ? ((Player)adress).getName()
                                    : Bukkit.getOfflinePlayer((UUID) adress).getName();

                    if (ownRank != 0 && ownLapSecond != 0) {
                        ranking += "<white>" + playerName + "<green>さんの順位は<yellow>" + ownRank
                                + "位 : " + ownLapSecond + " sec" + "<green>です<br>";
                    } else {
                        ranking += "<white>" + playerName + "<green>さんのデータは存在しません<br>";
                    }
                }

                MessageEnum.sendAbsolute(adress, MessageEnum.replaceChatColor(ranking));
            }
        }

        //カートレース
        if (getKartLapTimeList().size() == 0) {
            // Do nothing
        } else {
            //LapTimeオブジェクトを周回数毎にハッシュマップに格納
            HashMap<Integer, List<LapTime>> lapTimeKeySet = new HashMap<Integer, List<LapTime>>();

            for (LapTime lapTimeObject : getKartLapTimeList()) {
                int numberOfLaps = lapTimeObject.getNumberOfLaps();

                if (lapTimeKeySet.get(numberOfLaps) == null) {
                    lapTimeKeySet.put(numberOfLaps, new ArrayList<LapTime>());
                }

                lapTimeKeySet.get(numberOfLaps).add(lapTimeObject);
            }

            //LapTimeオブジェクトの配列をラップタイムに関して昇順ソート
            for (int numberOfLaps : lapTimeKeySet.keySet()) {
                Collections.sort(lapTimeKeySet.get(numberOfLaps), new Comparator<LapTime>(){
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
                    LapTime lapTimeObject = lapTimeObjectList.get(rank-1);
                    String playerName = Bukkit.getOfflinePlayer(lapTimeObject.getUuid()).getName();

                    //配列上位10位以内
                    if (rank <= 10) {
                        ranking += "   <yellow>" + rank + ". <white>" + playerName + " : "
                                + "<yellow>" + lapTimeObject.getLapTime() + " sec<br>";
                    }

                    //ランキング参照者がプレイヤーの場合、参照者自身のデータを格納する
                    if (adress instanceof Player) {
                        if (lapTimeObject.getUuid().equals(((Player) adress).getUniqueId())) {
                            ownRank = rank;
                            ownLapSecond = lapTimeObject.getLapTime();
                        }
                    } else if (adress instanceof UUID) {
                        if (lapTimeObject.getUuid().equals(((UUID) adress))) {
                            ownRank = rank;
                            ownLapSecond = lapTimeObject.getLapTime();
                        }
                    }
                }
                //ランキング参照者のデータを別枠で書き出す
                if (adress instanceof Player || adress instanceof UUID) {
                    String playerName = adress instanceof Player
                            ? ((Player)adress).getName()
                                    : Bukkit.getOfflinePlayer((UUID) adress).getName();

                    if (ownRank != 0 && ownLapSecond != 0) {
                        ranking += "<white>" + playerName + "<green>さんの順位は<yellow>" + ownRank
                                + "位 : " + ownLapSecond + " sec" + "<green>です<br>";
                    } else {
                        ranking += "<white>" + playerName + "<green>さんのデータは存在しません<br>";
                    }
                }

                MessageEnum.sendAbsolute(adress, MessageEnum.replaceChatColor(ranking));
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
    private List<LapTime> getLapTimeFromConfiguration (boolean isRunningRace) {
        List<LapTime> lapTimeMap = new ArrayList<LapTime>();

        //レースタイプ
        String raceTypeKey = isRunningRace ? "laptime" : "kartlaptime";

        //設定データを取得
        ConfigManager manager = ConfigManager.RACEDATA_CONFIG;
        FileConfiguration config = manager.getLocalConfig();

        //<Circuit name>.<Race Type>.<Number of Laps>のセクションを取得
        String lapKey = getCircuitDataName() + "." + raceTypeKey;
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
        return circuitDataName;
    }

    /** @return ワールド名 */
    public String getWorldName() {
        return worldName;
    }

    /** @return locationX x座標 */
    public double getLocationX() {
        return locationX;
    }

    /** @return locationY y座標 */
    public double getLocationY() {
        return locationY;
    }

    /** @return locationZ z座標 */
    public double getLocationZ() {
        return locationZ;
    }

    /** @return locationYaw Yaw */
    public float getLocationYaw() {
        return locationYaw;
    }

    /** @return locationPitch Pitch */
    public float getLocationPitch() {
        return locationPitch;
    }

    /** @return numberOfLaps 周回数 */
    public int getNumberOfLaps() {
        return numberOfLaps;
    }

    /** @return minPlayer 最小プレイ人数 */
    public int getMinPlayer() {
        return minPlayer;
    }

    /** @return maxPlayer 最大プレイ人数 */
    public int getMaxPlayer() {
        return maxPlayer;
    }

    /** @return matchingTime マッチング猶予時間 */
    public int getMatchingTime() {
        return matchingTime;
    }

    /** @return menuTime メニュー選択猶予時間 */
    public int getMenuTime() {
        return menuTime;
    }

    /** @return limitTime レース終了までのタイムリミット */
    public int getLimitTime() {
        return limitTime;
    }

    /** @return broadcastGoalMessage ゴールメッセージをサーバー全体に送信するかどうか */
    public boolean getBroadcastGoalMessage() {
        return broadcastGoalMessage;
    }

    /** @return runLapTime 参加者のランニングレースラップ記録 */
    public List<LapTime> getRunLapTimeList() {
        return runLapTime;
    }

    /** @return kartLapTime 参加者のカートレースラップ記録 */
    public List<LapTime> getKartLapTimeList() {
        return kartLapTime;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param configKey サーキット名 */
    public void setCircuitDataName(String circuitDataName) {
        this.circuitDataName = (String) circuitDataName;
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

    /** @param runLapTimeList 参加者のランニングレースラップ記録 */
    public void setRunLapTimeList(List<LapTime> runLapTimeList) {
        this.runLapTime = runLapTimeList;
    }

    /** @param kartLapTimeList 参加者のカートレースラップ記録 */
    public void setKartLapTimeList(List<LapTime> kartLapTimeList) {
        this.kartLapTime = kartLapTimeList;
    }
}
