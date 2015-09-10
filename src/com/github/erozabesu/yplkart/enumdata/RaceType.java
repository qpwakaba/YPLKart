package com.github.erozabesu.yplkart.enumdata;
/**
 * 開催するレースタイプを管理するクラス
 * @author erozabesu
 */
public enum RaceType {

    KART,

    RUNNING;

    /**
     * 引数raceTypeNameと一致する要素名を持つRaceTypeを返す
     * @param raceTypeName 文字列
     * @return 要素名の一致したRaceType。一致するRaceTypeがない場合{@code null}を返す
     */
    public static RaceType getRaceTypeByString(String raceTypeName) {
        for (RaceType raceType : RaceType.values()) {
            if (raceType.name().equalsIgnoreCase(raceTypeName)) {
                return raceType;
            }
        }
        return null;
    }

    /** @return RaceTypeの一覧 */
    public static String getRaceTypeListString() {
        String raceTypeList = null;
        for (RaceType raceType : RaceType.values()) {
            if (raceTypeList == null) {
                raceTypeList = raceType.name();
            } else {
                raceTypeList += ", " + raceType.name();
            }
        }
        return raceTypeList;
    }
}
