package com.jpmiii.Civrealms;


//import org.bukkit.entity.Player;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jpmiii.Civrealms.Civrealms;

public class CivrealmsTask extends BukkitRunnable {
	private final Civrealms plugin;

	public CivrealmsTask(Civrealms plugin) {
		this.plugin = plugin;
	}

	public void run() {
		for (Player ply : plugin.getServer()
				.getWorld(plugin.getConfig().getString("worldName"))
				.getPlayers()) {
			if (!plugin.perms.has(ply, "civrealms.nofollow") && !ply.hasMetadata("NPC")){
				plugin.getLogger().info(ply.getDisplayName() + ply.getLocation().toString());
			}
			
		}

	}
}