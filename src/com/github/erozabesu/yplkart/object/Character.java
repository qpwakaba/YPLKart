package com.github.erozabesu.yplkart.object;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;
import com.github.erozabesu.yplkart.utils.Util;

/**
 * Characterの各設定を格納するオブジェクトクラス
 * @author erozabesu
 */
public class Character {

    /** キャラクター名 */
    private String characterName;

    /** nmsEntityクラス */
    private Class<?> nmsClass;

    /** ヘッドブロックのプレイヤー名 */
    private String menuHeadBlockPlayerName;

    /** メニュークリック時の効果音 */
    private Sound menuClickSound;

    /** メニュークリック時の効果音の音量 */
    private float menuClickSoundVolume;

    /** メニュークリック時の効果音のピッチ */
    private float menuClickSoundPitch;

    /** 最大スロット数補正 */
    private int adjustMaxSlotSize;

    /** 最大スタック数補正 */
    private int adjustMaxStackSize;

    /** スピードエフェクトの秒数補正 */
    private int adjustPositiveEffectSecond;

    /** スピードエフェクトのLV補正 */
    private int adjustPositiveEffectLevel;

    /** スロウエフェクトの秒数補正 */
    private int adjustNegativeEffectSecond;

    /** スロウエフェクトのLV補正 */
    private int adjustNegativeEffectLevel;

    /** 攻撃力補正 */
    private int adjustAttackDamage;

    /** 最大体力 */
    private double maxHealth;

    /** 歩行速度 */
    private float walkSpeed;

    /** 死亡後無敵状態になる秒数 */
    private int penaltyAntiReskillSecond;

    /** デスペナルティの秒数 */
    private int penaltySecond;

    /** デスペナルティ時の歩行速度 */
    private float penaltyWalkSpeed;

    //〓 main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ
     * 設定をメンバ変数へ格納する
     * @param key コンフィグキー
     */
    public Character(String key) {
        setCharacterName(key);

        ConfigManager config = ConfigManager.CHARACTER_CONFIG;

        Class<?> nmsClass = ReflectionUtil.getNMSClass("Entity" + config.getString(key + ".entity_type"));
        if (nmsClass == null) {
            setNmsClass(ReflectionUtil.getNMSClass("EntityHuman"));
        } else {
            setNmsClass(nmsClass);
        }

        setMenuHeadBlockPlayerName(config.getString(key + ".menu_head_item_player_name"));

        setMenuClickSound(config.getSound(key + ".menu_click_sound"));
        setMenuClickSoundVolume(config.getFloat(key + ".menu_click_sound_volume"));
        setMenuClickSoundPitch(config.getFloat(key + ".menu_click_sound_pitch"));

        setAdjustMaxSlotSize(config.getInteger(key + ".item_adjust_max_slot"));
        setAdjustMaxStackSize(config.getInteger(key + ".item_adjust_max_stack_size"));
        setAdjustPositiveEffectSecond(config.getInteger(key + ".item_adjust_positive_effect_second"));
        setAdjustPositiveEffectLevel(config.getInteger(key + ".item_adjust_positive_effect_level"));
        setAdjustNegativeEffectSecond(config.getInteger(key + ".item_adjust_negative_effect_second"));
        setAdjustNegativeEffectLevel(config.getInteger(key + ".item_adjust_negative_effect_level"));
        setAdjustAttackDamage(config.getInteger(key + ".item_adjust_attack_damage"));

        setMaxHealth(config.getDouble(key + ".max_health"));
        setWalkSpeed(config.getFloat(key + ".walk_speed"));
        setPenaltyAntiReskillSecond(config.getInteger(key + ".death_penalty.anti_reskill_second"));
        setPenaltySecond(config.getInteger(key + ".death_penalty.penalty_second"));
        setPenaltyWalkSpeed(config.getFloat(key + ".death_penalty.walk_speed"));
    }

    //〓 getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return キャラクター名 */
    public String getCharacterName() {
        return this.characterName;
    }

    /** @return nmsEntityクラス */
    public Class<?> getNmsClass() {
        return this.nmsClass;
    }

    /** @return menuHeadBlockPlayerName ヘッドブロックのプレイヤー名 */
    public String getMenuHeadBlockPlayerName() {
        return menuHeadBlockPlayerName;
    }

    /**  @return メニュークリック時の効果音 */
    public Sound getMenuClickSound() {
        return this.menuClickSound;
    }

    /** @return メニュークリック時の効果音の音量 */
    public float getMenuClickSoundVolume() {
        return this.menuClickSoundVolume;
    }

    /** @return メニュークリック時の効果音のピッチ */
    public float getMenuClickSoundPitch() {
        return this.menuClickSoundPitch;
    }

    /** @return 最大スロット数補正 */
    public int getAdjustMaxSlotSize() {
        //アイテムスロットの上限は9
        if (9 < this.adjustMaxSlotSize + (Integer) ConfigEnum.ITEM_SLOT.getValue())
            return 9;
        return this.adjustMaxSlotSize;
    }

    /** @return 最大スタック数補正 */
    public int getAdjustMaxStackSize() {
        return this.adjustMaxStackSize;
    }

    /** @return スピードエフェクトの秒数補正 */
    public int getAdjustPositiveEffectSecond() {
        return this.adjustPositiveEffectSecond;
    }

    /** @return スピードエフェクトのLV補正 */
    public int getAdjustPositiveEffectLevel() {
        return this.adjustPositiveEffectLevel;
    }

    /** @return スロウエフェクトの秒数補正 */
    public int getAdjustNegativeEffectSecond() {
        return this.adjustNegativeEffectSecond;
    }

    /** @return スロウエフェクトのLV補正 */
    public int getAdjustNegativeEffectLevel() {
        return this.adjustNegativeEffectLevel;
    }

    /** @return 攻撃力補正 */
    public int getAdjustAttackDamage() {
        return this.adjustAttackDamage;
    }

    /** @return 最大体力 */
    public double getMaxHealth() {
        return this.maxHealth;
    }

    /** @return 歩行速度 */
    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    /** @return 死亡後無敵状態になる秒数 */
    public int getPenaltyAntiReskillSecond() {
        return this.penaltyAntiReskillSecond;
    }

    /** @return デスペナルティの秒数 */
    public int getPenaltySecond() {
        return this.penaltySecond;
    }

    /** @return デスペナルティ時の歩行速度 */
    public float getPenaltyWalkSpeed() {
        return this.penaltyWalkSpeed;
    }

    //〓 setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param characterName セットするキャラクター名 */
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    /** @param nmsClass セットするnmsEntityクラス */
    public void setNmsClass(Class<?> nmsClass) {
        this.nmsClass = nmsClass;
    }

    /** @param menuHeadBlockPlayerName セットするヘッドブロックのプレイヤー名 */
    public void setMenuHeadBlockPlayerName(String menuHeadBlockPlayerName) {
        this.menuHeadBlockPlayerName = menuHeadBlockPlayerName;
    }

    /** @param menuClickSound セットするメニュークリック時の効果音 */
    public void setMenuClickSound(Sound menuClickSound) {
        this.menuClickSound = menuClickSound;
    }

    /** @param menuClickSoundVolume セットするメニュークリック時の効果音の音量 */
    public void setMenuClickSoundVolume(float menuClickSoundVolume) {
        this.menuClickSoundVolume = menuClickSoundVolume;
    }

    /** @param menuClickSoundPitch セットするメニュークリック時の効果音のピッチ */
    public void setMenuClickSoundPitch(float menuClickSoundPitch) {
        this.menuClickSoundPitch = menuClickSoundPitch;
    }

    /** @param adjustMaxSlotSize セットする最大スロット数補正 */
    public void setAdjustMaxSlotSize(int adjustMaxSlotSize) {
        this.adjustMaxSlotSize = adjustMaxSlotSize;
    }

    /** @param adjustMaxStackSize セットする最大スタック数補正 */
    public void setAdjustMaxStackSize(int adjustMaxStackSize) {
        this.adjustMaxStackSize = adjustMaxStackSize;
    }

    /** @param adjustPositiveEffectSecond セットするスピードエフェクトの秒数補正 */
    public void setAdjustPositiveEffectSecond(int adjustPositiveEffectSecond) {
        this.adjustPositiveEffectSecond = adjustPositiveEffectSecond;
    }

    /** @param adjustPositiveEffectLevel セットするスピードエフェクトのLV補正 */
    public void setAdjustPositiveEffectLevel(int adjustPositiveEffectLevel) {
        this.adjustPositiveEffectLevel = adjustPositiveEffectLevel;
    }

    /** @param adjustNegativeEffectSecond セットするスロウエフェクトの秒数補正 */
    public void setAdjustNegativeEffectSecond(int adjustNegativeEffectSecond) {
        this.adjustNegativeEffectSecond = adjustNegativeEffectSecond;
    }

    /** @param adjustNegativeEffectLevel セットするスロウエフェクトのLV補正 */
    public void setAdjustNegativeEffectLevel(int adjustNegativeEffectLevel) {
        this.adjustNegativeEffectLevel = adjustNegativeEffectLevel;
    }

    /** @param adjustAttackDamage セットする攻撃力補正 */
    public void setAdjustAttackDamage(int adjustAttackDamage) {
        this.adjustAttackDamage = adjustAttackDamage;
    }

    /** @param maxHealth セットする最大体力 */
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    /** @param walkSpeed セットする歩行速度 */
    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    /** @param penaltyAntiReskillSecond セットする死亡後無敵状態になる秒数 */
    public void setPenaltyAntiReskillSecond(int penaltyAntiReskillSecond) {
        this.penaltyAntiReskillSecond = penaltyAntiReskillSecond;
    }

    /** @param penaltySecond セットするデスペナルティの秒数 */
    public void setPenaltySecond(int penaltySecond) {
        this.penaltySecond = penaltySecond;
    }

    /** @param penaltyWalkSpeed セットするデスペナルティ時の歩行速度 */
    public void setPenaltyWalkSpeed(float penaltyWalkSpeed) {
        this.penaltyWalkSpeed = penaltyWalkSpeed;
    }

    //〓 do 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return メニュー用アイテム */
    public ItemStack getMenuItem() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwner(getMenuHeadBlockPlayerName());
        meta.setDisplayName(ChatColor.GREEN + getCharacterName());
        meta.setLore(MessageEnum.replaceLine(
                MessageEnum.replaceChatColor(getParameter())));

        item.setItemMeta(meta);

        return item;
    }

    /** @return キャラクターのパラメーター一覧 */
    public String getParameter() {
        String[] parameter = new String[] {
                String.valueOf(getMaxHealth()),
                String.valueOf(getWalkSpeed()),
                String.valueOf(getAdjustMaxSlotSize()
                        + (Integer) ConfigEnum.ITEM_SLOT.getValue()),
                Util.convertSignNumber(getAdjustMaxStackSize()),
                Util.convertSignNumber(getAdjustAttackDamage()),
                String.valueOf(getPenaltyAntiReskillSecond()),
                String.valueOf(getPenaltySecond()),
                String.valueOf(getPenaltyWalkSpeed()),
                Util.convertSignNumber(getAdjustPositiveEffectLevel()),
                Util.convertSignNumber(getAdjustPositiveEffectSecond()),
                Util.convertSignNumberR(getAdjustNegativeEffectLevel()),
                Util.convertSignNumberR(getAdjustNegativeEffectSecond())
        };

        return MessageEnum.tableCharacterParameter.getConvertedMessage(new Object[] {parameter});
    }

    public void playMenuSelectSound(Player player) {
        player.playSound(player.getLocation(), this.getMenuClickSound()
                , this.getMenuClickSoundVolume(), this.getMenuClickSoundPitch());
    }
}
