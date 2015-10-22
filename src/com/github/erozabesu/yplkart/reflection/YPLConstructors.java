package com.github.erozabesu.yplkart.reflection;

import java.lang.reflect.Constructor;

import org.bukkit.Location;

import com.github.erozabesu.yplkart.enumdata.KartType;
import com.github.erozabesu.yplkart.object.Kart;
import com.github.erozabesu.yplutillibrary.reflection.Classes;
import com.github.erozabesu.yplutillibrary.util.ReflectionUtil;

public class YPLConstructors {

    public static Constructor<?> customArmorStand = ReflectionUtil.getConstructor(YPLClasses.customArmorStand, Classes.nmsWorld, Kart.class, KartType.class, Location.class);
}
