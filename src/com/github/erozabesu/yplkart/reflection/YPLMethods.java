package com.github.erozabesu.yplkart.reflection;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import com.github.erozabesu.yplkart.enumdata.KartType;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplutillibrary.util.ReflectionUtil;

public class YPLMethods {

    public static Method getGroundFrictionX = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getGroundFrictionX");
    public static Method getGroundFrictionY = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getGroundFrictionY");
    public static Method getGroundFrictionZ = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getGroundFrictionZ");
    public static Method getFlyFrictionX = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getFlyFrictionX");
    public static Method getFlyFrictionY = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getFlyFrictionY");
    public static Method getFlyFrictionZ = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getFlyFrictionZ");
    public static Method getKart = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getKart");
    public static Method getKartType = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getKartType");
    public static Method getSpeedStack = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getSpeedStack");
    public static Method getLastMotionSpeed = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getLastMotionSpeed");
    public static Method isKillerInitialized = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "isKillerInitialized");
    public static Method getKillerX = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getKillerX");
    public static Method getKillerY = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getKillerY");
    public static Method getKillerZ = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getKillerZ");
    public static Method getKillerPassedCheckPointList = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getKillerPassedCheckPointList");
    public static Method getKillerLastPassedCheckPoint = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getKillerLastPassedCheckPoint");
    public static Method getLivingCheckTask = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "getLivingCheckTask");

    public static Method setGroundFrictionX = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setGroundFrictionX", double.class);
    public static Method setGroundFrictionY = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setGroundFrictionY", double.class);
    public static Method setGroundFrictionZ = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setGroundFrictionZ", double.class);
    public static Method setFlyFrictionX = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setFlyFrictionX", double.class);
    public static Method setFlyFrictionY = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setFlyFrictionY", double.class);
    public static Method setFlyFrictionZ = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setFlyFrictionZ", double.class);
    public static Method setKart = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setKart", Kart.class);
    public static Method setKartType = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setKartType", KartType.class);
    public static Method setSpeedStack = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setSpeedStack", double.class);
    public static Method setLastMotionSpeed = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setLastMotionSpeed", double.class);
    public static Method setKillerInitialized = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setKillerInitialized", boolean.class);
    public static Method setKillerX = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setKillerX", double.class);
    public static Method setKillerY = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setKillerY", double.class);
    public static Method setKillerZ = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setKillerZ", double.class);
    public static Method setKillerPassedCheckPointList = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setKillerPassedCheckPointList", List.class);
    public static Method setKillerLastPassedCheckPoint = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setKillerLastPassedCheckPoint", Entity.class);
    public static Method setLivingCheckTask = ReflectionUtil.getMethod(YPLClasses.customArmorStand, "setLivingCheckTask", BukkitTask.class);
}
