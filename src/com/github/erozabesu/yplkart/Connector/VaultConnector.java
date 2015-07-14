package com.github.erozabesu.yplkart.Connector;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultConnector {

    private Economy economy;

    public VaultConnector() {
        // Do nothing
    }

    public static VaultConnector setupConnection(Plugin plugin) {
        if (plugin == null) {
            return null;
        }

        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider != null) {
            VaultConnector connection = new VaultConnector();
            connection.economy = provider.getProvider();
            if (connection.economy.isEnabled()) {
                return connection;
            }
        }
        return null;
    }
}
