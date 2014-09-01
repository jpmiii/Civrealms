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
		for (Player ply : plugin.getServer().getOnlinePlayers()) {
			if (!plugin.perms.has(ply, "civrealms.nofollow")){
				plugin.getLogger().info(ply.getName() + "   " + ply.getLocation().getX() + ", " + ply.getLocation().getY() + ", " + ply.getLocation().getZ());
			}
			
		}

	}
}