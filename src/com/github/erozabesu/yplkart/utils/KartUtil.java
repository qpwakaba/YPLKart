package com.github.erozabesu.yplkart.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.data.DisplayKartConfig;
import com.github.erozabesu.yplkart.enumdata.KartType;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.reflection.YPLConstructors;
import com.github.erozabesu.yplutillibrary.reflection.Methods;
import com.github.erozabesu.yplutillibrary.util.ReflectionUtil;

/**
 * カートエンティティの取得、操作を行うユーティリティクラス。
 * @author erozabesu
 */
public class KartUtil {

    /** 生成したカートエンティティのEntityIDとEntityを格納する */
    private static HashMap<Integer, Entity> kartEntityIdMap = new HashMap<Integer, Entity>();

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    private static HashMap<Integer, Entity> getKartEntityIdMap() {
        return kartEntityIdMap;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数entityをハッシュマップkartEntityIdMapに追加する
     * @param entity 追加するエンティティ
     */
    private static void putKartEntityIdMap(Entity entity) {
        getKartEntityIdMap().put(entity.getEntityId(), entity);
    }

    /**
     * 引数entityIdをハッシュマップkartEntityIdMapから削除する
     * @param entityId 削除するEntityID
     */
    private static void removeKartEntityIdMap(int entityId) {
        getKartEntityIdMap().remove(entityId);
    }

    //〓 Util - Get 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数entityIdをEntityIDとして持つカートエンティティを返す。<br>
     * 存在しない場合は{@code null}を返す。
     * @param entityId 調べるエンティティのEntityID
     * @return カートエンティティ
     */
    public static Entity getKartEntityByEntityId(int entityId) {
        return getKartEntityIdMap().get(entityId);
    }

    /**
     * 引数entityIdをEntityIDとして持つエンティティがカートエンティティかどうかを返す。
     * @param entityId 調べるエンティティのEntityID
     * @return カートエンティティかどうか
     */
    public static boolean isKartEntityByEntityId(int entityId) {
        return getKartEntityIdMap().keySet().contains(entityId);
    }

    /**
     * 引数entityのMetadataからCustomMinecartObjectを取得し返す
     * @param entity CustomMinecartObjectを取得するEntity
     * @return 取得したCustomMinecartObject
     */
    public static Object getCustomMinecartObjectByEntityMetaData(Entity entity) {
        List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
        if (metaDataList.size() != 0) {
            MetadataValue metaData = metaDataList.get(0);
            if (metaData != null) {
                Object metaDataValue = metaData.value();
                if (metaDataValue.getClass().isArray()) {
                    //MetaDataが配列の場合
                    for (Object values : (Object[]) metaDataValue) {
                        if (values.getClass().getSimpleName().contains("CustomArmorStand")) {
                            return values;
                        }
                    }
                } else {
                    //MetaDataが単数の場合
                    if (metaDataValue.getClass().getSimpleName().contains("CustomArmorStand")) {
                        return metaDataValue;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 引数entityのMetadataからKartオブジェクトを取得し返す
     * @param entity Kartオブジェクトを取得するEntity
     * @return 取得したKartオブジェクト
     */
    public static Kart getKartObjectByEntityMetaData(Entity entity) {
        List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
        if (metaDataList.size() != 0) {
            MetadataValue metaData = metaDataList.get(0);
            if (metaData != null) {
                Object metaDataValue = metaData.value();
                if (metaDataValue.getClass().isArray()) {
                    //MetaDataが配列の場合
                    for (Object values : (Object[]) metaDataValue) {
                        if (values instanceof Kart) {
                            return (Kart) values;
                        }
                    }
                } else {
                    //MetaDataが単数の場合
                    if (metaDataValue instanceof Kart) {
                        return (Kart) metaDataValue;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 引数entityが引数kartTypeのエンティティかどうか判別する
     * @param entity 判別するエンティティ
     * @param kartType 判別するKartType
     * @return 引数kartTypeのエンティティかどうか
     */
    public static boolean isSpecificKartType(Entity entity, KartType kartType) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof ArmorStand) {
            List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
            if (metaDataList.size() != 0) {
                MetadataValue metaData = metaDataList.get(0);
                if (metaData != null) {
                    Object metaDataValue = metaData.value();
                    if (metaDataValue.getClass().isArray()) {
                        //MetaDataが配列の場合
                        for (Object values : (Object[]) metaDataValue) {
                            if (values instanceof KartType) {
                                if (values.equals(kartType)) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        //MetaDataが単数の場合
                        if (metaDataValue instanceof KartType) {
                            if (metaDataValue.equals(kartType)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 引数entityカートエンティティかどうか判別する
     * @param entity 判別するエンティティ
     * @return カートエンティティかどうか
     */
    public static boolean isKartEntity(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof ArmorStand) {
            List<MetadataValue> metaDataList = entity.getMetadata(YPLKart.PLUGIN_NAME);
            if (metaDataList.size() != 0) {
                MetadataValue metaData = metaDataList.get(0);
                if (metaData != null) {
                    Object metaDataValue = metaData.value();
                    if (metaDataValue.getClass().isArray()) {
                        //MetaDataが配列の場合
                        for (Object values : (Object[]) metaDataValue) {
                            if (values.getClass().getSimpleName().equalsIgnoreCase("KartType")) {
                                return true;
                            }
                        }
                    } else {
                        //MetaDataが単数の場合
                        if (metaDataValue.getClass().getSimpleName().equalsIgnoreCase("KartType")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //〓 Edit Entity 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * カートエンティティをデスポーンさせる。<br>
     * 登録されているデータも削除する必要があるため、カートエンティティをデスポーンさせる場合は必ずこのメソッドを経由すること。
     * @param entity デスポーンさせるカートエンティティ
     */
    public static void removeKartEntity(Entity entity) {
        removeKartEntityIdMap(entity.getEntityId());
        entity.remove();
    }

    public static Entity createRacingKart(Location location, Kart kart) {
        Entity entity = KartUtil.createCustomKart(location, kart, KartType.RacingKart);

        entity.setCustomName(kart.getKartName());

        return entity;
    }

    /**
     * 引数uuidをカスタムネームに持つディスプレイ用カートを引数locationに生成する。
     * @param location 生成する座標
     * @param kart Kartオブジェクト。ディスプレイブロックの設定を引き継ぐ
     * @param uuid カスタムネーム。displaykart.ymlのコンフィグキーでもある
     * @return 生成したMinecartエンティティ
     */
    public static Entity createDisplayKart(Location location, Kart kart, String uuid) {
        Entity entity = KartUtil.createCustomKart(location, kart, KartType.DisplayKart);

        if (uuid == null) {
            entity.setCustomName(entity.getUniqueId().toString());
            DisplayKartConfig.createDisplayKart(entity.getUniqueId().toString(), kart, location);
        } else {
            entity.setCustomName(uuid);
        }

        return entity;
    }

    public static Entity createDriveKart(Location location, Kart kart) {
        Entity entity = KartUtil.createCustomKart(location, kart, KartType.DriveKart);

        entity.setCustomName(kart.getKartName());

        return entity;
    }

    /**
     * カスタムアーマースタンドエンティティを生成する。
     * @param location 生成する座標
     * @param kart パラメータを引き継ぐKartObject
     * @param kartType カートの種類
     * @return 生成したMinecartEntity
     */
    private static Entity createCustomKart(Location location, Kart kart, KartType kartType) {
        Entity entity = null;
        try {
            Object craftWorld = ReflectionUtil.invoke(Methods.craftWorld_getHandle, location.getWorld());
            Object customKart = YPLConstructors.customArmorStand.newInstance(craftWorld, kart, kartType, location);

            //個別情報の格納
            entity = (Entity) Methods.nmsEntity_getBukkitEntity.invoke(customKart);
            entity.setCustomNameVisible(false);
            entity.setMetadata(YPLKart.PLUGIN_NAME, new FixedMetadataValue(
                    YPLKart.getInstance(), new Object[]{kart, kartType, customKart}));

            //EntityIDの格納
            putKartEntityIdMap(entity);

            //エンティティのスポーン
            //必ずEntityIDの格納後に行う
            Methods.nmsWorld_addEntity.invoke(craftWorld, customKart);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entity;
    }

}
