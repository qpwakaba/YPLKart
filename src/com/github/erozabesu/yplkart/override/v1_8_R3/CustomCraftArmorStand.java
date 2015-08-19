package com.github.erozabesu.yplkart.override.v1_8_R3;

import net.minecraft.server.v1_8_R3.EntityArmorStand;

import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.Vehicle;

public class CustomCraftArmorStand extends CraftArmorStand implements Vehicle {

    public CustomCraftArmorStand(CraftServer server, EntityArmorStand entity) {
        super(server, entity);
    }
}