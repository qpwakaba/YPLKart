package com.github.erozabesu.yplkart.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplkart.object.KartType;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class Methods extends ReflectionUtil {

    //〓 Nms 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * BoundingBoxの全面を外側へ拡張する
     * @param Instance 拡張するNmsAxisAlignedBB
     * @param Double X方向の面の拡張値
     * @param Double Y方向の面の拡張値
     * @param Double Z方向の面の拡張値
     */
    public static Method nmsAxisAlignedBB_grow = getMethod(Classes.nmsAxisAlignedBB, "grow", double.class, double.class, double.class);

    /**
     * 引数NmsEntityのBoundingBox内のNmsEntityを返す
     * @param Instance 取得するNmsWorld
     * @param NmsEntity 取得するNmsEntity
     * @param NmsAxisAlignedBB 引数NmsEntityのBoundingBox
     */
    public static Method nmsWorld_getEntities = getMethod(Classes.nmsWorld, "getEntities", Classes.nmsEntity, Classes.nmsAxisAlignedBB);

    /**
     * 引数IntegerのIDを持つNmsEntityを返す
     * @param EntityId
     * @return NmsEntity
     */
    public static Method nmsWorld_getNmsEntityById = getMethod(Classes.nmsWorld, "a", int.class);

    /**
     * @param NmsVec3D
     * @param NmsVec3D
     * @return NmsMovingObjectPosition
     */
    public static Method nmsWorld_rayTrace = getMethod(Classes.nmsWorld, "rayTrace", Classes.nmsVec3D, Classes.nmsVec3D);

    /**
     * 引数NmsEntityをスポーンさせる
     * @param Instance NmsEntityをスポーンさせるNmsWorld
     * @param NmsEntity スポーンさせるNmsEntity
     */
    public static Method nmsWorld_addEntity = getMethod(Classes.nmsWorld, "addEntity", Classes.nmsEntity);

    /**
     * NmsBlockからNmsMaterialを取得する
     * @param Instance 取得するNmsBlock
     * @return 取得したNmsMaterial
     */
    public static Method nmsBlock_getMaterial = getMethod(Classes.nmsBlock, "getMaterial");

    /**
     * NmsDamageSourceからダメージを発生させたNmsEntityを取得する
     * @param Instance 取得するNmsDamageSource
     * @return 取得したNmsEntity
     */
    public static Method nmsDamageSource_getEntity = getMethod(Classes.nmsDamageSource, "getEntity");

    /**
     * NmsMaterialが固形ブロックかどうかを取得する
     * @param Instance 取得するNmsMaterial
     * @return NmsMaterialが固形ブロックかどうか
     */
    public static Method nmsMaterial_isSolid = getMethod(Classes.nmsMaterial, "isSolid");

    /**
     * NmsEntityのNmsAxisAlignedBBを取得する
     * @param Instance 取得するNmsEntity
     * @return 取得したNmsAxisAlignedBB
     */
    public static Method nmsEntity_getBoundingBox = getMethod(Classes.nmsEntity, "getBoundingBox");

    /**
     * NmsEntityからBukkitEntityを取得する
     * @param Instance 取得するNmsEntity
     * @return 取得したBukkitEntity
     */
    public static Method nmsEntity_getBukkitEntity = getMethod(Classes.nmsEntity, "getBukkitEntity");

    /**
     * NmsEntityからカスタムネームを取得する
     * @param Instance 取得するNmsEntity
     * @return 取得したカスタムネーム
     */
    public static Method nmsEntity_getCustomName = getMethod(Classes.nmsEntity, "getCustomName");

    /**
     * NmsEntityからEntityIDを取得する
     * @param Instance 取得するNmsEntity
     * @return 取得したEntityID
     */
    public static Method nmsEntity_getId = getMethod(Classes.nmsEntity, "getId");

    /**
     * NmsEntityからNmsWorldを取得する
     * @param Instance 取得するNmsEntity
     * @return 取得したNmsWorld
     */
    public static Method nmsEntity_getWorld = getMethod(Classes.nmsEntity, "getWorld");

    /**
     * NmsEntityのEntityIDを引数Integerにセットする
     * @param Instance セットするNmsEntity
     * @param Integer セットするEntityID
     */
    public static Method nmsEntity_setEntityID = getMethod(Classes.nmsEntity, "d", int.class);

    /**
     * NmsEntityの現在座標をセットする
     * @param Instance セットするNmsEntity
     * @param Double セットするX座標
     * @param Double セットするY座標
     * @param Double セットするZ座標
     * @param Float セットするYaw
     * @param Float セットするPitch
     */
    public static Method nmsEntity_setLocation = getMethod(Classes.nmsEntity, "setLocation", double.class, double.class, double.class, float.class, float.class);

    /**
     * NmsEntityのカスタムネームをセットする
     * @param Instance セットするNmsEntity
     * @param String セットするカスタムネーム
     */
    public static Method nmsEntity_setCustomName = getMethod(Classes.nmsEntity, "setCustomName", String.class);

    /**
     * NmsEntityのカスタムネームを表示するかどうかをセットする
     * @param Instance セットするNmsEntity
     * @param Boolean カスタムネームを表示するかどうか
     */
    public static Method nmsEntity_setCustomNameVisible = getMethod(Classes.nmsEntity, "setCustomNameVisible", boolean.class);

    /**
     * NmsEntityのAxisAlignedBBを引数Float1*引数Float1*引数Float2の値にセットし、それに伴う座標の移動を行う
     * @param Instance セットするNmsEntity
     * @param Float セットする横幅
     * @param Float セットする高さ
     */
    public static Method nmsEntity_setSize;

    /**
     * NmsEntityのYaw、Pitchをセットする
     * @param Instance セットするNmsEntity
     * @param Float セットするYaw
     * @param Float セットするPitch
     */
    public static Method nmsEntity_setYawPitch = getMethod(Classes.nmsEntity, "setYawPitch", float.class, float.class);

    /**
     * メソッドの処理を見ても特に具体的なものは実行していない用途不明のメソッド
     * @param Instance 実行するNmsEntity
     */
    public static Method nmsEntity_checkBlockCollisions = getMethod(Classes.nmsEntity, "checkBlockCollisions");

    /**
     * NmsEntityの当たり判定に基づく衝突モーションを、自身と引数NmsEntityに対し適用する
     * @param Instance 実行するNmsEntity
     * @param NmsEntity 衝突対象のNmsEntity
     */
    public static Method nmsEntity_collide = getMethod(Classes.nmsEntity, "collide", Classes.nmsEntity);

    /**
     * NmsEntityのデッドフラグをtrueにセットする
     * @param Instance セットするNmsEntity
     */
    public static Method nmsEntity_die = getMethod(Classes.nmsEntity, "die");

    /**
     * NmsEntityに引数NmsEntityへの搭乗を試みさせる
     * @param Instance セットするNmsEntity
     * @param NmsEntity 搭乗対象のNmsEntity
     */
    public static Method nmsEntity_mount = getMethod(Classes.nmsEntity, "mount", Classes.nmsEntity);

    /**
     * NmsEntityの移動を試みる
     * @param Instance 移動させるNmsEntity
     * @param Double セットするXモーション
     * @param Double セットするYモーション
     * @param Double セットするZモーション
     */
    public static Method nmsEntity_move = getMethod(Classes.nmsEntity, "move", double.class, double.class, double.class);

    /**
     * NmsEntityのモーション値をセットする
     * @param Instance セットするNmsEntity
     * @param Double セットするXモーション
     * @param Double セットするYモーション
     * @param Double セットするZモーション
     */
    public static Method nmsEntity_moveAbsolute = getMethod(Classes.nmsEntity, "g", double.class, double.class, double.class);

    /**
     * NmsEntityHumanの歩行速度のフィジカル値を取得する
     * @param Instance 取得するNmsEntityHuman
     */
    public static Method nmsEntityHuman_getAttributesMovementSpeed;

    /**
     * NmsEntityArmorStandの右腕のポーズをセットする
     * @param Instance セットするNmsEntityArmorStand
     * @param NmsVector3f セットするポーズ
     */
    public static Method nmsEntityArmorStand_setRightArmPose = getMethod(Classes.nmsEntityArmorStand, "setRightArmPose", Classes.nmsVector3f);

    /**
     * NmsWatchableObjectのNmsDataWatcherにおける配列Indexを取得する
     * @param Instance 取得するNmsWatchableObject
     * @param NmsWatchableObjectのNmsDataWatcherにおける配列Index
     */
    public static Method nmsWatchableObject_getIndex = getMethod(Classes.nmsWatchableObject, "a");

    /**
     * NmsPlayerConnectionに対しリスポーンウィンドウのリスポーンボタン押下を強制するパケットを送信する
     * @param Instance 送信対象のプレイヤーのNmsPlayerConnection
     * @param NmsPacketPlayInClientCommand 送信するパケット
     */
    public static Method nmsPlayerConnection_skipRespawnWindow = getMethod(Classes.nmsPlayerConnection, "a", Classes.nmsPacketPlayInClientCommand);

    /**
     * NmsPlayerConnectionに対し引数NmsPacketのパケットを送信する
     * @param Instance 送信対象のプレイヤーのNmsPlayerConnection
     * @param NmsPacket 送信するパケット
     */
    public static Method nmsPlayerConnection_sendPacket = getMethod(Classes.nmsPlayerConnection, "sendPacket", Classes.nmsPacket);

    /**
     * 引数Double1、引数Double2の絶対値を比較し、高い方の数値を返す
     * @param Instance {@code null}
     * @param Double
     * @param Double
     * @return 引数Double1、引数Double2の絶対値を比較し、高い方のDouble値
     */
    public static Method static_nmsMathHelper_a = getMethod(Classes.nmsMathHelper, "a", double.class, double.class);

    /**
     * 引数Double1、引数Double2、引数Double3の値を比較し、2番目に大きい数値を返す
     * @param Instance {@code null}
     * @param Double
     * @param Double
     * @param Double
     * @param 引数Double1、引数Double2、引数Double3の値を比較し、2番目に大きいDouble値
     */
    public static Method static_nmsMathHelper_a2 = getMethod(Classes.nmsMathHelper, "a", double.class, double.class, double.class);

    /**
     * 引数Doubleの小数点以下を切り捨てたIntegerを返す
     * @param Instance {@code null}
     * @param Double
     * @return 引数Doubleの小数点以下を切り捨てたInteger値
     */
    public static Method static_nmsMathHelper_floor = getMethod(Classes.nmsMathHelper, "floor", double.class);

    /**
     * 引数Floatのコサイン値を返す
     * @param Instance {@code null}
     * @param Float
     * @return 引数Floatのコサイン値
     */
    public static Method static_nmsMathHelper_cos = getMethod(Classes.nmsMathHelper, "cos", float.class);

    /**
     * 引数Floatのサイン値を返す
     * @param Instance {@code null}
     * @param Float
     * @return 引数Floatのサイン値
     */
    public static Method static_nmsMathHelper_sin = getMethod(Classes.nmsMathHelper, "sin", float.class);

    /**
     * 引数Stringをタイトルパケットに利用するIChatBaseComponentに変換し返す
     * @param Instance {@code null}
     * @param String 変換する文字列
     * @return 取得したIChatBaseComponent
     */
    public static Method static_nmsChatSerializer_buildNmsIChatBaseComponent = getMethod(Classes.nmsChatSerializer, "a", String.class);

    /**
     * 引数IntegerをIDにもつNmsBlockを返す
     * @param Instance {@code null}
     * @param Integer ブロックID
     * @param 取得したNmsBlock
     */
    public static Method static_nmsBlock_getById = getMethod(Classes.nmsBlock, "getById", int.class);

    static {
        //XXX: CraftBukkit Unstable
        if (getBukkitVersion().equalsIgnoreCase("v1_8_R1")) {
            Methods.nmsEntity_setSize = getMethod(Classes.nmsEntity, "a", float.class, float.class);
            Methods.nmsEntityHuman_getAttributesMovementSpeed = getMethod(Classes.nmsEntityHuman, "bH");
        } else {
            Methods.nmsEntity_setSize = getMethod(Classes.nmsEntity, "setSize", float.class, float.class);
            Methods.nmsEntityHuman_getAttributesMovementSpeed = getMethod(Classes.nmsEntityHuman, "bI");
        }
    }

    //〓 Craft 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * CraftWorldからNmsWorldServerを取得する
     * @param Instance CraftWorld
     * @return 取得したNmsWorldServer
     */
    public static Method craftWorld_getHandle =  getMethod(Classes.craftWorld, "getHandle");

    /**
     * CraftBlockからNmsBlockを取得する
     * @param Instance CraftBlock
     * @return 取得したNmsBlock
     */
    public static Method craftBlock_getNMSBlock =  getMethod(Classes.craftBlock, "getNMSBlock");

    /**
     * ItemStackからCraftItemStackを取得する
     * @param Instance {@code null}
     * @param ItemStack 取得するItemStack
     * @return 取得したCraftItemStack
     */
    public static Method static_craftItemStack_asNMSCopy =  getMethod(Classes.craftItemStack, "asNMSCopy", ItemStack.class);

    //〓 Craft List 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** EntityXxxxxクラス毎のgetHandleメソッド */
    public static HashMap<String, Method> craftEntity_getHandle = new HashMap<String, Method>();

    //〓 Ypl 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Method Ypl_getGroundFrictionX = getMethod(Classes.yplCustomKart, "getGroundFrictionX");
    public static Method Ypl_getGroundFrictionY = getMethod(Classes.yplCustomKart, "getGroundFrictionY");
    public static Method Ypl_getGroundFrictionZ = getMethod(Classes.yplCustomKart, "getGroundFrictionZ");
    public static Method Ypl_getFlyFrictionX = getMethod(Classes.yplCustomKart, "getFlyFrictionX");
    public static Method Ypl_getFlyFrictionY = getMethod(Classes.yplCustomKart, "getFlyFrictionY");
    public static Method Ypl_getFlyFrictionZ = getMethod(Classes.yplCustomKart, "getFlyFrictionZ");
    public static Method Ypl_getKart = getMethod(Classes.yplCustomKart, "getKart");
    public static Method Ypl_getKartType = getMethod(Classes.yplCustomKart, "getKartType");
    public static Method Ypl_getSpeedStack = getMethod(Classes.yplCustomKart, "getSpeedStack");
    public static Method Ypl_getLastMotionSpeed = getMethod(Classes.yplCustomKart, "getLastMotionSpeed");
    public static Method Ypl_isKillerInitialized = getMethod(Classes.yplCustomKart, "isKillerInitialized");
    public static Method Ypl_getKillerX = getMethod(Classes.yplCustomKart, "getKillerX");
    public static Method Ypl_getKillerY = getMethod(Classes.yplCustomKart, "getKillerY");
    public static Method Ypl_getKillerZ = getMethod(Classes.yplCustomKart, "getKillerZ");
    public static Method Ypl_getKillerPassedCheckPointList = getMethod(Classes.yplCustomKart, "getKillerPassedCheckPointList");
    public static Method Ypl_getKillerLastPassedCheckPoint = getMethod(Classes.yplCustomKart, "getKillerLastPassedCheckPoint");
    public static Method Ypl_getLivingCheckTask = getMethod(Classes.yplCustomKart, "getLivingCheckTask");

    public static Method Ypl_setGroundFrictionX = getMethod(Classes.yplCustomKart, "setGroundFrictionX", double.class);
    public static Method Ypl_setGroundFrictionY = getMethod(Classes.yplCustomKart, "setGroundFrictionY", double.class);
    public static Method Ypl_setGroundFrictionZ = getMethod(Classes.yplCustomKart, "setGroundFrictionZ", double.class);
    public static Method Ypl_setFlyFrictionX = getMethod(Classes.yplCustomKart, "setFlyFrictionX", double.class);
    public static Method Ypl_setFlyFrictionY = getMethod(Classes.yplCustomKart, "setFlyFrictionY", double.class);
    public static Method Ypl_setFlyFrictionZ = getMethod(Classes.yplCustomKart, "setFlyFrictionZ", double.class);
    public static Method Ypl_setKart = getMethod(Classes.yplCustomKart, "setKart", Kart.class);
    public static Method Ypl_setKartType = getMethod(Classes.yplCustomKart, "setKartType", KartType.class);
    public static Method Ypl_setSpeedStack = getMethod(Classes.yplCustomKart, "setSpeedStack", double.class);
    public static Method Ypl_setLastMotionSpeed = getMethod(Classes.yplCustomKart, "setLastMotionSpeed", double.class);
    public static Method Ypl_setKillerInitialized = getMethod(Classes.yplCustomKart, "setKillerInitialized", boolean.class);
    public static Method Ypl_setKillerX = getMethod(Classes.yplCustomKart, "setKillerX", double.class);
    public static Method Ypl_setKillerY = getMethod(Classes.yplCustomKart, "setKillerY", double.class);
    public static Method Ypl_setKillerZ = getMethod(Classes.yplCustomKart, "setKillerZ", double.class);
    public static Method Ypl_setKillerPassedCheckPointList = getMethod(Classes.yplCustomKart, "setKillerPassedCheckPointList", List.class);
    public static Method Ypl_setKillerLastPassedCheckPoint = getMethod(Classes.yplCustomKart, "setKillerLastPassedCheckPoint", Entity.class);
    public static Method Ypl_setLivingCheckTask = getMethod(Classes.yplCustomKart, "setLivingCheckTask", BukkitTask.class);
}
