package com.github.erozabesu.yplkart.override.v1_8_R1;

import net.minecraft.server.v1_8_R1.EntityArmorStand;

import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftArmorStand;
import org.bukkit.entity.Vehicle;

public class CustomCraftArmorStand extends CraftArmorStand implements Vehicle {

    public CustomCraftArmorStand(CraftServer server, EntityArmorStand entity) {
        super(server, entity);
    }
}