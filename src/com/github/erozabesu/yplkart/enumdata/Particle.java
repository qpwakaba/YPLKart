package com.github.erozabesu.yplkart.enumdata;

import com.github.erozabesu.yplkart.reflection.Objects;

public enum Particle {
    EXPLOSION_NORMAL(true), // 爆発パーティクル小
    EXPLOSION_LARGE(true), // 爆発パーティクル中
    EXPLOSION_HUGE(true), // 爆発パーティクル大
    FIREWORKS_SPARK(false), // 小さな白い粒子が周囲に拡散（当たり判定あり）
    WATER_BUBBLE(false), // 少量の泡が高速で上昇
    WATER_SPLASH(false), // 水色の粒子が足元から噴出
    WATER_WAKE(false), // 水色の粒子が周囲に拡散
    SUSPENDED(false), // ごく少量の小さな青い粒子が足元から真上へ高速で噴出？
    SUSPENDED_DEPTH(false), // 灰色の粒子がその場に残る
    CRIT(false), // クリティカルヒットパーティクル
    CRIT_MAGIC(false), // クリティカルヒットパーティクルの青色バージョン
    SMOKE_NORMAL(false), // 黒い粒子のパーティクル小
    SMOKE_LARGE(false), // 黒い粒子のパーティクル中
    SPELL(false), // 白い渦状の粒子が足元から噴出
    SPELL_INSTANT(false), // 白い渦状の粒子が足元から噴出
    SPELL_MOB(false), // カラフルな渦状の粒子が足元から噴出
    SPELL_MOB_AMBIENT(false), // ほぼ透明な渦状の粒子が足元から噴出
    SPELL_WITCH(false), // 小さな紫の粒子が周囲に拡散
    DRIP_WATER(false), // 頭上から水滴が滴る
    DRIP_LAVA(false), // 頭上から溶岩の水滴が滴る
    VILLAGER_ANGRY(false), // ハートに亀裂が入った灰色のマーク
    VILLAGER_HAPPY(false), // 黄緑色の粒子がその場に残る
    TOWN_AURA(false), // 灰色の粒子がその場に残る
    NOTE(false), // ♪
    PORTAL(false), // 小さな紫の粒子が収束
    ENCHANTMENT_TABLE(false), // 小さな文字が周囲からプレイヤーに収束
    FLAME(false), // 小さな火の玉が周囲に拡散
    LAVA(false), // 足元から火の玉が噴出する
    FOOTSTEP(false), // 柱上のタイルがその場に残る
    CLOUD(false), // 小さな白い粒子が周囲に拡散
    REDSTONE(false), // speed=1:カラフルな粒子がその場に残る speed=0:赤色の粒子がその場に残る
    SNOWBALL(false), // 雪玉が砕け散るパーティクル
    SNOW_SHOVEL(false), // 雪を掘り返した際のパーティクル
    SLIME(false), // スライムの欠片のようなものが足元から噴出
    HEART(false), // ハートマーク
    BARRIER(false), // バリアブロック
    ITEM_CRACK(false), // ツールのような耐久値のあるアイテムが破損した際のパーティクル。勢い良く周囲へ拡散する。particleData:int[2]{ID, Data}を利用する
    BLOCK_CRACK(false), // ブロックを破壊した際のパーティクル。少しホップアップした後落下していく。particleData:int[1]{ID+(Data<<12)}を利用する
    BLOCK_DUST(false), // ブロックに勢い良く着地した際のパーティクル。勢い良く周囲へ拡散する。particleData:int[1]{ID+(Data<<12)}を利用する
    WATER_DROP(false), // 水色の粒子が足元から噴出
    ITEM_TAKE(false), // 何も起こらない？
    MOB_APPEARANCE(true); // 海底遺跡ボスが目の前を通り過ぎる

    /** NmsEnumParticleクラスの要素 */
    private Object nmsEnumParticle;

    /** パーティクルの描画距離を広いモードで再生するかどうか */
    private boolean isLongDistance;

    /** @param isLongDistance パーティクルの描画距離を広いモードで再生するかどうか */
    private Particle(boolean isLongDistance) {
        String name = name();
        for (Object particle : Objects.nmsEnumParticle) {
            if (particle.toString().equalsIgnoreCase(name)) {
                this.nmsEnumParticle = particle;
                break;
            }
        }

        this.isLongDistance = isLongDistance;
    }

    /** @return NmsEnumParticleクラスの要素 */
    public Object getNmsEnumParticle () {
        return this.nmsEnumParticle;
    }

    /** @return パーティクルの描画距離を広いモードで再生するかどうか */
    public boolean isLongDistance() {
        return this.isLongDistance;
    }

    /**
     * 引数enumNameと一致する要素名を持つ要素を返す
     * @param enumName Particle要素名
     * @return Particle
     */
    public static Particle getParticleByName(String enumName) {
        for (Particle particle : Particle.values()) {
            if (particle.name().equalsIgnoreCase(enumName)) {
                return particle;
            }
        }
        return null;
    }
}
