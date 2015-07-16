package com.github.erozabesu.yplkart.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public enum Particle {
    BARRIER("barrier"), //バリアブロック
    BLOCK_CRACK("blockcrack"), //？
    BLOCK_DUST("blockdust"), //使用禁止
    CRIT("crit"), //クリティカルヒットエフェクト
    CRIT_MAGIC("magicCrit"), //クリティカルヒットエフェクト色違い青
    CLOUD("cloud"), //小さな白い粒子が周囲に拡散
    DRIP_WATER("dripWater"), //頭上から水滴が滴る
    DRIP_LAVA("dripLava"), //頭上から溶岩の水滴が滴る
    ENCHANTMENT_TABLE("enchantmenttable"), //小さな文字が周囲からプレイヤーに収束
    EXPLOSION("explode"), //名称変更？
    EXPLOSION_LARGE("largeexplode"), //効果なし
    EXPLOSION_HUGE("hugeexplosion"), //効果なし
    FIREWORKS_SPARK("fireworksSpark"), //小さな白い粒子が周囲に拡散（当たり判定あり）
    FLAME("flame"), //小さな火の玉が周囲に拡散
    FOOTSTEP("footstep"), //柱上のタイルがその場に残る
    HEART("heart"), //ハートマーク
    ITEM_CRACK("iconcrack"), //使用禁止
    ITEM_TAKE("take"), //?
    LAVA("lava"), //足元から火の玉が噴出する
    MOB_APPEARANCE("mobappearance"), //海底遺跡ボスが目の前を通り過ぎる
    NOTE("note"), //♪
    PORTAL("portal"), //小さな紫の粒子がプレイヤーに収束
    REDSTONE("reddust"), //カラフルな粒子がその場に残る
    SLIME("slime"), //スライムの欠片のようなものが足元から噴出
    SMOKE_NORMAL("smoke"), //
    SMOKE_LARGE("largesmoke"), //
    SNOWBALL("snowballpoof"), //白い欠片が足元から噴出
    SNOW_SHOVEL("snowshovel"), //小さな白い粒子が周囲に拡散
    SPELL("spell"), //白い渦状の粒子が足元から噴出
    SPELL_INSTANT("instantSpell"), //白い渦状の粒子が足元から噴出
    SPELL_MOB("mobSpell"), //カラフルな渦状の粒子が足元から噴出
    SPELL_MOB_AMBIENT("mobSpellAmbient"), //ほぼ透明な渦状の粒子が足元から噴出
    SPELL_WITCH("witchMagic"), //小さな紫の粒子が周囲に拡散
    SUSPENDED("suspended"), //小さな青い粒子が極少量足元から真上へ高速で噴出？
    SUSPENDED_DEPTH("depthsuspend"), //灰色の粒子がその場に残る
    TOWN_AURA("townaura"), //灰色の粒子がその場に残る
    WATER_BUBBLE("bubble"), //少量の泡が高速で上昇
    WATER_DROP("droplet"), //水色の粒子が足元から噴出
    WATER_SPLASH("splash"), //水色の粒子が足元から噴出
    WATER_WAKE("wake"), //水色の粒子が周囲に拡散
    VILLAGER_ANGRY("angryVillager"), //ハートに亀裂が入った灰色のマーク
    VILLAGER_HAPPY("happyVillager");//黄緑色の粒子がその場に残る

    private String name;

    Particle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void sendToPlayer(String effect, Player player, Location location, float offsetX, float offsetY,
            float offsetZ, float speed, int count) {
        try {
            Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void sendToLocation(String effect, Location location, float offsetX, float offsetY, float offsetZ,
            float speed, int count) {
        try {
            Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object createPacket(String effect, Location location, float offsetX, float offsetY,
            float offsetZ, float speed, int count) throws Exception {
        if (count <= 0) {
            count = 1;
        }
        Class<?> packetClass = Util.getBukkitClass("PacketPlayOutWorldParticles");

        Object enumobject = null;
        for (Object ob : Util.getBukkitClass("EnumParticle").getEnumConstants()) {
            if (ob.toString().equalsIgnoreCase(effect))
                enumobject = ob;
        }

        Object packet = packetClass.getConstructor(Util.getBukkitClass("EnumParticle"), boolean.class, float.class,
                float.class, float.class, float.class, float.class, float.class, float.class, int.class,
                int[].class).newInstance(enumobject, true, (float) location.getX(), (float) location.getY(),
                (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, null);

        return packet;
    }

    private static void sendPacket(Player p, Object packet) throws Exception {
        Object eplayer = getHandle(p);
        Field playerConnectionField = eplayer.getClass().getField("playerConnection");
        Object playerConnection = playerConnectionField.get(eplayer);
        for (Method m : playerConnection.getClass().getMethods()) {
            if (m.getName().equalsIgnoreCase("sendPacket")) {
                m.invoke(playerConnection, packet);
                return;
            }
        }
    }

    private static Object getHandle(Entity entity) {
        try {
            Method entity_getHandle = entity.getClass().getMethod("getHandle");
            Object nms_entity = entity_getHandle.invoke(entity);
            return nms_entity;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
