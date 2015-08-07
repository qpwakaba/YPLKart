package com.github.erozabesu.yplkart.object;

import java.util.UUID;

/**
 * ラップタイムを格納するオブジェクトクラス
 * @author erozabesu
 */
public class LapTime implements Comparable {

    /** 周回数 */
    private int numberOfLaps;

    /** UUID */
    private UUID uuid;

    /** ラップタイム */
    private double lapTime;

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * @param numberOfLaps 周回数
     * @param uuid UUID
     * @param lapTime ラップタイム
     */
    public LapTime(int numberOfLaps, UUID uuid, double lapTime) {
        setNumberOfLaps(numberOfLaps);
        setUuid(uuid);
        setLapTime(lapTime);
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return numberOfLaps 周回数 */
    public int getNumberOfLaps() {
        return numberOfLaps;
    }

    /** @return uuid UUID */
    public UUID getUuid() {
        return uuid;
    }

    /** @return lapTime ラップタイム */
    public double getLapTime() {
        return lapTime;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param numberOfLaps セットする周回数 */
    public void setNumberOfLaps(int numberOfLaps) {
        this.numberOfLaps = numberOfLaps;
    }

    /** @param uuid セットするUUID */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /** @param lapTime セットするラップタイム */
    public void setLapTime(double lapTime) {
        this.lapTime = lapTime;
    }

    //〓 util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 他のLapTimeオブジェクトのラップタイムと値を比較する
     * 他のラップタイムよりも速い場合-1、
     * 他のラップタイムよりも遅い場合1、
     * 同値の場合は0を返す
     * @param targetObject 他のLapTimeオブジェクト
     * @return 比較結果
     */
    @Override
    public int compareTo(Object targetObject) {
        LapTime otherLapTime = (LapTime) targetObject;

        double result = getLapTime() - otherLapTime.getLapTime();

        //他のラップタイムよりも低い（速い）
        if (result < 0) {
            return -1;

        //他のラップタイムよりも高い（遅い）
        } else if (0 < result) {
            return 1;

        //他のラップタイムと同値
        } else {
            return 0;
        }
    }
}
