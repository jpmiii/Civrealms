package com.jpmiii.Civrealms;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.jpmiii.Hero1000.Hero1000Listener;
import com.jpmiii.Hero1000.Hero1000Task;
import com.jpmiii.Hero1000.Hero1000Trait;

import net.milkbowl.vault.permission.Permission;

public class Civrealms extends JavaPlugin implements Listener {
	public Permission perms = null;

	public void onEnable() {
		// getLogger().info("onEnable has been invoked!");

		this.saveDefaultConfig();

		getServer().getPluginManager().registerEvents(
				this, this);
		setupPermissions();


		if (getServer().getPluginManager().getPlugin("Citizens") == null
				|| getServer().getPluginManager().getPlugin("Citizens")
						.isEnabled() == false) {
			getLogger().severe("Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		final Iterator<Recipe> recipes = getServer().recipeIterator();
		Recipe recipe;
		ItemStack result;

		while (recipes.hasNext()) {
			if ((recipe = recipes.next()) == null)
				continue;

			if ((result = recipe.getResult()) == null)
				continue;

			if (result.getType() == Material.ENCHANTMENT_TABLE)
				recipes.remove();
		}


	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer()
				.getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public void onDisable() {

		getLogger().info("onDisable has been invoked!");
	}

}
