package com.github.erozabesu.yplkart.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.YPLKart;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.reflection.Classes;
import com.github.erozabesu.yplkart.utils.CheckPointUtil;
import com.github.erozabesu.yplkart.utils.ReflectionUtil;

public class KartListener implements Listener {

    public KartListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, YPLKart.getInstance());
    }

    /**
     * 水中で搭乗した場合マインクラフトの仕様で強制的に搭乗解除されてしまうため、<br>
     * 仮想スニークフラグがtrueでない場合はキャンセルする<br>
     * また、スタンバイ状態のレースに参加しており、かつゴールしていないプレイヤーの搭乗解除をキャンセルする
     * @param event
     */
    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!YPLKart.isPluginEnabled(event.getExited().getWorld())) {
            return;
        }
        if (!(event.getExited() instanceof Player)) {
            return;
        }
        if (!ReflectionUtil.instanceOf(event.getVehicle(), Classes.yplCustomCraftArmorStand)) {
            return;
        }

        Player player = (Player) event.getExited();
        Racer racer = RaceManager.getRacer(player);

        //レース中はキャンセル
        if (RaceManager.isStandby(player.getUniqueId())) {
            if (!racer.isGoal()) {
                event.setCancelled(true);
                return;
            }
        }

        //仮想スニークフラグがtrueではない場合はキャンセル
        if (!racer.isSneaking()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void cancelArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (!YPLKart.isPluginEnabled(event.getPlayer().getWorld())) {
            return;
        }

        Entity armorStand = event.getRightClicked();
        if (CheckPointUtil.isCheckPointEntity(armorStand)) {
            event.setCancelled(true);
            return;
        }
    }
}
