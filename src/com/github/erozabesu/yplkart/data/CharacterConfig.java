package com.github.erozabesu.yplkart.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.object.Character;

/**
 * キャラクター設定を管理するクラス
 * ユーザ側で要素数を変更できる動的なコンフィグを扱うためオブジェクトで管理する
 * @author erozabesu
 */
public class CharacterConfig{

    /** Characterオブジェクトを格納しているハッシュマップ */
    private static HashMap<String, Character> characterObject = new HashMap<String, Character>();

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 設定データを再読み込みする
     * 既存のcharacterオブジェクトを破棄し、新規に生成しハッシュマップに格納する
     */
    public static void reload() {
        characterObject.clear();

        for(String key : ConfigManager.CHARACTER_CONFIG.getLocalConfig().getKeys(false)) {
            characterObject.put(key, new Character(key));
        }
    }

    /**
     * 全CharacterオブジェクトをHashMapで返す
     * @return HashMap型の全Characterオブジェクト
     */
    public static HashMap<String, Character> getCharacterMap() {
        return characterObject;
    }

    /**
     * 全CharacterオブジェクトをArrayListで返す
     * @return ArrayList型の全Characterオブジェクト
     */
    public static ArrayList<Character> getCharacterList() {
        return new ArrayList<Character>(getCharacterMap().values());
    }

    /** キャラクター名の一覧を返す */
    public static String getCharacterListString() {
        String characterlist = null;
        for (String charactername : getCharacterMap().keySet()) {
            if (characterlist == null)
                characterlist = charactername;
            else
                characterlist += ", " + charactername;
        }
        return characterlist;
    }

    /**
     * 文字列と一致するキャラクター名のCharacterオブジェクトを返す
     * @param key キャラクター名
     * @return Characterオブジェクト
     */
    public static Character getCharacter(String key) {
        for (String mapKey : getCharacterMap().keySet()) {
            if (mapKey.equalsIgnoreCase(key)) {
                return getCharacterMap().get(mapKey);
            }
        }
        return null;
    }

    /**
     * 全キャラクターの中からランダムに抽出した1キャラクターを返す
     * @return Characterオブジェクト
     */
    public static Character getRandomCharacter() {
        int ram = getCharacterList().size();
        ram = new Random().nextInt(ram);

        return getCharacterList().get(ram);
    }
}
