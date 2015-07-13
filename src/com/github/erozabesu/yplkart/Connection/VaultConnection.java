package com.github.erozabesu.yplkart.Connection;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultConnection {

	private Economy economy;

	public VaultConnection(){
		// Do nothing
	}

	public static VaultConnection setupConnection(Plugin plugin) {
		if (plugin == null) {
			return null;
		}

		RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (provider != null) {
			VaultConnection connection = new VaultConnection();
			connection.economy = provider.getProvider();
			if (connection.economy.isEnabled()) {
				return connection;
			}
		}
		return null;
	}
}
